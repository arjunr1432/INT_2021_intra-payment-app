package com.mc.ibpts.paymentapp;

import com.mc.ibpts.paymentapp.common.gen.model.PaymentTransferRequest;
import com.mc.ibpts.paymentapp.dvo.AccountInfo;
import com.mc.ibpts.paymentapp.dvo.TransactionInfo;
import org.ehcache.Cache;
import org.ehcache.config.CacheRuntimeConfiguration;
import org.ehcache.spi.loaderwriter.BulkCacheLoadingException;
import org.ehcache.spi.loaderwriter.BulkCacheWritingException;
import org.ehcache.spi.loaderwriter.CacheLoadingException;
import org.ehcache.spi.loaderwriter.CacheWritingException;
import org.hamcrest.internal.ArrayIterator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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

    public static Cache MOCKED_CACHE = new Cache() {
        @Override
        public Iterator iterator() {
            return new ArrayIterator(getAccountInfoList());
        }

        @Override
        public Object get(Object o) throws CacheLoadingException {
            return getOptionalAccountInfo().get();
        }

        @Override
        public void put(Object o, Object o2) throws CacheWritingException {

        }

        @Override
        public boolean containsKey(Object o) {
            return o.equals(1234) ? true : false;
        }

        @Override
        public void remove(Object o) throws CacheWritingException {

        }

        @Override
        public Map getAll(Set set) throws BulkCacheLoadingException {
            return null;
        }

        @Override
        public void putAll(Map map) throws BulkCacheWritingException {

        }

        @Override
        public void removeAll(Set set) throws BulkCacheWritingException {

        }

        @Override
        public void clear() {

        }

        @Override
        public Object putIfAbsent(Object o, Object o2) throws CacheLoadingException, CacheWritingException {
            return null;
        }

        @Override
        public boolean remove(Object o, Object o2) throws CacheWritingException {
            return false;
        }

        @Override
        public Object replace(Object o, Object o2) throws CacheLoadingException, CacheWritingException {
            return null;
        }

        @Override
        public boolean replace(Object o, Object o2, Object v1) throws CacheLoadingException, CacheWritingException {
            return false;
        }

        @Override
        public CacheRuntimeConfiguration getRuntimeConfiguration() {
            return null;
        }
    };
}
