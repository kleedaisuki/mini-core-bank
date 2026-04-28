package com.moesegfault.banking.presentation.web.business;

import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import java.util.Objects;

/**
 * @brief 业务流水响应 DTO（Business Transaction Response DTO），定义 REST 输出 schema；
 *        Business-transaction response DTO defining REST output schema.
 */
public record BusinessTransactionResponseDto(
        String transaction_id,
        String business_type_code,
        String initiator_customer_id,
        String operator_id,
        String channel,
        String transaction_status,
        String requested_at,
        String completed_at,
        String reference_no,
        String remarks
) {

    /**
     * @brief 规范化并校验 DTO 字段（Normalize and Validate DTO Fields）；
     *        Normalize and validate DTO fields.
     */
    public BusinessTransactionResponseDto {
        transaction_id = normalizeRequiredText(transaction_id, "transaction_id");
        business_type_code = normalizeRequiredText(business_type_code, "business_type_code");
        channel = normalizeRequiredText(channel, "channel");
        transaction_status = normalizeRequiredText(transaction_status, "transaction_status");
        requested_at = normalizeRequiredText(requested_at, "requested_at");
        reference_no = normalizeRequiredText(reference_no, "reference_no");
        initiator_customer_id = normalizeNullableText(initiator_customer_id);
        operator_id = normalizeNullableText(operator_id);
        completed_at = normalizeNullableText(completed_at);
        remarks = normalizeNullableText(remarks);
    }

    /**
     * @brief 从应用层结果映射 DTO（Map DTO from Application Result）；
     *        Map response DTO from application-layer result.
     *
     * @param result 应用层业务流水结果（Application business-transaction result）。
     * @return 业务流水响应 DTO（Business-transaction response DTO）。
     */
    public static BusinessTransactionResponseDto from(final BusinessTransactionResult result) {
        final BusinessTransactionResult normalized = Objects.requireNonNull(result, "result must not be null");
        return new BusinessTransactionResponseDto(
                normalized.transactionId(),
                normalized.businessTypeCode(),
                normalized.initiatorCustomerIdOrNull(),
                normalized.operatorIdOrNull(),
                normalized.channel().name(),
                normalized.transactionStatus().name(),
                normalized.requestedAt().toString(),
                normalized.completedAtOrNull() == null ? null : normalized.completedAtOrNull().toString(),
                normalized.referenceNo(),
                normalized.remarksOrNull());
    }

    /**
     * @brief 规范化必填文本（Normalize Required Text）；
     *        Normalize required text and reject blank value.
     *
     * @param value 原始文本（Raw text）。
     * @param fieldName 字段名（Field name）。
     * @return 规范化文本（Normalized text）。
     */
    private static String normalizeRequiredText(final String value, final String fieldName) {
        final String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    /**
     * @brief 规范化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text and collapse blank to null.
     *
     * @param value 原始文本（Raw text, nullable）。
     * @return 规范化文本或 null（Normalized text or null）。
     */
    private static String normalizeNullableText(final String value) {
        if (value == null) {
            return null;
        }
        final String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}

