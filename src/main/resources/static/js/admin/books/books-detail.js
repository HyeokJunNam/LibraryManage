document.addEventListener("DOMContentLoaded", () => {
    initBookBorrowArea();
});

function initBookBorrowArea() {
    const bookBorrowArea = document.getElementById("bookBorrowArea");

    if (!bookBorrowArea) {
        return;
    }

    const borrowsUrl = bookBorrowArea.dataset.borrowsUrl;
    const defaultPageSize = 5;

    function getCurrentPageSize() {
        const pagination = bookBorrowArea.querySelector("[data-page-size]");
        const pageSize = Number(pagination?.dataset.pageSize);

        if (!Number.isFinite(pageSize) || pageSize <= 0) {
            return defaultPageSize;
        }

        return pageSize;
    }

    async function fetchBorrows(page = 0) {
        if (!borrowsUrl) {
            return "";
        }

        const url = new URL(borrowsUrl, window.location.origin);

        url.searchParams.set("page", String(page));
        url.searchParams.set("size", String(getCurrentPageSize()));

        const response = await fetch(url.toString(), {
            method: "GET",
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        });

        if (!response.ok) {
            throw new Error(`대출 현황 조회 실패: ${response.status}`);
        }

        return response.text();
    }

    async function renderBorrows(page = 0) {
        try {
            const html = await fetchBorrows(page);

            if (!html || !html.trim()) {
                return;
            }

            bookBorrowArea.innerHTML = html;
        } catch (error) {
            console.error(error);
            await showSimpleAlert("대출 현황을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.");
        }
    }

    bookBorrowArea.addEventListener("click", (event) => {
        const pageButton = event.target.closest("[data-page]");

        if (!pageButton) {
            return;
        }

        if (
            pageButton.disabled ||
            pageButton.classList.contains("pagination__button--disabled") ||
            pageButton.classList.contains("is-disabled")
        ) {
            return;
        }

        event.preventDefault();

        const page = Number(pageButton.dataset.page);

        if (!Number.isFinite(page) || page < 0) {
            return;
        }

        renderBorrows(page);
    });

    renderBorrows(0);
}

function showSimpleAlert(message, title = "안내") {
    const modal = document.querySelector('[data-role="alert-modal"]');
    const titleElement = document.querySelector("#alert-modal-title");
    const messageElement = document.querySelector("#alert-modal-message");
    const confirmButton = document.querySelector('[data-role="alert-modal-confirm"]');
    const cancelButton = document.querySelector('[data-role="alert-modal-cancel"]');
    const backdrop = document.querySelector('[data-role="alert-modal-backdrop"]');

    if (!modal || !titleElement || !messageElement || !confirmButton || !cancelButton) {
        window.alert(message);
        return Promise.resolve();
    }

    titleElement.textContent = title;
    messageElement.textContent = message;
    confirmButton.textContent = "확인";
    cancelButton.hidden = true;

    modal.hidden = false;
    document.body.classList.add("modal-open");

    return new Promise(resolve => {
        let resolved = false;

        function close() {
            if (resolved) {
                return;
            }

            resolved = true;

            modal.hidden = true;
            document.body.classList.remove("modal-open");

            confirmButton.removeEventListener("click", handleConfirm);
            backdrop?.removeEventListener("click", handleBackdrop);
            document.removeEventListener("keydown", handleKeydown);

            resolve();
        }

        function handleConfirm() {
            close();
        }

        function handleBackdrop() {
            close();
        }

        function handleKeydown(event) {
            if (event.key === "Escape") {
                close();
            }
        }

        confirmButton.addEventListener("click", handleConfirm);
        backdrop?.addEventListener("click", handleBackdrop);
        document.addEventListener("keydown", handleKeydown);

        confirmButton.focus();
    });
}