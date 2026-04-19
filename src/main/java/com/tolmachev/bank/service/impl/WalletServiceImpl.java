package com.tolmachev.bank.service.impl;

import com.tolmachev.bank.config.RetryExecutor;
import com.tolmachev.bank.exceptions.BalanceIsLessThanWithdrawException;
import com.tolmachev.bank.exceptions.WalletNotFoundException;
import com.tolmachev.bank.mapper.BalanceMapper;
import com.tolmachev.bank.model.OperationDomain;
import com.tolmachev.bank.model.WalletBalanceDomain;
import com.tolmachev.bank.repository.LedgerRepository;
import com.tolmachev.bank.repository.WalletRepository;
import com.tolmachev.bank.repository.entity.LedgerEntity;
import com.tolmachev.bank.repository.entity.WalletEntity;
import com.tolmachev.bank.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final LedgerRepository ledgerRepository;
    private final BalanceMapper balanceMapper;
    private final RetryExecutor retryExecutor;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void processOperation(OperationDomain operationDomain) {
        retryExecutor.execute(() ->
                transactionTemplate.execute((TransactionStatus status) -> {
                    if (operationDomain.isDeposit()) {
                        deposit(operationDomain);
                    } else {
                        withdraw(operationDomain);
                    }
                    return null;
                })
        );
    }


    private void withdraw(OperationDomain operationDomain) {
        int reserve = walletRepository.reserve(operationDomain.getUuid(), operationDomain.getAmount());
        if (reserve == 0) {
            if (!walletRepository.existsById(operationDomain.getUuid())) {
                throw new WalletNotFoundException(operationDomain.getUuid().toString());
            } else {
                throw new BalanceIsLessThanWithdrawException(operationDomain.getUuid().toString());
            }
        }
        saveLedger(operationDomain.getUuid(), "WITHDRAW", operationDomain.getAmount());
    }

    private void deposit(OperationDomain operationDomain) {
        int deposit = walletRepository.deposit(operationDomain.getUuid(), operationDomain.getAmount());
        if (deposit == 0) {
            throw new WalletNotFoundException(operationDomain.getUuid().toString());
        }
        saveLedger(operationDomain.getUuid(), "DEPOSIT", operationDomain.getAmount());
    }

    private void saveLedger(UUID walletId, String operationType, BigDecimal amount) {
        LedgerEntity ledgerEntity = new LedgerEntity();
        ledgerEntity.setAccountId(walletId);
        ledgerEntity.setType(operationType);
        ledgerEntity.setAmount(amount);
        ledgerEntity.setDate(LocalDateTime.now());
        ledgerRepository.save(ledgerEntity);
    }

    @Override
    public WalletBalanceDomain getBalance(UUID uuid) {
        WalletEntity walletEntity = walletRepository.findById(uuid)
                                                    .orElseThrow(() -> new WalletNotFoundException(uuid.toString()));
        return balanceMapper.toDomain(walletEntity);
    }
}
