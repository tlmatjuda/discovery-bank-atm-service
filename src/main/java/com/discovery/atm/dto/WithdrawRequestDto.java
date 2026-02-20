package com.discovery.atm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.AssertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record WithdrawRequestDto(
        @NotNull @Positive Long clientId,
        @NotNull @Positive Long atmId,
        @NotBlank @Pattern(regexp = "\\d{10}", message = "Account number must be 10 digits") String accountNumber,
        @NotNull @DecimalMin(value = "0.01") BigDecimal requiredAmount
) {
    @AssertTrue(message = "requiredAmount must be in multiples of 10")
    public boolean isRequiredAmountMultipleOfTen() {
        if (requiredAmount == null) {
            return true;
        }
        return requiredAmount
                .setScale(2, RoundingMode.HALF_UP)
                .remainder(BigDecimal.TEN)
                .compareTo(BigDecimal.ZERO) == 0;
    }
}
