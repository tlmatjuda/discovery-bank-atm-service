package com.discovery.atm.repository.row;

import java.math.BigDecimal;

public record AtmNoteAllocationRow(
        Long atmAllocationId,
        Long atmId,
        Long denominationId,
        BigDecimal denominationValue,
        Integer count
) {
}
