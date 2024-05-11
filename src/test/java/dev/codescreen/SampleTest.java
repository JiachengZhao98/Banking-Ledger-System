package dev.codescreen;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SampleTest {
    @Autowired
    private MockMvc mockMvc;

    /**
     * Test data from sample_tests
     */

    @Test
    public void sampleTest() throws Exception {
        List<SampleTestTransaction> transactions = CsvParser.parseCsv("src/test/java/resources/sample_tests");
        for (SampleTestTransaction transaction : transactions) {
            TransactionRequest request = new TransactionRequest(
                    transaction.getMsgId(),
                    transaction.getUserId(),
                    transaction.getAmount(),
                    "USD",
                    transaction.getDebitOrCredit()
            );
            String jsonRequest = JsonUtil.toJson(request);
            if (transaction.getAction().equals("LOAD")) {
                mockMvc.perform(put("/api/load")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.balance.amount").value(transaction.getBalance()));
            } else {
                mockMvc.perform(put("/api/authorization")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.responseCode").value(transaction.getResponseCode()))
                        .andExpect(jsonPath("$.balance.amount").value(transaction.getBalance()));
            }
        }
    }
}

