package com.moesegfault.banking.presentation.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.application.account.AccountNotFoundException;
import com.moesegfault.banking.domain.shared.DomainException;
import org.junit.jupiter.api.Test;

/**
 * @brief Web 异常映射测试（Web Exception Mapper Test），验证 HTTP 状态与 JSON 错误码映射；
 *        Web exception-mapper tests verifying HTTP status and JSON error-code mapping.
 */
class WebExceptionMapperTest {

    /**
     * @brief 验证领域异常映射为 422；
     *        Verify domain exception maps to HTTP 422.
     */
    @Test
    void shouldMapDomainExceptionToUnprocessableEntity() {
        final WebExceptionMapper mapper = new WebExceptionMapper();
        final WebContext context = new WebContext(
                "req-1",
                "trace-1",
                java.util.Optional.empty(),
                java.util.Locale.ENGLISH,
                java.time.Instant.parse("2024-01-01T00:00:00Z"));

        final WebResponse response = mapper.toResponse(new DomainException("账户已冻结"), context);

        assertEquals(422, response.statusCode());
        assertTrue(new String(response.body()).contains("\"errorCode\":\"DOMAIN_RULE_VIOLATION\""));
    }

    /**
     * @brief 验证 NotFound 异常映射为 404；
     *        Verify not-found exception maps to HTTP 404.
     */
    @Test
    void shouldMapNotFoundExceptionToNotFound() {
        final WebExceptionMapper mapper = new WebExceptionMapper();
        final WebContext context = new WebContext(
                "req-2",
                "trace-2",
                java.util.Optional.empty(),
                java.util.Locale.ENGLISH,
                java.time.Instant.parse("2024-01-01T00:00:00Z"));

        final WebResponse response = mapper.toResponse(new AccountNotFoundException("账户不存在"), context);

        assertEquals(404, response.statusCode());
        assertTrue(new String(response.body()).contains("\"errorCode\":\"RESOURCE_NOT_FOUND\""));
    }

    /**
     * @brief 验证未知异常映射为 500；
     *        Verify unknown exception maps to HTTP 500.
     */
    @Test
    void shouldMapUnknownExceptionToInternalServerError() {
        final WebExceptionMapper mapper = new WebExceptionMapper();
        final WebContext context = new WebContext(
                "req-3",
                "trace-3",
                java.util.Optional.empty(),
                java.util.Locale.ENGLISH,
                java.time.Instant.parse("2024-01-01T00:00:00Z"));

        final WebResponse response = mapper.toResponse(new RuntimeException("network timeout"), context);

        assertEquals(500, response.statusCode());
        assertTrue(new String(response.body()).contains("\"errorCode\":\"INTERNAL_SERVER_ERROR\""));
    }
}

