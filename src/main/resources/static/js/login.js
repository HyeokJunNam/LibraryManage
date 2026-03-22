document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");
    const loginIdInput = document.getElementById("loginId");
    const passwordInput = document.getElementById("password");
    const loginError = document.getElementById("loginError");
    const loginSubmitButton = document.getElementById("loginSubmitButton");

    if (!loginForm || !loginIdInput || !passwordInput || !loginError || !loginSubmitButton) {
        return;
    }

    loginForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        // 초기화
        loginError.hidden = true;
        loginError.textContent = "";

        loginSubmitButton.disabled = true;
        loginSubmitButton.textContent = "로그인 중...";

        const formData = new FormData(loginForm);

        try {
            const response = await fetch(loginForm.action, {
                method: "POST",
                body: new URLSearchParams(formData),
                headers: {
                    "X-Requested-With": "XMLHttpRequest"
                },
                credentials: "same-origin"
            });

            let result = null;

            try {
                result = await response.json();
            } catch (e) {
                result = null;
            }

            // ✅ 성공
            if (response.ok) {
                const redirectUrl = result && result.redirectUrl
                    ? result.redirectUrl
                    : "/";

                window.location.href = redirectUrl;
                return;
            }

            // ❌ 실패
            const message = result && result.message
                ? result.message
                : "아이디 또는 비밀번호를 확인해주세요.";

            loginError.textContent = message;
            loginError.hidden = false;

            // 🔥 핵심: 비밀번호 초기화
            passwordInput.value = "";
            passwordInput.focus();

        } catch (e) {
            loginError.textContent = "요청 처리 중 오류가 발생했습니다.";
            loginError.hidden = false;

            passwordInput.value = "";
            passwordInput.focus();
        } finally {
            loginSubmitButton.disabled = false;
            loginSubmitButton.textContent = "로그인";
        }
    });
});