package com.discovery.atm.mapper;

import com.discovery.atm.dto.TransactionalAccountDto;
import com.discovery.atm.repository.row.TransactionalAccountRow;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TransactionalAccountMapper {

    private static final BigDecimal CHQ_OVERDRAFT_LIMIT = new BigDecimal("10000.000");
    private static final BigDecimal ZERO = new BigDecimal("0.000");
    public static final String INDICATOR_MULTIPLY = "*";
    public static final String INDICATOR_DIVIDE = "/";

    public TransactionalAccountDto toDto(TransactionalAccountRow row) {
        BigDecimal balance = scale3(row.displayBalance());
        BigDecimal conversionRate = scale3(defaultRate(row.conversionRate()));
        BigDecimal zarBalance = calculateZarBalance(balance, row.conversionIndicator(), row.conversionRate());
        BigDecimal accountLimit = resolveAccountLimit(row.typeCode(), row.accountLimit());

        return new TransactionalAccountDto(
                row.accountNumber(),
                row.typeCode(),
                row.accountTypeDescription(),
                row.currencyCode(),
                conversionRate,
                balance,
                zarBalance,
                accountLimit
        );
    }

    private BigDecimal resolveAccountLimit(String typeCode, BigDecimal accountLimit) {
        if ("CHQ".equalsIgnoreCase(typeCode)) {
            return CHQ_OVERDRAFT_LIMIT;
        }
        if (accountLimit != null) {
            return scale3(accountLimit);
        }
        return ZERO;
    }

    private BigDecimal calculateZarBalance(BigDecimal amount, String indicator, BigDecimal rate) {
        BigDecimal safeAmount = scale3(amount);
        BigDecimal safeRate = defaultRate(rate);

        if (INDICATOR_MULTIPLY.equals(indicator)) {
            return scale3(safeAmount.multiply(safeRate));
        }
        if (INDICATOR_DIVIDE.equals(indicator)) {
            if (safeRate.compareTo(BigDecimal.ZERO) == 0) {
                return ZERO;
            }
            return safeAmount.divide(safeRate, 3, RoundingMode.HALF_UP);
        }
        return safeAmount;
    }

    private BigDecimal defaultRate(BigDecimal rate) {
        if (rate == null) {
            return BigDecimal.ONE;
        }
        return rate;
    }

    private BigDecimal scale3(BigDecimal value) {
        if (value == null) {
            return ZERO;
        }
        return value.setScale(3, RoundingMode.HALF_UP);
    }
}
