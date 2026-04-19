package com.tolmachev.bank.exceptions;

public class BalanceIsLessThanWithdrawException extends RuntimeException {
    public BalanceIsLessThanWithdrawException(String message) {
        super(message);
    }
}
