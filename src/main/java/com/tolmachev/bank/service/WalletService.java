package com.tolmachev.bank.service;

import com.tolmachev.bank.model.OperationDomain;
import com.tolmachev.bank.model.WalletBalanceDomain;

import java.util.UUID;

public interface WalletService {
    void processOperation(OperationDomain operationDomain);
    WalletBalanceDomain getBalance(UUID uuid);
}
