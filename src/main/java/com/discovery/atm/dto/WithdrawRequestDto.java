package com.discovery.atm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WithdrawRequestDto(
        @NotNull Long clientId,
        @NotNull Long atmId,
        @NotBlank String accountNumber,
        @NotNull @DecimalMin(value = "0.01") BigDecimal requiredAmount
) {
}
