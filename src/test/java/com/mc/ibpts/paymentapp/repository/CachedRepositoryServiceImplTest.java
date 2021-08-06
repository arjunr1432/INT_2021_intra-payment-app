package com.mc.ibpts.paymentapp.repository;

import com.mc.ibpts.paymentapp.TestSupportUtils;
import com.mc.ibpts.paymentapp.dvo.AccountInfo;
import com.mc.ibpts.paymentapp.dvo.TransactionInfo;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.CacheRuntimeConfiguration;
import org.ehcache.core.Ehcache;
import org.ehcache.spi.loaderwriter.BulkCacheLoadingException;
import org.ehcache.spi.loaderwriter.BulkCacheWritingException;
import org.ehcache.spi.loaderwriter.CacheLoadingException;
import org.ehcache.spi.loaderwriter.CacheWritingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.math.BigDecimal;
import java.util.*;

import static com.mc.ibpts.paymentapp.TestSupportUtils.MOCKED_CACHE;
import static com.mc.ibpts.paymentapp.repository.EmbeddedSQLRepositoryServiceImpl.FETCH_ACCOUNT_DETAILS_BY_ID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CachedRepositoryServiceImplTest {

    private CachedRepositoryServiceImpl cachedRepositoryService;
    private  CacheManager cacheManager;
    private RepositoryService repositoryService;

    @BeforeAll
    public void init() {
        this.cacheManager = Mockito.mock(CacheManager.class);;
        this.repositoryService = Mockito.mock(RepositoryService.class);
        this.cachedRepositoryService = new CachedRepositoryServiceImpl(cacheManager, repositoryService);
    }


    @Test
    void fetchAccountInfo_Success_FoundInCache() {
        Cache mockCache = Mockito.mock(Cache.class);

        Mockito.when(cacheManager.getCache(Mockito.eq("accounts"), Mockito.any(), Mockito.any())).thenReturn(mockCache);
        Mockito.when(mockCache.get(Mockito.eq(1234L))).thenReturn(TestSupportUtils.getOptionalAccountInfo().get());

        Optional<AccountInfo> accountInfo = cachedRepositoryService.fetchAccountInfo(1234L);
        Assertions.assertFalse(accountInfo.isEmpty());
        Assertions.assertEquals(1234L, accountInfo.get().getAccountId());
    }

    @Test
    void fetchAccountInfo_Success_FoundInDB() {
        Cache mockCache = Mockito.mock(Cache.class);

        Mockito.when(cacheManager.getCache(Mockito.eq("accounts"), Mockito.any(), Mockito.any())).thenReturn(mockCache);
        Mockito.when(mockCache.get(Mockito.eq(1234L))).thenReturn(null);
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.eq(1234L)))
                .thenReturn(TestSupportUtils.getOptionalAccountInfo());

        Optional<AccountInfo> accountInfo = cachedRepositoryService.fetchAccountInfo(1234L);
        Assertions.assertFalse(accountInfo.isEmpty());
        Assertions.assertEquals(1234L, accountInfo.get().getAccountId());
    }

    @Test
    void fetchAccountInfo_Failed_NotFoundInCacheAndDB() {
        Cache mockCache = Mockito.mock(Cache.class);

        Mockito.when(cacheManager.getCache(Mockito.eq("accounts"), Mockito.any(), Mockito.any())).thenReturn(mockCache);
        Mockito.when(mockCache.get(Mockito.eq(1234L))).thenReturn(null);
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.eq(1234L)))
                .thenReturn(Optional.empty());

        Optional<AccountInfo> accountInfo = cachedRepositoryService.fetchAccountInfo(1234L);
        Assertions.assertTrue(accountInfo.isEmpty());
    }

    @Test
    void fetchAllAccountInfo() {
        Mockito.when(repositoryService.fetchAllAccountInfo())
                .thenReturn(TestSupportUtils.getAccountInfoList());
        Cache mockCache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(Mockito.eq("accounts"), Mockito.any(), Mockito.any())).thenReturn(mockCache);
        List<AccountInfo> accountInfoList = cachedRepositoryService.fetchAllAccountInfo();
        Assertions.assertFalse(accountInfoList.isEmpty());
        Assertions.assertEquals(3, accountInfoList.size());
    }

    @Test
    void fetchAllTransactionInfo() {
        Mockito.when(repositoryService.fetchAllTransactionInfo())
                .thenReturn(TestSupportUtils.getTransactionInfoList());
        Cache mockCache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(Mockito.eq("transactions"), Mockito.any(), Mockito.any())).thenReturn(mockCache);
        List<TransactionInfo> transactionInfoList = cachedRepositoryService.fetchAllTransactionInfo();

        Assertions.assertFalse(transactionInfoList.isEmpty());
        Assertions.assertEquals(3, transactionInfoList.size());
    }

    @Test
    void fetchMiniStatementByAccountId() {
        Mockito.when(repositoryService.fetchAllTransactionInfo())
                .thenReturn(TestSupportUtils.getTransactionInfoList());
        Cache mockCache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(Mockito.eq("transactions"), Mockito.any(), Mockito.any())).thenReturn(mockCache);
        List<TransactionInfo> transactionInfoList = cachedRepositoryService.fetchMiniStatementByAccountId(1234L);

        Assertions.assertFalse(transactionInfoList.isEmpty());
        Assertions.assertEquals(3, transactionInfoList.size());
    }

    @Test
    void insertIdempotencyKey() {
        Cache mockCache = Mockito.mock(Cache.class);
        Mockito.when(cacheManager.getCache(Mockito.eq("idempotency"), Mockito.any(), Mockito.any())).thenReturn(mockCache);
        cachedRepositoryService.insertIdempotencyKey("123456789");
    }
    @Test
    void saveTransactionDetails() {
        Cache mockCache = Mockito.mock(Cache.class);
        Mockito.doNothing().when(repositoryService).saveTransactionDetails(Mockito.any());
        Mockito.when(cacheManager.getCache(Mockito.eq("accounts"), Mockito.any(), Mockito.any())).thenReturn(mockCache);
        Mockito.when(cacheManager.getCache(Mockito.eq("transactions"), Mockito.any(), Mockito.any())).thenReturn(mockCache);
        cachedRepositoryService.saveTransactionDetails(TestSupportUtils.getTransactionInfoList().get(0));

    }

    @Test
    void updateBalanceInfo() {
        Cache mockCache = Mockito.mock(Cache.class);
        Mockito.doNothing().when(repositoryService).updateBalanceInfo(Mockito.any(), Mockito.any());
        Mockito.when(repositoryService.fetchAccountInfo(Mockito.eq(1234L)))
                .thenReturn(TestSupportUtils.getOptionalAccountInfo());
        Mockito.when(cacheManager.getCache(Mockito.eq("accounts"), Mockito.any(), Mockito.any())).thenReturn(mockCache);
        cachedRepositoryService.updateBalanceInfo(1234L, BigDecimal.valueOf(1000));
    }
}