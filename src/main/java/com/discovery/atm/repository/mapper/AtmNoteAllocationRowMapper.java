package com.discovery.atm.repository.mapper;

import com.discovery.atm.repository.row.AtmNoteAllocationRow;
import org.springframework.jdbc.core.RowMapper;

public final class AtmNoteAllocationRowMapper {

    private AtmNoteAllocationRowMapper() {
    }

    public static RowMapper<AtmNoteAllocationRow> rowMapper() {
        return (rs, rowNum) -> new AtmNoteAllocationRow(
                rs.getLong("ATM_ALLOCATION_ID"),
                rs.getLong("ATM_ID"),
                rs.getLong("DENOMINATION_ID"),
                rs.getBigDecimal("DENOMINATION_VALUE"),
                rs.getInt("COUNT")
        );
    }
}
