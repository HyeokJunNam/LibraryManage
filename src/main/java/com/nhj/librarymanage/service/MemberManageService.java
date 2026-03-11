package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.MemberRequest;
import com.nhj.librarymanage.domain.dto.MemberResponse;
import com.nhj.librarymanage.domain.entity.MemberEntity;
import com.nhj.librarymanage.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberManageService {

    private final MemberRepository memberRepository;

    public MemberResponse.InfoDto getMember(long id) {
        return MemberResponse.InfoDto.from(memberRepository.get(id));
    }

    @Transactional
    public List<MemberResponse.InfoDto> getMembers(MemberRequest.SearchDto searchDto) {
        return memberRepository.findAll().stream().map(MemberResponse.InfoDto::from).toList();
    }


    @Transactional
    public void createMember(MemberRequest.CreateDto createDto) {
        MemberEntity memberEntity = MemberEntity.builder()
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
