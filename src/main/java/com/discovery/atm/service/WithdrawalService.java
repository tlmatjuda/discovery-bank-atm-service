package com.discovery.atm.service;

import com.discovery.atm.dto.WithdrawRequestDto;
import com.discovery.atm.dto.WithdrawResponseDto;

public interface WithdrawalService {

    WithdrawResponseDto withdraw(WithdrawRequestDto request);
}
