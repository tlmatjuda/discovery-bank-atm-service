package com.discovery.atm.mapper;

import com.discovery.atm.constant.DomainConstants;
import com.discovery.atm.dto.WithdrawAccountDto;
import com.discovery.atm.repository.row.WithdrawAccountRow;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class WithdrawAccountMapper {

    private static final BigDecimal CHQ_OVERDRAFT_LIMIT = new BigDecimal("10000.000");
    private static final BigDecimal ZERO = new BigDecimal("0.000");

    public WithdrawAccountDto toDto(WithdrawAccountRow row, BigDecimal latestBalance) {
        BigDecimal balance = scale3(latestBalance);
        BigDecimal conversionRate = scale3(defaultRate(row.conversionRate()));
        BigDecimal zarBalance = calculateZarBalance(balance, row.conversionIndicator(), row.conversionRate());
        BigDecimal accountLimit = resolveAccountLimit(row.typeCode(), row.accountLimit());

        return new WithdrawAccountDto(
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

    public BigDecimal calculateAvailableFunds(WithdrawAccountRow row) {
        BigDecimal balance = scale3(row.displayBalance());
        if (DomainConstants.ACCOUNT_TYPE_CHQ.equalsIgnoreCase(row.typeCode())) {
            return balance.add(CHQ_OVERDRAFT_LIMIT);
        }
        return balance;
    }

    private BigDecimal resolveAccountLimit(String typeCode, BigDecimal accountLimit) {
        if (DomainConstants.ACCOUNT_TYPE_CHQ.equalsIgnoreCase(typeCode)) {
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

        if (DomainConstants.CONVERSION_INDICATOR_MULTIPLY.equals(indicator)) {
            return scale3(safeAmount.multiply(safeRate));
        }
        if (DomainConstants.CONVERSION_INDICATOR_DIVIDE.equals(indicator)) {
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
