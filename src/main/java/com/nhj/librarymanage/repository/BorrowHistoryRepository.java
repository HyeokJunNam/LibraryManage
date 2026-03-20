package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.BorrowHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowHistoryRepository extends JpaRepository<BorrowHistory, Long>, BorrowHistoryRepositoryCustom {

}
