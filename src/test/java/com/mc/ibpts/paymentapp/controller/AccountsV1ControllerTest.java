package com.mc.ibpts.paymentapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountsV1ControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void v1AccountsGet() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = get("/v1/accounts");
        mockHttpServletRequestBuilder.header("Authorization", "Basic YWRtaW46JGVDNkV0");
        this.mockMvc.perform(mockHttpServletRequestBuilder).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("\"account_id\":111")));
    }

    @Test
    void v1AccountsGet_Unauthorized() throws Exception {
        this.mockMvc.perform(get("/v1/accounts")).andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Request Unauthorised, validate the credentials.")));
    }

    @Test
    void v1AccountsAccountIdBalanceGet() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = get("/v1/accounts/111/balance");
        mockHttpServletRequestBuilder.header("Authorization", "Basic YWRtaW46JGVDNkV0");
        this.mockMvc.perform(mockHttpServletRequestBuilder).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("\"account_id\":111")));
    }

    @Test
    void v1AccountsAccountIdStatementsMiniGet() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = get("/v1/accounts/111/statements/mini");
        mockHttpServletRequestBuilder.header("Authorization", "Basic YWRtaW46JGVDNkV0");
        this.mockMvc.perform(mockHttpServletRequestBuilder).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("\"account_id\":444")));
    }



    @Test
    void v1PaymentsTransferPost_Success() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/v1/payments/transfer");
        mockHttpServletRequestBuilder.header("Authorization", "Basic YWRtaW46JGVDNkV0")
                .header("Idempotency-Key", "idem-key");
        mockHttpServletRequestBuilder.content("{\n" +
                "  \"sender_account_id\": 111,\n" +
                "  \"receiver_account_id\": 222,\n" +
                "  \"amount\": \"10.05\",\n" +
                "  \"currency\": \"GBP\"\n" +
                "}");
        mockHttpServletRequestBuilder.contentType("application/json");

        this.mockMvc.perform(mockHttpServletRequestBuilder).andDo(print()).andExpect(status().isAccepted())
                .andExpect(content().string(containsString("Transfer completed successfully")));
    }

    @Test
    void v1PaymentsTransferPost_Exception_InvalidContentType() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/v1/payments/transfer");
        mockHttpServletRequestBuilder.header("Authorization", "Basic YWRtaW46JGVDNkV0");
        mockHttpServletRequestBuilder.content("{\n" +
                "  \"sender_account_id\": 111,\n" +
                "  \"receiver_account_id\": 222,\n" +
                "  \"amount\": \"10.05\",\n" +
                "  \"currency\": \"GBP\"\n" +
                "}");

        this.mockMvc.perform(mockHttpServletRequestBuilder).andDo(print()).andExpect(status().isUnsupportedMediaType())
                .andExpect(content().string(containsString("Invalid Content-Type provided.")));
    }

    @Test
    void v1PaymentsTransferPost_Exception_MissingHeaders() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/v1/payments/transfer");
        mockHttpServletRequestBuilder.header("Authorization", "Basic YWRtaW46JGVDNkV0");
        mockHttpServletRequestBuilder.content("{\n" +
                "  \"sender_account_id\": 111,\n" +
                "  \"receiver_account_id\": 222,\n" +
                "  \"amount\": \"10.05\",\n" +
                "  \"currency\": \"GBP\"\n" +
                "}");
        mockHttpServletRequestBuilder.contentType("application/json");

        this.mockMvc.perform(mockHttpServletRequestBuilder).andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Required request header 'Idempotency-Key' for method parameter type String is not present")));
    }

    @Test
    void v1PaymentsTransferPost_Exception_MethordNotAllowed() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = get("/v1/payments/transfer");
        mockHttpServletRequestBuilder.header("Authorization", "Basic YWRtaW46JGVDNkV0");
        mockHttpServletRequestBuilder.content("{\n" +
                "  \"sender_account_id\": 111,\n" +
                "  \"receiver_account_id\": 222,\n" +
                "  \"amount\": \"10.05\",\n" +
                "  \"currency\": \"GBP\"\n" +
                "}");

        this.mockMvc.perform(mockHttpServletRequestBuilder).andDo(print()).andExpect(status().isMethodNotAllowed())
                .andExpect(content().string(containsString("Requested method is not allowed for the API.")));
    }

    @Test
    void v1PaymentsTransferPost_Exception_ValidationError() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/v1/payments/transfer");
        mockHttpServletRequestBuilder.header("Authorization", "Basic YWRtaW46JGVDNkV0")
                .header("Idempotency-Key", "idem-key");
        mockHttpServletRequestBuilder.content("{\n" +
                "  \"sender_account_id\": null,\n" +
                "  \"receiver_account_id\": 222,\n" +
                "  \"amount\": \"10.05\",\n" +
                "  \"currency\": \"GBP\"\n" +
                "}");
        mockHttpServletRequestBuilder.contentType("application/json");

        this.mockMvc.perform(mockHttpServletRequestBuilder).andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("must not be null")));
    }

    @Test
    void v1PaymentsTransferPost_Exception_JSONParseError() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/v1/payments/transfer");
        mockHttpServletRequestBuilder.header("Authorization", "Basic YWRtaW46JGVDNkV0")
                .header("Idempotency-Key", "idem-key");
        mockHttpServletRequestBuilder.content("{\n" +
                "  \"sender_account_id\": 123,\n" +
                "  \"receiver_account_id\": 222,\n" +
                "  \"amount\": \"10.05\",\n" +
                "  \"currency\": \"ABC\"\n" +
                "}");
        mockHttpServletRequestBuilder.contentType("application/json");

        this.mockMvc.perform(mockHttpServletRequestBuilder).andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("JSON parse error")));
    }

    @Test
    void v1PaymentsTransferPost_Exception_404_NotFound() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/v1/payments/invalid");
        mockHttpServletRequestBuilder.header("Authorization", "Basic YWRtaW46JGVDNkV0")
                .header("Idempotency-Key", "idem-key");
        mockHttpServletRequestBuilder.content("{\n" +
                "  \"sender_account_id\": 123,\n" +
                "  \"receiver_account_id\": 222,\n" +
                "  \"amount\": \"10.05\",\n" +
                "  \"currency\": \"GBP\"\n" +
                "}");
        mockHttpServletRequestBuilder.contentType("application/json");

        this.mockMvc.perform(mockHttpServletRequestBuilder).andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString("The service API is not available, kindly validate the URL and try again.")));
    }

    @Test
    void v1PaymentsTransferPost_Exception_CustomExceptionInsufficientBalance() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/v1/payments/transfer");
        mockHttpServletRequestBuilder.header("Authorization", "Basic YWRtaW46JGVDNkV0")
                .header("Idempotency-Key", "idem-key2");
        mockHttpServletRequestBuilder.content("{\n" +
                "  \"sender_account_id\": 111,\n" +
                "  \"receiver_account_id\": 222,\n" +
                "  \"amount\": \"90000.05\",\n" +
                "  \"currency\": \"GBP\"\n" +
                "}");
        mockHttpServletRequestBuilder.contentType("application/json");

        this.mockMvc.perform(mockHttpServletRequestBuilder).andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Insufficient funds, not able to perform the transaction.")));
    }
}