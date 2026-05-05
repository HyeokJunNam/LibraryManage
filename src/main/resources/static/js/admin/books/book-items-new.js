const page = document.getElementById('bookItemCreatePage');
const rowsContainer = document.getElementById('bookItemEditorRows');
const rowTemplate = document.getElementById('bookItemEditorRowTemplate');

const addButton = document.getElementById('addBookItemRowButton');
const saveHeaderButton = document.getElementById('saveBookItemsButton');
const saveFooterButton = document.getElementById('saveBookItemsFooterButton');

const emptyArea = document.getElementById('bookItemEditorEmpty');
const tableWrap = document.getElementById('bookItemEditorTableWrap');

const rowCountElement = document.getElementById('bookItemRowCount');

let tempRowSequence = 1;
let saving = false;

initBookItemEditor();

function initBookItemEditor() {
    bindExistingRows();
    bindEvents();
    refreshViewState();
}

function bindEvents() {
    addButton?.addEventListener('click', addNewRow);
    saveHeaderButton?.addEventListener('click', saveBookItems);
    saveFooterButton?.addEventListener('click', saveBookItems);

    rowsContainer?.addEventListener('input', handleRowChange);
    rowsContainer?.addEventListener('change', handleRowChange);
    rowsContainer?.addEventListener('click', handleRowClick);
}

function bindExistingRows() {
    getRows().forEach((row) => {
        const statusControl = row.querySelector('[data-field="status"]');
        const locationControl = row.querySelector('[data-field="location"]');

        row.dataset.rowMode = 'clean';

        if (!row.dataset.originalStatus && statusControl) {
            row.dataset.originalStatus = statusControl.value;
        }

        if (!row.dataset.originalLocation && locationControl) {
            row.dataset.originalLocation = locationControl.value.trim();
        }
    });
}

function addNewRow() {
    if (!rowTemplate || !rowsContainer) {
        return;
    }

    const fragment = rowTemplate.content.cloneNode(true);
    const row = fragment.querySelector('.book-item-editor-row');

    row.dataset.tempId = `temp-${tempRowSequence++}`;
    row.dataset.rowMode = 'created';

    rowsContainer.appendChild(fragment);

    refreshViewState();

    const locationInput = row.querySelector('[data-field="location"]');
    locationInput?.focus();
}

function handleRowChange(event) {
    const control = event.target.closest('[data-field]');

    if (!control) {
        return;
    }

    const row = control.closest('.book-item-editor-row');

    if (!row) {
        return;
    }

    if (row.dataset.rowMode === 'created') {
        return;
    }

    markRowDirtyIfChanged(row);
}

function handleRowClick(event) {
    const removeButton = event.target.closest('[data-role="remove-created-row"]');

    if (!removeButton) {
        return;
    }

    const row = removeButton.closest('.book-item-editor-row');

    if (!row) {
        return;
    }

    row.remove();
    refreshViewState();
}

function markRowDirtyIfChanged(row) {
    const currentStatus = getRowStatus(row);
    const currentLocation = getRowLocation(row);

    const originalStatus = row.dataset.originalStatus || '';
    const originalLocation = row.dataset.originalLocation || '';

    const changed = currentStatus !== originalStatus || currentLocation !== originalLocation;

    row.dataset.rowMode = changed ? 'updated' : 'clean';
    row.classList.toggle('book-item-editor-row--dirty', changed);
}

function refreshViewState() {
    const rows = getRows();

    rows.forEach((row, index) => {
        const indexElement = row.querySelector('.book-item-editor-row__index');

        if (indexElement) {
            indexElement.textContent = String(index + 1);
        }
    });

    const rowCount = rows.length;

    if (rowCountElement) {
        rowCountElement.textContent = String(rowCount);
    }

    emptyArea?.classList.toggle('is-hidden', rowCount > 0);
    tableWrap?.classList.toggle('is-hidden', rowCount === 0);
}

async function saveBookItems() {
    if (saving) {
        return;
    }

    const bookId = page?.dataset.bookId;

    if (!bookId) {
        showMessage('도서 ID를 찾을 수 없습니다.');
        return;
    }

    const validationResult = validateChangedRows();

    if (!validationResult.valid) {
        showMessage(validationResult.message);
        validationResult.row?.querySelector('[data-field="location"]')?.focus();
        return;
    }

    const payload = buildUpsertPayload();

    if (payload.createItems.length === 0 && payload.updateItems.length === 0) {
        showMessage('저장할 추가/수정 내용이 없습니다.');
        return;
    }

    try {
        setSaving(true);

        const response = await fetch(`/api/books/${bookId}/items/batch`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...getCsrfHeaders()
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMessage = await readErrorMessage(response);
            throw new Error(errorMessage || '재고 저장에 실패했습니다.');
        }

        showMessage('재고 정보가 저장되었습니다.');

        window.location.reload();
    } catch (error) {
        console.error(error);
        showMessage(error.message || '재고 저장 중 오류가 발생했습니다.');
    } finally {
        setSaving(false);
    }
}

function buildUpsertPayload() {
    const createItems = getRows()
        .filter((row) => row.dataset.rowMode === 'created')
        .map((row) => ({
            status: getRowStatus(row),
            location: getRowLocation(row)
        }));

    const updateItems = getRows()
        .filter((row) => row.dataset.rowMode === 'updated')
        .map((row) => ({
            bookItemId: Number(row.dataset.bookItemId),
            status: getRowStatus(row),
            location: getRowLocation(row)
        }));

    return {
        createItems,
        updateItems
    };
}

function validateChangedRows() {
    const changedRows = getRows().filter((row) => {
        const mode = row.dataset.rowMode;
        return mode === 'created' || mode === 'updated';
    });

    const invalidLocationRow = changedRows.find((row) => getRowLocation(row).length === 0);

    if (invalidLocationRow) {
        return {
            valid: false,
            message: '재고 위치를 입력해 주세요.',
            row: invalidLocationRow
        };
    }

    const invalidUpdateRow = changedRows.find((row) => {
        if (row.dataset.rowMode !== 'updated') {
            return false;
        }

        return !row.dataset.bookItemId || Number.isNaN(Number(row.dataset.bookItemId));
    });

    if (invalidUpdateRow) {
        return {
            valid: false,
            message: '수정할 재고 ID를 찾을 수 없습니다.',
            row: invalidUpdateRow
        };
    }

    return {
        valid: true,
        message: '',
        row: null
    };
}

function setSaving(value) {
    saving = value;

    saveHeaderButton?.toggleAttribute('disabled', value);
    saveFooterButton?.toggleAttribute('disabled', value);
    addButton?.toggleAttribute('disabled', value);

    if (saveHeaderButton) {
        saveHeaderButton.textContent = value ? '저장 중...' : '저장';
    }

    if (saveFooterButton) {
        saveFooterButton.textContent = value ? '저장 중...' : '저장';
    }
}

async function readErrorMessage(response) {
    const contentType = response.headers.get('content-type') || '';

    try {
        if (contentType.includes('application/json')) {
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

function getRows() {
    return Array.from(rowsContainer?.querySelectorAll('.book-item-editor-row') || []);
}

function getRowStatus(row) {
    return row.querySelector('[data-field="status"]')?.value || '';
}

function getRowLocation(row) {
    return row.querySelector('[data-field="location"]')?.value.trim() || '';
}

function showMessage(message) {
    if (window.alertModal && typeof window.alertModal.open === 'function') {
        window.alertModal.open(message);
        return;
    }

    if (typeof window.openAlertModal === 'function') {
        window.openAlertModal(message);
        return;
    }

    alert(message);
}