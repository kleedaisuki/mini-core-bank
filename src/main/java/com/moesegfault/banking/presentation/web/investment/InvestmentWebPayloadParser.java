package com.moesegfault.banking.presentation.web.investment;

import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.investment.ProductType;
import com.moesegfault.banking.domain.investment.RiskLevel;
import java.util.Locale;
import java.util.Objects;

/**
 * @brief 投资 Web 载荷解析器（Investment Web Payload Parser）；
 *        Utility parser converting web payload text fields into typed investment fields.
 */
final class InvestmentWebPayloadParser {

    /**
     * @brief 读取并规范化必填字符串（Read and Normalize Required Text）；
     *        Read required text and reject blank values.
     *
     * @param rawValue 原始值（Raw value）。
     * @param fieldName 字段名（Field name）。
     * @return 规范化文本（Normalized text）。
     */
    static String normalizeRequiredText(final String rawValue, final String fieldName) {
        final String normalized = Objects.requireNonNull(rawValue, fieldName + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    /**
     * @brief 读取并规范化可选字符串（Read and Normalize Optional Text）；
     *        Normalize optional text and convert blank values to null.
     *
     * @param rawValue 原始值（Raw value）。
     * @return 规范化文本（Normalized text, nullable）。
     */
    static String normalizeOptionalText(final String rawValue) {
        if (rawValue == null) {
            return null;
        }
        final String normalized = rawValue.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    /**
     * @brief 解析产品类型（Parse Product Type）；
     *        Parse product type enum from text token.
     *
     * @param rawValue 原始文本（Raw text）。
     * @param fieldName 字段名（Field name）。
     * @return 产品类型（Product type）。
     */
    static ProductType parseRequiredProductType(final String rawValue, final String fieldName) {
        return ProductType.valueOf(normalizeEnumToken(normalizeRequiredText(rawValue, fieldName)));
    }

    /**
     * @brief 解析风险等级（Parse Risk Level）；
     *        Parse risk-level enum from required text token.
     *
     * @param rawValue 原始文本（Raw text）。
     * @param fieldName 字段名（Field name）。
     * @return 风险等级（Risk level）。
     */
    static RiskLevel parseRequiredRiskLevel(final String rawValue, final String fieldName) {
        return RiskLevel.valueOf(normalizeEnumToken(normalizeRequiredText(rawValue, fieldName)));
    }

    /**
     * @brief 解析可选风险等级（Parse Optional Risk Level）；
     *        Parse optional risk-level enum from optional text token.
     *
     * @param rawValue 原始文本（Raw text）。
     * @return 风险等级（Risk level, nullable）。
     */
    static RiskLevel parseOptionalRiskLevel(final String rawValue) {
        final String normalized = normalizeOptionalText(rawValue);
        return normalized == null ? null : RiskLevel.valueOf(normalizeEnumToken(normalized));
    }

    /**
     * @brief 解析可选业务渠道（Parse Optional Business Channel）；
     *        Parse optional business-channel enum from optional text token.
     *
     * @param rawValue 原始文本（Raw text）。
     * @return 业务渠道（Business channel, nullable）。
     */
    static BusinessChannel parseOptionalBusinessChannel(final String rawValue) {
        final String normalized = normalizeOptionalText(rawValue);
        return normalized == null ? null : BusinessChannel.valueOf(normalizeEnumToken(normalized));
    }

    /**
     * @brief 解析布尔文本（Parse Boolean Token）；
     *        Parse common boolean text token.
     *
     * @param rawValue 原始值（Raw value）。
     * @param fieldName 字段名（Field name）。
     * @return 布尔值（Boolean value）。
     */
    static boolean parseBooleanToken(final String rawValue, final String fieldName) {
        final String normalized = normalizeRequiredText(rawValue, fieldName).toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "true", "1", "yes", "y" -> true;
            case "false", "0", "no", "n" -> false;
            default -> throw new IllegalArgumentException("Unsupported boolean value for " + fieldName + ": " + rawValue);
        };
    }

    /**
     * @brief 规范化枚举文本（Normalize Enum Token）；
     *        Normalize enum token by trimming, uppercasing and replacing hyphens.
     *
     * @param rawValue 原始文本（Raw text）。
     * @return 规范化枚举文本（Normalized enum token）。
     */
    static String normalizeEnumToken(final String rawValue) {
        return Objects.requireNonNull(rawValue, "rawValue must not be null")
                .trim()
                .toUpperCase(Locale.ROOT)
                .replace('-', '_');
    }

    /**
     * @brief 禁止实例化（No Instance）；
     *        Prevent instantiation.
     */
    private InvestmentWebPayloadParser() {
    }
}
