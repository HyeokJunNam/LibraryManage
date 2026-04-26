import { renderAsyncPagination } from "../common/async-pagination.js";

export function createMemberProcess({ onMemberSelected, onMemberCleared } = {}) {
    const modal = document.getElementById("memberSearchModal");
    if (!modal) {
        return {
            getSelectedMemberId() {
                return "";
            },
            onBorrow() {},
            onReturn() {},
            reset() {}
        };
    }

    const openButtons = document.querySelectorAll('[data-role="open-member-search"]');
    const closeButtons = document.querySelectorAll('[data-role="close-member-search"]');

    const searchForm = document.getElementById("memberSearchForm");
    const searchTypeSelect = searchForm?.querySelector('select[name="searchType"]');
    const keywordInput = searchForm?.querySelector('input[name="keyword"]');

    const emptyState = document.getElementById("memberSearchEmpty");
    const loadingState = document.getElementById("memberSearchLoading");
    const noResultPanel = document.getElementById("memberSearchNoResultPanel");
    const resultPanel = document.getElementById("memberSearchResultPanel");
    const resultCount = document.getElementById("memberSearchCount");
    const resultRows = document.getElementById("memberSearchResultRows");
    const rowTemplate = document.getElementById("memberSearchRowTemplate");
    const paginationContainer = document.getElementById("memberSearchPagination");

    const selectedMemberEmpty = document.getElementById("selectedMemberEmpty");
    const selectedMemberResult = document.getElementById("selectedMemberResult");
    const selectedMemberName = document.getElementById("selectedMemberName");
    const selectedMemberMeta = document.getElementById("selectedMemberMeta");
    const selectedMemberNo = document.getElementById("selectedMemberNo");
    const selectedMemberEmail = document.getElementById("selectedMemberEmail");

    const memberActionFooter = document.getElementById("memberActionFooter");
    const borrowActionButton = document.querySelector('[data-role="borrow-action"]');
    const returnActionButton = document.querySelector('[data-role="return-action"]');

    const defaultSearchType = searchTypeSelect?.options[0]?.value ?? "";

    const searchState = {
        searchType: defaultSearchType,
        keyword: "",
        page: 0,
        size: 5
    };

    function normalizeMember(rawMember) {
        return {
            id: String(rawMember.id ?? ""),
            name: rawMember.name ?? "",
            memberNo: rawMember.memberNo ?? "",
            email: rawMember.email ?? "",
            phone: rawMember.phone ?? "",
            loginId: rawMember.loginId ?? ""
        };
    }

    function bindFields(root, data, fallback = "-") {
        root.querySelectorAll("[data-field]").forEach((element) => {
            const fieldName = element.dataset.field;
            const value = data[fieldName];

            element.textContent = value === null || value === undefined || value === ""
                ? fallback
                : String(value);
        });
    }

    function renderRows(container, items, createRow) {
        if (!container) return;

        container.innerHTML = "";

        const fragment = document.createDocumentFragment();

        items.forEach((item, index) => {
            const row = createRow(item, index);

            if (row) {
                fragment.appendChild(row);
            }
        });

        container.appendChild(fragment);
    }

    function setActionButtonsEnabled(enabled) {
        [borrowActionButton, returnActionButton].forEach((button) => {
            if (!button) return;
            button.disabled = !enabled;
        });
    }

    function showActionFooter() {
        if (!memberActionFooter) return;
        memberActionFooter.classList.remove("is-hidden");
        memberActionFooter.setAttribute("aria-hidden", "false");
    }

    function hideActionFooter() {
        if (!memberActionFooter) return;
        memberActionFooter.classList.add("is-hidden");
        memberActionFooter.setAttribute("aria-hidden", "true");
    }

    function resetSelectedMember() {
        if (selectedMemberResult) {
            delete selectedMemberResult.dataset.memberId;
        }

        if (selectedMemberName) {
            selectedMemberName.textContent = "-";
        }

        if (selectedMemberMeta) {
            selectedMemberMeta.textContent = "선택된 회원의 기본 정보를 확인할 수 있습니다.";
        }

        if (selectedMemberNo) {
            selectedMemberNo.textContent = "-";
        }

        if (selectedMemberEmail) {
            selectedMemberEmail.textContent = "-";
        }

        selectedMemberResult?.classList.add("is-hidden");
        selectedMemberEmpty?.classList.remove("is-hidden");

        hideActionFooter();
        setActionButtonsEnabled(false);
        onMemberCleared?.();
    }

    function getSelectedMemberId() {
        return selectedMemberResult?.dataset.memberId ?? "";
    }

    function openModal() {
        modal.classList.remove("is-hidden");
        modal.setAttribute("aria-hidden", "false");
        window.setTimeout(() => keywordInput?.focus(), 0);
    }

    function hideAllSearchStates() {
        emptyState?.classList.add("is-hidden");
        loadingState?.classList.add("is-hidden");
        noResultPanel?.classList.add("is-hidden");
        resultPanel?.classList.add("is-hidden");
    }

    function clearResultRows() {
        if (resultRows) {
            resultRows.innerHTML = "";
        }

        if (resultCount) {
            resultCount.textContent = "0";
        }

        if (paginationContainer) {
            paginationContainer.innerHTML = "";
            paginationContainer.classList.add("is-hidden");
        }
    }

    function clearSelectedRows() {
        resultRows?.querySelectorAll('[data-role="select-member"]').forEach((row) => {
            row.classList.remove("is-selected");
        });
    }

    function showInitialState() {
        hideAllSearchStates();
        clearResultRows();
        emptyState?.classList.remove("is-hidden");
    }

    function showLoadingState() {
        hideAllSearchStates();
        clearResultRows();
        loadingState?.classList.remove("is-hidden");
    }

    function showNoResultState() {
        hideAllSearchStates();
        clearResultRows();
        noResultPanel?.classList.remove("is-hidden");
    }

    function resetSearchForm() {
        if (searchForm) {
            searchForm.reset();
        }

        if (searchTypeSelect) {
            searchTypeSelect.value = defaultSearchType;
        }

        if (keywordInput) {
            keywordInput.value = "";
        }

        searchState.searchType = defaultSearchType;
        searchState.keyword = "";
        searchState.page = 0;
    }

    function resetSearchModal() {
        resetSearchForm();
        showInitialState();
    }

    function closeModal() {
        modal.classList.add("is-hidden");
        modal.setAttribute("aria-hidden", "true");
        resetSearchModal();
    }

    function createMemberSearchRowElement(rawMember) {
        if (!rowTemplate) return null;

        const member = normalizeMember(rawMember);
        const row = rowTemplate.content.firstElementChild.cloneNode(true);

        row.dataset.memberId = member.id;
        row.dataset.member = encodeURIComponent(JSON.stringify(member));

        bindFields(row, member);

        return row;
    }

    function renderResultRows(members) {
        renderRows(resultRows, members, createMemberSearchRowElement);
    }

    function showResultState(pageResult) {
        hideAllSearchStates();
        resultPanel?.classList.remove("is-hidden");

        const members = Array.isArray(pageResult.content) ? pageResult.content : [];
        const totalElements = Number(pageResult.totalElements ?? members.length);
        const currentPage = Number(pageResult.page ?? 0);
        const totalPages = Math.max(1, Number(pageResult.totalPages ?? 0));

        if (resultCount) {
            resultCount.textContent = String(totalElements);
        }

        renderResultRows(members);

        if (paginationContainer) {
            paginationContainer.classList.remove("is-hidden");

            renderAsyncPagination(paginationContainer, {
                currentPage,
                totalPages,
                visiblePages: 5,
                onPageChange: (nextPage) => {
                    loadMembers(nextPage);
                }
            });
        }
    }

    function applySelectedMember(member) {
        if (!member) return;

        selectedMemberEmpty?.classList.add("is-hidden");
        selectedMemberResult?.classList.remove("is-hidden");

        if (selectedMemberResult) {
            selectedMemberResult.dataset.memberId = member.id || "";
        }

        if (selectedMemberName) {
            selectedMemberName.textContent = member.name || "-";
        }

        if (selectedMemberMeta) {
            const metaParts = [];

            if (member.memberNo) {
                metaParts.push(`회원번호 ${member.memberNo}`);
            }

            if (member.email) {
                metaParts.push(member.email);
            }

            selectedMemberMeta.textContent = metaParts.length > 0 ? metaParts.join(" · ") : "-";
        }

        if (selectedMemberNo) {
            selectedMemberNo.textContent = member.memberNo || "-";
        }

        if (selectedMemberEmail) {
            selectedMemberEmail.textContent = member.email || "-";
        }

        showActionFooter();
        setActionButtonsEnabled(true);
        onMemberSelected?.(member);
    }

    async function fetchMembers({ searchType, keyword, page, size }) {
        const params = new URLSearchParams();

        params.set(searchType, keyword);
        params.set("page", String(page));
        params.set("size", String(size));

        const payload = await apiGet(`/api/members?${params.toString()}`);

        return payload?.result ?? {};
    }

    async function loadMembers(page = 0) {
        try {
            searchState.page = page;
            showLoadingState();

            const pageResult = await fetchMembers(searchState);
            const members = Array.isArray(pageResult.content) ? pageResult.content : [];

            if (members.length === 0) {
                showNoResultState();
                return;
            }

            showResultState(pageResult);
        } catch (error) {
            console.error(error);
            alert(error?.message || "회원 조회 중 오류가 발생했습니다.");
            showNoResultState();
        }
    }

    function handleSearchSubmit(event) {
        event.preventDefault();

        const searchType = searchTypeSelect?.value ?? defaultSearchType;
        const keyword = keywordInput?.value.trim() ?? "";

        if (!keyword) {
            alert("검색어를 입력해 주세요.");
            keywordInput?.focus();
            return;
        }

        searchState.searchType = searchType;
        searchState.keyword = keyword;
        searchState.page = 0;

        loadMembers(0);
    }

    function handleRowSelection(row) {
        const raw = row.dataset.member;
        if (!raw) return;

        try {
            const member = normalizeMember(JSON.parse(decodeURIComponent(raw)));

            clearSelectedRows();
            row.classList.add("is-selected");
            applySelectedMember(member);
            closeModal();
        } catch (error) {
            console.error("선택한 회원 데이터 파싱 실패", error);
        }
    }

    openButtons.forEach((button) => {
        button.addEventListener("click", openModal);
    });

    closeButtons.forEach((button) => {
        button.addEventListener("click", closeModal);
    });

    searchForm?.addEventListener("submit", handleSearchSubmit);

    resultRows?.addEventListener("click", (event) => {
        const row = event.target.closest('[data-role="select-member"]');
        if (!row) return;

        handleRowSelection(row);
    });

    resultRows?.addEventListener("keydown", (event) => {
        if (event.key !== "Enter" && event.key !== " ") return;

        const row = event.target.closest('[data-role="select-member"]');
        if (!row) return;

        event.preventDefault();
        handleRowSelection(row);
    });

    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape" && !modal.classList.contains("is-hidden")) {
            closeModal();
        }
    });

    resetSelectedMember();
    showInitialState();

    return {
        getSelectedMemberId,
        reset: resetSelectedMember,
        onBorrow(handler) {
            borrowActionButton?.addEventListener("click", handler);
        },
        onReturn(handler) {
            returnActionButton?.addEventListener("click", handler);
        }
    };
}