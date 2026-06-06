package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.member.info.MyInfoRequest;
import com.nhj.librarymanage.domain.dto.member.info.MyInfoResponse;
import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.domain.dto.admin.common.PageResponse;
import com.nhj.librarymanage.domain.dto.admin.member.MemberManageRequest;
import com.nhj.librarymanage.domain.dto.admin.member.MemberManageResponse;
import com.nhj.librarymanage.error.code.MemberErrorCode;
import com.nhj.librarymanage.error.exception.EntityAlreadyExistsException;
import com.nhj.librarymanage.model.vo.MemberStatistics;
import com.nhj.librarymanage.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final SignupTokenService signupTokenService;
    private final NumberSequenceService numberSequenceService;

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    public boolean isLoginIdDuplicated(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    @Transactional
    public PageResponse<MemberManageResponse.ListItem> getMembers(MemberManageRequest.Search search, Pageable pageable) {
        Page<Member> members =  memberRepository.search(search, pageable);

        return PageResponse.from(members.map(MemberManageResponse.ListItem::from));
    }

    private void validateSignup(String email, String loginId, String token) {
        signupTokenService.verifyToken(email, token);

        if (isLoginIdDuplicated(loginId)) {
            throw new EntityAlreadyExistsException(MemberErrorCode.ALREADY_MEMBER);
        }
    }

    public MemberManageResponse.Detail getMember(Long id) {
        return MemberManageResponse.Detail.from(memberRepository.getById(id));
    }

    public MyInfoResponse.Detail getMyInfo(Long id) {
        return MyInfoResponse.Detail.from(memberRepository.getById(id));
    }

    @Transactional
    public void createMember(MemberManageRequest.Create create) {
        validateSignup(create.email(), create.loginId(), create.signupToken());

        Member member = Member.builder()
                .loginId(create.loginId())
                .password(passwordEncoder.encode(create.password()))
                .role(create.role())
                .name(create.name())
                .email(create.email())
                .phoneNumber(create.phoneNumber())
                .memberNo(numberSequenceService.nextMemberNumber())
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public void updateMyInfo(Long memberId, MyInfoRequest.Update update) {
        Member member = memberRepository.getById(memberId);
        member.changePhoneNumber(update.phoneNumber());

        if (StringUtils.hasText(update.newPassword())) {
            member.changePassword(passwordEncoder.encode(update.newPassword()));
        }
    }

    @Transactional
    public void updateMember(MemberManageRequest.Update update) {
        Member member = memberRepository.getById(update.id());

    }

    public boolean matchesCurrentPassword(Long memberId, String currentPassword) {
        Member member = memberRepository.getById(memberId);

        return passwordEncoder.matches(currentPassword, member.getPassword());
    }

    @Transactional
    public void deleteMember(long id) {
        memberRepository.deleteById(id);
    }


    public MemberStatistics getMemberStatistics() {
        return memberRepository.getMemberStatistics();
    }

}
