document.addEventListener("DOMContentLoaded", async () => {
    const panels = document.querySelectorAll("[data-fragment-url]");

    for (const panel of panels) {
        try {
            await window.TableLayout.reload(panel);
        } catch (error) {
            console.error("[Borrow Status Initial Load Error]:", error);
            panel.innerHTML = `
                <div class="ajax-error-card">
                    <div class="ajax-error-card__icon">⚠️</div>
                    <h3 class="ajax-error-card__title">목록을 불러오지 못했습니다</h3>
                    <p class="ajax-error-card__desc">잠시 후 다시 시도해주세요.</p>
                </div>
            `;
        }
    }
});