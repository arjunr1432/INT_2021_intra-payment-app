package com.mc.ibpts.paymentapp.repository;

import com.mc.ibpts.paymentapp.dvo.AccountInfo;
import com.mc.ibpts.paymentapp.dvo.TransactionInfo;
import com.mc.ibpts.paymentapp.repository.utils.RepositoryServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.mc.ibpts.paymentapp.repository.utils.RowMapperUtils.ACCOUNT_INFO_ROW_MAPPER;
import static com.mc.ibpts.paymentapp.repository.utils.RowMapperUtils.TRANSACTION_INFO_ROW_MAPPER;

@Repository
public class EmbeddedRepositoryService extends RepositoryServiceUtils implements RepositoryService  {

    private static final String FETCH_ACCOUNT_DETAILS_BY_ID = "select * from accounts where account_id=:account_id";
    public static final String FETCH_ALL_ACCOUNT_DETAILS = "select * from accounts";
    public static final String FETCH_ACCOUNT_TRANSACTION_DETAILS = "select * from transactions where (sender_account_id=:account_id OR receiver_account_id=:account_id) order by transaction_date desc limit 20";

    @Autowired
    public EmbeddedRepositoryService(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
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
    public List<TransactionInfo> fetchMiniStatementByAccountId(Long accountId) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("account_id", accountId);
        return fetch(FETCH_ACCOUNT_TRANSACTION_DETAILS, sqlParameterSource, TRANSACTION_INFO_ROW_MAPPER);
    }
}
