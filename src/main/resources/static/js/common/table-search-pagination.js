/**
 * 공통 테이블 레이아웃 비동기/동기 검색 및 페이지네이션 스크립트
 */
(function () {
    if (window.__tableLayoutInitialized) return;
    window.__tableLayoutInitialized = true;

    const TABLE_MODE = Object.freeze({
        SERVER: "server",
        AJAX: "ajax"
    });

    const DATA = Object.freeze({
        FRAGMENT_URL: "fragmentUrl",
        CURRENT_URL: "currentUrl",
        CURRENT_SEARCH_TARGET: "currentSearchTarget",
        SEARCH_MODE: "searchMode",
        PAGINATION_MODE: "paginationMode",
        SUBMITTING: "isSubmitting" // [최적화] 속성 키 추가
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
        PAGINATION_HIDDEN: "[data-table-pagination-hidden]"
    });

    const CLASS_NAME = Object.freeze({
        HIDDEN: "is-hidden"
    });

    const EVENT_NAME = Object.freeze({
        UPDATED: "table-layout:updated"
    });

    const REQUEST_HEADER = Object.freeze({
        AJAX: "XMLHttpRequest"
    });

    const FIRST_PAGE = "0";

    function resolveElement(target) {
        if (!target) return null;
        if (typeof target === "string") return document.querySelector(target);
        if (target instanceof Element) return target;
        return null;
    }

    function getFragmentContainer(element) {
        return element?.closest?.(SELECTOR.FRAGMENT_CONTAINER) || null;
    }

    function normalizeMode(value) {
        return value === TABLE_MODE.AJAX ? TABLE_MODE.AJAX : TABLE_MODE.SERVER;
    }

    function isAjaxMode(value) {
        return normalizeMode(value) === TABLE_MODE.AJAX;
    }

    function getToolbarMode(form) {
        const toolbar = form.closest(SELECTOR.TOOLBAR);
        return normalizeMode(toolbar?.dataset[DATA.SEARCH_MODE]);
    }

    function getPaginationMode(form) {
        const pagination = form.closest(SELECTOR.PAGINATION);
        return normalizeMode(pagination?.dataset[DATA.PAGINATION_MODE]);
    }

    function isAjaxSearchForm(form) {
        return isAjaxMode(getToolbarMode(form));
    }

    function isAjaxPaginationForm(form) {
        return isAjaxMode(getPaginationMode(form));
    }

    function toAbsoluteUrl(url) {
        return new URL(url, window.location.origin).toString();
    }

    function getContainerCurrentUrl(container) {
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

        // [최적화] 검색 폼이 존재하지 않는 테이블 구조라면 조기 종료하여 과도한 연산 차단
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
        if (!container) throw new Error("AJAX 검색 테이블 컨테이너를 찾을 수 없습니다.");

        const baseUrl = getFormBaseUrl(searchForm);
        const requestUrl = buildSearchUrl(baseUrl, searchForm);

        return reloadTableFragment(container, requestUrl);
    }

    async function submitAjaxPagination(paginationForm) {
        const container = getFragmentContainer(paginationForm);
        if (!container) throw new Error("AJAX 페이지네이션 테이블 컨테이너를 찾을 수 없습니다.");

        const baseUrl = getFormBaseUrl(paginationForm);
        const requestUrl = buildPaginationUrl(baseUrl, paginationForm);

        return reloadTableFragment(container, requestUrl);
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

        // [최적화] 전역 클로저 변수 대신 각 컨테이너 고유 데이터 속성으로 서브밋 상태 제어 (멀티 컴포넌트 안전 보장)
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

        // [최적화] 컨테이너 기반으로 중복 서브밋 방지 분리
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

    function initAll(root) {
        initializeTableLayout(root);
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => initAll(document));
    } else {
        initAll(document);
    }

    bindSubmitEvents();

    window.TableLayout = Object.freeze({
        MODE: TABLE_MODE,
        EVENT: EVENT_NAME,
        initialize: initializeTableLayout,
        reload: reloadTableFragment
    });

    window.initializeTableLayout = initializeTableLayout;
})();