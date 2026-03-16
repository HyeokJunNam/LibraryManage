package com.nhj.librarymanage.security.exception;

import org.springframework.http.HttpStatus;

public interface SecurityError {

    String getCode();

    HttpStatus getStatus();

    String getTitle();

    String getDetail();

}