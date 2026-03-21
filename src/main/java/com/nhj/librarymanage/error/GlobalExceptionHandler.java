package com.nhj.librarymanage.error;

import com.nhj.librarymanage.error.code.ErrorCode;
import com.nhj.librarymanage.error.exception.BusinessException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handle(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        ProblemDetail pd = ProblemDetail.forStatus(errorCode.getStatus());

        pd.setTitle(errorCode.getTitle());
        pd.setDetail(errorCode.getDetail());
        pd.setProperty("code", errorCode.getCode());

        return pd;
    }

}
