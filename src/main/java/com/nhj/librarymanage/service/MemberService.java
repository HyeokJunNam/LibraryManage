package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.model.dto.MemberRequest;
import com.nhj.librarymanage.domain.model.dto.MemberResponse;
import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.error.code.MemberErrorCode;
import com.nhj.librarymanage.error.exception.EntityAlreadyExistsException;
import com.nhj.librarymanage.repository.MemberRepository;
import com.nhj.librarymanage.security.member.SecurityUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService extends SecurityUserService<Member> {

    private final SignupTokenService signupTokenService;

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    public boolean isLoginIdDuplicated(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    @Override
    public Optional<Member> findUser(String loginId) {
        return memberRepository.findByLoginId(loginId);
    }

    public MemberResponse.Info getMember(long id) {
        return MemberResponse.Info.from(memberRepository.getById(id));
    }

    @Transactional
    public Page<MemberResponse.Info> getMembers(Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberResponse.Info::from);
    }

    private void validateSignup(String email, String loginId, String token) {
        signupTokenService.verifyToken(email, token);

        if (isLoginIdDuplicated(loginId)) {
            throw new EntityAlreadyExistsException(MemberErrorCode.ALREADY_MEMBER);
        }
    }

    @Transactional
    public void createMember(MemberRequest.Create create) {
        validateSignup(create.getEmail(), create.getLoginId(), create.getSignupToken());

        Member member = Member.builder()
                .loginId(create.getLoginId())
                .password(passwordEncoder.encode(create.getPassword()))
                .role(create.getRole())
                .name(create.getName())
                .email(create.getEmail())
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public void updateMember(MemberRequest.Update update) {
        Member member = memberRepository.getById(update.getId());

        member.changeName(update.getName());
    }

    @Transactional
    public void deleteMember(long id) {
        memberRepository.deleteById(id);
    }

}
