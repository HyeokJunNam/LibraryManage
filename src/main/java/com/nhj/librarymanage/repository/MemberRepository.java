package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.error.code.CommonErrorCode;
import com.nhj.librarymanage.error.code.MemberErrorCode;
import com.nhj.librarymanage.error.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findById(long id);

    Optional<Member> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    default Member get(long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

}
