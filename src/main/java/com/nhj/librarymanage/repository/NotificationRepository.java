package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    boolean existsByBookIdAndMemberId(Long bookId, Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            """
            delete from notification n where n.book.id = :bookId and n.member.id = :memberId
            """
    )
    void deleteByBookIdAndMemberId(Long bookId, Long memberId);

    List<Notification> findAllByBookId(Long bookId);
}
