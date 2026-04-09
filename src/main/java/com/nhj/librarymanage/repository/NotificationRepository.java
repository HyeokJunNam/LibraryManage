package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    boolean existsByMemberIdAndBookId(Long memberId, Long bookId);

}
