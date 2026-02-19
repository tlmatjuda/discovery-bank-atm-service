package com.discovery.atm.dto;

import java.util.List;

public record CurrencyBalancesResponseDto(
        ClientDto client,
        List<CurrencyAccountDto> accounts,
        ResultDto result
) {
}
