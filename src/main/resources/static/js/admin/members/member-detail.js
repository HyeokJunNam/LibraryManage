(() => {
    const SELECTOR = Object.freeze({
        BORROW_HISTORY_HOST: "#memberBorrowHistoryHost"
    });

    document.addEventListener("DOMContentLoaded", initializeMemberDetailPage);

    async function initializeMemberDetailPage() {
        const host = document.querySelector(SELECTOR.BORROW_HISTORY_HOST);

        if (!host || !host.dataset.fragmentUrl) {
            return;
        }

        if (!window.TableLayout?.reload) {
            return;
        }

        try {
            await window.TableLayout.reload(host);
        } catch (error) {
            console.error("[Member Borrow History Load Error]:", error);
        }
    }
})();