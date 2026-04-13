document.addEventListener("DOMContentLoaded", () => {
    saveCurrentPageForLogin();
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
        if (notifyButton.disabled || notifyButton.dataset.completed === "true") {
            return;
        }

        const bookId = getBookId();

        if (!bookId) {
            openAlertModal({
                title: "오류",
                message: "도서 정보를 확인할 수 없습니다.",
                confirmText: "확인"
            });
            return;
        }

        openNotifyConfirmModal(notifyButton, bookId);
    });
}

function openNotifyConfirmModal(notifyButton, bookId) {
    openAlertModal({
        title: "알림 신청",
        message: "알림을 신청하시겠습니까?",
        confirmText: "신청",
        cancelText: "취소",
        onConfirm: async () => {
            try {
                await submitNotificationRequest(notifyButton, bookId);
                showNotificationRequestSuccess();
            } catch (error) {
                showNotificationRequestError(error);
            }
        }
    });
}

async function submitNotificationRequest(notifyButton, bookId) {
    const originalHtml = notifyButton.innerHTML;

    try {
        setNotifyButtonLoading(notifyButton);

        await apiPost(`/api/books/${bookId}/notifications`, {
            channel: "EMAIL",
            type: "BORROW"
        });

        applyNotifyCompletedState(notifyButton);
    } catch (error) {
        console.error(error);
        restoreNotifyButton(notifyButton, originalHtml);
        throw error;
    }
}

function showNotificationRequestSuccess() {
    openAlertModal({
        title: "알림 신청 완료",
        message: "신청이 완료되었습니다.",
        confirmText: "확인"
    });
}

function showNotificationRequestError(error) {
    const message = error?.message || "요청 처리 중 오류가 발생했습니다.";

    if (error?.status === 401) {
        openAlertModal({
            title: "회원만 신청 가능합니다.",
            message,
            confirmText: "로그인",
            cancelText: "취소",
            onConfirm: () => {
                window.location.href = buildLoginUrl();
            }
        });
        return;
    }

    openAlertModal({
        title: "알림 신청 실패",
        message,
        confirmText: "확인"
    });
}

function getBookId() {
    const page = document.querySelector(".page");
    const bookId = page?.dataset?.bookId;

    if (!bookId || String(bookId).trim() === "") {
        return null;
    }

    return bookId;
}

function setNotifyButtonLoading(button) {
    button.disabled = true;
    button.innerHTML = `
        <span class="notify-btn__icon" aria-hidden="true">⏳</span>
        <span class="notify-btn__text">신청 중...</span>
    `;
}

function restoreNotifyButton(button, originalHtml) {
    button.disabled = false;
    button.innerHTML = originalHtml;
}

function applyNotifyCompletedState(button) {
    button.disabled = true;
    button.dataset.completed = "true";
    button.classList.add("notify-btn--done");
    button.innerHTML = `
        <span class="notify-btn__icon" aria-hidden="true">✅</span>
        <span class="notify-btn__text">신청 완료</span>
    `;
}

function saveCurrentPageForLogin() {
    const currentPageUrl = window.location.pathname + window.location.search + window.location.hash;

    if (isValidInternalPageUrl(currentPageUrl)) {
        sessionStorage.setItem("loginReturnUrl", currentPageUrl);
    }
}

function buildLoginUrl() {
    const savedReturnUrl = sessionStorage.getItem("loginReturnUrl");
    const returnUrl = isValidInternalPageUrl(savedReturnUrl)
        ? savedReturnUrl
        : "/library/books";

    return `/login?returnUrl=${encodeURIComponent(returnUrl)}`;
}

function isValidInternalPageUrl(url) {
    if (!url || typeof url !== "string") {
        return false;
    }

    const trimmed = url.trim();

    if (!trimmed.startsWith("/") || trimmed.startsWith("//")) {
        return false;
    }

    return !trimmed.startsWith("/api/");
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