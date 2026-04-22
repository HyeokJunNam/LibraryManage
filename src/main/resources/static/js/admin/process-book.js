import { renderAsyncPagination } from "../common/async-pagination.js";

export function createBookProcess() {
    const bookPanel = document.getElementById("bookPanel");
    const bookPanelEmpty = document.getElementById("bookPanelEmpty");
    const bookSelectionEmpty = document.getElementById("bookSelectionEmpty");
    const bookPanelResult = document.getElementById("bookPanelResult");
    const bookPanelDesc = document.getElementById("bookPanelDesc");
    const bookPanelModeBadge = document.getElementById("bookPanelModeBadge");
    const openBookSearchButton = document.getElementById("openBookSearchButton");
    const selectedBookCount = document.getElementById("selectedBookCount");
    const selectedBookList = document.getElementById("selectedBookList");

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
    const bookSearchPagination = document.getElementById("bookSearchPagination");

    const defaultBookSearchType = "title";

    const bookSearchState = {
        searchType: defaultBookSearchType,
        keyword: "",
        page: 0,
        size: 5
    };

    const pendingSelectedBooks = new Map();
    const confirmedSelectedBooks = new Map();

    function escapeHtml(value) {
        return String(value ?? "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#39;");
    }

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

    function getQuantityControlState(book) {
        const max = Math.max(1, toPositiveInt(book.availableQuantity, 0));
        const quantity = Math.max(1, toPositiveInt(book.quantity, 1));

        return {
            canDecrease: quantity > 1,
            canIncrease: quantity < max
        };
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

    function createSelectedBookRow(book, index) {
        const stock = toPositiveInt(book.stockQuantity, 0);
        const max = toPositiveInt(book.availableQuantity, 0);
        const quantity = max > 0 ? clampBorrowQuantity(book, book.quantity) : 0;
        const { canDecrease, canIncrease } = getQuantityControlState({
            ...book,
            quantity
        });

        return `
            <tr data-book-id="${escapeHtml(book.id)}">
                <td class="book-result__cell--index">${index + 1}</td>
                <td>
                    <span class="book-result__title-text">${escapeHtml(book.title || "-")}</span>
                </td>
                <td class="book-result__muted">${escapeHtml(book.isbn || "-")}</td>
                <td>${escapeHtml(book.author || "-")}</td>
                <td>${escapeHtml(book.publisher || "-")}</td>
                <td>${escapeHtml(book.location || "-")}</td>
                <td>${escapeHtml(stock)}</td>
                <td>${escapeHtml(max)}</td>
                <td class="book-result__quantity-cell">
                    <div class="book-result__quantity-box">
                        <button
                            type="button"
                            class="book-result__quantity-btn"
                            data-role="decrease-book-quantity"
                            data-book-id="${escapeHtml(book.id)}"
                            ${!canDecrease ? "disabled" : ""}>
                            −
                        </button>

                        <input
                            type="number"
                            class="book-result__quantity-input"
                            data-role="book-quantity-input"
                            data-book-id="${escapeHtml(book.id)}"
                            min="1"
                            max="${escapeHtml(max)}"
                            value="${escapeHtml(quantity)}"
                            ${max <= 0 ? "disabled" : ""}>

                        <button
                            type="button"
                            class="book-result__quantity-btn"
                            data-role="increase-book-quantity"
                            data-book-id="${escapeHtml(book.id)}"
                            ${!canIncrease ? "disabled" : ""}>
                            +
                        </button>
                    </div>
                </td>
                <td class="book-result__action">
                    <button
                        type="button"
                        class="book-result__remove-btn"
                        data-role="remove-selected-book"
                        data-book-id="${escapeHtml(book.id)}">
                        삭제
                    </button>
                </td>
            </tr>
        `;
    }

    function updateBookPanelResult() {
        const books = Array.from(confirmedSelectedBooks.values());

        if (selectedBookCount) {
            selectedBookCount.textContent = String(books.length);
        }

        if (!selectedBookList) return;

        if (books.length === 0) {
            selectedBookList.innerHTML = `
                <tr class="book-result__empty-row">
                    <td colspan="10">선택된 도서가 없습니다.</td>
                </tr>
            `;
            bookSelectionEmpty?.classList.remove("is-hidden");
            bookPanelResult?.classList.add("is-hidden");
            return;
        }

        selectedBookList.innerHTML = books
            .map((book, index) => createSelectedBookRow(book, index))
            .join("");

        bookSelectionEmpty?.classList.add("is-hidden");
        bookPanelResult?.classList.remove("is-hidden");
    }

    function removeConfirmedBook(bookId) {
        if (!bookId) return;

        confirmedSelectedBooks.delete(bookId);
        pendingSelectedBooks.delete(bookId);

        updateBookPanelResult();

        if (confirmedSelectedBooks.size === 0) {
            bookSelectionEmpty?.classList.remove("is-hidden");
            bookPanelResult?.classList.add("is-hidden");
        }
    }

    function updateConfirmedBookQuantity(bookId, nextQuantity) {
        const targetBook = confirmedSelectedBooks.get(bookId);
        if (!targetBook) return;

        const quantity = clampBorrowQuantity(targetBook, nextQuantity);
        const updatedBook = {
            ...targetBook,
            quantity
        };

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

        bookPanel?.setAttribute("data-mode", "idle");

        bookPanelEmpty?.classList.remove("is-hidden");
        bookSelectionEmpty?.classList.add("is-hidden");
        bookPanelResult?.classList.add("is-hidden");

        if (bookPanelDesc) {
            bookPanelDesc.textContent = "대출 시에는 대출할 도서를, 반납 시에는 반납할 도서를 이 영역에서 확인합니다.";
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

        if (selectedBookList) {
            selectedBookList.innerHTML = "";
        }
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

        if (confirmedSelectedBooks.size === 0) {
            bookSelectionEmpty?.classList.remove("is-hidden");
            bookPanelResult?.classList.add("is-hidden");
        }
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

        if (confirmBookSelectionButton) {
            confirmBookSelectionButton.disabled = selectedCount === 0;
        }

        bookSearchResultRows?.querySelectorAll('[data-role="select-book"]').forEach((row) => {
            const bookId = row.dataset.bookId;
            const checkbox = row.querySelector('.book-search-result__checkbox');
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

    function hideAllBookSearchStates() {
        bookSearchEmpty?.classList.add("is-hidden");
        bookSearchLoading?.classList.add("is-hidden");
        bookSearchNoResultPanel?.classList.add("is-hidden");
        bookSearchResultPanel?.classList.add("is-hidden");
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

    function showBookSearchInitialState() {
        hideAllBookSearchStates();
        clearBookSearchRows();
        bookSearchEmpty?.classList.remove("is-hidden");
        updateBookSelectionControls();
    }

    function showBookSearchLoadingState() {
        hideAllBookSearchStates();
        clearBookSearchRows();
        bookSearchLoading?.classList.remove("is-hidden");
        updateBookSelectionControls();
    }

    function showBookSearchNoResultState() {
        hideAllBookSearchStates();
        clearBookSearchRows();
        bookSearchNoResultPanel?.classList.remove("is-hidden");
        updateBookSelectionControls();
    }

    function showBookSearchResultState(pageResult) {
        hideAllBookSearchStates();
        bookSearchResultPanel?.classList.remove("is-hidden");

        const books = Array.isArray(pageResult.content) ? pageResult.content : [];
        const totalElements = Number(pageResult.totalElements ?? books.length);
        const currentPage = Number(pageResult.page ?? 0);
        const totalPages = Math.max(1, Number(pageResult.totalPages ?? 0));

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
                onPageChange: (nextPage) => {
                    loadBooks(nextPage);
                }
            });
        }
    }

    function resetBookSearchForm() {
        if (bookSearchForm) {
            bookSearchForm.reset();
        }

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

    function renderBookRows(books) {
        if (!bookSearchResultRows) return;

        bookSearchResultRows.innerHTML = books.map((rawBook) => {
            const safeBook = normalizeBook(rawBook);
            const encoded = encodeURIComponent(JSON.stringify(safeBook));
            const isSelected = pendingSelectedBooks.has(safeBook.id);
            const isDisabled = safeBook.availableQuantity <= 0;

            return `
                <button
                    type="button"
                    class="book-search-result__row ${isSelected ? "is-selected" : ""} ${isDisabled ? "is-disabled" : ""}"
                    data-role="select-book"
                    data-book-id="${escapeHtml(safeBook.id)}"
                    data-book="${encoded}"
                    ${isDisabled ? "disabled" : ""}>
                    <div class="book-search-result__checkbox-wrap">
                        <input
                            type="checkbox"
                            class="book-search-result__checkbox"
                            ${isSelected ? "checked" : ""}
                            ${isDisabled ? "disabled" : ""}
                            tabindex="-1"
                            aria-hidden="true">
                    </div>
                    <div class="book-search-result__cell book-search-result__cell--primary">
                        ${escapeHtml(safeBook.title || "-")}
                    </div>
                    <div class="book-search-result__cell">
                        ${escapeHtml(safeBook.isbn || "-")}
                    </div>
                    <div class="book-search-result__cell">
                        ${escapeHtml(safeBook.author || "-")}
                    </div>
                    <div class="book-search-result__cell book-search-result__stock">
                        ${escapeHtml(safeBook.stockQuantity)}
                    </div>
                    <div class="book-search-result__cell book-search-result__available ${safeBook.availableQuantity > 0 ? "is-positive" : "is-zero"}">
                        ${escapeHtml(safeBook.availableQuantity)}
                    </div>
                </button>
            `;
        }).join("");
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
        if (!raw || !bookId) return;
        if (row.disabled) return;

        try {
            const book = normalizeBook(JSON.parse(decodeURIComponent(raw)));

            if (book.availableQuantity <= 0) {
                return;
            }

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

        activateBorrowMode();
        updateBookPanelResult();
        closeBookModal();
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

    bookSearchForm?.addEventListener("submit", handleBookSearchSubmit);

    bookSearchResultRows?.addEventListener("click", (event) => {
        const row = event.target.closest('[data-role="select-book"]');
        if (!row) return;

        togglePendingBookSelection(row);
    });

    selectedBookList?.addEventListener("click", (event) => {
        const removeButton = event.target.closest('[data-role="remove-selected-book"]');
        if (removeButton) {
            const bookId = removeButton.dataset.bookId;
            removeConfirmedBook(bookId);
            return;
        }

        const decreaseButton = event.target.closest('[data-role="decrease-book-quantity"]');
        if (decreaseButton) {
            const bookId = decreaseButton.dataset.bookId;
            const book = confirmedSelectedBooks.get(bookId);
            if (!book) return;

            updateConfirmedBookQuantity(bookId, Number(book.quantity ?? 1) - 1);
            return;
        }

        const increaseButton = event.target.closest('[data-role="increase-book-quantity"]');
        if (increaseButton) {
            const bookId = increaseButton.dataset.bookId;
            const book = confirmedSelectedBooks.get(bookId);
            if (!book) return;

            updateConfirmedBookQuantity(bookId, Number(book.quantity ?? 1) + 1);
        }
    });

    selectedBookList?.addEventListener("change", (event) => {
        const quantityInput = event.target.closest('[data-role="book-quantity-input"]');
        if (!quantityInput) return;

        const bookId = quantityInput.dataset.bookId;
        updateConfirmedBookQuantity(bookId, quantityInput.value);
    });

    selectedBookList?.addEventListener("blur", (event) => {
        const quantityInput = event.target.closest('[data-role="book-quantity-input"]');
        if (!quantityInput) return;

        const bookId = quantityInput.dataset.bookId;
        updateConfirmedBookQuantity(bookId, quantityInput.value);
    }, true);

    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape" && bookSearchModal && !bookSearchModal.classList.contains("is-hidden")) {
            closeBookModal();
        }
    });

    reset();
    resetBookSearchModal();

    return {
        reset,
        activateBorrowMode,
        getSelectedBooks() {
            return Array.from(confirmedSelectedBooks.values()).map((book) => ({
                bookId: Number(book.id),
                quantity: Number(book.quantity)
            }));
        }
    };
}