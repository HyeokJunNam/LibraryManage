package com.nhj.librarymanage.service;

import com.nhj.librarymanage.config.SignupProperties;
import com.nhj.librarymanage.error.code.MemberErrorCode;
import com.nhj.librarymanage.error.exception.ValidationFailureException;
import com.nhj.librarymanage.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class SignupTokenService {

    private final RedisUtils redisUtils;
    private final SignupProperties signupProperties;

    private static final String SIGNUP_VERIFICATION_TOKEN_KEY = "signup:verify:";

    private String verifiedKey(String token) {
        return SIGNUP_VERIFICATION_TOKEN_KEY + token;
    }

    public String issueSignupToken(String email) {
        String token = OneTimeTokenGenerator.generateToken();
        String tokenKey = verifiedKey(token);

        redisUtils.save(tokenKey, email, signupProperties.getExpireSignupMinutes(), TimeUnit.MINUTES);

        return token;
    }

    public void validateSignupToken(String email, String token) {
        String tokenKey = verifiedKey(token);

        String verifiedEmail = redisUtils.getAndDelete(tokenKey, String.class);
        if (verifiedEmail == null) {
            throw new ValidationFailureException(MemberErrorCode.TOKEN_NOT_FOUND);
        }

        if (!verifiedEmail.equalsIgnoreCase(email)) {
            throw new ValidationFailureException(MemberErrorCode.INVALID_TOKEN);
        }
    }

}
