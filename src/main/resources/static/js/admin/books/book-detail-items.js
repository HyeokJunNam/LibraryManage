const itemsCard = document.querySelector("#bookDetailItemsCard");
const editButton = document.querySelector("#editBookItemsButton");
const editActions = document.querySelector("#bookItemEditActions");
const cancelButton = document.querySelector("#cancelBookItemsButton");
const saveButton = document.querySelector("#saveBookItemsButton");

const rowsContainer = document.querySelector("#bookDetailItemRows");
const emptyArea = document.querySelector("#bookDetailItemsEmpty");
const tableBlock = document.querySelector("#bookDetailItemsTableBlock");
const rowTemplate = document.querySelector("#bookDetailItemRowTemplate");
const addRowButton = document.querySelector("#addBookItemRowButton");

const STATUS_SELECT_CLASS_PREFIX = "book-detail-item-status-edit--";

let tempRowSequence = 1;

function getRows() {
    if (!rowsContainer) {
        return [];
    }

    return [...rowsContainer.querySelectorAll(".book-detail-items-table__row")];
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

function syncAllStatusSelectColors() {
    document.querySelectorAll(".book-detail-item-status-edit").forEach(select => {
        syncStatusSelectColor(select);
    });
}

function setEditMode(enabled) {
    if (!itemsCard) {
        return;
    }

    itemsCard.classList.toggle("is-editing", enabled);

    editButton?.classList.toggle("is-hidden", enabled);
    editActions?.classList.toggle("is-hidden", !enabled);

    if (enabled) {
        emptyArea?.classList.add("is-hidden");
        tableBlock?.classList.remove("is-hidden");
        syncAllStatusSelectColors();
    } else {
        removeCreatedRows();
        resetRowsToOriginal();
        refreshRowIndexes();
        refreshEmptyState();
        syncAllStatusSelectColors();
    }

    getRows().forEach(row => {
        row.querySelectorAll(".book-detail-item-control").forEach(control => {
            control.disabled = !enabled || isBorrowed(row) || row.dataset.rowMode === "deleted";
        });
    });
}

function removeCreatedRows() {
    getRows()
        .filter(row => row.dataset.rowMode === "created")
        .forEach(row => row.remove());
}

function resetRowsToOriginal() {
    getRows().forEach(row => {
        const statusControl = row.querySelector('[data-field="status"]');
        const locationControl = row.querySelector('[data-field="location"]');

        if (statusControl) {
            statusControl.value = row.dataset.originalStatus || "AVAILABLE";
            syncStatusSelectColor(statusControl);
        }

        if (locationControl) {
            locationControl.value = row.dataset.originalLocation || "";
        }

        row.dataset.rowMode = "clean";
        delete row.dataset.beforeDeleteMode;

        row.classList.remove(
            "book-detail-items-table__row--dirty",
            "book-detail-items-table__row--deleted"
        );
    });
}

function refreshRowIndexes() {
    getRows().forEach((row, index) => {
        const indexElement = row.querySelector(".book-detail-item-row__index");

        if (indexElement) {
            indexElement.textContent = String(index + 1);
        }
    });
}

function refreshEmptyState() {
    const hasRows = getRows().length > 0;

    emptyArea?.classList.toggle("is-hidden", hasRows);
    tableBlock?.classList.toggle("is-hidden", !hasRows);
}

function updateRowDirtyState(row) {
    if (isBorrowed(row)) {
        return;
    }

    if (row.dataset.rowMode === "created" || row.dataset.rowMode === "deleted") {
        return;
    }

    const currentStatus = getRowStatus(row);
    const currentLocation = getRowLocation(row);

    const originalStatus = row.dataset.originalStatus || "";
    const originalLocation = row.dataset.originalLocation || "";

    const dirty = currentStatus !== originalStatus || currentLocation !== originalLocation;

    row.dataset.rowMode = dirty ? "updated" : "clean";
    row.classList.toggle("book-detail-items-table__row--dirty", dirty);
}

function addNewRow() {
    if (!rowTemplate || !rowsContainer) {
        return;
    }

    emptyArea?.classList.add("is-hidden");
    tableBlock?.classList.remove("is-hidden");

    const fragment = rowTemplate.content.cloneNode(true);
    const row = fragment.querySelector(".book-detail-items-table__row");

    if (!row) {
        return;
    }

    row.dataset.tempId = `temp-${tempRowSequence++}`;
    row.dataset.rowMode = "created";
    row.dataset.borrowed = "false";

    const statusSelect = row.querySelector('[data-field="status"]');
    syncStatusSelectColor(statusSelect);

    rowsContainer.appendChild(fragment);
    refreshRowIndexes();

    const locationInput = row.querySelector('[data-field="location"]');
    locationInput?.focus();
}

function markRowDeleted(row) {
    if (!row || isBorrowed(row)) {
        return;
    }

    if (row.dataset.rowMode === "created") {
        row.remove();
        refreshRowIndexes();
        refreshEmptyState();
        return;
    }

    row.dataset.beforeDeleteMode = row.dataset.rowMode || "clean";
    row.dataset.rowMode = "deleted";
    row.classList.remove("book-detail-items-table__row--dirty");
    row.classList.add("book-detail-items-table__row--deleted");

    row.querySelectorAll(".book-detail-item-control").forEach(control => {
        control.disabled = true;
    });
}

function cancelDeleteRow(row) {
    if (!row || row.dataset.rowMode !== "deleted") {
        return;
    }

    const previousMode = row.dataset.beforeDeleteMode || "clean";

    row.dataset.rowMode = previousMode === "updated" ? "updated" : "clean";
    delete row.dataset.beforeDeleteMode;

    row.classList.remove("book-detail-items-table__row--deleted");

    if (row.dataset.rowMode === "updated") {
        row.classList.add("book-detail-items-table__row--dirty");
    }

    row.querySelectorAll(".book-detail-item-control").forEach(control => {
        control.disabled = isBorrowed(row);
    });

    updateRowDirtyState(row);
}

function collectCreateItems() {
    return getRows()
        .filter(row => row.dataset.rowMode === "created")
        .map(row => ({
            status: getRowStatus(row),
            location: getRowLocation(row)
        }));
}

function collectUpdateItems() {
    return getRows()
        .filter(row => row.dataset.rowMode === "updated")
        .map(row => ({
            bookItemId: Number(row.dataset.bookItemId),
            status: getRowStatus(row),
            location: getRowLocation(row)
        }));
}

function collectDeleteItemIds() {
    return getRows()
        .filter(row => row.dataset.rowMode === "deleted")
        .map(row => Number(row.dataset.bookItemId));
}

function validateChangedItems(createItems, updateItems, deleteItemIds) {
    if (createItems.length === 0 && updateItems.length === 0 && deleteItemIds.length === 0) {
        return {
            valid: false,
            message: "저장할 추가/수정/삭제 내용이 없습니다."
        };
    }

    const invalidCreateStatusItem = createItems.find(item => {
        return !item.status;
    });

    if (invalidCreateStatusItem) {
        return {
            valid: false,
            message: "추가할 재고의 도서 상태를 선택해 주세요."
        };
    }

    const invalidUpdateStatusItem = updateItems.find(item => {
        return !item.status;
    });

    if (invalidUpdateStatusItem) {
        return {
            valid: false,
            message: "수정할 재고의 도서 상태를 선택해 주세요."
        };
    }

    const invalidUpdateItem = updateItems.find(item => {
        return !Number.isFinite(item.bookItemId) || item.bookItemId <= 0;
    });

    if (invalidUpdateItem) {
        return {
            valid: false,
            message: "수정할 재고 ID를 찾을 수 없습니다."
        };
    }

    const invalidDeleteItemId = deleteItemIds.find(bookItemId => {
        return !Number.isFinite(bookItemId) || bookItemId <= 0;
    });

    if (invalidDeleteItemId) {
        return {
            valid: false,
            message: "삭제할 재고 ID를 찾을 수 없습니다."
        };
    }

    const emptyLocationItem = [...createItems, ...updateItems].find(item => {
        return item.location.length === 0;
    });

    if (emptyLocationItem) {
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

async function saveBookItems() {
    const bookId = itemsCard?.dataset.bookId;

    if (!bookId) {
        await showAlert("도서 ID를 찾을 수 없습니다.");
        return;
    }

    const createItems = collectCreateItems();
    const updateItems = collectUpdateItems();
    const deleteItemIds = collectDeleteItemIds();
    const validation = validateChangedItems(createItems, updateItems, deleteItemIds);

    if (!validation.valid) {
        await showAlert(validation.message);
        return;
    }

    const confirmed = await showConfirm("저장 하시겠습니까?");

    if (!confirmed) {
        return;
    }

    const payload = {
        createItems,
        updateItems,
        deleteItemIds
    };

    setSaving(true);

    try {
        const response = await fetch(`/api/books/${bookId}/items/batch`, {
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
        window.location.reload();
    } catch (error) {
        console.error(error);
        await showAlert(error.message || "재고 저장에 실패했습니다.");
    } finally {
        setSaving(false);
    }
}

function setSaving(saving) {
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
        return Promise.resolve(window.confirm(message));
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

editButton?.addEventListener("click", () => {
    setEditMode(true);
});

cancelButton?.addEventListener("click", () => {
    setEditMode(false);
});

saveButton?.addEventListener("click", saveBookItems);

addRowButton?.addEventListener("click", addNewRow);

rowsContainer?.addEventListener("input", event => {
    const control = event.target.closest(".book-detail-item-control");

    if (!control) {
        return;
    }

    const row = control.closest(".book-detail-items-table__row");

    if (!row) {
        return;
    }

    updateRowDirtyState(row);
});

rowsContainer?.addEventListener("change", event => {
    const control = event.target.closest(".book-detail-item-control");

    if (!control) {
        return;
    }

    const row = control.closest(".book-detail-items-table__row");

    if (!row) {
        return;
    }

    if (control.dataset.field === "status") {
        syncStatusSelectColor(control);
    }

    updateRowDirtyState(row);
});

rowsContainer?.addEventListener("click", event => {
    const removeCreatedButton = event.target.closest('[data-role="remove-created-row"]');

    if (removeCreatedButton) {
        const row = removeCreatedButton.closest(".book-detail-items-table__row");

        if (row && row.dataset.rowMode === "created") {
            row.remove();
            refreshRowIndexes();
            refreshEmptyState();
        }

        return;
    }

    const markDeleteButton = event.target.closest('[data-role="mark-delete-row"]');

    if (markDeleteButton) {
        const row = markDeleteButton.closest(".book-detail-items-table__row");
        markRowDeleted(row);
        return;
    }

    const cancelDeleteButton = event.target.closest('[data-role="cancel-delete-row"]');

    if (cancelDeleteButton) {
        const row = cancelDeleteButton.closest(".book-detail-items-table__row");
        cancelDeleteRow(row);
    }
});

syncAllStatusSelectColors();
setEditMode(false);