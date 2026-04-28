package com.moesegfault.banking.infrastructure.web.jdk;

import com.moesegfault.banking.presentation.web.WebResponse;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @brief JDK Web 响应写入器（JDK Web Response Writer），将 WebResponse 写回 HttpExchange；
 *        JDK web response writer that serializes WebResponse into HttpExchange.
 */
public final class JdkWebResponseWriter {

    /**
     * @brief 将响应写回客户端（Write Response to Client）；
     *        Write web response back to HTTP client.
     *
     * @param exchange HttpExchange（HttpExchange）。
     * @param response Web 响应（Web response）。
     * @throws IOException I/O 异常（I/O exception）。
     */
    public void write(final HttpExchange exchange, final WebResponse response) throws IOException {
        final HttpExchange resolvedExchange = Objects.requireNonNull(exchange, "exchange must not be null");
        final WebResponse resolvedResponse = Objects.requireNonNull(response, "response must not be null");

        final byte[] responseBody = resolvedResponse.body();
        final Map<String, List<String>> responseHeaders = resolvedResponse.headers();

        resolvedExchange.getResponseHeaders().set("Content-Type", resolvedResponse.contentType());
        for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
            for (String value : entry.getValue()) {
                resolvedExchange.getResponseHeaders().add(entry.getKey(), value);
            }
        }

        resolvedExchange.sendResponseHeaders(resolvedResponse.statusCode(), responseBody.length);
        try (OutputStream outputStream = resolvedExchange.getResponseBody()) {
            outputStream.write(responseBody);
        }
    }
}
