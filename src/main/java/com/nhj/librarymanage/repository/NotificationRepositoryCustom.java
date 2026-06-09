package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.Notification;
import com.nhj.librarymanage.error.code.NotificationErrorCode;
import com.nhj.librarymanage.error.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepositoryCustom {

    List<Notification> findAllByBookId(Long bookId);

    Optional<Notification> findByBookIdAndMemberId(Long bookId, Long memberId);

}
