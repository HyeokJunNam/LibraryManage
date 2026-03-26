package com.nhj.librarymanage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@ConfigurationPropertiesScan
@EnableJpaAuditing
@SpringBootApplication
public class LibraryManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManageApplication.class, args);

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

}
