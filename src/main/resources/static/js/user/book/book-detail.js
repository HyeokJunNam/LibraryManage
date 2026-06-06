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

    initializeNotifyButtonState(notifyButton);
    bindNotifyHoverEvents(notifyButton);
    bindNotifyClickEvent(notifyButton);
}

function initializeNotifyButtonState(notifyButton) {
    if (notifyButton.dataset.completed === "true") {
        notifyButton.disabled = false;
        applyNotifyCompletedState(notifyButton);
        return;
    }

    if (!notifyButton.disabled) {
        applyNotifyRequestEnabledState(notifyButton);
    }
}

function bindNotifyHoverEvents(notifyButton) {
    notifyButton.addEventListener("mouseenter", () => {
        if (notifyButton.disabled || notifyButton.dataset.loading === "true") {
            return;
        }

        if (notifyButton.dataset.completed === "true") {
            applyNotifyCancelHoverState(notifyButton);
        }
    });

    notifyButton.addEventListener("mouseleave", () => {
        if (notifyButton.disabled || notifyButton.dataset.loading === "true") {
            return;
        }

        if (notifyButton.dataset.completed === "true") {
            applyNotifyCompletedState(notifyButton);
        }
    });
}

function bindNotifyClickEvent(notifyButton) {
    notifyButton.addEventListener("click", () => {
        if (notifyButton.disabled || notifyButton.dataset.loading === "true") {
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

        if (notifyButton.dataset.completed === "true") {
            openNotifyCancelConfirmModal(notifyButton, bookId);
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

function openNotifyCancelConfirmModal(notifyButton, bookId) {
    openAlertModal({
        title: "알림 취소",
        message: "알림을 취소 하시겠습니까?",
        confirmText: "확인",
        cancelText: "유지",
        onConfirm: async () => {
            try {
                await submitNotificationCancel(notifyButton, bookId);
                showNotificationCancelSuccess();
            } catch (error) {
                showNotificationCancelError(error);
            }
        }
    });
}

async function submitNotificationRequest(notifyButton, bookId) {
    const originalHtml = notifyButton.innerHTML;
    const originalClassName = notifyButton.className;
    const originalDisabled = notifyButton.disabled;

    try {
        setNotifyButtonLoading(notifyButton, "신청 중...");

        await apiPost(`/api/books/${bookId}/notifications`, {
            channel: "EMAIL",
            type: "BORROW"
        });

        applyNotifyCompletedState(notifyButton);
    } catch (error) {
        console.error(error);
        restoreNotifyButton(notifyButton, originalHtml, originalClassName, originalDisabled);
        throw error;
    }
}

async function submitNotificationCancel(notifyButton, bookId) {
    const originalHtml = notifyButton.innerHTML;
    const originalClassName = notifyButton.className;
    const originalDisabled = notifyButton.disabled;

    try {
        setNotifyButtonLoading(notifyButton, "취소 중...");

        await apiDelete(`/api/books/${bookId}/notifications/me`);

        applyNotifyRequestEnabledState(notifyButton);
    } catch (error) {
        console.error(error);
        restoreNotifyButton(notifyButton, originalHtml, originalClassName, originalDisabled);
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

function showNotificationCancelSuccess() {
    openAlertModal({
        title: "알림 취소 완료",
        message: "알림 신청이 취소되었습니다.",
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

function showNotificationCancelError(error) {
    const message = error?.message || "요청 처리 중 오류가 발생했습니다.";

    if (error?.status === 401) {
        openAlertModal({
            title: "로그인이 필요합니다.",
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
        title: "알림 취소 실패",
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

function setNotifyButtonLoading(button, text) {
    button.disabled = true;
    button.dataset.loading = "true";
    button.classList.remove("notify-btn--cancel");
    button.innerHTML = `
        <span class="notify-btn__icon" aria-hidden="true">⏳</span>
        <span class="notify-btn__text">${text}</span>
    `;
}

function restoreNotifyButton(button, originalHtml, originalClassName, originalDisabled) {
    button.className = originalClassName;
    button.disabled = originalDisabled;
    button.dataset.loading = "false";
    button.innerHTML = originalHtml;
}

function applyNotifyCompletedState(button) {
    button.disabled = false;
    button.dataset.completed = "true";
    button.dataset.loading = "false";
    button.classList.add("notify-btn--done");
    button.classList.remove("notify-btn--cancel");
    button.innerHTML = `
        <span class="notify-btn__icon" aria-hidden="true">✅</span>
        <span class="notify-btn__text">신청 완료</span>
    `;
}

function applyNotifyCancelHoverState(button) {
    button.disabled = false;
    button.dataset.loading = "false";
    button.classList.add("notify-btn--done", "notify-btn--cancel");
    button.innerHTML = `
        <span class="notify-btn__icon" aria-hidden="true">🔕</span>
        <span class="notify-btn__text">알림 취소</span>
    `;
}

function applyNotifyRequestEnabledState(button) {
    button.disabled = false;
    button.dataset.completed = "false";
    button.dataset.loading = "false";
    button.classList.remove("notify-btn--done", "notify-btn--cancel");
    button.classList.add("notify-btn--enabled");
    button.innerHTML = `
        <span class="notify-btn__icon" aria-hidden="true">🔔</span>
        <span class="notify-btn__text">알림 신청</span>
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

async function apiDelete(url) {
    const response = await fetch(url, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
        },
        credentials: "same-origin"
    });

    const responseText = await response.text();
    let responseBody = null;

    if (responseText) {
        try {
            responseBody = JSON.parse(responseText);
        } catch (error) {
            responseBody = responseText;
        }
    }

    if (!response.ok) {
        const error = new Error(
            typeof responseBody === "object" && responseBody?.message
                ? responseBody.message
                : "요청 처리 중 오류가 발생했습니다."
        );

        error.status = response.status;
        error.body = responseBody;
        throw error;
    }

    return responseBody;
}