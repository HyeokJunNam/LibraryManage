const STATUS_SELECT_CLASS_PREFIX = "book-detail-copy-status-edit--";
const DEFAULT_COPY_PAGE_SIZE = 5;

document.addEventListener("DOMContentLoaded", () => {
    initBookCopiesArea();
});

function initBookCopiesArea() {
    const bookCopiesArea = document.getElementById("bookDetailCopiesArea");

    if (!bookCopiesArea || bookCopiesArea.dataset.initialized === "true") {
        return;
    }

    bookCopiesArea.dataset.initialized = "true";

    const copiesUrl = bookCopiesArea.dataset.copiesUrl;

    const editState = {
        editing: false,
        createdCopies: [],
        updatedCopies: new Map(),
        deletedCopyIds: new Set(),
        tempSequence: 1
    };

    function getCopiesCard() {
        return bookCopiesArea.querySelector("#bookDetailCopiesCard");
    }

    function getRowsContainer() {
        return bookCopiesArea.querySelector("#bookDetailCopyRows");
    }

    function getPaginationArea() {
        return bookCopiesArea.querySelector(".table-block__pagination");
    }

    function getCurrentPageSize() {
        const pagination = bookCopiesArea.querySelector("[data-page-size]");
        const pageSize = Number(pagination?.dataset.pageSize);

        if (!Number.isFinite(pageSize) || pageSize <= 0) {
            return DEFAULT_COPY_PAGE_SIZE;
        }

        return pageSize;
    }

    function getCurrentPage() {
        const activePage = bookCopiesArea.querySelector("[data-page].is-active, [data-page].pagination__number--active");
        const page = Number(activePage?.dataset.page);

        if (!Number.isFinite(page) || page < 0) {
            return 0;
        }

        return page;
    }

    function getLastPage() {
        const pagination = getPaginationArea();
        const totalPages = Number(pagination?.dataset.totalPages);

        if (Number.isFinite(totalPages) && totalPages > 0) {
            return totalPages - 1;
        }

        const pageButtons = [...bookCopiesArea.querySelectorAll("[data-page]")];
        const pages = pageButtons
            .map(button => Number(button.dataset.page))
            .filter(page => Number.isFinite(page) && page >= 0);

        if (pages.length === 0) {
            return 0;
        }

        return Math.max(...pages);
    }

    async function fetchCopies(page = 0) {
        if (!copiesUrl) {
            return "";
        }

        const url = new URL(copiesUrl, window.location.origin);

        url.searchParams.set("page", String(page));
        url.searchParams.set("size", String(getCurrentPageSize()));

        const response = await fetch(url.toString(), {
            method: "GET",
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        });

        if (!response.ok) {
            throw new Error(`재고 현황 조회 실패: ${response.status}`);
        }

        return response.text();
    }

    async function renderCopies(page = 0, options = {}) {
        const shouldCapture = options.captureBefore !== false;

        if (shouldCapture) {
            captureVisibleState();
        }

        try {
            const html = await fetchCopies(page);

            if (!html || !html.trim()) {
                return;
            }

            bookCopiesArea.innerHTML = html;

            initBookDetailCopies();
            applyEditStateToCurrentPage();
        } catch (error) {
            console.error(error);
            await showAlert("재고 현황을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.");
        }
    }

    function getRows() {
        const rowsContainer = getRowsContainer();

        if (!rowsContainer) {
            return [];
        }

        return [...rowsContainer.querySelectorAll(".book-detail-copies-table__row")];
    }

    function getPageStartIndex() {
        const rowsContainer = getRowsContainer();

        if (!rowsContainer) {
            return 0;
        }

        const pageStartIndex = Number(rowsContainer.dataset.pageStartIndex);

        if (!Number.isFinite(pageStartIndex) || pageStartIndex < 0) {
            return 0;
        }

        return pageStartIndex;
    }

    function getBookCopyId(row) {
        const bookCopyId = Number(row.dataset.bookCopyId);

        if (!Number.isFinite(bookCopyId) || bookCopyId <= 0) {
            return null;
        }

        return bookCopyId;
    }

    function isBorrowed(row) {
        return row.dataset.borrowed === "true";
    }

    function getRowStatus(row) {
        return row.querySelector('[data-field="status"]')?.value || "";
    }

    function getRowLocation(row) {
        return row.querySelector('[data-field="location"]')?.value.trim() || "";
    }

    function getOriginalStatus(row) {
        return row.dataset.originalStatus || "";
    }

    function getOriginalLocation(row) {
        return row.dataset.originalLocation || "";
    }

    function isRowChanged(row) {
        return getRowStatus(row) !== getOriginalStatus(row)
            || getRowLocation(row) !== getOriginalLocation(row);
    }

    function syncStatusSelectColor(select) {
        if (!select) {
            return;
        }

        [...select.classList].forEach(className => {
            if (className.startsWith(STATUS_SELECT_CLASS_PREFIX)) {
                select.classList.remove(className);
            }
        });

        const status = select.value;

        if (!status) {
            return;
        }

        select.classList.add(`${STATUS_SELECT_CLASS_PREFIX}${status.toLowerCase()}`);
    }

    function syncAllStatusSelectColors(root = bookCopiesArea) {
        root.querySelectorAll(".book-detail-copy-status-edit").forEach(select => {
            syncStatusSelectColor(select);
        });
    }

    function captureVisibleState() {
        if (!editState.editing) {
            return;
        }

        getRows().forEach(row => {
            const rowMode = row.dataset.rowMode;

            if (rowMode === "created") {
                syncCreatedStateFromRow(row);
                return;
            }

            const bookCopyId = getBookCopyId(row);

            if (!bookCopyId) {
                return;
            }

            if (rowMode === "deleted") {
                editState.deletedCopyIds.add(bookCopyId);
                editState.updatedCopies.delete(bookCopyId);
                return;
            }

            editState.deletedCopyIds.delete(bookCopyId);

            if (isRowChanged(row)) {
                editState.updatedCopies.set(bookCopyId, {
                    bookCopyId,
                    status: getRowStatus(row),
                    location: getRowLocation(row)
                });
            } else {
                editState.updatedCopies.delete(bookCopyId);
            }
        });
    }

    function syncCreatedStateFromRow(row) {
        const tempId = row.dataset.tempId;

        if (!tempId) {
            return;
        }

        const existing = editState.createdCopies.find(copy => copy.tempId === tempId);

        if (!existing) {
            editState.createdCopies.push({
                tempId,
                status: getRowStatus(row),
                location: getRowLocation(row)
            });

            return;
        }

        existing.status = getRowStatus(row);
        existing.location = getRowLocation(row);
    }

    function removeCreatedState(tempId) {
        if (!tempId) {
            return;
        }

        editState.createdCopies = editState.createdCopies.filter(copy => copy.tempId !== tempId);
    }

    function clearEditState() {
        editState.editing = false;
        editState.createdCopies = [];
        editState.updatedCopies.clear();
        editState.deletedCopyIds.clear();
        editState.tempSequence = 1;
    }

    function applyEditStateToCurrentPage() {
        const copiesCard = getCopiesCard();

        if (!copiesCard) {
            return;
        }

        setEditMode(editState.editing, {
            resetState: false,
            captureBefore: false
        });

        if (!editState.editing) {
            return;
        }

        getRows().forEach(row => {
            if (row.dataset.rowMode === "created") {
                return;
            }

            const bookCopyId = getBookCopyId(row);

            if (!bookCopyId) {
                return;
            }

            if (editState.deletedCopyIds.has(bookCopyId)) {
                applyDeletedVisualState(row);
                return;
            }

            const updatedCopy = editState.updatedCopies.get(bookCopyId);

            if (updatedCopy) {
                applyUpdatedVisualState(row, updatedCopy);
            }
        });

        if (getCurrentPage() === getLastPage()) {
            renderCreatedRows();
        }

        refreshRowIndexes();
        refreshEmptyState();
        syncAllStatusSelectColors(copiesCard);
    }

    function applyUpdatedVisualState(row, updatedCopy) {
        const statusControl = row.querySelector('[data-field="status"]');
        const locationControl = row.querySelector('[data-field="location"]');

        if (statusControl) {
            statusControl.value = updatedCopy.status || "";
            syncStatusSelectColor(statusControl);
        }

        if (locationControl) {
            locationControl.value = updatedCopy.location || "";
        }

        row.dataset.rowMode = "updated";
        row.classList.add("book-detail-copies-table__row--dirty");
        row.classList.remove("book-detail-copies-table__row--deleted");
    }

    function applyDeletedVisualState(row) {
        row.dataset.beforeDeleteMode = row.dataset.rowMode || "clean";
        row.dataset.rowMode = "deleted";

        row.classList.remove("book-detail-copies-table__row--dirty");
        row.classList.add("book-detail-copies-table__row--deleted");

        row.querySelectorAll(".book-detail-copy-control").forEach(control => {
            control.disabled = true;
        });
    }

    function renderCreatedRows() {
        const copiesCard = getCopiesCard();
        const rowsContainer = getRowsContainer();
        const rowTemplate = copiesCard?.querySelector("#bookDetailCopyRowTemplate");

        if (!copiesCard || !rowsContainer || !rowTemplate) {
            return;
        }

        rowsContainer
            .querySelectorAll('.book-detail-copies-table__row[data-row-mode="created"]')
            .forEach(row => row.remove());

        editState.createdCopies.forEach(createdCopy => {
            const row = createRowFromTemplate(rowTemplate, createdCopy);

            if (row) {
                rowsContainer.appendChild(row);
            }
        });
    }

    function createRowFromTemplate(rowTemplate, createdCopy) {
        const fragment = rowTemplate.content.cloneNode(true);
        const row = fragment.querySelector(".book-detail-copies-table__row");

        if (!row) {
            return null;
        }

        row.dataset.tempId = createdCopy.tempId;
        row.dataset.rowMode = "created";
        row.dataset.borrowed = "false";

        const indexElement = row.querySelector(".book-detail-copy-row__index");

        if (indexElement) {
            indexElement.textContent = "신규";
        }

        const statusControl = row.querySelector('[data-field="status"]');

        if (statusControl && createdCopy.status) {
            statusControl.value = createdCopy.status;
        }

        syncStatusSelectColor(statusControl);

        const locationControl = row.querySelector('[data-field="location"]');

        if (locationControl) {
            locationControl.value = createdCopy.location || "";
        }

        return row;
    }

    function initBookDetailCopies() {
        const copiesCard = getCopiesCard();

        if (!copiesCard || copiesCard.dataset.editorInitialized === "true") {
            return;
        }

        copiesCard.dataset.editorInitialized = "true";

        const editButton = copiesCard.querySelector("#editBookCopiesButton");
        const cancelButton = copiesCard.querySelector("#cancelBookCopiesButton");
        const saveButton = copiesCard.querySelector("#saveBookCopiesButton");
        const addRowButton = copiesCard.querySelector("#addBookCopyRowButton");
        const rowsContainer = copiesCard.querySelector("#bookDetailCopyRows");

        editButton?.addEventListener("click", () => {
            setEditMode(true);
        });

        cancelButton?.addEventListener("click", async () => {
            const confirmed = await showConfirm("저장하지 않은 변경사항을 취소하시겠습니까?");

            if (!confirmed) {
                return;
            }

            const currentPage = getCurrentPage();
            clearEditState();
            await renderCopies(currentPage, {
                captureBefore: false
            });
        });

        saveButton?.addEventListener("click", saveBookCopies);

        addRowButton?.addEventListener("click", async () => {
            await addNewRowAtLastPage();
        });

        rowsContainer?.addEventListener("input", event => {
            const control = event.target.closest(".book-detail-copy-control");

            if (!control) {
                return;
            }

            const row = control.closest(".book-detail-copies-table__row");

            if (!row) {
                return;
            }

            updateRowDirtyState(row);
            syncStateFromRow(row);
        });

        rowsContainer?.addEventListener("change", event => {
            const control = event.target.closest(".book-detail-copy-control");

            if (!control) {
                return;
            }

            const row = control.closest(".book-detail-copies-table__row");

            if (!row) {
                return;
            }

            if (control.dataset.field === "status") {
                syncStatusSelectColor(control);
            }

            updateRowDirtyState(row);
            syncStateFromRow(row);
        });

        rowsContainer?.addEventListener("click", event => {
            const removeCreatedButton = event.target.closest('[data-role="remove-created-row"]');

            if (removeCreatedButton) {
                const row = removeCreatedButton.closest(".book-detail-copies-table__row");

                if (row && row.dataset.rowMode === "created") {
                    removeCreatedState(row.dataset.tempId);
                    row.remove();
                    refreshRowIndexes();
                    refreshEmptyState();
                }

                return;
            }

            const markDeleteButton = event.target.closest('[data-role="mark-delete-row"]');

            if (markDeleteButton) {
                const row = markDeleteButton.closest(".book-detail-copies-table__row");
                markRowDeleted(row);
                return;
            }

            const cancelDeleteButton = event.target.closest('[data-role="cancel-delete-row"]');

            if (cancelDeleteButton) {
                const row = cancelDeleteButton.closest(".book-detail-copies-table__row");
                cancelDeleteRow(row);
            }
        });

        syncAllStatusSelectColors(copiesCard);
        setEditMode(editState.editing, {
            resetState: false,
            captureBefore: false
        });
    }

    function syncStateFromRow(row) {
        if (!editState.editing || !row) {
            return;
        }

        if (row.dataset.rowMode === "created") {
            syncCreatedStateFromRow(row);
            return;
        }

        const bookCopyId = getBookCopyId(row);

        if (!bookCopyId) {
            return;
        }

        if (row.dataset.rowMode === "deleted") {
            editState.deletedCopyIds.add(bookCopyId);
            editState.updatedCopies.delete(bookCopyId);
            return;
        }

        editState.deletedCopyIds.delete(bookCopyId);

        if (isRowChanged(row)) {
            editState.updatedCopies.set(bookCopyId, {
                bookCopyId,
                status: getRowStatus(row),
                location: getRowLocation(row)
            });
        } else {
            editState.updatedCopies.delete(bookCopyId);
        }
    }

    function setEditMode(enabled, options = {}) {
        const copiesCard = getCopiesCard();

        if (!copiesCard) {
            return;
        }

        const editButton = copiesCard.querySelector("#editBookCopiesButton");
        const editActions = copiesCard.querySelector("#bookCopyEditActions");
        const emptyArea = copiesCard.querySelector("#bookDetailCopiesEmpty");
        const tableBlock = copiesCard.querySelector("#bookDetailCopiesTableBlock");

        const shouldResetState = options.resetState !== false;
        const shouldCapture = options.captureBefore === true;

        if (shouldCapture) {
            captureVisibleState();
        }

        editState.editing = enabled;

        copiesCard.classList.toggle("is-editing", enabled);

        editButton?.classList.toggle("is-hidden", enabled);
        editActions?.classList.toggle("is-hidden", !enabled);

        if (enabled) {
            emptyArea?.classList.add("is-hidden");
            tableBlock?.classList.remove("is-hidden");
        } else if (shouldResetState) {
            clearEditState();
        }

        getRows().forEach(row => {
            row.querySelectorAll(".book-detail-copy-control").forEach(control => {
                control.disabled = !enabled || isBorrowed(row) || row.dataset.rowMode === "deleted";
            });
        });

        syncAllStatusSelectColors(copiesCard);
    }

    function updateRowDirtyState(row) {
        if (!row || isBorrowed(row)) {
            return;
        }

        if (row.dataset.rowMode === "created" || row.dataset.rowMode === "deleted") {
            return;
        }

        const dirty = isRowChanged(row);

        row.dataset.rowMode = dirty ? "updated" : "clean";
        row.classList.toggle("book-detail-copies-table__row--dirty", dirty);
    }

    async function addNewRowAtLastPage() {
        if (!editState.editing) {
            setEditMode(true);
        }

        captureVisibleState();

        const lastPage = getLastPage();

        if (getCurrentPage() !== lastPage) {
            await renderCopies(lastPage, {
                captureBefore: false
            });
        }

        const createdCopy = {
            tempId: `temp-${editState.tempSequence++}`,
            status: "AVAILABLE",
            location: ""
        };

        editState.createdCopies.push(createdCopy);

        renderCreatedRows();
        refreshRowIndexes();
        refreshEmptyState();

        const createdRow = getRows().find(row => row.dataset.tempId === createdCopy.tempId);
        const locationInput = createdRow?.querySelector('[data-field="location"]');

        locationInput?.focus();
    }

    function markRowDeleted(row) {
        if (!row || isBorrowed(row)) {
            return;
        }

        if (row.dataset.rowMode === "created") {
            removeCreatedState(row.dataset.tempId);
            row.remove();
            refreshRowIndexes();
            refreshEmptyState();
            return;
        }

        const bookCopyId = getBookCopyId(row);

        if (!bookCopyId) {
            return;
        }

        row.dataset.beforeDeleteMode = row.dataset.rowMode || "clean";
        row.dataset.rowMode = "deleted";

        row.classList.remove("book-detail-copies-table__row--dirty");
        row.classList.add("book-detail-copies-table__row--deleted");

        row.querySelectorAll(".book-detail-copy-control").forEach(control => {
            control.disabled = true;
        });

        editState.deletedCopyIds.add(bookCopyId);
        editState.updatedCopies.delete(bookCopyId);
    }

    function cancelDeleteRow(row) {
        if (!row || row.dataset.rowMode !== "deleted") {
            return;
        }

        const bookCopyId = getBookCopyId(row);

        if (!bookCopyId) {
            return;
        }

        editState.deletedCopyIds.delete(bookCopyId);

        const previousMode = row.dataset.beforeDeleteMode || "clean";

        row.dataset.rowMode = previousMode === "updated" ? "updated" : "clean";
        delete row.dataset.beforeDeleteMode;

        row.classList.remove("book-detail-copies-table__row--deleted");

        row.querySelectorAll(".book-detail-copy-control").forEach(control => {
            control.disabled = isBorrowed(row);
        });

        updateRowDirtyState(row);
        syncStateFromRow(row);
    }

    function refreshRowIndexes() {
        const pageStartIndex = getPageStartIndex();
        let visibleIndex = 0;

        getRows().forEach(row => {
            const indexElement = row.querySelector(".book-detail-copy-row__index");

            if (!indexElement) {
                return;
            }

            if (row.dataset.rowMode === "created") {
                indexElement.textContent = "신규";
                return;
            }

            indexElement.textContent = String(pageStartIndex + visibleIndex + 1);
            visibleIndex++;
        });
    }

    function refreshEmptyState() {
        const emptyArea = bookCopiesArea.querySelector("#bookDetailCopiesEmpty");
        const tableBlock = bookCopiesArea.querySelector("#bookDetailCopiesTableBlock");

        const hasRows = getRows().length > 0;

        emptyArea?.classList.toggle("is-hidden", hasRows);
        tableBlock?.classList.toggle("is-hidden", !hasRows);
    }

    function buildPayload() {
        captureVisibleState();

        return {
            createCopies: editState.createdCopies.map(copy => ({
                status: copy.status,
                location: copy.location
            })),
            updateCopies: [...editState.updatedCopies.values()].map(copy => ({
                bookCopyId: copy.bookCopyId,
                status: copy.status,
                location: copy.location
            })),
            deleteCopyIds: [...editState.deletedCopyIds]
        };
    }

    function validatePayload(payload) {
        const createCopies = payload.createCopies;
        const updateCopies = payload.updateCopies;
        const deleteCopyIds = payload.deleteCopyIds;

        if (createCopies.length === 0 && updateCopies.length === 0 && deleteCopyIds.length === 0) {
            return {
                valid: false,
                message: "저장할 추가/수정/삭제 내용이 없습니다."
            };
        }

        const invalidCreateStatusCopy = createCopies.find(copy => !copy.status);

        if (invalidCreateStatusCopy) {
            return {
                valid: false,
                message: "추가할 재고의 도서 상태를 선택해 주세요."
            };
        }

        const invalidUpdateStatusCopy = updateCopies.find(copy => !copy.status);

        if (invalidUpdateStatusCopy) {
            return {
                valid: false,
                message: "수정할 재고의 도서 상태를 선택해 주세요."
            };
        }

        const invalidUpdateCopy = updateCopies.find(copy => {
            return !Number.isFinite(copy.bookCopyId) || copy.bookCopyId <= 0;
        });

        if (invalidUpdateCopy) {
            return {
                valid: false,
                message: "수정할 재고 ID를 찾을 수 없습니다."
            };
        }

        const invalidDeleteCopyId = deleteCopyIds.find(bookCopyId => {
            return !Number.isFinite(bookCopyId) || bookCopyId <= 0;
        });

        if (invalidDeleteCopyId) {
            return {
                valid: false,
                message: "삭제할 재고 ID를 찾을 수 없습니다."
            };
        }

        const emptyLocationCopy = [...createCopies, ...updateCopies].find(copy => {
            return copy.location.length === 0;
        });

        if (emptyLocationCopy) {
            return {
                valid: false,
                message: "재고 위치를 입력해 주세요."
            };
        }

        return {
            valid: true,
            message: ""
        };
    }

    async function saveBookCopies() {
        const copiesCard = getCopiesCard();
        const bookId = copiesCard?.dataset.bookId;

        if (!bookId) {
            await showAlert("도서 ID를 찾을 수 없습니다.");
            return;
        }

        const payload = buildPayload();
        const validation = validatePayload(payload);

        if (!validation.valid) {
            await showAlert(validation.message);
            return;
        }

        const confirmed = await showConfirm("저장 하시겠습니까?");

        if (!confirmed) {
            return;
        }

        setSaving(true);

        try {
            const response = await fetch(`/api/books/${bookId}/copies/batch`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    ...getCsrfHeaders()
                },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const message = await readErrorMessage(response);
                throw new Error(message);
            }

            await showAlert("재고가 저장되었습니다.");

            const currentPage = getCurrentPage();

            clearEditState();

            await renderCopies(currentPage, {
                captureBefore: false
            });
        } catch (error) {
            console.error(error);
            await showAlert(error.message || "재고 저장에 실패했습니다.");
        } finally {
            setSaving(false);
        }
    }

    function setSaving(saving) {
        const copiesCard = getCopiesCard();

        if (!copiesCard) {
            return;
        }

        const saveButton = copiesCard.querySelector("#saveBookCopiesButton");
        const cancelButton = copiesCard.querySelector("#cancelBookCopiesButton");
        const addRowButton = copiesCard.querySelector("#addBookCopyRowButton");

        saveButton?.toggleAttribute("disabled", saving);
        cancelButton?.toggleAttribute("disabled", saving);
        addRowButton?.toggleAttribute("disabled", saving);

        if (saveButton) {
            saveButton.textContent = saving ? "저장 중..." : "저장";
        }
    }

    async function readErrorMessage(response) {
        const contentType = response.headers.get("content-type") || "";

        try {
            if (contentType.includes("application/json")) {
                const body = await response.json();

                return body.message
                    || body.error
                    || body.result?.message
                    || body.data?.message
                    || `재고 저장에 실패했습니다. (${response.status})`;
            }

            const text = await response.text();

            return text || `재고 저장에 실패했습니다. (${response.status})`;
        } catch {
            return `재고 저장에 실패했습니다. (${response.status})`;
        }
    }

    bookCopiesArea.addEventListener("book-copies:page-change", async event => {
        const page = Number(event.detail?.page);

        if (!Number.isFinite(page) || page < 0) {
            return;
        }

        await renderCopies(page);
    });

    renderCopies(0, {
        captureBefore: false
    });
}

function getCsrfHeaders() {
    const token = document.querySelector('meta[name="_csrf"]')?.content;
    const header = document.querySelector('meta[name="_csrf_header"]')?.content;

    if (!token || !header) {
        return {};
    }

    return {
        [header]: token
    };
}

function showAlert(message, title = "안내") {
    return openCommonModal({
        title,
        message,
        confirmText: "확인",
        cancelText: "",
        useCancel: false
    });
}

function showConfirm(message, title = "확인") {
    return openCommonModal({
        title,
        message,
        confirmText: "확인",
        cancelText: "취소",
        useCancel: true
    });
}

function openCommonModal({ title, message, confirmText, cancelText, useCancel }) {
    const modal = document.querySelector('[data-role="alert-modal"]');
    const titleElement = document.querySelector("#alert-modal-title");
    const messageElement = document.querySelector("#alert-modal-message");
    const confirmButton = document.querySelector('[data-role="alert-modal-confirm"]');
    const cancelButton = document.querySelector('[data-role="alert-modal-cancel"]');
    const backdrop = document.querySelector('[data-role="alert-modal-backdrop"]');

    if (!modal || !titleElement || !messageElement || !confirmButton || !cancelButton) {
        if (useCancel) {
            return Promise.resolve(window.confirm(message));
        }

        window.alert(message);
        return Promise.resolve(true);
    }

    titleElement.textContent = title;
    messageElement.textContent = message;
    confirmButton.textContent = confirmText || "확인";
    cancelButton.textContent = cancelText || "취소";

    cancelButton.hidden = !useCancel;
    modal.hidden = false;
    document.body.classList.add("modal-open");

    return new Promise(resolve => {
        let resolved = false;

        function close(result) {
            if (resolved) {
                return;
            }

            resolved = true;

            modal.hidden = true;
            document.body.classList.remove("modal-open");

            confirmButton.removeEventListener("click", handleConfirm);
            cancelButton.removeEventListener("click", handleCancel);
            backdrop?.removeEventListener("click", handleBackdrop);
            document.removeEventListener("keydown", handleKeydown);

            resolve(result);
        }

        function handleConfirm() {
            close(true);
        }

        function handleCancel() {
            close(false);
        }

        function handleBackdrop() {
            close(false);
        }

        function handleKeydown(event) {
            if (event.key === "Escape") {
                close(false);
            }
        }

        confirmButton.addEventListener("click", handleConfirm);
        cancelButton.addEventListener("click", handleCancel);
        backdrop?.addEventListener("click", handleBackdrop);
        document.addEventListener("keydown", handleKeydown);

        confirmButton.focus();
    });
}