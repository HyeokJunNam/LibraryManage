export function createMemberSearchModal({ onSelectMember } = {}) {
    const modal = document.getElementById("memberSearchModal");

    if (!modal) {
        return {
            open() {},
            close() {},
            reset() {}
        };
    }

    const openButtons = document.querySelectorAll('[data-role="open-member-search"]');
    const closeButtons = document.querySelectorAll('[data-role="close-member-search"]');

    function getResultPanel() {
        return modal.querySelector("#memberSearchResultPanel");
    }

    function getSearchKeyword() {
        return modal.querySelector("[data-table-search-keyword]");
    }

    async function open() {
        modal.classList.remove("is-hidden");
        modal.setAttribute("aria-hidden", "false");

        const resultPanel = getResultPanel();

        if (resultPanel && window.TableLayout?.reload) {
            await window.TableLayout.reload(resultPanel);
        }

        window.setTimeout(() => {
            getSearchKeyword()?.focus();
        }, 50);
    }

    function close() {
        modal.classList.add("is-hidden");
        modal.setAttribute("aria-hidden", "true");
    }

    function reset() {
        const resultPanel = getResultPanel();

        if (resultPanel) {
            resultPanel.innerHTML = "";
            delete resultPanel.dataset.currentUrl;
            delete resultPanel.dataset.currentSearchTarget;
        }

        close();
    }

    function selectRow(row) {
        const member = {
            id: row.dataset.memberId ?? "",
            memberNo: row.dataset.memberNo ?? "",
            name: row.dataset.memberName ?? "",
            email: row.dataset.memberEmail ?? ""
        };

        if (typeof onSelectMember === "function") {
            onSelectMember(member);
        }

        reset();
    }

    openButtons.forEach((button) => button.addEventListener("click", open));
    closeButtons.forEach((button) => button.addEventListener("click", reset));

    modal.addEventListener("click", (event) => {
        const row = event.target.closest('[data-role="select-member"]');
        if (!row) return;

        selectRow(row);
    });

    modal.addEventListener("keydown", (event) => {
        if (event.key !== "Enter" && event.key !== " ") return;

        const row = event.target.closest('[data-role="select-member"]');
        if (!row) return;

        event.preventDefault();
        selectRow(row);
    });

    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape" && !modal.classList.contains("is-hidden")) {
            reset();
        }
    });

    return {
        open,
        close,
        reset
    };
}