package com.mc.ibpts.paymentapp.repository.utils;

import com.mc.ibpts.paymentapp.exception.CustomBusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CacheRepositoryServiceUtils {
    private static final String CACHE_ERROR_LOG_TRACE_MESSAGE = "Unknown exception happened while accessing cache resource, error={}";

    private final CacheManager cacheManager;

    protected <T, K> List<T> fetchAll(String cacheName, Class<K> kClass, Class<T> tClass) {
        List<T> dataFromCache = new ArrayList<T>();
        try {
            cacheManager.getCache(cacheName, kClass, tClass).forEach(entry -> dataFromCache.add(entry.getValue()));
        } catch (Exception e) {
            log.error(CACHE_ERROR_LOG_TRACE_MESSAGE, e.getMessage());
        }
        return dataFromCache;
    }

    protected <T, K> Optional<T> fetch(String cacheName, Class<K> kClass, Class<T> tClass, K key) {
        T cashedValue;
        try {
            cashedValue = cacheManager.getCache(cacheName, kClass, tClass).get(key);
        } catch (Exception e) {
            log.error(CACHE_ERROR_LOG_TRACE_MESSAGE, e.getMessage());
            cashedValue = null;
        }
        return cashedValue == null ? Optional.empty() : Optional.of(cashedValue);
    }

    protected <T, K> void update(String cacheName, Class<K> kClass, Class<T> tClass, K key, T value) {
        try {
            if (cacheManager.getCache(cacheName, kClass, tClass).containsKey(key)) {
                cacheManager.getCache(cacheName, kClass, tClass).replace(key, value);
            } else {
                cacheManager.getCache(cacheName, kClass, tClass).put(key, value);
            }
        } catch (Exception e) {
            log.error(CACHE_ERROR_LOG_TRACE_MESSAGE, e.getMessage());
        }
    }

    protected <T, K> void insertAll(String cacheName, Class<K> kClass, Class<T> tClass, Map<K, T> value) {
        try {
            cacheManager.getCache(cacheName, kClass, tClass).clear();
            cacheManager.getCache(cacheName, kClass, tClass).putAll(value);
        } catch (Exception e) {
            log.error(CACHE_ERROR_LOG_TRACE_MESSAGE, e.getMessage());
        }
    }

    protected <T, K> Boolean isKeyAvailable(String cacheName, Class<K> kClass, Class<T> tClass, K key) {
        try {
            return cacheManager.getCache(cacheName, kClass, tClass).containsKey(key);
        } catch (Exception e) {
            log.error(CACHE_ERROR_LOG_TRACE_MESSAGE, e.getMessage());
            return false;
        }
    }
}
