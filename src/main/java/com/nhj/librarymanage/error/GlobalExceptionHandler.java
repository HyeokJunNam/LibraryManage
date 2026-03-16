package com.nhj.librarymanage.error;

import com.nhj.librarymanage.error.exception.BusinessException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handle(BusinessException ex) {
        Error error = ex.getError();

        ProblemDetail pd = ProblemDetail.forStatus(error.getStatus());

        pd.setTitle(error.getTitle());
        pd.setDetail(error.getDetail());
        pd.setProperty("code", error.getCode());

        return pd;
    }

}
