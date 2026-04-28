package com.moesegfault.banking.domain.customer;

import java.util.Locale;

/**
 * @brief 证件类型枚举（Identity Document Type Enum），对应 `customer.id_type` 语义；
 *        Identity document type enum aligned to `customer.id_type` semantics.
 */
public enum IdentityDocumentType {

    /**
     * @brief 居民身份证（Identity Card）；
     *        Resident identity card.
     */
    ID_CARD,

    /**
     * @brief 护照（Passport）；
     *        Passport.
     */
    PASSPORT,

    /**
     * @brief 香港身份证（Hong Kong Identity Card, HKID）；
     *        Hong Kong Identity Card (HKID).
     */
    HKID,

    /**
     * @brief 其他证件（Other Document Type）；
     *        Other document type.
     */
    OTHER;

    /**
     * @brief 从数据库字符串解析证件类型（Parse from Database Value）；
     *        Parse identity document type from a database string value.
     *
     * @param rawValue 原始字符串（Raw string value）。
     * @return 证件类型枚举（Identity document type enum）。
     */
    public static IdentityDocumentType fromDatabaseValue(final String rawValue) {
        if (rawValue == null) {
            throw new IllegalArgumentException("Identity document type must not be null");
        }
        final String normalized = rawValue.trim().toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Identity document type must not be blank");
        }
        try {
            return IdentityDocumentType.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported identity document type: " + rawValue, ex);
        }
    }

    /**
     * @brief 输出数据库值（Render Database Value）；
     *        Render enum value for database storage.
     *
     * @return 数据库字符串值（Database string value）。
     */
    public String databaseValue() {
        return name();
    }
}
