package com.tolmachev.bank.api;

import com.tolmachev.bank.mapper.BalanceMapper;
import com.tolmachev.bank.mapper.OperationsMapper;
import com.tolmachev.bank.service.WalletService;
import lombok.val;
import org.openapitools.api.WalletApi;
import org.openapitools.model.BalanceDto;
import org.openapitools.model.WalletOperationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class WalletController implements WalletApi {
    private final WalletService walletService;
    private final OperationsMapper operationsMapper;
    private final BalanceMapper balanceMapper;

    @Autowired
    public WalletController(WalletService walletService, OperationsMapper operationsMapper, BalanceMapper balanceMapper) {
        this.walletService = walletService;
        this.operationsMapper = operationsMapper;
        this.balanceMapper = balanceMapper;
    }

    @Override
    public ResponseEntity<Void> walletPost(WalletOperationDto walletOperationDto) {
        val operationDomain = operationsMapper.toOperationDomain(walletOperationDto);
        walletService.processOperation(operationDomain);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BalanceDto> walletIdGet(UUID id) {
        val balance = walletService.getBalance(id);
        var result = balanceMapper.toDto(balance);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
