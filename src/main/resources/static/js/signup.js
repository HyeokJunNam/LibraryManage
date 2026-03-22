document.addEventListener("DOMContentLoaded", function () {
    const signupForm = document.getElementById("signupForm");
    const signupButton = document.getElementById("signupButton");

    const loginIdElement = document.getElementById("loginId");
    const passwordElement = document.getElementById("password");
    const nameElement = document.getElementById("name");
    const roleElement = document.getElementById("role");

    const loginIdErrorElement = document.getElementById("loginIdError");
    const passwordErrorElement = document.getElementById("passwordError");
    const nameErrorElement = document.getElementById("nameError");
    const formErrorElement = document.getElementById("formError");

    const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

    if (!signupForm) {
        return;
    }

    function clearFieldErrors() {
        loginIdErrorElement.textContent = "";
        passwordErrorElement.textContent = "";
        nameErrorElement.textContent = "";

        loginIdElement.classList.remove("input-error");
        passwordElement.classList.remove("input-error");
        nameElement.classList.remove("input-error");
    }

    function clearFormError() {
        formErrorElement.textContent = "";
        formErrorElement.style.display = "none";
    }

    function showFormError(message) {
        formErrorElement.textContent = message;
        formErrorElement.style.display = "block";
    }

    function showFieldError(field, message) {
        if (field === "loginId") {
            loginIdErrorElement.textContent = message;
            loginIdElement.classList.add("input-error");
            return;
        }

        if (field === "password") {
            passwordErrorElement.textContent = message;
            passwordElement.classList.add("input-error");
            return;
        }

        if (field === "name") {
            nameErrorElement.textContent = message;
            nameElement.classList.add("input-error");
            return;
        }

        showFormError(message);
    }

    signupForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        clearFieldErrors();
        clearFormError();

        signupButton.disabled = true;

        const payload = {
            loginId: loginIdElement.value,
            password: passwordElement.value,
            name: nameElement.value,
            role: roleElement.value
        };

        const headers = {
            "Content-Type": "application/json"
        };

        if (csrfTokenMeta && csrfHeaderMeta) {
            headers[csrfHeaderMeta.content] = csrfTokenMeta.content;
        }

        try {
            const response = await fetch("/signup", {
                method: "POST",
                headers: headers,
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                window.location.href = "/login";
                return;
            }

            let result = null;

            try {
                result = await response.json();
            } catch (error) {
                result = null;
            }

            const message = result?.detail || "회원가입 처리 중 오류가 발생했습니다.";
            const field = result?.field || null;

            if (field) {
                showFieldError(field, message);
            } else {
                showFormError(message);
            }
        } catch (error) {
            console.error(error);
            showFormError("네트워크 오류가 발생했습니다.");
        } finally {
            signupButton.disabled = false;
        }
    });
});