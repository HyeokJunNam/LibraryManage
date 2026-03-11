package com.nhj.librarymanage.error;

import org.springframework.http.HttpStatus;

public interface Error {

    String getCode();

    HttpStatus getStatus();

    String getTitle();

    String getDetail();

}