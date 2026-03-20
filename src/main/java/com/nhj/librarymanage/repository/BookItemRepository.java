package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.BookItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookItemRepository extends JpaRepository<BookItem, Long> {



}
