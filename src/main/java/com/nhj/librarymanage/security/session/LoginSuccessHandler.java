package com.nhj.librarymanage.security.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Slf4j
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public LoginSuccessHandler(String defaultTargetUrl) {
        setDefaultTargetUrl(defaultTargetUrl);
    }

}
