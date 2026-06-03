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

initBookItemEditor();

function initBookItemEditor() {
    bindExistingRows();
    bindEvents();
    refreshViewState();
}

function bindEvents() {
    addButton?.addEventListener('click', addNewRow);
    saveHeaderButton?.addEventListener('click', saveBookItemsDummy);
    saveFooterButton?.addEventListener('click', saveBookItemsDummy);

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

function saveBookItemsDummy() {
    const bookId = page?.dataset.bookId || null;

    const createdItems = getRows()
        .filter((row) => row.dataset.rowMode === 'created')
        .map((row) => ({
            status: getRowStatus(row),
            location: getRowLocation(row)
        }));

    const updatedItems = getRows()
        .filter((row) => row.dataset.rowMode === 'updated')
        .map((row) => ({
            bookItemId: Number(row.dataset.bookItemId),
            status: getRowStatus(row),
            location: getRowLocation(row)
        }));

    const invalidRow = getRows().find((row) => {
        const mode = row.dataset.rowMode;

        if (mode !== 'created' && mode !== 'updated') {
            return false;
        }

        return getRowLocation(row).length === 0;
    });

    if (invalidRow) {
        showMessage('재고 위치를 입력해 주세요.');
        invalidRow.querySelector('[data-field="location"]')?.focus();
        return;
    }

    const payload = {
        bookId,
        createdItems,
        updatedItems
    };

    console.log('[더미 저장] 재고 저장 payload:', payload);

    showMessage(
        `더미 저장입니다.\n추가 ${createdItems.length}건, 수정 ${updatedItems.length}건이 콘솔에 출력되었습니다.`
    );
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