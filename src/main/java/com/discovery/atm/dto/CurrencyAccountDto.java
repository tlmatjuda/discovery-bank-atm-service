package com.discovery.atm.dto;

import java.math.BigDecimal;

public record CurrencyAccountDto(
        String accountNumber,
        String typeCode,
        String accountTypeDescription,
        String currencyCode,
        BigDecimal conversionRate,
        BigDecimal ccyBalance,
        BigDecimal zarBalance,
        BigDecimal accountLimit
) {
}
