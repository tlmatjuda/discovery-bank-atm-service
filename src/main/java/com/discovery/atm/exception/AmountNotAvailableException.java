package com.discovery.atm.exception;

import org.springframework.http.HttpStatus;

public class AmountNotAvailableException extends ApiException {

    public AmountNotAvailableException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
