package com.discovery.atm.repository;

import com.discovery.atm.constant.DomainConstants;

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

    public static final String FIND_NOTE_ALLOCATIONS_BY_ATM_ID_SQL =
            "SELECT\n"
                    + "    aa.ATM_ALLOCATION_ID,\n"
                    + "    aa.ATM_ID,\n"
                    + "    aa.DENOMINATION_ID,\n"
                    + "    d.DENOMINATION_VALUE,\n"
                    + "    aa.COUNT\n"
                    + "FROM ATM_ALLOCATION aa\n"
                    + "JOIN DENOMINATION d ON d.DENOMINATION_ID = aa.DENOMINATION_ID\n"
                    + "JOIN DENOMINATION_TYPE dt ON dt.DENOMINATION_TYPE_CODE = d.DENOMINATION_TYPE_CODE\n"
                    + "WHERE aa.ATM_ID = :atmId\n"
                    + "  AND dt.DENOMINATION_TYPE_CODE = '" + DomainConstants.DENOMINATION_TYPE_NOTE + "'\n"
                    + "ORDER BY d.DENOMINATION_VALUE DESC";

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
