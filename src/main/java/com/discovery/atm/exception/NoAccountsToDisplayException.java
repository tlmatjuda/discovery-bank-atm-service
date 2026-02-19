package com.discovery.atm.exception;

import org.springframework.http.HttpStatus;

public class NoAccountsToDisplayException extends ApiException {

    public NoAccountsToDisplayException() {
        super(HttpStatus.NOT_FOUND, "No accounts to display");
    }
}
