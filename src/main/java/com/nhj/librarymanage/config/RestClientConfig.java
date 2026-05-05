package com.nhj.librarymanage.config;

import com.nhj.librarymanage.config.properties.NlLibraryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient nlLibraryRestClient(RestClient.Builder builder, NlLibraryProperties properties) {
        return builder
                .baseUrl(properties.baseUrl())
                .build();
    }

}