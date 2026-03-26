package com.nhj.librarymanage.error.exception.mail;

import com.nhj.librarymanage.error.code.MailErrorCode;
import com.nhj.librarymanage.error.exception.BusinessException;

public class MailVerificationFailureException extends BusinessException {

    public MailVerificationFailureException(MailErrorCode mailErrorCode) {
        super(mailErrorCode);
    }

    public MailVerificationFailureException(MailErrorCode mailErrorCode, Throwable cause) {
        super(mailErrorCode, cause);
    }

}
