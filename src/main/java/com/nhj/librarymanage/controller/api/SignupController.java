package com.nhj.librarymanage.controller.api;

import com.nhj.librarymanage.domain.dto.admin.common.ApiResponse;
import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.member.auth.EmailVerificationRequest;
import com.nhj.librarymanage.domain.dto.member.auth.EmailVerificationResponse;
import com.nhj.librarymanage.domain.dto.admin.member.MemberManageRequest;
import com.nhj.librarymanage.domain.dto.admin.member.MemberManageResponse;
import com.nhj.librarymanage.service.MemberService;
import com.nhj.librarymanage.service.SignupEmailVerificationService;
import com.nhj.librarymanage.service.SignupTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/public")
@RestController
public class SignupController {

    private final SignupEmailVerificationService signupEmailVerificationService;
    private final SignupTokenService signupTokenService;
    private final MemberService memberService;

    @Description(value = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody MemberManageRequest.Create create) {
        memberService.createMember(create);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Description(value = "ID 중복 검사")
    @GetMapping("/auth/check-id")
    public ResponseEntity<ApiResponse> verifyDuplicateLoginId(@RequestParam String loginId) {
        boolean duplicatedLoginId = memberService.isLoginIdDuplicated(loginId);
        MemberManageResponse.LoginIdCheck loginIdCheck = MemberManageResponse.LoginIdCheck.of(loginId, !duplicatedLoginId);

        ApiResponse apiResponse = ApiResponse.result(loginIdCheck);

        return ResponseEntity.ok().body(apiResponse);
    }

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
