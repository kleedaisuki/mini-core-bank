package com.moesegfault.banking.application.ledger.query;

import com.moesegfault.banking.domain.shared.CurrencyCode;
import java.util.Objects;

/**
 * @brief 余额查询请求（Find Balance Query），按账户与币种读取余额快照；
 *        Balance query request for fetching snapshot by account and currency.
 *
 * @param accountId    账户 ID（Account ID）。
 * @param currencyCode 币种代码（Currency code）。
 */
public record FindBalanceQuery(
        String accountId,
        CurrencyCode currencyCode
) {

    /**
     * @brief 紧凑构造并校验查询参数（Compact Constructor with Validation）；
     *        Compact constructor validating query parameters.
     */
    public FindBalanceQuery {
        accountId = normalizeRequiredId(accountId, "Account ID");
        currencyCode = Objects.requireNonNull(currencyCode, "Currency code must not be null");
    }

    /**
     * @brief 标准化并校验必填标识（Normalize Required Identifier）；
     *        Normalize and validate required identifier.
     *
     * @param rawValue 原始值（Raw value）。
     * @param label    字段标签（Field label）。
     * @return 标准化标识（Normalized identifier）。
     */
    private static String normalizeRequiredId(final String rawValue, final String label) {
        if (rawValue == null) {
            throw new IllegalArgumentException(label + " must not be null");
        }
        final String normalized = rawValue.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
        return normalized;
    }
}
