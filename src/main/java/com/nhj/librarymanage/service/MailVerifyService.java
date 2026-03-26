package com.nhj.librarymanage.service;


import com.nhj.librarymanage.config.SignupProperties;
import com.nhj.librarymanage.error.code.MailErrorCode;
import com.nhj.librarymanage.error.exception.mail.MailVerificationFailureException;
import com.nhj.librarymanage.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailVerifyService {

    private final SignupTokenService signupTokenService;
    private final SignupProperties signupProperties;

    private static final String VERIFICATION_EMAIL_KEY = "email:verify:";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int VERIFICATION_CODE_LENGTH = 6;

    private final RedisUtils redisUtils;

    private String buildVerificationKey(String email) {
        return VERIFICATION_EMAIL_KEY + email.trim();
    }

    private String getSavedVerificationCode(String email) {
        String verificationCode = redisUtils.get(buildVerificationKey(email), String.class);

        if (verificationCode == null) {
            throw new MailVerificationFailureException(MailErrorCode.EMAIL_CODE_INVALID);
        }

        return verificationCode;
    }

    private String createVerificationCode() {
        StringBuilder code = new StringBuilder(VERIFICATION_CODE_LENGTH);

        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            int index = SECURE_RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }

    private void deleteVerificationCode(String email) {
        redisUtils.delete(buildVerificationKey(email));
    }

    private void validateVerificationCode(String savedCode, String requestCode) {
        if (!savedCode.equals(requestCode)) {
            throw new MailVerificationFailureException(MailErrorCode.EMAIL_CODE_MISMATCH);
        }
    }

    protected String generateVerificationCode(String email) {
        String verificationCode = createVerificationCode();

        redisUtils.save(
                buildVerificationKey(email),
                verificationCode,
                signupProperties.getExpireEmailMinutes(),
                TimeUnit.MINUTES
        );

        return verificationCode;
    }

    public String verify(String email, String code) {
        String savedCode = getSavedVerificationCode(email);
        validateVerificationCode(savedCode, code);
        deleteVerificationCode(email);

        return signupTokenService.issueSignupToken(email);
    }

}
