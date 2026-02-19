package com.discovery.atm.repository;

import com.discovery.atm.dto.ClientDto;
import com.discovery.atm.repository.mapper.ClientRowMapper;
import com.discovery.atm.repository.mapper.CurrencyAccountRowMapper;
import com.discovery.atm.repository.mapper.TransactionalAccountRowMapper;
import com.discovery.atm.repository.row.CurrencyAccountRow;
import com.discovery.atm.repository.row.TransactionalAccountRow;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class BalanceQueryRepository {

    public static final String PARAM_CLIENT_ID = "clientId";

    private final JdbcClient jdbcClient;

    public BalanceQueryRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<ClientDto> findClientById(Long clientId) {
        List<ClientDto> clients = jdbcClient.sql(BalanceQuerySql.FIND_CLIENT_BY_ID_SQL)
                .param(PARAM_CLIENT_ID, clientId)
                .query(ClientRowMapper.rowMapper())
                .list();

        return clients.stream().findFirst();
    }

    public List<TransactionalAccountRow> findTransactionalAccountsByClientId(Long clientId) {
        return jdbcClient.sql(BalanceQuerySql.FIND_TRANSACTIONAL_ACCOUNTS_BY_CLIENT_ID_SQL)
                .param(PARAM_CLIENT_ID, clientId)
                .query(TransactionalAccountRowMapper.rowMapper())
                .list();
    }

    public List<CurrencyAccountRow> findCurrencyAccountsByClientId(Long clientId) {
        return jdbcClient.sql(BalanceQuerySql.FIND_CURRENCY_ACCOUNTS_BY_CLIENT_ID_SQL)
                .param(PARAM_CLIENT_ID, clientId)
                .query(CurrencyAccountRowMapper.rowMapper())
                .list();
    }
}
