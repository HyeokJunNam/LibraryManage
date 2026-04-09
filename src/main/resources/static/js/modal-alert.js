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

    if (backdrop) {
        backdrop.addEventListener("click", closeAlertModal);
    }

    if (confirmButton) {
        confirmButton.addEventListener("click", () => {
            const onConfirm = modal.__onConfirm;

            closeAlertModal();

            if (typeof onConfirm === "function") {
                onConfirm();
            }
        });
    }
}

function openAlertModal({ title = "안내", message = "", confirmText = "확인", onConfirm = null }) {
    const modal = document.querySelector('[data-role="alert-modal"]');

    if (!modal) {
        window.alert(message);

        if (typeof onConfirm === "function") {
            onConfirm();
        }
        return;
    }

    const titleElement = modal.querySelector("#alert-modal-title");
    const messageElement = modal.querySelector("#alert-modal-message");
    const confirmButton = modal.querySelector('[data-role="alert-modal-confirm"]');

    if (titleElement) {
        titleElement.textContent = title;
    }

    if (messageElement) {
        messageElement.textContent = message;
    }

    if (confirmButton) {
        confirmButton.textContent = confirmText;
    }

    modal.__onConfirm = onConfirm;
    modal.hidden = false;
    document.body.classList.add("modal-open");

    if (confirmButton) {
        confirmButton.focus();
    }
}

function closeAlertModal() {
    const modal = document.querySelector('[data-role="alert-modal"]');

    if (!modal) {
        return;
    }

    modal.hidden = true;
    modal.__onConfirm = null;
    document.body.classList.remove("modal-open");
}