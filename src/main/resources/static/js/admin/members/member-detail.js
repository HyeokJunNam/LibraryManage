// /js/admin/member-detail.js

document.addEventListener("DOMContentLoaded", () => {
    initializeMemberDetailPage();
});

const borrowHistoryState = {
    page: 0,
    size: 10
};

function initializeMemberDetailPage() {
    setupBackLinkState();
    setupKeyboardAccessibility();
    initializeBorrowHistoryState();
    loadBorrowHistory();
}

function setupBackLinkState() {
    const backLink = document.querySelector(".btn--secondary");

    if (!backLink) {
        return;
    }

    const previousPath = document.referrer ? new URL(document.referrer).pathname : "";

    if (previousPath === "/admin/members") {
        backLink.dataset.fromList = "true";
    }
}

function setupKeyboardAccessibility() {
    const infoCards = document.querySelectorAll(".info-card");

    infoCards.forEach((card) => {
        card.setAttribute("tabindex", "0");
    });
}

function initializeBorrowHistoryState() {
    const container = document.querySelector(".borrow-history");

    if (!container) {
        return;
    }

    const pageSize = Number(container.dataset.pageSize);

    if (Number.isInteger(pageSize) && pageSize > 0) {
        borrowHistoryState.size = pageSize;
    }
}

async function loadBorrowHistory(page = 0) {
    const container = document.querySelector(".borrow-history");
    const statusElement = document.getElementById("borrow-history-status");
    const tbody = document.getElementById("borrow-history-body");

    if (!container || !statusElement || !tbody) {
        return;
    }

    const memberId = container.dataset.memberId;

    if (!memberId) {
        renderBorrowHistoryError("회원 정보를 찾을 수 없습니다.");
        return;
    }

    borrowHistoryState.page = page;

    try {
        setBorrowHistoryStatus("대출 이력을 불러오는 중입니다.");
        clearBorrowHistoryRows();
        clearBorrowHistoryPagination();

        const query = new URLSearchParams({
            page: String(borrowHistoryState.page),
            size: String(borrowHistoryState.size)
        });

        const response = await fetch(`/api/members/${memberId}/borrows?${query.toString()}`, {
            method: "GET",
            headers: {
                Accept: "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const apiResponse = await response.json();
        const pageResponse = normalizeBorrowHistoryResponse(apiResponse);

        renderBorrowHistoryPage(pageResponse);
    } catch (error) {
        console.error("Failed to load borrow history:", error);
        renderBorrowHistoryError("대출 이력을 불러오지 못했습니다.");
    }
}

function normalizeBorrowHistoryResponse(apiResponse) {
    const result = apiResponse?.result;

    if (!result || !Array.isArray(result.content)) {
        throw new Error("Invalid page response");
    }

    return {
        content: result.content,
        page: Number(result.page ?? 0),
        size: Number(result.size ?? borrowHistoryState.size),
        totalElements: Number(result.totalElements ?? 0),
        totalPages: Number(result.totalPages ?? 0),
        first: Boolean(result.first),
        last: Boolean(result.last)
    };
}

function renderBorrowHistoryPage(pageResponse) {
    const borrows = Array.isArray(pageResponse.content) ? pageResponse.content : [];

    updateBorrowHistorySummary(pageResponse);

    if (pageResponse.totalElements === 0 || borrows.length === 0) {
        renderBorrowHistoryEmpty(pageResponse);
        return;
    }

    renderBorrowHistoryRows(borrows);
    renderBorrowHistoryPagination(pageResponse);
    hideBorrowHistoryStatus();
}

function renderBorrowHistoryRows(borrows) {
    const tbody = document.getElementById("borrow-history-body");

    clearBorrowHistoryRows();

    const rows = borrows.map((borrow) => {
        const borrowRecordId = borrow.borrowRecordId ?? "-";
        const bookId = borrow.bookId ?? "-";
        const bookItemId = borrow.bookItemId ?? "-";
        const bookTitle = borrow.bookTitle ?? "-";
        const borrowedAt = formatDateTime(borrow.borrowedAt);
        const dueAt = formatDateTime(borrow.dueAt);
        const returnedAt = formatDateTime(borrow.returnedAt);
        const status = resolveBorrowStatus(borrow);

        return `
            <tr>
                <td>${escapeHtml(String(borrowRecordId))}</td>
                <td>${escapeHtml(String(bookId))}</td>
                <td>${escapeHtml(String(bookItemId))}</td>
                <td>${escapeHtml(String(bookTitle))}</td>
                <td>${escapeHtml(borrowedAt)}</td>
                <td>${escapeHtml(dueAt)}</td>
                <td>${escapeHtml(returnedAt)}</td>
                <td>${renderBorrowStatusBadge(status)}</td>
            </tr>
        `;
    }).join("");

    tbody.innerHTML = rows;
}

function resolveBorrowStatus(borrow) {
    if (borrow.returnedAt) {
        return "반납완료";
    }

    if (borrow.dueAt) {
        const dueDate = new Date(borrow.dueAt);

        if (!Number.isNaN(dueDate.getTime()) && dueDate.getTime() < Date.now()) {
            return "연체";
        }
    }

    return "대출중";
}

function renderBorrowHistoryEmpty(pageResponse) {
    const tbody = document.getElementById("borrow-history-body");

    clearBorrowHistoryRows();
    renderBorrowHistoryPagination(pageResponse);
    hideBorrowHistoryStatus();

    tbody.innerHTML = `
        <tr>
            <td colspan="8" class="borrow-history__empty">대출 이력이 없습니다.</td>
        </tr>
    `;
}

function renderBorrowHistoryError(message) {
    const tbody = document.getElementById("borrow-history-body");

    clearBorrowHistoryRows();
    clearBorrowHistoryPagination();
    updateBorrowHistorySummary();
    setBorrowHistoryStatus(message);

    tbody.innerHTML = `
        <tr>
            <td colspan="8" class="borrow-history__empty">데이터를 표시할 수 없습니다.</td>
        </tr>
    `;
}

function renderBorrowHistoryPagination(pageResponse) {
    const pagination = document.getElementById("borrow-history-pagination");

    if (!pagination) {
        return;
    }

    const currentPage = Number(pageResponse.page ?? 0);
    const totalPages = Number(pageResponse.totalPages ?? 0);

    if (totalPages <= 1) {
        pagination.innerHTML = "";
        return;
    }

    const buttons = [];

    buttons.push(`
        <button type="button"
                class="pagination__button"
                data-page="${currentPage - 1}"
                ${pageResponse.first ? "disabled" : ""}>
            이전
        </button>
    `);

    for (let pageIndex = 0; pageIndex < totalPages; pageIndex += 1) {
        buttons.push(`
            <button type="button"
                    class="pagination__button ${pageIndex === currentPage ? "is-active" : ""}"
                    data-page="${pageIndex}"
                    aria-current="${pageIndex === currentPage ? "page" : "false"}">
                ${pageIndex + 1}
            </button>
        `);
    }

    buttons.push(`
        <button type="button"
                class="pagination__button"
                data-page="${currentPage + 1}"
                ${pageResponse.last ? "disabled" : ""}>
            다음
        </button>
    `);

    pagination.innerHTML = buttons.join("");
    bindBorrowHistoryPaginationEvents();
}

function bindBorrowHistoryPaginationEvents() {
    const buttons = document.querySelectorAll("#borrow-history-pagination .pagination__button[data-page]");

    buttons.forEach((button) => {
        button.addEventListener("click", () => {
            if (button.disabled) {
                return;
            }

            const nextPage = Number(button.dataset.page);

            if (!Number.isInteger(nextPage) || nextPage < 0) {
                return;
            }

            loadBorrowHistory(nextPage);
        });
    });
}

function clearBorrowHistoryPagination() {
    const pagination = document.getElementById("borrow-history-pagination");

    if (!pagination) {
        return;
    }

    pagination.innerHTML = "";
}

function updateBorrowHistorySummary(pageResponse) {
    const summary = document.getElementById("borrow-history-summary");

    if (!summary) {
        return;
    }

    if (!pageResponse) {
        summary.textContent = "총 0건";
        return;
    }

    const totalElements = Number(pageResponse.totalElements ?? 0);
    const currentPage = Number(pageResponse.page ?? 0);
    const size = Number(pageResponse.size ?? borrowHistoryState.size);
    const numberOfElements = Array.isArray(pageResponse.content) ? pageResponse.content.length : 0;

    if (totalElements === 0 || numberOfElements === 0) {
        summary.textContent = "총 0건";
        return;
    }

    const start = currentPage * size + 1;
    const end = Math.min(start + numberOfElements - 1, totalElements);

    summary.textContent = `총 ${totalElements}건 · ${start}-${end} 표시`;
}

function clearBorrowHistoryRows() {
    const tbody = document.getElementById("borrow-history-body");

    if (!tbody) {
        return;
    }

    tbody.innerHTML = "";
}

function setBorrowHistoryStatus(message) {
    const statusElement = document.getElementById("borrow-history-status");

    if (!statusElement) {
        return;
    }

    statusElement.textContent = message;
    statusElement.hidden = false;
    statusElement.style.display = "";
}

function hideBorrowHistoryStatus() {
    const statusElement = document.getElementById("borrow-history-status");

    if (!statusElement) {
        return;
    }

    statusElement.hidden = true;
    statusElement.style.display = "none";
}

function formatDateTime(value) {
    if (!value) {
        return "-";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return value;
    }

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hour = String(date.getHours()).padStart(2, "0");
    const minute = String(date.getMinutes()).padStart(2, "0");

    return `${year}-${month}-${day} ${hour}:${minute}`;
}

function renderBorrowStatusBadge(status) {
    const normalizedStatus = escapeHtml(status);
    let modifierClass = "status-badge--default";

    if (status === "대출중") {
        modifierClass = "status-badge--borrowing";
    } else if (status === "반납완료") {
        modifierClass = "status-badge--returned";
    } else if (status === "연체") {
        modifierClass = "status-badge--overdue";
    }

    return `<span class="status-badge ${modifierClass}">${normalizedStatus}</span>`;
}

function escapeHtml(value) {
    return value
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}