package com.discovery.atm.repository.mapper;

import com.discovery.atm.repository.row.CurrencyAccountRow;
import org.springframework.jdbc.core.RowMapper;

public final class CurrencyAccountRowMapper {

    private CurrencyAccountRowMapper() {
    }

    public static RowMapper<CurrencyAccountRow> rowMapper() {
        return (rs, rowNum) -> new CurrencyAccountRow(
                rs.getString("CLIENT_ACCOUNT_NUMBER"),
                rs.getString("ACCOUNT_TYPE_CODE"),
                rs.getString("ACCOUNT_TYPE_DESCRIPTION"),
                rs.getString("CURRENCY_CODE"),
                rs.getBigDecimal("DISPLAY_BALANCE"),
                rs.getString("CONVERSION_INDICATOR"),
                rs.getBigDecimal("RATE")
        );
    }
}
