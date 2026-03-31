package com.nhj.librarymanage.service;

import com.nhj.librarymanage.config.MailProperties;
import com.nhj.librarymanage.domain.model.vo.MailContent;
import com.nhj.librarymanage.error.code.MailErrorCode;
import com.nhj.librarymanage.error.exception.mail.MailSendFailureException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class MailSendHelper {

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

    public MimeMessage createMimeMessage(MailTemplate mailTemplate) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(mailProperties.getUsername(), mailProperties.getFromName());
            helper.setTo(mailTemplate.getToEmail());
            helper.setSubject(mailProperties.getSubject());
            helper.setText(mailTemplate.getTextBody(), mailTemplate.getHtmlBody());

            return mimeMessage;
        }
        catch (MessagingException e) {
            throw new MailSendFailureException(MailErrorCode.SEND_FAILURE, e);
        }
        catch (UnsupportedEncodingException e) {
            throw new MailSendFailureException(MailErrorCode.PERSONAL_ENCODING_FAILURE, e);
        }
    }

    public void send(MailTemplate mailTemplate) {
        MimeMessage mimeMessage = createMimeMessage(mailTemplate);

        javaMailSender.send(mimeMessage);
    }

}
