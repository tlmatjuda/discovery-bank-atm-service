package com.discovery.atm.dto;

import java.util.List;

public record WithdrawResponseDto(
        ClientDto client,
        WithdrawAccountDto account,
        List<DispensedDenominationDto> denomination,
        ResultDto result
) {
}
