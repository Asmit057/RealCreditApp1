package com.aurumx.exception;

public class InsufficientRewardBalanceException extends RuntimeException {
    public InsufficientRewardBalanceException(String message) {
        super(message);
    }
}
