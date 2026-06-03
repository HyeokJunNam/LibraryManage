export function createBorrowBookProcess({
                                            getSelectedMemberId,
                                            bookSearchModalController,
                                            onActivateBorrowMode
                                        } = {}) {
    const borrowBookList = document.getElementById("borrowBookList");
    const borrowBookEmptyRow = document.getElementById("borrowBookEmptyRow");
    const borrowBookRowTemplate = document.getElementById("borrowBookRowTemplate");

    const borrowBookPaginationWrap = document.getElementById("borrowBookPaginationWrap");
    const borrowBookPagination = document.getElementById("borrowBookPagination");

    const openBorrowBookSearchButton = document.querySelector('[data-role="open-borrow-book-search"]');

    const bookPanelFooter = document.getElementById("bookPanelFooter");
    const resetBorrowButton = document.querySelector('[data-role="reset-borrow-books"]');
    const confirmBorrowButton = document.querySelector('[data-role="confirm-borrow"]');

    const pageState = {
        page: 0,
        size: 5
    };

    const selectedBooks = new Map();

    function toIdString(value) {
        if (value === null || value === undefined) {
            return "";
        }

        return String(value).trim();
    }

    function toPositiveInt(value, fallback = 0) {
        const number = Number(value);

        if (!Number.isFinite(number)) {
            return fallback;
        }

        return Math.max(0, Math.floor(number));
    }

    function isPositiveLongId(value) {
        const id = toIdString(value);

        return /^\d+$/.test(id) && id !== "0";
    }

    function setHidden(element, hidden) {
        element?.classList.toggle("is-hidden", hidden);

        if (element) {
            element.setAttribute("aria-hidden", String(hidden));
        }
    }

    function setDisabled(element, disabled) {
        if (element) {
            element.disabled = disabled;
        }
    }

    function hidePagination() {
        borrowBookPagination?.replaceChildren();
        borrowBookPaginationWrap?.classList.add("table-layout__pagination--empty");
    }

    function showPagination() {
        borrowBookPaginationWrap?.classList.remove("table-layout__pagination--empty");
    }

    function getSelectedMemberIdValue() {
        return toIdString(
            typeof getSelectedMemberId === "function"
                ? getSelectedMemberId()
                : ""
        );
    }

    function getAvailableQuantity(book) {
        return toPositiveInt(book.availableQuantity, 0);
    }

    function clampBorrowQuantity(book, quantity) {
        const max = getAvailableQuantity(book);

        if (max <= 0) {
            return 0;
        }

        const nextQuantity = toPositiveInt(quantity, 1);

        return Math.min(Math.max(nextQuantity, 1), max);
    }

    function normalizeSelectedBook(book) {
        if (!book) {
            return null;
        }

        const id = toIdString(book.id ?? book.bookId);

        if (!isPositiveLongId(id)) {
            return null;
        }

        const normalizedBook = {
            id,
            title: book.title ?? "",
            isbn: book.isbn ?? "",
            author: book.author ?? "",
            publisher: book.publisher ?? "",
            location: book.location ?? "",
            stockQuantity: toPositiveInt(book.stockQuantity, 0),
            availableQuantity: toPositiveInt(book.availableQuantity, 0),
            quantity: 1
        };

        return {
            ...normalizedBook,
            quantity: clampBorrowQuantity(
                normalizedBook,
                book.quantity ?? normalizedBook.quantity
            )
        };
    }

    function getSelectedBooks() {
        return Array.from(selectedBooks.values());
    }

    function hasSelectedBooks() {
        return selectedBooks.size > 0;
    }

    function bindFields(root, data, fallback = "-") {
        root.querySelectorAll("[data-field]").forEach((element) => {
            const fieldName = element.dataset.field;
            const value = data[fieldName];

            element.textContent = value === null || value === undefined || value === ""
                ? fallback
                : String(value);
        });
    }

    function clearRows() {
        borrowBookList
            ?.querySelectorAll(".book-borrow-table__row")
            .forEach((row) => row.remove());
    }

    function clearPagination() {
        hidePagination();
    }

    function updateActionButtons() {
        const disabled = !hasSelectedBooks();

        setHidden(bookPanelFooter, disabled);
        setDisabled(resetBorrowButton, disabled);
        setDisabled(confirmBorrowButton, disabled);
    }

    function createPageButton({
                                  text,
                                  page,
                                  active = false,
                                  disabled = false,
                                  onClick
                              }) {
        const button = document.createElement("button");

        button.type = "button";
        button.textContent = text;
        button.disabled = disabled;

        button.className = active
            ? "table-layout__page-button table-layout__page-button--active"
            : "table-layout__page-button";

        if (active) {
            button.setAttribute("aria-current", "page");
        }

        if (!active && !disabled) {
            button.addEventListener("click", () => onClick(page));
        }

        return button;
    }

    function renderPagination(totalPages) {
        if (!borrowBookPagination) {
            return;
        }

        borrowBookPagination.replaceChildren();

        const safeTotalPages = Math.max(1, toPositiveInt(totalPages, 1));

        showPagination();

        const currentPage = Math.max(
            0,
            Math.min(pageState.page, safeTotalPages - 1)
        );

        pageState.page = currentPage;

        const startPage = Math.max(0, currentPage - 2);
        const endPage = Math.min(safeTotalPages - 1, currentPage + 2);

        const movePage = (page) => {
            pageState.page = Math.max(0, Math.min(page, safeTotalPages - 1));
            render();
        };

        borrowBookPagination.appendChild(createPageButton({
            text: "이전",
            page: currentPage - 1,
            disabled: currentPage === 0,
            onClick: movePage
        }));

        for (let page = startPage; page <= endPage; page += 1) {
            borrowBookPagination.appendChild(createPageButton({
                text: String(page + 1),
                page,
                active: page === currentPage,
                disabled: page === currentPage,
                onClick: movePage
            }));
        }

        borrowBookPagination.appendChild(createPageButton({
            text: "다음",
            page: currentPage + 1,
            disabled: currentPage >= safeTotalPages - 1,
            onClick: movePage
        }));
    }

    function getQuantityViewState(book) {
        const max = getAvailableQuantity(book);
        const quantity = clampBorrowQuantity(book, book.quantity);

        return {
            max,
            quantity,
            canDecrease: quantity > 1,
            canIncrease: quantity > 0 && quantity < max
        };
    }

    function setBookIdToRoleElement(row, role, bookId) {
        row.querySelector(`[data-role="${role}"]`)
            ?.setAttribute("data-book-id", toIdString(bookId));
    }

    function createRow(book, displayIndex) {
        if (!borrowBookRowTemplate) {
            return null;
        }

        const row = borrowBookRowTemplate.content.firstElementChild.cloneNode(true);
        const quantityState = getQuantityViewState(book);

        row.dataset.bookId = book.id;

        bindFields(row, {
            ...book,
            index: displayIndex,
            availableQuantity: quantityState.max,
            quantity: quantityState.quantity
        });

        const quantityInput = row.querySelector('[data-role="borrow-quantity-input"]');

        if (quantityInput) {
            quantityInput.dataset.bookId = book.id;
            quantityInput.min = "1";
            quantityInput.max = String(quantityState.max);
            quantityInput.value = String(quantityState.quantity);
            quantityInput.disabled = quantityState.max <= 0;
        }

        setBookIdToRoleElement(row, "decrease-borrow-quantity", book.id);
        setBookIdToRoleElement(row, "increase-borrow-quantity", book.id);
        setBookIdToRoleElement(row, "remove-borrow-book", book.id);

        setDisabled(
            row.querySelector('[data-role="decrease-borrow-quantity"]'),
            !quantityState.canDecrease
        );

        setDisabled(
            row.querySelector('[data-role="increase-borrow-quantity"]'),
            !quantityState.canIncrease
        );

        return row;
    }

    function renderRows(books) {
        if (!borrowBookList) {
            return;
        }

        const totalPages = Math.max(1, Math.ceil(books.length / pageState.size));

        if (pageState.page >= totalPages) {
            pageState.page = totalPages - 1;
        }

        const startIndex = pageState.page * pageState.size;
        const pageBooks = books.slice(startIndex, startIndex + pageState.size);

        const fragment = document.createDocumentFragment();

        pageBooks.forEach((book, index) => {
            const row = createRow(book, startIndex + index + 1);

            if (row) {
                fragment.appendChild(row);
            }
        });

        borrowBookList.appendChild(fragment);
        renderPagination(totalPages);
    }

    function render() {
        const books = getSelectedBooks();
        const empty = books.length === 0;

        clearRows();
        clearPagination();

        setHidden(borrowBookEmptyRow, !empty);

        if (empty) {
            pageState.page = 0;
            renderPagination(1);
        } else {
            renderRows(books);
        }

        updateActionButtons();
    }

    function clearSelectedBooks() {
        selectedBooks.clear();
        pageState.page = 0;

        render();
    }

    function replaceSelectedBooks(books) {
        selectedBooks.clear();

        (books ?? [])
            .map(normalizeSelectedBook)
            .filter(Boolean)
            .forEach((book) => {
                selectedBooks.set(book.id, book);
            });

        pageState.page = 0;

        onActivateBorrowMode?.();
        render();
    }

    function removeSelectedBook(bookId) {
        const id = toIdString(bookId);

        if (!id) {
            return;
        }

        selectedBooks.delete(id);
        render();
    }

    function updateSelectedBookQuantity(bookId, quantity) {
        const id = toIdString(bookId);
        const book = selectedBooks.get(id);

        if (!book) {
            return;
        }

        selectedBooks.set(id, {
            ...book,
            quantity: clampBorrowQuantity(book, quantity)
        });

        render();
    }

    function increaseSelectedBookQuantity(bookId) {
        const book = selectedBooks.get(toIdString(bookId));

        if (!book) {
            return;
        }

        updateSelectedBookQuantity(book.id, toPositiveInt(book.quantity, 1) + 1);
    }

    function decreaseSelectedBookQuantity(bookId) {
        const book = selectedBooks.get(toIdString(bookId));

        if (!book) {
            return;
        }

        updateSelectedBookQuantity(book.id, toPositiveInt(book.quantity, 1) - 1);
    }

    function createBorrowRequestBody() {
        return {
            memberId: getSelectedMemberIdValue(),
            items: getSelectedBooks().map((book) => ({
                bookId: book.id,
                quantity: toPositiveInt(book.quantity, 1)
            }))
        };
    }

    function validateBorrowRequest(requestBody) {
        if (!isPositiveLongId(requestBody.memberId)) {
            alert("대출할 회원을 먼저 선택해 주세요.");
            return false;
        }

        if (requestBody.items.length === 0) {
            alert("대출할 도서를 선택해 주세요.");
            return false;
        }

        const invalidItem = requestBody.items.some((item) => {
            return !isPositiveLongId(item.bookId)
                || !Number.isFinite(item.quantity)
                || item.quantity <= 0;
        });

        if (invalidItem) {
            alert("선택한 도서의 ID 또는 수량 정보가 올바르지 않습니다.");
            return false;
        }

        return true;
    }

    async function postBorrow(requestBody) {
        return apiPost("/api/borrows", requestBody);
    }

    function openConfirmModal(onConfirm) {
        if (typeof openAlertModal === "function") {
            openAlertModal({
                title: "대출 처리 확인",
                message: "선택한 도서를 대출 처리하시겠습니까?",
                confirmText: "확인",
                cancelText: "취소",
                onConfirm
            });

            return;
        }

        if (window.confirm("선택한 도서를 대출 처리하시겠습니까?")) {
            onConfirm();
        }
    }

    function openCompleteModal() {
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
            setDisabled(confirmBorrowButton, true);
            setDisabled(resetBorrowButton, true);

            await postBorrow(requestBody);

            openCompleteModal();

            clearSelectedBooks();
            bookSearchModalController?.reset?.();
        } catch (error) {
            console.error(error);
            alert(error?.message || "대출 처리 중 오류가 발생했습니다.");
        } finally {
            updateActionButtons();
        }
    }

    function requestBorrow() {
        const requestBody = createBorrowRequestBody();

        if (!validateBorrowRequest(requestBody)) {
            updateActionButtons();
            return;
        }

        openConfirmModal(() => executeBorrow(requestBody));
    }

    function openBookSearchModal() {
        onActivateBorrowMode?.();

        if (typeof bookSearchModalController?.open === "function") {
            bookSearchModalController.open();
            return;
        }

        if (typeof bookSearchModalController?.show === "function") {
            bookSearchModalController.show();
            return;
        }

        if (typeof bookSearchModalController?.activate === "function") {
            bookSearchModalController.activate();
        }
    }

    function activate() {
        onActivateBorrowMode?.();
        render();
    }

    borrowBookList?.addEventListener("click", (event) => {
        const removeButton = event.target.closest('[data-role="remove-borrow-book"]');

        if (removeButton) {
            removeSelectedBook(removeButton.dataset.bookId);
            return;
        }

        const decreaseButton = event.target.closest('[data-role="decrease-borrow-quantity"]');

        if (decreaseButton) {
            decreaseSelectedBookQuantity(decreaseButton.dataset.bookId);
            return;
        }

        const increaseButton = event.target.closest('[data-role="increase-borrow-quantity"]');

        if (increaseButton) {
            increaseSelectedBookQuantity(increaseButton.dataset.bookId);
        }
    });

    borrowBookList?.addEventListener("change", (event) => {
        const quantityInput = event.target.closest('[data-role="borrow-quantity-input"]');

        if (!quantityInput) {
            return;
        }

        updateSelectedBookQuantity(quantityInput.dataset.bookId, quantityInput.value);
    });

    borrowBookList?.addEventListener("blur", (event) => {
        const quantityInput = event.target.closest('[data-role="borrow-quantity-input"]');

        if (!quantityInput) {
            return;
        }

        updateSelectedBookQuantity(quantityInput.dataset.bookId, quantityInput.value);
    }, true);

    openBorrowBookSearchButton?.addEventListener("click", openBookSearchModal);

    resetBorrowButton?.addEventListener("click", () => {
        clearSelectedBooks();
        bookSearchModalController?.reset?.();
    });

    confirmBorrowButton?.addEventListener("click", requestBorrow);

    render();

    return {
        activate,

        getSelectedBooks,
        clearSelectedBooks,
        replaceSelectedBooks,

        // 기존 호출부 호환용
        getBooks: getSelectedBooks,
        clear: clearSelectedBooks,
        replaceBooks: replaceSelectedBooks
    };
}