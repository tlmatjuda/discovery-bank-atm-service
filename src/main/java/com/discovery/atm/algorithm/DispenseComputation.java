package com.discovery.atm.algorithm;

import java.math.BigDecimal;
import java.util.List;

public record DispenseComputation(
        boolean exact,
        BigDecimal dispensedAmount,
        List<NoteDispensingAlgorithm.DispenseLine> lines
) {
}
