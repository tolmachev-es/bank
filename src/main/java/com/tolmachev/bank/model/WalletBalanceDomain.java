package com.tolmachev.bank.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class WalletBalanceDomain {
    private UUID walletId;
    private BigDecimal balance;
}
