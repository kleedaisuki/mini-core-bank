package com.moesegfault.banking.presentation.web.card;

import java.util.Objects;

/**
 * @brief 主信用卡发卡请求 DTO（Issue Credit Card Request DTO），对应 REST `POST /cards/credit` 输入；
 *        Issue-credit-card request DTO aligned with REST `POST /cards/credit` input.
 */
public record IssueCreditCardRequestDto(
        String holder_customer_id,
        String credit_card_account_id,
        String card_no
) {

    /**
     * @brief 规范化并校验请求字段（Normalize and Validate Request Fields）；
     *        Normalize and validate request fields.
     */
    public IssueCreditCardRequestDto {
        holder_customer_id = normalizeRequiredText(holder_customer_id, "holder_customer_id");
        credit_card_account_id = normalizeRequiredText(credit_card_account_id, "credit_card_account_id");
        card_no = normalizeRequiredText(card_no, "card_no");
    }

    /**
     * @brief 规范化必填字符串（Normalize Required Text）；
     *        Normalize required text and reject blank value.
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

