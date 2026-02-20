package com.discovery.atm.repository.mapper;

import com.discovery.atm.repository.row.WithdrawAccountRow;
import org.springframework.jdbc.core.RowMapper;

public final class WithdrawAccountRowMapper {

    private WithdrawAccountRowMapper() {
    }

    public static RowMapper<WithdrawAccountRow> rowMapper() {
        return (rs, rowNum) -> new WithdrawAccountRow(
                rs.getString("CLIENT_ACCOUNT_NUMBER"),
                rs.getLong("CLIENT_ID"),
                rs.getString("ACCOUNT_TYPE_CODE"),
                rs.getString("ACCOUNT_TYPE_DESCRIPTION"),
                rs.getString("CURRENCY_CODE"),
                rs.getBigDecimal("DISPLAY_BALANCE"),
                rs.getString("CONVERSION_INDICATOR"),
                rs.getBigDecimal("RATE"),
                rs.getBigDecimal("ACCOUNT_LIMIT")
        );
    }
}
