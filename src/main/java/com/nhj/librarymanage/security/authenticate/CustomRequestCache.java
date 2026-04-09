package com.nhj.librarymanage.security.authenticate;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Component;

@Component
public class CustomRequestCache extends HttpSessionRequestCache {

    @Override
    public void saveRequest(@NonNull HttpServletRequest request, jakarta.servlet.http.@NonNull HttpServletResponse response) {
        if (!shouldSaveRequest(request)) {
            return;
        }

        super.saveRequest(request, response);
    }

    private boolean shouldSaveRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (!"GET".equalsIgnoreCase(method)) {
            return false;
        }

        if (uri == null || uri.startsWith("/api/")) {
            return false;
        }

        if (uri.startsWith("/login")) {
            return false;
        }

        return true;
    }
}