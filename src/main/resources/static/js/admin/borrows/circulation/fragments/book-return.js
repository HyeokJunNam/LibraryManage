export function createReturnBookProcess({
                                            root,
                                            onActivateReturnMode,
                                            reloadReturnPanel
                                        } = {}) {
    const selectedBorrowIds = new Set();

    let bound = false;

    function getPanel() {
        return document.getElementById("bookReturnModePanel") || root || null;
    }

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
        const panel = getPanel();
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

    function isReturnPanelUpdatedEvent(event) {
        return event.target?.id === "bookReturnModePanel"
            || event.detail?.targetId === "bookReturnModePanel";
    }

    function handleClick(event) {
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
    }

    function handleKeydown(event) {
        if (!isReturnPanelTarget(event.target)) return;
        if (event.key !== "Enter" && event.key !== " ") return;

        const row = event.target.closest('#returnBookList [data-role="select-return-book"]');
        if (!row) return;

        event.preventDefault();
        toggleReturnBookSelection(row);
    }

    function handleTableLayoutUpdated(event) {
        if (!isReturnPanelUpdatedEvent(event)) return;

        syncRowStatesWithSelection();
        updateReturnActionButtons();
    }

    function bindReturnBookEvents() {
        if (bound) {
            return;
        }

        document.addEventListener("click", handleClick);
        document.addEventListener("keydown", handleKeydown);
        document.addEventListener("table-layout:updated", handleTableLayoutUpdated);

        bound = true;
    }

    function destroy() {
        if (!bound) {
            return;
        }

        document.removeEventListener("click", handleClick);
        document.removeEventListener("keydown", handleKeydown);
        document.removeEventListener("table-layout:updated", handleTableLayoutUpdated);

        bound = false;
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
        syncRowStatesWithSelection();
        updateReturnActionButtons();
    }

    bindReturnBookEvents();
    updateReturnActionButtons();

    return {
        activate,
        clear,
        refresh: syncRowStatesWithSelection,
        destroy
    };
}