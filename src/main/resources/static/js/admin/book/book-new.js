document.addEventListener("DOMContentLoaded", () => {
    initBookCreatePage();
});

const bookDrafts = [];
const DRAFT_PAGE_SIZE = 5;

let draftSequence = 1;
let currentDraftPage = 1;

function initBookCreatePage() {
    bindBookCreateEvents();
    refreshBookDraftView();
}

function bindBookCreateEvents() {
    getElement("lookupBookByIsbnButton")?.addEventListener("click", lookupBookByIsbn);
    getElement("addBookDraftButton")?.addEventListener("click", addBookDraft);
    getElement("clearBookDraftButton")?.addEventListener("click", openClearDraftsConfirmModal);
    getElement("saveBookBatchButton")?.addEventListener("click", openSaveBooksConfirmModal);
    getElement("bookDraftRows")?.addEventListener("click", handleDraftRowClick);
    getElement("thumbnailUrl")?.addEventListener("input", updateBookImagePreview);
    getElement("isbnLookupKeyword")?.addEventListener("keydown", handleIsbnLookupKeydown);
}

function handleIsbnLookupKeydown(event) {
    if (event.key !== "Enter") {
        return;
    }

    event.preventDefault();
    lookupBookByIsbn();
}

async function lookupBookByIsbn() {
    const isbn = getValue("isbnLookupKeyword");

    if (!isbn) {
        openMessageModal("ISBN 조회", "조회할 ISBN을 입력해 주세요.");
        focusElement("isbnLookupKeyword");
        return;
    }

    const lookupButton = getElement("lookupBookByIsbnButton");
    const originalText = lookupButton?.textContent;

    try {
        setButtonLoading(lookupButton, "조회 중...");

        const page = getElement("bookCreatePage");
        const lookupUrl = page?.dataset.isbnLookupUrl || "/api/books/lookup";
        const response = await apiGet(`${lookupUrl}?isbn=${encodeURIComponent(isbn)}`);

        fillBookForm(response?.result);
        setValue("isbnLookupKeyword", "");

        openMessageModal("ISBN 조회 완료", "도서 정보를 입력 폼에 반영했습니다.");
    } catch (error) {
        openMessageModal(
            "ISBN 조회 실패",
            error?.message || "도서 정보를 불러오지 못했습니다."
        );
    } finally {
        restoreButton(lookupButton, originalText);
    }
}

function fillBookForm(book) {
    if (!book) {
        openMessageModal("ISBN 조회 실패", "조회된 도서 정보가 없습니다.");
        return;
    }

    setValue("isbn", pickValue(book.isbn, ""));
    setValue("title", pickValue(book.title, ""));
    setValue("author", pickValue(book.author, ""));
    setValue("publisher", pickValue(book.publisher, ""));
    setValue("thumbnailUrl", pickValue(book.thumbnailUrl, ""));
    setValue("description", pickValue(book.description, ""));

    updateBookImagePreview();
}

function addBookDraft() {
    const draft = readBookDraftForm();
    const invalidMessage = validateBookDraft(draft);

    if (invalidMessage) {
        openMessageModal("입력 확인", invalidMessage.message);
        focusElement(invalidMessage.targetId);
        return;
    }

    if (hasDuplicateIsbn(draft.isbn)) {
        openMessageModal("입력 확인", "이미 등록 예정 목록에 추가된 ISBN입니다.");
        focusElement("isbn");
        return;
    }

    bookDrafts.push({
        id: draftSequence++,
        ...draft
    });

    currentDraftPage = Math.max(1, getTotalDraftPages());

    clearBookDraftForm();
    refreshBookDraftView();
}

function readBookDraftForm() {
    return {
        isbn: getValue("isbn"),
        title: getValue("title"),
        author: getValue("author"),
        publisher: getValue("publisher"),
        description: getValue("description"),
        thumbnailUrl: getValue("thumbnailUrl"),
        location: getValue("location")
    };
}

function validateBookDraft(draft) {
    if (!draft.isbn) {
        return { targetId: "isbn", message: "ISBN을 입력해 주세요." };
    }

    if (!draft.title) {
        return { targetId: "title", message: "도서명을 입력해 주세요." };
    }

    if (!draft.author) {
        return { targetId: "author", message: "저자를 입력해 주세요." };
    }

    if (!draft.publisher) {
        return { targetId: "publisher", message: "출판사를 입력해 주세요." };
    }

    if (!draft.location) {
        return { targetId: "location", message: "위치를 입력해 주세요." };
    }

    return null;
}

function hasDuplicateIsbn(isbn) {
    return bookDrafts.some((draft) => draft.isbn === isbn);
}

function handleDraftRowClick(event) {
    const removeButton = event.target.closest("[data-role='remove-book-draft']");

    if (!removeButton) {
        return;
    }

    const row = removeButton.closest(".book-draft-row");
    const draftId = Number(row?.dataset.draftId);

    removeBookDraft(draftId);
}

function removeBookDraft(draftId) {
    const index = bookDrafts.findIndex((draft) => draft.id === draftId);

    if (index < 0) {
        return;
    }

    bookDrafts.splice(index, 1);
    normalizeCurrentDraftPage();
    refreshBookDraftView();
}

function openClearDraftsConfirmModal() {
    if (bookDrafts.length === 0) {
        return;
    }

    openAlertModal({
        title: "등록 예정 목록 비우기",
        message: "등록 예정 도서를 모두 비우시겠습니까?",
        confirmText: "비우기",
        cancelText: "취소",
        onConfirm: clearBookDrafts
    });
}

function clearBookDrafts() {
    bookDrafts.splice(0, bookDrafts.length);
    currentDraftPage = 1;
    refreshBookDraftView();
}

function openSaveBooksConfirmModal() {
    if (bookDrafts.length === 0) {
        openMessageModal("도서 등록", "등록할 도서가 없습니다.");
        return;
    }

    openAlertModal({
        title: "도서 등록",
        message: `도서 ${bookDrafts.length}권을 등록하시겠습니까?`,
        confirmText: "저장",
        cancelText: "취소",
        onConfirm: saveBookBatch
    });
}

async function saveBookBatch() {
    const saveButton = getElement("saveBookBatchButton");
    const originalText = saveButton?.textContent;

    try {
        setButtonLoading(saveButton, "저장 중...");

        const page = getElement("bookCreatePage");
        const createUrl = page?.dataset.createUrl || "/api/books";

        await apiPost(createUrl, {
            items: bookDrafts.map(toBookCreateRequest)
        });

        openAlertModal({
            title: "등록 완료",
            message: "도서 등록이 완료되었습니다.",
            confirmText: "확인",
            onConfirm: () => {
                window.location.href = "/admin/books";
            }
        });
    } catch (error) {
        openMessageModal(
            "등록 실패",
            error?.message || "도서 등록 중 오류가 발생했습니다."
        );
    } finally {
        restoreButton(saveButton, originalText);
    }
}

function toBookCreateRequest(draft) {
    return {
        isbn: draft.isbn,
        title: draft.title,
        author: draft.author,
        publisher: draft.publisher,
        description: draft.description,
        thumbnailUrl: draft.thumbnailUrl,
        location: draft.location
    };
}

function refreshBookDraftView() {
    normalizeCurrentDraftPage();
    renderBookDraftRows();

    const hasDrafts = bookDrafts.length > 0;

    getElement("bookDraftEmpty")?.classList.toggle("is-hidden", hasDrafts);
    getElement("bookDraftTableWrap")?.classList.toggle("is-hidden", !hasDrafts);
    getElement("bookDraftPagination")?.classList.toggle("is-hidden", !hasDrafts);

    setDisabled("clearBookDraftButton", !hasDrafts);
    setDisabled("saveBookBatchButton", !hasDrafts);

    renderDraftPagination();
}

function renderBookDraftRows() {
    const rowsContainer = getElement("bookDraftRows");
    const template = getElement("bookDraftRowTemplate");

    if (!rowsContainer || !template) {
        return;
    }

    rowsContainer.innerHTML = "";

    getCurrentDraftPageItems().forEach((draft, index) => {
        const fragment = template.content.cloneNode(true);
        const row = fragment.querySelector(".book-draft-row");
        const globalIndex = getCurrentDraftStartIndex() + index + 1;

        row.dataset.draftId = String(draft.id);

        setRowText(row, "index", String(globalIndex));
        setRowText(row, "title", draft.title);
        setRowText(row, "isbn", draft.isbn);
        setRowText(row, "author", draft.author);
        setRowText(row, "publisher", draft.publisher);
        setRowText(row, "location", draft.location);

        rowsContainer.appendChild(fragment);
    });
}

function renderDraftPagination() {
    const paginationRoot = getElement("bookDraftPagination");

    if (!paginationRoot || !window.TableLayout?.renderClientPagination) {
        return;
    }

    TableLayout.renderClientPagination(paginationRoot, {
        currentPage: Math.max(0, currentDraftPage - 1),
        totalPages: getTotalDraftPages(),
        pageSize: DRAFT_PAGE_SIZE,
        onPageChange: (nextPage) => {
            currentDraftPage = nextPage + 1;
            renderBookDraftRows();
            renderDraftPagination();
        }
    });
}

function getCurrentDraftPageItems() {
    const startIndex = getCurrentDraftStartIndex();
    return bookDrafts.slice(startIndex, startIndex + DRAFT_PAGE_SIZE);
}

function getCurrentDraftStartIndex() {
    return (currentDraftPage - 1) * DRAFT_PAGE_SIZE;
}

function getTotalDraftPages() {
    if (bookDrafts.length === 0) {
        return 0;
    }

    return Math.ceil(bookDrafts.length / DRAFT_PAGE_SIZE);
}

function normalizeCurrentDraftPage() {
    const totalPages = getTotalDraftPages();

    if (totalPages === 0) {
        currentDraftPage = 1;
        return;
    }

    if (currentDraftPage < 1) {
        currentDraftPage = 1;
        return;
    }

    if (currentDraftPage > totalPages) {
        currentDraftPage = totalPages;
    }
}

function setRowText(row, field, value) {
    const element = row.querySelector(`[data-field="${field}"]`);

    if (element) {
        element.textContent = value || "-";
    }
}

function clearBookDraftForm() {
    const form = getElement("bookDraftForm");

    if (form) {
        form.reset();
    }

    clearBookImagePreview();
}

function updateBookImagePreview() {
    const preview = getElement("bookImagePreview");
    const thumbnailUrl = getValue("thumbnailUrl");

    if (!preview) {
        return;
    }

    if (!thumbnailUrl) {
        clearBookImagePreview();
        return;
    }

    preview.innerHTML = `
        <img
            src="${escapeAttribute(thumbnailUrl)}"
            alt="도서 이미지"
            class="book-image-preview__image"
        >
    `;
}

function clearBookImagePreview() {
    const preview = getElement("bookImagePreview");

    if (!preview) {
        return;
    }

    preview.innerHTML = `
        <div class="book-image-preview__dummy">
            <span class="book-image-preview__mark">BOOK</span>
            <span class="book-image-preview__text">도서 이미지</span>
        </div>
    `;
}

function openMessageModal(title, message) {
    openAlertModal({
        title,
        message,
        confirmText: "확인"
    });
}

function getElement(id) {
    return document.getElementById(id);
}

function getValue(id) {
    return getElement(id)?.value?.trim() || "";
}

function setValue(id, value) {
    const element = getElement(id);

    if (element) {
        element.value = value || "";
    }
}

function focusElement(id) {
    getElement(id)?.focus();
}

function setDisabled(id, disabled) {
    const element = getElement(id);

    if (element) {
        element.disabled = disabled;
    }
}

function setButtonLoading(button, text) {
    if (!button) {
        return;
    }

    button.disabled = true;
    button.textContent = text;
}

function restoreButton(button, text) {
    if (!button) {
        return;
    }

    button.disabled = false;

    if (text) {
        button.textContent = text;
    }
}

function pickValue(...values) {
    for (const value of values) {
        if (value !== null && value !== undefined && String(value).trim() !== "") {
            return String(value).trim();
        }
    }

    return "";
}

function escapeAttribute(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll('"', "&quot;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;");
}