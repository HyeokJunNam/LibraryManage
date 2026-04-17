document.addEventListener("DOMContentLoaded", () => {
    initializeAlertModal();
});

function initializeAlertModal() {
    const modal = document.querySelector('[data-role="alert-modal"]');

    if (!modal) {
        return;
    }

    const backdrop = modal.querySelector('[data-role="alert-modal-backdrop"]');
    const confirmButton = modal.querySelector('[data-role="alert-modal-confirm"]');
    const cancelButton = modal.querySelector('[data-role="alert-modal-cancel"]');

    if (backdrop) {
        backdrop.addEventListener("click", () => {
            if (modal.__isProcessing) {
                return;
            }

            handleAlertModalCancel();
        });
    }

    if (confirmButton) {
        confirmButton.addEventListener("click", async () => {
            if (modal.__isProcessing) {
                return;
            }

            const onConfirm = modal.__onConfirm;
            const hasConfirmHandler = typeof onConfirm === "function";

            if (!hasConfirmHandler) {
                closeAlertModal();
                return;
            }

            try {
                setAlertModalProcessing(true);
                closeAlertModal();
                await onConfirm();
            } catch (error) {
                console.error(error);
            }
        });
    }

    if (cancelButton) {
        cancelButton.addEventListener("click", () => {
            if (modal.__isProcessing) {
                return;
            }

            handleAlertModalCancel();
        });
    }

    document.addEventListener("keydown", (event) => {
        if (event.key !== "Escape") {
            return;
        }

        if (modal.hidden || modal.__isProcessing) {
            return;
        }

        handleAlertModalCancel();
    });
}

function openAlertModal({
                            title = "안내",
                            message = "",
                            confirmText = "확인",
                            cancelText = "",
                            onConfirm = null,
                            onCancel = null
                        }) {
    const modal = document.querySelector('[data-role="alert-modal"]');

    if (!modal) {
        const confirmed = cancelText ? window.confirm(message) : true;

        if (confirmed) {
            if (typeof onConfirm === "function") {
                onConfirm();
            }
            return;
        }

        if (typeof onCancel === "function") {
            onCancel();
        }
        return;
    }

    const titleElement = modal.querySelector("#alert-modal-title");
    const messageElement = modal.querySelector("#alert-modal-message");
    const confirmButton = modal.querySelector('[data-role="alert-modal-confirm"]');
    const cancelButton = modal.querySelector('[data-role="alert-modal-cancel"]');

    if (titleElement) {
        titleElement.textContent = title;
    }

    if (messageElement) {
        messageElement.textContent = message;
    }

    if (confirmButton) {
        confirmButton.textContent = confirmText;
    }

    if (cancelButton) {
        const shouldShowCancel = Boolean(cancelText);
        cancelButton.textContent = cancelText || "취소";
        cancelButton.hidden = !shouldShowCancel;
    }

    modal.__onConfirm = onConfirm;
    modal.__onCancel = onCancel;
    modal.__confirmText = confirmText;
    modal.__cancelText = cancelText || "취소";
    modal.__isProcessing = false;
    modal.hidden = false;
    document.body.classList.add("modal-open");

    setAlertModalProcessing(false);

    if (confirmButton) {
        confirmButton.focus();
    }
}

function handleAlertModalCancel() {
    const modal = document.querySelector('[data-role="alert-modal"]');

    if (!modal) {
        return;
    }

    const onCancel = modal.__onCancel;
    closeAlertModal();

    if (typeof onCancel === "function") {
        onCancel();
    }
}

function setAlertModalProcessing(isProcessing) {
    const modal = document.querySelector('[data-role="alert-modal"]');

    if (!modal) {
        return;
    }

    const confirmButton = modal.querySelector('[data-role="alert-modal-confirm"]');
    const cancelButton = modal.querySelector('[data-role="alert-modal-cancel"]');

    modal.__isProcessing = isProcessing;

    if (confirmButton) {
        confirmButton.disabled = isProcessing;
        confirmButton.textContent = isProcessing ? "처리 중..." : (modal.__confirmText || "확인");
    }

    if (cancelButton) {
        cancelButton.disabled = isProcessing;
        cancelButton.textContent = modal.__cancelText || "취소";
    }
}

function closeAlertModal() {
    const modal = document.querySelector('[data-role="alert-modal"]');

    if (!modal) {
        return;
    }

    const confirmButton = modal.querySelector('[data-role="alert-modal-confirm"]');
    const cancelButton = modal.querySelector('[data-role="alert-modal-cancel"]');

    modal.hidden = true;
    modal.__onConfirm = null;
    modal.__onCancel = null;
    modal.__confirmText = null;
    modal.__cancelText = null;
    modal.__isProcessing = false;
    document.body.classList.remove("modal-open");

    if (confirmButton) {
        confirmButton.disabled = false;
        confirmButton.textContent = "확인";
    }

    if (cancelButton) {
        cancelButton.disabled = false;
        cancelButton.textContent = "취소";
        cancelButton.hidden = true;
    }
}