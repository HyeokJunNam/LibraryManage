document.addEventListener("DOMContentLoaded", () => {
    initBookBorrowArea();
});

async function initBookBorrowArea() {
    const bookBorrowArea = document.getElementById("bookBorrowArea");

    if (!bookBorrowArea || bookBorrowArea.dataset.initialized === "true") {
        return;
    }

    bookBorrowArea.dataset.initialized = "true";

    const borrowsUrl = bookBorrowArea.dataset.borrowsUrl;

    if (!borrowsUrl) {
        console.error("대출 현황 URL이 정의되지 않았습니다.");
        renderBorrowError(bookBorrowArea);
        return;
    }

    try {
        const html = await fetchBorrowPanelHtml(borrowsUrl);

        if (!html || !html.trim()) {
            renderBorrowError(bookBorrowArea);
            return;
        }

        bookBorrowArea.innerHTML = html;

        initializeTableLayout(bookBorrowArea);
    } catch (error) {
        console.error(error);
        renderBorrowError(bookBorrowArea);
    }

    bookBorrowArea.addEventListener("table-layout:updated", event => {
        console.debug("대출 현황 테이블이 비동기로 갱신되었습니다.", event.detail);
    });
}

async function fetchBorrowPanelHtml(url) {
    const response = await fetch(url, {
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

function initializeTableLayout(root) {
    if (window.TableLayout && typeof window.TableLayout.initialize === "function") {
        window.TableLayout.initialize(root);
    }
}

function renderBorrowError(target) {
    const template = document.getElementById("bookBorrowErrorTemplate");

    if (!template) {
        target.replaceChildren();
        return;
    }

    target.replaceChildren(template.content.cloneNode(true));
}