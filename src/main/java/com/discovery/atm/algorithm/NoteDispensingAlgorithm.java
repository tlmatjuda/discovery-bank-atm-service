package com.discovery.atm.algorithm;

import com.discovery.atm.repository.row.AtmNoteAllocationRow;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class NoteDispensingAlgorithm {

    private static final int INF = 1_000_000;

    public DispenseComputation compute(List<AtmNoteAllocationRow> noteAllocations, BigDecimal requiredAmount) {
        // Work in 10-rand units so note math stays integer and coin values are naturally excluded.
        int requiredCents = toCents(requiredAmount);
        int targetUnits = requiredCents / 1000;
        boolean exactCandidate = requiredCents % 1000 == 0;

        // Ignore denominations that are currently unavailable in the ATM.
        List<AtmNoteAllocationRow> usableAllocations = noteAllocations.stream()
                .filter(a -> a.count() != null && a.count() > 0)
                .toList();

        if (usableAllocations.isEmpty()) {
            return new DispenseComputation(false, BigDecimal.ZERO, List.of());
        }

        int n = usableAllocations.size();
        int[] unitValues = new int[n];
        int[] maxCounts = new int[n];

        // Prepare bounded denomination arrays for dynamic programming.
        for (int i = 0; i < n; i++) {
            unitValues[i] = noteValueToUnits(usableAllocations.get(i).denominationValue());
            maxCounts[i] = usableAllocations.get(i).count();
        }

        int[][] dp = new int[n + 1][targetUnits + 1];
        int[][] choice = new int[n + 1][targetUnits + 1];

        for (int i = 0; i <= n; i++) {
            for (int a = 0; a <= targetUnits; a++) {
                dp[i][a] = INF;
                choice[i][a] = 0;
            }
        }
        dp[0][0] = 0;

        // Bounded knapsack: minimize number of notes while respecting each denomination's count limit.
        for (int i = 1; i <= n; i++) {
            int value = unitValues[i - 1];
            int limit = maxCounts[i - 1];

            for (int amount = 0; amount <= targetUnits; amount++) {
                int best = INF;
                int bestK = 0;
                int maxK = Math.min(limit, value == 0 ? 0 : amount / value);

                for (int k = 0; k <= maxK; k++) {
                    int prevAmount = amount - (k * value);
                    int prev = dp[i - 1][prevAmount];
                    if (prev == INF) {
                        continue;
                    }

                    int candidate = prev + k;
                    if (candidate < best) {
                        best = candidate;
                        bestK = k;
                    }
                }

                dp[i][amount] = best;
                choice[i][amount] = bestK;
            }
        }

        // Prefer exact payout; otherwise choose the best lower dispensable amount.
        int chosenUnits = -1;
        if (exactCandidate && dp[n][targetUnits] != INF) {
            chosenUnits = targetUnits;
        } else {
            for (int amount = targetUnits; amount >= 0; amount--) {
                if (dp[n][amount] != INF) {
                    chosenUnits = amount;
                    break;
                }
            }
        }

        if (chosenUnits < 0) {
            return new DispenseComputation(false, BigDecimal.ZERO, List.of());
        }

        // Rebuild denomination lines from DP choices and return exact flag + dispensed total.
        List<DispenseLine> lines = reconstructLines(usableAllocations, choice, unitValues, chosenUnits);
        BigDecimal dispensedAmount = unitsToAmount(chosenUnits);
        boolean exact = exactCandidate && chosenUnits == targetUnits;

        return new DispenseComputation(exact, dispensedAmount, lines);
    }

    private List<DispenseLine> reconstructLines(
            List<AtmNoteAllocationRow> allocations,
            int[][] choice,
            int[] unitValues,
            int chosenUnits
    ) {
        List<DispenseLine> lines = new ArrayList<>();
        int amount = chosenUnits;

        for (int i = allocations.size(); i >= 1; i--) {
            int usedCount = choice[i][amount];
            if (usedCount > 0) {
                AtmNoteAllocationRow allocation = allocations.get(i - 1);
                lines.add(new DispenseLine(
                        allocation.denominationId(),
                        allocation.denominationValue().setScale(2, RoundingMode.HALF_UP),
                        usedCount
                ));
                amount -= usedCount * unitValues[i - 1];
            }
        }

        Collections.reverse(lines);
        return lines;
    }

    private int toCents(BigDecimal amount) {
        return amount
                .setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2)
                .intValueExact();
    }

    private int noteValueToUnits(BigDecimal denominationValue) {
        int cents = denominationValue.setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2)
                .intValueExact();
        return cents / 1000;
    }

    private BigDecimal unitsToAmount(int units) {
        return BigDecimal.valueOf(units)
                .multiply(BigDecimal.TEN)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public record DispenseLine(
            Long denominationId,
            BigDecimal denominationValue,
            int count
    ) {
    }

}
