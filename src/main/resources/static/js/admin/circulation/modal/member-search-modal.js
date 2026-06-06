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

    let hasLoadedOnce = false;
    let preloadPromise = null;

    function getResultMount() {
        return modal.querySelector('[data-role="member-search-result-mount"]');
    }

    function getSearchKeyword() {
        return modal.querySelector("[data-table-search-keyword]");
    }

    async function loadResultPanel() {
        const resultMount = getResultMount();
        if (!resultMount || !window.TableLayout?.reload) return;

        await window.TableLayout.reload(resultMount);
        hasLoadedOnce = true;
    }

    function preloadResultPanel() {
        if (hasLoadedOnce) return Promise.resolve();
        if (preloadPromise) return preloadPromise;

        preloadPromise = loadResultPanel().finally(() => {
            preloadPromise = null;
        });

        return preloadPromise;
    }

    async function open() {
        modal.classList.remove("is-hidden");
        modal.setAttribute("aria-hidden", "false");

        if (!hasLoadedOnce) {
            await preloadResultPanel();
        } else {
            loadResultPanel();
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
        close();
    }

    function selectRow(row) {
        const member = {
            id: row.dataset.memberId ?? "",
            loginId: row.dataset.loginId ?? "",
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

    window.setTimeout(() => {
        preloadResultPanel();
    }, 0);

    return {
        open,
        close,
        reset
    };
}