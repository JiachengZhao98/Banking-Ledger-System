package dev.codescreen;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test all API endpoints in TransactionController in sequence
     */

    //@Disabled
    @Test
    @Order(1)
    public void testPing() throws Exception {
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serverTime").exists());
    }

    //@Disabled
    @Test
    @Order(2)
    public void testLoad() throws Exception {
        TransactionRequest request = new TransactionRequest(
                "msg1",
                "user1",
                "100.00", "USD", "CREDIT"
        );
        String jsonRequest = JsonUtil.toJson(request);
        mockMvc.perform(put("/api/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(request.getUserId()))
                .andExpect(jsonPath("$.messageId").value(request.getMessageId()))
                .andExpect(jsonPath("$.balance.currency").value(request.getCurrency()))
                .andExpect(jsonPath("$.balance.debitOrCredit").value(request.getDebitOrCredit()))
                .andExpect(jsonPath("$.balance.amount").value(request.getAmount()));
    }

    //@Disabled
    @Test
    @Order(3)
    public void testCheckBalance() throws Exception {
        CheckBalanceRequest checkRequest = new CheckBalanceRequest(
                "msg2",
                "user1"
        );
        String jsonCheckRequest = JsonUtil.toJson(checkRequest);
        mockMvc.perform(get("/api/checkBalance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCheckRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(checkRequest.getMessageId()))
                .andExpect(jsonPath("$.userId").value(checkRequest.getUserId()))
                .andExpect(jsonPath("$.balance").value("100.00"));

    }

    //@Disabled
    @Test
    @Order(4)
    public void testAuthorizationWithSufficientFunds() throws Exception {
        TransactionRequest authorizationRequest = new TransactionRequest(
                "msg3",
                "user1",
                "70", "USD", "DEBIT"
        );
        String jsonAuthorizationRequest = JsonUtil.toJson(authorizationRequest);
        mockMvc.perform(put("/api/authorization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAuthorizationRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance.amount").value("30.00"))
                .andExpect(jsonPath("$.userId").value(authorizationRequest.getUserId()))
                .andExpect(jsonPath("$.messageId").value(authorizationRequest.getMessageId()))
                .andExpect(jsonPath("$.balance.currency").value(authorizationRequest.getCurrency()))
                .andExpect(jsonPath("$.balance.debitOrCredit").value(authorizationRequest.getDebitOrCredit()))
                .andExpect(jsonPath("$.responseCode").value("APPROVED"));
    }

    //@Disabled
    @Test
    @Order(5)
    public void testAuthorizationWithInsufficientFunds() throws Exception {
        TransactionRequest authorizationRequest = new TransactionRequest(
                "msg4",
                "user1",
                "70", "USD", "DEBIT"
        );
        String jsonAuthorizationRequest = JsonUtil.toJson(authorizationRequest);
        mockMvc.perform(put("/api/authorization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAuthorizationRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance.amount").value(30.00))
                .andExpect(jsonPath("$.userId").value(authorizationRequest.getUserId()))
                .andExpect(jsonPath("$.messageId").value(authorizationRequest.getMessageId()))
                .andExpect(jsonPath("$.balance.currency").value(authorizationRequest.getCurrency()))
                .andExpect(jsonPath("$.balance.debitOrCredit").value(authorizationRequest.getDebitOrCredit()))
                .andExpect(jsonPath("$.responseCode").value("DENIED"));
    }

    //@Disabled
    @Test
    @Order(6)
    public void testConcurrentTransactions() throws Exception {
        int numberOfThreads = 20;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        Runnable loadTask = () -> {
            try {
                TransactionRequest loadRequest = new TransactionRequest(
                        "msg5",
                        "user2",
                        "100.00", "USD", "CREDIT"
                );
                String jsonLoadRequest = JsonUtil.toJson(loadRequest);
                mockMvc.perform(put("/api/load")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonLoadRequest))
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Runnable authTask = () -> {
            try {
                TransactionRequest authorizationRequest = new TransactionRequest(
                        "msg6",
                        "user2",
                        "50.00", "USD", "CREDIT"
                );
                String jsonAuthorizationRequest = JsonUtil.toJson(authorizationRequest);
                mockMvc.perform(put("/api/authorization")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonAuthorizationRequest))
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(loadTask);
            service.execute(authTask);
        }

        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);
    }

    //@Disabled
    @Test
    @Order(7)
    public void testInternalServerError() throws Exception {
        // Triggering the path that leads to internal server error
        mockMvc.perform(MockMvcRequestBuilders.put("/api/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"bad_user\", \"amount\": -100.0}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.balance.amount").value("ERROR: Amount must be greater than 0"));
    }

    //@Disabled
    @Test
    @Order(8)
    public void testAuthorizationWithNoUserFound() throws Exception {
        TransactionRequest authorizationRequest = new TransactionRequest(
                "msg8",
                "user3",
                "70.00", "USD", "DEBIT"
        );
        String jsonAuthorizationRequest = JsonUtil.toJson(authorizationRequest);
        mockMvc.perform(put("/api/authorization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAuthorizationRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance.amount").value("0.00"))
                .andExpect(jsonPath("$.userId").value(authorizationRequest.getUserId()))
                .andExpect(jsonPath("$.messageId").value(authorizationRequest.getMessageId()))
                .andExpect(jsonPath("$.balance.currency").value(authorizationRequest.getCurrency()))
                .andExpect(jsonPath("$.balance.debitOrCredit").value(authorizationRequest.getDebitOrCredit()))
                .andExpect(jsonPath("$.responseCode").value("DENIED"));
    }
}
