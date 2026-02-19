package com.discovery.atm.repository.row;

import java.math.BigDecimal;

public record TransactionalAccountRow(
        Long accountNumber,
        String typeCode,
        String accountTypeDescription,
        String currencyCode,
        BigDecimal displayBalance,
        String conversionIndicator,
        BigDecimal conversionRate,
        BigDecimal accountLimit
) {
}
