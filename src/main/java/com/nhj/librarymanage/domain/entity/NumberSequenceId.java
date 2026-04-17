package com.nhj.librarymanage.domain.entity;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@RequiredArgsConstructor
@AllArgsConstructor
public class NumberSequenceId implements Serializable {

    private String sequenceName;
    private Integer sequenceYear;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberSequenceId that)) return false;
        return Objects.equals(sequenceName, that.sequenceName)
                && Objects.equals(sequenceYear, that.sequenceYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequenceName, sequenceYear);
    }
}