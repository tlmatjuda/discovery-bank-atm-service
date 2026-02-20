package com.discovery.atm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WithdrawalApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void withdrawReturnsSuccessWhenRequestIsValidAndDispensable() throws Exception {
        String requestBody = """
                {
                  "clientId": 6,
                  "atmId": 1,
                  "accountNumber": "4078108908",
                  "requiredAmount": 150.00
                }
                """;

        mockMvc.perform(post("/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.success").value(true))
                .andExpect(jsonPath("$.result.statusCode").value(200))
                .andExpect(jsonPath("$.account.accountNumber").value("4078108908"))
                .andExpect(jsonPath("$.denomination.length()").value(2));
    }

    @Test
    void withdrawReturnsInsufficientFundsForLowAvailableBalance() throws Exception {
        String requestBody = """
                {
                  "clientId": 2,
                  "atmId": 1,
                  "accountNumber": "4030838761",
                  "requiredAmount": 100.00
                }
                """;

        mockMvc.perform(post("/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.success").value(false))
                .andExpect(jsonPath("$.result.statusCode").value(400))
                .andExpect(jsonPath("$.result.statusReason").value("Insufficient funds"));
    }

    @Test
    void withdrawReturnsBadRequestWhenAtmIsRegisteredButUnfunded() throws Exception {
        String requestBody = """
                {
                  "clientId": 6,
                  "atmId": 7,
                  "accountNumber": "4078108908",
                  "requiredAmount": 100.00
                }
                """;

        mockMvc.perform(post("/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.success").value(false))
                .andExpect(jsonPath("$.result.statusCode").value(400))
                .andExpect(jsonPath("$.result.statusReason").value("ATM not registered or unfunded"));
    }

    @Test
    void withdrawReturnsBadRequestForInvalidClient() throws Exception {
        String requestBody = """
                {
                  "clientId": 99999,
                  "atmId": 1,
                  "accountNumber": "4078108908",
                  "requiredAmount": 100.00
                }
                """;

        mockMvc.perform(post("/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.success").value(false))
                .andExpect(jsonPath("$.result.statusCode").value(400))
                .andExpect(jsonPath("$.result.statusReason").value("Invalid client"));
    }

    @Test
    void withdrawReturnsBadRequestForAccountThatDoesNotBelongToClient() throws Exception {
        String requestBody = """
                {
                  "clientId": 1,
                  "atmId": 1,
                  "accountNumber": "4030838761",
                  "requiredAmount": 100.00
                }
                """;

        mockMvc.perform(post("/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.success").value(false))
                .andExpect(jsonPath("$.result.statusCode").value(400))
                .andExpect(jsonPath("$.result.statusReason").value("Invalid account number"));
    }
}
