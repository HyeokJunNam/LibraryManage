document.addEventListener("DOMContentLoaded", function () {
    const currentListUrl = window.location.pathname + window.location.search;
    sessionStorage.setItem("bookListReturnUrl", currentListUrl);

    const searchForm = document.getElementById("searchForm");
    const conditionElement = document.getElementById("condition");
    const keywordElement = document.getElementById("keyword");

    if (!searchForm || !conditionElement || !keywordElement) {
        return;
    }

    syncSearchFieldsFromQuery(conditionElement, keywordElement);
    bindSearchSubmit(searchForm, conditionElement, keywordElement);
});

function syncSearchFieldsFromQuery(conditionElement, keywordElement) {
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
}

function bindSearchSubmit(searchForm, conditionElement, keywordElement) {
    searchForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const condition = conditionElement.value;
        const keyword = keywordElement.value.trim();
        const params = new URLSearchParams();

        if (keyword !== "") {
            params.set(condition, keyword);
        }

        const queryString = params.toString();
        window.location.href = queryString ? `/library/books?${queryString}` : "/library/books";
    });
}