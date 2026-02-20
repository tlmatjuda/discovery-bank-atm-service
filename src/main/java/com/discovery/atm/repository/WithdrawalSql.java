package com.discovery.atm.repository;

public final class WithdrawalSql {

    private WithdrawalSql() {
    }

    public static final String FIND_TRANSACTIONAL_ACCOUNT_FOR_WITHDRAW_SQL = """
            SELECT
                ca.CLIENT_ACCOUNT_NUMBER,
                ca.CLIENT_ID,
                ca.ACCOUNT_TYPE_CODE,
                at.DESCRIPTION AS ACCOUNT_TYPE_DESCRIPTION,
                ca.CURRENCY_CODE,
                ca.DISPLAY_BALANCE,
                ccr.CONVERSION_INDICATOR,
                ccr.RATE,
                ccl.ACCOUNT_LIMIT
            FROM CLIENT_ACCOUNT ca
            JOIN ACCOUNT_TYPE at ON at.ACCOUNT_TYPE_CODE = ca.ACCOUNT_TYPE_CODE
            LEFT JOIN CURRENCY_CONVERSION_RATE ccr ON ccr.CURRENCY_CODE = ca.CURRENCY_CODE
            LEFT JOIN CREDIT_CARD_LIMIT ccl ON ccl.CLIENT_ACCOUNT_NUMBER = ca.CLIENT_ACCOUNT_NUMBER
            WHERE ca.CLIENT_ID = :clientId
              AND ca.CLIENT_ACCOUNT_NUMBER = :accountNumber
              AND at.TRANSACTIONAL = TRUE
            """;

    public static final String ATM_EXISTS_SQL = """
            SELECT COUNT(1)
            FROM ATM
            WHERE ATM_ID = :atmId
            """;

    public static final String FIND_NOTE_ALLOCATIONS_BY_ATM_ID_SQL = """
            SELECT
                aa.ATM_ALLOCATION_ID,
                aa.ATM_ID,
                aa.DENOMINATION_ID,
                d.DENOMINATION_VALUE,
                aa.COUNT
            FROM ATM_ALLOCATION aa
            JOIN DENOMINATION d ON d.DENOMINATION_ID = aa.DENOMINATION_ID
            JOIN DENOMINATION_TYPE dt ON dt.DENOMINATION_TYPE_CODE = d.DENOMINATION_TYPE_CODE
            WHERE aa.ATM_ID = :atmId
              AND dt.DENOMINATION_TYPE_CODE = 'N'
            ORDER BY d.DENOMINATION_VALUE DESC
            """;

    public static final String UPDATE_ACCOUNT_BALANCE_SQL = """
            UPDATE CLIENT_ACCOUNT
            SET DISPLAY_BALANCE = :newBalance
            WHERE CLIENT_ACCOUNT_NUMBER = :accountNumber
            """;

    public static final String DECREMENT_ATM_ALLOCATION_SQL = """
            UPDATE ATM_ALLOCATION
            SET COUNT = COUNT - :dispensedCount
            WHERE ATM_ID = :atmId
              AND DENOMINATION_ID = :denominationId
              AND COUNT >= :dispensedCount
            """;
}
