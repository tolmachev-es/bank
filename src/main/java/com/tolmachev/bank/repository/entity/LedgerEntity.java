package com.tolmachev.bank.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(schema = "PUBLIC", name = "OPERATIONS")
public class LedgerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private UUID id;
    @Column(name = "ACCOUNT_ID")
    private UUID accountId;
    @Column(name = "AMOUNT")
    private BigDecimal amount;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "DATE")
    private LocalDateTime date;
}
