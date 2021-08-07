package com.mc.ibpts.paymentapp.repository;

import com.mc.ibpts.paymentapp.dvo.AccountInfo;
import com.mc.ibpts.paymentapp.dvo.TransactionInfo;
import com.mc.ibpts.paymentapp.exception.CustomBusinessException;
import com.mc.ibpts.paymentapp.repository.utils.CacheRepositoryServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.mc.ibpts.paymentapp.repository.utils.RowMapperUtils.ACCOUNT_INFO_ROW_MAPPER;
import static com.mc.ibpts.paymentapp.repository.utils.RowMapperUtils.TRANSACTION_INFO_ROW_MAPPER;

@Slf4j
@Repository
public class CachedRepositoryServiceImpl extends CacheRepositoryServiceUtils implements RepositoryService  {

    private final RepositoryService repositoryService;
    @Autowired
    public CachedRepositoryServiceImpl(
            CacheManager cacheManager,
            @Qualifier("embeddedSQLRepositoryServiceImpl") RepositoryService repositoryService) {
        super(cacheManager);
        this.repositoryService = repositoryService;

        /* Init the cache by loading all the existing data from database.*/
        fetchAllAccountInfo();
        fetchAllTransactionInfo();
    }


    @Override
    public Optional<AccountInfo> fetchAccountInfo(Long accountId) {
        AtomicReference<Optional<AccountInfo>> accountInfoOptional = new AtomicReference<>(Optional.empty());
        /* Fetch data from cache first, if found return the data else fetch data from database and persist in cache.*/
        fetch("accounts", Long.class, AccountInfo.class, accountId).ifPresentOrElse(
                accountInfo -> {
                    log.info("Fetched Account details from cache.");
                    accountInfoOptional.set(Optional.of(accountInfo));
                },
                () -> {
                    log.info("Account details not found in cache, fetching data from database.");
                    repositoryService.fetchAccountInfo(accountId).ifPresentOrElse(accountInfo -> {
                        log.info("Fetched Account details from database. Saving the data back to cache.");
                        accountInfoOptional.set(Optional.of(accountInfo));
                        /* Update data back to cache*/
                        update("accounts", Long.class, AccountInfo.class, accountId, accountInfo);
                    }, () -> {
                        log.info("Data not found in database.");
                    });
                }
        );
        return accountInfoOptional.get();
    }

    @Override
    public List<AccountInfo> fetchAllAccountInfo() {
        List<AccountInfo> accountInfoList;

        /* Fetch data from cache first, if found return the data else fetch data from database and persist in cache.*/
        accountInfoList = fetchAll("accounts", Long.class, AccountInfo.class);
        if (!CollectionUtils.isEmpty(accountInfoList)) {
            log.info("Fetched all Account details from cache.");
        } else {
            log.info("Account details not found in cache, fetching data from database.");
            accountInfoList = repositoryService.fetchAllAccountInfo();
            if (!CollectionUtils.isEmpty(accountInfoList)) {
                log.info("Fetched Account details from database. Saving the data back to cache.");
                /* Update data back to cache*/
                insertAll("accounts", Long.class, AccountInfo.class, accountInfoList
                        .stream().collect(Collectors.toMap(AccountInfo::getAccountId, o -> o)));
            }
        }
        return accountInfoList;
    }

    @Override
    public List<TransactionInfo> fetchAllTransactionInfo() {
        List<TransactionInfo> transactionInfoList;

        /* Fetch data from cache first, if found return the data else fetch data from database and persist in cache.*/
        transactionInfoList = fetchAll("transactions", String.class, TransactionInfo.class);
        if (!CollectionUtils.isEmpty(transactionInfoList)) {
            log.info("Fetched all Transaction details from cache.");
        } else {
            log.info("Account details not found in cache, fetching data from database.");
            transactionInfoList = repositoryService.fetchAllTransactionInfo();
            if (!CollectionUtils.isEmpty(transactionInfoList)) {
                log.info("Fetched Account details from database. Saving the data back to cache.");
                /* Update data back to cache*/
                insertAll("transactions", String.class, TransactionInfo.class, transactionInfoList
                        .stream().collect(Collectors.toMap(k-> UUID.randomUUID().toString(), v->v)));
            }
        }
        return transactionInfoList;
    }

    @Override
    public List<TransactionInfo> fetchMiniStatementByAccountId(Long accountId) {
        /* Fetch data from cache first, if found return the data else fetch data from database and persist in cache.*/
        return fetchAllTransactionInfo().stream()
                .filter(transactionInfo ->
                        transactionInfo.getSenderAccountId().equals(accountId) ||
                                transactionInfo.getReceiverAccountId().equals(accountId))
                .skip(0).limit(20)      // Limiting the top 20 records.
                .collect(Collectors.toList());
    }

    @Override
    public void insertIdempotencyKey(String idempotencyKey) {
        /* Check if key is already in cache, if so reject the request here itself. */
        if (isKeyAvailable("idempotency", String.class, String.class, idempotencyKey)) {
            log.error("Idempotency key already exists.");
            throw new CustomBusinessException(
                    HttpStatus.BAD_REQUEST,
                    "Duplicate Idempotency-Key, please try again with a new key.");
        } else {
            log.info("Inserting idempotency key to both cache and database.");
            update("idempotency", String.class, String.class, idempotencyKey, LocalDateTime.now().toString());
            repositoryService.insertIdempotencyKey(idempotencyKey);
        }
    }

    @Override
    public void saveTransactionDetails(TransactionInfo transactionInfo) {
        /* Save transaction details in database. */
        repositoryService.saveTransactionDetails(transactionInfo);
        update("transactions", String.class, TransactionInfo.class, LocalDateTime.now().toString(), transactionInfo);
    }

    @Override
    public void updateBalanceInfo(Long accountId, BigDecimal amountToAdd) {
        /* Update balance info in database, then fetch latest account balance from database. */
        repositoryService.updateBalanceInfo(accountId, amountToAdd);
        AccountInfo accountInfo = repositoryService.fetchAccountInfo(accountId).orElseThrow();
        update("accounts", Long.class, AccountInfo.class, accountId, accountInfo);
    }
}
