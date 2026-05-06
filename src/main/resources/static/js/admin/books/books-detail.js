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

            bookBorrowArea.innerHTML = `
                <section class="book-borrow-card">
                    <div class="book-borrow-card__head">
                        <div>
                            <h3 class="book-borrow-card__title">대출 현황</h3>
                            <p class="book-borrow-card__desc">
                                대출 현황을 불러오지 못했습니다.
                            </p>
                        </div>
                    </div>

                    <div class="book-borrow-empty">
                        <div class="book-borrow-empty__icon">⚠️</div>
                        <h4 class="book-borrow-empty__title">대출 현황 조회 실패</h4>
                        <p class="book-borrow-empty__desc">
                            잠시 후 다시 시도해주세요.
                        </p>
                    </div>
                </section>
            `;
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