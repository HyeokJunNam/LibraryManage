package com.nhj.librarymanage.service;

import com.nhj.librarymanage.config.SignupProperties;
import com.nhj.librarymanage.error.code.MemberErrorCode;
import com.nhj.librarymanage.error.exception.ValidationFailureException;
import com.nhj.librarymanage.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class SignupTokenService {

    private final RedisUtils redisUtils;
    private final SignupProperties signupProperties;

    private static final String SIGNUP_TOKEN_KEY = "signup:verify:";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTE_LENGTH = 32;

    private String buildTokenKey(String token) {
        return SIGNUP_TOKEN_KEY + token;
    }

    private static String generateToken() {
        byte[] randomBytes = new byte[TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    public String issueToken(String email) {
        String token = generateToken();
        String tokenKey = buildTokenKey(token);

        redisUtils.save(tokenKey, email, signupProperties.getExpireSignupMinutes(), TimeUnit.MINUTES);

        return token;
    }

    public void verifyToken(String email, String token) {
        String tokenKey = buildTokenKey(token);

        String verifiedEmail = redisUtils.getAndDelete(tokenKey, String.class);
        if (verifiedEmail == null) {
            throw new ValidationFailureException(MemberErrorCode.TOKEN_NOT_FOUND);
        }

        if (!verifiedEmail.equalsIgnoreCase(email)) {
            throw new ValidationFailureException(MemberErrorCode.INVALID_TOKEN);
        }
    }

}
