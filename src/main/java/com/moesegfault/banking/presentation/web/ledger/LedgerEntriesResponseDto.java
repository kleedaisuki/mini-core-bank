package com.moesegfault.banking.presentation.web.ledger;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

/**
 * @brief 多条分录响应 DTO（Ledger Entries Response DTO），封装账户分录列表与查询元信息；
 *        Ledger-entries response DTO encapsulating account entries and query metadata.
 */
public record LedgerEntriesResponseDto(
        @JsonProperty("account_id") String accountId,
        @JsonProperty("limit") int limit,
        @JsonProperty("total") int total,
        @JsonProperty("items") List<LedgerEntryResponseDto> items
) {

    /**
     * @brief 规范化并校验响应字段（Normalize and Validate Response Fields）；
     *        Normalize and validate ledger-entries response fields.
     */
    public LedgerEntriesResponseDto {
        accountId = normalizeRequiredText(accountId, "account_id");
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }
        if (total < 0) {
            throw new IllegalArgumentException("total must be greater than or equal to 0");
        }
        items = List.copyOf(Objects.requireNonNull(items, "items must not be null"));
    }

    /**
     * @brief 规范化必填文本（Normalize Required Text）；
     *        Normalize required text field and reject blank value.
     *
     * @param rawValue 原始值（Raw value）。
     * @param fieldName 字段名（Field name）。
     * @return 规范化文本（Normalized text）。
     */
    private static String normalizeRequiredText(final String rawValue, final String fieldName) {
        final String normalized = Objects.requireNonNull(rawValue, fieldName + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
