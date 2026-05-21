import { createBorrowBookProcess } from "./process-book-borrow.js";
import { createReturnBookProcess } from "./process-book-return.js";
import { createBookSearchModal } from "../modal/book-search-modal.js";

export function createBookProcess({
                                      getSelectedMemberId
                                  } = {}) {
    const bookPanel = document.getElementById("bookPanel");
    const bookPanelEmpty = document.getElementById("bookPanelEmpty");

    const bookBorrowModePanel = document.getElementById("bookBorrowModePanel");
    const bookReturnModePanel = document.getElementById("bookReturnModePanel");

    let borrowProcess;
    let returnProcess;

    function setHidden(element, hidden) {
        element?.classList.toggle("is-hidden", hidden);
    }

    function setBookPanelMode(mode) {
        bookPanel?.setAttribute("data-mode", mode);
    }

    function hideAllModePanels() {
        setHidden(bookBorrowModePanel, true);
        setHidden(bookReturnModePanel, true);
    }

    function showIdlePanel() {
        setBookPanelMode("idle");

        setHidden(bookPanelEmpty, false);
        hideAllModePanels();
    }

    function showBorrowPanel() {
        setBookPanelMode("borrow");

        setHidden(bookPanelEmpty, true);
        setHidden(bookBorrowModePanel, false);
        setHidden(bookReturnModePanel, true);
    }

    function showReturnPanel() {
        setBookPanelMode("return");

        setHidden(bookPanelEmpty, true);
        setHidden(bookBorrowModePanel, true);
        setHidden(bookReturnModePanel, false);
    }

    function createReturnPanelUrl(memberId) {
        return `/admin/members/${encodeURIComponent(memberId)}/borrows/list`;
    }

    function setReturnPanelUrl(memberId) {
        if (!bookReturnModePanel) return "";

        const url = createReturnPanelUrl(memberId);

        bookReturnModePanel.dataset.fragmentUrl = url;
        bookReturnModePanel.dataset.currentUrl = url;

        delete bookReturnModePanel.dataset.currentSearchTarget;

        return url;
    }

    async function reloadReturnPanel() {
        const memberId = typeof getSelectedMemberId === "function"
            ? getSelectedMemberId()
            : "";

        if (!memberId || !bookReturnModePanel) {
            return;
        }

        const url = setReturnPanelUrl(memberId);

        if (!window.TableLayout?.reload) {
            throw new Error("TableLayout.reload을 찾을 수 없습니다.");
        }

        await window.TableLayout.reload(bookReturnModePanel, url);

        returnProcess?.refresh?.();
    }

    const bookSearchModalController = createBookSearchModal({
        getConfirmedBooks: () => borrowProcess?.getBooks?.() ?? [],
        onConfirmSelected: (books) => {
            borrowProcess?.replaceBooks?.(books);
        }
    });

    borrowProcess = createBorrowBookProcess({
        getSelectedMemberId,
        bookSearchModalController,
        onActivateBorrowMode: showBorrowPanel
    });

    returnProcess = createReturnBookProcess({
        onActivateReturnMode: showReturnPanel,
        reloadReturnPanel
    });

    function activateBorrowMode() {
        borrowProcess?.activate?.();
    }

    function activateReturnMode() {
        returnProcess?.activate?.();
    }

    function replaceBorrowBooks(books) {
        borrowProcess?.replaceBooks?.(books);
    }

    function clearBorrowBooks() {
        borrowProcess?.clear?.();
    }

    function reset() {
        borrowProcess?.clear?.();
        returnProcess?.clear?.();
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