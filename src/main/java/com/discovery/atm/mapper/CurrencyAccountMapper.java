package com.discovery.atm.mapper;

import com.discovery.atm.dto.CurrencyAccountDto;
import com.discovery.atm.repository.row.CurrencyAccountRow;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CurrencyAccountMapper {

    private static final BigDecimal ZERO = new BigDecimal("0.000");

    public CurrencyAccountDto toDto(CurrencyAccountRow row) {
        BigDecimal ccyBalance = scale3(row.displayBalance());
        BigDecimal conversionRate = scale3(defaultRate(row.conversionRate()));
        BigDecimal zarBalance = calculateZarBalance(ccyBalance, row.conversionIndicator(), row.conversionRate());

        return new CurrencyAccountDto(
                row.accountNumber(),
                row.typeCode(),
                row.accountTypeDescription(),
                row.currencyCode(),
                conversionRate,
                ccyBalance,
                zarBalance,
                ZERO
        );
    }

    private BigDecimal calculateZarBalance(BigDecimal amount, String indicator, BigDecimal rate) {
        BigDecimal safeAmount = scale3(amount);
        BigDecimal safeRate = defaultRate(rate);

        if ("*".equals(indicator)) {
            return scale3(safeAmount.multiply(safeRate));
        }
        if ("/".equals(indicator)) {
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
