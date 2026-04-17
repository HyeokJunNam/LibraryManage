package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.NumberSequence;
import com.nhj.librarymanage.domain.entity.NumberSequenceId;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NumberSequenceRepository extends JpaRepository<NumberSequence, NumberSequenceId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select ns
        from NumberSequence ns
        where ns.sequenceName = :sequenceName
          and ns.sequenceYear = :sequenceYear
    """)
    Optional<NumberSequence> findForUpdate(
            @Param("sequenceName") String sequenceName,
            @Param("sequenceYear") Integer sequenceYear
    );

}
