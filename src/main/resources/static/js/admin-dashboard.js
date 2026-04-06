const dashboardData = {
    todayTaskCount: 37,
    summaries: [
        {
            label: "전체 회원 수",
            value: "1,284",
            meta: "이번 주 신규 가입 23명"
        },
        {
            label: "전체 도서 수",
            value: "8,642",
            meta: "이번 달 신규 등록 126권"
        },
        {
            label: "현재 대출 중",
            value: "312",
            meta: "어제 대비 18건 증가"
        },
        {
            label: "연체 건수",
            value: "27",
            meta: "즉시 확인 필요"
        }
    ],
    loanStatuses: [
        {
            label: "오늘 신규 대출",
            value: "42",
            desc: "정상적으로 승인된 오늘 대출 건수입니다."
        },
        {
            label: "오늘 반납 완료",
            value: "29",
            desc: "오늘 기준 반납 처리된 도서 건수입니다."
        },
        {
            label: "연장 요청",
            value: "11",
            desc: "관리자 확인이 필요한 연장 요청입니다."
        }
    ],
    activities: [
        {
            title: "홍길동 회원이 신규 가입되었습니다.",
            meta: "방금 전 · 회원 관리"
        },
        {
            title: "『해리 포터와 마법사의 돌』 도서 정보가 수정되었습니다.",
            meta: "12분 전 · 도서 관리"
        },
        {
            title: "김영희 회원의 대출 요청이 승인되었습니다.",
            meta: "28분 전 · 대출 관리"
        },
        {
            title: "연체 회원 3명에게 알림이 발송되었습니다.",
            meta: "1시간 전 · 알림 시스템"
        }
    ],
    alerts: [
        {
            level: "danger",
            title: "연체 7일 이상 회원 5명",
            desc: "즉시 확인 후 반납 독촉 또는 이용 제한 조치가 필요합니다."
        },
        {
            level: "warning",
            title: "재고 부족 도서 8권",
            desc: "예약이 많거나 훼손/분실 이력이 있는 도서를 우선 점검하세요."
        },
        {
            level: "normal",
            title: "시스템 백업 정상 완료",
            desc: "오늘 새벽 백업 작업이 정상적으로 완료되었습니다."
        }
    ]
};

document.addEventListener("DOMContentLoaded", () => {
    renderTodayTaskCount();
    renderSummaries();
    renderLoanStatuses();
    renderActivities();
    renderAlerts();
});

function renderTodayTaskCount() {
    const todayTaskCount = document.getElementById("todayTaskCount");
    if (!todayTaskCount) {
        return;
    }

    todayTaskCount.textContent = dashboardData.todayTaskCount.toLocaleString();
}

function renderSummaries() {
    const summaryGrid = document.getElementById("summaryGrid");
    if (!summaryGrid) {
        return;
    }

    summaryGrid.innerHTML = dashboardData.summaries
        .map(
            (summary) => `
                <article class="summary-card">
                    <p class="summary-card__label">${escapeHtml(summary.label)}</p>
                    <strong class="summary-card__value">${escapeHtml(summary.value)}</strong>
                    <p class="summary-card__meta">${escapeHtml(summary.meta)}</p>
                </article>
            `
        )
        .join("");
}

function renderLoanStatuses() {
    const loanStatusList = document.getElementById("loanStatusList");
    if (!loanStatusList) {
        return;
    }

    loanStatusList.innerHTML = dashboardData.loanStatuses
        .map(
            (status) => `
                <article class="loan-status-item">
                    <div class="loan-status-item__top">
                        <p class="loan-status-item__label">${escapeHtml(status.label)}</p>
                        <strong class="loan-status-item__value">${escapeHtml(status.value)}</strong>
                    </div>
                    <p class="loan-status-item__desc">${escapeHtml(status.desc)}</p>
                </article>
            `
        )
        .join("");
}

function renderActivities() {
    const activityList = document.getElementById("activityList");
    if (!activityList) {
        return;
    }

    activityList.innerHTML = dashboardData.activities
        .map(
            (activity) => `
                <article class="activity-item">
                    <div class="activity-item__top">
                        <p class="activity-item__title">${escapeHtml(activity.title)}</p>
                    </div>
                    <p class="activity-item__meta">${escapeHtml(activity.meta)}</p>
                </article>
            `
        )
        .join("");
}

function renderAlerts() {
    const alertList = document.getElementById("alertList");
    if (!alertList) {
        return;
    }

    alertList.innerHTML = dashboardData.alerts
        .map(
            (alert) => `
                <article class="alert-item alert-item--${escapeHtml(alert.level)}">
                    <div class="alert-item__top">
                        <p class="alert-item__title">${escapeHtml(alert.title)}</p>
                        <span class="alert-badge alert-badge--${escapeHtml(alert.level)}">
                            ${getAlertLabel(alert.level)}
                        </span>
                    </div>
                    <p class="alert-item__desc">${escapeHtml(alert.desc)}</p>
                </article>
            `
        )
        .join("");
}

function getAlertLabel(level) {
    switch (level) {
        case "danger":
            return "긴급";
        case "warning":
            return "주의";
        default:
            return "정상";
    }
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}