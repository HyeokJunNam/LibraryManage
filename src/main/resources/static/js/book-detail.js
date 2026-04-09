document.addEventListener("DOMContentLoaded", () => {
    initializeBackToListButton();
    bindDetailPageEvents();
});

function initializeBackToListButton() {
    const backButton = getElement("back-to-list-button");

    if (!backButton) {
        return;
    }

    const returnUrl = resolveReturnUrl();
    backButton.setAttribute("href", returnUrl);

    backButton.addEventListener("click", (event) => {
        event.preventDefault();
        window.location.href = returnUrl;
    });
}

function bindDetailPageEvents() {
    const notifyButton = getElement("notify-button");

    if (!notifyButton) {
        return;
    }

    notifyButton.addEventListener("click", () => {
        if (notifyButton.disabled) {
            return;
        }

        window.alert("알림 신청 기능은 준비 중입니다.");
    });
}

function resolveReturnUrl() {
    const queryReturnUrl = getReturnUrlFromQuery();
    if (isValidInternalListUrl(queryReturnUrl)) {
        return saveBookListReturnUrl(queryReturnUrl);
    }

    const savedReturnUrl = sessionStorage.getItem("bookListReturnUrl");
    if (isValidInternalListUrl(savedReturnUrl)) {
        return savedReturnUrl;
    }

    const referrerReturnUrl = getReturnUrlFromReferrer();
    if (isValidInternalListUrl(referrerReturnUrl)) {
        return saveBookListReturnUrl(referrerReturnUrl);
    }

    return "/library/books";
}

function getReturnUrlFromQuery() {
    const params = new URLSearchParams(window.location.search);
    return params.get("returnUrl");
}

function getReturnUrlFromReferrer() {
    const referrer = document.referrer;

    if (!referrer) {
        return null;
    }

    try {
        const referrerUrl = new URL(referrer, window.location.origin);

        if (referrerUrl.origin !== window.location.origin) {
            return null;
        }

        return referrerUrl.pathname + referrerUrl.search + referrerUrl.hash;
    } catch (error) {
        console.error(error);
        return null;
    }
}

function isValidInternalListUrl(url) {
    if (!url || typeof url !== "string") {
        return false;
    }

    const trimmed = url.trim();

    if (!trimmed.startsWith("/") || trimmed.startsWith("//")) {
        return false;
    }

    return trimmed === "/library/books" || trimmed.startsWith("/library/books?");
}

function saveBookListReturnUrl(url) {
    sessionStorage.setItem("bookListReturnUrl", url);
    return url;
}

function getElement(role) {
    return document.querySelector(`[data-role="${role}"]`);
}