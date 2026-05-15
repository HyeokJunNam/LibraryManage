document.addEventListener("click", (event) => {
    const pageButton = event.target.closest("[data-page]");

    if (!pageButton) {
        return;
    }

    const paginationArea = findPaginationArea(pageButton);

    if (!paginationArea) {
        return;
    }

    if (isDisabledPageButton(pageButton)) {
        return;
    }

    const page = Number(pageButton.dataset.page);

    if (!Number.isFinite(page) || page < 0) {
        return;
    }

    event.preventDefault();

    const mode = paginationArea.dataset.paginationMode || "link";

    if (mode === "link") {
        movePageByLink(paginationArea, page);
        return;
    }

    if (mode === "ajax") {
        dispatchAjaxPageEvent(paginationArea, page);
    }
});

function findPaginationArea(pageButton) {
    return pageButton.closest("[data-pagination-mode]")
        || pageButton.closest(".table-block__pagination");
}

function isDisabledPageButton(pageButton) {
    return pageButton.disabled
        || pageButton.classList.contains("pagination__button--disabled")
        || pageButton.classList.contains("pagination__number--active");
}

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