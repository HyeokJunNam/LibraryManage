document.addEventListener("DOMContentLoaded", () => {
    initializeMemberDetailPage();
});

function initializeMemberDetailPage() {
    setupBackLinkState();
    setupKeyboardAccessibility();
}

function setupBackLinkState() {
    const backLink = document.querySelector(".btn--secondary");

    if (!backLink) {
        return;
    }

    const previousPath = document.referrer ? new URL(document.referrer).pathname : "";

    if (previousPath === "/admin/members") {
        backLink.dataset.fromList = "true";
    }
}

function setupKeyboardAccessibility() {
    const infoCard = document.querySelector(".info-card");

    if (!infoCard) {
        return;
    }

    infoCard.setAttribute("tabindex", "0");
}