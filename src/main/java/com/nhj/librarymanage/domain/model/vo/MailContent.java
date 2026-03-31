package com.nhj.librarymanage.domain.model.vo;

import com.nhj.librarymanage.service.MailTemplate;

public record MailContent(
        String toEmail,
        String htmlBody,
        String textBody
) implements MailTemplate {
    @Override
    public String getToEmail() {
        return toEmail;
    }

    @Override
    public String getHtmlBody() {
        return htmlBody;
    }

    @Override
    public String getTextBody() {
        return textBody;
    }
}
