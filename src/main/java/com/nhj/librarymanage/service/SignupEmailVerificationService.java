package com.nhj.librarymanage.service;


import com.nhj.librarymanage.config.MailProperties;
import com.nhj.librarymanage.config.SignupProperties;
import com.nhj.librarymanage.error.code.MailErrorCode;
import com.nhj.librarymanage.error.exception.mail.MailVerificationFailureException;
import com.nhj.librarymanage.repository.MemberRepository;
import com.nhj.librarymanage.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class SignupEmailVerificationService {

    private static final String SIGNUP_EMAIL_VERIFICATION_KEY = "email:verify:";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int VERIFICATION_CODE_LENGTH = 6;

    private final RedisUtils redisUtils;
    private final MailTemplateRenderer mailTemplateRenderer;

    private final ApplicationEventPublisher eventPublisher;

    private final SignupProperties signupProperties;
    private final MailProperties mailProperties;

    private final MemberRepository memberRepository;


    private String buildKey(String email) {
        return SIGNUP_EMAIL_VERIFICATION_KEY + email.trim();
    }

    private String generateCode() {
        char[] result = new char[VERIFICATION_CODE_LENGTH];

        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            result[i] = CHARACTERS.charAt(SECURE_RANDOM.nextInt(CHARACTERS.length()));
        }

        return new String(result);
    }

    private MailTemplate composeMail(String toEmail, String verificationCode) {
        Context context = new Context(Locale.KOREAN);
        context.setVariable("appName", mailProperties.getAppName());
        context.setVariable("expireMinutes", signupProperties.getExpireEmailMinutes());
        context.setVariable("userName", mailProperties.getDefaultUserName());
        context.setVariable("code", verificationCode);

        String htmlURL = "mail/verification-code.html";
        String textURL = "mail/verification-code.txt";

        return mailTemplateRenderer.renderMailContent(toEmail, htmlURL, textURL, context);
    }

    private void saveCode(String email, String code) {
        redisUtils.save(
                buildKey(email),
                code,
                signupProperties.getExpireEmailMinutes(),
                TimeUnit.MINUTES
        );
    }

    public void sendCode(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new MailVerificationFailureException(MailErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        String verificationCode = generateCode();
        MailTemplate mailTemplate = composeMail(email, verificationCode);

        eventPublisher.publishEvent(mailTemplate);

        saveCode(email, verificationCode); // TODO 비동기로 처리하고 저장할거면 발송 완료 후 저장되도록 리스너 안으로 옮겨라.....
    }

    private String getCode(String email) {
        String verificationCode = redisUtils.get(buildKey(email), String.class);

        if (verificationCode == null) {
            throw new MailVerificationFailureException(MailErrorCode.EMAIL_CODE_INVALID);
        }

        return verificationCode;
    }

    private void deleteCode(String email) {
        redisUtils.delete(buildKey(email));
    }

    private void validateCode(String savedCode, String requestCode) {
        if (!savedCode.equals(requestCode)) {
            throw new MailVerificationFailureException(MailErrorCode.EMAIL_CODE_MISMATCH);
        }
    }

    public void verifyCode(String email, String code) {
        String savedCode = getCode(email);
        validateCode(savedCode, code);
        deleteCode(email);
    }

}
