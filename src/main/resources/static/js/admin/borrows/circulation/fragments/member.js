import { createMemberSearchModal } from "../modal/member-search-modal.js";

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

    function toIdString(value) {
        if (value === null || value === undefined) {
            return "";
        }

        return String(value).trim();
    }

    function setHidden(element, hidden) {
        element?.classList.toggle("is-hidden", hidden);

        if (element) {
            element.setAttribute("aria-hidden", String(hidden));
        }
    }

    function setDisabled(element, disabled) {
        if (element) {
            element.disabled = disabled;
        }
    }

    function setText(element, value, fallback = "-") {
        if (!element) {
            return;
        }

        element.textContent = value === null || value === undefined || value === ""
            ? fallback
            : String(value);
    }

    function createSelectedMemberMeta(member) {
        const metaParts = [];

        if (member.memberNo) {
            metaParts.push(`회원 번호 ${member.memberNo}`);
        }

        if (member.email) {
            metaParts.push(member.email);
        }

        return metaParts.length > 0 ? metaParts.join(" · ") : "-";
    }

    function hasSelectedMember() {
        return !!getSelectedMemberId();
    }

    function updateActionButtons() {
        const disabled = !hasSelectedMember();

        setHidden(memberActionFooter, disabled);
        setDisabled(borrowActionButton, disabled);
        setDisabled(returnActionButton, disabled);
    }

    function getSelectedMemberId() {
        return toIdString(selectedMemberResult?.dataset.memberId);
    }

    function clearSelectedMember() {
        if (selectedMemberResult) {
            delete selectedMemberResult.dataset.memberId;
        }

        setHidden(selectedMemberResult, true);
        setHidden(selectedMemberEmpty, false);

        setText(selectedMemberName, "-");
        setText(selectedMemberMeta, "선택된 회원의 기본 정보를 확인할 수 있습니다.");
        setText(selectedMemberNo, "-");
        setText(selectedMemberEmail, "-");

        updateActionButtons();

        memberSearchModalController.reset();
        onMemberCleared?.();
    }

    function applySelectedMember(member) {
        if (!member) {
            return;
        }

        setHidden(selectedMemberEmpty, true);
        setHidden(selectedMemberResult, false);

        if (selectedMemberResult) {
            selectedMemberResult.dataset.memberId = toIdString(member.id);
        }

        setText(selectedMemberName, member.name);
        setText(selectedMemberMeta, createSelectedMemberMeta(member));
        setText(selectedMemberNo, member.memberNo);
        setText(selectedMemberEmail, member.email);

        updateActionButtons();

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

    clearSelectedMember();

    return {
        getSelectedMemberId,

        clearSelectedMember,
        reset: clearSelectedMember,

        applySelectedMember,

        onBorrow(handler) {
            borrowActionButton.__memberProcessHandler = handler;
        },

        onReturn(handler) {
            returnActionButton.__memberProcessHandler = handler;
        }
    };
}