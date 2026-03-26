document.addEventListener("DOMContentLoaded", function () {
    const signupForm = document.getElementById("signupForm");
    const signupButton = document.getElementById("signupButton");
    const sendVerificationButton = document.getElementById("sendVerificationButton");
    const sendVerificationButtonTextElement = document.getElementById("sendVerificationButtonText");
    const verifyCodeButton = document.getElementById("verifyCodeButton");

    const loginIdGroupElement = document.getElementById("loginIdGroup");
    const passwordGroupElement = document.getElementById("passwordGroup");
    const nameGroupElement = document.getElementById("nameGroup");
    const emailGroupElement = document.getElementById("emailGroup");
    const verificationCodeGroupElement = document.getElementById("verificationCodeGroup");

    const loginIdElement = document.getElementById("loginId");
    const passwordElement = document.getElementById("password");
    const nameElement = document.getElementById("name");
    const emailElement = document.getElementById("email");
    const verificationCodeElement = document.getElementById("verificationCode");
    const roleElement = document.getElementById("role");
    const emailVerifiedElement = document.getElementById("emailVerified");

    const formErrorElement = document.getElementById("formError");
    const verificationStatusElement = document.getElementById("verificationStatus");

    const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

    const LOGIN_ID_CHECK_API = "/members/exists";
    const EMAIL_SEND_API = "/mail/send";
    const EMAIL_VERIFY_API = "/mail/verify";
    const SIGNUP_API = "/signup";

    const VERIFICATION_TIMEOUT_SECONDS = 180;

    const SEND_BUTTON_DEFAULT_TEXT = "코드 발송";
    const SEND_BUTTON_DONE_TEXT = "인증 완료";

    let verifiedEmail = "";
    let requestedEmail = "";
    let verificationTimerId = null;
    let verificationDeadline = null;

    let checkedLoginId = "";
    let isLoginIdAvailable = false;
    let loginIdCheckInProgress = false;
    let loginIdModifiedSinceCheck = false;

    if (!signupForm) {
        return;
    }

    document.addEventListener(
        "keydown",
        function (event) {
            if (event.key === "Enter") {
                event.preventDefault();
            }
        },
        true
    );

    function buildHeaders() {
        const headers = {
            "Content-Type": "application/json"
        };

        if (csrfTokenMeta && csrfHeaderMeta) {
            headers[csrfHeaderMeta.content] = csrfTokenMeta.content;
        }

        return headers;
    }

    function isBlank(value) {
        return !value || value.trim() === "";
    }

    function isValidEmail(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }

    function isValidLoginId(loginId) {
        return /^[a-zA-Z0-9._-]{4,20}$/.test(loginId);
    }

    function showElement(element) {
        if (element) {
            element.classList.remove("is-hidden");
        }
    }

    function hideElement(element) {
        if (element) {
            element.classList.add("is-hidden");
        }
    }

    function formatCountdown(seconds) {
        const minutes = String(Math.floor(seconds / 60)).padStart(2, "0");
        const remainSeconds = String(seconds % 60).padStart(2, "0");
        return `${minutes}:${remainSeconds}`;
    }

    function getFieldMessageElement(groupElement) {
        if (!groupElement) {
            return null;
        }

        let messageElement = groupElement.querySelector("[data-field-message='true']");

        if (!messageElement) {
            messageElement = document.createElement("div");
            messageElement.setAttribute("data-field-message", "true");
            messageElement.className = "field-error is-hidden";
            groupElement.appendChild(messageElement);
        }

        return messageElement;
    }

    function setFieldMessage(groupElement, type, message) {
        const messageElement = getFieldMessageElement(groupElement);

        if (!messageElement) {
            return;
        }

        if (isBlank(message)) {
            messageElement.textContent = "";
            messageElement.className = "field-error is-hidden";
            return;
        }

        const nextClassName = type === "error" ? "field-error" : "field-success";

        if (
            messageElement.textContent === message &&
            messageElement.className === nextClassName
        ) {
            return;
        }

        messageElement.textContent = message;
        messageElement.className = nextClassName;
    }

    function clearFieldMessage(groupElement) {
        const messageElement = groupElement?.querySelector("[data-field-message='true']");

        if (!messageElement) {
            return;
        }

        if (messageElement.textContent === "" && messageElement.classList.contains("is-hidden")) {
            return;
        }

        messageElement.textContent = "";
        messageElement.className = "field-error is-hidden";
    }

    function clearInputError(inputElement) {
        if (inputElement) {
            inputElement.classList.remove("input-error");
        }
    }

    function setInputError(inputElement) {
        if (inputElement) {
            inputElement.classList.add("input-error");
        }
    }

    function clearLoginIdMessages() {
        clearInputError(loginIdElement);
        clearFieldMessage(loginIdGroupElement);
    }

    function showLoginIdError(message) {
        setInputError(loginIdElement);
        setFieldMessage(loginIdGroupElement, "error", message);
    }

    function showLoginIdStatus(message) {
        clearInputError(loginIdElement);
        setFieldMessage(loginIdGroupElement, "success", message);
    }

    function clearPasswordMessage() {
        clearInputError(passwordElement);
        clearFieldMessage(passwordGroupElement);
    }

    function clearNameMessage() {
        clearInputError(nameElement);
        clearFieldMessage(nameGroupElement);
    }

    function clearEmailMessage() {
        clearInputError(emailElement);
        clearFieldMessage(emailGroupElement);
    }

    function clearVerificationCodeMessage() {
        clearInputError(verificationCodeElement);
        clearFieldMessage(verificationCodeGroupElement);
    }

    function resetLoginIdCheckState() {
        checkedLoginId = "";
        isLoginIdAvailable = false;
        loginIdCheckInProgress = false;
        loginIdModifiedSinceCheck = false;
        clearLoginIdMessages();
    }

    function clearFieldErrors() {
        clearLoginIdMessages();
        clearPasswordMessage();
        clearNameMessage();
        clearEmailMessage();
        clearVerificationCodeMessage();
    }

    function clearFormError() {
        if (!formErrorElement) {
            return;
        }

        formErrorElement.textContent = "";
        hideElement(formErrorElement);
    }

    function showFormError(message) {
        if (!formErrorElement) {
            return;
        }

        formErrorElement.textContent = message;
        showElement(formErrorElement);
    }

    function clearVerificationStatus() {
        if (!verificationStatusElement) {
            return;
        }

        verificationStatusElement.textContent = "";
        verificationStatusElement.className = "is-hidden";
    }

    function showVerificationStatus(message, isError = false) {
        if (!verificationStatusElement) {
            return;
        }

        const nextClassName = isError ? "field-error" : "field-success";

        if (
            verificationStatusElement.textContent === message &&
            verificationStatusElement.className === nextClassName
        ) {
            return;
        }

        verificationStatusElement.textContent = message;
        verificationStatusElement.className = nextClassName;
    }

    function showFieldError(field, message) {
        if (field === "loginId") {
            showLoginIdError(message);
            return;
        }

        if (field === "password") {
            setInputError(passwordElement);
            setFieldMessage(passwordGroupElement, "error", message);
            return;
        }

        if (field === "name") {
            setInputError(nameElement);
            setFieldMessage(nameGroupElement, "error", message);
            return;
        }

        if (field === "email") {
            setInputError(emailElement);
            setFieldMessage(emailGroupElement, "error", message);
            return;
        }

        if (field === "verificationCode") {
            setInputError(verificationCodeElement);
            setFieldMessage(verificationCodeGroupElement, "error", message);
            return;
        }

        showFormError(message);
    }

    function stopVerificationTimer() {
        if (verificationTimerId) {
            clearInterval(verificationTimerId);
            verificationTimerId = null;
        }

        verificationDeadline = null;
    }

    function setSendButtonText(text) {
        if (sendVerificationButtonTextElement) {
            if (sendVerificationButtonTextElement.textContent !== text) {
                sendVerificationButtonTextElement.textContent = text;
            }
            return;
        }

        if (sendVerificationButton.textContent !== text) {
            sendVerificationButton.textContent = text;
        }
    }

    function setSendButtonDefaultState() {
        sendVerificationButton.disabled = false;
        setSendButtonText(SEND_BUTTON_DEFAULT_TEXT);
        sendVerificationButton.classList.remove("is-complete");
    }

    function setSendButtonCountdownState(remainingSeconds) {
        sendVerificationButton.disabled = true;
        setSendButtonText(formatCountdown(remainingSeconds));
        sendVerificationButton.classList.remove("is-complete");
    }

    function setSendButtonCompleteState() {
        sendVerificationButton.disabled = true;
        setSendButtonText(SEND_BUTTON_DONE_TEXT);
        sendVerificationButton.classList.add("is-complete");
    }

    function lockEmailSection() {
        emailElement.disabled = true;
        sendVerificationButton.disabled = true;
    }

    function unlockEmailSection() {
        emailElement.disabled = false;
        sendVerificationButton.disabled = false;
    }

    function resetEmailVerificationState() {
        stopVerificationTimer();

        emailVerifiedElement.value = "false";
        verifiedEmail = "";
        requestedEmail = "";

        verificationCodeElement.value = "";
        verificationCodeElement.disabled = false;
        verifyCodeButton.disabled = false;

        clearVerificationStatus();
        clearVerificationCodeMessage();
        hideElement(verificationCodeGroupElement);

        unlockEmailSection();
        setSendButtonDefaultState();
    }

    function expireEmailVerificationState() {
        stopVerificationTimer();

        emailVerifiedElement.value = "false";
        verifiedEmail = "";
        requestedEmail = "";

        verificationCodeElement.value = "";
        verificationCodeElement.disabled = false;
        verifyCodeButton.disabled = false;

        clearVerificationStatus();
        hideElement(verificationCodeGroupElement);

        unlockEmailSection();
        setSendButtonDefaultState();
        showFieldError("email", "인증 시간이 만료되었습니다. 다시 인증해주세요.");
    }

    function updateVerificationCountdown() {
        if (!verificationDeadline) {
            return;
        }

        const remainingMilliseconds = verificationDeadline - Date.now();
        const remainingSeconds = Math.ceil(remainingMilliseconds / 1000);

        if (remainingSeconds <= 0) {
            if (emailVerifiedElement.value !== "true") {
                expireEmailVerificationState();
            } else {
                stopVerificationTimer();
                lockEmailSection();
                setSendButtonCompleteState();
            }
            return;
        }

        if (emailVerifiedElement.value !== "true") {
            setSendButtonCountdownState(remainingSeconds);
        }
    }

    function startVerificationTimer() {
        stopVerificationTimer();

        verificationDeadline = Date.now() + VERIFICATION_TIMEOUT_SECONDS * 1000;

        lockEmailSection();
        updateVerificationCountdown();

        verificationTimerId = setInterval(function () {
            updateVerificationCountdown();
        }, 1000);
    }

    async function parseJsonResponse(response) {
        try {
            return await response.json();
        } catch (error) {
            return null;
        }
    }

    async function checkLoginIdDuplicate() {
        const loginId = loginIdElement.value.trim();

        if (isBlank(loginId)) {
            resetLoginIdCheckState();
            return;
        }

        if (!isValidLoginId(loginId)) {
            checkedLoginId = "";
            isLoginIdAvailable = false;
            loginIdModifiedSinceCheck = false;
            showLoginIdError("아이디는 4~20자의 영문, 숫자, ., _, - 만 사용할 수 있습니다.");
            return;
        }

        loginIdCheckInProgress = true;

        try {
            const response = await fetch(`${LOGIN_ID_CHECK_API}?loginId=${encodeURIComponent(loginId)}`, {
                method: "GET",
                headers: buildHeaders()
            });

            const data = await parseJsonResponse(response);

            if (!response.ok) {
                checkedLoginId = loginId;
                isLoginIdAvailable = false;
                loginIdModifiedSinceCheck = false;
                showLoginIdError(data?.message || data?.detail || "아이디 중복 확인에 실패했습니다.");
                return;
            }

            const responseLoginId = data?.result?.loginId ?? loginId;
            const available = Boolean(data?.result?.available);

            checkedLoginId = responseLoginId;
            isLoginIdAvailable = available;
            loginIdModifiedSinceCheck = false;

            if (available) {
                showLoginIdStatus("사용 가능한 아이디입니다.");
            } else {
                showLoginIdError("이미 사용 중인 아이디입니다.");
            }
        } catch (error) {
            console.error(error);
            checkedLoginId = "";
            isLoginIdAvailable = false;
            loginIdModifiedSinceCheck = true;
            showLoginIdError("아이디 중복 확인 중 네트워크 오류가 발생했습니다.");
        } finally {
            loginIdCheckInProgress = false;
        }
    }

    function validateSignupForm() {
        let valid = true;
        const loginId = loginIdElement.value.trim();
        const email = emailElement.value.trim();

        if (isBlank(loginId)) {
            showFieldError("loginId", "아이디를 입력해주세요.");
            valid = false;
        } else if (!isValidLoginId(loginId)) {
            showFieldError("loginId", "아이디는 4~20자의 영문, 숫자, ., _, - 만 사용할 수 있습니다.");
            valid = false;
        } else if (checkedLoginId !== loginId || !isLoginIdAvailable) {
            showFieldError("loginId", "아이디 중복 확인을 완료해주세요.");
            valid = false;
        }

        if (isBlank(passwordElement.value)) {
            showFieldError("password", "비밀번호를 입력해주세요.");
            valid = false;
        }

        if (isBlank(nameElement.value)) {
            showFieldError("name", "이름을 입력해주세요.");
            valid = false;
        }

        if (isBlank(email)) {
            showFieldError("email", "이메일을 입력해주세요.");
            valid = false;
        } else if (!isValidEmail(email)) {
            showFieldError("email", "올바른 이메일 형식을 입력해주세요.");
            valid = false;
        }

        if (requestedEmail !== email) {
            showFieldError("email", "인증코드를 먼저 발송해주세요.");
            valid = false;
        } else if (emailVerifiedElement.value !== "true" || verifiedEmail !== email) {
            showFieldError("email", "이메일 인증을 완료해주세요.");
            valid = false;
        }

        return valid;
    }

    sendVerificationButton.addEventListener("click", async function () {
        clearFormError();

        const email = emailElement.value.trim();

        if (isBlank(email)) {
            clearVerificationStatus();
            showFieldError("email", "이메일을 입력해주세요.");
            return;
        }

        if (!isValidEmail(email)) {
            clearVerificationStatus();
            showFieldError("email", "올바른 이메일 형식을 입력해주세요.");
            return;
        }

        sendVerificationButton.disabled = true;
        emailElement.disabled = true;
        verifyCodeButton.disabled = true;

        try {
            const response = await fetch(EMAIL_SEND_API, {
                method: "POST",
                headers: buildHeaders(),
                body: JSON.stringify({ email: email })
            });

            if (!response.ok) {
                const result = await parseJsonResponse(response);
                const message = result?.detail || "인증코드 발송에 실패했습니다.";
                const field = result?.field || "email";

                unlockEmailSection();
                setSendButtonDefaultState();
                verifyCodeButton.disabled = false;

                if (field === "email") {
                    clearVerificationStatus();
                }

                showFieldError(field, message);
                return;
            }

            clearEmailMessage();
            clearVerificationCodeMessage();
            clearVerificationStatus();

            requestedEmail = email;
            verifiedEmail = "";
            emailVerifiedElement.value = "false";

            verificationCodeElement.value = "";
            verificationCodeElement.disabled = false;
            verifyCodeButton.disabled = false;

            showElement(verificationCodeGroupElement);
            showVerificationStatus("인증코드를 발송했습니다. 3분 안에 인증을 완료해주세요.");

            startVerificationTimer();
            verificationCodeElement.focus();
        } catch (error) {
            console.error(error);
            unlockEmailSection();
            setSendButtonDefaultState();
            verifyCodeButton.disabled = false;
            showFormError("인증코드 발송 중 네트워크 오류가 발생했습니다.");
        }
    });

    verifyCodeButton.addEventListener("click", async function () {
        clearFormError();
        clearEmailMessage();

        const email = emailElement.value.trim();
        const verificationCode = verificationCodeElement.value.trim();

        if (isBlank(email)) {
            showFieldError("email", "이메일을 입력해주세요.");
            return;
        }

        if (!isValidEmail(email)) {
            showFieldError("email", "올바른 이메일 형식을 입력해주세요.");
            return;
        }

        if (requestedEmail !== email) {
            showFieldError("email", "먼저 인증코드를 발송해주세요.");
            return;
        }

        if (isBlank(verificationCode)) {
            clearVerificationStatus();
            showFieldError("verificationCode", "인증코드를 입력해주세요.");
            return;
        }

        verifyCodeButton.disabled = true;

        try {
            const response = await fetch(EMAIL_VERIFY_API, {
                method: "POST",
                headers: buildHeaders(),
                body: JSON.stringify({
                    email: email,
                    code: verificationCode
                })
            });

            if (!response.ok) {
                const result = await parseJsonResponse(response);
                const message = result?.detail || "인증코드 확인에 실패했습니다.";
                const field = result?.field || "verificationCode";

                verifyCodeButton.disabled = false;

                if (field === "verificationCode") {
                    clearVerificationStatus();
                }

                showFieldError(field, message);
                return;
            }

            stopVerificationTimer();

            clearVerificationCodeMessage();
            clearVerificationStatus();

            emailVerifiedElement.value = "true";
            verifiedEmail = email;

            verificationCodeElement.disabled = true;
            verifyCodeButton.disabled = true;

            lockEmailSection();
            setSendButtonCompleteState();
            showVerificationStatus("이메일 인증이 완료되었습니다.");
        } catch (error) {
            console.error(error);
            verifyCodeButton.disabled = false;
            showFormError("인증코드 확인 중 네트워크 오류가 발생했습니다.");
        }
    });

    loginIdElement.addEventListener("input", function () {
        const currentLoginId = loginIdElement.value.trim();

        if (currentLoginId !== checkedLoginId) {
            loginIdModifiedSinceCheck = true;
            checkedLoginId = "";
            isLoginIdAvailable = false;
            clearLoginIdMessages();
        }
    });

    loginIdElement.addEventListener("blur", async function () {
        const currentLoginId = loginIdElement.value.trim();

        if (isBlank(currentLoginId)) {
            resetLoginIdCheckState();
            return;
        }

        if (!isValidLoginId(currentLoginId)) {
            checkedLoginId = "";
            isLoginIdAvailable = false;
            loginIdModifiedSinceCheck = false;
            showLoginIdError("아이디는 4~20자의 영문, 숫자, ., _, - 만 사용할 수 있습니다.");
            return;
        }

        const neverChecked = checkedLoginId === "";
        const changedAfterCheck = loginIdModifiedSinceCheck;

        if (neverChecked || changedAfterCheck) {
            await checkLoginIdDuplicate();
        }
    });

    passwordElement.addEventListener("input", function () {
        clearPasswordMessage();
    });

    nameElement.addEventListener("input", function () {
        clearNameMessage();
    });

    emailElement.addEventListener("input", function () {
        clearEmailMessage();

        if (!emailElement.disabled && (requestedEmail || verifiedEmail)) {
            resetEmailVerificationState();
        }
    });

    verificationCodeElement.addEventListener("input", function () {
        clearVerificationCodeMessage();
    });

    signupForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        clearFieldErrors();
        clearFormError();
        clearVerificationStatus();

        const currentLoginId = loginIdElement.value.trim();
        const needsLoginIdCheck =
            !isBlank(currentLoginId) &&
            isValidLoginId(currentLoginId) &&
            (checkedLoginId !== currentLoginId || loginIdModifiedSinceCheck);

        if (needsLoginIdCheck) {
            await checkLoginIdDuplicate();
        }

        if (loginIdCheckInProgress) {
            showFieldError("loginId", "아이디 중복 확인이 아직 진행 중입니다.");
            return;
        }

        if (!validateSignupForm()) {
            return;
        }

        signupButton.disabled = true;

        const payload = {
            loginId: loginIdElement.value.trim(),
            password: passwordElement.value,
            name: nameElement.value.trim(),
            email: emailElement.value.trim(),
            role: roleElement.value
        };

        try {
            const response = await fetch(SIGNUP_API, {
                method: "POST",
                headers: buildHeaders(),
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                window.location.replace("/login");
                return;
            }

            const result = await parseJsonResponse(response);
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