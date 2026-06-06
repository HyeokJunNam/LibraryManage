document.addEventListener("DOMContentLoaded", function () {
    const searchForm = document.getElementById("searchForm");
    const conditionElement = document.getElementById("condition");
    const keywordElement = document.getElementById("keyword");
    const resetButton = document.getElementById("resetBtn");

    if (!searchForm || !conditionElement || !keywordElement) {
        return;
    }

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

    if (resetButton) {
        resetButton.addEventListener("click", function () {
            conditionElement.selectedIndex = 0;
            keywordElement.value = "";
            keywordElement.focus();
        });
    }
});