export function renderAsyncPagination(container, options) {
    if (!container) return;

    const currentPage = normalizePage(options.currentPage);
    const totalPages = Math.max(1, normalizeTotalPages(options.totalPages));
    const visiblePages = normalizeVisiblePages(options.visiblePages);
    const onPageChange = typeof options.onPageChange === "function"
        ? options.onPageChange
        : null;

    container.innerHTML = "";

    const nav = document.createElement("nav");
    nav.className = "pagination";
    nav.setAttribute("aria-label", "페이지 이동");

    nav.appendChild(
        createPageButton({
            label: "이전",
            targetPage: currentPage - 1,
            disabled: currentPage <= 0,
            className: "pagination__button",
            onPageChange
        })
    );

    const { startPage, endPage } = calculatePageRange(currentPage, totalPages, visiblePages);

    if (startPage > 0) {
        nav.appendChild(
            createPageButton({
                label: "1",
                targetPage: 0,
                className: "pagination__number",
                onPageChange
            })
        );

        if (startPage > 1) {
            nav.appendChild(createEllipsis());
        }
    }

    for (let page = startPage; page <= endPage; page += 1) {
        nav.appendChild(
            createPageButton({
                label: String(page + 1),
                targetPage: page,
                className: page === currentPage
                    ? "pagination__number pagination__number--active"
                    : "pagination__number",
                ariaCurrent: page === currentPage ? "page" : null,
                onPageChange
            })
        );
    }

    if (endPage < totalPages - 1) {
        if (endPage < totalPages - 2) {
            nav.appendChild(createEllipsis());
        }

        nav.appendChild(
            createPageButton({
                label: String(totalPages),
                targetPage: totalPages - 1,
                className: "pagination__number",
                onPageChange
            })
        );
    }

    nav.appendChild(
        createPageButton({
            label: "다음",
            targetPage: currentPage + 1,
            disabled: currentPage >= totalPages - 1,
            className: "pagination__button",
            onPageChange
        })
    );

    container.appendChild(nav);
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

function createPageButton({
                              label,
                              targetPage,
                              disabled = false,
                              className,
                              ariaCurrent = null,
                              onPageChange
                          }) {
    if (disabled) {
        const span = document.createElement("span");
        span.className = `${className} pagination__button--disabled`;
        span.textContent = label;
        span.setAttribute("aria-disabled", "true");
        return span;
    }

    const button = document.createElement("button");
    button.type = "button";
    button.className = className;
    button.textContent = label;
    button.dataset.page = String(targetPage);

    if (ariaCurrent) {
        button.setAttribute("aria-current", ariaCurrent);
    }

    button.addEventListener("click", () => {
        if (typeof onPageChange === "function") {
            onPageChange(targetPage);
        }
    });

    return button;
}

function createEllipsis() {
    const span = document.createElement("span");
    span.className = "pagination__ellipsis";
    span.textContent = "...";
    span.setAttribute("aria-hidden", "true");
    return span;
}