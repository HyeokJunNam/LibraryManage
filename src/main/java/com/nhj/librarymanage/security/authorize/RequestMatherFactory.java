package com.nhj.librarymanage.security.authorize;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMatherFactory {

    public static RequestMatcher of(HttpMethod httpMethod, String url) {
        return PathPatternRequestMatcher.pathPattern(httpMethod, url);
    }

    public static RequestMatcher of(String httpMethod, String url) {
        return of(HttpMethod.valueOf(httpMethod), url);
    }

}