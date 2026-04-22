import { createMemberProcess } from "./process-member.js";
import { createBookProcess } from "./process-book.js";

document.addEventListener("DOMContentLoaded", function () {
    const bookProcess = createBookProcess();
    const memberProcess = createMemberProcess({
        onMemberSelected: () => {
            bookProcess.reset();
        },
        onMemberCleared: () => {
            bookProcess.reset();
        }
    });

    memberProcess.onBorrow(() => {
        const memberId = memberProcess.getSelectedMemberId();
        if (!memberId) return;

        bookProcess.activateBorrowMode();
    });

    memberProcess.onReturn(() => {
        const memberId = memberProcess.getSelectedMemberId();
        if (!memberId) return;

        bookProcess.reset();
    });
});