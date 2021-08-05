package com.mc.ibpts.paymentapp.repository.utils;

import com.mc.ibpts.paymentapp.dvo.AccountInfo;
import com.mc.ibpts.paymentapp.dvo.TransactionInfo;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class RowMapperUtils {

    public static final RowMapper<AccountInfo> ACCOUNT_INFO_ROW_MAPPER = (resultSet, i) -> {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(resultSet.getLong("account_id"));
        accountInfo.setBalance(resultSet.getDouble("balance"));
        accountInfo.setCurrency(resultSet.getString("currency"));
        accountInfo.setAccountStatus(resultSet.getString("account_status"));
        return accountInfo;
    };

    public static final RowMapper<TransactionInfo> TRANSACTION_INFO_ROW_MAPPER = (resultSet, i) -> {
        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setSenderAccountId(resultSet.getLong("sender_account_id"));
        transactionInfo.setReceiverAccountId(resultSet.getLong("receiver_account_id"));
        transactionInfo.setAmount(resultSet.getDouble("amount"));
        transactionInfo.setCurrency(resultSet.getString("currency"));
        transactionInfo.setTransactionDate(
                LocalDateTime.ofInstant(resultSet.getTimestamp("transaction_date").toInstant(),
                        ZoneId.systemDefault()));
        transactionInfo.setReferenceId(resultSet.getString("reference_id"));
        return transactionInfo;
    };
}
