package com.tolmachev.bank.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(schema = "PUBLIC", name = "ACCOUNTS")
public class WalletEntity {
    @Id
    @Column(name = "ID")
    private UUID id;
    @Column(name = "AVAILABLE_BALANCE")
    private BigDecimal availableBalance;
}
