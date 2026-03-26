package com.nhj.librarymanage.service;

import com.nhj.librarymanage.error.code.MemberErrorCode;
import com.nhj.librarymanage.error.exception.EntityAlreadyExistsException;
import com.nhj.librarymanage.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberValidationService {

    private final MemberRepository memberRepository;
    private final SignupTokenService signupTokenService;

    public boolean isLoginIdDuplicated(String loginId) {
        return memberRepository.existsByLoginId(loginId);

        /*if (existsMember) {
            throw new EntityAlreadyExistsException(MemberErrorCode.ALREADY_MEMBER);
        }*/
    }

    // TODO 캐치는 되는데, 이걸 프론트에서 받아 쓰질 못하네..... 에러일때도 무조건 전환이 아니라...
    public void validateSignup(String loginId, String email, String token) {
        signupTokenService.validateSignupToken(email, token);

        if (isLoginIdDuplicated(loginId)) {
            throw new EntityAlreadyExistsException(MemberErrorCode.ALREADY_MEMBER);
        }

    }

}
