SELECT
    ranked.CLIENT_ID AS client_id,
    ranked.SURNAME AS client_surname,
    ranked.CLIENT_ACCOUNT_NUMBER AS account_number,
    ranked.ACCOUNT_DESCRIPTION AS account_description,
    ranked.DISPLAY_BALANCE AS display_balance
FROM (
    SELECT
        c.CLIENT_ID,
        c.SURNAME,
        ca.CLIENT_ACCOUNT_NUMBER,
        at.DESCRIPTION AS ACCOUNT_DESCRIPTION,
        ca.DISPLAY_BALANCE,
        ROW_NUMBER() OVER (
            PARTITION BY c.CLIENT_ID
            ORDER BY ca.DISPLAY_BALANCE DESC, ca.CLIENT_ACCOUNT_NUMBER
        ) AS row_num
    FROM CLIENT c
    JOIN CLIENT_ACCOUNT ca ON ca.CLIENT_ID = c.CLIENT_ID
    JOIN ACCOUNT_TYPE at ON at.ACCOUNT_TYPE_CODE = ca.ACCOUNT_TYPE_CODE
    WHERE at.TRANSACTIONAL = TRUE
) ranked
WHERE ranked.row_num = 1
ORDER BY ranked.CLIENT_ID;
