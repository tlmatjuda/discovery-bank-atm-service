package com.discovery.atm.repository;

import com.discovery.atm.constant.DomainConstants;

public final class BalanceQuerySql {

    private BalanceQuerySql() {
    }

    public static final String FIND_CLIENT_BY_ID_SQL = """
            SELECT c.CLIENT_ID, c.TITLE, c.NAME, c.SURNAME
            FROM CLIENT c
            WHERE c.CLIENT_ID = :clientId
            """;

    public static final String FIND_TRANSACTIONAL_ACCOUNTS_BY_CLIENT_ID_SQL = """
            SELECT
                ca.CLIENT_ACCOUNT_NUMBER,
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
              AND at.TRANSACTIONAL = TRUE
            """;

    public static final String FIND_CURRENCY_ACCOUNTS_BY_CLIENT_ID_SQL =
            "SELECT\n"
                    + "    ca.CLIENT_ACCOUNT_NUMBER,\n"
                    + "    ca.ACCOUNT_TYPE_CODE,\n"
                    + "    at.DESCRIPTION AS ACCOUNT_TYPE_DESCRIPTION,\n"
                    + "    ca.CURRENCY_CODE,\n"
                    + "    ca.DISPLAY_BALANCE,\n"
                    + "    ccr.CONVERSION_INDICATOR,\n"
                    + "    ccr.RATE\n"
                    + "FROM CLIENT_ACCOUNT ca\n"
                    + "JOIN ACCOUNT_TYPE at ON at.ACCOUNT_TYPE_CODE = ca.ACCOUNT_TYPE_CODE\n"
                    + "LEFT JOIN CURRENCY_CONVERSION_RATE ccr ON ccr.CURRENCY_CODE = ca.CURRENCY_CODE\n"
                    + "WHERE ca.CLIENT_ID = :clientId\n"
                    + "  AND ca.ACCOUNT_TYPE_CODE = '" + DomainConstants.ACCOUNT_TYPE_CFCA + "'";
}
