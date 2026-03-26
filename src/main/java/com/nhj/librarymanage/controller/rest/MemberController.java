package com.nhj.librarymanage.controller.rest;

import com.nhj.librarymanage.domain.ApiResponse;
import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.MemberRequest;
import com.nhj.librarymanage.domain.dto.MemberResponse;
import com.nhj.librarymanage.service.MemberService;
import com.nhj.librarymanage.service.MemberValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;
    private final MemberValidationService memberValidationService;

    @Description(value = "회원 조회")
    @GetMapping("/members/{id}")
    public ResponseEntity<ApiResponse> getMember(@PathVariable long id) {
        MemberResponse.Info info = memberService.getMember(id);
        ApiResponse apiResponse = ApiResponse.result(info);

        return ResponseEntity.ok().body(apiResponse);
    }

    @Description(value = "회원 목록 조회")
    @GetMapping("/members")
    public ResponseEntity<ApiResponse> getMembers(Pageable pageable) {
        Page<MemberResponse.Info> infos = memberService.getMembers(pageable);
        ApiResponse apiResponse = ApiResponse.result(infos);

        return ResponseEntity.ok().body(apiResponse);
    }

    @Description(value = "회원 생성")
    @PostMapping("/members")
    public ResponseEntity<Void> createMember(@RequestBody MemberRequest.Create create) {
        memberService.createMember(create);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Description(value = "회원 수정")
    @PutMapping("/members")
    public ResponseEntity<Void> updateMember(@RequestBody MemberRequest.Update update) {
        memberService.updateMember(update);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Description(value = "회원 삭제")
    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable long id) {
        memberService.deleteMember(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Description(value = "ID 중복 검사")
    @GetMapping("/members/exists")
    public ResponseEntity<ApiResponse> verifyDuplicateLoginId(@RequestParam String loginId) {
        boolean duplicatedLoginId = memberValidationService.isLoginIdDuplicated(loginId);
        MemberResponse.LoginIdCheck loginIdCheck = new MemberResponse.LoginIdCheck(loginId, !duplicatedLoginId);

        ApiResponse apiResponse = ApiResponse.result(loginIdCheck);

        return ResponseEntity.ok().body(apiResponse);
    }

}
