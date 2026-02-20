package com.discovery.atm.controller;

import com.discovery.atm.dto.WithdrawRequestDto;
import com.discovery.atm.dto.WithdrawResponseDto;
import com.discovery.atm.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponseDto> withdraw(@Valid @RequestBody WithdrawRequestDto request) {
        return ResponseEntity.ok(withdrawalService.withdraw(request));
    }
}
