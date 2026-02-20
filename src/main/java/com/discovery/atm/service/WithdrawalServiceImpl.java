package com.discovery.atm.service;

import com.discovery.atm.algorithm.DispenseComputation;
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
import java.util.function.Predicate;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {

    private static final String SUCCESS_REASON = "Success";

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
        BigDecimal requiredAmount = scaleMoney(request.requiredAmount());

        // 1) Validate actor/account/ATM upfront.
        ClientDto client = findClientOrThrow(clientId);
        WithdrawAccountRow accountRow = findTransactionalAccountOrThrow(clientId, request.accountNumber());
        List<AtmNoteAllocationRow> noteAllocations = findFundedAtmNoteAllocationsOrThrow(atmId);

        // 2) Validate money rules before touching state.
        validateSufficientFunds(accountRow, requiredAmount);
        DispenseComputation dispenseComputation = computeExactDispenseOrThrow(noteAllocations, requiredAmount);

        // 3) Persist account + ATM updates in a single transaction.
        BigDecimal newBalance = applyWithdrawalUpdates(accountRow, atmId, requiredAmount, dispenseComputation);

        return buildWithdrawResponse(client, accountRow, newBalance, dispenseComputation);
    }

    private ClientDto findClientOrThrow(Long clientId) {
        return balanceQueryRepository.findClientById(clientId)
                .orElseThrow(InvalidClientException::new);
    }

    private WithdrawAccountRow findTransactionalAccountOrThrow(Long clientId, String accountNumber) {
        return withdrawalRepository
                .findTransactionalAccountForClient(clientId, accountNumber)
                .orElseThrow(InvalidAccountNumberException::new);
    }

    private List<AtmNoteAllocationRow> findFundedAtmNoteAllocationsOrThrow(Long atmId) {
        if (!withdrawalRepository.atmExists(atmId)) {
            throw new AtmNotRegisteredOrUnfundedException();
        }

        List<AtmNoteAllocationRow> noteAllocations = withdrawalRepository.findNoteAllocationsByAtmId(atmId);
        Predicate<AtmNoteAllocationRow> hasPositiveNoteCound = a -> a.count() != null && a.count() > 0;
        boolean hasAnyNotes = noteAllocations.stream().anyMatch(hasPositiveNoteCound);
        if (!hasAnyNotes) {
            throw new AtmNotRegisteredOrUnfundedException();
        }
        return noteAllocations;
    }

    private void validateSufficientFunds(WithdrawAccountRow accountRow, BigDecimal requiredAmount) {
        BigDecimal availableFunds = withdrawAccountMapper.calculateAvailableFunds(accountRow);
        if (requiredAmount.compareTo(availableFunds) > 0) {
            throw new InsufficientFundsException();
        }
    }

    private DispenseComputation computeExactDispenseOrThrow(
            List<AtmNoteAllocationRow> noteAllocations,
            BigDecimal requiredAmount
    ) {
        DispenseComputation dispenseComputation = noteDispensingAlgorithm.compute(noteAllocations, requiredAmount);
        if (!dispenseComputation.exact()) {
            throw amountNotAvailableForSuggestedValue(dispenseComputation.dispensedAmount());
        }
        return dispenseComputation;
    }

    private BigDecimal applyWithdrawalUpdates(
            WithdrawAccountRow accountRow,
            Long atmId,
            BigDecimal requiredAmount,
            DispenseComputation dispenseComputation
    ) {
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
                throw amountNotAvailableForSuggestedValue(dispenseComputation.dispensedAmount());
            }
        }

        return newBalance;
    }

    private WithdrawResponseDto buildWithdrawResponse(
            ClientDto client,
            WithdrawAccountRow accountRow,
            BigDecimal newBalance,
            DispenseComputation dispenseComputation
    ) {
        WithdrawAccountDto account = withdrawAccountMapper.toDto(accountRow, newBalance);
        List<DispensedDenominationDto> denominations = dispenseComputation.lines().stream()
                .map(line -> new DispensedDenominationDto(
                        line.denominationId(),
                        line.denominationValue().setScale(3, RoundingMode.HALF_UP),
                        line.count()
                ))
                .toList();

        return new WithdrawResponseDto(client, account, denominations, new ResultDto(true, 200, SUCCESS_REASON));
    }

    private AmountNotAvailableException amountNotAvailableForSuggestedValue(BigDecimal suggestedAmount) {
        BigDecimal suggested = scaleMoney(suggestedAmount);
        return new AmountNotAvailableException("Amount not available, would you like to draw <R " + suggested + ">");
    }

    private BigDecimal scaleMoney(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
