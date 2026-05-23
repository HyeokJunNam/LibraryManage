export function createReturnBookProcess({
                                            onActivateReturnMode,
                                            reloadReturnPanel
                                        } = {}) {
    // [최적화] 무거운 Map 대신 대출 ID(string)만 저장하는 Set을 사용하여 메모리 및 로직 경량화
    const selectedBorrowIds = new Set();

    function getElements() {
        return {
            returnBookList: document.getElementById("returnBookList"),
            resetReturnBookButton: document.getElementById("resetReturnBookButton"),
            confirmReturnBookButton: document.getElementById("confirmReturnBookButton")
        };
    }

    function toIdString(value) {
        return value !== null && value !== undefined ? String(value).trim() : "";
    }

    function isPositiveLongId(value) {
        const id = toIdString(value);
        return /^\d+$/.test(id) && id !== "0";
    }

    function setElementDisabled(element, disabled) {
        if (element) {
            element.disabled = disabled;
        }
    }

    function isReturnPanelTarget(target) {
        const panel = document.getElementById("bookReturnModePanel");
        return !!panel && panel.contains(target);
    }

    function getReturnRows() {
        return Array.from(
            document.querySelectorAll('#returnBookList [data-role="select-return-book"]')
        );
    }

    function updateReturnActionButtons() {
        const { resetReturnBookButton, confirmReturnBookButton } = getElements();
        const hasSelection = selectedBorrowIds.size > 0;

        setElementDisabled(resetReturnBookButton, !hasSelection);
        setElementDisabled(confirmReturnBookButton, !hasSelection);
    }

    // [추가] AJAX 페이지네이션 후 새 DOM이 그려졌을 때, 기존 선택 상태를 체크박스와 Row에 복원
    function syncRowStatesWithSelection() {
        getReturnRows().forEach((row) => {
            const borrowRecordId = toIdString(row.dataset.borrowRecordId);
            if (!isPositiveLongId(borrowRecordId)) return;

            const isSelected = selectedBorrowIds.has(borrowRecordId);
            const checkbox = row.querySelector('[data-role="return-book-checkbox"]');

            row.classList.toggle("is-selected", isSelected);
            if (checkbox) {
                checkbox.checked = isSelected;
            }
        });
    }

    function clearSelectedReturnBooks() {
        selectedBorrowIds.clear();
        syncRowStatesWithSelection();
        updateReturnActionButtons();
    }

    function toggleReturnBookSelection(row) {
        const borrowRecordId = toIdString(row.dataset.borrowRecordId);
        if (!isPositiveLongId(borrowRecordId)) return;

        if (selectedBorrowIds.has(borrowRecordId)) {
            selectedBorrowIds.delete(borrowRecordId);
        } else {
            selectedBorrowIds.add(borrowRecordId);
        }

        syncRowStatesWithSelection();
        updateReturnActionButtons();
    }

    function setReturnBookSelection(row, selected) {
        const borrowRecordId = toIdString(row.dataset.borrowRecordId);
        if (!isPositiveLongId(borrowRecordId)) return;

        if (selected) {
            selectedBorrowIds.add(borrowRecordId);
        } else {
            selectedBorrowIds.delete(borrowRecordId);
        }

        syncRowStatesWithSelection();
        updateReturnActionButtons();
    }

    function getSelectedReturnBookIds() {
        return Array.from(selectedBorrowIds).filter(isPositiveLongId);
    }

    function createReturnRequestBody() {
        return {
            bookRecordIds: getSelectedReturnBookIds()
        };
    }

    async function postReturn(requestBody) {
        return apiPost("/api/returns", requestBody);
    }

    function openReturnConfirmModal(onConfirm) {
        if (typeof openAlertModal === "function") {
            openAlertModal({
                title: "반납 처리 확인",
                message: "선택한 도서를 반납 처리하시겠습니까?",
                confirmText: "확인",
                cancelText: "취소",
                onConfirm
            });
            return;
        }

        if (window.confirm("선택한 도서를 반납 처리하시겠습니까?")) {
            onConfirm();
        }
    }

    function showReturnCompleteModal() {
        if (typeof openAlertModal === "function") {
            openAlertModal({
                title: "반납 처리 완료",
                message: "반납 처리가 완료되었습니다.",
                confirmText: "확인"
            });
            return;
        }

        alert("반납 처리가 완료되었습니다.");
    }

    async function refreshReturnPanelAfterSubmit() {
        selectedBorrowIds.clear();

        if (typeof reloadReturnPanel === "function") {
            await reloadReturnPanel();
        }

        updateReturnActionButtons();
    }

    async function executeReturn(requestBody) {
        const { confirmReturnBookButton, resetReturnBookButton } = getElements();

        try {
            setElementDisabled(confirmReturnBookButton, true);
            setElementDisabled(resetReturnBookButton, true);

            await postReturn(requestBody);

            showReturnCompleteModal();
            await refreshReturnPanelAfterSubmit();
        } catch (error) {
            console.error(error);
            alert(error?.message || "반납 처리 중 오류가 발생했습니다.");
        } finally {
            updateReturnActionButtons();
        }
    }

    function requestReturn() {
        const requestBody = createReturnRequestBody();

        if (requestBody.bookRecordIds.length === 0) {
            alert("반납할 도서를 선택해 주세요.");
            return;
        }

        openReturnConfirmModal(() => {
            executeReturn(requestBody);
        });
    }

    function bindReturnBookEvents() {
        document.addEventListener("click", (event) => {
            if (!isReturnPanelTarget(event.target)) return;

            const checkbox = event.target.closest('[data-role="return-book-checkbox"]');

            if (checkbox) {
                event.stopPropagation();
                const row = checkbox.closest('[data-role="select-return-book"]');
                if (!row) return;

                setReturnBookSelection(row, checkbox.checked);
                return;
            }

            if (event.target.id === "resetReturnBookButton") {
                clearSelectedReturnBooks();
                return;
            }

            if (event.target.id === "confirmReturnBookButton") {
                requestReturn();
                return;
            }

            const row = event.target.closest('#returnBookList [data-role="select-return-book"]');
            if (!row) return;

            toggleReturnBookSelection(row);
        });

        document.addEventListener("keydown", (event) => {
            if (!isReturnPanelTarget(event.target)) return;
            if (event.key !== "Enter" && event.key !== " ") return;

            const row = event.target.closest('#returnBookList [data-role="select-return-book"]');
            if (!row) return;

            event.preventDefault();
            toggleReturnBookSelection(row);
        });

        // [최적화] 페이지 이동 시 무조건 선택 해제(clear)되던 버그 제거.
        // 새로운 페이지의 DOM에 기존 선택 상태를 복원(sync)하도록 수정하여 다중 페이지 선택 지원.
        document.addEventListener("table-layout:updated", (event) => {
            if (event.target?.id !== "bookReturnModePanel") return;
            syncRowStatesWithSelection();
            updateReturnActionButtons();
        });
    }

    async function activate() {
        onActivateReturnMode?.();
        selectedBorrowIds.clear();

        if (typeof reloadReturnPanel === "function") {
            await reloadReturnPanel();
        }

        updateReturnActionButtons();
    }

    function clear() {
        selectedBorrowIds.clear();
        updateReturnActionButtons();
    }

    bindReturnBookEvents();
    updateReturnActionButtons();

    return {
        activate,
        clear,
        refresh: syncRowStatesWithSelection // 부모 컨트롤러가 안전하게 호출할 수 있도록 매핑
    };
}