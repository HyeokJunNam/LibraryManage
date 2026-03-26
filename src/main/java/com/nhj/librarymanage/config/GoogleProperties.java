package com.nhj.librarymanage.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google")
@Getter
@Setter
public class GoogleProperties {

    private String clientId;
    private String clientSecret;

}
