(function () {
    const CREATED_ROWS_STATE = {
        items: [],
        nextTempId: 1
    };

    const CREATED_PAGE_BUTTON_SELECTOR = "[data-created-page]";
    const SERVER_PAGE_BUTTON_SELECTOR = "[data-table-pagination-page-button]";
    const DEFAULT_PAGE_SIZE = 5;

    document.addEventListener("DOMContentLoaded", () => {
        bindCreatedRowsFeature();
        syncCreatedRowsUi();
    });

    function bindCreatedRowsFeature() {
        if (document.body.dataset.bookDetailCreatedRowsBound === "true") {
            return;
        }

        document.body.dataset.bookDetailCreatedRowsBound = "true";

        document.addEventListener("click", handleDocumentClick, true);
        document.addEventListener("input", handleDocumentInput, true);
        document.addEventListener("change", handleDocumentChange, true);
    }

    function handleDocumentClick(event) {
        const card = getCopiesCard();
        if (!card) {
            return;
        }

        const addButton = event.target.closest("#addBookCopyRowButton");
        if (addButton && card.contains(addButton)) {
            event.preventDefault();
            event.stopPropagation();
            addCreatedRow();
            return;
        }

        const removeCreatedButton = event.target.closest('[data-role="remove-created-row"]');
        if (removeCreatedButton && card.contains(removeCreatedButton)) {
            const row = removeCreatedButton.closest(".table-layout__row");
            const createdRowId = row?.dataset.createdRowId;

            if (!createdRowId) {
                return;
            }

            event.preventDefault();
            event.stopPropagation();
            removeCreatedRow(createdRowId);
            return;
        }

        const createdPageButton = event.target.closest(CREATED_PAGE_BUTTON_SELECTOR);
        if (createdPageButton && card.contains(createdPageButton)) {
            event.preventDefault();
            event.stopPropagation();

            const createdPageIndex = Number(createdPageButton.dataset.createdPage);
            if (!Number.isFinite(createdPageIndex) || createdPageIndex < 0) {
                return;
            }

            activateCreatedPage(createdPageIndex);
            return;
        }

        const cancelButton = event.target.closest("#cancelBookCopiesButton");
        if (cancelButton && card.contains(cancelButton)) {
            window.setTimeout(() => {
                resetCreatedRowsState();
                syncCreatedRowsUi();
            }, 0);
            return;
        }

        const editButton = event.target.closest("#editBookCopiesButton");
        if (editButton && card.contains(editButton)) {
            window.setTimeout(() => {
                syncCreatedRowsUi();
            }, 0);
            return;
        }

        const serverPageButton = event.target.closest(SERVER_PAGE_BUTTON_SELECTOR);
        if (serverPageButton && card.contains(serverPageButton)) {
            window.setTimeout(() => {
                syncCreatedRowsUi();
            }, 0);
        }
    }

    function handleDocumentInput(event) {
        const row = event.target.closest(".book-detail-copies-table__row--created");
        if (!row) {
            return;
        }

        const item = findCreatedItem(row.dataset.createdRowId);
        if (!item) {
            return;
        }

        if (event.target.matches('[data-field="location"]')) {
            item.location = event.target.value;
        }
    }

    function handleDocumentChange(event) {
        const row = event.target.closest(".book-detail-copies-table__row--created");
        if (!row) {
            return;
        }

        const item = findCreatedItem(row.dataset.createdRowId);
        if (!item) {
            return;
        }

        if (event.target.matches('[data-field="status"]')) {
            item.status = event.target.value;
            syncCreatedRowStatusClass(event.target);
        }
    }

    function resetCreatedRowsState() {
        CREATED_ROWS_STATE.items = [];
        CREATED_ROWS_STATE.nextTempId = 1;
    }

    function syncCreatedRowsUi() {
        const card = getCopiesCard();
        if (!card) {
            return;
        }

        const addButton = card.querySelector("#addBookCopyRowButton");
        const isEditing = isEditingMode(card);

        if (addButton) {
            addButton.classList.toggle("is-hidden", !isEditing);
            addButton.hidden = !isEditing;
            addButton.style.display = isEditing ? "" : "none";
        }

        rebuildCreatedPaginationButtons();

        if (!isEditing) {
            return;
        }

        const currentPage = getCurrentVisualPage();
        const serverPageCount = getServerPageCount();
        const mixedLastPage = getMixedLastPageNumber();
        const createdPageCount = getCreatedPageCount();

        if (CREATED_ROWS_STATE.items.length === 0) {
            return;
        }

        if (currentPage === mixedLastPage || currentPage >= serverPageCount) {
            renderVisualPage(currentPage);
            return;
        }

        if (createdPageCount > 0 && currentPage > mixedLastPage) {
            renderVisualPage(currentPage);
        }
    }

    function isEditingMode(card = getCopiesCard()) {
        if (!card) {
            return false;
        }

        const editActions = card.querySelector("#bookCopyEditActions");
        const editButton = card.querySelector("#editBookCopiesButton");

        return !!editActions
            && !editActions.classList.contains("is-hidden")
            && (!editButton || editButton.classList.contains("is-hidden"));
    }

    function addCreatedRow() {
        CREATED_ROWS_STATE.items.push({
            createdRowId: String(CREATED_ROWS_STATE.nextTempId++),
            status: "AVAILABLE",
            location: ""
        });

        openTailPageForCreatedRows();
    }

    function removeCreatedRow(createdRowId) {
        const index = CREATED_ROWS_STATE.items.findIndex(
            item => item.createdRowId === String(createdRowId)
        );

        if (index < 0) {
            return;
        }

        CREATED_ROWS_STATE.items.splice(index, 1);

        if (CREATED_ROWS_STATE.items.length === 0) {
            rebuildCreatedPaginationButtons();
            openServerLastPage();
            return;
        }

        const currentPage = getCurrentVisualPage();
        const maxVisualPage = getMaxVisualPageNumber();
        const targetPage = Math.min(currentPage, maxVisualPage);

        renderVisualPage(targetPage);
    }

    function openTailPageForCreatedRows() {
        const serverLastPage = getServerLastPageNumber();
        const currentServerPage = getCurrentServerPage();

        if (currentServerPage !== serverLastPage) {
            navigateToServerLastPage(() => {
                renderVisualPage(getMixedLastPageNumber());
            });
            return;
        }

        renderVisualPage(getMixedLastPageNumber());
    }

    function navigateToServerLastPage(callback) {
        const serverButtons = getServerPageButtons();
        if (serverButtons.length === 0) {
            callback?.();
            return;
        }

        const lastButton = serverButtons[serverButtons.length - 1];
        lastButton.click();

        window.setTimeout(() => {
            callback?.();
        }, 0);
    }

    function openServerLastPage() {
        navigateToServerLastPage(() => {
            syncCreatedRowsUi();
        });
    }

    function renderVisualPage(visualPage) {
        const rowsContainer = getRowsContainer();
        if (!rowsContainer) {
            return;
        }

        const serverPageCount = getServerPageCount();
        const mixedLastPage = getMixedLastPageNumber();

        if (visualPage < serverPageCount - 1) {
            return;
        }

        if (visualPage === mixedLastPage) {
            renderMixedLastPage();
            return;
        }

        if (visualPage >= serverPageCount) {
            renderCreatedOnlyPage(visualPage);
        }
    }

    function renderMixedLastPage() {
        const rowsContainer = getRowsContainer();
        if (!rowsContainer) {
            return;
        }

        const pageSize = getPageSize();
        const serverLastRows = getServerLastPageRowsSnapshot();
        const serverLastRowCount = serverLastRows.length;
        const remainingSlots = Math.max(0, pageSize - serverLastRowCount);
        const createdItemsForMixedPage = CREATED_ROWS_STATE.items.slice(0, remainingSlots);

        rowsContainer.innerHTML = "";

        if (serverLastRows.length > 0) {
            serverLastRows.forEach(row => {
                rowsContainer.appendChild(row.cloneNode(true));
            });
        }

        createdItemsForMixedPage.forEach((item, index) => {
            const absoluteIndex = serverLastRowCount + index;
            rowsContainer.appendChild(createCreatedRowElement(item, absoluteIndex));
        });

        if (serverLastRowCount === 0 && createdItemsForMixedPage.length === 0) {
            rowsContainer.appendChild(createEmptyRow("등록된 재고가 없습니다."));
        }

        rowsContainer.dataset.pageStartIndex = String(
            getServerLastPageNumber() * pageSize
        );

        rebuildCreatedPaginationButtons();
        markActiveVisualPage(getMixedLastPageNumber());
    }

    function renderCreatedOnlyPage(visualPage) {
        const rowsContainer = getRowsContainer();
        if (!rowsContainer) {
            return;
        }

        const pageSize = getPageSize();
        const serverLastRowCount = getServerLastPageRowsSnapshot().length;
        const remainingSlots = Math.max(0, pageSize - serverLastRowCount);
        const overflowItems = CREATED_ROWS_STATE.items.slice(remainingSlots);

        const createdOnlyPageIndex = visualPage - getServerPageCount();
        const start = createdOnlyPageIndex * pageSize;
        const end = start + pageSize;
        const pageItems = overflowItems.slice(start, end);

        rowsContainer.innerHTML = "";

        if (pageItems.length === 0) {
            rowsContainer.appendChild(createEmptyRow("등록된 신규 재고가 없습니다."));
        } else {
            pageItems.forEach((item, index) => {
                rowsContainer.appendChild(createCreatedRowElement(item, start + index));
            });
        }

        rowsContainer.dataset.pageStartIndex = String(
            getServerPageCount() * pageSize + start
        );

        rebuildCreatedPaginationButtons();
        markActiveVisualPage(visualPage);
    }

    function getServerLastPageRowsSnapshot() {
        const rowsContainer = getRowsContainer();
        if (!rowsContainer) {
            return [];
        }

        const currentServerPage = getCurrentServerPage();
        const serverLastPage = getServerLastPageNumber();

        if (currentServerPage !== serverLastPage) {
            return [];
        }

        return [...rowsContainer.querySelectorAll(".table-layout__row")]
            .filter(row => !row.classList.contains("table-layout__row--empty"))
            .filter(row => row.dataset.rowMode !== "created");
    }

    function rebuildCreatedPaginationButtons() {
        removeCreatedPaginationButtons();

        const pagination = getPaginationArea();
        if (!pagination) {
            return;
        }

        const createdPageCount = getCreatedPageCount();
        if (createdPageCount <= 0) {
            return;
        }

        const basePageNumber = getServerPageCount();

        for (let i = 0; i < createdPageCount; i++) {
            const button = document.createElement("button");
            button.type = "button";
            button.className = "table-layout__page-button";
            button.dataset.createdPage = String(i);
            button.dataset.page = String(basePageNumber + i);
            button.textContent = String(basePageNumber + i + 1);
            pagination.appendChild(button);
        }
    }

    function removeCreatedPaginationButtons() {
        const pagination = getPaginationArea();
        if (!pagination) {
            return;
        }

        pagination.querySelectorAll(CREATED_PAGE_BUTTON_SELECTOR).forEach(button => {
            button.remove();
        });
    }

    function activateCreatedPage(createdPageIndex) {
        renderCreatedOnlyPage(getServerPageCount() + createdPageIndex);
    }

    function getCreatedPageCount() {
        const pageSize = getPageSize();
        const serverLastRowCount = getServerLastPageRowsSnapshot().length;
        const remainingSlots = Math.max(0, pageSize - serverLastRowCount);
        const overflowCount = Math.max(0, CREATED_ROWS_STATE.items.length - remainingSlots);

        return Math.ceil(overflowCount / pageSize);
    }

    function getMixedLastPageNumber() {
        return getServerLastPageNumber();
    }

    function getMaxVisualPageNumber() {
        return getServerLastPageNumber() + getCreatedPageCount();
    }

    function createCreatedRowElement(item, absoluteIndex) {
        const template = getRowTemplate();

        if (template) {
            const fragment = template.content.cloneNode(true);
            const row = fragment.querySelector(".table-layout__row");
            decorateCreatedRow(row, item, absoluteIndex);
            return row;
        }

        const row = document.createElement("tr");
        row.className = "table-layout__row book-detail-copies-table__row book-detail-copies-table__row--created";
        row.innerHTML = `
            <td class="table-layout__cell table-layout__cell--leading">
                <span class="book-detail-copy-row__index"></span>
            </td>
            <td class="table-layout__cell">
                <span class="book-detail-copy-row__new-label">신규</span>
            </td>
            <td class="table-layout__cell">
                <select class="book-detail-copy-control book-detail-copy-status-edit" data-field="status"></select>
            </td>
            <td class="table-layout__cell">
                <input type="text" class="book-detail-copy-control book-detail-copy-location-edit" data-field="location" placeholder="예: A-01" autocomplete="off">
            </td>
            <td class="table-layout__cell">-</td>
            <td class="table-layout__cell">
                <span class="book-detail-copy-row__empty-value">-</span>
            </td>
            <td class="table-layout__cell">
                <button type="button" class="btn btn--danger btn--sm book-detail-copy-row__remove" data-role="remove-created-row">삭제</button>
            </td>
        `;

        decorateCreatedRow(row, item, absoluteIndex);
        return row;
    }

    function decorateCreatedRow(row, item, absoluteIndex) {
        if (!row) {
            return;
        }

        row.dataset.rowMode = "created";
        row.dataset.borrowed = "false";
        row.dataset.createdRowId = item.createdRowId;

        const indexEl = row.querySelector(".book-detail-copy-row__index");
        if (indexEl) {
            indexEl.textContent = String(absoluteIndex + 1);
        }

        const statusSelect = row.querySelector('[data-field="status"]');
        if (statusSelect) {
            if (statusSelect.options.length === 0) {
                appendFallbackStatusOptions(statusSelect);
            }

            statusSelect.value = item.status || "AVAILABLE";
            syncCreatedRowStatusClass(statusSelect);
        }

        const locationInput = row.querySelector('[data-field="location"]');
        if (locationInput) {
            locationInput.value = item.location || "";
        }
    }

    function appendFallbackStatusOptions(select) {
        const fallbackOptions = [
            { value: "AVAILABLE", label: "정상" },
            { value: "LOST", label: "분실" },
            { value: "DAMAGED", label: "훼손" },
            { value: "UNAVAILABLE", label: "이용불가" }
        ];

        fallbackOptions.forEach(optionData => {
            const option = document.createElement("option");
            option.value = optionData.value;
            option.textContent = optionData.label;
            select.appendChild(option);
        });
    }

    function createEmptyRow(message) {
        const row = document.createElement("tr");
        row.className = "table-layout__row table-layout__row--empty";

        const cell = document.createElement("td");
        cell.className = "table-layout__cell table-layout__cell--empty";
        cell.colSpan = 7;
        cell.textContent = message;

        row.appendChild(cell);
        return row;
    }

    function syncCreatedRowStatusClass(select) {
        [...select.classList].forEach(className => {
            if (className.startsWith("book-detail-copy-status-edit--")) {
                select.classList.remove(className);
            }
        });

        if (select.value) {
            select.classList.add(
                `book-detail-copy-status-edit--${select.value.toLowerCase()}`
            );
        }
    }

    function findCreatedItem(createdRowId) {
        return CREATED_ROWS_STATE.items.find(
            item => item.createdRowId === String(createdRowId)
        );
    }

    function getPageSize() {
        const pagination = getPaginationArea();
        const size = Number(pagination?.dataset.pageSize);

        return Number.isFinite(size) && size > 0
            ? size
            : DEFAULT_PAGE_SIZE;
    }

    function getServerPageButtons() {
        const pagination = getPaginationArea();
        if (!pagination) {
            return [];
        }

        return [...pagination.querySelectorAll(SERVER_PAGE_BUTTON_SELECTOR)];
    }

    function getServerPageCount() {
        const pagination = getPaginationArea();
        if (!pagination) {
            return 1;
        }

        const totalPages = Number(pagination.dataset.totalPages);
        if (Number.isFinite(totalPages) && totalPages > 0) {
            return totalPages;
        }

        const buttons = getServerPageButtons();
        return buttons.length > 0 ? buttons.length : 1;
    }

    function getServerLastPageNumber() {
        return Math.max(0, getServerPageCount() - 1);
    }

    function getCurrentServerPage() {
        const active = getCopiesCard()?.querySelector(
            `${SERVER_PAGE_BUTTON_SELECTOR}.table-layout__page-button--active`
        );
        const page = Number(active?.dataset.page);

        return Number.isFinite(page) && page >= 0 ? page : 0;
    }

    function getCurrentVisualPage() {
        const active = getPaginationArea()?.querySelector(
            ".table-layout__page-button--active"
        );
        const page = Number(active?.dataset.page);

        return Number.isFinite(page) && page >= 0 ? page : 0;
    }

    function getCopiesCard() {
        return document.getElementById("bookDetailCopiesCard");
    }

    function getRowsContainer() {
        return document.getElementById("bookDetailCopyRows");
    }

    function getPaginationArea() {
        return getCopiesCard()?.querySelector(".table-layout__pagination") || null;
    }

    function getRowTemplate() {
        return document.getElementById("bookDetailCopyRowTemplate");
    }
})();