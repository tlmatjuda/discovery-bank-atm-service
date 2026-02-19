package com.discovery.atm.controller;

import com.discovery.atm.dto.CurrencyBalancesResponseDto;
import com.discovery.atm.dto.TransactionalBalancesResponseDto;
import com.discovery.atm.service.BalanceQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceQueryController {

    private final BalanceQueryService balanceQueryService;

    public BalanceQueryController(BalanceQueryService balanceQueryService) {
        this.balanceQueryService = balanceQueryService;
    }

    @GetMapping("/queryTransactionalBalances")
    public ResponseEntity<TransactionalBalancesResponseDto> queryTransactionalBalances(
            @RequestParam Long clientId) {
        return ResponseEntity.ok(balanceQueryService.queryTransactionalBalances(clientId));
    }

    @GetMapping("/queryCcyBalances")
    public ResponseEntity<CurrencyBalancesResponseDto> queryCcyBalances(
            @RequestParam Long clientId) {
        return ResponseEntity.ok(balanceQueryService.queryCcyBalances(clientId));
    }
}
