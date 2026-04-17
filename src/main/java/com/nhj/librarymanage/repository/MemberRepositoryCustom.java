package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.domain.model.dto.MemberRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepositoryCustom {

    Page<Member> findAll(MemberRequest.SearchCondition searchCondition, Pageable pageable);

}
