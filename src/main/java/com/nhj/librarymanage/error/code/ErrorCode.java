package com.nhj.librarymanage.error.code;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    String getCode();

    HttpStatus getStatus();

    String getTitle();

    String getDetail();

    String getLogMessage();

}