const STATUS_SELECT_CLASS_PREFIX = "book-detail-copy-status-edit--";
const DEFAULT_COPY_PAGE_SIZE = 5;
const PAGE_BLOCK_SIZE = 3;

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
        updatedCopies: new Map(),
        deletedCopyIds: new Set()
    };

    const createdState = {
        items: [],
        nextTempId: 1,
        targetPage: null,
        lastServerRowCount: 0
    };

    function getCopiesCard() {
        return bookCopiesArea.querySelector("#bookDetailCopiesCard");
    }

    function getRowsContainer() {
        return bookCopiesArea.querySelector("#bookDetailCopyRows");
    }

    function getPaginationArea() {
        return bookCopiesArea.querySelector(".table-layout__pagination");
    }

    function getPaginationNav() {
        return getPaginationArea()?.querySelector(".table-layout__pagination-nav") || null;
    }

    function getRowTemplate() {
        return bookCopiesArea.querySelector("#bookDetailCopyRowTemplate");
    }

    function getEmptyRowTemplate() {
        return bookCopiesArea.querySelector("#bookDetailCopyEmptyRowTemplate");
    }

    function getCurrentPageSize() {
        const pagination = getPaginationArea();
        const pageSize = Number(pagination?.dataset.pageSize);

        return Number.isFinite(pageSize) && pageSize > 0
            ? pageSize
            : DEFAULT_COPY_PAGE_SIZE;
    }

    function getCurrentPage() {
        const page = Number(getPaginationArea()?.dataset.currentPage);

        return Number.isFinite(page) && page >= 0
            ? page
            : 0;
    }

    function getServerPageCount() {
        const totalPages = Number(getPaginationArea()?.dataset.totalPages);

        return Number.isFinite(totalPages) && totalPages > 0
            ? totalPages
            : 1;
    }

    function getLastServerPage() {
        return Math.max(0, getServerPageCount() - 1);
    }

    function getRows() {
        const rowsContainer = getRowsContainer();
        if (!rowsContainer) {
            return [];
        }

        return [
            ...rowsContainer.querySelectorAll(".table-layout__row")
        ].filter(row => !row.classList.contains("table-layout__row--empty"));
    }

    function getPageStartIndex() {
        const rowsContainer = getRowsContainer();
        if (!rowsContainer) {
            return 0;
        }

        const pageStartIndex = Number(rowsContainer.dataset.pageStartIndex);
        return Number.isFinite(pageStartIndex) && pageStartIndex >= 0
            ? pageStartIndex
            : 0;
    }

    function getColumnCount() {
        const rowsContainer = getRowsContainer();
        const columnCount = Number(rowsContainer?.dataset.columnCount);

        return Number.isFinite(columnCount) && columnCount > 0
            ? columnCount
            : 1;
    }

    function getBookCopyId(row) {
        const bookCopyId = Number(row.dataset.bookCopyId);
        return Number.isFinite(bookCopyId) && bookCopyId > 0
            ? bookCopyId
            : null;
    }

    function getCreatedRowId(row) {
        return row?.dataset.createdRowId || "";
    }

    function isBorrowed(row) {
        return row.dataset.borrowed === "true";
    }

    function isCreatedRow(row) {
        return row?.dataset.rowMode === "created";
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

        select.classList.add(
            `${STATUS_SELECT_CLASS_PREFIX}${status.toLowerCase()}`
        );
    }

    function syncAllStatusSelectColors(root = bookCopiesArea) {
        root.querySelectorAll(".book-detail-copy-status-edit").forEach(select => {
            syncStatusSelectColor(select);
        });
    }

    function createCreatedRowData() {
        return {
            createdRowId: String(createdState.nextTempId++),
            status: "AVAILABLE",
            location: ""
        };
    }

    function findCreatedRowItem(createdRowId) {
        return createdState.items.find(item => item.createdRowId === String(createdRowId));
    }

    function decorateCreatedRow(row, item) {
        if (!row) {
            return;
        }

        row.dataset.rowMode = "created";
        row.dataset.borrowed = "false";
        row.dataset.createdRowId = item.createdRowId;

        const statusControl = row.querySelector('[data-field="status"]');
        const locationControl = row.querySelector('[data-field="location"]');

        if (statusControl) {
            statusControl.value = item.status || "AVAILABLE";
            syncStatusSelectColor(statusControl);
        }

        if (locationControl) {
            locationControl.value = item.location || "";
        }
    }

    function createCreatedRowElement(item) {
        const template = getRowTemplate();

        if (!template) {
            console.error("신규 재고 행 템플릿을 찾을 수 없습니다. #bookDetailCopyRowTemplate");
            return null;
        }

        const fragment = template.content.cloneNode(true);
        const row = fragment.querySelector(".table-layout__row");

        if (!row) {
            console.error("신규 재고 행 템플릿 안에서 .table-layout__row를 찾을 수 없습니다.");
            return null;
        }

        decorateCreatedRow(row, item);
        return row;
    }

    function createCreatedEmptyRowElement() {
        const template = getEmptyRowTemplate();

        if (!template) {
            console.error("신규 재고 빈 행 템플릿을 찾을 수 없습니다. #bookDetailCopyEmptyRowTemplate");
            return null;
        }

        const fragment = template.content.cloneNode(true);
        const row = fragment.querySelector(".table-layout__row");

        if (!row) {
            console.error("신규 재고 빈 행 템플릿 안에서 .table-layout__row를 찾을 수 없습니다.");
            return null;
        }

        const emptyCell = row.querySelector(".table-layout__cell--empty");
        if (emptyCell) {
            emptyCell.colSpan = getColumnCount();
        }

        return row;
    }

    function syncCreatedRowItemFromRow(row) {
        const createdRowId = getCreatedRowId(row);
        const item = findCreatedRowItem(createdRowId);

        if (!item) {
            return;
        }

        item.status = getRowStatus(row) || "AVAILABLE";
        item.location = getRowLocation(row);
    }

    function removeCreatedRowsFromDom() {
        const rowsContainer = getRowsContainer();
        if (!rowsContainer) {
            return;
        }

        rowsContainer.querySelectorAll('.table-layout__row[data-row-mode="created"]').forEach(row => {
            row.remove();
        });
    }

    function syncLastServerRowCountFromDom() {
        if (getCurrentPage() !== getLastServerPage()) {
            return;
        }

        createdState.lastServerRowCount = getRows()
            .filter(row => !isCreatedRow(row))
            .length;
    }

    function getMixedCreatedCapacity() {
        return Math.max(0, getCurrentPageSize() - createdState.lastServerRowCount);
    }

    function getMixedCreatedItems() {
        return createdState.items.slice(0, getMixedCreatedCapacity());
    }

    function getOverflowCreatedItems() {
        return createdState.items.slice(getMixedCreatedCapacity());
    }

    function getCreatedVirtualPageCount() {
        const overflowCount = getOverflowCreatedItems().length;

        return overflowCount === 0
            ? 0
            : Math.ceil(overflowCount / getCurrentPageSize());
    }

    function getTailCreatedPage() {
        const mixedCapacity = getMixedCreatedCapacity();

        if (createdState.items.length <= mixedCapacity) {
            return getLastServerPage();
        }

        const overflowIndex = createdState.items.length - mixedCapacity - 1;
        const virtualPageIndex = Math.floor(overflowIndex / getCurrentPageSize());

        return getServerPageCount() + virtualPageIndex;
    }

    function getTotalPageCountWithCreated() {
        return Math.max(
            1,
            getServerPageCount() + getCreatedVirtualPageCount()
        );
    }

    function getPageBlockStart(page) {
        return page - (page % PAGE_BLOCK_SIZE);
    }

    function getPageBlockEnd(startPage, totalPages) {
        return Math.min(
            totalPages - 1,
            startPage + PAGE_BLOCK_SIZE - 1
        );
    }

    function findPaginationMoveButton(text) {
        const paginationNav = getPaginationNav();

        if (!paginationNav) {
            return null;
        }

        return [...paginationNav.querySelectorAll("[data-table-pagination-page-button]")]
            .find(button => button.textContent.trim() === text) || null;
    }

    function syncMoveButton(button, page, totalPages) {
        if (!button) {
            return;
        }

        const disabled = page < 0 || page >= totalPages;

        button.dataset.page = String(page);
        button.disabled = disabled;

        if (!disabled && page >= getServerPageCount()) {
            button.dataset.createdPage = String(page - getServerPageCount());
        } else {
            delete button.dataset.createdPage;
        }
    }

    function removePaginationNumberButtons() {
        const paginationNav = getPaginationNav();

        if (!paginationNav) {
            return;
        }

        paginationNav.querySelectorAll("[data-table-pagination-number-button]").forEach(button => {
            button.remove();
        });
    }

    function createPaginationNumberButton(page, activePage) {
        const button = document.createElement("button");

        button.type = "button";
        button.className = "table-layout__page-button";
        button.dataset.page = String(page);
        button.textContent = String(page + 1);

        button.setAttribute("data-table-pagination-page-button", "");
        button.setAttribute("data-table-pagination-number-button", "");

        if (page >= getServerPageCount()) {
            button.dataset.createdPage = String(page - getServerPageCount());
        }

        const active = page === activePage;

        button.classList.toggle("table-layout__page-button--active", active);
        button.disabled = active;

        if (active) {
            button.setAttribute("aria-current", "page");
        }

        return button;
    }

    function syncCreatedPaginationButtons(activePage = getCurrentPage()) {
        const paginationNav = getPaginationNav();

        if (!paginationNav) {
            return;
        }

        const totalPages = getTotalPageCountWithCreated();
        const normalizedActivePage = Math.min(
            Math.max(0, activePage),
            totalPages - 1
        );

        const previousButton = findPaginationMoveButton("이전");
        const nextButton = findPaginationMoveButton("다음");

        removePaginationNumberButtons();

        const startPage = getPageBlockStart(normalizedActivePage);
        const endPage = getPageBlockEnd(startPage, totalPages);

        for (let page = startPage; page <= endPage; page++) {
            const button = createPaginationNumberButton(page, normalizedActivePage);

            if (nextButton) {
                paginationNav.insertBefore(button, nextButton);
            } else {
                paginationNav.appendChild(button);
            }
        }

        syncMoveButton(previousButton, normalizedActivePage - 1, totalPages);
        syncMoveButton(nextButton, normalizedActivePage + 1, totalPages);
    }

    function markActivePage(page) {
        const pagination = getPaginationArea();
        if (!pagination) {
            return;
        }

        pagination.dataset.currentPage = String(page);

        pagination
            .querySelectorAll("[data-table-pagination-number-button]")
            .forEach(button => {
                const buttonPage = Number(button.dataset.page);
                const active = Number.isFinite(buttonPage) && buttonPage === page;

                button.classList.toggle("table-layout__page-button--active", active);

                if (active) {
                    button.setAttribute("aria-current", "page");
                } else {
                    button.removeAttribute("aria-current");
                }

                button.disabled = active;
            });
    }

    function renderCreatedRowsOnCurrentPage() {
        const rowsContainer = getRowsContainer();
        if (!rowsContainer) {
            return;
        }

        if (!editState.editing) {
            return;
        }

        if (createdState.targetPage === null) {
            return;
        }

        if (getCurrentPage() !== createdState.targetPage) {
            syncCreatedPaginationButtons();
            return;
        }

        syncLastServerRowCountFromDom();
        removeCreatedRowsFromDom();

        const emptyRow = rowsContainer.querySelector(".table-layout__row--empty");
        if (emptyRow && createdState.items.length > 0) {
            emptyRow.remove();
        }

        getMixedCreatedItems().forEach(item => {
            const row = createCreatedRowElement(item);

            if (row) {
                rowsContainer.appendChild(row);
            }
        });

        syncCreatedPaginationButtons(createdState.targetPage);
        markActivePage(createdState.targetPage);
        refreshRowIndexes();
        refreshEmptyState();
        syncAllStatusSelectColors(getCopiesCard());
    }

    function renderCreatedVirtualPage(page) {
        const rowsContainer = getRowsContainer();
        if (!rowsContainer) {
            return;
        }

        captureVisibleState();

        const virtualPageIndex = page - getServerPageCount();
        if (virtualPageIndex < 0) {
            return;
        }

        const pageSize = getCurrentPageSize();
        const overflowItems = getOverflowCreatedItems();
        const start = virtualPageIndex * pageSize;
        const end = start + pageSize;
        const pageItems = overflowItems.slice(start, end);

        rowsContainer.innerHTML = "";

        if (pageItems.length === 0) {
            const emptyRow = createCreatedEmptyRowElement();

            if (emptyRow) {
                rowsContainer.appendChild(emptyRow);
            }
        } else {
            pageItems.forEach(item => {
                const row = createCreatedRowElement(item);

                if (row) {
                    rowsContainer.appendChild(row);
                }
            });
        }

        rowsContainer.dataset.pageStartIndex = String(getServerPageCount() * pageSize + start);

        setEditMode(true, {
            resetState: false,
            captureBefore: false
        });

        syncCreatedPaginationButtons(page);
        markActivePage(page);
        refreshRowIndexes();
        refreshEmptyState();
        syncAllStatusSelectColors(getCopiesCard());
    }

    function captureVisibleState() {
        if (!editState.editing) {
            return;
        }

        getRows().forEach(row => {
            if (isCreatedRow(row)) {
                syncCreatedRowItemFromRow(row);
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
        });
    }

    function clearCreatedState() {
        createdState.items = [];
        createdState.nextTempId = 1;
        createdState.targetPage = null;
        createdState.lastServerRowCount = 0;
    }

    function clearEditState() {
        editState.editing = false;
        editState.updatedCopies.clear();
        editState.deletedCopyIds.clear();
        clearCreatedState();
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

        let html = "";

        try {
            html = await fetchCopies(page);
        } catch (error) {
            console.error("재고 현황 fetch 실패", error);
            await showAlert("재고 현황을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.");
            return;
        }

        if (!html || !html.trim()) {
            console.warn("재고 현황 응답이 비어 있습니다.");
            return;
        }

        bookCopiesArea.innerHTML = html;

        const shouldFallbackToLastPage = options.fallbackToLastPage === true;
        const serverPageCountAfterRender = getServerPageCount();

        if (
            shouldFallbackToLastPage
            && page >= serverPageCountAfterRender
            && serverPageCountAfterRender > 0
        ) {
            await renderCopies(getLastServerPage(), {
                captureBefore: false,
                fallbackToLastPage: false
            });
            return;
        }

        try {
            initBookDetailCopies();
            applyEditStateToCurrentPage();
            renderCreatedRowsOnCurrentPage();
            syncCreatedPaginationButtons(page);
            markActivePage(page);
        } catch (error) {
            console.error("재고 현황 렌더 후처리 실패", error);
        }
    }

    async function appendCreatedRowToLastServerPage() {
        captureVisibleState();

        const lastPage = getLastServerPage();
        const currentPage = getCurrentPage();

        createdState.targetPage = lastPage;

        if (createdState.lastServerRowCount === 0 || currentPage < getServerPageCount()) {
            if (currentPage !== lastPage) {
                await renderCopies(lastPage, {
                    captureBefore: false
                });
            }

            syncLastServerRowCountFromDom();
        }

        const newItem = createCreatedRowData();
        createdState.items.push(newItem);

        const targetPage = getTailCreatedPage();

        if (targetPage === lastPage) {
            if (getCurrentPage() !== lastPage) {
                await renderCopies(lastPage, {
                    captureBefore: false
                });
            } else {
                renderCreatedRowsOnCurrentPage();
            }
            return;
        }

        renderCreatedVirtualPage(targetPage);
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
        row.classList.add("table-layout__row--dirty");
        row.classList.remove("table-layout__row--deleted");
    }

    function applyDeletedVisualState(row) {
        row.dataset.beforeDeleteMode = row.dataset.rowMode || "clean";
        row.dataset.rowMode = "deleted";

        row.classList.remove("table-layout__row--dirty");
        row.classList.add("table-layout__row--deleted");

        row.querySelectorAll(".book-detail-copy-control").forEach(control => {
            control.disabled = true;
        });
    }

    function setEditMode(editing, options = {}) {
        const copiesCard = getCopiesCard();
        if (!copiesCard) {
            return;
        }

        const resetState = options.resetState ?? false;
        const shouldCapture = options.captureBefore !== false;

        if (shouldCapture && editState.editing) {
            captureVisibleState();
        }

        if (!editing && resetState) {
            clearEditState();
        } else {
            editState.editing = editing;
        }

        copiesCard.classList.toggle("is-editing", editing);

        const editButton = copiesCard.querySelector("#editBookCopiesButton");
        const editActions = copiesCard.querySelector("#bookCopyEditActions");
        const addRowButton = copiesCard.querySelector("#addBookCopyRowButton");

        editButton?.classList.toggle("is-hidden", editing);
        editActions?.classList.toggle("is-hidden", !editing);
        addRowButton?.classList.toggle("is-hidden", !editing);

        getRows().forEach(row => {
            const isCreated = isCreatedRow(row);

            if (isCreated) {
                row.querySelectorAll(".book-detail-copy-control").forEach(control => {
                    control.disabled = !editing;
                });
                return;
            }

            const borrowed = isBorrowed(row);
            const isDeleted = row.dataset.rowMode === "deleted";
            const rowMode = row.dataset.rowMode || "clean";

            const statusView = row.querySelector(".book-detail-copy-status-view");
            const locationView = row.querySelector(".book-detail-copy-location-view");
            const statusEdit = row.querySelector(".book-detail-copy-status-edit");
            const locationEdit = row.querySelector(".book-detail-copy-location-edit");
            const deleteButton = row.querySelector('[data-role="mark-delete-row"]');
            const cancelDeleteButton = row.querySelector('[data-role="cancel-delete-row"]');
            const stateLabel = row.querySelector(".book-detail-copy-row__state");

            statusView?.classList.toggle("is-hidden", editing);
            locationView?.classList.toggle("is-hidden", editing);

            statusEdit?.classList.toggle("is-hidden", !editing || isDeleted);
            locationEdit?.classList.toggle("is-hidden", !editing || isDeleted);

            if (statusEdit) {
                statusEdit.disabled = !editing || borrowed || isDeleted;
            }

            if (locationEdit) {
                locationEdit.disabled = !editing || borrowed || isDeleted;
            }

            deleteButton?.classList.toggle("is-hidden", !editing || borrowed || isDeleted);
            cancelDeleteButton?.classList.toggle("is-hidden", !editing || !isDeleted);

            if (stateLabel) {
                if (!editing) {
                    stateLabel.textContent = "기존";
                    stateLabel.classList.remove("is-hidden");
                } else if (isDeleted) {
                    stateLabel.textContent = "삭제 예정";
                    stateLabel.classList.remove("is-hidden");
                } else if (rowMode === "updated") {
                    stateLabel.textContent = "수정됨";
                    stateLabel.classList.remove("is-hidden");
                } else {
                    stateLabel.classList.add("is-hidden");
                }
            }
        });

        syncAllStatusSelectColors(copiesCard);
        refreshRowIndexes();
        refreshEmptyState();
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
            if (isCreatedRow(row)) {
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

        refreshRowIndexes();
        refreshEmptyState();
        syncAllStatusSelectColors(copiesCard);
    }

    function updateRowDirtyState(row) {
        if (!row || isBorrowed(row) || isCreatedRow(row)) {
            return;
        }

        if (row.dataset.rowMode === "deleted") {
            return;
        }

        const dirty = isRowChanged(row);
        row.dataset.rowMode = dirty ? "updated" : "clean";
        row.classList.toggle("table-layout__row--dirty", dirty);
    }

    function markRowDeleted(row) {
        if (!row || isBorrowed(row) || isCreatedRow(row)) {
            return;
        }

        const bookCopyId = getBookCopyId(row);
        if (!bookCopyId) {
            return;
        }

        row.dataset.beforeDeleteMode = row.dataset.rowMode || "clean";
        row.dataset.rowMode = "deleted";

        row.classList.remove("table-layout__row--dirty");
        row.classList.add("table-layout__row--deleted");

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

        row.classList.remove("table-layout__row--deleted");

        row.querySelectorAll(".book-detail-copy-control").forEach(control => {
            control.disabled = isBorrowed(row);
        });

        updateRowDirtyState(row);
        syncStateFromRow(row);
    }

    function syncStateFromRow(row) {
        if (!editState.editing || !row) {
            return;
        }

        if (isCreatedRow(row)) {
            syncCreatedRowItemFromRow(row);
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

    function refreshRowIndexes() {
        const pageStartIndex = getPageStartIndex();
        let visibleIndex = 0;

        getRows().forEach(row => {
            const indexElement = row.querySelector(".book-detail-copy-row__index");

            if (!indexElement) {
                return;
            }

            indexElement.textContent = String(pageStartIndex + visibleIndex + 1);
            visibleIndex++;
        });
    }

    function refreshEmptyState() {
        const rowsContainer = getRowsContainer();
        if (!rowsContainer) {
            return;
        }

        const emptyRow = rowsContainer.querySelector(".table-layout__row--empty");
        const hasRows = getRows().length > 0;

        if (emptyRow) {
            emptyRow.hidden = hasRows;
            emptyRow.classList.toggle("is-hidden", hasRows);
        }
    }

    function buildPayload() {
        captureVisibleState();

        return {
            createItems: createdState.items.map(item => ({
                status: item.status,
                location: item.location
            })),
            updateItems: [...editState.updatedCopies.values()].map(copy => ({
                bookItemId: copy.bookCopyId,
                status: copy.status,
                location: copy.location
            })),
            deleteIds: [...editState.deletedCopyIds]
        };
    }

    function validatePayload(payload) {
        const createItems = payload.createItems;
        const updateItems = payload.updateItems;
        const deleteIds = payload.deleteIds;

        if (
            createItems.length === 0
            && updateItems.length === 0
            && deleteIds.length === 0
        ) {
            return {
                valid: false,
                message: "저장할 추가/수정/삭제 내용이 없습니다."
            };
        }

        const invalidCreateStatusCopy = createItems.find(copy => !copy.status);
        if (invalidCreateStatusCopy) {
            return {
                valid: false,
                message: "추가할 재고의 도서 상태를 선택해 주세요."
            };
        }

        const emptyCreateLocationCopy = createItems.find(copy => {
            return copy.location.length === 0;
        });
        if (emptyCreateLocationCopy) {
            return {
                valid: false,
                message: "추가할 재고의 위치를 입력해 주세요."
            };
        }

        const invalidUpdateStatusCopy = updateItems.find(copy => !copy.status);
        if (invalidUpdateStatusCopy) {
            return {
                valid: false,
                message: "수정할 재고의 도서 상태를 선택해 주세요."
            };
        }

        const invalidUpdateCopy = updateItems.find(copy => {
            return !Number.isFinite(copy.bookItemId) || copy.bookItemId <= 0;
        });
        if (invalidUpdateCopy) {
            return {
                valid: false,
                message: "수정할 재고 ID를 찾을 수 없습니다."
            };
        }

        const emptyUpdateLocationCopy = updateItems.find(copy => {
            return copy.location.length === 0;
        });
        if (emptyUpdateLocationCopy) {
            return {
                valid: false,
                message: "수정할 재고의 위치를 입력해 주세요."
            };
        }

        const invalidDeleteCopyId = deleteIds.find(bookItemId => {
            return !Number.isFinite(bookItemId) || bookItemId <= 0;
        });
        if (invalidDeleteCopyId) {
            return {
                valid: false,
                message: "삭제할 재고 ID를 찾을 수 없습니다."
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

            const pageToRenderAfterSave = getCurrentPage();

            clearEditState();

            await renderCopies(pageToRenderAfterSave, {
                captureBefore: false,
                fallbackToLastPage: true
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
            const confirmed = await showConfirm(
                "저장하지 않은 변경사항을 취소하시겠습니까?"
            );
            if (!confirmed) {
                return;
            }

            const currentPage = getCurrentPage();
            clearEditState();

            await renderCopies(
                currentPage >= getServerPageCount() ? getLastServerPage() : currentPage,
                { captureBefore: false }
            );
        });

        saveButton?.addEventListener("click", saveBookCopies);

        addRowButton?.addEventListener("click", async () => {
            await appendCreatedRowToLastServerPage();
        });

        rowsContainer?.addEventListener("input", event => {
            const control = event.target.closest(".book-detail-copy-control");
            if (!control) {
                return;
            }

            const row = control.closest(".table-layout__row");
            if (!row || row.classList.contains("table-layout__row--empty")) {
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

            const row = control.closest(".table-layout__row");
            if (!row || row.classList.contains("table-layout__row--empty")) {
                return;
            }

            if (control.dataset.field === "status") {
                syncStatusSelectColor(control);
            }

            updateRowDirtyState(row);
            syncStateFromRow(row);
        });

        rowsContainer?.addEventListener("click", async event => {
            const removeCreatedButton = event.target.closest(
                '[data-role="remove-created-row"]'
            );

            if (removeCreatedButton) {
                const row = removeCreatedButton.closest(".table-layout__row");
                const createdRowId = getCreatedRowId(row);

                captureVisibleState();

                createdState.items = createdState.items.filter(
                    item => item.createdRowId !== createdRowId
                );

                if (createdState.items.length === 0) {
                    await renderCopies(getLastServerPage(), {
                        captureBefore: false
                    });
                    return;
                }

                const currentPage = getCurrentPage();
                const virtualPageCount = getCreatedVirtualPageCount();

                if (currentPage >= getServerPageCount()) {
                    const maxVirtualPage = getServerPageCount() + virtualPageCount - 1;

                    if (virtualPageCount <= 0) {
                        await renderCopies(getLastServerPage(), {
                            captureBefore: false
                        });
                        return;
                    }

                    renderCreatedVirtualPage(Math.min(currentPage, maxVirtualPage));
                    return;
                }

                renderCreatedRowsOnCurrentPage();
                return;
            }

            const markDeleteButton = event.target.closest(
                '[data-role="mark-delete-row"]'
            );

            if (markDeleteButton) {
                const row = markDeleteButton.closest(".table-layout__row");
                markRowDeleted(row);
                return;
            }

            const cancelDeleteButton = event.target.closest(
                '[data-role="cancel-delete-row"]'
            );

            if (cancelDeleteButton) {
                const row = cancelDeleteButton.closest(".table-layout__row");
                cancelDeleteRow(row);
            }
        });

        syncAllStatusSelectColors(copiesCard);
        setEditMode(editState.editing, {
            resetState: false,
            captureBefore: false
        });
    }

    bookCopiesArea.addEventListener("click", async event => {
        const createdPageButton = event.target.closest("[data-created-page]");
        if (createdPageButton) {
            event.preventDefault();

            const page = Number(createdPageButton.dataset.page);
            if (Number.isFinite(page) && page >= getServerPageCount()) {
                renderCreatedVirtualPage(page);
            }

            return;
        }

        const pageButton = event.target.closest("[data-table-pagination-page-button]");
        if (!pageButton) {
            return;
        }

        event.preventDefault();

        const page = Number(pageButton.dataset.page);
        if (!Number.isFinite(page) || page < 0) {
            return;
        }

        if (page >= getServerPageCount()) {
            renderCreatedVirtualPage(page);
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

    return !token || !header
        ? {}
        : { [header]: token };
}

function showAlert(message, title = "안내") {
    return new Promise(resolve => {
        if (typeof openAlertModal !== "function") {
            window.alert(message);
            resolve(true);
            return;
        }

        openAlertModal({
            title,
            message,
            confirmText: "확인",
            onConfirm: () => resolve(true)
        });
    });
}

function showConfirm(message, title = "확인") {
    return new Promise(resolve => {
        if (typeof openAlertModal !== "function") {
            resolve(window.confirm(message));
            return;
        }

        openAlertModal({
            title,
            message,
            confirmText: "확인",
            cancelText: "취소",
            onConfirm: () => resolve(true),
            onCancel: () => resolve(false)
        });
    });
}