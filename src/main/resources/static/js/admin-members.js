document.addEventListener("DOMContentLoaded", () => {
    setupStatusModal();
    setupFilterForm();
});

function setupStatusModal() {
    const modal = document.getElementById("statusModal");
    const closeButton = document.getElementById("closeStatusModal");
    const cancelButton = document.getElementById("cancelStatusModal");
    const form = document.getElementById("statusChangeForm");
    const memberNameElement = document.getElementById("modalMemberName");
    const memberIdElement = document.getElementById("modalMemberId");
    const statusElement = document.getElementById("modalStatus");
    const toggleButtons = document.querySelectorAll(".js-status-toggle");

    if (
        !modal ||
        !closeButton ||
        !cancelButton ||
        !form ||
        !memberNameElement ||
        !memberIdElement ||
        !statusElement
    ) {
        return;
    }

    toggleButtons.forEach((button) => {
        button.addEventListener("click", () => {
            const memberId = button.dataset.memberId ?? "";
            const memberName = button.dataset.memberName ?? "회원";
            const currentStatus = button.dataset.currentStatus ?? "ACTIVE";

            memberIdElement.value = memberId;
            memberNameElement.textContent = memberName;
            statusElement.value = currentStatus;

            openModal(modal);
        });
    });

    closeButton.addEventListener("click", () => closeModal(modal));
    cancelButton.addEventListener("click", () => closeModal(modal));

    modal.addEventListener("click", (event) => {
        if (event.target.classList.contains("modal__backdrop")) {
            closeModal(modal);
        }
    });

    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape" && !modal.hidden) {
            closeModal(modal);
        }
    });

    form.addEventListener("submit", (event) => {
        event.preventDefault();

        const memberId = memberIdElement.value;
        const nextStatus = statusElement.value;

        console.log("상태 변경 요청", {
            memberId,
            status: nextStatus
        });

        closeModal(modal);
    });
}

function setupFilterForm() {
    const form = document.getElementById("memberFilterForm");
    const resetButton = document.getElementById("resetFilterButton");

    if (!form || !resetButton) {
        return;
    }

    form.addEventListener("submit", () => {
        const keywordInput = document.getElementById("keyword");

        if (!keywordInput) {
            return;
        }

        keywordInput.value = keywordInput.value.trim();
    });
}

function openModal(modal) {
    modal.hidden = false;
    document.body.classList.add("modal-open");
}

function closeModal(modal) {
    modal.hidden = true;
    document.body.classList.remove("modal-open");
}