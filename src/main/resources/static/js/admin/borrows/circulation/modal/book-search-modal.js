export function createBookSearchModal({ getConfirmedBooks, onConfirmSelected } = {}) {
    const modal = document.getElementById("bookSearchModal");

    if (!modal) {
        return {
            open() {},
            close() {},
            reset() {}
        };
    }

    const openButtons = document.querySelectorAll('[data-role="open-book-search"]');
    const pendingSelectedBooks = new Map();

    let hasLoadedOnce = false;
    let preloadPromise = null;

    function getResultMount() {
        return modal.querySelector('[data-role="book-search-result-mount"]');
    }

    function getSearchKeyword() {
        return modal.querySelector("[data-table-search-keyword]");
    }

    function getConfirmButton() {
        return modal.querySelector("#confirmBookSelectionButton");
    }

    function toPositiveInt(value, fallback = 0) {
        const number = Number(value);
        if (!Number.isFinite(number)) return fallback;
        return Math.max(0, Math.floor(number));
    }

    function normalizeBook(rawBook) {
        return {
            id: String(rawBook.id ?? ""),
            title: rawBook.title ?? "",
            isbn: rawBook.isbn ?? "",
            author: rawBook.author ?? "",
            publisher: rawBook.publisher ?? "",
            stockQuantity: toPositiveInt(rawBook.stockQuantity, 0),
            availableQuantity: toPositiveInt(rawBook.availableQuantity, 0)
        };
    }

    function syncPendingBooksFromConfirmed() {
        pendingSelectedBooks.clear();

        if (typeof getConfirmedBooks !== "function") return;

        getConfirmedBooks().forEach((book) => {
            const normalizedBook = normalizeBook(book);
            if (!normalizedBook.id) return;

            pendingSelectedBooks.set(normalizedBook.id, normalizedBook);
        });
    }

    function updateSelectionControls() {
        const rows = modal.querySelectorAll('[data-role="select-book-row"]');

        rows.forEach((row) => {
            const checkbox = row.querySelector(".book-search-result__checkbox");
            const bookId = row.dataset.bookId;
            const isSelected = pendingSelectedBooks.has(bookId);

            row.classList.toggle("is-selected", isSelected);

            if (checkbox) {
                checkbox.checked = isSelected;
            }
        });

        const confirmButton = getConfirmButton();
        if (confirmButton) {
            confirmButton.disabled = pendingSelectedBooks.size === 0;
        }
    }

    async function loadResultPanel() {
        const resultMount = getResultMount();
        if (!resultMount || !window.TableLayout?.reload) return;

        await window.TableLayout.reload(resultMount);
        hasLoadedOnce = true;
        updateSelectionControls();
    }

    function preloadResultPanel() {
        if (hasLoadedOnce) return Promise.resolve();
        if (preloadPromise) return preloadPromise;

        preloadPromise = loadResultPanel().finally(() => {
            preloadPromise = null;
        });

        return preloadPromise;
    }

    async function open() {
        syncPendingBooksFromConfirmed();

        modal.classList.remove("is-hidden");
        modal.setAttribute("aria-hidden", "false");

        if (!hasLoadedOnce) {
            await preloadResultPanel();
        } else {
            loadResultPanel();
            updateSelectionControls();
        }

        window.setTimeout(() => {
            getSearchKeyword()?.focus();
        }, 50);
    }

    function close() {
        modal.classList.add("is-hidden");
        modal.setAttribute("aria-hidden", "true");
    }

    function reset() {
        pendingSelectedBooks.clear();
        updateSelectionControls();
        close();
    }

    function buildBookFromRow(row) {
        return normalizeBook({
            id: row.dataset.bookId,
            title: row.dataset.bookTitle,
            isbn: row.dataset.bookIsbn,
            author: row.dataset.bookAuthor,
            publisher: row.dataset.bookPublisher,
            stockQuantity: row.dataset.bookStockQuantity,
            availableQuantity: row.dataset.bookAvailableQuantity
        });
    }

    function togglePendingBookSelection(row) {
        if (!row || row.dataset.disabled === "true") return;

        const book = buildBookFromRow(row);
        if (!book.id || book.availableQuantity <= 0) return;

        if (pendingSelectedBooks.has(book.id)) {
            pendingSelectedBooks.delete(book.id);
        } else {
            pendingSelectedBooks.set(book.id, book);
        }

        updateSelectionControls();
    }

    function confirmSelection() {
        if (typeof onConfirmSelected === "function") {
            onConfirmSelected(Array.from(pendingSelectedBooks.values()));
        }

        reset();
    }

    openButtons.forEach((button) => button.addEventListener("click", open));

    modal.addEventListener("click", (event) => {
        const closeButton = event.target.closest('[data-role="close-book-search"]');
        if (closeButton) {
            reset();
            return;
        }

        const confirmButton = event.target.closest("#confirmBookSelectionButton");
        if (confirmButton) {
            confirmSelection();
            return;
        }

        const row = event.target.closest('[data-role="select-book-row"]');
        if (row) {
            togglePendingBookSelection(row);
        }
    });

    modal.addEventListener("keydown", (event) => {
        if (event.key !== "Enter" && event.key !== " ") return;

        const row = event.target.closest('[data-role="select-book-row"]');
        if (!row) return;

        event.preventDefault();
        togglePendingBookSelection(row);
    });

    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape" && !modal.classList.contains("is-hidden")) {
            reset();
        }
    });

    const observer = new MutationObserver(() => {
        updateSelectionControls();
    });

    const resultMount = getResultMount();
    if (resultMount) {
        observer.observe(resultMount, { childList: true, subtree: true });
    }

    window.setTimeout(() => {
        preloadResultPanel();
    }, 0);

    return {
        open,
        close,
        reset
    };
}