package com.discovery.atm.repository;

import com.discovery.atm.repository.mapper.AtmNoteAllocationRowMapper;
import com.discovery.atm.repository.mapper.WithdrawAccountRowMapper;
import com.discovery.atm.repository.row.AtmNoteAllocationRow;
import com.discovery.atm.repository.row.WithdrawAccountRow;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class WithdrawalRepository {

    public static final String PARAM_ACCOUNT_NUMBER = "accountNumber";
    public static final String PARAM_ATM_ID = "atmId";

    private final JdbcClient jdbcClient;

    public WithdrawalRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<WithdrawAccountRow> findTransactionalAccountForClient(Long clientId, String accountNumber) {
        List<WithdrawAccountRow> rows = jdbcClient.sql(WithdrawalSql.FIND_TRANSACTIONAL_ACCOUNT_FOR_WITHDRAW_SQL)
                .param("clientId", clientId)
                .param(PARAM_ACCOUNT_NUMBER, accountNumber)
                .query(WithdrawAccountRowMapper.rowMapper())
                .list();

        return rows.stream().findFirst();
    }

    public boolean atmExists(Long atmId) {
        Integer count = jdbcClient.sql(WithdrawalSql.ATM_EXISTS_SQL)
                .param(PARAM_ATM_ID, atmId)
                .query(Integer.class)
                .single();

        return count != null && count > 0;
    }

    public List<AtmNoteAllocationRow> findNoteAllocationsByAtmId(Long atmId) {
        return jdbcClient.sql(WithdrawalSql.FIND_NOTE_ALLOCATIONS_BY_ATM_ID_SQL)
                .param(PARAM_ATM_ID, atmId)
                .query(AtmNoteAllocationRowMapper.rowMapper())
                .list();
    }

    public int updateAccountBalance(String accountNumber, BigDecimal newBalance) {
        return jdbcClient.sql(WithdrawalSql.UPDATE_ACCOUNT_BALANCE_SQL)
                .param(PARAM_ACCOUNT_NUMBER, accountNumber)
                .param("newBalance", newBalance)
                .update();
    }

    public int decrementAtmAllocation(Long atmId, Long denominationId, int dispensedCount) {
        return jdbcClient.sql(WithdrawalSql.DECREMENT_ATM_ALLOCATION_SQL)
                .param(PARAM_ATM_ID, atmId)
                .param("denominationId", denominationId)
                .param("dispensedCount", dispensedCount)
                .update();
    }
}
