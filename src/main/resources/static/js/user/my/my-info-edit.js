document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("myInfoEditForm");
    const submitButton = document.getElementById("editConfirmOpenButton");

    const phoneNumber = document.getElementById("phoneNumber");

    const newPassword = document.getElementById("newPassword");
    const newPasswordConfirm = document.getElementById("newPasswordConfirm");
    const passwordMatchMessage = document.getElementById("passwordMatchMessage");

    if (!form || !submitButton) {
        return;
    }

    function formatPhoneNumber(value) {
        const numbers = value.replace(/\D/g, "").slice(0, 11);

        if (numbers.length <= 3) {
            return numbers;
        }

        if (numbers.length <= 7) {
            return `${numbers.slice(0, 3)}-${numbers.slice(3)}`;
        }

        return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7)}`;
    }

    function handlePhoneNumberInput() {
        if (!phoneNumber) {
            return;
        }

        phoneNumber.value = formatPhoneNumber(phoneNumber.value);
    }

    function validatePasswordMatch() {
        if (!newPassword || !newPasswordConfirm || !passwordMatchMessage) {
            return true;
        }

        passwordMatchMessage.className = "form-field__message";
        passwordMatchMessage.textContent = "";

        newPasswordConfirm.classList.remove("is-error");
        newPasswordConfirm.setCustomValidity("");

        const password = newPassword.value.trim();
        const confirmPassword = newPasswordConfirm.value.trim();

        if (!password && !confirmPassword) {
            return true;
        }

        if (!password && confirmPassword) {
            passwordMatchMessage.textContent = "새 비밀번호를 입력해 주세요.";
            passwordMatchMessage.classList.add("is-visible", "is-error");

            newPasswordConfirm.classList.add("is-error");
            newPasswordConfirm.setCustomValidity("새 비밀번호를 입력해 주세요.");

            return false;
        }

        if (password && !confirmPassword) {
            passwordMatchMessage.textContent = "새 비밀번호 확인을 입력해 주세요.";
            passwordMatchMessage.classList.add("is-visible", "is-error");

            newPasswordConfirm.classList.add("is-error");
            newPasswordConfirm.setCustomValidity("새 비밀번호 확인을 입력해 주세요.");

            return false;
        }

        if (password !== confirmPassword) {
            passwordMatchMessage.textContent = "비밀번호가 일치하지 않습니다.";
            passwordMatchMessage.classList.add("is-visible", "is-error");

            newPasswordConfirm.classList.add("is-error");
            newPasswordConfirm.setCustomValidity("비밀번호가 일치하지 않습니다.");

            return false;
        }

        return true;
    }

    if (phoneNumber) {
        phoneNumber.value = formatPhoneNumber(phoneNumber.value);
        phoneNumber.addEventListener("input", handlePhoneNumberInput);
    }

    if (newPassword && newPasswordConfirm) {
        newPassword.addEventListener("input", validatePasswordMatch);
        newPasswordConfirm.addEventListener("input", validatePasswordMatch);
    }

    submitButton.addEventListener("click", () => {
        const isPasswordMatched = validatePasswordMatch();

        if (phoneNumber) {
            phoneNumber.value = formatPhoneNumber(phoneNumber.value);
        }

        if (!form.checkValidity() || !isPasswordMatched) {
            form.reportValidity();
            return;
        }

        openAlertModal({
            title: "회원정보 수정",
            message: "입력한 내용으로 회원정보를 수정하시겠습니까?",
            confirmText: "확인",
            cancelText: "취소",
            onConfirm: () => {
                form.submit();
            }
        });
    });
});