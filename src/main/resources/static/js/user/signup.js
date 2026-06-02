document.addEventListener("DOMContentLoaded", () => {
    const $ = (id) => document.getElementById(id);
    const form = $("signupForm");

    if (!form) {
        return;
    }

    const elements = {
        form,
        signupButton: $("signupButton"),
        loginId: $("loginId"),
        password: $("password"),
        passwordConfirm: $("passwordConfirm"),
        name: $("name"),
        phoneNumber: $("phoneNumber"),
        email: $("email"),
        verificationCode: $("verificationCode"),
        role: $("role"),
        emailVerified: $("emailVerified"),
        formError: $("formError"),
        verificationStatus: $("verificationStatus"),
        sendVerificationButton: $("sendVerificationButton"),
        sendVerificationButtonText: $("sendVerificationButtonText"),
        verifyCodeButton: $("verifyCodeButton")
    };

    const groups = {
        loginId: $("loginIdGroup"),
        password: $("passwordGroup"),
        passwordConfirm: $("passwordConfirmGroup"),
        name: $("nameGroup"),
        phoneNumber: $("phoneGroup"),
        email: $("emailGroup"),
        verificationCode: $("verificationCodeGroup")
    };

    const requiredElementKeys = [
        "signupButton",
        "loginId",
        "password",
        "passwordConfirm",
        "name",
        "phoneNumber",
        "email",
        "verificationCode",
        "role",
        "emailVerified",
        "formError",
        "verificationStatus",
        "sendVerificationButton",
        "verifyCodeButton"
    ];

    const missingKeys = requiredElementKeys.filter((key) => !elements[key]);

    if (missingKeys.length > 0) {
        console.error("회원가입 폼 초기화 실패: 필수 요소가 없습니다.", missingKeys);
        return;
    }

    const API = {
        checkLoginId: "/api/public/auth/check-id",
        sendEmail: "/api/public/auth/email-verifications",
        verifyEmail: "/api/public/auth/email-verifications/confirm",
        signup: "/api/public/signup"
    };

    const REGEX = {
        loginId: /^[a-zA-Z0-9._-]{4,20}$/,
        email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        phoneNumber: /^010[0-9]{8}$/
    };

    const TEXT = {
        sendCode: "코드 발송",
        verifyComplete: "인증 완료",

        requiredLoginId: "아이디를 입력해주세요.",
        invalidLoginId: "아이디는 4~20자의 영문, 숫자, ., _, - 만 사용할 수 있습니다.",
        availableLoginId: "사용 가능한 아이디입니다.",
        duplicatedLoginId: "이미 사용 중인 아이디입니다.",
        needLoginIdCheck: "아이디 중복 확인을 완료해주세요.",
        loginIdChecking: "아이디 중복 확인이 아직 진행 중입니다.",

        requiredPassword: "비밀번호를 입력해주세요.",
        requiredPasswordConfirm: "비밀번호 확인을 입력해주세요.",
        passwordMismatch: "비밀번호가 일치하지 않습니다.",

        requiredName: "이름을 입력해주세요.",

        requiredPhone: "휴대폰 번호를 입력해주세요.",
        invalidPhone: "휴대폰 번호는 010으로 시작하는 11자리 숫자여야 합니다.",

        requiredEmail: "이메일을 입력해주세요.",
        invalidEmail: "올바른 이메일 형식을 입력해주세요.",
        sendCodeFirst: "인증코드를 먼저 발송해주세요.",
        needEmailVerification: "이메일 인증을 완료해주세요.",
        expiredEmailVerification: "인증 시간이 만료되었습니다. 다시 인증해주세요.",
        missingSignupToken: "이메일 인증 토큰이 없습니다. 다시 인증해주세요.",

        requiredVerificationCode: "인증코드를 입력해주세요.",
        sentVerificationCode: "인증코드를 발송했습니다. 3분 안에 인증을 완료해주세요.",
        verifiedEmail: "이메일 인증이 완료되었습니다.",
        missingVerificationToken: "인증 토큰을 받지 못했습니다. 다시 인증해주세요.",

        loginIdCheckFail: "아이디 중복 확인에 실패했습니다.",
        loginIdCheckNetworkFail: "아이디 중복 확인 중 네트워크 오류가 발생했습니다.",

        sendCodeFail: "인증코드 발송에 실패했습니다.",
        sendCodeNetworkFail: "인증코드 발송 중 네트워크 오류가 발생했습니다.",

        verifyCodeFail: "인증코드 확인에 실패했습니다.",
        verifyCodeNetworkFail: "인증코드 확인 중 네트워크 오류가 발생했습니다.",

        signupFail: "회원가입 처리 중 오류가 발생했습니다.",
        signupNetworkFail: "네트워크 오류가 발생했습니다."
    };

    const EMAIL_VERIFIED = {
        TRUE: "true",
        FALSE: "false"
    };

    const VERIFICATION_TIMEOUT_MS = 180000;

    const state = {
        checkedLoginId: "",
        loginIdAvailable: false,
        loginIdChecking: false,
        loginIdChanged: false,
        requestedEmail: "",
        verifiedEmail: "",
        signupToken: "",
        timerId: null,
        deadline: null
    };

    const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

    const isBlank = (value) => !value || value.trim() === "";
    const trimValue = (element) => element.value.trim();
    const onlyNumbers = (value) => value.replace(/[^0-9]/g, "");
    const isValidLoginId = (value) => REGEX.loginId.test(value);
    const isValidEmail = (value) => REGEX.email.test(value);
    const isValidPhone = (value) => REGEX.phoneNumber.test(value);
    const isEmailVerified = () => elements.emailVerified.value === EMAIL_VERIFIED.TRUE;

    const getHeaders = () => {
        if (!csrfTokenMeta || !csrfHeaderMeta) {
            return {};
        }

        return {
            [csrfHeaderMeta.content]: csrfTokenMeta.content
        };
    };

    const getValues = () => ({
        loginId: trimValue(elements.loginId),
        password: elements.password.value,
        passwordConfirm: elements.passwordConfirm.value,
        name: trimValue(elements.name),
        phoneNumber: formatPhone(elements.phoneNumber.value),
        phoneNumberDigits: onlyNumbers(elements.phoneNumber.value),
        email: trimValue(elements.email),
        verificationCode: trimValue(elements.verificationCode)
    });

    const formatPhone = (value) => {
        const numbers = onlyNumbers(value).slice(0, 11);

        if (numbers.length <= 3) {
            return numbers;
        }

        if (numbers.length <= 7) {
            return `${numbers.slice(0, 3)}-${numbers.slice(3)}`;
        }

        return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7)}`;
    };

    const toggleHidden = (element, hidden) => {
        element?.classList.toggle("is-hidden", hidden);
    };

    const getMessageElement = (field) => {
        const group = groups[field];

        if (!group) {
            return null;
        }

        let message = group.querySelector("[data-field-message='true']");

        if (!message) {
            message = document.createElement("div");
            message.dataset.fieldMessage = "true";
            message.className = "field-error is-hidden";
            group.appendChild(message);
        }

        return message;
    };

    const setFieldMessage = (field, message = "", type = "error") => {
        const messageElement = getMessageElement(field);
        const inputElement = elements[field];

        if (!messageElement) {
            return;
        }

        if (isBlank(message)) {
            messageElement.textContent = "";
            messageElement.className = "field-error is-hidden";
            inputElement?.classList.remove("input-error");
            return;
        }

        const isError = type === "error";

        messageElement.textContent = message;
        messageElement.className = isError ? "field-error" : "field-success";
        inputElement?.classList.toggle("input-error", isError);
    };

    const clearField = (field) => setFieldMessage(field);
    const clearAllFieldMessages = () => Object.keys(groups).forEach(clearField);

    const showFormError = (message) => {
        elements.formError.textContent = message;
        toggleHidden(elements.formError, false);
    };

    const clearFormError = () => {
        elements.formError.textContent = "";
        toggleHidden(elements.formError, true);
    };

    const showFieldError = (field, message) => {
        if (groups[field]) {
            setFieldMessage(field, message, "error");
            return;
        }

        showFormError(message);
    };

    const showFieldSuccess = (field, message) => {
        setFieldMessage(field, message, "success");
    };

    const setVerificationStatus = (message = "", type = "success") => {
        elements.verificationStatus.textContent = message;
        elements.verificationStatus.className = message
            ? (type === "error" ? "field-error" : "field-success")
            : "is-hidden";
    };

    const setSendButton = ({ text, disabled = false, complete = false }) => {
        elements.sendVerificationButton.disabled = disabled;
        elements.sendVerificationButton.classList.toggle("is-complete", complete);

        if (elements.sendVerificationButtonText) {
            elements.sendVerificationButtonText.textContent = text;
            return;
        }

        elements.sendVerificationButton.textContent = text;
    };

    const setEmailLock = (locked) => {
        elements.email.disabled = locked;
        elements.sendVerificationButton.disabled = locked;
    };

    const setEmailVerifiedValue = (verified) => {
        elements.emailVerified.value = verified ? EMAIL_VERIFIED.TRUE : EMAIL_VERIFIED.FALSE;
    };

    const stopTimer = () => {
        if (state.timerId) {
            clearInterval(state.timerId);
        }

        state.timerId = null;
        state.deadline = null;
    };

    const resetLoginIdCheckState = ({ changed = false } = {}) => {
        state.checkedLoginId = "";
        state.loginIdAvailable = false;
        state.loginIdChecking = false;
        state.loginIdChanged = changed;
    };

    const invalidateLoginIdCheck = () => {
        resetLoginIdCheckState({ changed: true });
    };

    const resetEmailVerificationState = () => {
        state.requestedEmail = "";
        state.verifiedEmail = "";
        state.signupToken = "";
        setEmailVerifiedValue(false);
    };

    const resetEmailVerificationUI = () => {
        elements.verificationCode.value = "";
        elements.verificationCode.disabled = false;
        elements.verifyCodeButton.disabled = false;

        clearField("verificationCode");
        setVerificationStatus();
        toggleHidden(groups.verificationCode, true);
        setEmailLock(false);
        setSendButton({ text: TEXT.sendCode });
    };

    const resetEmailVerification = () => {
        stopTimer();
        resetEmailVerificationState();
        resetEmailVerificationUI();
    };

    const formatCountdown = (seconds) => {
        const minutes = String(Math.floor(seconds / 60)).padStart(2, "0");
        const remainSeconds = String(seconds % 60).padStart(2, "0");

        return `${minutes}:${remainSeconds}`;
    };

    const expireEmailVerification = () => {
        resetEmailVerification();
        showFieldError("email", TEXT.expiredEmailVerification);
    };

    const updateTimer = () => {
        if (!state.deadline) {
            return;
        }

        const remainingSeconds = Math.ceil((state.deadline - Date.now()) / 1000);

        if (remainingSeconds <= 0) {
            if (isEmailVerified()) {
                stopTimer();
                setEmailLock(true);
                setSendButton({
                    text: TEXT.verifyComplete,
                    disabled: true,
                    complete: true
                });
            } else {
                expireEmailVerification();
            }

            return;
        }

        if (!isEmailVerified()) {
            setSendButton({
                text: formatCountdown(remainingSeconds),
                disabled: true
            });
        }
    };

    const startTimer = () => {
        stopTimer();
        state.deadline = Date.now() + VERIFICATION_TIMEOUT_MS;
        setEmailLock(true);
        updateTimer();
        state.timerId = setInterval(updateTimer, 1000);
    };

    const handleRequestError = (
        error,
        {
            field = null,
            apiMessage,
            networkMessage,
            networkToField = false
        }
    ) => {
        if (error instanceof ApiError) {
            showFieldError(error?.data?.field || field, error.message || apiMessage);
            return;
        }

        console.error(error);

        if (networkToField && field) {
            showFieldError(field, networkMessage);
            return;
        }

        showFormError(networkMessage);
    };

    const validatePasswordMatch = () => {
        const password = elements.password.value;
        const passwordConfirm = elements.passwordConfirm.value;

        if (isBlank(passwordConfirm)) {
            clearField("passwordConfirm");
            return true;
        }

        if (password !== passwordConfirm) {
            showFieldError("passwordConfirm", TEXT.passwordMismatch);
            return false;
        }

        clearField("passwordConfirm");
        return true;
    };

    const validateForm = () => {
        const values = getValues();
        let valid = true;

        const invalidate = (field, message) => {
            showFieldError(field, message);
            valid = false;
        };

        if (isBlank(values.loginId)) {
            invalidate("loginId", TEXT.requiredLoginId);
        } else if (!isValidLoginId(values.loginId)) {
            invalidate("loginId", TEXT.invalidLoginId);
        } else if (state.checkedLoginId !== values.loginId || !state.loginIdAvailable) {
            invalidate("loginId", TEXT.needLoginIdCheck);
        }

        if (isBlank(values.password)) {
            invalidate("password", TEXT.requiredPassword);
        }

        if (isBlank(values.passwordConfirm)) {
            invalidate("passwordConfirm", TEXT.requiredPasswordConfirm);
        } else if (values.password !== values.passwordConfirm) {
            invalidate("passwordConfirm", TEXT.passwordMismatch);
        }

        if (isBlank(values.name)) {
            invalidate("name", TEXT.requiredName);
        }

        if (isBlank(values.phoneNumberDigits)) {
            invalidate("phoneNumber", TEXT.requiredPhone);
        } else if (!isValidPhone(values.phoneNumberDigits)) {
            invalidate("phoneNumber", TEXT.invalidPhone);
        }

        if (isBlank(values.email)) {
            invalidate("email", TEXT.requiredEmail);
        } else if (!isValidEmail(values.email)) {
            invalidate("email", TEXT.invalidEmail);
        } else if (state.requestedEmail !== values.email) {
            invalidate("email", TEXT.sendCodeFirst);
        } else if (!isEmailVerified() || state.verifiedEmail !== values.email) {
            invalidate("email", TEXT.needEmailVerification);
        } else if (isBlank(state.signupToken)) {
            invalidate("email", TEXT.missingSignupToken);
        }

        return { valid, values };
    };

    const validateLoginIdBeforeCheck = (loginId) => {
        if (isBlank(loginId)) {
            resetLoginIdCheckState();
            clearField("loginId");
            return false;
        }

        if (!isValidLoginId(loginId)) {
            resetLoginIdCheckState();
            showFieldError("loginId", TEXT.invalidLoginId);
            return false;
        }

        return true;
    };

    const checkLoginIdDuplicate = async () => {
        const { loginId } = getValues();

        if (!validateLoginIdBeforeCheck(loginId) || state.loginIdChecking) {
            return false;
        }

        state.loginIdChecking = true;

        try {
            const data = await apiGet(
                `${API.checkLoginId}?loginId=${encodeURIComponent(loginId)}`,
                { headers: getHeaders() }
            );

            const result = data?.result || {};
            const available = Boolean(result.available);

            state.checkedLoginId = result.loginId ?? loginId;
            state.loginIdAvailable = available;
            state.loginIdChanged = false;

            if (available) {
                showFieldSuccess("loginId", TEXT.availableLoginId);
            } else {
                showFieldError("loginId", TEXT.duplicatedLoginId);
            }

            return available;
        } catch (error) {
            invalidateLoginIdCheck();

            handleRequestError(error, {
                field: "loginId",
                apiMessage: TEXT.loginIdCheckFail,
                networkMessage: TEXT.loginIdCheckNetworkFail,
                networkToField: true
            });

            return false;
        } finally {
            state.loginIdChecking = false;
        }
    };

    const sendVerificationCode = async () => {
        clearFormError();
        clearField("email");
        setVerificationStatus();

        const { email } = getValues();

        if (isBlank(email)) {
            showFieldError("email", TEXT.requiredEmail);
            return;
        }

        if (!isValidEmail(email)) {
            showFieldError("email", TEXT.invalidEmail);
            return;
        }

        elements.sendVerificationButton.disabled = true;
        elements.email.disabled = true;
        elements.verifyCodeButton.disabled = true;

        try {
            await apiPost(API.sendEmail, { email }, { headers: getHeaders() });

            state.requestedEmail = email;
            state.verifiedEmail = "";
            state.signupToken = "";
            setEmailVerifiedValue(false);

            elements.verificationCode.value = "";
            elements.verificationCode.disabled = false;
            elements.verifyCodeButton.disabled = false;

            clearField("verificationCode");
            toggleHidden(groups.verificationCode, false);
            setVerificationStatus(TEXT.sentVerificationCode);

            startTimer();
            elements.verificationCode.focus();
        } catch (error) {
            setEmailLock(false);
            setSendButton({ text: TEXT.sendCode });
            elements.verifyCodeButton.disabled = false;

            handleRequestError(error, {
                field: "email",
                apiMessage: TEXT.sendCodeFail,
                networkMessage: TEXT.sendCodeNetworkFail
            });
        }
    };

    const verifyEmailCode = async () => {
        clearFormError();
        clearField("email");
        clearField("verificationCode");

        const { email, verificationCode } = getValues();

        if (isBlank(email)) {
            showFieldError("email", TEXT.requiredEmail);
            return;
        }

        if (!isValidEmail(email)) {
            showFieldError("email", TEXT.invalidEmail);
            return;
        }

        if (state.requestedEmail !== email) {
            showFieldError("email", TEXT.sendCodeFirst);
            return;
        }

        if (isBlank(verificationCode)) {
            setVerificationStatus();
            showFieldError("verificationCode", TEXT.requiredVerificationCode);
            return;
        }

        elements.verifyCodeButton.disabled = true;

        try {
            const data = await apiPost(
                API.verifyEmail,
                {
                    email,
                    code: verificationCode
                },
                { headers: getHeaders() }
            );

            const token = data?.result?.token;

            if (isBlank(token)) {
                elements.verifyCodeButton.disabled = false;
                showFormError(TEXT.missingVerificationToken);
                return;
            }

            stopTimer();

            state.verifiedEmail = email;
            state.signupToken = token;
            setEmailVerifiedValue(true);

            elements.verificationCode.disabled = true;
            elements.verifyCodeButton.disabled = true;

            setEmailLock(true);
            setSendButton({
                text: TEXT.verifyComplete,
                disabled: true,
                complete: true
            });
            setVerificationStatus(TEXT.verifiedEmail);
        } catch (error) {
            elements.verifyCodeButton.disabled = false;
            setVerificationStatus();

            handleRequestError(error, {
                field: "verificationCode",
                apiMessage: TEXT.verifyCodeFail,
                networkMessage: TEXT.verifyCodeNetworkFail
            });
        }
    };

    const submitSignup = async (event) => {
        event.preventDefault();

        clearAllFieldMessages();
        clearFormError();
        setVerificationStatus();

        const { loginId } = getValues();
        const needsLoginIdCheck =
            !isBlank(loginId) &&
            isValidLoginId(loginId) &&
            (state.checkedLoginId !== loginId || state.loginIdChanged);

        if (needsLoginIdCheck) {
            await checkLoginIdDuplicate();
        }

        if (state.loginIdChecking) {
            showFieldError("loginId", TEXT.loginIdChecking);
            return;
        }

        const { valid, values } = validateForm();

        if (!valid) {
            return;
        }

        elements.signupButton.disabled = true;

        const payload = {
            loginId: values.loginId,
            password: values.password,
            name: values.name,
            phoneNumber: values.phoneNumber,
            email: values.email,
            role: elements.role.value,
            signupToken: state.signupToken
        };

        try {
            await apiPost(API.signup, payload, {
                headers: getHeaders()
            });

            window.location.replace("/login");
        } catch (error) {
            handleRequestError(error, {
                apiMessage: TEXT.signupFail,
                networkMessage: TEXT.signupNetworkFail
            });
        } finally {
            elements.signupButton.disabled = false;
        }
    };

    const on = (element, eventName, handler, options) => {
        element?.addEventListener(eventName, handler, options);
    };

    on(document, "keydown", (event) => {
        if (event.key === "Enter") {
            event.preventDefault();
        }
    }, true);

    on(elements.loginId, "input", () => {
        const { loginId } = getValues();

        if (loginId !== state.checkedLoginId) {
            invalidateLoginIdCheck();
            clearField("loginId");
        }
    });

    on(elements.loginId, "blur", () => {
        const { loginId } = getValues();

        if (isBlank(loginId)) {
            resetLoginIdCheckState();
            clearField("loginId");
            return;
        }

        if (!isValidLoginId(loginId)) {
            resetLoginIdCheckState();
            showFieldError("loginId", TEXT.invalidLoginId);
            return;
        }

        if (!state.checkedLoginId || state.loginIdChanged) {
            void checkLoginIdDuplicate();
        }
    });

    on(elements.password, "input", () => {
        clearField("password");
        validatePasswordMatch();
    });

    on(elements.passwordConfirm, "input", () => {
        validatePasswordMatch();
    });

    on(elements.name, "input", () => {
        clearField("name");
    });

    on(elements.phoneNumber, "input", () => {
        elements.phoneNumber.value = formatPhone(elements.phoneNumber.value);
        clearField("phoneNumber");
    });

    on(elements.email, "input", () => {
        clearField("email");

        if (!elements.email.disabled && (state.requestedEmail || state.verifiedEmail || state.signupToken)) {
            resetEmailVerification();
        }
    });

    on(elements.verificationCode, "input", () => {
        clearField("verificationCode");
    });

    on(elements.sendVerificationButton, "click", sendVerificationCode);
    on(elements.verifyCodeButton, "click", verifyEmailCode);
    on(elements.form, "submit", submitSignup);
});