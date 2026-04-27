const bookDrafts = [];

const dummyBookByIsbn = {
    isbn: "9788966260959",
    title: "클린 코드",
    author: "로버트 C. 마틴",
    publisher: "인사이트",
    location: "A-01",
    stockQuantity: 1,
    thumbnailUrl: "",
    description: "좋은 코드를 작성하기 위한 원칙과 실천 방법을 다루는 소프트웨어 개발서입니다."
};

document.addEventListener("DOMContentLoaded", () => {
    const bookDraftForm = document.getElementById("bookDraftForm");

    const isbnLookupKeywordInput = document.getElementById("isbnLookupKeyword");
    const lookupBookByIsbnButton = document.getElementById("lookupBookByIsbnButton");

    const isbnInput = document.getElementById("isbn");
    const titleInput = document.getElementById("title");
    const authorInput = document.getElementById("author");
    const publisherInput = document.getElementById("publisher");
    const locationInput = document.getElementById("location");
    const stockQuantityInput = document.getElementById("stockQuantity");
    const imageUrlInput = document.getElementById("imageUrl");
    const descriptionInput = document.getElementById("description");

    const addBookDraftButton = document.getElementById("addBookDraftButton");
    const clearBookDraftButton = document.getElementById("clearBookDraftButton");
    const saveBookBatchButton = document.getElementById("saveBookBatchButton");

    const bookDraftCount = document.getElementById("bookDraftCount");
    const bookDraftEmpty = document.getElementById("bookDraftEmpty");
    const bookDraftTableWrap = document.getElementById("bookDraftTableWrap");
    const bookDraftRows = document.getElementById("bookDraftRows");
    const bookDraftRowTemplate = document.getElementById("bookDraftRowTemplate");

    function openCustomAlert({ title = "안내", message = "", confirmText = "확인" } = {}) {
        if (typeof openAlertModal === "function") {
            openAlertModal({
                title,
                message,
                confirmText
            });
            return;
        }

        window.alert(message);
    }

    function openCustomConfirm({
                                   title = "확인",
                                   message = "",
                                   confirmText = "확인",
                                   cancelText = "취소",
                                   onConfirm
                               } = {}) {
        if (typeof openAlertModal === "function") {
            openAlertModal({
                title,
                message,
                confirmText,
                cancelText,
                onConfirm
            });
            return;
        }

        if (window.confirm(message)) {
            onConfirm?.();
        }
    }

    function getTrimmedValue(input) {
        return input?.value.trim() ?? "";
    }

    function toPositiveInt(value, fallback = 1) {
        const number = Number(value);

        if (!Number.isFinite(number)) {
            return fallback;
        }

        return Math.max(1, Math.floor(number));
    }

    function setInputValue(input, value) {
        if (!input) return;

        input.value = value ?? "";
    }

    function createBookDraftFromForm() {
        return {
            id: crypto.randomUUID(),
            isbn: getTrimmedValue(isbnInput),
            title: getTrimmedValue(titleInput),
            author: getTrimmedValue(authorInput),
            publisher: getTrimmedValue(publisherInput),
            location: getTrimmedValue(locationInput),
            stockQuantity: toPositiveInt(stockQuantityInput?.value, 1),
            thumbnailUrl: getTrimmedValue(imageUrlInput),
            description: getTrimmedValue(descriptionInput)
        };
    }

    function validateBookDraft(bookDraft) {
        if (!bookDraft.isbn) {
            openCustomAlert({
                title: "ISBN 입력 필요",
                message: "ISBN을 입력해 주세요."
            });
            isbnInput?.focus();
            return false;
        }

        if (!bookDraft.title) {
            openCustomAlert({
                title: "도서명 입력 필요",
                message: "도서명을 입력해 주세요."
            });
            titleInput?.focus();
            return false;
        }

        if (!bookDraft.author) {
            openCustomAlert({
                title: "저자 입력 필요",
                message: "저자를 입력해 주세요."
            });
            authorInput?.focus();
            return false;
        }

        if (!bookDraft.publisher) {
            openCustomAlert({
                title: "출판사 입력 필요",
                message: "출판사를 입력해 주세요."
            });
            publisherInput?.focus();
            return false;
        }

        if (!bookDraft.location) {
            openCustomAlert({
                title: "보관 위치 입력 필요",
                message: "보관 위치를 입력해 주세요."
            });
            locationInput?.focus();
            return false;
        }

        if (bookDraft.stockQuantity < 1) {
            openCustomAlert({
                title: "재고 수량 확인",
                message: "재고 수량은 1 이상이어야 합니다."
            });
            stockQuantityInput?.focus();
            return false;
        }

        const duplicatedBook = bookDrafts.find((item) => item.isbn === bookDraft.isbn);

        if (duplicatedBook) {
            openCustomAlert({
                title: "중복 ISBN",
                message: "이미 등록 예정 목록에 추가된 ISBN입니다."
            });
            isbnInput?.focus();
            return false;
        }

        return true;
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

    function clearDraftRows() {
        if (!bookDraftRows) return;

        bookDraftRows.innerHTML = "";
    }

    function createDraftRow(bookDraft, index) {
        if (!bookDraftRowTemplate) return null;

        const row = bookDraftRowTemplate.content.firstElementChild.cloneNode(true);

        row.dataset.bookDraftId = bookDraft.id;

        bindFields(row, {
            ...bookDraft,
            index: index + 1
        });

        return row;
    }

    function renderDraftRows() {
        if (!bookDraftRows) return;

        clearDraftRows();

        const fragment = document.createDocumentFragment();

        bookDrafts.forEach((bookDraft, index) => {
            const row = createDraftRow(bookDraft, index);

            if (row) {
                fragment.appendChild(row);
            }
        });

        bookDraftRows.appendChild(fragment);
    }

    function updateDraftSummary() {
        const hasDrafts = bookDrafts.length > 0;

        if (bookDraftCount) {
            bookDraftCount.textContent = String(bookDrafts.length);
        }

        bookDraftEmpty?.classList.toggle("is-hidden", hasDrafts);
        bookDraftTableWrap?.classList.toggle("is-hidden", !hasDrafts);

        if (clearBookDraftButton) {
            clearBookDraftButton.disabled = !hasDrafts;
        }

        if (saveBookBatchButton) {
            saveBookBatchButton.disabled = !hasDrafts;
        }
    }

    function renderDraftList() {
        renderDraftRows();
        updateDraftSummary();
    }

    function resetFormAfterAdd() {
        bookDraftForm?.reset();

        if (stockQuantityInput) {
            stockQuantityInput.value = "1";
        }

        titleInput?.focus();
    }

    function addBookDraft() {
        const bookDraft = createBookDraftFromForm();

        if (!validateBookDraft(bookDraft)) {
            return;
        }

        bookDrafts.push(bookDraft);

        renderDraftList();
        resetFormAfterAdd();

        openCustomAlert({
            title: "등록 예정 목록 추가",
            message: "도서가 등록 예정 목록에 추가되었습니다."
        });
    }

    function removeBookDraft(bookDraftId) {
        const targetIndex = bookDrafts.findIndex((bookDraft) => bookDraft.id === bookDraftId);

        if (targetIndex === -1) {
            return;
        }

        bookDrafts.splice(targetIndex, 1);
        renderDraftList();
    }

    function clearBookDrafts() {
        if (bookDrafts.length === 0) {
            return;
        }

        openCustomConfirm({
            title: "등록 예정 목록 비우기",
            message: "등록 예정 도서 목록을 모두 비우시겠습니까?",
            confirmText: "비우기",
            cancelText: "취소",
            onConfirm: () => {
                bookDrafts.splice(0, bookDrafts.length);
                renderDraftList();
            }
        });
    }

    function fillBookForm(book) {
        setInputValue(isbnInput, book.isbn);
        setInputValue(titleInput, book.title);
        setInputValue(authorInput, book.author);
        setInputValue(publisherInput, book.publisher);
        setInputValue(locationInput, book.location);
        setInputValue(stockQuantityInput, book.stockQuantity);
        setInputValue(imageUrlInput, book.thumbnailUrl);
        setInputValue(descriptionInput, book.description);
    }

    function lookupBookByIsbn() {
        const lookupIsbn = getTrimmedValue(isbnLookupKeywordInput);

        if (!lookupIsbn) {
            openCustomAlert({
                title: "ISBN 입력 필요",
                message: "조회할 ISBN을 입력해 주세요."
            });
            isbnLookupKeywordInput?.focus();
            return;
        }

        fillBookForm({
            ...dummyBookByIsbn,
            isbn: lookupIsbn
        });

        openCustomAlert({
            title: "도서 정보 불러오기",
            message: "더미 도서 정보를 입력 폼에 불러왔습니다."
        });
    }

    function createBookCreateEntries() {
        return bookDrafts.map((bookDraft) => ({
            isbn: bookDraft.isbn,
            title: bookDraft.title,
            author: bookDraft.author,
            publisher: bookDraft.publisher,
            location: bookDraft.location,
            stockQuantity: Number(bookDraft.stockQuantity),
            description: bookDraft.description,
            thumbnailUrl: bookDraft.thumbnailUrl
        }));
    }

    function createBatchRequestBody() {
        return {
            bookCreateEntries: createBookCreateEntries()
        };
    }

    async function postBooks(requestBody) {
        return apiPost("/api/books", requestBody);
    }

    function setActionButtonsDisabled(disabled) {
        if (saveBookBatchButton) {
            saveBookBatchButton.disabled = disabled;
        }

        if (clearBookDraftButton) {
            clearBookDraftButton.disabled = disabled;
        }

        if (addBookDraftButton) {
            addBookDraftButton.disabled = disabled;
        }

        if (lookupBookByIsbnButton) {
            lookupBookByIsbnButton.disabled = disabled;
        }
    }

    async function executeSaveBookBatch() {
        const requestBody = createBatchRequestBody();

        try {
            setActionButtonsDisabled(true);

            await postBooks(requestBody);

            bookDrafts.splice(0, bookDrafts.length);
            renderDraftList();

            openCustomAlert({
                title: "도서 등록 완료",
                message: "도서 등록이 완료되었습니다.",
                confirmText: "확인",
                onConfirm: () => {
                    window.location.reload();
                }
            });
        } catch (error) {
            console.error(error);

            openCustomAlert({
                title: "도서 등록 실패",
                message: error?.message || "도서 등록 중 오류가 발생했습니다."
            });

            setActionButtonsDisabled(false);
            updateDraftSummary();
        }
    }

    function saveBookBatch() {
        if (bookDrafts.length === 0) {
            openCustomAlert({
                title: "저장할 도서 없음",
                message: "저장할 도서가 없습니다."
            });
            return;
        }

        openCustomConfirm({
            title: "도서 일괄 등록",
            message: "등록 예정 도서를 저장하시겠습니까?",
            confirmText: "저장",
            cancelText: "취소",
            onConfirm: executeSaveBookBatch
        });
    }

    lookupBookByIsbnButton?.addEventListener("click", lookupBookByIsbn);
    addBookDraftButton?.addEventListener("click", addBookDraft);
    clearBookDraftButton?.addEventListener("click", clearBookDrafts);
    saveBookBatchButton?.addEventListener("click", saveBookBatch);

    bookDraftRows?.addEventListener("click", (event) => {
        const removeButton = event.target.closest('[data-role="remove-book-draft"]');

        if (!removeButton) {
            return;
        }

        const row = removeButton.closest(".book-draft-row");

        if (!row) {
            return;
        }

        removeBookDraft(row.dataset.bookDraftId);
    });

    bookDraftForm?.addEventListener("submit", (event) => {
        event.preventDefault();
        addBookDraft();
    });

    renderDraftList();
});