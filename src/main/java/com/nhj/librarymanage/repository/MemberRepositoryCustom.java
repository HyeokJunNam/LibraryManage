package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.domain.dto.MemberRequest;
import com.nhj.librarymanage.model.vo.MemberStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepositoryCustom {

    Page<Member> search(MemberRequest.SearchCondition searchCondition, Pageable pageable);

    MemberStatistics getMemberStatistics();

}
