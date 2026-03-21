document.addEventListener("DOMContentLoaded", function () {
    const searchForm = document.getElementById("searchForm");
    const conditionElement = document.getElementById("condition");
    const keywordElement = document.getElementById("keyword");

    if (!searchForm || !conditionElement || !keywordElement) {
        return;
    }

    const searchParams = new URLSearchParams(window.location.search);
    const searchKeys = ["title", "isbn", "author", "publisher"];

    let selectedKey = "title";
    let selectedValue = "";

    for (const key of searchKeys) {
        const value = searchParams.get(key);
        if (value !== null && value.trim() !== "") {
            selectedKey = key;
            selectedValue = value;
            break;
        }
    }

    conditionElement.value = selectedKey;
    keywordElement.value = selectedValue;

    searchForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const condition = conditionElement.value;
        const keyword = keywordElement.value.trim();

        if (!keyword) {
            window.location.href = "/";
            return;
        }

        const params = new URLSearchParams();

        params.set(condition, keyword);

        const currentPage = searchParams.get("page");
        if (currentPage !== null) {
            params.set("page", "0");
        }

        window.location.href = `/?${params.toString()}`;
    });

    const paginationLinks = document.querySelectorAll(".pagination a[href]");

    paginationLinks.forEach((link) => {
        const href = link.getAttribute("href");
        if (!href) {
            return;
        }

        const url = new URL(href, window.location.origin);

        if (selectedValue) {
            url.searchParams.set(selectedKey, selectedValue);
        }

        link.setAttribute("href", url.pathname + url.search);
    });
});