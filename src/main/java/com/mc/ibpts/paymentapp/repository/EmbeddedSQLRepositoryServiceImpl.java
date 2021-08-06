package com.mc.ibpts.paymentapp.repository;

import com.mc.ibpts.paymentapp.dvo.AccountInfo;
import com.mc.ibpts.paymentapp.dvo.TransactionInfo;
import com.mc.ibpts.paymentapp.exception.CustomBusinessException;
import com.mc.ibpts.paymentapp.repository.utils.SQLRepositoryServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.mc.ibpts.paymentapp.repository.utils.RowMapperUtils.ACCOUNT_INFO_ROW_MAPPER;
import static com.mc.ibpts.paymentapp.repository.utils.RowMapperUtils.TRANSACTION_INFO_ROW_MAPPER;

@Repository
public class EmbeddedSQLRepositoryServiceImpl extends SQLRepositoryServiceUtils implements RepositoryService  {

    public static final String FETCH_ACCOUNT_DETAILS_BY_ID = "select * from accounts where account_id=:account_id";
    public static final String FETCH_ALL_ACCOUNT_DETAILS = "select * from accounts";
    public static final String FETCH_ALL_TRANSACTION_DETAILS = "select * from transactions";
    public static final String FETCH_ACCOUNT_TRANSACTION_DETAILS = "select * from transactions where (sender_account_id=:account_id OR receiver_account_id=:account_id) order by transaction_date desc limit 20";
    public static final String UPDATE_ACCOUNT_BALANCE = "update accounts set balance = balance + :amount_to_add where account_id=:account_id";
    public static final String INSERT_TRANSACTION_DETAILS = "insert into transactions (sender_account_id, receiver_account_id, amount, currency, transaction_date, reference_id) values (:sender_account_id, :receiver_account_id, :amount, :currency, :transaction_date, :reference_id)";
    public static final String INSERT_IDEMPOTENCY_KEY = "insert into idempotency (idempotency_key) values (:idempotency_key)";

    @Autowired
    public EmbeddedSQLRepositoryServiceImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }


    @Override
    public Optional<AccountInfo> fetchAccountInfo(Long accountId) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("account_id", accountId);
        return fetchObject(FETCH_ACCOUNT_DETAILS_BY_ID, sqlParameterSource, ACCOUNT_INFO_ROW_MAPPER);
    }

    @Override
    public List<AccountInfo> fetchAllAccountInfo() {
        return fetch(FETCH_ALL_ACCOUNT_DETAILS, null, ACCOUNT_INFO_ROW_MAPPER);
    }

    @Override
    public List<TransactionInfo> fetchAllTransactionInfo() {
        return fetch(FETCH_ALL_TRANSACTION_DETAILS, null, TRANSACTION_INFO_ROW_MAPPER);
    }

    @Override
    public List<TransactionInfo> fetchMiniStatementByAccountId(Long accountId) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("account_id", accountId);
        return fetch(FETCH_ACCOUNT_TRANSACTION_DETAILS, sqlParameterSource, TRANSACTION_INFO_ROW_MAPPER);
    }

    @Override
    public void insertIdempotencyKey(String idempotencyKey) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("idempotency_key", idempotencyKey);
        try {
            upsert(INSERT_IDEMPOTENCY_KEY, sqlParameterSource);
        } catch (CustomBusinessException e) {
            if (e.getException() != null && e.getException() instanceof DuplicateKeyException) {
                throw new CustomBusinessException(
                        HttpStatus.BAD_REQUEST,
                        "Duplicate Idempotency-Key, please try again with a new key.");
            } else {
                throw e;
            }

        }
    }

    @Override
    public void saveTransactionDetails(TransactionInfo transactionInfo) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("sender_account_id", transactionInfo.getSenderAccountId());
        sqlParameterSource.addValue("receiver_account_id", transactionInfo.getReceiverAccountId());
        sqlParameterSource.addValue("amount", transactionInfo.getAmount());
        sqlParameterSource.addValue("currency", transactionInfo.getCurrency());
        sqlParameterSource.addValue("transaction_date", transactionInfo.getTransactionDate());
        sqlParameterSource.addValue("reference_id", transactionInfo.getReferenceId());

        upsert(INSERT_TRANSACTION_DETAILS, sqlParameterSource);
    }

    @Override
    public void updateBalanceInfo(Long accountId, BigDecimal amountToAdd) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("account_id", accountId);
        sqlParameterSource.addValue("amount_to_add", amountToAdd);
        upsert(UPDATE_ACCOUNT_BALANCE, sqlParameterSource);

    }
}
