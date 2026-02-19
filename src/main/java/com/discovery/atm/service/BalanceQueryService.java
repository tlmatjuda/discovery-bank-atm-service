package com.discovery.atm.service;

import com.discovery.atm.dto.CurrencyBalancesResponseDto;
import com.discovery.atm.dto.TransactionalBalancesResponseDto;

public interface BalanceQueryService {

    TransactionalBalancesResponseDto queryTransactionalBalances(Long clientId);

    CurrencyBalancesResponseDto queryCcyBalances(Long clientId);
}
