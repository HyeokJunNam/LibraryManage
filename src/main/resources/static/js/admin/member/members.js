document.addEventListener("DOMContentLoaded", () => {
    setupFilterForm();
    setupMemberRowNavigation();
});

function setupFilterForm() {
    const form = document.getElementById("memberFilterForm");

    if (!form) {
        return;
    }

    form.addEventListener("submit", () => {
        const keywordInput = document.getElementById("keyword");

        if (!keywordInput) {
            return;
        }

        keywordInput.value = keywordInput.value.trim();
    });
}

function setupMemberRowNavigation() {
    const memberRows = document.querySelectorAll(".member-row[data-member-url]");

    if (!memberRows.length) {
        return;
    }

    memberRows.forEach((memberRow) => {
        memberRow.addEventListener("click", (event) => {
            if (shouldIgnoreNavigation(event.target)) {
                return;
            }

            navigateToMemberDetail(memberRow);
        });

        memberRow.addEventListener("keydown", (event) => {
            if (event.key !== "Enter" && event.key !== " ") {
                return;
            }

            event.preventDefault();
            navigateToMemberDetail(memberRow);
        });
    });
}

function navigateToMemberDetail(memberRow) {
    const memberDetailUrl = memberRow.dataset.memberUrl;

    if (!memberDetailUrl) {
        return;
    }

    window.location.href = memberDetailUrl;
}

function shouldIgnoreNavigation(target) {
    if (!(target instanceof Element)) {
        return false;
    }

    return Boolean(target.closest("a, button, input, select, textarea, label"));
}