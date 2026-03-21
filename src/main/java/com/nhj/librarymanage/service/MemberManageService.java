package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.MemberRequest;
import com.nhj.librarymanage.domain.dto.MemberResponse;
import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.error.code.MemberErrorCode;
import com.nhj.librarymanage.error.exception.EntityAlreadyExistsException;
import com.nhj.librarymanage.repository.MemberRepository;
import com.nhj.librarymanage.security.member.SecurityUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberManageService extends SecurityUserService<Member> {

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    @Override
    public Optional<Member> findUser(String loginId) {
        return memberRepository.findByLoginId(loginId);
    }

    public MemberResponse.Info getMember(long id) {
        return MemberResponse.Info.from(memberRepository.get(id));
    }

    @Transactional
    public Page<MemberResponse.Info> getMembers(Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberResponse.Info::from);
    }

    @Transactional
    public void createMember(MemberRequest.Create create) {
        boolean existsMember = memberRepository.existsByLoginId(create.getLoginId());

        if (existsMember) {
            throw new EntityAlreadyExistsException(MemberErrorCode.ALREADY_MEMBER);
        }

        Member member = Member.builder()
                .loginId(create.getLoginId())
                .password(passwordEncoder.encode(create.getPassword()))
                .role(create.getRole())
                .name(create.getName())
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public void updateMember(MemberRequest.Update update) {
        Member member = memberRepository.get(update.getId());

        member.changeName(update.getName());
    }

    @Transactional
    public void deleteMember(long id) {
        memberRepository.deleteById(id);
    }

}
