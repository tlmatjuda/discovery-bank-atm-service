package com.discovery.atm.service;

import com.discovery.atm.dto.ClientDto;
import com.discovery.atm.dto.CurrencyAccountDto;
import com.discovery.atm.dto.CurrencyBalancesResponseDto;
import com.discovery.atm.dto.ResultDto;
import com.discovery.atm.dto.TransactionalAccountDto;
import com.discovery.atm.dto.TransactionalBalancesResponseDto;
import com.discovery.atm.exception.InvalidClientException;
import com.discovery.atm.exception.NoAccountsToDisplayException;
import com.discovery.atm.mapper.CurrencyAccountMapper;
import com.discovery.atm.mapper.TransactionalAccountMapper;
import com.discovery.atm.repository.BalanceQueryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class BalanceQueryServiceImpl implements BalanceQueryService {

    private final BalanceQueryRepository balanceQueryRepository;
    private final TransactionalAccountMapper transactionalAccountMapper;
    private final CurrencyAccountMapper currencyAccountMapper;

    public BalanceQueryServiceImpl(
            BalanceQueryRepository balanceQueryRepository,
            TransactionalAccountMapper transactionalAccountMapper,
            CurrencyAccountMapper currencyAccountMapper) {
        this.balanceQueryRepository = balanceQueryRepository;
        this.transactionalAccountMapper = transactionalAccountMapper;
        this.currencyAccountMapper = currencyAccountMapper;
    }

    @Override
    public TransactionalBalancesResponseDto queryTransactionalBalances(Long clientId) {
        ClientDto client = findClientOrThrow(clientId);

        List<TransactionalAccountDto> accounts = balanceQueryRepository
                .findTransactionalAccountsByClientId(clientId)
                .stream()
                .map(transactionalAccountMapper::toDto)
                .sorted(Comparator.comparing(TransactionalAccountDto::balance).reversed())
                .toList();

        if (accounts.isEmpty()) {
            throw new NoAccountsToDisplayException();
        }

        return new TransactionalBalancesResponseDto(
                client,
                accounts,
                successResult()
        );
    }

    @Override
    public CurrencyBalancesResponseDto queryCcyBalances(Long clientId) {
        ClientDto client = findClientOrThrow(clientId);

        List<CurrencyAccountDto> accounts = balanceQueryRepository
                .findCurrencyAccountsByClientId(clientId)
                .stream()
                .map(currencyAccountMapper::toDto)
                .sorted(Comparator.comparing(CurrencyAccountDto::zarBalance))
                .toList();

        if (accounts.isEmpty()) {
            throw new NoAccountsToDisplayException();
        }

        return new CurrencyBalancesResponseDto(
                client,
                accounts,
                successResult()
        );
    }

    private ResultDto successResult() {
        return new ResultDto(true, HttpStatus.OK.value(), "Success");
    }

    private ClientDto findClientOrThrow(Long clientId) {
        return balanceQueryRepository.findClientById(clientId)
                .orElseThrow(InvalidClientException::new);
    }
}
