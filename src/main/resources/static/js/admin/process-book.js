import { renderAsyncPagination } from "../common/async-pagination.js";

export function createBookProcess({ getSelectedMemberId } = {}) {
    const bookPanel = document.getElementById("bookPanel");
    const bookPanelEmpty = document.getElementById("bookPanelEmpty");
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

    const bookSearchModal = document.getElementById("bookSearchModal");
    const bookSearchOpenButtons = document.querySelectorAll('[data-role="open-book-search"]');
    const bookSearchCloseButtons = document.querySelectorAll('[data-role="close-book-search"]');
    const cancelBookSelectionButtons = document.querySelectorAll('[data-role="cancel-book-selection"]');
    const confirmBookSelectionButton = document.getElementById("confirmBookSelectionButton");

    const bookSearchForm = document.getElementById("bookSearchForm");
    const bookSearchTypeSelect = bookSearchForm?.querySelector('select[name="searchType"]');
    const bookSearchKeywordInput = bookSearchForm?.querySelector('input[name="keyword"]');

    const bookSearchEmpty = document.getElementById("bookSearchEmpty");
    const bookSearchLoading = document.getElementById("bookSearchLoading");
    const bookSearchNoResultPanel = document.getElementById("bookSearchNoResultPanel");
    const bookSearchResultPanel = document.getElementById("bookSearchResultPanel");
    const bookSearchCount = document.getElementById("bookSearchCount");
    const bookSearchSelectedCount = document.getElementById("bookSearchSelectedCount");
    const bookSearchResultRows = document.getElementById("bookSearchResultRows");
    const bookSearchRowTemplate = document.getElementById("bookSearchRowTemplate");
    const bookSearchPagination = document.getElementById("bookSearchPagination");

    const defaultBookSearchType = "title";

    const selectedBookState = {
        page: 0,
        size: 5
    };

    const bookSearchState = {
        searchType: defaultBookSearchType,
        keyword: "",
        page: 0,
        size: 5
    };

    const pendingSelectedBooks = new Map();
    const confirmedSelectedBooks = new Map();

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

    function normalizeBook(rawBook) {
        const availableQuantity = toPositiveInt(rawBook.availableQuantity, 0);
        const stockQuantity = toPositiveInt(rawBook.stockQuantity, 0);

        return {
            id: String(rawBook.id ?? ""),
            title: rawBook.title ?? "",
            isbn: rawBook.isbn ?? "",
            author: rawBook.author ?? "",
            publisher: rawBook.publisher ?? "",
            location: rawBook.location ?? "",
            stockQuantity,
            availableQuantity,
            quantity: availableQuantity > 0
                ? clampBorrowQuantity({ availableQuantity }, rawBook.quantity ?? 1)
                : 0
        };
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

    function setBookIdToRoleElements(root, bookId, roles) {
        roles.forEach((role) => {
            root.querySelector(`[data-role="${role}"]`)?.setAttribute("data-book-id", bookId);
        });
    }

    function setElementDisabled(element, disabled) {
        if (!element) return;
        element.disabled = disabled;
    }

    function setInteractiveDisabled(element, disabled) {
        if (!element) return;

        element.dataset.disabled = String(disabled);
        element.setAttribute("aria-disabled", String(disabled));
        element.classList.toggle("is-disabled", disabled);

        if (disabled) {
            element.removeAttribute("tabindex");
        } else {
            element.setAttribute("tabindex", "0");
        }
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

    function clearBookSearchRows() {
        if (bookSearchResultRows) {
            bookSearchResultRows.innerHTML = "";
        }

        if (bookSearchCount) {
            bookSearchCount.textContent = "0";
        }

        if (bookSearchPagination) {
            bookSearchPagination.innerHTML = "";
            bookSearchPagination.classList.add("is-hidden");
        }
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

        row.dataset.bookId = book.id;

        bindFields(row, {
            ...book,
            index: index + 1,
            availableQuantity: max,
            quantity
        });

        const quantityInput = row.querySelector('[data-role="book-quantity-input"]');

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

    function createBookSearchRowElement(rawBook) {
        if (!bookSearchRowTemplate) return null;

        const book = normalizeBook(rawBook);
        const row = bookSearchRowTemplate.content.firstElementChild.cloneNode(true);
        const checkbox = row.querySelector(".book-search-result__checkbox");
        const availableCell = row.querySelector('[data-field="availableQuantity"]');

        const isSelected = pendingSelectedBooks.has(book.id);
        const isDisabled = book.availableQuantity <= 0;

        row.dataset.bookId = book.id;
        row.dataset.book = encodeURIComponent(JSON.stringify(book));
        row.classList.toggle("is-selected", isSelected);

        setInteractiveDisabled(row, isDisabled);
        bindFields(row, book);

        if (checkbox) {
            checkbox.checked = isSelected;
            checkbox.disabled = isDisabled;
            checkbox.setAttribute("aria-label", `${book.title || "도서"} 선택`);
        }

        if (availableCell) {
            availableCell.classList.toggle("is-positive", book.availableQuantity > 0);
            availableCell.classList.toggle("is-zero", book.availableQuantity <= 0);
        }

        return row;
    }

    function renderRows(container, items, createRow) {
        if (!container) return;

        container.innerHTML = "";

        const fragment = document.createDocumentFragment();

        items.forEach((item, index) => {
            const row = createRow(item, index);

            if (row) {
                fragment.appendChild(row);
            }
        });

        container.appendChild(fragment);
    }

    function renderBookRows(books) {
        renderRows(bookSearchResultRows, books, createBookSearchRowElement);
    }

    function updateBorrowActionButtons() {
        const hasBooks = confirmedSelectedBooks.size > 0;

        bookPanelFooter?.classList.toggle("is-hidden", !hasBooks);
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

        selectedBookEmptyRow?.classList.toggle("is-hidden", hasBooks);
        bookSelectionEmpty?.classList.toggle("is-hidden", hasBooks);
        bookPanelResult?.classList.toggle("is-hidden", !hasBooks);

        const totalPages = Math.max(1, Math.ceil(books.length / selectedBookState.size));

        if (selectedBookState.page >= totalPages) {
            selectedBookState.page = totalPages - 1;
        }

        if (hasBooks) {
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
        } else {
            clearSelectedBookPagination();
        }

        updateBorrowActionButtons();
    }

    function removeConfirmedBook(bookId) {
        if (!bookId) return;

        confirmedSelectedBooks.delete(bookId);
        pendingSelectedBooks.delete(bookId);

        updateBookPanelResult();
    }

    function updateConfirmedBookQuantity(bookId, nextQuantity) {
        const targetBook = confirmedSelectedBooks.get(bookId);
        if (!targetBook) return;

        const quantity = clampBorrowQuantity(targetBook, nextQuantity);
        const updatedBook = { ...targetBook, quantity };

        confirmedSelectedBooks.set(bookId, updatedBook);

        if (pendingSelectedBooks.has(bookId)) {
            pendingSelectedBooks.set(bookId, {
                ...pendingSelectedBooks.get(bookId),
                quantity
            });
        }

        updateBookPanelResult();
    }

    function reset() {
        confirmedSelectedBooks.clear();
        pendingSelectedBooks.clear();
        selectedBookState.page = 0;

        bookPanel?.setAttribute("data-mode", "idle");

        bookPanelEmpty?.classList.remove("is-hidden");
        bookSelectionEmpty?.classList.add("is-hidden");
        bookPanelResult?.classList.add("is-hidden");

        if (bookPanelDesc) {
            bookPanelDesc.textContent = "대출 시에는 대출할 도서를 선택하세요.";
        }

        if (bookPanelModeBadge) {
            bookPanelModeBadge.textContent = "";
            bookPanelModeBadge.classList.add("is-hidden");
        }

        if (openBookSearchButton) {
            openBookSearchButton.classList.add("is-hidden");
            openBookSearchButton.disabled = true;
        }

        if (selectedBookCount) {
            selectedBookCount.textContent = "0";
        }

        clearSelectedBookRows();
        clearSelectedBookPagination();
        selectedBookEmptyRow?.classList.remove("is-hidden");

        updateBorrowActionButtons();
    }

    function activateBorrowMode() {
        bookPanel?.setAttribute("data-mode", "borrow");

        bookPanelEmpty?.classList.add("is-hidden");

        if (bookPanelDesc) {
            bookPanelDesc.textContent = "대출할 도서를 검색하여 목록에 추가한 뒤 대출 처리를 진행할 수 있습니다.";
        }

        if (bookPanelModeBadge) {
            bookPanelModeBadge.textContent = "대출 모드";
            bookPanelModeBadge.classList.remove("is-hidden");
        }

        if (openBookSearchButton) {
            openBookSearchButton.classList.remove("is-hidden");
            openBookSearchButton.disabled = false;
        }

        updateBookPanelResult();
    }

    function syncPendingBooksFromConfirmed() {
        pendingSelectedBooks.clear();

        confirmedSelectedBooks.forEach((book, id) => {
            pendingSelectedBooks.set(id, { ...book });
        });
    }

    function updateBookSelectionControls() {
        const selectedCount = pendingSelectedBooks.size;

        if (bookSearchSelectedCount) {
            bookSearchSelectedCount.textContent = String(selectedCount);
        }

        setElementDisabled(confirmBookSelectionButton, selectedCount === 0);

        bookSearchResultRows?.querySelectorAll('[data-role="select-book"]').forEach((row) => {
            const bookId = row.dataset.bookId;
            const checkbox = row.querySelector(".book-search-result__checkbox");
            const isSelected = pendingSelectedBooks.has(bookId);

            row.classList.toggle("is-selected", isSelected);

            if (checkbox) {
                checkbox.checked = isSelected;
            }
        });
    }

    function openBookModal() {
        if (!bookSearchModal) return;

        syncPendingBooksFromConfirmed();

        bookSearchModal.classList.remove("is-hidden");
        bookSearchModal.setAttribute("aria-hidden", "false");

        updateBookSelectionControls();

        window.setTimeout(() => bookSearchKeywordInput?.focus(), 0);
    }

    function showOnlyBookSearchState(visibleStateElement) {
        [
            bookSearchEmpty,
            bookSearchLoading,
            bookSearchNoResultPanel,
            bookSearchResultPanel
        ].forEach((element) => {
            element?.classList.toggle("is-hidden", element !== visibleStateElement);
        });
    }

    function showBookSearchInitialState() {
        clearBookSearchRows();
        showOnlyBookSearchState(bookSearchEmpty);
        updateBookSelectionControls();
    }

    function showBookSearchLoadingState() {
        clearBookSearchRows();
        showOnlyBookSearchState(bookSearchLoading);
        updateBookSelectionControls();
    }

    function showBookSearchNoResultState() {
        clearBookSearchRows();
        showOnlyBookSearchState(bookSearchNoResultPanel);
        updateBookSelectionControls();
    }

    function showBookSearchResultState(pageResult) {
        const books = Array.isArray(pageResult.content) ? pageResult.content : [];
        const totalElements = Number(pageResult.totalElements ?? books.length);
        const currentPage = Number(pageResult.page ?? 0);
        const totalPages = Math.max(1, Number(pageResult.totalPages ?? 0));

        showOnlyBookSearchState(bookSearchResultPanel);

        if (bookSearchCount) {
            bookSearchCount.textContent = String(totalElements);
        }

        renderBookRows(books);
        updateBookSelectionControls();

        if (bookSearchPagination) {
            bookSearchPagination.classList.remove("is-hidden");

            renderAsyncPagination(bookSearchPagination, {
                currentPage,
                totalPages,
                visiblePages: 5,
                onPageChange: loadBooks
            });
        }
    }

    function resetBookSearchForm() {
        bookSearchForm?.reset();

        if (bookSearchTypeSelect) {
            bookSearchTypeSelect.value = defaultBookSearchType;
        }

        if (bookSearchKeywordInput) {
            bookSearchKeywordInput.value = "";
        }

        bookSearchState.searchType = defaultBookSearchType;
        bookSearchState.keyword = "";
        bookSearchState.page = 0;
    }

    function resetBookSearchModal() {
        pendingSelectedBooks.clear();
        resetBookSearchForm();
        showBookSearchInitialState();
    }

    function closeBookModal() {
        if (!bookSearchModal) return;

        bookSearchModal.classList.add("is-hidden");
        bookSearchModal.setAttribute("aria-hidden", "true");

        resetBookSearchModal();
    }

    async function fetchBooks({ searchType, keyword, page, size }) {
        const params = new URLSearchParams();

        params.set(searchType, keyword);
        params.set("page", String(page));
        params.set("size", String(size));

        const payload = await apiGet(`/api/books?${params.toString()}`);

        return payload?.result ?? {};
    }

    async function loadBooks(page = 0) {
        try {
            bookSearchState.page = page;

            showBookSearchLoadingState();

            const pageResult = await fetchBooks(bookSearchState);
            const books = Array.isArray(pageResult.content) ? pageResult.content : [];

            if (books.length === 0) {
                showBookSearchNoResultState();
                return;
            }

            showBookSearchResultState(pageResult);
        } catch (error) {
            console.error(error);

            alert(error?.message || "도서 조회 중 오류가 발생했습니다.");

            showBookSearchNoResultState();
        }
    }

    function handleBookSearchSubmit(event) {
        event.preventDefault();

        const searchType = bookSearchTypeSelect?.value ?? defaultBookSearchType;
        const keyword = bookSearchKeywordInput?.value.trim() ?? "";

        if (!keyword) {
            alert("검색어를 입력해 주세요.");
            bookSearchKeywordInput?.focus();
            return;
        }

        bookSearchState.searchType = searchType;
        bookSearchState.keyword = keyword;
        bookSearchState.page = 0;

        loadBooks(0);
    }

    function togglePendingBookSelection(row) {
        const raw = row.dataset.book;
        const bookId = row.dataset.bookId;

        if (!raw || !bookId || row.dataset.disabled === "true") return;

        try {
            const book = normalizeBook(JSON.parse(decodeURIComponent(raw)));

            if (book.availableQuantity <= 0) return;

            if (pendingSelectedBooks.has(bookId)) {
                pendingSelectedBooks.delete(bookId);
            } else {
                pendingSelectedBooks.set(bookId, book);
            }

            updateBookSelectionControls();
        } catch (error) {
            console.error("선택한 도서 데이터 파싱 실패", error);
        }
    }

    function confirmBookSelection() {
        confirmedSelectedBooks.clear();

        pendingSelectedBooks.forEach((book, id) => {
            confirmedSelectedBooks.set(id, {
                ...book,
                quantity: clampBorrowQuantity(book, book.quantity ?? 1)
            });
        });

        selectedBookState.page = 0;

        activateBorrowMode();
        updateBookPanelResult();
        closeBookModal();
    }

    function clearSelectedBooks() {
        confirmedSelectedBooks.clear();
        pendingSelectedBooks.clear();
        selectedBookState.page = 0;

        updateBookPanelResult();

        if (bookPanel?.dataset.mode === "borrow") {
            bookSelectionEmpty?.classList.remove("is-hidden");
            bookPanelResult?.classList.add("is-hidden");
        }

        updateBorrowActionButtons();
    }

    function resolveSelectedMemberId() {
        if (typeof getSelectedMemberId !== "function") {
            return "";
        }

        return getSelectedMemberId();
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

    bookSearchOpenButtons.forEach((button) => {
        button.addEventListener("click", openBookModal);
    });

    bookSearchCloseButtons.forEach((button) => {
        button.addEventListener("click", closeBookModal);
    });

    cancelBookSelectionButtons.forEach((button) => {
        button.addEventListener("click", closeBookModal);
    });

    confirmBookSelectionButton?.addEventListener("click", confirmBookSelection);

    resetBorrowButton?.addEventListener("click", clearSelectedBooks);
    confirmBorrowButton?.addEventListener("click", requestBorrow);

    bookSearchForm?.addEventListener("submit", handleBookSearchSubmit);

    bookSearchResultRows?.addEventListener("click", (event) => {
        const row = event.target.closest('[data-role="select-book"]');
        if (!row) return;

        togglePendingBookSelection(row);
    });

    bookSearchResultRows?.addEventListener("keydown", (event) => {
        if (event.key !== "Enter" && event.key !== " ") return;

        const row = event.target.closest('[data-role="select-book"]');
        if (!row || row.dataset.disabled === "true") return;

        event.preventDefault();
        togglePendingBookSelection(row);
    });

    selectedBookList?.addEventListener("click", (event) => {
        const removeButton = event.target.closest('[data-role="remove-selected-book"]');

        if (removeButton) {
            removeConfirmedBook(removeButton.dataset.bookId);
            return;
        }

        const decreaseButton = event.target.closest('[data-role="decrease-book-quantity"]');

        if (decreaseButton) {
            const book = confirmedSelectedBooks.get(decreaseButton.dataset.bookId);
            if (!book) return;

            updateConfirmedBookQuantity(book.id, Number(book.quantity ?? 1) - 1);
            return;
        }

        const increaseButton = event.target.closest('[data-role="increase-book-quantity"]');

        if (increaseButton) {
            const book = confirmedSelectedBooks.get(increaseButton.dataset.bookId);
            if (!book) return;

            updateConfirmedBookQuantity(book.id, Number(book.quantity ?? 1) + 1);
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

    document.addEventListener("keydown", (event) => {
        if (
            event.key === "Escape"
            && bookSearchModal
            && !bookSearchModal.classList.contains("is-hidden")
        ) {
            closeBookModal();
        }
    });

    reset();
    resetBookSearchModal();

    return {
        reset,
        activateBorrowMode
    };
}