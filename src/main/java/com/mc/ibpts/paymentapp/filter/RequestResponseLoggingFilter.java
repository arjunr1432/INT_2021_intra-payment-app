package com.mc.ibpts.paymentapp.filter;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        Stopwatch stopWatch = Stopwatch.createStarted();
        MDC.clear();
        MDC.put("requestID", UUID.randomUUID().toString());
        httpServletResponse.addHeader("Request-Id", MDC.get("requestID"));
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        log.info("Service responded : RequestURL={}, Method={}, ApiResponse={}, ResponseTime={}ms",
                httpServletRequest.getRequestURI(),
                httpServletRequest.getMethod(),
                httpServletResponse.getStatus(),
                stopWatch.stop().elapsed(TimeUnit.MILLISECONDS));
    }
}
