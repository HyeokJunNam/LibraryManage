package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.code.BookItemStatus;
import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.domain.model.dto.BookRequest;
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
import static com.nhj.librarymanage.domain.entity.QBookItem.bookItem;

@RequiredArgsConstructor
@Repository
public class BookRepositoryImpl implements BookRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static final Map<String, Expression<? extends Comparable<?>>> ORDER_COLUMN_MAP =
            QuerydslSortHelper.buildOrderColumnMap(List.of());

    @Override
    public Page<Book> findAll(BookRequest.SearchCondition searchCondition, Pageable pageable) {
        OrderSpecifier<?>[] order = QuerydslSortHelper.sort(book.id, ORDER_COLUMN_MAP, pageable);

        BooleanExpression likeIsbn = QuerydslFilterHelper.like(book.isbn, searchCondition.getIsbn());
        BooleanExpression likeTitle = QuerydslFilterHelper.like(book.title, searchCondition.getTitle());
        BooleanExpression likeAuthor = QuerydslFilterHelper.like(book.author, searchCondition.getAuthor());
        BooleanExpression likePublisher = QuerydslFilterHelper.like(book.publisher, searchCondition.getPublisher());

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
        BooleanExpression likeAvailable = QuerydslFilterHelper.like(bookItem.status.stringValue(), BookItemStatus.AVAILABLE.getCode());
        BooleanExpression isNullBorrowRecord = QuerydslFilterHelper.isNull(bookItem.borrowRecord);

        return jpaQueryFactory
                .selectFrom(book)
                .join(book.bookItems, bookItem).fetchJoin()
                .where(inBook, likeAvailable, isNullBorrowRecord)
                .fetch();
    }

}

