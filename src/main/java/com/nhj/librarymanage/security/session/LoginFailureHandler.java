package com.nhj.librarymanage.security.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public LoginFailureHandler(String defaultFailureUrl) {
        setDefaultFailureUrl(defaultFailureUrl);
    }

}
