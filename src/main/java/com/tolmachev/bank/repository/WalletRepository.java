package com.tolmachev.bank.repository;

import com.tolmachev.bank.repository.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
    @Modifying
    @Query("""
                UPDATE WalletEntity w
                SET w.availableBalance = w.availableBalance - :amount
                WHERE w.id = :id
                  AND w.availableBalance >= :amount
            """)
    int reserve(UUID id, BigDecimal amount);

    @Modifying
    @Query("""
            UPDATE WalletEntity w
            SET w.availableBalance = w.availableBalance + :amount
            WHERE w.id = :id
            """)
    int deposit(UUID id, BigDecimal amount);

    boolean existsById(UUID id);

    Optional<WalletEntity> findById(UUID uuid);
}
