package com.nhj.librarymanage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.util.TimeZone;

@ConfigurationPropertiesScan
@EnableMethodSecurity
@EnableJpaAuditing
@EnableAsync
@SpringBootApplication
public class LibraryManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManageApplication.class, args);

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

}
