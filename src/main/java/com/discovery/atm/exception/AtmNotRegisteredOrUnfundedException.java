package com.discovery.atm.exception;

import org.springframework.http.HttpStatus;

public class AtmNotRegisteredOrUnfundedException extends ApiException {

    public AtmNotRegisteredOrUnfundedException() {
        super(HttpStatus.BAD_REQUEST, "ATM not registered or unfunded");
    }
}
