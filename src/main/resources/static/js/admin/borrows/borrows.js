// js/admin/borrows/borrows.js

const PANEL_SELECTOR = "[data-fragment-url]";
const SEARCH_FORM_SELECTOR = "[data-table-search-form]";
const RESET_LINK_SELECTOR = "[data-table-reset-link]";
const PAGINATION_SELECTOR = ".table-block__pagination";
const PAGE_TARGET_SELECTOR = "a[href], button[data-page], [data-page]";

const SEARCH_TARGET_SELECTOR = "[data-borrow-search-target]";
const SEARCH_KEYWORD_SELECTOR = "[data-borrow-search-keyword]";
const SEARCH_BOOK_TITLE_SELECTOR = "[data-borrow-search-book-title]";
const SEARCH_MEMBER_NAME_SELECTOR = "[data-borrow-search-member-name]";

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(PANEL_SELECTOR).forEach((panel) => {
        const fragmentUrl = panel.dataset.fragmentUrl;

        if (!fragmentUrl) {
            return;
        }

        loadPanel(panel, fragmentUrl);
    });
});

document.addEventListener("submit", (event) => {
    const form = event.target.closest(SEARCH_FORM_SELECTOR);

    if (!form) {
        return;
    }

    event.preventDefault();

    syncBorrowSearchFields(form);

    const panel = form.closest(PANEL_SELECTOR);

    if (!panel) {
        return;
    }

    const url = buildUrlFromForm(form);

    loadPanel(panel, url);
});

document.addEventListener("click", (event) => {
    const resetLink = event.target.closest(RESET_LINK_SELECTOR);

    if (resetLink) {
        event.preventDefault();

        const panel = resetLink.closest(PANEL_SELECTOR);

        if (!panel) {
            return;
        }

        const url = resetLink.getAttribute("href") || panel.dataset.fragmentUrl;

        loadPanel(panel, url);
        return;
    }

    const pageTarget = event.target.closest(PAGE_TARGET_SELECTOR);

    if (!pageTarget) {
        return;
    }

    const pagination = pageTarget.closest(PAGINATION_SELECTOR);

    if (!pagination) {
        return;
    }

    if (pagination.dataset.paginationMode !== "ajax") {
        return;
    }

    event.preventDefault();

    const panel = pagination.closest(PANEL_SELECTOR);

    if (!panel) {
        return;
    }

    const url = buildUrlFromPagination(pageTarget, pagination, panel);

    if (!url) {
        return;
    }

    loadPanel(panel, url);
});

async function loadPanel(panel, url) {
    const requestUrl = normalizeUrl(url);

    if (!requestUrl) {
        return;
    }

    setPanelLoading(panel);

    try {
        const response = await fetch(requestUrl, {
            method: "GET",
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        });

        if (!response.ok) {
            throw new Error(`Fragment request failed. status=${response.status}`);
        }

        const html = await response.text();

        replacePanel(panel, html);
    } catch (error) {
        console.error(error);
        setPanelError(panel);
    }
}

function replacePanel(panel, html) {
    const template = document.createElement("template");
    template.innerHTML = html.trim();

    const nextPanel = template.content.querySelector(PANEL_SELECTOR);

    if (nextPanel) {
        panel.replaceWith(nextPanel);
        return;
    }

    panel.innerHTML = html;
}

function setPanelLoading(panel) {
    panel.innerHTML = `
        <div class="borrow-page__loading">
            목록을 불러오는 중입니다.
        </div>
    `;
}

function setPanelError(panel) {
    panel.innerHTML = `
        <div class="ajax-error-card">
            <div class="ajax-error-card__icon">⚠️</div>
            <h3 class="ajax-error-card__title">목록을 불러오지 못했습니다</h3>
            <p class="ajax-error-card__desc">
                잠시 후 다시 시도해주세요.
            </p>
        </div>
    `;
}

function syncBorrowSearchFields(form) {
    const searchTarget = form.querySelector(SEARCH_TARGET_SELECTOR);
    const searchKeyword = form.querySelector(SEARCH_KEYWORD_SELECTOR);
    const bookTitleField = form.querySelector(SEARCH_BOOK_TITLE_SELECTOR);
    const memberNameField = form.querySelector(SEARCH_MEMBER_NAME_SELECTOR);

    if (!searchTarget || !searchKeyword || !bookTitleField || !memberNameField) {
        return;
    }

    bookTitleField.value = "";
    memberNameField.value = "";

    bookTitleField.disabled = true;
    memberNameField.disabled = true;

    const target = searchTarget.value;
    const keyword = searchKeyword.value.trim();

    if (!keyword) {
        return;
    }

    if (target === "memberName") {
        memberNameField.disabled = false;
        memberNameField.value = keyword;
        return;
    }

    bookTitleField.disabled = false;
    bookTitleField.value = keyword;
}

function buildUrlFromForm(form) {
    const action = form.getAttribute("action") || window.location.pathname;
    const url = new URL(action, window.location.origin);

    appendFormParams(url, form);

    url.searchParams.set("page", "0");

    return toRelativeUrl(url);
}

function buildUrlFromPagination(pageTarget, pagination, panel) {
    const href = pageTarget.getAttribute("href");

    if (href && href !== "#") {
        return href;
    }

    const page = pageTarget.dataset.page;

    if (page == null || page === "") {
        return null;
    }

    const baseUrl = pagination.dataset.baseUrl || panel.dataset.fragmentUrl;

    if (!baseUrl) {
        return null;
    }

    const url = new URL(baseUrl, window.location.origin);

    const form = panel.querySelector(SEARCH_FORM_SELECTOR);

    if (form) {
        syncBorrowSearchFields(form);
        appendFormParams(url, form);
    }

    url.searchParams.set("page", page);

    const size = pagination.dataset.pageSize;

    if (size) {
        url.searchParams.set("size", size);
    }

    return toRelativeUrl(url);
}

function appendFormParams(url, form) {
    const formData = new FormData(form);

    for (const [name, value] of formData.entries()) {
        const normalizedValue = String(value).trim();

        url.searchParams.delete(name);

        if (normalizedValue === "") {
            continue;
        }

        url.searchParams.set(name, normalizedValue);
    }
}

function normalizeUrl(url) {
    if (!url) {
        return null;
    }

    const parsedUrl = new URL(url, window.location.origin);

    return toRelativeUrl(parsedUrl);
}

function toRelativeUrl(url) {
    return `${url.pathname}${url.search}`;
}