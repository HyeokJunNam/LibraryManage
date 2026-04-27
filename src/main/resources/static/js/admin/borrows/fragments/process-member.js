import { createMemberSearchModal } from "./process-member-search-modal.js";

export function createMemberProcess({ onMemberSelected, onMemberCleared } = {}) {
    const selectedMemberEmpty = document.getElementById("selectedMemberEmpty");
    const selectedMemberResult = document.getElementById("selectedMemberResult");
    const selectedMemberName = document.getElementById("selectedMemberName");
    const selectedMemberMeta = document.getElementById("selectedMemberMeta");
    const selectedMemberNo = document.getElementById("selectedMemberNo");
    const selectedMemberEmail = document.getElementById("selectedMemberEmail");

    const memberActionFooter = document.getElementById("memberActionFooter");
    const borrowActionButton = document.querySelector('[data-role="borrow-action"]');
    const returnActionButton = document.querySelector('[data-role="return-action"]');

    function setElementText(element, value, fallback = "-") {
        if (!element) return;

        element.textContent = value === null || value === undefined || value === ""
            ? fallback
            : String(value);
    }

    function setActionButtonsEnabled(enabled) {
        [borrowActionButton, returnActionButton].forEach((button) => {
            if (button) {
                button.disabled = !enabled;
            }
        });
    }

    function setActionFooterVisible(visible) {
        if (!memberActionFooter) return;

        memberActionFooter.classList.toggle("is-hidden", !visible);
        memberActionFooter.setAttribute("aria-hidden", String(!visible));
    }

    function createMemberMeta(member) {
        const metaParts = [];

        if (member.memberNo) {
            metaParts.push(`회원번호 ${member.memberNo}`);
        }

        if (member.email) {
            metaParts.push(member.email);
        }

        return metaParts.length > 0 ? metaParts.join(" · ") : "-";
    }

    function getSelectedMemberId() {
        return selectedMemberResult?.dataset.memberId ?? "";
    }

    function reset() {
        if (selectedMemberResult) {
            delete selectedMemberResult.dataset.memberId;
        }

        selectedMemberResult?.classList.add("is-hidden");
        selectedMemberEmpty?.classList.remove("is-hidden");

        setElementText(selectedMemberName, "-");
        setElementText(selectedMemberMeta, "선택된 회원의 기본 정보를 확인할 수 있습니다.");
        setElementText(selectedMemberNo, "-");
        setElementText(selectedMemberEmail, "-");

        setActionFooterVisible(false);
        setActionButtonsEnabled(false);

        memberSearchModalController.reset();
        onMemberCleared?.();
    }

    function applySelectedMember(member) {
        if (!member) return;

        selectedMemberEmpty?.classList.add("is-hidden");
        selectedMemberResult?.classList.remove("is-hidden");

        if (selectedMemberResult) {
            selectedMemberResult.dataset.memberId = member.id || "";
        }

        setElementText(selectedMemberName, member.name);
        setElementText(selectedMemberMeta, createMemberMeta(member));
        setElementText(selectedMemberNo, member.memberNo);
        setElementText(selectedMemberEmail, member.email);

        setActionFooterVisible(true);
        setActionButtonsEnabled(true);

        onMemberSelected?.(member);
    }

    const memberSearchModalController = createMemberSearchModal({
        onSelectMember: applySelectedMember
    });

    borrowActionButton?.addEventListener("click", () => {
        const handler = borrowActionButton.__memberProcessHandler;

        if (typeof handler === "function") {
            handler();
        }
    });

    returnActionButton?.addEventListener("click", () => {
        const handler = returnActionButton.__memberProcessHandler;

        if (typeof handler === "function") {
            handler();
        }
    });

    reset();

    return {
        getSelectedMemberId,
        reset,
        onBorrow(handler) {
            borrowActionButton.__memberProcessHandler = handler;
        },
        onReturn(handler) {
            returnActionButton.__memberProcessHandler = handler;
        }
    };
}