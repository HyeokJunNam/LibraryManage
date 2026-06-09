/**
 * 공통 테이블 레이아웃 비동기/동기 검색 및 페이지네이션 스크립트
 */
(function () {
    if (window.__tableLayoutInitialized) return;
    window.__tableLayoutInitialized = true;

    const TABLE_MODE = Object.freeze({
        SERVER: "server",
        AJAX: "ajax",
        CLIENT: "client"
    });

    const DATA = Object.freeze({
        FRAGMENT_URL: "fragmentUrl",
        CURRENT_URL: "currentUrl",
        CURRENT_SEARCH_TARGET: "currentSearchTarget",
        SEARCH_MODE: "searchMode",
        PAGINATION_MODE: "paginationMode",
        CURRENT_PAGE: "currentPage",
        TOTAL_PAGES: "totalPages",
        PAGE_SIZE: "pageSize",
        PAGE_BLOCK_SIZE: "pageBlockSize",
        SUBMITTING: "isSubmitting"
    });

    const SELECTOR = Object.freeze({
        FRAGMENT_CONTAINER: "[data-fragment-url]",

        SEARCH_FORM: "[data-table-search-form]",
        SEARCH_TARGET: "[data-table-search-target]",
        SEARCH_KEYWORD: "[data-table-search-keyword]",

        TOOLBAR: ".table-layout__toolbar",

        PAGINATION: ".table-layout__pagination",
        PAGINATION_FORM: "[data-table-pagination-form]",
        PAGINATION_PAGE: "[data-table-pagination-page]",
        PAGINATION_SIZE: "[data-table-pagination-size]",
        PAGINATION_PAGE_BUTTON: "[data-table-pagination-page-button]",
        PAGINATION_HIDDEN: "[data-table-pagination-hidden]",

        PAGINATION_CLIENT_NAV: "[data-table-pagination-client-nav]",
        PAGINATION_CLIENT_BUTTON: "[data-table-pagination-client-button]"
    });

    const CLASS_NAME = Object.freeze({
        HIDDEN: "is-hidden",
        EMPTY_PAGINATION: "table-layout__pagination--empty"
    });

    const EVENT_NAME = Object.freeze({
        UPDATED: "table-layout:updated",
        CLIENT_PAGE_CHANGE: "table-layout:client-page-change"
    });

    const REQUEST_HEADER = Object.freeze({
        AJAX: "XMLHttpRequest"
    });

    const FIRST_PAGE = "0";
    const DEFAULT_PAGE_BLOCK_SIZE = 3;
    const clientPaginationStateMap = new WeakMap();

    function resolveElement(target) {
        if (!target) return null;
        if (typeof target === "string") return document.querySelector(target);
        if (target instanceof Element) return target;
        return null;
    }

    function resolvePaginationElement(target) {
        const element = resolveElement(target);
        if (!element) return null;

        if (element.matches?.(SELECTOR.PAGINATION)) {
            return element;
        }

        if (element.closest?.(SELECTOR.PAGINATION)) {
            return element.closest(SELECTOR.PAGINATION);
        }

        return element.querySelector?.(SELECTOR.PAGINATION) || null;
    }

    function getFragmentContainer(element) {
        return element?.closest?.(SELECTOR.FRAGMENT_CONTAINER) || null;
    }

    function toAbsoluteUrl(url) {
        return new URL(url, window.location.origin).toString();
    }

    function toSafeNumber(value, fallback = 0) {
        const number = Number(value);
        return Number.isFinite(number) ? number : fallback;
    }

    function normalizeSearchMode(value) {
        return value === TABLE_MODE.AJAX ? TABLE_MODE.AJAX : TABLE_MODE.SERVER;
    }

    function normalizePaginationMode(value) {
        if (value === TABLE_MODE.AJAX || value === TABLE_MODE.CLIENT) {
            return value;
        }

        return TABLE_MODE.SERVER;
    }

    function isAjaxMode(value) {
        return normalizeSearchMode(value) === TABLE_MODE.AJAX;
    }

    function getToolbarMode(form) {
        const toolbar = form.closest(SELECTOR.TOOLBAR);
        return normalizeSearchMode(toolbar?.dataset[DATA.SEARCH_MODE]);
    }

    function getPaginationMode(target) {
        const pagination = target?.closest?.(SELECTOR.PAGINATION) ||
            (target?.matches?.(SELECTOR.PAGINATION) ? target : null);

        return normalizePaginationMode(pagination?.dataset[DATA.PAGINATION_MODE]);
    }

    function isAjaxSearchForm(form) {
        return isAjaxMode(getToolbarMode(form));
    }

    function isAjaxPaginationForm(form) {
        return getPaginationMode(form) === TABLE_MODE.AJAX;
    }

    function isClientPagination(target) {
        return getPaginationMode(target) === TABLE_MODE.CLIENT;
    }

    function getContainerCurrentUrl(container) {
        const toolbar = container?.querySelector(SELECTOR.TOOLBAR);
        const searchMode = normalizeSearchMode(toolbar?.dataset[DATA.SEARCH_MODE]);

        if (searchMode === TABLE_MODE.SERVER) {
            return window.location.href;
        }

        if (container?.dataset[DATA.CURRENT_URL]?.trim()) {
            return toAbsoluteUrl(container.dataset[DATA.CURRENT_URL]);
        }

        if (container?.dataset[DATA.FRAGMENT_URL]?.trim()) {
            return toAbsoluteUrl(container.dataset[DATA.FRAGMENT_URL]);
        }

        return window.location.href;
    }

    function getFormBaseUrl(form) {
        const container = getFragmentContainer(form);

        if (container?.dataset[DATA.FRAGMENT_URL]?.trim()) {
            return toAbsoluteUrl(container.dataset[DATA.FRAGMENT_URL]);
        }

        const action = form.getAttribute("action");
        if (action?.trim()) {
            return toAbsoluteUrl(action);
        }

        return toAbsoluteUrl(window.location.pathname);
    }

    function getSearchFields(form) {
        const targetSelect = form.querySelector(SELECTOR.SEARCH_TARGET);
        if (!targetSelect) return [];

        return Array.from(targetSelect.options)
            .map((option) => option.value)
            .filter((value) => value?.trim());
    }

    function initializeSearchForm(form) {
        const container = getFragmentContainer(form);
        const currentUrl = getContainerCurrentUrl(container);
        const urlParams = new URL(currentUrl, window.location.origin).searchParams;

        const targetSelect = form.querySelector(SELECTOR.SEARCH_TARGET);
        const keywordInput = form.querySelector(SELECTOR.SEARCH_KEYWORD);

        if (!targetSelect || !keywordInput) return;

        const fieldNames = getSearchFields(form);
        const activeFieldName =
            fieldNames.find((name) => urlParams.get(name)?.trim()) ||
            container?.dataset[DATA.CURRENT_SEARCH_TARGET] ||
            "";

        if (activeFieldName && fieldNames.includes(activeFieldName)) {
            targetSelect.value = activeFieldName;
            keywordInput.value = urlParams.get(activeFieldName) || "";
            return;
        }

        keywordInput.value = "";
    }

    function prepareSearchForm(form) {
        const container = getFragmentContainer(form);
        const targetSelect = form.querySelector(SELECTOR.SEARCH_TARGET);
        const keywordInput = form.querySelector(SELECTOR.SEARCH_KEYWORD);

        if (!targetSelect || !keywordInput) return;

        const selectedField = targetSelect.value?.trim() || "";
        const keyword = keywordInput.value.trim();

        if (container) {
            container.dataset[DATA.CURRENT_SEARCH_TARGET] = selectedField;
        }

        keywordInput.removeAttribute("name");

        if (!selectedField || !keyword) {
            keywordInput.value = "";
            return;
        }

        keywordInput.setAttribute("name", selectedField);
        keywordInput.value = keyword;
    }

    function clearPaginationPageToFirst(container) {
        const pageInput = container?.querySelector(SELECTOR.PAGINATION_PAGE);
        if (pageInput) {
            pageInput.value = FIRST_PAGE;
        }
    }

    function setPaginationPage(form, submitter) {
        if (!submitter?.matches(SELECTOR.PAGINATION_PAGE_BUTTON)) return;

        const pageInput = form.querySelector(SELECTOR.PAGINATION_PAGE);
        if (pageInput) {
            pageInput.value = submitter.dataset.page;
        }
    }

    function getCurrentPageSize(container) {
        const sizeInput = container?.querySelector(SELECTOR.PAGINATION_SIZE);
        return sizeInput?.value?.trim() || "";
    }

    function syncPaginationHiddenInputs(form) {
        form.querySelectorAll(SELECTOR.PAGINATION_HIDDEN).forEach((input) => {
            input.disabled = !input.value?.trim();
        });
    }

    function syncPaginationSearchParams(container) {
        if (!container) return;

        const searchForm = container.querySelector(SELECTOR.SEARCH_FORM);
        const paginationForm = container.querySelector(SELECTOR.PAGINATION_FORM);

        if (!searchForm || !paginationForm) return;

        const fieldNames = getSearchFields(searchForm);
        const targetSelect = searchForm.querySelector(SELECTOR.SEARCH_TARGET);
        const keywordInput = searchForm.querySelector(SELECTOR.SEARCH_KEYWORD);

        if (!targetSelect || !keywordInput) return;

        const selectedField = targetSelect.value?.trim() || "";
        const keyword = keywordInput.value.trim();

        paginationForm.querySelectorAll(SELECTOR.PAGINATION_HIDDEN).forEach((input) => {
            const name = input.name;
            const shouldUse = name && name === selectedField && keyword && fieldNames.includes(name);

            input.value = shouldUse ? keyword : "";
            input.disabled = !shouldUse;
        });
    }

    function appendFormParams(url, form) {
        const formData = new FormData(form);

        formData.forEach((value, key) => {
            if (typeof value !== "string") return;

            const trimmedValue = value.trim();
            if (trimmedValue) {
                url.searchParams.set(key, trimmedValue);
            } else {
                url.searchParams.delete(key);
            }
        });
    }

    function buildPaginationUrl(baseUrl, paginationForm) {
        const url = new URL(baseUrl, window.location.origin);
        appendFormParams(url, paginationForm);
        return url.toString();
    }

    function buildSearchUrl(baseUrl, searchForm) {
        const container = getFragmentContainer(searchForm);
        const url = new URL(baseUrl, window.location.origin);

        appendFormParams(url, searchForm);
        url.searchParams.set("page", FIRST_PAGE);

        const size = getCurrentPageSize(container);
        if (size) {
            url.searchParams.set("size", size);
        }

        return url.toString();
    }

    function preserveTableContainerState(container) {
        return {
            hidden: container.classList.contains(CLASS_NAME.HIDDEN),
            opacity: container.style.opacity || "",
            currentSearchTarget: container.dataset[DATA.CURRENT_SEARCH_TARGET] || ""
        };
    }

    function restoreTableContainerState(container, state) {
        container.classList.toggle(CLASS_NAME.HIDDEN, state.hidden);
        container.style.opacity = state.opacity;

        if (state.currentSearchTarget) {
            container.dataset[DATA.CURRENT_SEARCH_TARGET] = state.currentSearchTarget;
        }
    }

    function findNextTableContainer(container, doc) {
        if (container.id) {
            return doc.getElementById(container.id);
        }

        return doc.querySelector(SELECTOR.FRAGMENT_CONTAINER);
    }

    function copyTableContainerDataset(container, nextContainer) {
        if (nextContainer.dataset[DATA.FRAGMENT_URL]) {
            container.dataset[DATA.FRAGMENT_URL] = nextContainer.dataset[DATA.FRAGMENT_URL];
        }
    }

    function replaceTableFragment(container, html, requestUrl) {
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, "text/html");
        const nextContainer = findNextTableContainer(container, doc);

        if (!nextContainer) {
            throw new Error("올바른 테이블 fragment 응답 구조를 찾을 수 없습니다.");
        }

        const state = preserveTableContainerState(container);

        container.className = nextContainer.className;
        container.innerHTML = nextContainer.innerHTML;

        copyTableContainerDataset(container, nextContainer);

        if (requestUrl) {
            container.dataset[DATA.CURRENT_URL] = requestUrl;
        }

        restoreTableContainerState(container, state);
        initializeTableLayout(container);

        container.dispatchEvent(new CustomEvent(EVENT_NAME.UPDATED, {
            bubbles: true,
            detail: { container, requestUrl }
        }));
    }

    async function fetchTableFragment(requestUrl) {
        const response = await fetch(requestUrl, {
            method: "GET",
            headers: {
                "X-Requested-With": REQUEST_HEADER.AJAX
            }
        });

        if (!response.ok) {
            throw new Error("서버 응답 실패");
        }

        return response.text();
    }

    async function reloadTableFragment(target, requestUrl) {
        const container = resolveElement(target);

        if (!container) {
            throw new Error("테이블 fragment 컨테이너를 찾을 수 없습니다.");
        }

        if (!container.matches(SELECTOR.FRAGMENT_CONTAINER)) {
            throw new Error("data-fragment-url이 있는 테이블 컨테이너만 갱신할 수 있습니다.");
        }

        const url = requestUrl
            ? toAbsoluteUrl(requestUrl)
            : toAbsoluteUrl(container.dataset[DATA.FRAGMENT_URL]);

        container.style.opacity = "0.5";

        try {
            const html = await fetchTableFragment(url);
            replaceTableFragment(container, html, url);
            return true;
        } finally {
            container.style.opacity = "1";
        }
    }

    async function submitAjaxSearch(searchForm) {
        const container = getFragmentContainer(searchForm);
        if (!container) {
            throw new Error("AJAX 검색 테이블 컨테이너를 찾을 수 없습니다.");
        }

        const baseUrl = getFormBaseUrl(searchForm);
        const requestUrl = buildSearchUrl(baseUrl, searchForm);

        return reloadTableFragment(container, requestUrl);
    }

    async function submitAjaxPagination(paginationForm) {
        const container = getFragmentContainer(paginationForm);
        if (!container) {
            throw new Error("AJAX 페이지네이션 테이블 컨테이너를 찾을 수 없습니다.");
        }

        const baseUrl = getFormBaseUrl(paginationForm);
        const requestUrl = buildPaginationUrl(baseUrl, paginationForm);

        return reloadTableFragment(container, requestUrl);
    }

    function getClientPaginationState(pagination) {
        return clientPaginationStateMap.get(pagination) || {};
    }

    function setClientPaginationState(pagination, state) {
        clientPaginationStateMap.set(pagination, state);
    }

    function createClientPaginationButton(options) {
        const {
            text,
            page,
            disabled = false,
            active = false,
            isNumberButton = false
        } = options;

        const button = document.createElement("button");
        button.type = "button";
        button.className = "table-layout__page-button";
        button.textContent = text;
        button.disabled = disabled;
        button.dataset.page = String(page);
        button.setAttribute("data-table-pagination-client-button", "");

        if (isNumberButton) {
            button.setAttribute("data-table-pagination-number-button", "");
        }

        if (active) {
            button.classList.add("table-layout__page-button--active");
            button.setAttribute("aria-current", "page");
        }

        return button;
    }

    function buildClientPageRange(currentPage, totalPages, pageBlockSize) {
        const startPage = currentPage - (currentPage % pageBlockSize);
        const endPage = Math.min(startPage + pageBlockSize - 1, totalPages - 1);

        return {
            startPage,
            endPage
        };
    }

    function renderClientPagination(target, options = {}) {
        const pagination = resolvePaginationElement(target);
        if (!pagination) return false;
        if (!isClientPagination(pagination)) return false;

        const nav = pagination.querySelector(SELECTOR.PAGINATION_CLIENT_NAV) ||
            pagination.querySelector(".table-layout__pagination-nav");

        if (!nav) return false;

        const previousState = getClientPaginationState(pagination);

        const rawCurrentPage = options.currentPage ?? pagination.dataset[DATA.CURRENT_PAGE] ?? 0;
        const rawTotalPages = options.totalPages ?? pagination.dataset[DATA.TOTAL_PAGES] ?? 0;
        const rawPageSize = options.pageSize ?? pagination.dataset[DATA.PAGE_SIZE] ?? 5;
        const rawPageBlockSize = options.pageBlockSize ?? pagination.dataset[DATA.PAGE_BLOCK_SIZE] ?? DEFAULT_PAGE_BLOCK_SIZE;

        const totalPages = Math.max(0, toSafeNumber(rawTotalPages, 0));
        const currentPage = totalPages > 0
            ? Math.min(Math.max(0, toSafeNumber(rawCurrentPage, 0)), totalPages - 1)
            : 0;
        const pageSize = Math.max(1, toSafeNumber(rawPageSize, 5));
        const pageBlockSize = Math.max(1, toSafeNumber(rawPageBlockSize, DEFAULT_PAGE_BLOCK_SIZE));

        pagination.dataset[DATA.CURRENT_PAGE] = String(currentPage);
        pagination.dataset[DATA.TOTAL_PAGES] = String(totalPages);
        pagination.dataset[DATA.PAGE_SIZE] = String(pageSize);
        pagination.dataset[DATA.PAGE_BLOCK_SIZE] = String(pageBlockSize);

        setClientPaginationState(pagination, {
            onPageChange: typeof options.onPageChange === "function"
                ? options.onPageChange
                : previousState.onPageChange || null
        });

        nav.innerHTML = "";

        if (totalPages === 0) {
            pagination.classList.add(CLASS_NAME.EMPTY_PAGINATION);

            nav.appendChild(createClientPaginationButton({
                text: "이전",
                page: 0,
                disabled: true
            }));

            nav.appendChild(createClientPaginationButton({
                text: "1",
                page: 0,
                disabled: true,
                active: true,
                isNumberButton: true
            }));

            nav.appendChild(createClientPaginationButton({
                text: "다음",
                page: 0,
                disabled: true
            }));

            return true;
        }

        pagination.classList.remove(CLASS_NAME.EMPTY_PAGINATION);

        const { startPage, endPage } = buildClientPageRange(currentPage, totalPages, pageBlockSize);

        nav.appendChild(createClientPaginationButton({
            text: "이전",
            page: Math.max(0, currentPage - 1),
            disabled: currentPage === 0
        }));

        for (let pageNumber = startPage; pageNumber <= endPage; pageNumber += 1) {
            nav.appendChild(createClientPaginationButton({
                text: String(pageNumber + 1),
                page: pageNumber,
                disabled: pageNumber === currentPage,
                active: pageNumber === currentPage,
                isNumberButton: true
            }));
        }

        nav.appendChild(createClientPaginationButton({
            text: "다음",
            page: Math.min(totalPages - 1, currentPage + 1),
            disabled: currentPage >= totalPages - 1
        }));

        return true;
    }

    function initializeClientPagination(pagination) {
        if (!pagination || !isClientPagination(pagination)) return;
        renderClientPagination(pagination);
    }

    function initializeTableLayout(root) {
        root.querySelectorAll(SELECTOR.SEARCH_FORM).forEach((form) => {
            initializeSearchForm(form);
        });

        root.querySelectorAll(SELECTOR.PAGINATION_FORM).forEach((form) => {
            const container = getFragmentContainer(form);
            syncPaginationSearchParams(container);
            syncPaginationHiddenInputs(form);
        });

        root.querySelectorAll(SELECTOR.PAGINATION).forEach((pagination) => {
            initializeClientPagination(pagination);
        });
    }

    async function handleSearchSubmit(event, searchForm) {
        prepareSearchForm(searchForm);

        const container = getFragmentContainer(searchForm);
        if (container) {
            clearPaginationPageToFirst(container);
            syncPaginationSearchParams(container);
        }

        if (!isAjaxSearchForm(searchForm)) return;

        event.preventDefault();

        if (container && container.dataset[DATA.SUBMITTING] === "true") return;

        try {
            if (container) container.dataset[DATA.SUBMITTING] = "true";
            await submitAjaxSearch(searchForm);
        } catch (error) {
            console.error("[Search Error]:", error);
            alert("데이터를 불러오는 중 오류가 발생했습니다.");
        } finally {
            if (container) container.dataset[DATA.SUBMITTING] = "false";
        }
    }

    async function handlePaginationSubmit(event, paginationForm) {
        const submitter = event.submitter || document.activeElement;
        setPaginationPage(paginationForm, submitter);

        const container = getFragmentContainer(paginationForm);
        if (container) {
            syncPaginationSearchParams(container);
        }

        if (!isAjaxPaginationForm(paginationForm)) {
            syncPaginationHiddenInputs(paginationForm);
            return;
        }

        event.preventDefault();

        if (container && container.dataset[DATA.SUBMITTING] === "true") return;

        syncPaginationHiddenInputs(paginationForm);

        try {
            if (container) container.dataset[DATA.SUBMITTING] = "true";
            await submitAjaxPagination(paginationForm);
        } catch (error) {
            console.error("[Pagination Error]:", error);
            alert("데이터를 불러오는 중 오류가 발생했습니다.");
        } finally {
            if (container) container.dataset[DATA.SUBMITTING] = "false";
        }
    }

    function handleClientPaginationClick(event) {
        const button = event.target.closest(SELECTOR.PAGINATION_CLIENT_BUTTON);
        if (!button) return;

        const pagination = button.closest(SELECTOR.PAGINATION);
        if (!pagination || !isClientPagination(pagination) || button.disabled) return;

        event.preventDefault();

        const nextPage = Math.max(0, toSafeNumber(button.dataset.page, 0));
        const state = getClientPaginationState(pagination);

        if (typeof state.onPageChange === "function") {
            state.onPageChange(nextPage, pagination);
        }

        pagination.dispatchEvent(new CustomEvent(EVENT_NAME.CLIENT_PAGE_CHANGE, {
            bubbles: true,
            detail: {
                page: nextPage,
                pagination
            }
        }));
    }

    function bindSubmitEvents() {
        document.addEventListener("submit", async function (event) {
            const target = event.target;
            const searchForm = target.closest(SELECTOR.SEARCH_FORM);

            if (searchForm) {
                await handleSearchSubmit(event, searchForm);
                return;
            }

            const paginationForm = target.closest(SELECTOR.PAGINATION_FORM);
            if (paginationForm) {
                await handlePaginationSubmit(event, paginationForm);
            }
        });
    }

    function bindClickEvents() {
        document.addEventListener("click", handleClientPaginationClick);
    }

    function initAll(root) {
        initializeTableLayout(root);
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => initAll(document));
    } else {
        initAll(document);
    }

    bindSubmitEvents();
    bindClickEvents();

    window.TableLayout = Object.freeze({
        MODE: TABLE_MODE,
        EVENT: EVENT_NAME,
        initialize: initializeTableLayout,
        reload: reloadTableFragment,
        renderClientPagination
    });

    window.initializeTableLayout = initializeTableLayout;
})();