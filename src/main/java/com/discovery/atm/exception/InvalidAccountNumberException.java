package com.discovery.atm.exception;

import org.springframework.http.HttpStatus;

public class InvalidAccountNumberException extends ApiException {

    public InvalidAccountNumberException() {
        super(HttpStatus.BAD_REQUEST, "Invalid account number");
    }
}
