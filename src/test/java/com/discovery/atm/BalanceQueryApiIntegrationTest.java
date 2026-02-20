package com.discovery.atm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BalanceQueryApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void queryTransactionalBalancesReturnsNoAccountsForClientWithoutTransactionalAccounts() throws Exception {
        mockMvc.perform(get("/queryTransactionalBalances")
                        .param("clientId", "31"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result.success").value(false))
                .andExpect(jsonPath("$.result.statusCode").value(404))
                .andExpect(jsonPath("$.result.statusReason").value("No accounts to display"));
    }

    @Test
    void queryTransactionalBalancesReturnsAccountsSortedByBalanceDescending() throws Exception {
        mockMvc.perform(get("/queryTransactionalBalances")
                        .param("clientId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.success").value(true))
                .andExpect(jsonPath("$.accounts[0].accountNumber").value(4067342946L))
                .andExpect(jsonPath("$.accounts[1].accountNumber").value(5027913218L))
                .andExpect(jsonPath("$.accounts[2].accountNumber").value(1053664521L));
    }

    @Test
    void queryCurrencyBalancesReturnsAccountsSortedByZarBalanceAscending() throws Exception {
        mockMvc.perform(get("/queryCcyBalances")
                        .param("clientId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.success").value(true))
                .andExpect(jsonPath("$.accounts[0].accountNumber").value(9164010053L))
                .andExpect(jsonPath("$.accounts[1].accountNumber").value(9760793578L))
                .andExpect(jsonPath("$.accounts[2].accountNumber").value(9755978035L));
    }
}
