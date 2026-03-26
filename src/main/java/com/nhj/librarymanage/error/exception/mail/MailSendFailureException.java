package com.nhj.librarymanage.error.exception.mail;

import com.nhj.librarymanage.error.code.MailErrorCode;
import com.nhj.librarymanage.error.exception.BusinessException;

public class MailSendFailureException extends BusinessException {

    public MailSendFailureException(MailErrorCode mailErrorCode) {
        super(mailErrorCode);
    }

    public MailSendFailureException(MailErrorCode mailErrorCode, Throwable cause) {
        super(mailErrorCode, cause);
    }

}
