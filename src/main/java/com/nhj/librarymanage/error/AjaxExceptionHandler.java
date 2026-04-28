package com.nhj.librarymanage.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AjaxExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handle(Exception exception, HttpServletRequest request, Model model) {
        if (!isAjaxFragmentRequest(request)) {
            throw new RuntimeException(exception);
        }

        model.addAttribute("errorTitle", "요청 처리 실패");
        model.addAttribute("errorMessage", "화면 정보를 불러오지 못했습니다.");

        return "error/ajax :: ajaxError";
    }

    private boolean isAjaxFragmentRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String requestedWith = request.getHeader("X-Requested-With");

        return "XMLHttpRequest".equals(requestedWith)
                && requestUri.startsWith("/admin/")
                && !requestUri.startsWith("/api/");
    }

}
