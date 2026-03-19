package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.MemberRequest;
import com.nhj.librarymanage.domain.dto.MemberResponse;
import com.nhj.librarymanage.domain.entity.MemberEntity;
import com.nhj.librarymanage.error.ErrorCode;
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
public class MemberManageService extends SecurityUserService<MemberEntity> {

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    @Override
    public Optional<MemberEntity> findUser(String loginId) {
        return memberRepository.findByLoginId(loginId);
    }

    public MemberResponse.InfoDto getMember(long id) {
        return MemberResponse.InfoDto.from(memberRepository.get(id));
    }

    @Transactional
    public Page<MemberResponse.InfoDto> getMembers(Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberResponse.InfoDto::from);
    }

    @Transactional
    public void createMember(MemberRequest.CreateDto createDto) {
        boolean existsMember = memberRepository.existsByLoginId(createDto.getLoginId());

        if (existsMember) {
            throw new EntityAlreadyExistsException(ErrorCode.ALREADY_MEMBER);
        }

        MemberEntity memberEntity = MemberEntity.builder()
                .loginId(createDto.getLoginId())
                .password(passwordEncoder.encode(createDto.getPassword()))
                .role(createDto.getRole())
                .name(createDto.getName())
                .build();

        memberRepository.save(memberEntity);
    }

    @Transactional
    public void updateMember(MemberRequest.UpdateDto updateDto) {
        MemberEntity memberEntity = memberRepository.get(updateDto.getId());

        memberEntity.changeName(updateDto.getName());
    }

    @Transactional
    public void deleteMember(long id) {
        memberRepository.deleteById(id);
    }

}
