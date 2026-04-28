document.addEventListener("DOMContentLoaded", () => {
    const bookBorrowArea = document.getElementById("bookBorrowArea");

    if (!bookBorrowArea) {
        return;
    }

    const borrowsUrl = bookBorrowArea.dataset.borrowsUrl;
    const pageSize = 5;

    async function fetchBorrows(page = 0) {
        if (!borrowsUrl) {
            return "";
        }

        const url = new URL(borrowsUrl, window.location.origin);

        url.searchParams.set("page", String(page));
        url.searchParams.set("size", String(pageSize));

        const response = await fetch(url.toString(), {
            method: "GET",
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        });

        return response.text();
    }

    async function renderBorrows(page = 0) {
        const html = await fetchBorrows(page);

        if (!html || !html.trim()) {
            return;
        }

        bookBorrowArea.innerHTML = html;
    }

    bookBorrowArea.addEventListener("click", (event) => {
        const pageButton = event.target.closest("[data-page]");

        if (!pageButton) {
            return;
        }

        if (pageButton.disabled || pageButton.classList.contains("is-disabled")) {
            return;
        }

        const page = Number(pageButton.dataset.page);

        if (!Number.isFinite(page) || page < 0) {
            return;
        }

        renderBorrows(page);
    });

    renderBorrows(0);
});