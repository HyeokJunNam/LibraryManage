package com.nhj.librarymanage.controller.api;

import com.nhj.librarymanage.domain.ApiResponse;
import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.model.dto.EmailVerificationRequest;
import com.nhj.librarymanage.domain.model.dto.EmailVerificationResponse;
import com.nhj.librarymanage.service.SignupEmailVerificationService;
import com.nhj.librarymanage.service.SignupTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SignupVerificationController {

    private final SignupEmailVerificationService signupEmailVerificationService;
    private final SignupTokenService signupTokenService;

    @Description(value = "이메일 유효성 검증 인증번호 발송")
    @PostMapping("/auth/email-verifications")
    public ResponseEntity<ApiResponse> requestEmailVerification(@RequestBody EmailVerificationRequest.Send send) {
        signupEmailVerificationService.sendCode(send.getEmail());

        return ResponseEntity.ok().build();
    }

    @Description(value = "이메일 유효성 검증 인증번호 검증")
    @PostMapping("/auth/email-verifications/confirm")
    public ResponseEntity<ApiResponse> confirmEmailVerification(@RequestBody EmailVerificationRequest.Verify verify) {
        signupEmailVerificationService.verifyCode(verify.getEmail(), verify.getCode());
        String token = signupTokenService.issueToken(verify.getEmail());

        EmailVerificationResponse.Verified verified = EmailVerificationResponse.Verified.from(token);

        ApiResponse apiResponse = ApiResponse.result(verified);
        return ResponseEntity.ok(apiResponse);
    }

}
