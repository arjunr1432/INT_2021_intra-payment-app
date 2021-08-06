package com.mc.ibpts.paymentapp.service;

import com.mc.ibpts.paymentapp.TestSupportUtils;
import com.mc.ibpts.paymentapp.common.gen.model.*;
import com.mc.ibpts.paymentapp.dvo.AccountInfo;
import com.mc.ibpts.paymentapp.exception.CustomBusinessException;
import com.mc.ibpts.paymentapp.repository.RepositoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountsV1ServiceTest {

    private RepositoryService repositoryService;
    private AccountsV1Service accountsV1Service;

    @BeforeAll
    public void init() {
        repositoryService = Mockito.mock(RepositoryService.class);
        accountsV1Service = new AccountsV1Service(repositoryService);
    }

    @Test
    void testV1AccountsAccountIdBalanceGet_Success() {
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.eq(1234L)))
                .thenReturn(TestSupportUtils.getOptionalAccountInfo());

        ResponseEntity<AccountBalanceResponse> responseEntity =  accountsV1Service.v1AccountsAccountIdBalanceGet("1234");

        Assertions.assertEquals(200, responseEntity.getStatusCode().value());
        Assertions.assertEquals(1234L, responseEntity.getBody().getAccountId());
        Assertions.assertEquals("1,000.00", responseEntity.getBody().getBalance());

    }

    @Test
    void testV1AccountsAccountIdBalanceGet_Failed_NoAccountDetails() {
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.eq(1234L)))
                .thenReturn(Optional.empty());

        try {
            accountsV1Service.v1AccountsAccountIdBalanceGet("1234");
        } catch (CustomBusinessException e) {
            Assertions.assertEquals(400, e.getHttpStatus().value());
            Assertions.assertEquals("Invalid account number.", e.getMessage());

        }
    }

    @Test
    void v1AccountsAccountIdStatementsMiniGet_Success() {
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.eq(1234L)))
                .thenReturn(TestSupportUtils.getOptionalAccountInfo());

        Mockito.when(repositoryService.fetchMiniStatementByAccountId(Mockito.eq(1234L)))
                .thenReturn(TestSupportUtils.getTransactionInfoList());
        ResponseEntity<List<AccountStatementResponseData>> responseEntity = accountsV1Service.v1AccountsAccountIdStatementsMiniGet("1234");

        Assertions.assertEquals(200, responseEntity.getStatusCode().value());
        Assertions.assertEquals(3, responseEntity.getBody().size());
    }

    @Test
    void v1AccountsAccountIdStatementsMiniGet_Failed_NoAccountDetails() {
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.eq(1234L)))
                .thenReturn(Optional.empty());
        try {
            accountsV1Service.v1AccountsAccountIdStatementsMiniGet("1234");
        } catch (CustomBusinessException e) {
            Assertions.assertEquals(400, e.getHttpStatus().value());
            Assertions.assertEquals("Invalid account number.", e.getMessage());
        }
    }

    @Test
    void v1AccountsAccountIdStatementsMiniGet_Failed_NoTransactionDetails() {
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.eq(1234L)))
                .thenReturn(TestSupportUtils.getOptionalAccountInfo());

        Mockito.when(repositoryService.fetchMiniStatementByAccountId(Mockito.eq(1234L)))
                .thenReturn(new ArrayList<>());
        try {
            accountsV1Service.v1AccountsAccountIdStatementsMiniGet("1234");
        } catch (CustomBusinessException e) {
            Assertions.assertEquals(200, e.getHttpStatus().value());
            Assertions.assertEquals("No Transaction statement for the account.", e.getMessage());
        }
    }

    @Test
    void v1AccountsGet_Success() {
        Mockito.when(repositoryService.fetchAllAccountInfo())
                .thenReturn(TestSupportUtils.getAccountInfoList());
        ResponseEntity<List<AccountDetailsResponseData>> responseEntity = accountsV1Service.v1AccountsGet();

        Assertions.assertEquals(200, responseEntity.getStatusCode().value());
        Assertions.assertEquals(3, responseEntity.getBody().size());
    }

    @Test
    void v1PaymentsTransferPost_Success() {
        Mockito.doNothing().when(repositoryService).insertIdempotencyKey(Mockito.anyString());
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.anyLong()))
                .thenReturn(TestSupportUtils.getOptionalAccountInfo())
                .thenReturn(TestSupportUtils.getOptionalAccountInfo2());

        Mockito.doNothing().when(repositoryService).saveTransactionDetails(Mockito.any());
        Mockito.doNothing().when(repositoryService).updateBalanceInfo(Mockito.anyLong(), Mockito.any());

        ResponseEntity<PaymentTransferResponse> responseEntity = accountsV1Service.v1PaymentsTransferPost(
                "idem-key", TestSupportUtils.getPaymentTransferRequest());

        Assertions.assertEquals(202, responseEntity.getStatusCode().value());
        Assertions.assertEquals(1234L, responseEntity.getBody().getSenderAccountId());
        Assertions.assertEquals(1111L, responseEntity.getBody().getReceiverAccountId());
        Assertions.assertEquals("Transfer completed successfully", responseEntity.getBody().getStatus());
    }

    @Test
    void v1PaymentsTransferPost_Failed_SenderAccountDeleted() {
        Mockito.doNothing().when(repositoryService).insertIdempotencyKey(Mockito.anyString());
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.anyLong()))
                .thenReturn(TestSupportUtils.getOptionalDeletedAccountInfo());
        try {
            accountsV1Service.v1PaymentsTransferPost(
                    "idem-key", TestSupportUtils.getPaymentTransferRequest());
        } catch (CustomBusinessException e) {
            Assertions.assertEquals(400, e.getHttpStatus().value());
            Assertions.assertEquals("Sender's account is in Deleted status, not able to perform the transaction.", e.getMessage());
        }
    }

    @Test
    void v1PaymentsTransferPost_Failed_InsufficientFund() {
        Mockito.doNothing().when(repositoryService).insertIdempotencyKey(Mockito.anyString());
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.anyLong()))
                .thenReturn(TestSupportUtils.getOptionalAccountInfo());
        try {
            accountsV1Service.v1PaymentsTransferPost(
                    "idem-key", TestSupportUtils.getPaymentTransferRequest().amount("99999.00"));
        } catch (CustomBusinessException e) {
            Assertions.assertEquals(400, e.getHttpStatus().value());
            Assertions.assertEquals("Insufficient funds, not able to perform the transaction.", e.getMessage());
        }
    }

    @Test
    void v1PaymentsTransferPost_Failed_SenderCurrencyNotSupported() {
        Mockito.doNothing().when(repositoryService).insertIdempotencyKey(Mockito.anyString());
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.anyLong()))
                .thenReturn(TestSupportUtils.getOptionalAccountInfo());
        try {
            accountsV1Service.v1PaymentsTransferPost(
                    "idem-key", TestSupportUtils.getPaymentTransferRequest()
                            .currency(PaymentTransferRequest.CurrencyEnum.NOK));
        } catch (CustomBusinessException e) {
            Assertions.assertEquals(400, e.getHttpStatus().value());
            Assertions.assertEquals("Sender's currency not supported.", e.getMessage());
        }
    }

    @Test
    void v1PaymentsTransferPost_Failed_SenderNotFound() {
        Mockito.doNothing().when(repositoryService).insertIdempotencyKey(Mockito.anyString());
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        try {
            accountsV1Service.v1PaymentsTransferPost(
                    "idem-key", TestSupportUtils.getPaymentTransferRequest());
        } catch (CustomBusinessException e) {
            Assertions.assertEquals(400, e.getHttpStatus().value());
            Assertions.assertEquals("Sender's account_id not found.", e.getMessage());
        }
    }

    @Test
    void v1PaymentsTransferPost_Failed_ReceiverAccountDeleted() {
        Mockito.doNothing().when(repositoryService).insertIdempotencyKey(Mockito.anyString());
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.anyLong()))
                .thenReturn(TestSupportUtils.getOptionalAccountInfo())
                .thenReturn(TestSupportUtils.getOptionalDeletedAccountInfo());
        try {
            accountsV1Service.v1PaymentsTransferPost(
                    "idem-key", TestSupportUtils.getPaymentTransferRequest());
        } catch (CustomBusinessException e) {
            Assertions.assertEquals(400, e.getHttpStatus().value());
            Assertions.assertEquals("Receiver's account is in Deleted status, not able to perform the transaction.", e.getMessage());
        }
    }

    @Test
    void v1PaymentsTransferPost_Failed_ReceiverCurrencyNotSupported() {
        Mockito.doNothing().when(repositoryService).insertIdempotencyKey(Mockito.anyString());
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.anyLong()))
                .thenReturn(TestSupportUtils.getOptionalAccountInfo())
                .thenReturn(TestSupportUtils.getOptionalNOKAccountInfo());
        try {
            accountsV1Service.v1PaymentsTransferPost(
                    "idem-key", TestSupportUtils.getPaymentTransferRequest());
        } catch (CustomBusinessException e) {
            Assertions.assertEquals(400, e.getHttpStatus().value());
            Assertions.assertEquals("Receiver's currency not supported.", e.getMessage());
        }
    }

    @Test
    void v1PaymentsTransferPost_Failed_ReceiverNotFound() {
        Mockito.doNothing().when(repositoryService).insertIdempotencyKey(Mockito.anyString());
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.anyLong()))
                .thenReturn(TestSupportUtils.getOptionalAccountInfo())
                .thenReturn(Optional.empty());
        try {
            accountsV1Service.v1PaymentsTransferPost(
                    "idem-key", TestSupportUtils.getPaymentTransferRequest());
        } catch (CustomBusinessException e) {
            Assertions.assertEquals(400, e.getHttpStatus().value());
            Assertions.assertEquals("Receiver's account_id not found.", e.getMessage());
        }
    }

    @Test
    void v1PaymentsTransferPost_Failed_TransferBetweenSameAccounts() {
        Mockito.doNothing().when(repositoryService).insertIdempotencyKey(Mockito.anyString());
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.anyLong()))
                .thenReturn(TestSupportUtils.getOptionalAccountInfo())
                .thenReturn(TestSupportUtils.getOptionalAccountInfo());
        try {
            accountsV1Service.v1PaymentsTransferPost(
                    "idem-key", TestSupportUtils.getPaymentTransferRequest().receiverAccountId(1234L));
        } catch (CustomBusinessException e) {
            Assertions.assertEquals(400, e.getHttpStatus().value());
            Assertions.assertEquals("Sending between same account numbers not permitted.", e.getMessage());
        }
    }
}