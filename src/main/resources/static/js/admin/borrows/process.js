import { createMemberProcess } from "./fragments/process-member.js";
import { createBookProcess } from "./fragments/process-book.js";

document.addEventListener("DOMContentLoaded", function () {
    let memberProcess;

    const bookProcess = createBookProcess({
        getSelectedMemberId: () => memberProcess?.getSelectedMemberId()
    });

    memberProcess = createMemberProcess({
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

        bookProcess.activateReturnMode();
    });
});