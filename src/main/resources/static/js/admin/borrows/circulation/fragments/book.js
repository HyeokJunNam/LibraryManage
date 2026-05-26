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
        return "/admin/circulation/borrow";
    }

    function createReturnPanelUrl(memberId) {
        return `/admin/circulation/return/members/${encodeURIComponent(memberId)}/borrows`;
    }

    function getReturnPanel() {
        return document.getElementById("bookReturnModePanel");
    }

    function resolveReturnPanelUrl(memberId) {
        const panel = getReturnPanel();
        const panelUrl = panel?.dataset.fragmentUrl;

        return panelUrl || createReturnPanelUrl(memberId);
    }

    function syncReturnPanelUrl(panel, url) {
        if (!panel || !url) {
            return;
        }

        panel.dataset.fragmentUrl = url;
        panel.dataset.currentUrl = url;
    }

    async function reloadTableLayoutPanel(panel) {
        if (window.TableLayout && typeof window.TableLayout.reload === "function") {
            await window.TableLayout.reload(panel);
            return true;
        }

        return false;
    }

    function createReturnProcess(panel) {
        returnProcess?.destroy?.();

        returnProcess = createReturnBookProcess({
            root: panel,
            reloadReturnPanel
        });
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

            syncReturnPanelUrl(panel, url);
            mountPanel(panel);
            createReturnProcess(panel);
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

        const panel = getReturnPanel();

        if (!panel) {
            await showReturnPanel();
            return;
        }

        const url = resolveReturnPanelUrl(memberId);
        syncReturnPanelUrl(panel, url);

        try {
            const reloadedByTableLayout = await reloadTableLayoutPanel(panel);

            if (reloadedByTableLayout) {
                returnProcess?.refresh?.();
                return;
            }

            const refreshedPanel = await fetchFragmentElement(
                url,
                "#bookReturnModePanel"
            );

            syncReturnPanelUrl(refreshedPanel, url);
            mountPanel(refreshedPanel);
            createReturnProcess(refreshedPanel);
        } catch (error) {
            console.error(error);
            alert(error?.message || "반납 패널을 다시 불러오는 중 오류가 발생했습니다.");
        }
    }

    async function activateBorrowMode() {
        await showBorrowPanel();
    }

    async function activateReturnMode() {
        await showReturnPanel();
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
        reset
    };
}