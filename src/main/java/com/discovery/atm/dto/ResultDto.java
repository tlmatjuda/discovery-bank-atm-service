package com.discovery.atm.dto;

public record ResultDto(
        boolean success,
        int statusCode,
        String statusReason
) {
}
