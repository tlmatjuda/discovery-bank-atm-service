package com.discovery.atm.dto;

import java.util.List;

public record TransactionalBalancesResponseDto(
        ClientDto client,
        List<TransactionalAccountDto> accounts,
        ResultDto result
) {
}
