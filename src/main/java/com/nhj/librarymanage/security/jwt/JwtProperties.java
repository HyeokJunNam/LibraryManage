package com.nhj.librarymanage.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt.token")
@Component
@Getter @Setter
public class JwtProperties {

    private String header;
    private String secret;
    private int expirySeconds;
    private int refreshExpirySeconds;
    private String issuer;

}