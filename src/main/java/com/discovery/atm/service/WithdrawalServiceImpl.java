package com.discovery.atm.service;

import com.discovery.atm.algorithm.NoteDispensingAlgorithm;
import com.discovery.atm.dto.ClientDto;
import com.discovery.atm.dto.DispensedDenominationDto;
import com.discovery.atm.dto.ResultDto;
import com.discovery.atm.dto.WithdrawAccountDto;
import com.discovery.atm.dto.WithdrawRequestDto;
import com.discovery.atm.dto.WithdrawResponseDto;
import com.discovery.atm.exception.AmountNotAvailableException;
import com.discovery.atm.exception.AtmNotRegisteredOrUnfundedException;
import com.discovery.atm.exception.InsufficientFundsException;
import com.discovery.atm.exception.InvalidAccountNumberException;
import com.discovery.atm.exception.InvalidClientException;
import com.discovery.atm.mapper.WithdrawAccountMapper;
import com.discovery.atm.repository.BalanceQueryRepository;
import com.discovery.atm.repository.WithdrawalRepository;
import com.discovery.atm.repository.row.AtmNoteAllocationRow;
import com.discovery.atm.repository.row.WithdrawAccountRow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {

    private final BalanceQueryRepository balanceQueryRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final NoteDispensingAlgorithm noteDispensingAlgorithm;
    private final WithdrawAccountMapper withdrawAccountMapper;

    public WithdrawalServiceImpl(
            BalanceQueryRepository balanceQueryRepository,
            WithdrawalRepository withdrawalRepository,
            NoteDispensingAlgorithm noteDispensingAlgorithm,
            WithdrawAccountMapper withdrawAccountMapper) {
        this.balanceQueryRepository = balanceQueryRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.noteDispensingAlgorithm = noteDispensingAlgorithm;
        this.withdrawAccountMapper = withdrawAccountMapper;
    }

    @Override
    @Transactional
    public WithdrawResponseDto withdraw(WithdrawRequestDto request) {
        Long clientId = request.clientId();
        Long atmId = request.atmId();

        ClientDto client = balanceQueryRepository.findClientById(clientId)
                .orElseThrow(InvalidClientException::new);

        WithdrawAccountRow accountRow = withdrawalRepository
                .findTransactionalAccountForClient(clientId, request.accountNumber())
                .orElseThrow(InvalidAccountNumberException::new);

        validateAtm(atmId);

        List<AtmNoteAllocationRow> noteAllocations = withdrawalRepository.findNoteAllocationsByAtmId(atmId);
        if (noteAllocations.isEmpty() || noteAllocations.stream().noneMatch(a -> a.count() != null && a.count() > 0)) {
            throw new AtmNotRegisteredOrUnfundedException();
        }

        BigDecimal requiredAmount = request.requiredAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal availableFunds = withdrawAccountMapper.calculateAvailableFunds(accountRow);
        if (requiredAmount.compareTo(availableFunds) > 0) {
            throw new InsufficientFundsException();
        }

        NoteDispensingAlgorithm.DispenseComputation dispenseComputation =
                noteDispensingAlgorithm.compute(noteAllocations, requiredAmount);

        if (!dispenseComputation.exact()) {
            BigDecimal suggested = dispenseComputation.dispensedAmount().setScale(2, RoundingMode.HALF_UP);
            throw new AmountNotAvailableException(
                    "Amount not available, would you like to draw <R " + suggested + ">"
            );
        }

        BigDecimal newBalance = accountRow.displayBalance().subtract(requiredAmount).setScale(3, RoundingMode.HALF_UP);
        int updatedAccountRows = withdrawalRepository.updateAccountBalance(accountRow.accountNumber(), newBalance);
        if (updatedAccountRows != 1) {
            throw new InvalidAccountNumberException();
        }

        for (NoteDispensingAlgorithm.DispenseLine line : dispenseComputation.lines()) {
            int updatedAllocationRows = withdrawalRepository.decrementAtmAllocation(
                    atmId,
                    line.denominationId(),
                    line.count()
            );

            if (updatedAllocationRows != 1) {
                throw new AmountNotAvailableException("Amount not available, would you like to draw <R "
                        + dispenseComputation.dispensedAmount().setScale(2, RoundingMode.HALF_UP) + ">"
                );
            }
        }

        WithdrawAccountDto account = withdrawAccountMapper.toDto(accountRow, newBalance);
        List<DispensedDenominationDto> denominations = dispenseComputation.lines().stream()
                .map(line -> new DispensedDenominationDto(
                        line.denominationId(),
                        line.denominationValue().setScale(3, RoundingMode.HALF_UP),
                        line.count()
                ))
                .toList();

        return new WithdrawResponseDto(
                client,
                account,
                denominations,
                new ResultDto(true, 200, "Success")
        );
    }

    private void validateAtm(Long atmId) {
        if (!withdrawalRepository.atmExists(atmId)) {
            throw new AtmNotRegisteredOrUnfundedException();
        }
    }
}
