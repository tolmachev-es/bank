package com.tolmachev.bank.api.handler;

import com.tolmachev.bank.exceptions.BalanceIsLessThanWithdrawException;
import com.tolmachev.bank.exceptions.WalletNotFoundException;
import org.openapitools.model.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleException(WalletNotFoundException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setError(String.format("Wallet with id %s not found", e.getMessage()));
        errorResponseDto.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BalanceIsLessThanWithdrawException.class)
    public ResponseEntity<ErrorResponseDto> handleException(BalanceIsLessThanWithdrawException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setError(String.format("Balance less than withdraw amount on wallet %s", e.getMessage()));
        errorResponseDto.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
