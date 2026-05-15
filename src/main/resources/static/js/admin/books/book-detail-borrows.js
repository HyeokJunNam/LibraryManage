document.addEventListener("DOMContentLoaded", () => {
    initBookBorrowArea();
});

function initBookBorrowArea() {
    const bookBorrowArea = document.getElementById("bookBorrowArea");

    if (!bookBorrowArea || bookBorrowArea.dataset.initialized === "true") {
        return;
    }

    bookBorrowArea.dataset.initialized = "true";

    const borrowsUrl = bookBorrowArea.dataset.borrowsUrl;
    const defaultPageSize = 5;

    async function renderBorrows(page = 0) {
        if (!borrowsUrl) {
            return;
        }

        try {
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

            const html = await response.text();

            if (html && html.trim()) {
                bookBorrowArea.innerHTML = html;
            }
        } catch (error) {
            console.error(error);
            await showAlert("대출 현황을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.");
        }
    }

    function getCurrentPageSize() {
        const pagination = bookBorrowArea.querySelector("[data-page-size]");
        const pageSize = Number(pagination?.dataset.pageSize);

        return Number.isFinite(pageSize) && pageSize > 0
            ? pageSize
            : defaultPageSize;
    }

    bookBorrowArea.addEventListener("book-borrows:page-change", (event) => {
        const page = Number(event.detail?.page);

        if (Number.isFinite(page) && page >= 0) {
            renderBorrows(page);
        }
    });

    renderBorrows(0);
}