import { renderAsyncPagination } from "../../../common/async-pagination.js";

export function createBookSearchModal({ getConfirmedBooks, onConfirmSelected } = {}) {
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

    const defaultBookSearchType = bookSearchTypeSelect?.options[0]?.value ?? "";

    const bookSearchState = {
        searchType: defaultBookSearchType,
        keyword: "",
        page: 0,
        size: 5
    };

    const pendingSelectedBooks = new Map();

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

        return {
            id: String(rawBook.id ?? ""),
            title: rawBook.title ?? "",
            isbn: rawBook.isbn ?? "",
            author: rawBook.author ?? "",
            publisher: rawBook.publisher ?? "",
            location: rawBook.location ?? "",
            stockQuantity: toPositiveInt(rawBook.stockQuantity, 0),
            availableQuantity,
            quantity: availableQuantity > 0
                ? clampBorrowQuantity({ availableQuantity }, rawBook.quantity ?? 1)
                : 0
        };
    }

    function bindFields(root, data, fallback = "-") {
        root.querySelectorAll("[data-field]").forEach((element) => {
            const value = data[element.dataset.field];

            element.textContent = value === null || value === undefined || value === ""
                ? fallback
                : String(value);
        });
    }

    function setElementDisabled(element, disabled) {
        if (element) {
            element.disabled = disabled;
        }
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

    function updateBookSelectionControls() {
        const selectedCount = pendingSelectedBooks.size;

        if (bookSearchSelectedCount) {
            bookSearchSelectedCount.textContent = String(selectedCount);
        }

        setElementDisabled(confirmBookSelectionButton, selectedCount === 0);

        bookSearchResultRows?.querySelectorAll('[data-role="select-book"]').forEach((row) => {
            const checkbox = row.querySelector(".book-search-result__checkbox");
            const isSelected = pendingSelectedBooks.has(row.dataset.bookId);

            row.classList.toggle("is-selected", isSelected);

            if (checkbox) {
                checkbox.checked = isSelected;
            }
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

    function renderBookRows(books) {
        if (!bookSearchResultRows) return;

        bookSearchResultRows.innerHTML = "";

        const fragment = document.createDocumentFragment();

        books.forEach((book) => {
            const row = createBookSearchRowElement(book);

            if (row) {
                fragment.appendChild(row);
            }
        });

        bookSearchResultRows.appendChild(fragment);
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

        if (!bookSearchPagination) return;

        bookSearchPagination.classList.remove("is-hidden");

        renderAsyncPagination(bookSearchPagination, {
            currentPage,
            totalPages,
            visiblePages: 5,
            onPageChange: loadBooks
        });
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

    function reset() {
        pendingSelectedBooks.clear();
        resetBookSearchForm();
        showBookSearchInitialState();
    }

    function syncPendingBooksFromConfirmed() {
        pendingSelectedBooks.clear();

        if (typeof getConfirmedBooks !== "function") return;

        getConfirmedBooks().forEach((book) => {
            const normalizedBook = normalizeBook(book);
            pendingSelectedBooks.set(normalizedBook.id, normalizedBook);
        });
    }

    function open() {
        if (!bookSearchModal) return;

        syncPendingBooksFromConfirmed();

        bookSearchModal.classList.remove("is-hidden");
        bookSearchModal.setAttribute("aria-hidden", "false");

        updateBookSelectionControls();

        window.setTimeout(() => bookSearchKeywordInput?.focus(), 0);
    }

    function close() {
        if (!bookSearchModal) return;

        bookSearchModal.classList.add("is-hidden");
        bookSearchModal.setAttribute("aria-hidden", "true");

        reset();
    }

    async function fetchBooks() {
        const params = new URLSearchParams();

        params.set(bookSearchState.searchType, bookSearchState.keyword);
        params.set("page", String(bookSearchState.page));
        params.set("size", String(bookSearchState.size));

        const payload = await apiGet(`/api/books?${params.toString()}`);

        return payload?.result ?? {};
    }

    async function loadBooks(page = 0) {
        try {
            bookSearchState.page = page;
            showBookSearchLoadingState();

            const pageResult = await fetchBooks();
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

        const keyword = bookSearchKeywordInput?.value.trim() ?? "";

        if (!keyword) {
            alert("검색어를 입력해 주세요.");
            bookSearchKeywordInput?.focus();
            return;
        }

        bookSearchState.searchType = bookSearchTypeSelect?.value ?? defaultBookSearchType;
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
        if (typeof onConfirmSelected === "function") {
            const selectedBooks = Array.from(pendingSelectedBooks.values()).map((book) => ({
                ...book,
                quantity: clampBorrowQuantity(book, book.quantity ?? 1)
            }));

            onConfirmSelected(selectedBooks);
        }

        close();
    }

    bookSearchOpenButtons.forEach((button) => {
        button.addEventListener("click", open);
    });

    bookSearchCloseButtons.forEach((button) => {
        button.addEventListener("click", close);
    });

    cancelBookSelectionButtons.forEach((button) => {
        button.addEventListener("click", close);
    });

    confirmBookSelectionButton?.addEventListener("click", confirmBookSelection);
    bookSearchForm?.addEventListener("submit", handleBookSearchSubmit);

    bookSearchResultRows?.addEventListener("click", (event) => {
        const row = event.target.closest('[data-role="select-book"]');
        if (row) {
            togglePendingBookSelection(row);
        }
    });

    bookSearchResultRows?.addEventListener("keydown", (event) => {
        if (event.key !== "Enter" && event.key !== " ") return;

        const row = event.target.closest('[data-role="select-book"]');
        if (!row || row.dataset.disabled === "true") return;

        event.preventDefault();
        togglePendingBookSelection(row);
    });

    document.addEventListener("keydown", (event) => {
        if (
            event.key === "Escape"
            && bookSearchModal
            && !bookSearchModal.classList.contains("is-hidden")
        ) {
            close();
        }
    });

    reset();

    return {
        reset
    };
}