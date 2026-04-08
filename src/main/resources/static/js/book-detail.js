document.addEventListener("DOMContentLoaded", () => {
    bindDetailPageEvents();
    initializeBackToListButton();
    loadBookDetail();
});

function bindDetailPageEvents() {
    const borrowButton = getElement("borrow-button");
    const dummyButton1 = getElement("dummy-button-1");
    const dummyButton2 = getElement("dummy-button-2");

    if (borrowButton) {
        borrowButton.addEventListener("click", onBorrowClick);
    }

    if (dummyButton1) {
        dummyButton1.addEventListener("click", onDummyButtonClick);
    }

    if (dummyButton2) {
        dummyButton2.addEventListener("click", onDummyButtonClick);
    }
}

function initializeBackToListButton() {
    const backButton = getElement("back-to-list-button");

    if (!backButton) {
        return;
    }

    const returnUrl = resolveReturnUrl();
    backButton.setAttribute("href", returnUrl);

    backButton.addEventListener("click", (event) => {
        event.preventDefault();
        window.location.href = returnUrl;
    });
}

function resolveReturnUrl() {
    const queryReturnUrl = getReturnUrlFromQuery();
    if (isValidInternalListUrl(queryReturnUrl)) {
        return saveBookListReturnUrl(queryReturnUrl);
    }

    const savedReturnUrl = sessionStorage.getItem("bookListReturnUrl");
    if (isValidInternalListUrl(savedReturnUrl)) {
        return savedReturnUrl;
    }

    const referrerReturnUrl = getReturnUrlFromReferrer();
    if (isValidInternalListUrl(referrerReturnUrl)) {
        return saveBookListReturnUrl(referrerReturnUrl);
    }

    return "/library/books";
}

function getReturnUrlFromQuery() {
    const params = new URLSearchParams(window.location.search);
    return params.get("returnUrl");
}

function getReturnUrlFromReferrer() {
    const referrer = document.referrer;

    if (!referrer) {
        return null;
    }

    try {
        const referrerUrl = new URL(referrer, window.location.origin);

        if (referrerUrl.origin !== window.location.origin) {
            return null;
        }

        return referrerUrl.pathname + referrerUrl.search + referrerUrl.hash;
    } catch (error) {
        console.error(error);
        return null;
    }
}

function isValidInternalListUrl(url) {
    if (!url || typeof url !== "string") {
        return false;
    }

    const trimmed = url.trim();

    if (!trimmed.startsWith("/") || trimmed.startsWith("//")) {
        return false;
    }

    return trimmed === "/library/books" || trimmed.startsWith("/library/books?");
}

function saveBookListReturnUrl(url) {
    sessionStorage.setItem("bookListReturnUrl", url);
    return url;
}

async function loadBookDetail() {
    const bookId = getBookId();

    if (!bookId) {
        hideActionButtons();
        renderEmptyState();
        return;
    }

    setPageBookId(bookId);

    try {
        const response = await fetch(`/api/books/${encodeURIComponent(bookId)}`, {
            method: "GET",
            headers: buildRequestHeaders()
        });

        if (!response.ok) {
            hideActionButtons();
            renderEmptyState();
            return;
        }

        const data = await safeJson(response);
        const book = normalizeBookResponse(data);

        renderBook(book);
    } catch (error) {
        console.error(error);
        hideActionButtons();
        renderEmptyState();
    }
}

function normalizeBookResponse(source) {
    const root = isObject(source) ? source : {};
    const result = isObject(root.result) ? root.result : {};
    const itemDetails = Array.isArray(result.bookItemDetails) ? result.bookItemDetails : [];

    return {
        id: firstDefined(result.id, null),
        isbn: firstNonEmpty(result.isbn, "-"),
        title: firstNonEmpty(result.title, "도서명 미등록"),
        author: firstNonEmpty(result.author, "저자 미등록"),
        publisher: firstNonEmpty(result.publisher, "출판사 미등록"),
        location: firstNonEmpty(result.location, "-"),
        bookItemCount: toSafeNumber(result.bookItemCount),
        borrowedCount: toSafeNumber(result.borrowedCount),
        availableCount: toSafeNumber(result.availableCount),
        unavailableCount: toSafeNumber(result.unavailableCount),
        canBorrow: toNullableBoolean(result.canBorrow),
        description: firstNonEmpty(result.description, "등록된 도서 설명이 없습니다."),
        bookItemDetails: itemDetails.map(normalizeBookItem)
    };
}

function normalizeBookItem(item) {
    const source = isObject(item) ? item : {};

    return {
        id: firstDefined(source.id, null),
        status: firstNonEmpty(source.status, "UNKNOWN"),
        borrowed: toNullableBoolean(source.borrowed),
        borrowedAt: formatDateTime(source.borrowedAt),
        dueAt: formatDateTime(source.dueAt),
        returnedAt: formatDateTime(source.returnedAt)
    };
}

function renderBook(book) {
    setText("book-title", book.title);
    setText("book-author", book.author);
    setText("book-publisher", book.publisher);
    setText("book-isbn", book.isbn);
    setText("book-location", book.location);
    setText("book-location-badge", book.location);

    setText("book-item-count", String(book.bookItemCount));
    setText("available-count", String(book.availableCount));
    setText("borrowed-count", String(book.borrowedCount));
    setText("book-description", book.description);

    setText("info-id", valueOrDefault(book.id, "-"));
    setText("info-title", book.title);
    setText("info-author", book.author);
    setText("info-publisher", book.publisher);
    setText("info-isbn", book.isbn);
    setText("info-location", book.location);

    setText("loan-book-item-count", String(book.bookItemCount));
    setText("loan-borrowed-count", String(book.borrowedCount));
    setText("loan-available-count", String(book.availableCount));
    setText("loan-unavailable-count", String(book.unavailableCount));
    setText("loan-can-borrow", getCanBorrowText(book.canBorrow));

    renderStatus(book);
    renderActionButtons(book);
    renderBookItems(book.bookItemDetails);
}

function renderStatus(book) {
    const badge = getElement("book-status-badge");
    const statusText = getElement("book-status-text");
    const status = getOverallStatusText(book);

    if (badge) {
        badge.textContent = status;
        badge.classList.remove("status-badge--available", "status-badge--rented");

        if (book.canBorrow === true) {
            badge.classList.add("status-badge--available");
        } else if (book.canBorrow === false) {
            badge.classList.add("status-badge--rented");
        }
    }

    if (statusText) {
        statusText.textContent = status;
    }
}

function renderActionButtons(book) {
    const borrowButton = getElement("borrow-button");

    if (borrowButton) {
        borrowButton.hidden = !(book.canBorrow === true && book.id);
    }
}

function renderBookItems(items) {
    const container = getElement("book-item-list");

    if (!container) {
        return;
    }

    if (!Array.isArray(items) || items.length === 0) {
        container.innerHTML = '<div class="summary-box">등록된 소장 도서 정보가 없습니다.</div>';
        return;
    }

    container.innerHTML = items.map((item) => {
        return `
            <div class="loan-info" style="margin-bottom: 16px;">
                <div class="loan-info__row">
                    <span class="loan-info__label">도서 개별 ID</span>
                    <strong class="loan-info__value">${escapeHtml(valueOrDefault(item.id, "-"))}</strong>
                </div>
                <div class="loan-info__row">
                    <span class="loan-info__label">상태</span>
                    <strong class="loan-info__value">${escapeHtml(getBookItemStatusText(item.status, item.borrowed))}</strong>
                </div>
                <div class="loan-info__row">
                    <span class="loan-info__label">대출 여부</span>
                    <strong class="loan-info__value">${escapeHtml(getBorrowedText(item.borrowed))}</strong>
                </div>
                <div class="loan-info__row">
                    <span class="loan-info__label">대출일</span>
                    <strong class="loan-info__value">${escapeHtml(valueOrDefault(item.borrowedAt, "-"))}</strong>
                </div>
                <div class="loan-info__row">
                    <span class="loan-info__label">반납 예정일</span>
                    <strong class="loan-info__value">${escapeHtml(valueOrDefault(item.dueAt, "-"))}</strong>
                </div>
                <div class="loan-info__row">
                    <span class="loan-info__label">반납일</span>
                    <strong class="loan-info__value">${escapeHtml(valueOrDefault(item.returnedAt, "-"))}</strong>
                </div>
            </div>
        `;
    }).join("");
}

function renderEmptyState() {
    setText("book-title", "도서 정보를 불러올 수 없습니다.");
    setText("book-author", "-");
    setText("book-publisher", "-");
    setText("book-isbn", "-");
    setText("book-location", "-");
    setText("book-location-badge", "위치 미등록");
    setText("book-item-count", "0");
    setText("available-count", "0");
    setText("borrowed-count", "0");
    setText("book-description", "도서 정보를 불러오지 못했습니다.");

    setText("info-id", "-");
    setText("info-title", "도서 정보를 불러올 수 없습니다.");
    setText("info-author", "-");
    setText("info-publisher", "-");
    setText("info-isbn", "-");
    setText("info-location", "-");

    setText("loan-book-item-count", "0");
    setText("loan-borrowed-count", "0");
    setText("loan-available-count", "0");
    setText("loan-unavailable-count", "0");
    setText("loan-can-borrow", "-");

    const container = getElement("book-item-list");
    if (container) {
        container.innerHTML = '<div class="summary-box">등록된 소장 도서 정보가 없습니다.</div>';
    }
}

async function onBorrowClick(event) {
    event.preventDefault();

    const bookId = getCurrentBookId();

    if (!bookId) {
        window.alert("도서 정보를 찾을 수 없습니다.");
        return;
    }

    if (!window.confirm("이 도서를 대출하시겠습니까?")) {
        return;
    }

    await submitBookAction({
        endpoint: `/api/books/${encodeURIComponent(bookId)}/borrow`,
        successMessage: "대출이 완료되었습니다.",
        fallbackErrorMessage: "대출 처리 중 오류가 발생했습니다.",
        onSuccess: loadBookDetail
    });
}

function onDummyButtonClick() {
    window.alert("준비 중인 기능입니다.");
}

async function submitBookAction({ endpoint, successMessage, fallbackErrorMessage, onSuccess }) {
    try {
        const response = await fetch(endpoint, {
            method: "POST",
            headers: buildRequestHeaders()
        });

        if (!response.ok) {
            const errorMessage = await extractErrorMessage(response);
            throw new Error(errorMessage || fallbackErrorMessage);
        }

        window.alert(successMessage);

        if (typeof onSuccess === "function") {
            await onSuccess();
        }
    } catch (error) {
        console.error(error);
        window.alert(error.message || fallbackErrorMessage);
    }
}

function hideActionButtons() {
    const borrowButton = getElement("borrow-button");

    if (borrowButton) {
        borrowButton.hidden = true;
    }
}

function setPageBookId(bookId) {
    const page = document.querySelector(".book-detail .page");

    if (page) {
        page.dataset.bookId = bookId;
    }
}

function getCurrentBookId() {
    const page = document.querySelector(".book-detail .page");
    const id = page?.dataset?.bookId;

    return id && id.trim() ? id.trim() : getBookIdFromUrl();
}

function getBookId() {
    const page = document.querySelector(".book-detail .page");
    const fromData = page?.dataset?.bookId?.trim();

    if (fromData) {
        return fromData;
    }

    return getBookIdFromUrl();
}

function getBookIdFromUrl() {
    const pathname = window.location.pathname || "";
    const segments = pathname.split("/").filter(Boolean);

    if (segments.length === 0) {
        return null;
    }

    const lastSegment = segments[segments.length - 1];

    if (lastSegment === "edit" && segments.length >= 2) {
        return segments[segments.length - 2];
    }

    return lastSegment || null;
}

function buildRequestHeaders() {
    const headers = {
        "Content-Type": "application/json"
    };

    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute("content");
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute("content");

    if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
    }

    return headers;
}

async function safeJson(response) {
    try {
        return await response.json();
    } catch (error) {
        console.error(error);
        return {};
    }
}

async function extractErrorMessage(response) {
    const contentType = response.headers.get("content-type") || "";

    try {
        if (contentType.includes("application/json")) {
            const data = await response.json();

            if (typeof data === "string" && data.trim()) {
                return data;
            }

            if (data?.message) {
                return data.message;
            }

            if (data?.error) {
                return data.error;
            }

            if (data?.result?.message) {
                return data.result.message;
            }

            return null;
        }

        const text = await response.text();
        return text?.trim() || null;
    } catch (error) {
        console.error(error);
        return null;
    }
}

function setText(role, value) {
    const element = getElement(role);

    if (element) {
        element.textContent = valueOrDefault(value, "-");
    }
}

function getElement(role) {
    return document.querySelector(`[data-role="${role}"]`);
}

function getOverallStatusText(book) {
    if (book.canBorrow === true) {
        return "대출 가능";
    }

    if (book.canBorrow === false) {
        if (book.availableCount > 0) {
            return "일부 대출 가능";
        }

        if (book.borrowedCount > 0) {
            return "전체 대출 중";
        }

        return "대출 불가";
    }

    return "상태 미확인";
}

function getCanBorrowText(canBorrow) {
    if (canBorrow === true) {
        return "가능";
    }

    if (canBorrow === false) {
        return "불가";
    }

    return "-";
}

function getBorrowedText(borrowed) {
    if (borrowed === true) {
        return "대출 중";
    }

    if (borrowed === false) {
        return "미대출";
    }

    return "-";
}

function getBookItemStatusText(status, borrowed) {
    const normalized = String(status || "").trim().toUpperCase();

    if (normalized === "AVAILABLE") {
        return "대출 가능";
    }

    if (normalized === "BORROWED") {
        return "대출 중";
    }

    if (normalized === "UNAVAILABLE") {
        return "이용 불가";
    }

    if (borrowed === true) {
        return "대출 중";
    }

    if (borrowed === false) {
        return "대출 가능";
    }

    return "상태 미확인";
}

function formatDateTime(value) {
    if (!value) {
        return null;
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return String(value);
    }

    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    const hh = String(date.getHours()).padStart(2, "0");
    const mi = String(date.getMinutes()).padStart(2, "0");

    return `${yyyy}.${mm}.${dd} ${hh}:${mi}`;
}

function firstDefined(...values) {
    for (const value of values) {
        if (value !== undefined && value !== null) {
            return value;
        }
    }

    return null;
}

function firstNonEmpty(...values) {
    for (const value of values) {
        if (typeof value === "string" && value.trim()) {
            return value.trim();
        }

        if (value !== undefined && value !== null && value !== "") {
            return String(value);
        }
    }

    return "";
}

function valueOrDefault(value, defaultValue) {
    if (value === undefined || value === null || value === "") {
        return defaultValue;
    }

    return value;
}

function toSafeNumber(value) {
    const number = Number(value);

    if (Number.isNaN(number) || !Number.isFinite(number)) {
        return 0;
    }

    return number;
}

function toNullableBoolean(value) {
    if (typeof value === "boolean") {
        return value;
    }

    return null;
}

function isObject(value) {
    return value !== null && typeof value === "object" && !Array.isArray(value);
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}