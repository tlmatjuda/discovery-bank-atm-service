package com.discovery.atm.dto;

import java.math.BigDecimal;

public record DispensedDenominationDto(
        Long denominationId,
        BigDecimal denominationValue,
        Integer count
) {
}
