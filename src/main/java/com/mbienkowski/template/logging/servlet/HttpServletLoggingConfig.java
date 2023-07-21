package com.mbienkowski.template.logging.servlet;

import static com.mbienkowski.template.logging.tracing.TracingConfig.TRACE_MDC;
import static org.zalando.logbook.core.Conditions.exclude;

import java.util.List;
import java.util.function.Predicate;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.CorrelationId;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.Conditions;
import org.zalando.logbook.core.DefaultHttpLogWriter;
import org.zalando.logbook.core.DefaultSink;

@Configuration
public class HttpServletLoggingConfig {

    @Value("#{'${logger.exclude.uris:/health}'.split(',')}")
    private List<String> ignoredUris;

    @Value("#{'${logger.hide-body.content-types:text}'.split(',')}")
    private List<String> hiddenContentTypes;

    @Bean
    public Logbook logbook() {
        return Logbook.builder()
            .correlationId(new TraceIdProvider())
            .sink(
                new DefaultSink(
                    new HttpServletLoggingFormatter(),
                    new DefaultHttpLogWriter()
                )
            )
            .condition(getExclusions())
            .bodyFilter(bodyFilter())
            .build();
    }

    private Predicate<HttpRequest> getExclusions() {
        return exclude(ignoredUris.stream().map(Conditions::requestTo).toList());
    }

    private BodyFilter bodyFilter() {
        return (contentType, body) -> {
            if (contentType != null) {
                if (hiddenContentTypes.stream()
                    .noneMatch(hiddenContentType -> contentType.toLowerCase().contains(hiddenContentType.toLowerCase()))) {
                    return body;
                } else {
                    return "<hidden>";
                }
            }
            return body;
        };
    }

    private static class TraceIdProvider implements CorrelationId {

        @Override
        public String generate(HttpRequest request) {
            return MDC.get(TRACE_MDC);
        }
    }
}
