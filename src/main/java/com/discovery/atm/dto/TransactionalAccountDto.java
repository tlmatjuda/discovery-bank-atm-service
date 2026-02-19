package com.discovery.atm.dto;

import java.math.BigDecimal;

public record TransactionalAccountDto(
        Long accountNumber,
        String typeCode,
        String accountTypeDescription,
        String currencyCode,
        BigDecimal conversionRate,
        BigDecimal balance,
        BigDecimal zarBalance,
        BigDecimal accountLimit
) {
}
