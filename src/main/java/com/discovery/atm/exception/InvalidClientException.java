package com.discovery.atm.exception;

import org.springframework.http.HttpStatus;

public class InvalidClientException extends ApiException {

    public InvalidClientException() {
        super(HttpStatus.BAD_REQUEST, "Invalid client");
    }
}
