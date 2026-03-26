package com.nhj.librarymanage.service;

import com.nhj.librarymanage.config.MailProperties;
import com.nhj.librarymanage.config.SignupProperties;
import com.nhj.librarymanage.error.code.MailErrorCode;
import com.nhj.librarymanage.error.exception.EntityAlreadyExistsException;
import com.nhj.librarymanage.error.exception.mail.MailSendFailureException;
import com.nhj.librarymanage.error.exception.mail.MailVerificationFailureException;
import com.nhj.librarymanage.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class MailSendService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final MailProperties mailProperties;
    private final SignupProperties signupProperties;

    private final MailVerifyService mailVerifyService;
    private final MemberRepository memberRepository;

    private record MailContent(
            String textBody,
            String htmlBody
    ) {
    }

    private void validDuplicateEmail(String toEmail) {
        boolean exists = memberRepository.existsByEmail(toEmail);

        if (exists) {
            throw new EntityAlreadyExistsException(MailErrorCode.EMAIL_DUPLICATE);
        }
    }

    public void send(String toEmail) {
        send(toEmail, mailProperties.getDefaultUserName());
    }

    public void send(String toEmail, String userName) {
        validDuplicateEmail(toEmail);

        String verificationCode = mailVerifyService.generateVerificationCode(toEmail);
        Context context = createVerificationContext(userName, verificationCode);
        MailContent mailContent = renderVerificationMail(context);
        MimeMessage mimeMessage = createVerificationMimeMessage(toEmail, mailContent);

        javaMailSender.send(mimeMessage);
    }

    private Context createVerificationContext(String userName, String verificationCode) {
        Context context = new Context(Locale.KOREAN);
        context.setVariable("appName", mailProperties.getAppName());
        context.setVariable("userName", userName);
        context.setVariable("code", verificationCode);
        context.setVariable("expireMinutes", signupProperties.getExpireEmailMinutes());
        return context;
    }

    private MailContent renderVerificationMail(Context context) {
        String htmlBody = templateEngine.process("mail/verification-code", context);
        String textBody = templateEngine.process("mail/verification-code.txt", context);
        return new MailContent(textBody, htmlBody);
    }

    private MimeMessage createVerificationMimeMessage(String toEmail, MailContent mailContent) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(mailProperties.getUsername(), mailProperties.getFromName());
            helper.setTo(toEmail);
            helper.setSubject(mailProperties.getSubject());
            helper.setText(mailContent.textBody(), mailContent.htmlBody());

            return mimeMessage;
        }
        catch (MessagingException e) {
            throw new MailSendFailureException(MailErrorCode.SEND_FAILURE, e);
        }
        catch (UnsupportedEncodingException e) {
            throw new MailSendFailureException(MailErrorCode.PERSONAL_ENCODING_FAILURE, e);
        }
    }

}
