package com.moesegfault.banking.presentation.gui.investment;

import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.investment.ProductType;
import com.moesegfault.banking.domain.investment.RiskLevel;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 投资 GUI 载荷解析器（Investment GUI Payload Parser）；
 *        Utility parser for converting view-event payload values into typed command fields.
 */
final class InvestmentGuiPayloadParser {

    /**
     * @brief 读取必填字符串（Read Required Text）；
     *        Read required text field from payload map.
     *
     * @param attributes 事件载荷（Event payload map）。
     * @param fieldName 字段名（Field name）。
     * @return 非空字符串（Non-blank text value）。
     */
    static String requiredText(final Map<String, Object> attributes, final String fieldName) {
        final String text = optionalText(attributes, fieldName);
        if (text == null) {
            throw new IllegalArgumentException("Missing required field: " + fieldName);
        }
        return text;
    }

    /**
     * @brief 读取可选字符串（Read Optional Text）；
     *        Read optional text field from payload map.
     *
     * @param attributes 事件载荷（Event payload map）。
     * @param fieldName 字段名（Field name）。
     * @return 字符串或 null（Text value or null）。
     */
    static String optionalText(final Map<String, Object> attributes, final String fieldName) {
        final Map<String, Object> normalizedAttributes = Objects.requireNonNull(attributes, "attributes must not be null");
        final Object rawValue = normalizedAttributes.get(Objects.requireNonNull(fieldName, "fieldName must not be null"));
        if (rawValue == null) {
            return null;
        }
        final String text = rawValue.toString().trim();
        return text.isEmpty() ? null : text;
    }

    /**
     * @brief 读取必填十进制值（Read Required Decimal）；
     *        Read required decimal field from payload map.
     *
     * @param attributes 事件载荷（Event payload map）。
     * @param fieldName 字段名（Field name）。
     * @return 十进制值（Decimal value）。
     */
    static BigDecimal requiredBigDecimal(final Map<String, Object> attributes, final String fieldName) {
        final String rawText = requiredText(attributes, fieldName);
        return new BigDecimal(rawText);
    }

    /**
     * @brief 读取可选十进制值（Read Optional Decimal）；
     *        Read optional decimal field from payload map.
     *
     * @param attributes 事件载荷（Event payload map）。
     * @param fieldName 字段名（Field name）。
     * @return 十进制值或 null（Decimal value or null）。
     */
    static BigDecimal optionalBigDecimal(final Map<String, Object> attributes, final String fieldName) {
        final String rawText = optionalText(attributes, fieldName);
        return rawText == null ? null : new BigDecimal(rawText);
    }

    /**
     * @brief 读取可选时间戳（Read Optional Instant）；
     *        Read optional ISO-8601 instant field from payload map.
     *
     * @param attributes 事件载荷（Event payload map）。
     * @param fieldName 字段名（Field name）。
     * @return 时间戳或 null（Instant or null）。
     */
    static Instant optionalInstant(final Map<String, Object> attributes, final String fieldName) {
        final String rawText = optionalText(attributes, fieldName);
        return rawText == null ? null : Instant.parse(rawText);
    }

    /**
     * @brief 读取可选布尔值（Read Optional Boolean）；
     *        Read optional boolean field from payload map.
     *
     * @param attributes 事件载荷（Event payload map）。
     * @param fieldName 字段名（Field name）。
     * @param defaultValue 默认值（Default value）。
     * @return 布尔值（Boolean value）。
     */
    static boolean optionalBoolean(final Map<String, Object> attributes, final String fieldName, final boolean defaultValue) {
        final Map<String, Object> normalizedAttributes = Objects.requireNonNull(attributes, "attributes must not be null");
        final Object rawValue = normalizedAttributes.get(Objects.requireNonNull(fieldName, "fieldName must not be null"));
        if (rawValue == null) {
            return defaultValue;
        }
        if (rawValue instanceof Boolean boolValue) {
            return boolValue;
        }
        if (rawValue instanceof Number numberValue) {
            return numberValue.intValue() != 0;
        }

        final String normalizedText = rawValue.toString().trim().toLowerCase(Locale.ROOT);
        return switch (normalizedText) {
            case "true", "1", "yes", "y" -> true;
            case "false", "0", "no", "n" -> false;
            default -> throw new IllegalArgumentException("Unsupported boolean value for field "
                    + fieldName + ": " + rawValue);
        };
    }

    /**
     * @brief 解析产品类型（Parse Product Type）；
     *        Parse product type enum from payload field.
     *
     * @param attributes 事件载荷（Event payload map）。
     * @param fieldName 字段名（Field name）。
     * @return 产品类型（Product type）。
     */
    static ProductType requiredProductType(final Map<String, Object> attributes, final String fieldName) {
        return ProductType.valueOf(normalizeEnumToken(requiredText(attributes, fieldName)));
    }

    /**
     * @brief 解析风险等级（Parse Risk Level）；
     *        Parse risk level enum from payload field.
     *
     * @param attributes 事件载荷（Event payload map）。
     * @param fieldName 字段名（Field name）。
     * @return 风险等级（Risk level）。
     */
    static RiskLevel requiredRiskLevel(final Map<String, Object> attributes, final String fieldName) {
        return RiskLevel.valueOf(normalizeEnumToken(requiredText(attributes, fieldName)));
    }

    /**
     * @brief 解析可选风险等级（Parse Optional Risk Level）；
     *        Parse optional risk level enum from payload field.
     *
     * @param attributes 事件载荷（Event payload map）。
     * @param fieldName 字段名（Field name）。
     * @return 风险等级或 null（Risk level or null）。
     */
    static RiskLevel optionalRiskLevel(final Map<String, Object> attributes, final String fieldName) {
        final String rawText = optionalText(attributes, fieldName);
        return rawText == null ? null : RiskLevel.valueOf(normalizeEnumToken(rawText));
    }

    /**
     * @brief 解析可选业务渠道（Parse Optional Business Channel）；
     *        Parse optional business channel enum from payload field.
     *
     * @param attributes 事件载荷（Event payload map）。
     * @param fieldName 字段名（Field name）。
     * @return 业务渠道或 null（Business channel or null）。
     */
    static BusinessChannel optionalBusinessChannel(final Map<String, Object> attributes, final String fieldName) {
        final String rawText = optionalText(attributes, fieldName);
        return rawText == null ? null : BusinessChannel.valueOf(normalizeEnumToken(rawText));
    }

    /**
     * @brief 规范化枚举令牌（Normalize Enum Token）；
     *        Normalize enum token by trimming, uppercasing and replacing hyphens.
     *
     * @param rawText 原始文本（Raw enum token）。
     * @return 规范化文本（Normalized enum token）。
     */
    static String normalizeEnumToken(final String rawText) {
        return Objects.requireNonNull(rawText, "rawText must not be null")
                .trim()
                .toUpperCase(Locale.ROOT)
                .replace('-', '_');
    }

    /**
     * @brief 禁止实例化（No Instance）；
     *        Prevent instantiation.
     */
    private InvestmentGuiPayloadParser() {
    }
}
