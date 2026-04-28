package com.moesegfault.banking.presentation.gui.account;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @brief Account GUI 输入读取器（Account GUI Input Reader），统一读取并校验表单字段；
 *        Account GUI input reader that normalizes and validates form field values.
 */
final class AccountGuiInputReader {

    /**
     * @brief 私有构造（Private Constructor）;
     *        Private constructor for utility class.
     */
    private AccountGuiInputReader() {
    }

    /**
     * @brief 读取必填字段（Read Required Field）;
     *        Read one required field from form-value map.
     *
     * @param values 表单值映射（Form-value map）。
     * @param fieldName 字段名（Field name）。
     * @return 字段值（Field value）。
     */
    static String required(final Map<String, String> values, final String fieldName) {
        final Map<String, String> normalizedValues = Objects.requireNonNull(values, "values must not be null");
        final String normalizedField = Objects.requireNonNull(fieldName, "fieldName must not be null");
        final String rawValue = normalizedValues.get(normalizedField);
        if (rawValue == null) {
            throw new IllegalArgumentException("Missing required field: " + normalizedField);
        }
        final String normalizedValue = rawValue.trim();
        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException("Field must not be blank: " + normalizedField);
        }
        return normalizedValue;
    }

    /**
     * @brief 读取可选字段（Read Optional Field）;
     *        Read one optional field from form-value map.
     *
     * @param values 表单值映射（Form-value map）。
     * @param fieldName 字段名（Field name）。
     * @return 字段值（Field value），缺失返回空字符串。
     */
    static String optional(final Map<String, String> values, final String fieldName) {
        final Map<String, String> normalizedValues = Objects.requireNonNull(values, "values must not be null");
        final String normalizedField = Objects.requireNonNull(fieldName, "fieldName must not be null");
        final String rawValue = normalizedValues.get(normalizedField);
        if (rawValue == null) {
            return "";
        }
        return rawValue.trim();
    }

    /**
     * @brief 读取布尔字段（Read Boolean Field）;
     *        Read one boolean field with default fallback.
     *
     * @param values 表单值映射（Form-value map）。
     * @param fieldName 字段名（Field name）。
     * @param defaultValue 默认值（Default value）。
     * @return 布尔值（Boolean value）。
     */
    static boolean optionalBoolean(final Map<String, String> values, final String fieldName, final boolean defaultValue) {
        final String rawValue = optional(values, fieldName);
        if (rawValue.isEmpty()) {
            return defaultValue;
        }
        return switch (rawValue.toLowerCase(Locale.ROOT)) {
            case "true", "1", "yes", "y", "on" -> true;
            case "false", "0", "no", "n", "off" -> false;
            default -> throw new IllegalArgumentException(
                    "Invalid boolean field: " + fieldName + ", expected true/false/1/0/yes/no");
        };
    }

    /**
     * @brief 提取事件中的表单值（Extract Form Values from View Event Attribute）;
     *        Extract form-value map from one view-event attribute object.
     *
     * @param rawValue 事件属性值（Event attribute value）。
     * @return 表单值映射（Form-value map）。
     */
    @SuppressWarnings("unchecked")
    static Map<String, String> asFormValues(final Object rawValue) {
        if (!(rawValue instanceof Map<?, ?> rawMap)) {
            throw new IllegalArgumentException("Form values must be a map");
        }
        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof String)) {
                throw new IllegalArgumentException("Form values must be Map<String, String>");
            }
        }
        return (Map<String, String>) rawMap;
    }
}
