package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.NumberSequence;
import com.nhj.librarymanage.domain.entity.NumberSequenceId;
import com.nhj.librarymanage.repository.NumberSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class NumberSequenceInitializer implements ApplicationRunner {

    private final NumberSequenceRepository numberSequenceRepository;

    @Transactional
    @Override
    public void run(@NonNull ApplicationArguments args) {
        int year = LocalDate.now().getYear();
        NumberSequenceId id = new NumberSequenceId("MEMBER", year);

        if (!numberSequenceRepository.existsById(id)) {
            numberSequenceRepository.save(NumberSequence.create("MEMBER", year));
        }
    }
}