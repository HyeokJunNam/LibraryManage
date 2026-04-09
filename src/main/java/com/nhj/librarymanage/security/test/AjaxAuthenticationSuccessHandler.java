package com.nhj.librarymanage.security.test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull Authentication authentication) throws IOException {

        String returnUrl = request.getParameter("returnUrl");
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        String redirectUrl;
        if (isValidRedirectUrl(returnUrl)) {
            redirectUrl = returnUrl;
        } else if (savedRequest != null) {
            redirectUrl = extractPath(savedRequest.getRedirectUrl());
        } else {
            redirectUrl = "/library";
        }

        requestCache.removeRequest(request, response);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("redirectUrl", redirectUrl);

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private boolean isValidRedirectUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }

        if (!url.startsWith("/") || url.startsWith("//")) {
            return false;
        }

        return !url.startsWith("/api/");
    }

    private String extractPath(String url) {
        try {
            URI uri = URI.create(url);

            String path = uri.getPath();
            String query = uri.getQuery();
            String fragment = uri.getFragment();

            StringBuilder result = new StringBuilder(path != null ? path : "/");

            if (query != null && !query.isBlank()) {
                result.append("?").append(query);
            }

            if (fragment != null && !fragment.isBlank()) {
                result.append("#").append(fragment);
            }

            return result.toString();
        } catch (Exception e) {
            return "/library";
        }
    }
}