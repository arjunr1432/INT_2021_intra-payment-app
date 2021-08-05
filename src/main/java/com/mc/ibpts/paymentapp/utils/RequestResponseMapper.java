package com.mc.ibpts.paymentapp.utils;

import com.mc.ibpts.paymentapp.common.gen.model.AccountBalanceResponse;
import com.mc.ibpts.paymentapp.common.gen.model.AccountDetailsResponseData;
import com.mc.ibpts.paymentapp.common.gen.model.AccountStatementResponseData;
import com.mc.ibpts.paymentapp.dvo.AccountInfo;
import com.mc.ibpts.paymentapp.dvo.TransactionInfo;

import java.text.DecimalFormat;
import java.util.function.Function;

public class RequestResponseMapper {

    private static final DecimalFormat df2 = new DecimalFormat("#.##");


    public static final Function<TransactionInfo, AccountStatementResponseData> TRANSACTION_INFO_TO_STATEMENT =
            transactionInfo -> new AccountStatementResponseData()
                    .accountId(transactionInfo.getType().equals(AccountStatementResponseData.TypeEnum.CREDIT.name())
                            ? transactionInfo.getSenderAccountId() : transactionInfo.getReceiverAccountId())
                    .currency(AccountStatementResponseData.CurrencyEnum.fromValue(transactionInfo.getCurrency()))
                    .amount(df2.format(transactionInfo.getAmount()))
                    .type(AccountStatementResponseData.TypeEnum.fromValue(transactionInfo.getType()))
                    .transactionDate(transactionInfo.getTransactionDate().toString());

    public static final Function<AccountInfo, AccountDetailsResponseData> ACCOUNT_INFO_TO_ACCOUNT_DETAILS_RESPONSE =
            accountInfo -> new AccountDetailsResponseData()
                    .accountId(accountInfo.getAccountId())
                    .currency(AccountDetailsResponseData.CurrencyEnum.fromValue(accountInfo.getCurrency()))
                    .balance(df2.format(accountInfo.getBalance()))
                    .accountStatus(AccountDetailsResponseData.AccountStatusEnum.fromValue(accountInfo.getAccountStatus()));

    public static final Function<AccountInfo, AccountBalanceResponse> ACCOUNT_INFO_TO_ACCOUNT_BALANCE_DETAILS_RESPONSE =
            accountInfo -> new AccountBalanceResponse()
                    .accountId(accountInfo.getAccountId())
                    .currency(AccountBalanceResponse.CurrencyEnum.fromValue(accountInfo.getCurrency()))
                    .balance(df2.format(accountInfo.getBalance()));
}
