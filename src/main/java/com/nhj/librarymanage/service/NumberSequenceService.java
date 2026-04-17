package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.NumberSequence;
import com.nhj.librarymanage.repository.NumberSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class NumberSequenceService {

    private final NumberSequenceRepository numberSequenceRepository;

    @Transactional
    public String nextMemberNumber() {
        int year = LocalDate.now().getYear();

        NumberSequence sequence = numberSequenceRepository.findForUpdate("MEMBER", year)
                .orElseGet(() -> numberSequenceRepository.save(NumberSequence.create("MEMBER", year)));

        Long nextValue = sequence.next();

        return year + String.format("%05d", nextValue);
    }

}
