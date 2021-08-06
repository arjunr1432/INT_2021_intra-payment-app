package com.mc.ibpts.paymentapp.repository;

import com.mc.ibpts.paymentapp.TestSupportUtils;
import com.mc.ibpts.paymentapp.dvo.AccountInfo;
import com.mc.ibpts.paymentapp.dvo.TransactionInfo;
import com.mc.ibpts.paymentapp.exception.CustomBusinessException;
import com.mc.ibpts.paymentapp.service.AccountsV1Service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.mc.ibpts.paymentapp.repository.EmbeddedRepositoryService.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmbeddedRepositoryServiceTest {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private EmbeddedRepositoryService embeddedRepositoryService;

    @BeforeAll
    public void init() {
        namedParameterJdbcTemplate = Mockito.mock(NamedParameterJdbcTemplate.class);
        embeddedRepositoryService = new EmbeddedRepositoryService(namedParameterJdbcTemplate);
    }

    @Test
    void fetchAccountInfo_Success() {
        Mockito.when(namedParameterJdbcTemplate.query(
                Mockito.eq(FETCH_ACCOUNT_DETAILS_BY_ID),
                Mockito.any(SqlParameterSource.class),
                Mockito.any(RowMapper.class)))
                .thenReturn(Collections.singletonList(TestSupportUtils.getOptionalAccountInfo().get()));
        Optional<AccountInfo> accountInfo = embeddedRepositoryService.fetchAccountInfo(1234L);
        Assertions.assertFalse(accountInfo.isEmpty());
        Assertions.assertEquals(1234L, accountInfo.get().getAccountId());
    }

    @Test
    void fetchAccountInfo_Failed_EmptyResponse() {
        Mockito.when(namedParameterJdbcTemplate.query(
                Mockito.eq(FETCH_ACCOUNT_DETAILS_BY_ID),
                Mockito.any(SqlParameterSource.class),
                Mockito.any(RowMapper.class)))
                .thenReturn(new ArrayList());
        Optional<AccountInfo> accountInfo = embeddedRepositoryService.fetchAccountInfo(1234L);
        Assertions.assertTrue(accountInfo.isEmpty());
    }

    @Test
    void fetchAccountInfo_Failed_DatabaseException() {
        Mockito.doThrow(new DataAccessResourceFailureException("Exception"))
                .when(namedParameterJdbcTemplate).query(
                Mockito.eq(FETCH_ACCOUNT_DETAILS_BY_ID),
                Mockito.any(SqlParameterSource.class),
                Mockito.any(RowMapper.class));
        try {
            embeddedRepositoryService.fetchAccountInfo(1234L);
        } catch (CustomBusinessException e) {
            Assertions.assertEquals(500, e.getHttpStatus().value());
            Assertions.assertEquals("Something went wrong, please try again or contact our support team.", e.getMessage());

        }
    }

    @Test
    void fetchAllAccountInfo_Success() {
        Mockito.when(namedParameterJdbcTemplate.query(
                Mockito.eq(FETCH_ALL_ACCOUNT_DETAILS),
                Mockito.any(RowMapper.class)))
                .thenReturn(Collections.singletonList(TestSupportUtils.getOptionalAccountInfo().get()));
        List<AccountInfo> accountInfoList = embeddedRepositoryService.fetchAllAccountInfo();
        Assertions.assertFalse(accountInfoList.isEmpty());
        Assertions.assertEquals(1, accountInfoList.size());
    }

    @Test
    void fetchAllAccountInfo_Failed_DatabaseException() {
        Mockito.doThrow(new DataAccessResourceFailureException("Exception"))
                .when(namedParameterJdbcTemplate).query(
                Mockito.eq(FETCH_ALL_ACCOUNT_DETAILS),
                Mockito.any(RowMapper.class));
        try {
            embeddedRepositoryService.fetchAllAccountInfo();
        } catch (CustomBusinessException e) {
            Assertions.assertEquals(500, e.getHttpStatus().value());
            Assertions.assertEquals("Something went wrong, please try again or contact our support team.", e.getMessage());

        }
    }

    @Test
    void fetchMiniStatementByAccountId_Success() {
        Mockito.when(namedParameterJdbcTemplate.query(
                Mockito.eq(FETCH_ACCOUNT_TRANSACTION_DETAILS),
                Mockito.any(SqlParameterSource.class),
                Mockito.any(RowMapper.class)))
                .thenReturn(TestSupportUtils.getTransactionInfoList());

        List<TransactionInfo> transactionInfoList = embeddedRepositoryService.fetchMiniStatementByAccountId(1234L);
        Assertions.assertFalse(transactionInfoList.isEmpty());
        Assertions.assertEquals(3, transactionInfoList.size());
    }

    @Test
    void insertIdempotencyKey_Success() {
        Mockito.when(namedParameterJdbcTemplate.update(
                Mockito.eq(INSERT_IDEMPOTENCY_KEY),
                Mockito.any(SqlParameterSource.class)))
                .thenReturn(1);

        embeddedRepositoryService.insertIdempotencyKey("idem-key");
    }

    @Test
    void insertIdempotencyKey_Failed_DatabaseException() {
        Mockito.doThrow(new DataAccessResourceFailureException("Exception"))
                .when(namedParameterJdbcTemplate).update(
                Mockito.eq(INSERT_IDEMPOTENCY_KEY),
                Mockito.any(SqlParameterSource.class));
        try {
            embeddedRepositoryService.insertIdempotencyKey("idem-key");
        } catch (CustomBusinessException e) {
            Assertions.assertEquals(500, e.getHttpStatus().value());
            Assertions.assertEquals("Something went wrong, please try again or contact our support team.", e.getMessage());

        }
    }

    @Test
    void insertIdempotencyKey_Failed_DuplicateIdemKeyException() {
        Mockito.doThrow(new DuplicateKeyException("Exception"))
                .when(namedParameterJdbcTemplate).update(
                Mockito.eq(INSERT_IDEMPOTENCY_KEY),
                Mockito.any(SqlParameterSource.class));
        try {
            embeddedRepositoryService.insertIdempotencyKey("idem-key");
        } catch (CustomBusinessException e) {
            Assertions.assertEquals(400, e.getHttpStatus().value());
            Assertions.assertEquals("Duplicate Idempotency-Key, please try again with a new key.", e.getMessage());

        }
    }

    @Test
    void saveTransactionDetails_Success() {
        Mockito.when(namedParameterJdbcTemplate.update(
                Mockito.eq(INSERT_TRANSACTION_DETAILS),
                Mockito.any(SqlParameterSource.class)))
                .thenReturn(1);

        embeddedRepositoryService.saveTransactionDetails(
                TestSupportUtils.getTransactionInfoList().get(0));

        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(1)).update(
                Mockito.eq(INSERT_TRANSACTION_DETAILS),
                Mockito.any(SqlParameterSource.class));
    }

    @Test
    void updateBalanceInfo_Success() {
        Mockito.when(namedParameterJdbcTemplate.update(
                Mockito.eq(UPDATE_ACCOUNT_BALANCE),
                Mockito.any(SqlParameterSource.class)))
                .thenReturn(1);

        embeddedRepositoryService.updateBalanceInfo(1234L, new BigDecimal(100));

        Mockito.verify(namedParameterJdbcTemplate, Mockito.times(1)).update(
                Mockito.eq(UPDATE_ACCOUNT_BALANCE),
                Mockito.any(SqlParameterSource.class));
    }
}