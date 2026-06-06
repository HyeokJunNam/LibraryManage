document.addEventListener("DOMContentLoaded", function () {
    const currentListUrl = window.location.pathname + window.location.search;
    sessionStorage.setItem("bookListReturnUrl", currentListUrl);

    const searchForm = document.getElementById("searchForm");
    const conditionElement = document.getElementById("condition");
    const keywordElement = document.getElementById("keyword");
    const resetButton = document.getElementById("resetBtn");

    if (!searchForm || !conditionElement || !keywordElement) {
        return;
    }

    restoreSearchForm(conditionElement, keywordElement);
    bindSearchSubmit(searchForm, conditionElement, keywordElement);
    bindResetButton(resetButton, conditionElement, keywordElement);
});

function restoreSearchForm(conditionElement, keywordElement) {
    const searchParams = new URLSearchParams(window.location.search);
    const options = Array.from(conditionElement.options);

    const selectedOption = options.find(function (option) {
        return searchParams.has(option.value);
    });

    if (!selectedOption) {
        conditionElement.selectedIndex = 0;
        keywordElement.value = "";
        return;
    }

    conditionElement.value = selectedOption.value;
    keywordElement.value = searchParams.get(selectedOption.value) ?? "";
}

function bindSearchSubmit(searchForm, conditionElement, keywordElement) {
    searchForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const fieldName = conditionElement.value;
        const keyword = keywordElement.value.trim();

        if (keyword === "") {
            window.location.href = searchForm.action;
            return;
        }

        const params = new URLSearchParams();
        params.set(fieldName, keyword);

        window.location.href = `${searchForm.action}?${params.toString()}`;
    });
}

function bindResetButton(resetButton, conditionElement, keywordElement) {
    if (!resetButton) {
        return;
    }

    resetButton.addEventListener("click", function () {
        conditionElement.selectedIndex = 0;
        keywordElement.value = "";
        keywordElement.focus();
    });
}