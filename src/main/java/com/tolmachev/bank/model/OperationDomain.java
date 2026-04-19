package com.tolmachev.bank.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class OperationDomain {
    private UUID uuid;
    private boolean deposit;
    private BigDecimal amount;
}
