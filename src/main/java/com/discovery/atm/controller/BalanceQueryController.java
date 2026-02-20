package com.discovery.atm.controller;

import com.discovery.atm.dto.CurrencyBalancesResponseDto;
import com.discovery.atm.dto.TransactionalBalancesResponseDto;
import com.discovery.atm.service.BalanceQueryService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class BalanceQueryController {

    private final BalanceQueryService balanceQueryService;

    public BalanceQueryController(BalanceQueryService balanceQueryService) {
        this.balanceQueryService = balanceQueryService;
    }

    @GetMapping("/queryTransactionalBalances")
    public ResponseEntity<TransactionalBalancesResponseDto> queryTransactionalBalances(
            @RequestParam @NotNull @Positive Long clientId) {
        return ResponseEntity.ok(balanceQueryService.queryTransactionalBalances(clientId));
    }

    @GetMapping("/queryCcyBalances")
    public ResponseEntity<CurrencyBalancesResponseDto> queryCcyBalances(
            @RequestParam @NotNull @Positive Long clientId) {
        return ResponseEntity.ok(balanceQueryService.queryCcyBalances(clientId));
    }
}
