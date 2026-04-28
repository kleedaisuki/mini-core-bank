package com.moesegfault.banking.presentation.web.card;

import com.moesegfault.banking.application.card.result.CardResult;
import com.moesegfault.banking.application.card.result.IssueCardResult;
import java.util.Objects;

/**
 * @brief 卡响应 DTO（Card Response DTO），统一发卡成功与卡详情查询输出 schema；
 *        Card response DTO unifying output schema for issuance success and card-detail query.
 */
public record CardResponseDto(
        String card_id,
        String masked_card_number,
        String holder_customer_id,
        String card_status,
        String card_kind,
        String issued_at,
        String expired_at,
        String savings_account_id,
        String fx_account_id,
        String credit_card_account_id,
        String primary_card_id
) {

    /**
     * @brief 规范化并校验响应字段（Normalize and Validate Response Fields）；
     *        Normalize and validate response fields.
     */
    public CardResponseDto {
        card_id = normalizeRequiredText(card_id, "card_id");
        masked_card_number = normalizeRequiredText(masked_card_number, "masked_card_number");
        holder_customer_id = normalizeRequiredText(holder_customer_id, "holder_customer_id");
        card_status = normalizeRequiredText(card_status, "card_status");
        card_kind = normalizeRequiredText(card_kind, "card_kind");
        issued_at = normalizeRequiredText(issued_at, "issued_at");
        expired_at = normalizeNullableText(expired_at);
        savings_account_id = normalizeNullableText(savings_account_id);
        fx_account_id = normalizeNullableText(fx_account_id);
        credit_card_account_id = normalizeNullableText(credit_card_account_id);
        primary_card_id = normalizeNullableText(primary_card_id);
    }

    /**
     * @brief 从发卡结果构建响应 DTO（Build Response DTO from Issue Result）；
     *        Build response DTO from card-issuance result.
     *
     * @param issueResult 发卡结果（Issue result）。
     * @return 卡响应 DTO（Card response DTO）。
     */
    public static CardResponseDto fromIssueResult(final IssueCardResult issueResult) {
        final IssueCardResult normalizedResult = Objects.requireNonNull(issueResult, "issueResult must not be null");
        return new CardResponseDto(
                normalizedResult.cardId(),
                normalizedResult.maskedCardNumber(),
                normalizedResult.holderCustomerId(),
                normalizedResult.cardStatus(),
                normalizedResult.cardKind().name(),
                normalizedResult.issuedAt().toString(),
                null,
                null,
                null,
                null,
                normalizedResult.primaryCardIdOrNull());
    }

    /**
     * @brief 从卡查询结果构建响应 DTO（Build Response DTO from Card Query Result）；
     *        Build response DTO from card-query result.
     *
     * @param cardResult 卡查询结果（Card query result）。
     * @return 卡响应 DTO（Card response DTO）。
     */
    public static CardResponseDto fromCardResult(final CardResult cardResult) {
        final CardResult normalizedResult = Objects.requireNonNull(cardResult, "cardResult must not be null");
        return new CardResponseDto(
                normalizedResult.cardId(),
                normalizedResult.maskedCardNumber(),
                normalizedResult.holderCustomerId(),
                normalizedResult.cardStatus(),
                normalizedResult.cardKind().name(),
                normalizedResult.issuedAt().toString(),
                instantToStringOrNull(normalizedResult.expiredAtOrNull()),
                normalizedResult.savingsAccountIdOrNull(),
                normalizedResult.fxAccountIdOrNull(),
                normalizedResult.creditCardAccountIdOrNull(),
                normalizedResult.primaryCardIdOrNull());
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

    /**
     * @brief 规范化可空字符串（Normalize Nullable Text）；
     *        Normalize nullable text and collapse blank to null.
     *
     * @param rawValue 原始值（Raw value）。
     * @return 规范化结果（Normalized result）。
     */
    private static String normalizeNullableText(final String rawValue) {
        if (rawValue == null) {
            return null;
        }
        final String normalized = rawValue.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        return normalized;
    }

    /**
     * @brief 转换可空时间为字符串（Convert Nullable Instant to String）；
     *        Convert nullable instant value to ISO-8601 string.
     *
     * @param instantValue 时间值（Instant value）。
     * @return ISO-8601 字符串（ISO-8601 string）或 null。
     */
    private static String instantToStringOrNull(final java.time.Instant instantValue) {
        return instantValue == null ? null : instantValue.toString();
    }
}
