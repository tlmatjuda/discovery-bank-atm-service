package com.discovery.atm.repository.row;

import java.math.BigDecimal;

public record WithdrawAccountRow(
        String accountNumber,
        Long clientId,
        String typeCode,
        String accountTypeDescription,
        String currencyCode,
        BigDecimal displayBalance,
        String conversionIndicator,
        BigDecimal conversionRate,
        BigDecimal accountLimit
) {
}
