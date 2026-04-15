document.addEventListener("DOMContentLoaded", function () {
    const searchForm = document.getElementById("searchForm");
    const conditionElement = document.getElementById("condition");
    const keywordElement = document.getElementById("keyword");
    const resetButton = document.getElementById("resetBtn");
    const bookListContainer = document.getElementById("bookListContainer");
    const bookCountElement = document.getElementById("bookCount");
    const selectedBookDetailContainer = document.getElementById("selectedBookDetailContainer");

    if (!searchForm || !conditionElement || !keywordElement || !bookListContainer || !bookCountElement || !selectedBookDetailContainer) {
        return;
    }

    bindSearchSubmit({
        searchForm,
        conditionElement,
        keywordElement,
        bookListContainer,
        bookCountElement,
        selectedBookDetailContainer
    });

    bindReset({
        resetButton,
        conditionElement,
        keywordElement,
        bookListContainer,
        bookCountElement,
        selectedBookDetailContainer
    });

    bindBookRowSelection(bookListContainer, selectedBookDetailContainer);
});

function bindSearchSubmit({
                              searchForm,
                              conditionElement,
                              keywordElement,
                              bookListContainer,
                              bookCountElement,
                              selectedBookDetailContainer
                          }) {
    searchForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const condition = conditionElement.value;
        const keyword = keywordElement.value.trim();
        const params = new URLSearchParams();

        if (keyword !== "") {
            params.set(condition, keyword);
        }

        renderLoading(bookListContainer);

        try {
            const requestUrl = params.toString()
                ? `/api/books?${params.toString()}`
                : "/api/books";

            const response = await fetch(requestUrl, {
                method: "GET",
                headers: {
                    "Accept": "application/json",
                    "X-Requested-With": "XMLHttpRequest"
                },
                credentials: "same-origin"
            });

            if (!response.ok) {
                throw new Error(`도서 조회 실패: ${response.status}`);
            }

            const data = await response.json();
            const result = extractResult(data);
            const books = extractBooks(result);
            const totalCount = extractTotalCount(result, books);

            renderBookList({
                books,
                totalCount,
                bookListContainer,
                bookCountElement
            });

            renderEmptyDetail(selectedBookDetailContainer);
            bindBookRowSelection(bookListContainer, selectedBookDetailContainer);
        } catch (error) {
            console.error(error);
            renderError(bookListContainer);
            bookCountElement.textContent = "0";
            renderEmptyDetail(selectedBookDetailContainer);
        }
    });
}

function bindReset({
                       resetButton,
                       conditionElement,
                       keywordElement,
                       bookListContainer,
                       bookCountElement,
                       selectedBookDetailContainer
                   }) {
    if (!resetButton) {
        return;
    }

    resetButton.addEventListener("click", function () {
        conditionElement.value = "title";
        keywordElement.value = "";
        bookListContainer.innerHTML = renderEmptyHtml(
            "조회된 도서가 없습니다.",
            "검색어를 입력해 주세요."
        );
        bookCountElement.textContent = "0";
        renderEmptyDetail(selectedBookDetailContainer);
    });
}

function bindBookRowSelection(bookListContainer, selectedBookDetailContainer) {
    const rows = bookListContainer.querySelectorAll(".borrow-book-row");

    rows.forEach(function (row) {
        row.addEventListener("click", function () {
            setActiveRow(rows, row);
            renderSelectedBookDetail(row, selectedBookDetailContainer);
        });

        row.addEventListener("keydown", function (event) {
            if (event.key !== "Enter" && event.key !== " ") {
                return;
            }

            event.preventDefault();
            setActiveRow(rows, row);
            renderSelectedBookDetail(row, selectedBookDetailContainer);
        });
    });
}

function setActiveRow(rows, selectedRow) {
    rows.forEach(function (row) {
        row.classList.remove("borrow-book-row--active");
    });

    selectedRow.classList.add("borrow-book-row--active");
}

function extractResult(data) {
    if (data && typeof data === "object" && data.result && typeof data.result === "object") {
        return data.result;
    }

    return {};
}

function extractBooks(result) {
    if (Array.isArray(result.content)) {
        return result.content;
    }

    return [];
}

function extractTotalCount(result, books) {
    if (typeof result.totalElements === "number") {
        return result.totalElements;
    }

    return books.length;
}

function renderBookList({
                            books,
                            totalCount,
                            bookListContainer,
                            bookCountElement
                        }) {
    bookCountElement.textContent = String(totalCount);

    if (!books.length) {
        bookListContainer.innerHTML = renderEmptyHtml(
            "조회된 도서가 없습니다.",
            "검색 조건을 변경해서 다시 시도해보세요."
        );
        return;
    }

    const rowsHtml = books.map(function (book, index) {
        const id = escapeHtml(toDisplayValue(book.id));
        const title = escapeHtml(toDisplayValue(book.title));
        const author = escapeHtml(toDisplayValue(book.author));
        const isbn = escapeHtml(toDisplayValue(book.isbn));
        const publisher = escapeHtml(toDisplayValue(book.publisher));
        const location = escapeHtml(toDisplayValue(book.location));
        const status = getDummyStatus(index);

        return `
            <div class="borrow-book-row"
                 tabindex="0"
                 data-book-id="${id}"
                 data-book-title="${title}"
                 data-book-author="${author}"
                 data-book-isbn="${isbn}"
                 data-book-publisher="${publisher}"
                 data-book-location="${location}"
                 data-book-status="${status}">
                <div class="borrow-book-row__cell borrow-book-row__cell--title">
                    <div class="borrow-book-card">
                        <div class="borrow-book-card__info">
                            <strong class="borrow-book-card__title">${title}</strong>
                        </div>
                    </div>
                </div>

                <div class="borrow-book-row__cell borrow-book-row__cell--author">
                    ${author}
                </div>

                <div class="borrow-book-row__cell borrow-book-row__cell--isbn">
                    ${isbn}
                </div>

                <div class="borrow-book-row__cell borrow-book-row__cell--status">
                    ${status}
                </div>
            </div>
        `;
    }).join("");

    bookListContainer.innerHTML = `
        <div class="borrow-book-list">
            <div class="borrow-book-table">
                <div class="borrow-book-table__header">
                    <div>도서명</div>
                    <div>저자</div>
                    <div>ISBN</div>
                    <div>상태</div>
                </div>
                ${rowsHtml}
            </div>
        </div>
    `;
}

function renderSelectedBookDetail(row, selectedBookDetailContainer) {
    const title = escapeHtml(toDisplayValue(row.dataset.bookTitle));
    const author = escapeHtml(toDisplayValue(row.dataset.bookAuthor));
    const isbn = escapeHtml(toDisplayValue(row.dataset.bookIsbn));
    const publisher = escapeHtml(toDisplayValue(row.dataset.bookPublisher));
    const location = escapeHtml(toDisplayValue(row.dataset.bookLocation));
    const status = escapeHtml(toDisplayValue(row.dataset.bookStatus));

    selectedBookDetailContainer.innerHTML = `
        <div class="borrow-detail">
            <section class="borrow-detail__section">
                <h4 class="borrow-detail__section-title">선택 도서 정보</h4>

                <div class="borrow-detail__grid">
                    <div class="borrow-detail__item">
                        <span class="borrow-detail__label">도서명</span>
                        <strong class="borrow-detail__value">${title}</strong>
                    </div>

                    <div class="borrow-detail__item">
                        <span class="borrow-detail__label">저자</span>
                        <strong class="borrow-detail__value">${author}</strong>
                    </div>

                    <div class="borrow-detail__item">
                        <span class="borrow-detail__label">ISBN</span>
                        <strong class="borrow-detail__value">${isbn}</strong>
                    </div>

                    <div class="borrow-detail__item">
                        <span class="borrow-detail__label">출판사</span>
                        <strong class="borrow-detail__value">${publisher}</strong>
                    </div>

                    <div class="borrow-detail__item">
                        <span class="borrow-detail__label">위치</span>
                        <strong class="borrow-detail__value">${location}</strong>
                    </div>

                    <div class="borrow-detail__item">
                        <span class="borrow-detail__label">상태</span>
                        <strong class="borrow-detail__value">${status}</strong>
                    </div>
                </div>
            </section>

            <section class="borrow-detail__section">
                <h4 class="borrow-detail__section-title">대출 회원 검색</h4>

                <form class="member-search-form" onsubmit="return false;">
                    <input
                        type="text"
                        class="member-search-form__input"
                        placeholder="회원명 또는 회원번호 검색">
                    <button type="button" class="btn btn--primary">검색</button>
                </form>

                <p class="member-search-placeholder">
                    회원 검색 기능은 다음 단계에서 연결 예정입니다.
                </p>
            </section>
        </div>
    `;
}

function renderEmptyDetail(selectedBookDetailContainer) {
    selectedBookDetailContainer.innerHTML = `
        <div class="empty-block">
            <div class="empty-block__icon">📝</div>
            <p class="empty-block__title">선택된 도서가 없습니다.</p>
            <p class="empty-block__desc">좌측 목록에서 도서를 선택해 주세요.</p>
        </div>
    `;
}

function renderLoading(bookListContainer) {
    bookListContainer.innerHTML = `
        <div class="empty-block">
            <div class="empty-block__icon">⏳</div>
            <p class="empty-block__title">도서를 조회하고 있습니다.</p>
            <p class="empty-block__desc">잠시만 기다려 주세요.</p>
        </div>
    `;
}

function renderError(bookListContainer) {
    bookListContainer.innerHTML = `
        <div class="empty-block">
            <div class="empty-block__icon">⚠️</div>
            <p class="empty-block__title">도서 목록을 불러오지 못했습니다.</p>
            <p class="empty-block__desc">잠시 후 다시 시도해 주세요.</p>
        </div>
    `;
}

function renderEmptyHtml(title, desc) {
    return `
        <div class="empty-block">
            <div class="empty-block__icon">📚</div>
            <p class="empty-block__title">${escapeHtml(title)}</p>
            <p class="empty-block__desc">${escapeHtml(desc)}</p>
        </div>
    `;
}

function getDummyStatus(index) {
    const dummyStatuses = ["대출 가능", "대출 중", "점검 중"];
    return dummyStatuses[index % dummyStatuses.length];
}

function toDisplayValue(value) {
    if (value === null || value === undefined || String(value).trim() === "") {
        return "-";
    }

    return String(value);
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}