package com.nhj.librarymanage.controller.rest;

import com.nhj.librarymanage.domain.ApiResponse;
import com.nhj.librarymanage.domain.dto.MailRequest;
import com.nhj.librarymanage.service.MailSendService;
import com.nhj.librarymanage.service.MailVerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class MailController {

    private final MailSendService mailSendService;
    private final MailVerifyService mailVerifyService;

    @PostMapping("/mail/send")
    public ResponseEntity<Void> sendVerificationCodeMail(@RequestBody MailRequest.Send send) {
        mailSendService.send(send.getEmail());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/mail/verify")
    public ResponseEntity<ApiResponse> verifyCode(@RequestBody MailRequest.Verify verify) {
        String signupToken = mailVerifyService.verify(verify.getEmail(), verify.getCode());

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("verify", signupToken);

        ApiResponse apiResponse = ApiResponse.result(tokenMap);

        return ResponseEntity.ok(apiResponse);
    }

}
