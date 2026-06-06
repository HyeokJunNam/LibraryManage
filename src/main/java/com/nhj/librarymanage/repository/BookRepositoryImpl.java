package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.code.BookCopyCondition;
import com.nhj.librarymanage.domain.dto.admin.book.BookManageRequest;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.util.QuerydslFilterHelper;
import com.nhj.librarymanage.util.QuerydslSortHelper;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.nhj.librarymanage.domain.entity.QBook.book;
import static com.nhj.librarymanage.domain.entity.QBookCopy.bookCopy;

@RequiredArgsConstructor
@Repository
public class BookRepositoryImpl implements BookRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static final Map<String, Expression<? extends Comparable<?>>> ORDER_COLUMN_MAP =
            QuerydslSortHelper.buildOrderColumnMap(List.of());

    @Override
    public Page<Book> findAll(BookManageRequest.Search search, Pageable pageable) {
        OrderSpecifier<?>[] order = QuerydslSortHelper.sort(book.id, ORDER_COLUMN_MAP, pageable);

        BooleanExpression likeIsbn = QuerydslFilterHelper.like(book.isbn, search.isbn());
        BooleanExpression likeTitle = QuerydslFilterHelper.like(book.title, search.title());
        BooleanExpression likeAuthor = QuerydslFilterHelper.like(book.author, search.author());
        BooleanExpression likePublisher = QuerydslFilterHelper.like(book.publisher, search.publisher());

        List<Book> query = jpaQueryFactory
                .selectFrom(book)
                .where(likeIsbn, likeTitle, likeAuthor, likePublisher)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(book.id.count())
                .from(book)
                .where(likeIsbn, likeTitle, likeAuthor, likePublisher);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }


    @Override
    public List<Book> findBorrowableBook(List<Long> bookIds) {
        BooleanExpression inBook = QuerydslFilterHelper.in(book.id, bookIds);
        BooleanExpression likeNormal = QuerydslFilterHelper.like(bookCopy.bookCopyCondition.stringValue(), BookCopyCondition.NORMAL.getCode());
        BooleanExpression isNullBorrowRecord = QuerydslFilterHelper.isNull(bookCopy.borrowRecord);

        return jpaQueryFactory
                .selectFrom(book)
                .join(book.bookCopies, bookCopy).fetchJoin()
                .where(inBook, likeNormal, isNullBorrowRecord)
                .fetch();
    }

}

