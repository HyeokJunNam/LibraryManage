// auto-init pagination module

export function renderPagination(container, options) {
    if (!container) {
        return;
    }

    const currentPage = normalizePage(options.currentPage);
    const totalPages = normalizeTotalPages(options.totalPages);
    const visiblePages = normalizeVisiblePages(options.visiblePages);
    const baseUrl = resolveBaseUrl(options.baseUrl);
    const currentSearch = resolveSearch(options.search);

    container.innerHTML = "";

    const nav = document.createElement("nav");
    nav.className = "pagination";
    nav.setAttribute("aria-label", "페이지 이동");

    nav.appendChild(
        createPageButton({
            label: "이전",
            targetPage: currentPage - 1,
            disabled: currentPage <= 0,
            baseUrl,
            search: currentSearch,
            className: "pagination__button"
        })
    );

    const { startPage, endPage } = calculatePageRange(currentPage, totalPages, visiblePages);

    if (startPage > 0) {
        nav.appendChild(
            createPageLink({
                label: "1",
                targetPage: 0,
                baseUrl,
                search: currentSearch,
                className: "pagination__number"
            })
        );

        if (startPage > 1) {
            nav.appendChild(createEllipsis());
        }
    }

    for (let page = startPage; page <= endPage; page += 1) {
        nav.appendChild(
            createPageLink({
                label: String(page + 1),
                targetPage: page,
                baseUrl,
                search: currentSearch,
                className: page === currentPage
                    ? "pagination__number pagination__number--active"
                    : "pagination__number",
                ariaCurrent: page === currentPage ? "page" : null
            })
        );
    }

    if (endPage < totalPages - 1) {
        if (endPage < totalPages - 2) {
            nav.appendChild(createEllipsis());
        }

        nav.appendChild(
            createPageLink({
                label: String(totalPages),
                targetPage: totalPages - 1,
                baseUrl,
                search: currentSearch,
                className: "pagination__number"
            })
        );
    }

    nav.appendChild(
        createPageButton({
            label: "다음",
            targetPage: currentPage + 1,
            disabled: currentPage >= totalPages - 1,
            baseUrl,
            search: currentSearch,
            className: "pagination__button"
        })
    );

    container.appendChild(nav);
}

function initPagination(root = document) {
    restorePaginationScroll();

    const elements = root.querySelectorAll("[data-pagination]");

    elements.forEach((el) => {
        renderPagination(el, {
            currentPage: el.dataset.currentPage,
            totalPages: el.dataset.totalPages,
            baseUrl: el.dataset.baseUrl,
            search: window.location.search,
            visiblePages: el.dataset.visiblePages || 5
        });
    });
}

document.addEventListener("DOMContentLoaded", () => {
    initPagination();
});

function restorePaginationScroll() {
    const savedY = sessionStorage.getItem("pagination-scroll-y");

    if (!savedY) {
        return;
    }

    sessionStorage.removeItem("pagination-scroll-y");

    window.requestAnimationFrame(() => {
        window.scrollTo(0, Number(savedY));
    });
}

function normalizePage(page) {
    const value = Number(page);
    return Number.isInteger(value) && value >= 0 ? value : 0;
}

function normalizeTotalPages(totalPages) {
    const value = Number(totalPages);
    return Number.isInteger(value) && value >= 0 ? value : 0;
}

function normalizeVisiblePages(visiblePages) {
    const value = Number(visiblePages);
    return Number.isInteger(value) && value > 0 ? value : 5;
}

function resolveBaseUrl(baseUrl) {
    return baseUrl && baseUrl.trim() !== "" ? baseUrl : window.location.pathname;
}

function resolveSearch(search) {
    return typeof search === "string" ? search : window.location.search;
}

function calculatePageRange(currentPage, totalPages, visiblePages) {
    const half = Math.floor(visiblePages / 2);
    let startPage = Math.max(0, currentPage - half);
    let endPage = startPage + visiblePages - 1;

    if (endPage >= totalPages) {
        endPage = totalPages - 1;
        startPage = Math.max(0, endPage - visiblePages + 1);
    }

    return { startPage, endPage };
}

function createPageButton({ label, targetPage, disabled, baseUrl, search, className }) {
    if (disabled) {
        const span = document.createElement("span");
        span.className = `${className} pagination__button--disabled`;
        span.textContent = label;
        span.setAttribute("aria-disabled", "true");
        return span;
    }

    return createPageLink({ label, targetPage, baseUrl, search, className });
}

function createPageLink({ label, targetPage, baseUrl, search, className, ariaCurrent = null }) {
    const link = document.createElement("a");
    link.className = className;
    link.textContent = label;
    link.href = buildPageUrl(baseUrl, search, targetPage);

    link.addEventListener("click", () => {
        sessionStorage.setItem("pagination-scroll-y", String(window.scrollY));
    });

    if (ariaCurrent) {
        link.setAttribute("aria-current", ariaCurrent);
    }

    return link;
}

function createEllipsis() {
    const span = document.createElement("span");
    span.className = "pagination__ellipsis";
    span.textContent = "...";
    span.setAttribute("aria-hidden", "true");
    return span;
}

function buildPageUrl(baseUrl, search, page) {
    const params = new URLSearchParams(search || "");
    params.set("page", String(page));
    const query = params.toString();
    return query ? `${baseUrl}?${query}` : baseUrl;
}