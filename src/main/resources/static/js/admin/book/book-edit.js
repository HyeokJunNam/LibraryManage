document.addEventListener("DOMContentLoaded", () => {
    bindBookEditSaveButton();
});

function bindBookEditSaveButton() {
    const saveButton = document.getElementById("saveBookButton");

    if (!saveButton) {
        return;
    }

    saveButton.addEventListener("click", openBookEditConfirmModal);
}

function openBookEditConfirmModal() {
    openAlertModal({
        title: "도서 정보 수정",
        message: "도서 정보를 수정하시겠습니까?",
        confirmText: "저장",
        cancelText: "취소",
        onConfirm: submitBookUpdate
    });
}

async function submitBookUpdate() {
    const bookId = getValue("bookId");

    try {
        await apiPut(`/api/books/${bookId}`, {
            isbn: getValue("isbn"),
            title: getValue("title"),
            author: getValue("author"),
            publisher: getValue("publisher"),
            location: getValue("location"),
            thumbnailUrl: getValue("thumbnailUrl"),
            description: getValue("description")
        });

        openAlertModal({
            title: "수정 완료",
            message: "도서 정보가 수정되었습니다.",
            confirmText: "확인",
            onConfirm: () => {
                window.location.href = `/admin/books/${bookId}`;
            }
        });
    } catch (error) {
        openAlertModal({
            title: "수정 실패",
            message: error?.message || "도서 정보 수정 중 오류가 발생했습니다.",
            confirmText: "확인"
        });
    }
}

function getValue(id) {
    const element = document.getElementById(id);

    if (!element) {
        return "";
    }

    return element.value.trim();
}