import { renderAsyncPagination } from "../../../common/async-pagination.js";
import { createBookSearchModal } from "./process-book-search-modal.js";

export function createBookProcess({ getSelectedMemberId } = {}) {
    const bookPanel = document.getElementById("bookPanel");
    const bookPanelEmpty = document.getElementById("bookPanelEmpty");
    const bookBorrowModePanel = document.getElementById("bookBorrowModePanel");
    const bookReturnModePanel = document.getElementById("bookReturnModePanel");

    const bookSelectionEmpty = document.getElementById("bookSelectionEmpty");
    const bookPanelResult = document.getElementById("bookPanelResult");
    const bookPanelDesc = document.getElementById("bookPanelDesc");
    const bookPanelModeBadge = document.getElementById("bookPanelModeBadge");
    const openBookSearchButton = document.getElementById("openBookSearchButton");

    const selectedBookCount = document.getElementById("selectedBookCount");
    const selectedBookList = document.getElementById("selectedBookList");
    const selectedBookEmptyRow = document.getElementById("selectedBookEmptyRow");
    const selectedBookRowTemplate = document.getElementById("selectedBookRowTemplate");
    const selectedBookPagination = document.getElementById("selectedBookPagination");

    const bookPanelFooter = document.getElementById("bookPanelFooter");
    const resetBorrowButton = document.getElementById("resetBorrowButton");
    const confirmBorrowButton = document.getElementById("confirmBorrowButton");

    const returnBookEmpty = document.getElementById("returnBookEmpty");
    const returnBookLoading = document.getElementById("returnBookLoading");
    const returnBookResult = document.getElementById("returnBookResult");
    const returnBookList = document.getElementById("returnBookList");
    const returnBookEmptyRow = document.getElementById("returnBookEmptyRow");
    const returnBookRowTemplate = document.getElementById("returnBookRowTemplate");
    const returnBookPagination = document.getElementById("returnBookPagination");

    const returnBookPanelFooter = document.getElementById("returnBookPanelFooter");
    const selectedReturnBookCount = document.getElementById("selectedReturnBookCount");
    const resetReturnBookButton = document.getElementById("resetReturnBookButton");
    const confirmReturnBookButton = document.getElementById("confirmReturnBookButton");

    const selectedBookState = {
        page: 0,
        size: 5
    };

    const returnBookState = {
        page: 0,
        size: 5
    };

    const confirmedSelectedBooks = new Map();
    const returnBooks = new Map();
    const selectedReturnBooks = new Map();

    function toPositiveInt(value, fallback = 0) {
        const number = Number(value);
        if (!Number.isFinite(number)) return fallback;
        return Math.max(0, Math.floor(number));
    }

    function clampBorrowQuantity(book, quantity) {
        const max = Math.max(1, toPositiveInt(book.availableQuantity, 0));
        const next = toPositiveInt(quantity, 1);
        return Math.min(Math.max(next, 1), max);
    }

    function formatDate(value) {
        if (!value) return "-";

        const date = new Date(value);

        if (Number.isNaN(date.getTime())) {
            return String(value);
        }

        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");

        return `${year}-${month}-${day}`;
    }

    function isOverdueByDueAt(dueAt, returnedAt) {
        if (returnedAt || !dueAt) return false;

        const dueDate = new Date(dueAt);

        if (Number.isNaN(dueDate.getTime())) {
            return false;
        }

        return dueDate.getTime() < Date.now();
    }

    function bindFields(root, data, fallback = "-") {
        root.querySelectorAll("[data-field]").forEach((element) => {
            const value = data[element.dataset.field];

            element.textContent = value === null || value === undefined || value === ""
                ? fallback
                : String(value);
        });
    }

    function setBookIdToRoleElements(root, bookId, roles) {
        roles.forEach((role) => {
            root.querySelector(`[data-role="${role}"]`)?.setAttribute("data-book-id", bookId);
        });
    }

    function setElementDisabled(element, disabled) {
        if (element) {
            element.disabled = disabled;
        }
    }

    function setHidden(element, hidden) {
        element?.classList.toggle("is-hidden", hidden);
    }

    function setBookPanelMode(mode) {
        bookPanel?.setAttribute("data-mode", mode);
    }

    function setPanelDescription(text) {
        if (bookPanelDesc) {
            bookPanelDesc.textContent = text;
        }
    }

    function setModeBadge(text) {
        if (!bookPanelModeBadge) return;

        bookPanelModeBadge.textContent = text;
        bookPanelModeBadge.classList.toggle("is-hidden", !text);
    }

    function setBookSearchButtonVisible(visible) {
        if (!openBookSearchButton) return;

        openBookSearchButton.classList.toggle("is-hidden", !visible);
        openBookSearchButton.disabled = !visible;
    }

    function showIdlePanel() {
        setHidden(bookPanelEmpty, false);
        setHidden(bookBorrowModePanel, true);
        setHidden(bookReturnModePanel, true);
    }

    function showBorrowPanel() {
        setHidden(bookPanelEmpty, true);
        setHidden(bookBorrowModePanel, false);
        setHidden(bookReturnModePanel, true);
    }

    function showReturnPanel() {
        setHidden(bookPanelEmpty, true);
        setHidden(bookBorrowModePanel, true);
        setHidden(bookReturnModePanel, false);
    }

    function clearSelectedBookRows() {
        selectedBookList
            ?.querySelectorAll(".book-result__row")
            .forEach((row) => row.remove());
    }

    function clearSelectedBookPagination() {
        if (selectedBookPagination) {
            selectedBookPagination.innerHTML = "";
        }
    }

    function renderSelectedBookPagination(totalPages) {
        if (!selectedBookPagination) return;

        selectedBookPagination.innerHTML = "";

        renderAsyncPagination(selectedBookPagination, {
            currentPage: selectedBookState.page,
            totalPages: Math.max(1, totalPages),
            visiblePages: 5,
            onPageChange: (page) => {
                selectedBookState.page = page;
                updateBookPanelResult();
            }
        });
    }

    function getQuantityState(book) {
        const max = toPositiveInt(book.availableQuantity, 0);
        const quantity = max > 0 ? clampBorrowQuantity(book, book.quantity) : 0;

        return {
            max,
            quantity,
            canDecrease: quantity > 1,
            canIncrease: quantity < max
        };
    }

    function createSelectedBookRowElement(book, index) {
        if (!selectedBookRowTemplate) return null;

        const row = selectedBookRowTemplate.content.firstElementChild.cloneNode(true);
        const { max, quantity, canDecrease, canIncrease } = getQuantityState(book);
        const quantityInput = row.querySelector('[data-role="book-quantity-input"]');

        row.dataset.bookId = book.id;

        bindFields(row, {
            ...book,
            index: index + 1,
            availableQuantity: max,
            quantity
        });

        if (quantityInput) {
            quantityInput.dataset.bookId = book.id;
            quantityInput.min = "1";
            quantityInput.max = String(max);
            quantityInput.value = String(quantity);
            quantityInput.disabled = max <= 0;
        }

        setBookIdToRoleElements(row, book.id, [
            "decrease-book-quantity",
            "increase-book-quantity",
            "remove-selected-book"
        ]);

        setElementDisabled(row.querySelector('[data-role="decrease-book-quantity"]'), !canDecrease);
        setElementDisabled(row.querySelector('[data-role="increase-book-quantity"]'), !canIncrease);

        return row;
    }

    function updateBorrowActionButtons() {
        const hasBooks = confirmedSelectedBooks.size > 0;

        setHidden(bookPanelFooter, !hasBooks);
        setElementDisabled(resetBorrowButton, !hasBooks);
        setElementDisabled(confirmBorrowButton, !hasBooks);
    }

    function updateBookPanelResult() {
        const books = Array.from(confirmedSelectedBooks.values());

        if (selectedBookCount) {
            selectedBookCount.textContent = String(books.length);
        }

        if (!selectedBookList) {
            updateBorrowActionButtons();
            return;
        }

        clearSelectedBookRows();

        const hasBooks = books.length > 0;

        setHidden(selectedBookEmptyRow, hasBooks);
        setHidden(bookSelectionEmpty, hasBooks);
        setHidden(bookPanelResult, !hasBooks);

        const totalPages = Math.max(1, Math.ceil(books.length / selectedBookState.size));

        if (selectedBookState.page >= totalPages) {
            selectedBookState.page = totalPages - 1;
        }

        if (!hasBooks) {
            clearSelectedBookPagination();
            updateBorrowActionButtons();
            return;
        }

        const start = selectedBookState.page * selectedBookState.size;
        const pagedBooks = books.slice(start, start + selectedBookState.size);
        const fragment = document.createDocumentFragment();

        pagedBooks.forEach((book, index) => {
            const row = createSelectedBookRowElement(book, start + index);

            if (row) {
                fragment.appendChild(row);
            }
        });

        selectedBookList.appendChild(fragment);
        renderSelectedBookPagination(totalPages);
        updateBorrowActionButtons();
    }

    function clearBorrowBooks() {
        confirmedSelectedBooks.clear();
        selectedBookState.page = 0;
        clearSelectedBookRows();
        clearSelectedBookPagination();

        setHidden(bookSelectionEmpty, false);
        setHidden(bookPanelResult, true);
        setHidden(selectedBookEmptyRow, false);

        if (selectedBookCount) {
            selectedBookCount.textContent = "0";
        }

        updateBorrowActionButtons();
    }

    function removeConfirmedBook(bookId) {
        if (!bookId) return;

        confirmedSelectedBooks.delete(bookId);
        updateBookPanelResult();
    }

    function updateConfirmedBookQuantity(bookId, nextQuantity) {
        const targetBook = confirmedSelectedBooks.get(bookId);
        if (!targetBook) return;

        confirmedSelectedBooks.set(bookId, {
            ...targetBook,
            quantity: clampBorrowQuantity(targetBook, nextQuantity)
        });

        updateBookPanelResult();
    }

    function normalizeReturnBook(rawBook) {
        const overdue = isOverdueByDueAt(rawBook.dueAt, rawBook.returnedAt);

        return {
            borrowRecordId: String(rawBook.borrowRecordId ?? ""),
            bookItemId: String(rawBook.bookItemId ?? ""),
            bookTitle: rawBook.bookTitle ?? "",
            borrowedAt: formatDate(rawBook.borrowedAt),
            dueAt: formatDate(rawBook.dueAt),
            overdue,
            overdueText: overdue ? "연체" : "정상"
        };
    }

    function clearReturnBookRows() {
        returnBookList
            ?.querySelectorAll(".book-result__row")
            .forEach((row) => row.remove());
    }

    function clearReturnBookPagination() {
        if (returnBookPagination) {
            returnBookPagination.innerHTML = "";
        }
    }

    function showOnlyReturnState(visibleStateElement) {
        [
            returnBookEmpty,
            returnBookLoading,
            returnBookResult
        ].forEach((element) => {
            setHidden(element, element !== visibleStateElement);
        });
    }

    function showReturnLoadingState() {
        clearReturnBookRows();
        clearReturnBookPagination();
        showOnlyReturnState(returnBookLoading);
        updateReturnActionButtons();
    }

    function showReturnEmptyState() {
        clearReturnBookRows();
        clearReturnBookPagination();
        showOnlyReturnState(returnBookEmpty);
        updateReturnActionButtons();
    }

    function showReturnResultState() {
        showOnlyReturnState(returnBookResult);
        updateReturnActionButtons();
    }

    function updateReturnActionButtons() {
        const selectedCount = selectedReturnBooks.size;
        const hasSelectedBooks = selectedCount > 0;

        if (selectedReturnBookCount) {
            selectedReturnBookCount.textContent = String(selectedCount);
        }

        setHidden(returnBookPanelFooter, returnBooks.size === 0);
        setElementDisabled(resetReturnBookButton, !hasSelectedBooks);
        setElementDisabled(confirmReturnBookButton, !hasSelectedBooks);

        returnBookList?.querySelectorAll('[data-role="select-return-book"]').forEach((row) => {
            const borrowRecordId = row.dataset.borrowRecordId;
            const checkbox = row.querySelector('[data-role="return-book-checkbox"]');
            const isSelected = selectedReturnBooks.has(borrowRecordId);

            row.classList.toggle("is-selected", isSelected);

            if (checkbox) {
                checkbox.checked = isSelected;
            }
        });
    }

    function createReturnBookRowElement(book) {
        if (!returnBookRowTemplate) return null;

        const row = returnBookRowTemplate.content.firstElementChild.cloneNode(true);
        const checkbox = row.querySelector('[data-role="return-book-checkbox"]');
        const overdueBadge = row.querySelector(".book-return-result__overdue-badge");

        row.dataset.borrowRecordId = book.borrowRecordId;
        row.dataset.returnBook = encodeURIComponent(JSON.stringify(book));

        bindFields(row, book);

        if (checkbox) {
            checkbox.checked = selectedReturnBooks.has(book.borrowRecordId);
            checkbox.setAttribute("aria-label", `${book.bookTitle || "도서"} 반납 선택`);
        }

        if (overdueBadge) {
            overdueBadge.classList.toggle("is-overdue", book.overdue);
            overdueBadge.classList.toggle("is-normal", !book.overdue);
        }

        return row;
    }

    function renderReturnBookRows(books) {
        if (!returnBookList) return;

        clearReturnBookRows();

        if (returnBookEmptyRow) {
            setHidden(returnBookEmptyRow, books.length > 0);
        }

        const fragment = document.createDocumentFragment();

        books.forEach((book) => {
            const row = createReturnBookRowElement(book);

            if (row) {
                fragment.appendChild(row);
            }
        });

        returnBookList.appendChild(fragment);
        updateReturnActionButtons();
    }

    function renderReturnBookPagination(totalPages) {
        if (!returnBookPagination) return;

        returnBookPagination.innerHTML = "";

        renderAsyncPagination(returnBookPagination, {
            currentPage: returnBookState.page,
            totalPages: Math.max(1, totalPages),
            visiblePages: 5,
            onPageChange: (page) => {
                returnBookState.page = page;
                updateReturnBookResult();
            }
        });
    }

    function updateReturnBookResult() {
        const books = Array.from(returnBooks.values());
        const totalPages = Math.max(1, Math.ceil(books.length / returnBookState.size));

        if (returnBookState.page >= totalPages) {
            returnBookState.page = totalPages - 1;
        }

        if (books.length === 0) {
            showReturnEmptyState();
            return;
        }

        const start = returnBookState.page * returnBookState.size;
        const pagedBooks = books.slice(start, start + returnBookState.size);

        showReturnResultState();
        renderReturnBookRows(pagedBooks);
        renderReturnBookPagination(totalPages);
    }

    function clearReturnBooks() {
        returnBooks.clear();
        selectedReturnBooks.clear();
        returnBookState.page = 0;

        clearReturnBookRows();
        clearReturnBookPagination();

        if (returnBookEmptyRow) {
            setHidden(returnBookEmptyRow, false);
        }

        showReturnEmptyState();
    }

    function getSelectedReturnBookIds() {
        return Array.from(selectedReturnBooks.keys()).map(Number);
    }

    function createReturnRequestBody() {
        return {
            bookRecordIds: getSelectedReturnBookIds()
        };
    }

    async function fetchReturnBooks(memberId) {
        const payload = await apiGet(`/api/members/${memberId}/borrows`);
        return payload?.result ?? {};
    }

    async function postReturn(requestBody) {
        return apiPost("/api/returns", requestBody);
    }

    async function loadReturnBooks() {
        const memberId = resolveSelectedMemberId();

        if (!memberId) {
            clearReturnBooks();
            alert("반납할 회원을 먼저 선택해 주세요.");
            return;
        }

        try {
            showReturnLoadingState();

            const result = await fetchReturnBooks(memberId);
            const content = Array.isArray(result)
                ? result
                : Array.isArray(result.content)
                    ? result.content
                    : [];

            returnBooks.clear();
            selectedReturnBooks.clear();
            returnBookState.page = 0;

            content
                .map(normalizeReturnBook)
                .filter((book) => book.borrowRecordId)
                .forEach((book) => {
                    returnBooks.set(book.borrowRecordId, book);
                });

            updateReturnBookResult();
        } catch (error) {
            console.error(error);
            alert(error?.message || "대출 도서 목록 조회 중 오류가 발생했습니다.");
            clearReturnBooks();
        }
    }

    function toggleReturnBookSelection(row) {
        const raw = row.dataset.returnBook;
        const borrowRecordId = row.dataset.borrowRecordId;

        if (!raw || !borrowRecordId) return;

        try {
            const book = normalizeReturnBook(JSON.parse(decodeURIComponent(raw)));

            if (selectedReturnBooks.has(borrowRecordId)) {
                selectedReturnBooks.delete(borrowRecordId);
            } else {
                selectedReturnBooks.set(borrowRecordId, book);
            }

            updateReturnActionButtons();
        } catch (error) {
            console.error("선택한 반납 도서 데이터 파싱 실패", error);
        }
    }

    function clearSelectedReturnBooks() {
        selectedReturnBooks.clear();
        updateReturnActionButtons();
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

    async function executeReturn(requestBody) {
        try {
            setElementDisabled(confirmReturnBookButton, true);
            setElementDisabled(resetReturnBookButton, true);

            await postReturn(requestBody);

            showReturnCompleteModal();

            selectedReturnBooks.clear();
            await loadReturnBooks();
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

        openReturnConfirmModal(() => executeReturn(requestBody));
    }

    function reset() {
        clearBorrowBooks();
        clearReturnBooks();
        bookSearchModalController.reset();

        setBookPanelMode("idle");
        showIdlePanel();

        setPanelDescription("대출 또는 반납할 회원을 먼저 선택하세요.");
        setModeBadge("");
        setBookSearchButtonVisible(false);
    }

    function activateBorrowMode() {
        clearReturnBooks();

        setBookPanelMode("borrow");
        showBorrowPanel();

        setPanelDescription("대출할 도서를 검색하여 목록에 추가한 뒤 대출 처리를 진행할 수 있습니다.");
        setModeBadge("대출 모드");
        setBookSearchButtonVisible(true);

        updateBookPanelResult();
    }

    function activateReturnMode() {
        clearBorrowBooks();
        bookSearchModalController.reset();

        setBookPanelMode("return");
        showReturnPanel();

        setPanelDescription("선택한 회원의 대출 도서를 조회한 뒤 반납할 도서를 선택하세요.");
        setModeBadge("반납 모드");
        setBookSearchButtonVisible(false);

        loadReturnBooks();
    }

    function replaceConfirmedBooks(books) {
        confirmedSelectedBooks.clear();

        books.forEach((book) => {
            confirmedSelectedBooks.set(book.id, {
                ...book,
                quantity: clampBorrowQuantity(book, book.quantity ?? 1)
            });
        });

        selectedBookState.page = 0;
        activateBorrowMode();
    }

    const bookSearchModalController = createBookSearchModal({
        getConfirmedBooks: () => Array.from(confirmedSelectedBooks.values()),
        onConfirmSelected: replaceConfirmedBooks
    });

    function clearSelectedBooks() {
        clearBorrowBooks();

        if (bookPanel?.dataset.mode === "borrow") {
            showBorrowPanel();
        }

        bookSearchModalController.reset();
    }

    function resolveSelectedMemberId() {
        return typeof getSelectedMemberId === "function"
            ? getSelectedMemberId()
            : "";
    }

    function createBorrowRequestBody() {
        return {
            memberId: resolveSelectedMemberId(),
            borrowBooks: Array.from(confirmedSelectedBooks.values()).map((book) => ({
                bookId: Number(book.id),
                quantity: Number(book.quantity)
            }))
        };
    }

    async function postBorrow(requestBody) {
        return apiPost("/api/borrows", requestBody);
    }

    function openBorrowConfirmModal(onConfirm) {
        if (typeof openAlertModal === "function") {
            openAlertModal({
                title: "대출 처리 확인",
                message: "처리하시겠습니까?",
                confirmText: "확인",
                cancelText: "취소",
                onConfirm
            });
            return;
        }

        if (window.confirm("처리하시겠습니까?")) {
            onConfirm();
        }
    }

    function showBorrowCompleteModal() {
        if (typeof openAlertModal === "function") {
            openAlertModal({
                title: "대출 처리 완료",
                message: "대출 처리가 완료되었습니다.",
                confirmText: "확인"
            });
            return;
        }

        alert("대출 처리가 완료되었습니다.");
    }

    async function executeBorrow(requestBody) {
        try {
            setElementDisabled(confirmBorrowButton, true);
            setElementDisabled(resetBorrowButton, true);

            await postBorrow(requestBody);

            showBorrowCompleteModal();
            clearSelectedBooks();
        } catch (error) {
            console.error(error);
            alert(error?.message || "대출 처리 중 오류가 발생했습니다.");
        } finally {
            updateBorrowActionButtons();
        }
    }

    function requestBorrow() {
        const requestBody = createBorrowRequestBody();

        if (!requestBody.memberId) {
            alert("대출할 회원을 먼저 선택해 주세요.");
            return;
        }

        if (requestBody.borrowBooks.length === 0) {
            alert("대출할 도서를 선택해 주세요.");
            updateBorrowActionButtons();
            return;
        }

        openBorrowConfirmModal(() => executeBorrow(requestBody));
    }

    resetBorrowButton?.addEventListener("click", clearSelectedBooks);
    confirmBorrowButton?.addEventListener("click", requestBorrow);

    selectedBookList?.addEventListener("click", (event) => {
        const removeButton = event.target.closest('[data-role="remove-selected-book"]');

        if (removeButton) {
            removeConfirmedBook(removeButton.dataset.bookId);
            return;
        }

        const decreaseButton = event.target.closest('[data-role="decrease-book-quantity"]');

        if (decreaseButton) {
            const book = confirmedSelectedBooks.get(decreaseButton.dataset.bookId);

            if (book) {
                updateConfirmedBookQuantity(book.id, Number(book.quantity ?? 1) - 1);
            }

            return;
        }

        const increaseButton = event.target.closest('[data-role="increase-book-quantity"]');

        if (increaseButton) {
            const book = confirmedSelectedBooks.get(increaseButton.dataset.bookId);

            if (book) {
                updateConfirmedBookQuantity(book.id, Number(book.quantity ?? 1) + 1);
            }
        }
    });

    selectedBookList?.addEventListener("change", (event) => {
        const quantityInput = event.target.closest('[data-role="book-quantity-input"]');
        if (!quantityInput) return;

        updateConfirmedBookQuantity(quantityInput.dataset.bookId, quantityInput.value);
    });

    selectedBookList?.addEventListener("blur", (event) => {
        const quantityInput = event.target.closest('[data-role="book-quantity-input"]');
        if (!quantityInput) return;

        updateConfirmedBookQuantity(quantityInput.dataset.bookId, quantityInput.value);
    }, true);

    returnBookList?.addEventListener("click", (event) => {
        const row = event.target.closest('[data-role="select-return-book"]');

        if (row) {
            toggleReturnBookSelection(row);
        }
    });

    returnBookList?.addEventListener("keydown", (event) => {
        if (event.key !== "Enter" && event.key !== " ") return;

        const row = event.target.closest('[data-role="select-return-book"]');
        if (!row) return;

        event.preventDefault();
        toggleReturnBookSelection(row);
    });

    resetReturnBookButton?.addEventListener("click", clearSelectedReturnBooks);
    confirmReturnBookButton?.addEventListener("click", requestReturn);

    reset();

    return {
        reset,
        activateBorrowMode,
        activateReturnMode
    };
}