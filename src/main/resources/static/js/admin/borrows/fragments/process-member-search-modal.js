import { renderAsyncPagination } from "../../../common/async-pagination.js";

export function createMemberSearchModal({ onSelectMember } = {}) {
    const modal = document.getElementById("memberSearchModal");

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
            const value = data[element.dataset.field];

            element.textContent = value === null || value === undefined || value === ""
                ? fallback
                : String(value);
        });
    }

    function showOnlyState(visibleStateElement) {
        [
            emptyState,
            loadingState,
            noResultPanel,
            resultPanel
        ].forEach((element) => {
            element?.classList.toggle("is-hidden", element !== visibleStateElement);
        });
    }

    function clearResults() {
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

    function showInitialState() {
        clearResults();
        showOnlyState(emptyState);
    }

    function showLoadingState() {
        clearResults();
        showOnlyState(loadingState);
    }

    function showNoResultState() {
        clearResults();
        showOnlyState(noResultPanel);
    }

    function createMemberRow(rawMember) {
        if (!rowTemplate) return null;

        const member = normalizeMember(rawMember);
        const row = rowTemplate.content.firstElementChild.cloneNode(true);

        row.dataset.memberId = member.id;
        row.dataset.member = encodeURIComponent(JSON.stringify(member));

        bindFields(row, member);

        return row;
    }

    function renderRows(members) {
        if (!resultRows) return;

        resultRows.innerHTML = "";

        const fragment = document.createDocumentFragment();

        members.forEach((member) => {
            const row = createMemberRow(member);

            if (row) {
                fragment.appendChild(row);
            }
        });

        resultRows.appendChild(fragment);
    }

    function showResultState(pageResult) {
        const members = Array.isArray(pageResult.content) ? pageResult.content : [];
        const totalElements = Number(pageResult.totalElements ?? members.length);
        const currentPage = Number(pageResult.page ?? 0);
        const totalPages = Math.max(1, Number(pageResult.totalPages ?? 0));

        showOnlyState(resultPanel);

        if (resultCount) {
            resultCount.textContent = String(totalElements);
        }

        renderRows(members);

        if (!paginationContainer) return;

        paginationContainer.classList.remove("is-hidden");

        renderAsyncPagination(paginationContainer, {
            currentPage,
            totalPages,
            visiblePages: 5,
            onPageChange: loadMembers
        });
    }

    function resetSearchForm() {
        searchForm?.reset();

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

    function reset() {
        resetSearchForm();
        showInitialState();
    }

    function open() {
        if (!modal) return;

        modal.classList.remove("is-hidden");
        modal.setAttribute("aria-hidden", "false");

        window.setTimeout(() => keywordInput?.focus(), 0);
    }

    function close() {
        if (!modal) return;

        modal.classList.add("is-hidden");
        modal.setAttribute("aria-hidden", "true");

        reset();
    }

    async function fetchMembers() {
        const params = new URLSearchParams();

        params.set(searchState.searchType, searchState.keyword);
        params.set("page", String(searchState.page));
        params.set("size", String(searchState.size));

        const payload = await apiGet(`/api/members?${params.toString()}`);

        return payload?.result ?? {};
    }

    async function loadMembers(page = 0) {
        try {
            searchState.page = page;
            showLoadingState();

            const pageResult = await fetchMembers();
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

        const keyword = keywordInput?.value.trim() ?? "";

        if (!keyword) {
            alert("검색어를 입력해 주세요.");
            keywordInput?.focus();
            return;
        }

        searchState.searchType = searchTypeSelect?.value ?? defaultSearchType;
        searchState.keyword = keyword;
        searchState.page = 0;

        loadMembers(0);
    }

    function selectRow(row) {
        const raw = row.dataset.member;

        if (!raw) return;

        try {
            const member = normalizeMember(JSON.parse(decodeURIComponent(raw)));

            onSelectMember?.(member);
            close();
        } catch (error) {
            console.error("선택한 회원 데이터 파싱 실패", error);
        }
    }

    openButtons.forEach((button) => {
        button.addEventListener("click", open);
    });

    closeButtons.forEach((button) => {
        button.addEventListener("click", close);
    });

    searchForm?.addEventListener("submit", handleSearchSubmit);

    resultRows?.addEventListener("click", (event) => {
        const row = event.target.closest('[data-role="select-member"]');

        if (row) {
            selectRow(row);
        }
    });

    resultRows?.addEventListener("keydown", (event) => {
        if (event.key !== "Enter" && event.key !== " ") return;

        const row = event.target.closest('[data-role="select-member"]');

        if (!row) return;

        event.preventDefault();
        selectRow(row);
    });

    document.addEventListener("keydown", (event) => {
        if (
            event.key === "Escape"
            && modal
            && !modal.classList.contains("is-hidden")
        ) {
            close();
        }
    });

    reset();

    return {
        reset
    };
}