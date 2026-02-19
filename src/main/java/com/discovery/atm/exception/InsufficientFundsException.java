package com.discovery.atm.exception;

import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends ApiException {

    public InsufficientFundsException() {
        super(HttpStatus.BAD_REQUEST, "Insufficient funds");
    }
}
