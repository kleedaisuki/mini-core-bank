package com.moesegfault.banking.domain.customer;

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * @brief 身份证件值对象（Identity Document Value Object），映射 `id_type/id_number/issuing_region`；
 *        Identity document value object mapped to `id_type/id_number/issuing_region`.
 */
public final class IdentityDocument implements Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief 证件号码最大长度（Max Length of Document Number）；
     *        Maximum allowed length of `id_number`.
     */
    private static final int ID_NUMBER_MAX_LENGTH = 64;

    /**
     * @brief 签发地区最大长度（Max Length of Issuing Region）；
     *        Maximum allowed length of `issuing_region`.
     */
    private static final int ISSUING_REGION_MAX_LENGTH = 32;

    /**
     * @brief 证件类型（Document Type）；
     *        Identity document type.
     */
    private final IdentityDocumentType idType;

    /**
     * @brief 证件号码（Document Number）；
     *        Identity document number.
     */
    private final String idNumber;

    /**
     * @brief 签发地区（Issuing Region）；
     *        Identity document issuing region.
     */
    private final String issuingRegion;

    /**
     * @brief 构造身份证件（Construct Identity Document）；
     *        Construct an identity document.
     *
     * @param idType        证件类型（Document type）。
     * @param idNumber      证件号码（Document number）。
     * @param issuingRegion 签发地区（Issuing region）。
     */
    private IdentityDocument(
            final IdentityDocumentType idType,
            final String idNumber,
            final String issuingRegion
    ) {
        this.idType = Objects.requireNonNull(idType, "Identity document type must not be null");
        this.idNumber = normalizeUppercaseText(idNumber, "Identity document number", ID_NUMBER_MAX_LENGTH);
        this.issuingRegion = normalizeUppercaseText(
                issuingRegion,
                "Identity document issuing region",
                ISSUING_REGION_MAX_LENGTH);
    }

    /**
     * @brief 创建身份证件值对象（Factory Method）；
     *        Create an identity document value object.
     *
     * @param idType        证件类型（Document type）。
     * @param idNumber      证件号码（Document number）。
     * @param issuingRegion 签发地区（Issuing region）。
     * @return 身份证件值对象（Identity document value object）。
     */
    public static IdentityDocument of(
            final IdentityDocumentType idType,
            final String idNumber,
            final String issuingRegion
    ) {
        return new IdentityDocument(idType, idNumber, issuingRegion);
    }

    /**
     * @brief 返回证件类型（Return Document Type）；
     *        Return identity document type.
     *
     * @return 证件类型（Document type）。
     */
    public IdentityDocumentType idType() {
        return idType;
    }

    /**
     * @brief 返回证件号码（Return Document Number）；
     *        Return identity document number.
     *
     * @return 证件号码（Document number）。
     */
    public String idNumber() {
        return idNumber;
    }

    /**
     * @brief 返回签发地区（Return Issuing Region）；
     *        Return issuing region.
     *
     * @return 签发地区（Issuing region）。
     */
    public String issuingRegion() {
        return issuingRegion;
    }

    /**
     * @brief 值对象相等判定（Value Object Equality）；
     *        Value-object equality check.
     *
     * @param other 对比对象（Object to compare）。
     * @return 同值返回 true（true when equal）。
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof IdentityDocument that)) {
            return false;
        }
        return idType == that.idType
                && idNumber.equals(that.idNumber)
                && issuingRegion.equals(that.issuingRegion);
    }

    /**
     * @brief 计算哈希值（Compute Hash Code）；
     *        Compute hash code.
     *
     * @return 哈希值（Hash code）。
     */
    @Override
    public int hashCode() {
        return Objects.hash(idType, idNumber, issuingRegion);
    }

    /**
     * @brief 返回字符串表示（String Representation）；
     *        Return string representation.
     *
     * @return `TYPE/NUMBER@REGION` 格式字符串（String in `TYPE/NUMBER@REGION` format）。
     */
    @Override
    public String toString() {
        return idType + "/" + idNumber + "@" + issuingRegion;
    }

    /**
     * @brief 标准化大写文本并校验长度（Normalize Uppercase Text with Length Check）；
     *        Normalize uppercase text and validate its length.
     *
     * @param rawValue   原始输入（Raw input）。
     * @param fieldName  字段名（Field name）。
     * @param maxLength  最大长度（Maximum length）。
     * @return 标准化文本（Normalized text）。
     */
    private static String normalizeUppercaseText(
            final String rawValue,
            final String fieldName,
            final int maxLength
    ) {
        if (rawValue == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        final String normalized = rawValue.trim().toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " length must be <= " + maxLength);
        }
        return normalized;
    }
}
