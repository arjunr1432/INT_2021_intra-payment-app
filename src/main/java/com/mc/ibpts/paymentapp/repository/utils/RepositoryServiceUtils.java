package com.mc.ibpts.paymentapp.repository.utils;

import com.mc.ibpts.paymentapp.exception.CustomBusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class RepositoryServiceUtils {
    private static final String DATABASE_ERROR_LOG_TRACE_MESSAGE = "Unknown exception happened while accessing database resource, error={}";
    private static final String DATABASE_ERROR_RESPONSE_MESSAGE = "Something went wrong, please try again or contact our support team.";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    protected int upsert(String sql, SqlParameterSource sqlParameterSource) {
        try {
            return namedParameterJdbcTemplate.update(sql, sqlParameterSource);
        } catch (Exception e) {
            log.error(DATABASE_ERROR_LOG_TRACE_MESSAGE, e.getMessage());
            throw new CustomBusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    DATABASE_ERROR_RESPONSE_MESSAGE,
                    e);
        }
    }

    protected <T> List<T> fetch(String sql, SqlParameterSource sqlParameterSource, RowMapper<T> mapper) {
        try {
            if (sqlParameterSource == null) {
                return namedParameterJdbcTemplate.query(sql, mapper);
            } else {
                return namedParameterJdbcTemplate.query(sql, sqlParameterSource, mapper);
            }
        } catch (Exception e) {
            log.error(DATABASE_ERROR_LOG_TRACE_MESSAGE, e.getMessage());
            throw new CustomBusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    DATABASE_ERROR_RESPONSE_MESSAGE,
                    e);
        }
    }

    protected <T> Optional<T> fetchObject(String sql, SqlParameterSource sqlParameterSource, RowMapper<T> mapper) {
        try {
            List<T> list = namedParameterJdbcTemplate.query(sql, sqlParameterSource, mapper);
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } catch (Exception e) {
            log.error(DATABASE_ERROR_LOG_TRACE_MESSAGE, e.getMessage());
            throw new CustomBusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    DATABASE_ERROR_RESPONSE_MESSAGE,
                    e);
        }
    }
}
