package com.mc.ibpts.paymentapp.utils;

import com.mc.ibpts.paymentapp.common.gen.model.AccountBalanceResponse;
import com.mc.ibpts.paymentapp.common.gen.model.AccountDetailsResponseData;
import com.mc.ibpts.paymentapp.common.gen.model.AccountStatementResponseData;
import com.mc.ibpts.paymentapp.common.gen.model.PaymentTransferResponse;
import com.mc.ibpts.paymentapp.dvo.AccountInfo;
import com.mc.ibpts.paymentapp.dvo.TransactionInfo;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Function;

public class RequestResponseMapper {

    private static final NumberFormat AMOUNT_FORMAT = NumberFormat.getInstance(new Locale("en", "US"));
    static {
        AMOUNT_FORMAT.setMinimumFractionDigits( 2 );
        AMOUNT_FORMAT.setMaximumFractionDigits( 2 );
    }


    public static final Function<TransactionInfo, AccountStatementResponseData> TRANSACTION_INFO_TO_STATEMENT =
            transactionInfo -> new AccountStatementResponseData()
                    .accountId(transactionInfo.getType().equals(AccountStatementResponseData.TypeEnum.CREDIT.name())
                            ? transactionInfo.getSenderAccountId() : transactionInfo.getReceiverAccountId())
                    .currency(AccountStatementResponseData.CurrencyEnum.fromValue(transactionInfo.getCurrency()))
                    .amount(AMOUNT_FORMAT.format(transactionInfo.getAmount()))
                    .type(AccountStatementResponseData.TypeEnum.fromValue(transactionInfo.getType()))
                    .transactionDate(transactionInfo.getTransactionDate().toString());

    public static final Function<AccountInfo, AccountDetailsResponseData> ACCOUNT_INFO_TO_ACCOUNT_DETAILS_RESPONSE =
            accountInfo -> new AccountDetailsResponseData()
                    .accountId(accountInfo.getAccountId())
                    .currency(AccountDetailsResponseData.CurrencyEnum.fromValue(accountInfo.getCurrency()))
                    .balance(AMOUNT_FORMAT.format(accountInfo.getBalance()))
                    .accountStatus(AccountDetailsResponseData.AccountStatusEnum.fromValue(accountInfo.getAccountStatus()));

    public static final Function<AccountInfo, AccountBalanceResponse> ACCOUNT_INFO_TO_ACCOUNT_BALANCE_DETAILS_RESPONSE =
            accountInfo -> new AccountBalanceResponse()
                    .accountId(accountInfo.getAccountId())
                    .currency(AccountBalanceResponse.CurrencyEnum.fromValue(accountInfo.getCurrency()))
                    .balance(AMOUNT_FORMAT.format(accountInfo.getBalance()));

    public static final Function<TransactionInfo, PaymentTransferResponse> TRANSACTION_INFO_TO_PAYMENT_TRANSFER_RESPONSE =
            transactionInfo -> new PaymentTransferResponse()
                    .status("Transfer completed successfully")
                    .senderAccountId(transactionInfo.getSenderAccountId())
                    .receiverAccountId(transactionInfo.getReceiverAccountId())
                    .amount(AMOUNT_FORMAT.format(transactionInfo.getAmount()));

}
