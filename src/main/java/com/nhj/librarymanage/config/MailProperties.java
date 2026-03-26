package com.nhj.librarymanage.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.mail")
@Getter
@Setter
public class MailProperties {

    private String username;

    private String password;

    private String personal;

    private String appName;

    private String fromName;

    private String subject;

    private String defaultUserName;

}
