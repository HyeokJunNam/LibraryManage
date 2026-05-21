document.addEventListener("DOMContentLoaded", () => {
    initBookBorrowArea();
});

async function initBookBorrowArea() {
    // 1. 메인 페이지(book-detail.html)에 렌더링되어 있는 껍데기 영역을 찾습니다.
    const bookBorrowArea = document.getElementById("bookBorrowArea");

    if (!bookBorrowArea || bookBorrowArea.dataset.initialized === "true") {
        return;
    }
    bookBorrowArea.dataset.initialized = "true";

    // 2. 껍데기 영역에 저장된 최초 호출 URL을 가져옵니다.
    const borrowsUrl = bookBorrowArea.dataset.borrowsUrl;
    if (!borrowsUrl) {
        console.error("대출 현황 URL이 정의되지 않았습니다.");
        return;
    }

    // 3. 최초 1회 데이터를 불러와서 화면에 렌더링하는 함수
    async function loadInitialBorrows() {
        try {
            const response = await fetch(borrowsUrl, {
                method: "GET",
                headers: {
                    "X-Requested-With": "XMLHttpRequest"
                }
            });

            if (!response.ok) {
                throw new Error(`대출 현황 조회 실패: ${response.status}`);
            }

            const html = await response.text();

            // 응답받은 프래그먼트 HTML을 껍데기 영역 안에 주입합니다.
            if (html && html.trim()) {
                bookBorrowArea.innerHTML = html;

                // [중요] 동적으로 삽입된 폼(페이지네이션 등)을 table-layout.js가 추적할 수 있도록 수동 초기화
                if (window.TableLayout && typeof window.TableLayout.initialize === "function") {
                    window.TableLayout.initialize(bookBorrowArea);
                }
            }
        } catch (error) {
            console.error(error);
            // 에러 발생 시 공통 에러 UI 노출 (common.css에 정의된 ajax-error-card 활용)
            bookBorrowArea.innerHTML = `
                <div class="ajax-error-card">
                    <div class="ajax-error-card__icon">⚠️</div>
                    <h4 class="ajax-error-card__title">대출 현황을 불러오지 못했습니다</h4>
                    <p class="ajax-error-card__desc">서버와 통신 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.</p>
                </div>
            `;
        }
    }

    // 4. 스크립트가 실행되면 즉시 최초 데이터를 요청합니다.
    await loadInitialBorrows();

    // 5. 이후 페이지네이션 등은 table-layout.js가 전담하며, 갱신 성공 시 아래 이벤트가 발생합니다.
    bookBorrowArea.addEventListener("table-layout:updated", (event) => {
        console.debug("대출 현황 테이블이 비동기로 갱신되었습니다.", event.detail);
        // 향후 추가 툴팁 바인딩 등이 필요하다면 이 곳에 작성하시면 됩니다.
    });
}