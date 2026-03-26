package com.nhj.librarymanage.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "db")
@Getter
@Setter
public class DataSourceProperties {

    private String url;
    private String username;
    private String password;

}
