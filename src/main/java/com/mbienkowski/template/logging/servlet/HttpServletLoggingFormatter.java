package com.mbienkowski.template.logging.servlet;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;

@Component
public class HttpServletLoggingFormatter implements HttpLogFormatter {

    @Override
    public String format(Precorrelation precorrelation, HttpRequest request) throws IOException {
        var sb = new StringBuilder();
        sb.append("\n> ---------- Server Incoming Request ----------");
        sb.append("\n> CorrelationId: ").append(precorrelation.getId());
        sb.append("\n> RequestURL: ").append(request.getRequestUri());

        if (request.getQuery() != null) {
            sb.append(request.getQuery());
        }

        sb.append("\n> Http-Method: ").append(request.getMethod());
        sb.append("\n> Content-Type: ").append(request.getContentType());
        sb.append("\n> Headers: ").append(getHeadersMap(request.getHeaders()));

        if (request.getBody() != null && request.getBody().length > 0) {
            sb.append("\n< Body: ").append(IOUtils.toString(request.getBody(), UTF_8.name()));
        }

        sb.append("\n> ---------- End of Server Incoming Request ----------");
        return sb.toString();
    }

    @Override
    public String format(Correlation correlation, HttpResponse response) throws IOException {
        var sb = new StringBuilder();
        sb.append("\n< ---------- Server Outgoing Response ----------");
        sb.append("\n< CorrelationId: ").append(correlation.getId());
        sb.append("\n< Duration (ms): ").append(correlation.getDuration().toMillis());
        sb.append("\n< Response-Code: ").append(response.getStatus());
        sb.append("\n< Content-Type: ").append(response.getContentType());
        sb.append("\n< Headers: ").append(getHeadersMap(response.getHeaders()));

        if (response.getBody() != null && response.getBody().length > 0) {
            sb.append("\n< Body: ").append(IOUtils.toString(response.getBody(), UTF_8.name()));
        }

        sb.append("\n< ---------- End of Server Outgoing Response ----------");

        return sb.toString();
    }

    private Map<String, List<String>> getHeadersMap(final HttpHeaders headers) {
        return headers.keySet().stream().collect(toMap(Function.identity(), headers::get));
    }
}
