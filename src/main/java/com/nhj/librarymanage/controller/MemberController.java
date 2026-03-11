package com.nhj.librarymanage.controller;

import com.nhj.librarymanage.domain.ApiResponse;
import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.MemberRequest;
import com.nhj.librarymanage.domain.dto.MemberResponse;
import com.nhj.librarymanage.service.MemberManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberManageService memberManageService;

    @Description(value = "회원 조회")
    @GetMapping("/members/{id}")
    public ResponseEntity<ApiResponse> getMember(@PathVariable long id) {
        MemberResponse.InfoDto infoDtoList = memberManageService.getMember(id);
        ApiResponse apiResponse = ApiResponse.result(infoDtoList);

        return ResponseEntity.ok().body(apiResponse);
    }

    @Description(value = "회원 목록 조회")
    @GetMapping("/members")
    public ResponseEntity<ApiResponse> getMembers(@RequestBody(required = false) MemberRequest.SearchDto searchDto) {
        List<MemberResponse.InfoDto> infoDtoList = memberManageService.getMembers(searchDto);
        ApiResponse apiResponse = ApiResponse.result(infoDtoList);

        return ResponseEntity.ok().body(apiResponse);
    }

    @Description(value = "회원 생성")
    @PostMapping("/members")
    public ResponseEntity<Void> createMember(@RequestBody MemberRequest.CreateDto createDto) {
        memberManageService.createMember(createDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Description(value = "회원 수정")
    @PutMapping("/members")
    public ResponseEntity<Void> updateMember(@RequestBody MemberRequest.UpdateDto updateDto) {
        memberManageService.updateMember(updateDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Description(value = "회원 삭제")
    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable long id) {
        memberManageService.deleteMember(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
