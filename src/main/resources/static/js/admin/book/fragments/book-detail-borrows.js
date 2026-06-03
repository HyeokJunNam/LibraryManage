document.addEventListener("DOMContentLoaded", () => {
    initBookBorrowPanel();
});

function initBookBorrowPanel() {
    const borrowPanel = document.getElementById("bookDetailBorrowsPanel");

    if (!borrowPanel || borrowPanel.dataset.initialized === "true") {
        return;
    }

    borrowPanel.dataset.initialized = "true";

    reloadBorrowPanel(borrowPanel);
}

async function reloadBorrowPanel(panel) {
    try {
        if (!window.TableLayout || typeof window.TableLayout.reload !== "function") {
            throw new Error("TableLayout.reload을 찾을 수 없습니다.");
        }

        await window.TableLayout.reload(panel);
    } catch (error) {
        console.error(error);
        renderBorrowError(panel);
    }
}

function renderBorrowError(target) {
    const template = document.getElementById("bookBorrowErrorTemplate");

    if (!template) {
        target.replaceChildren();
        return;
    }

    target.replaceChildren(template.content.cloneNode(true));
}