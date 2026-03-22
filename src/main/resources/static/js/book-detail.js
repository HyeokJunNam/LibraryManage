document.addEventListener("DOMContentLoaded", function () {
    applyBackLink();
    bindActionButtons();
});

function applyBackLink() {
    const backLink = document.getElementById("backToList");

    if (!backLink) {
        return;
    }

    const savedReturnUrl = sessionStorage.getItem("bookListReturnUrl");
    backLink.setAttribute("href", resolveReturnUrl(savedReturnUrl));
}

function resolveReturnUrl(returnUrl) {
    if (!returnUrl || returnUrl.trim() === "") {
        return "/";
    }

    if (!returnUrl.startsWith("/") || returnUrl.startsWith("//")) {
        return "/";
    }

    return returnUrl;
}

function bindActionButtons() {
    const pageElement = document.querySelector(".page");
    const loginId = pageElement ? pageElement.dataset.loginId : null;
    const actionButtons = document.querySelectorAll(".book-action-btn");

    if (!actionButtons.length) {
        return;
    }

    actionButtons.forEach(function (button) {
        button.addEventListener("click", async function () {
            if (button.disabled) {
                return;
            }

            const bookItemId = button.dataset.bookItemId;
            const action = button.dataset.action;

            if (!loginId) {
                alert("회원 정보를 찾을 수 없습니다.");
                return;
            }

            if (!bookItemId) {
                alert("도서 정보를 찾을 수 없습니다.");
                return;
            }

            if (action !== "borrow" && action !== "return") {
                return;
            }

            const actionConfig = getActionConfig(action);
            const confirmed = confirm(actionConfig.confirmMessage);

            if (!confirmed) {
                return;
            }

            const originalText = button.textContent;

            try {
                setActionButtonLoading(button);

                const response = await fetch(actionConfig.url, {
                    method: "POST",
                    headers: buildJsonHeaders(),
                    body: JSON.stringify({
                        bookItemId: Number(bookItemId),
                        loginId: String(loginId)
                    })
                });

                if (!response.ok) {
                    throw new Error(await extractErrorMessage(response));
                }

                alert(actionConfig.successMessage);
                window.location.reload();
            } catch (error) {
                alert(error.message || actionConfig.errorMessage);
                resetActionButton(button, originalText);
            }
        });
    });
}

function getActionConfig(action) {
    if (action === "return") {
        return {
            url: "/returns",
            confirmMessage: "이 도서를 반납하시겠습니까?",
            successMessage: "반납이 완료되었습니다.",
            errorMessage: "반납 처리 중 오류가 발생했습니다."
        };
    }

    return {
        url: "/borrows",
        confirmMessage: "이 도서를 대여하시겠습니까?",
        successMessage: "대여가 완료되었습니다.",
        errorMessage: "대여 처리 중 오류가 발생했습니다."
    };
}

function buildJsonHeaders() {
    const headers = {
        "Content-Type": "application/json"
    };

    const csrfToken = getMetaContent("_csrf");
    const csrfHeader = getMetaContent("_csrf_header");

    if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
    }

    return headers;
}

function setActionButtonLoading(button) {
    button.disabled = true;
    button.classList.add("book-action-btn--loading");
    button.textContent = "처리 중...";
}

function resetActionButton(button, text) {
    button.disabled = false;
    button.classList.remove("book-action-btn--loading");
    button.textContent = text;
}

function getMetaContent(name) {
    const element = document.querySelector(`meta[name="${name}"]`);
    return element ? element.getAttribute("content") : "";
}

async function extractErrorMessage(response) {
    let message = "요청 처리 중 오류가 발생했습니다.";

    try {
        const result = await response.json();

        if (result) {
            if (typeof result.detail === "string" && result.detail.trim() !== "") {
                message = result.detail;
            } else if (typeof result.message === "string" && result.message.trim() !== "") {
                message = result.message;
            }
        }
    } catch (error) {
        // ignore
    }

    return message;
}