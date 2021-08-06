package com.mc.ibpts.paymentapp;

import com.mc.ibpts.paymentapp.common.gen.model.PaymentTransferRequest;
import com.mc.ibpts.paymentapp.dvo.AccountInfo;
import com.mc.ibpts.paymentapp.dvo.TransactionInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestSupportUtils {

    public static Optional<AccountInfo> getOptionalAccountInfo() {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(1234L);
        accountInfo.setCurrency("GBP");
        accountInfo.setBalance(new BigDecimal(1000));
        accountInfo.setAccountStatus("ACTIVE");
        return Optional.of(accountInfo);
    }

    public static Optional<AccountInfo> getOptionalAccountInfo2() {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(1111L);
        accountInfo.setCurrency("GBP");
        accountInfo.setBalance(new BigDecimal(2000));
        accountInfo.setAccountStatus("ACTIVE");
        return Optional.of(accountInfo);
    }

    public static Optional<AccountInfo> getOptionalDeletedAccountInfo() {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(1234L);
        accountInfo.setCurrency("GBP");
        accountInfo.setBalance(new BigDecimal(1000));
        accountInfo.setAccountStatus("DELETED");
        return Optional.of(accountInfo);
    }

    public static Optional<AccountInfo> getOptionalNOKAccountInfo() {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(1111L);
        accountInfo.setCurrency("NOK");
        accountInfo.setBalance(new BigDecimal(2000));
        accountInfo.setAccountStatus("ACTIVE");
        return Optional.of(accountInfo);
    }

    public static List<TransactionInfo> getTransactionInfoList() {
        List<TransactionInfo> transactionInfoList = new ArrayList<>();
        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setSenderAccountId(1234L);
        transactionInfo.setReceiverAccountId(1111L);
        transactionInfo.setAmount(new BigDecimal(123));
        transactionInfo.setCurrency("GBP");
        transactionInfo.setTransactionDate(LocalDateTime.now());
        transactionInfo.setReferenceId("123123jk-13123n-123123-123123");
        transactionInfoList.add(transactionInfo);

        transactionInfo = new TransactionInfo();
        transactionInfo.setSenderAccountId(4444L);
        transactionInfo.setReceiverAccountId(1234L);
        transactionInfo.setAmount(new BigDecimal(1000));
        transactionInfo.setCurrency("GBP");
        transactionInfo.setTransactionDate(LocalDateTime.now());
        transactionInfo.setReferenceId("123123jk-13123n-123123-123123");
        transactionInfoList.add(transactionInfo);

        transactionInfo = new TransactionInfo();
        transactionInfo.setSenderAccountId(1134L);
        transactionInfo.setReceiverAccountId(1234L);
        transactionInfo.setAmount(new BigDecimal(444));
        transactionInfo.setCurrency("GBP");
        transactionInfo.setTransactionDate(LocalDateTime.now());
        transactionInfo.setReferenceId("123123jk-13123n-123123-123123");
        transactionInfoList.add(transactionInfo);
        return transactionInfoList;
    }

    public static List<AccountInfo> getAccountInfoList() {
        List<AccountInfo> accountInfoList = new ArrayList<>();
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(1234L);
        accountInfo.setBalance(new BigDecimal(1000));
        accountInfo.setCurrency("GBP");
        accountInfo.setAccountStatus("ACTIVE");
        accountInfoList.add(accountInfo);

        accountInfo = new AccountInfo();
        accountInfo.setAccountId(1111L);
        accountInfo.setBalance(new BigDecimal(1000));
        accountInfo.setCurrency("GBP");
        accountInfo.setAccountStatus("ACTIVE");
        accountInfoList.add(accountInfo);

        accountInfo = new AccountInfo();
        accountInfo.setAccountId(2222L);
        accountInfo.setBalance(new BigDecimal(1000));
        accountInfo.setCurrency("GBP");
        accountInfo.setAccountStatus("ACTIVE");
        accountInfoList.add(accountInfo);
        return accountInfoList;
    }

    public static PaymentTransferRequest getPaymentTransferRequest() {
        PaymentTransferRequest paymentTransferRequest = new PaymentTransferRequest();
        paymentTransferRequest.setSenderAccountId(1234L);
        paymentTransferRequest.setReceiverAccountId(1111L);
        paymentTransferRequest.setCurrency(PaymentTransferRequest.CurrencyEnum.GBP);
        paymentTransferRequest.setAmount("100.00");
        return paymentTransferRequest;
    }
}
