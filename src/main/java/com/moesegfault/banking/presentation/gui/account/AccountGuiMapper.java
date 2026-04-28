package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.application.account.result.OpenAccountResult;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @brief Account GUI 结果映射器（Account GUI Result Mapper），将应用层结果映射为 GUI 可渲染结构；
 *        Account GUI result mapper converting application-layer results into GUI render structures.
 */
final class AccountGuiMapper {

    /**
     * @brief 私有构造（Private Constructor）;
     *        Private constructor for utility class.
     */
    private AccountGuiMapper() {
    }

    /**
     * @brief 映射开户结果为标准表格行（Map Open-account Result to Canonical Row）;
     *        Map open-account result to canonical account row.
     *
     * @param result 开户结果（Open-account result）。
     * @return 表格行字段（Table-row fields）。
     */
    static List<String> toCanonicalRow(final OpenAccountResult result) {
        final OpenAccountResult normalized = Objects.requireNonNull(result, "result must not be null");
        return List.of(
                normalized.accountId(),
                normalized.customerId(),
                normalized.accountNo(),
                normalized.accountType(),
                normalized.accountStatus(),
                formatInstant(normalized.openedAt()),
                "",
                normalizeNullable(normalized.linkedSavingsAccountId())
        );
    }

    /**
     * @brief 映射账户结果为标准表格行（Map Account Result to Canonical Row）;
     *        Map account result to canonical account row.
     *
     * @param result 账户结果（Account result）。
     * @return 表格行字段（Table-row fields）。
     */
    static List<String> toCanonicalRow(final AccountResult result) {
        final AccountResult normalized = Objects.requireNonNull(result, "result must not be null");
        return List.of(
                normalized.accountId(),
                normalized.customerId(),
                normalized.accountNo(),
                normalized.accountType(),
                normalized.accountStatus(),
                formatInstant(normalized.openedAt()),
                formatInstantNullable(normalized.closedAt()),
                normalizeNullable(normalized.linkedSavingsAccountId())
        );
    }

    /**
     * @brief 从开户结果提取表单字段（Extract Form Values from Open-account Result）;
     *        Extract form values from open-account result.
     *
     * @param result 开户结果（Open-account result）。
     * @return 表单字段映射（Form-value map）。
     */
    static Map<String, String> toFormValues(final OpenAccountResult result) {
        final OpenAccountResult normalized = Objects.requireNonNull(result, "result must not be null");
        return Map.of(
                AccountGuiSchema.CUSTOMER_ID, normalized.customerId(),
                AccountGuiSchema.ACCOUNT_NO, normalized.accountNo()
        );
    }

    /**
     * @brief 格式化时间戳（Format Instant）;
     *        Format instant into ISO-8601 text.
     *
     * @param instant 时间戳（Instant）。
     * @return 文本格式时间（Text timestamp）。
     */
    static String formatInstant(final Instant instant) {
        return Objects.requireNonNull(instant, "instant must not be null").toString();
    }

    /**
     * @brief 格式化可空时间戳（Format Nullable Instant）;
     *        Format nullable instant into text.
     *
     * @param instant 时间戳（Instant, nullable）。
     * @return 文本格式时间（Text timestamp, empty when null）。
     */
    static String formatInstantNullable(final Instant instant) {
        return instant == null ? "" : instant.toString();
    }

    /**
     * @brief 标准化可空文本（Normalize Nullable Text）;
     *        Normalize nullable text by trimming and null-to-empty conversion.
     *
     * @param raw 原始文本（Raw text）。
     * @return 规范化文本（Normalized text）。
     */
    static String normalizeNullable(final String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim();
    }
}
