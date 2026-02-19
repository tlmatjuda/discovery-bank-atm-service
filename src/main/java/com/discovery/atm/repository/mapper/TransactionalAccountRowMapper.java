package com.discovery.atm.repository.mapper;

import com.discovery.atm.repository.row.TransactionalAccountRow;
import org.springframework.jdbc.core.RowMapper;

public final class TransactionalAccountRowMapper {

    private TransactionalAccountRowMapper() {
    }

    public static RowMapper<TransactionalAccountRow> rowMapper() {
        return (rs, rowNum) -> new TransactionalAccountRow(
                rs.getLong("CLIENT_ACCOUNT_NUMBER"),
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
