import { createBorrowBookProcess } from "./book-borrow.js";
import { createReturnBookProcess } from "./book-return.js";
import { createBookSearchModal } from "../modal/book-search-modal.js";

export function createBookProcess({
                                      getSelectedMemberId
                                  } = {}) {
    const bookPanel = document.getElementById("bookPanel");
    const bookPanelContent = document.getElementById("bookPanelContent");

    const idlePanelPrototype = bookPanelContent?.firstElementChild?.cloneNode(true) ?? null;

    let currentMode = "idle";
    let borrowProcess = null;
    let returnProcess = null;

    const bookSearchModalController = createBookSearchModal({
        getConfirmedBooks: () => borrowProcess?.getBooks?.() ?? [],
        onConfirmSelected: (books) => {
            if (currentMode !== "borrow") {
                showBorrowPanel().then(() => {
                    borrowProcess?.replaceBooks?.(books);
                });
                return;
            }

            borrowProcess?.replaceBooks?.(books);
        }
    });

    function setBookPanelMode(mode) {
        currentMode = mode;
        bookPanel?.setAttribute("data-mode", mode);
    }

    function getSelectedMemberIdValue() {
        return typeof getSelectedMemberId === "function"
            ? String(getSelectedMemberId() ?? "").trim()
            : "";
    }

    function destroyCurrentProcesses() {
        borrowProcess?.destroy?.();
        returnProcess?.destroy?.();

        borrowProcess = null;
        returnProcess = null;
    }

    function mountPanel(panelElement) {
        if (!bookPanelContent || !panelElement) {
            return;
        }

        bookPanelContent.replaceChildren(panelElement);
    }

    function createIdlePanelElement() {
        return idlePanelPrototype?.cloneNode(true) ?? document.createElement("section");
    }

    async function fetchFragmentElement(url, expectedSelector) {
        const response = await fetch(url, {
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        });

        if (!response.ok) {
            throw new Error("패널을 불러오지 못했습니다.");
        }

        const html = await response.text();
        const parser = new DOMParser();
        const documentFragment = parser.parseFromString(html, "text/html");

        const panel = documentFragment.querySelector(expectedSelector)
            ?? documentFragment.body.firstElementChild;

        if (!panel) {
            throw new Error("패널 fragment가 비어 있습니다.");
        }

        return panel;
    }

    function createBorrowPanelUrl() {
        return "/admin/circulation/borrows";
    }

    function createReturnPanelUrl(memberId) {
        return `/admin/circulation/members/${encodeURIComponent(memberId)}/returns`;
    }

    function showIdlePanel() {
        destroyCurrentProcesses();
        setBookPanelMode("idle");
        mountPanel(createIdlePanelElement());
    }

    async function showBorrowPanel() {
        destroyCurrentProcesses();
        setBookPanelMode("borrow");

        try {
            const panel = await fetchFragmentElement(
                createBorrowPanelUrl(),
                "#bookBorrowModePanel"
            );

            mountPanel(panel);

            borrowProcess = createBorrowBookProcess({
                root: panel,
                getSelectedMemberId,
                bookSearchModalController
            });
        } catch (error) {
            console.error(error);
            alert(error?.message || "대출 패널을 불러오는 중 오류가 발생했습니다.");
            showIdlePanel();
        }
    }

    async function showReturnPanel() {
        const memberId = getSelectedMemberIdValue();

        if (!memberId) {
            alert("반납할 회원을 먼저 선택해 주세요.");
            return;
        }

        destroyCurrentProcesses();
        setBookPanelMode("return");

        try {
            const url = createReturnPanelUrl(memberId);

            const panel = await fetchFragmentElement(
                url,
                "#bookReturnModePanel"
            );

            panel.dataset.fragmentUrl = url;
            panel.dataset.currentUrl = url;

            mountPanel(panel);

            returnProcess = createReturnBookProcess({
                root: panel,
                onReloadReturnPanel: reloadReturnPanel
            });
        } catch (error) {
            console.error(error);
            alert(error?.message || "반납 패널을 불러오는 중 오류가 발생했습니다.");
            showIdlePanel();
        }
    }

    async function reloadReturnPanel() {
        const memberId = getSelectedMemberIdValue();

        if (!memberId) {
            return;
        }

        const url = createReturnPanelUrl(memberId);

        const panel = await fetchFragmentElement(
            url,
            "#bookReturnModePanel"
        );

        panel.dataset.fragmentUrl = url;
        panel.dataset.currentUrl = url;

        returnProcess?.destroy?.();
        returnProcess = null;

        mountPanel(panel);

        returnProcess = createReturnBookProcess({
            root: panel,
            onReloadReturnPanel: reloadReturnPanel
        });
    }

    async function activateBorrowMode() {
        await showBorrowPanel();
    }

    async function activateReturnMode() {
        await showReturnPanel();
    }

    async function replaceBorrowBooks(books) {
        if (currentMode !== "borrow") {
            await showBorrowPanel();
        }

        borrowProcess?.replaceBooks?.(books);
    }

    function clearBorrowBooks() {
        borrowProcess?.clear?.();
    }

    function reset() {
        destroyCurrentProcesses();
        bookSearchModalController?.reset?.();
        showIdlePanel();
    }

    showIdlePanel();

    return {
        activateBorrowMode,
        activateReturnMode,
        replaceBorrowBooks,
        clearBorrowBooks,
        reset
    };
}