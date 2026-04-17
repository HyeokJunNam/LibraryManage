package com.nhj.librarymanage.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "number_sequence")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(NumberSequenceId.class)
public class NumberSequence {

    @Id
    private String sequenceName;

    @Id
    private Integer sequenceYear;

    @Column(nullable = false)
    private Long currentValue;

    public Long next() {
        this.currentValue += 1;
        return this.currentValue;
    }

    public static NumberSequence create(String sequenceName, Integer sequenceYear) {
        NumberSequence sequence = new NumberSequence();
        sequence.sequenceName = sequenceName;
        sequence.sequenceYear = sequenceYear;
        sequence.currentValue = 0L;
        return sequence;
    }

}
