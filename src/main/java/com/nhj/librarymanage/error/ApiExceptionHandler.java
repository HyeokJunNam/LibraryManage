package com.nhj.librarymanage.error;

import com.nhj.librarymanage.error.code.ErrorCode;
import com.nhj.librarymanage.error.exception.BusinessException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handle(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        ProblemDetail pd = ProblemDetail.forStatus(errorCode.getStatus());

        pd.setTitle(errorCode.getTitle());
        pd.setDetail(errorCode.getDetail());
        pd.setProperty("code", errorCode.getCode());

        String message = errorCode.getLogMessage() != null ? errorCode.getLogMessage() : errorCode.getDetail();

        log.error(message, ex);

        return pd;
    }


}
