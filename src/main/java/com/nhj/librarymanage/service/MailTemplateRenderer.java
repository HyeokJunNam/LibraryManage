package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.model.vo.MailContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RequiredArgsConstructor
@Service
public class MailTemplateRenderer {

    private final TemplateEngine templateEngine;

    public MailContent renderMailContent(String toEmail, String htmlURL, String textURL, Context context) {
        String htmlBody = templateEngine.process(htmlURL, context);
        String textBody = templateEngine.process(textURL, context);

        return new MailContent(toEmail, htmlBody, textBody);
    }

}
