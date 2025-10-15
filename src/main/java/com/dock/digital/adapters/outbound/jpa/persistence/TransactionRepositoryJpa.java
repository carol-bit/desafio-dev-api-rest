package com.dock.digital.adapters.outbound.jpa.persistence;

import com.dock.digital.adapters.outbound.jpa.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepositoryJpa extends JpaRepository<TransactionEntity, UUID> {

    @Query("SELECT t FROM TransactionEntity t WHERE t.accountId = :accountId AND t.timestamp BETWEEN :start AND :end ORDER BY t.timestamp DESC")
    List<TransactionEntity> findByPeriod(
            @Param("accountId") UUID accountId,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end
    );

    @Query("SELECT t FROM TransactionEntity t WHERE t.accountId = :accountId AND t.type = :type AND t.timestamp BETWEEN :start AND :end")
    List<TransactionEntity> findByAccountAndTypeAndPeriod(
            @Param("accountId") UUID accountId,
            @Param("type") TransactionEntity.Type type,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end
    );
}