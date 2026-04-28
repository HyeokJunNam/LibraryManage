document.addEventListener("click", (event) => {
    const pageButton = event.target.closest("[data-page]");

    if (!pageButton) {
        return;
    }

    const paginationArea = pageButton.closest("[data-pagination-mode]");

    if (!paginationArea) {
        return;
    }

    if (
        pageButton.disabled ||
        pageButton.classList.contains("pagination__button--disabled")
    ) {
        return;
    }

    const page = Number(pageButton.dataset.page);

    if (!Number.isFinite(page) || page < 0) {
        return;
    }

    const mode = paginationArea.dataset.paginationMode;

    if (mode === "link") {
        movePageByLink(paginationArea, page);
        return;
    }

    if (mode === "ajax") {
        dispatchAjaxPageEvent(paginationArea, page);
    }
});

function movePageByLink(paginationArea, page) {
    const baseUrl = paginationArea.dataset.baseUrl || window.location.pathname;
    const url = new URL(baseUrl, window.location.origin);

    const currentParams = new URLSearchParams(window.location.search);

    currentParams.forEach((value, key) => {
        url.searchParams.set(key, value);
    });

    url.searchParams.set("page", String(page));

    const size = paginationArea.dataset.pageSize;

    if (size) {
        url.searchParams.set("size", size);
    }

    window.location.href = url.toString();
}

function dispatchAjaxPageEvent(paginationArea, page) {
    const eventName = paginationArea.dataset.paginationEvent || "pagination:change";

    paginationArea.dispatchEvent(new CustomEvent(eventName, {
        bubbles: true,
        detail: {
            page
        }
    }));
}