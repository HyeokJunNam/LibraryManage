package com.nhj.librarymanage.service;

import com.nhj.librarymanage.config.properties.NlLibraryProperties;
import com.nhj.librarymanage.domain.model.dto.NlLibraryBookSearchApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class NlLibraryClient {

    private final RestClient nlLibraryRestClient;
    private final NlLibraryProperties properties;

    private MultiValueMap<String, String> isbnQueryParams(String isbn) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("cert_key", properties.apiKey());
        params.add("result_style", "json");
        params.add("page_no", "1");
        params.add("page_size", "1");
        params.add("isbn", isbn.replace("-", "").trim());

        return params;
    }

    public NlLibraryBookSearchApi.Receive searchByIsbn(String isbn) {
        return nlLibraryRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(properties.apiPath().isbn())
                        .queryParams(isbnQueryParams(isbn))
                        .build())
                .retrieve()
                .body(NlLibraryBookSearchApi.Receive.class);
    }

}
