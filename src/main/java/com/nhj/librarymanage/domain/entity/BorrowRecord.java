package com.nhj.librarymanage.domain.entity;

import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.code.ReturnStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity(name = "borrow_record")
public class BorrowRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id")
    private BookCopy bookCopy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime borrowedAt;

    private LocalDateTime dueAt;

    private LocalDateTime returnedAt;


    public void returnBook() {
        this.returnedAt = LocalDateTime.now();
        this.bookCopy.setBorrowStatus(BorrowStatus.AVAILABLE);
    }

    public ReturnStatus getReturnStatus() {
        if (returnedAt != null) {
            return ReturnStatus.RETURNED;
        }
        else {
            LocalDate dueDate = dueAt.toLocalDate();
            boolean overdue = dueDate.isBefore(LocalDate.now());

            if (overdue) {
                return  ReturnStatus.OVERDUE;
            }
        }

        return  ReturnStatus.BORROWED;
    }

}
