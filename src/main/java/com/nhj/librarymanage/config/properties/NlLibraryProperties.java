package com.nhj.librarymanage.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.nl-library")
public record NlLibraryProperties(
        String baseUrl,
        String apiKey,
        ApiPath apiPath
) {
    public record ApiPath(
            String isbn
    ) {
    }
}