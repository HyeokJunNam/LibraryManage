package com.nhj.librarymanage.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.signup.verification")
@Getter
@Setter
public class SignupProperties {

    private int expireEmailMinutes;

    private int expireSignupMinutes;


}
