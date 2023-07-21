package com.mbienkowski.template.logging.tracing;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(value = HIGHEST_PRECEDENCE)
public class TracingConfig extends OncePerRequestFilter {

    public static final String TRACE_HEADER = "x-trace-id";
    public static final String TRACE_MDC = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        var tracingHeader = request.getHeader(TRACE_HEADER);
        if (isBlank(response.getHeader(TRACE_HEADER))) {
            tracingHeader = RandomStringUtils.randomAlphanumeric(16).toLowerCase();
        }

        MDC.put(TRACE_MDC, tracingHeader);
        if (isBlank(response.getHeader(TRACE_HEADER))) {
            response.addHeader(TRACE_HEADER, tracingHeader);
        }

        filterChain.doFilter(request, response);
    }
}
