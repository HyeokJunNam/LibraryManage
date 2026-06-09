package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.Notification;
import com.nhj.librarymanage.error.code.NotificationErrorCode;
import com.nhj.librarymanage.error.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            """
            delete from notification n where n.book.id = :bookId and n.member.id = :memberId
            """
    )
    void deleteByBookIdAndMemberId(Long bookId, Long memberId);

    default Notification getByBookIdAndMemberId(Long bookId, Long memberId) {
        return findByBookIdAndMemberId(bookId, memberId)
                .orElseThrow(() -> new EntityNotFoundException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
    }

    boolean existsByBookIdAndMemberId(Long bookId, Long memberId);

}
