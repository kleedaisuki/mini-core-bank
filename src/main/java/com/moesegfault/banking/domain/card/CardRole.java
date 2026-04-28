package com.moesegfault.banking.domain.card;

import java.util.Locale;

/**
 * @brief 信用卡角色枚举（Credit Card Role Enum），对应 `credit_card.card_role` 语义；
 *        Credit card role enum mapped to `credit_card.card_role` semantics.
 */
public enum CardRole {

    /**
     * @brief 主卡（Primary Card）；
     *        Primary card role.
     */
    PRIMARY,

    /**
     * @brief 附属卡（Supplementary Card）；
     *        Supplementary card role.
     */
    SUPPLEMENTARY;

    /**
     * @brief 从数据库字符串解析角色（Parse Role from Database Value）；
     *        Parse role from database string value.
     *
     * @param rawValue 原始值（Raw string value）。
     * @return 卡角色（Card role）。
     */
    public static CardRole fromDatabaseValue(final String rawValue) {
        if (rawValue == null) {
            throw new IllegalArgumentException("Card role must not be null");
        }
        final String normalized = rawValue.trim().toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Card role must not be blank");
        }
        try {
            return CardRole.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported card role: " + rawValue, ex);
        }
    }

    /**
     * @brief 输出数据库值（Render Database Value）；
     *        Render enum value for database persistence.
     *
     * @return 数据库字符串值（Database value）。
     */
    public String databaseValue() {
        return name();
    }
}
