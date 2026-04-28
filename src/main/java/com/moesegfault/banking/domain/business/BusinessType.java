package com.moesegfault.banking.domain.business;

import java.util.Objects;

/**
 * @brief 业务类型实体（Business Type Entity），映射 `business_type` 并承载业务码能力元数据；
 *        Business type entity mapped to `business_type` and carrying capability metadata of business codes.
 */
public final class BusinessType {

    /**
     * @brief 业务类型码（Business Type Code）；
     *        Business type code.
     */
    private final BusinessTypeCode businessTypeCode;

    /**
     * @brief 业务分类（Business Category）；
     *        Business category.
     */
    private final BusinessCategory businessCategory;

    /**
     * @brief 业务名称（Business Name）；
     *        Business name.
     */
    private final String businessName;

    /**
     * @brief 业务说明（Business Description, Nullable）；
     *        Business description, nullable.
     */
    private final String description;

    /**
     * @brief 是否金融类业务（Is Financial Business）；
     *        Whether this is a financial business type.
     */
    private final boolean financial;

    /**
     * @brief 是否可冲正（Is Reversible）；
     *        Whether this type supports reversal.
     */
    private final boolean reversible;

    /**
     * @brief 类型状态（Type Status）；
     *        Type status.
     */
    private BusinessTypeStatus status;

    /**
     * @brief 构造业务类型实体（Construct Business Type Entity）；
     *        Construct business type entity.
     *
     * @param businessTypeCode 业务类型码（Business type code）。
     * @param businessCategory 业务分类（Business category）。
     * @param businessName     业务名称（Business name）。
     * @param description      业务说明（Description, nullable）。
     * @param financial        是否金融类（Is financial）。
     * @param reversible       是否可冲正（Is reversible）。
     * @param status           类型状态（Type status）。
     */
    private BusinessType(
            final BusinessTypeCode businessTypeCode,
            final BusinessCategory businessCategory,
            final String businessName,
            final String description,
            final boolean financial,
            final boolean reversible,
            final BusinessTypeStatus status
    ) {
        this.businessTypeCode = Objects.requireNonNull(businessTypeCode, "Business type code must not be null");
        this.businessCategory = Objects.requireNonNull(businessCategory, "Business category must not be null");
        this.businessName = normalizeName(businessName);
        this.description = normalizeNullableText(description);
        this.financial = financial;
        this.reversible = reversible;
        this.status = Objects.requireNonNull(status, "Business type status must not be null");
    }

    /**
     * @brief 创建激活业务类型（Create Active Business Type）；
     *        Create an active business type.
     *
     * @param businessTypeCode 业务类型码（Business type code）。
     * @param businessCategory 业务分类（Business category）。
     * @param businessName     业务名称（Business name）。
     * @param description      业务说明（Description, nullable）。
     * @param financial        是否金融类（Is financial）。
     * @param reversible       是否可冲正（Is reversible）。
     * @return 业务类型实体（Business type entity）。
     */
    public static BusinessType create(
            final BusinessTypeCode businessTypeCode,
            final BusinessCategory businessCategory,
            final String businessName,
            final String description,
            final boolean financial,
            final boolean reversible
    ) {
        return new BusinessType(
                businessTypeCode,
                businessCategory,
                businessName,
                description,
                financial,
                reversible,
                BusinessTypeStatus.ACTIVE);
    }

    /**
     * @brief 从持久化状态重建业务类型（Restore Business Type from Persistence）；
     *        Restore business type from persistence state.
     *
     * @param businessTypeCode 业务类型码（Business type code）。
     * @param businessCategory 业务分类（Business category）。
     * @param businessName     业务名称（Business name）。
     * @param description      业务说明（Description, nullable）。
     * @param financial        是否金融类（Is financial）。
     * @param reversible       是否可冲正（Is reversible）。
     * @param status           类型状态（Type status）。
     * @return 重建后的业务类型实体（Reconstructed business type entity）。
     */
    public static BusinessType restore(
            final BusinessTypeCode businessTypeCode,
            final BusinessCategory businessCategory,
            final String businessName,
            final String description,
            final boolean financial,
            final boolean reversible,
            final BusinessTypeStatus status
    ) {
        return new BusinessType(
                businessTypeCode,
                businessCategory,
                businessName,
                description,
                financial,
                reversible,
                status);
    }

    /**
     * @brief 激活业务类型（Activate Business Type）；
     *        Activate business type.
     */
    public void activate() {
        this.status = BusinessTypeStatus.ACTIVE;
    }

    /**
     * @brief 停用业务类型（Deactivate Business Type）；
     *        Deactivate business type.
     */
    public void deactivate() {
        this.status = BusinessTypeStatus.INACTIVE;
    }

    /**
     * @brief 判断业务类型是否可发起交易（Check Start Eligibility）；
     *        Check whether this type can start new transactions.
     *
     * @return 可发起返回 true（true when startable）。
     */
    public boolean isStartable() {
        return status.isActive();
    }

    /**
     * @brief 返回业务类型码（Return Business Type Code）；
     *        Return business type code.
     *
     * @return 业务类型码（Business type code）。
     */
    public BusinessTypeCode businessTypeCode() {
        return businessTypeCode;
    }

    /**
     * @brief 返回业务分类（Return Business Category）；
     *        Return business category.
     *
     * @return 业务分类（Business category）。
     */
    public BusinessCategory businessCategory() {
        return businessCategory;
    }

    /**
     * @brief 返回业务名称（Return Business Name）；
     *        Return business name.
     *
     * @return 业务名称（Business name）。
     */
    public String businessName() {
        return businessName;
    }

    /**
     * @brief 返回业务说明（可空）（Return Optional Description）；
     *        Return business description, nullable.
     *
     * @return 业务说明或 null（Description or null）。
     */
    public String descriptionOrNull() {
        return description;
    }

    /**
     * @brief 返回是否金融类（Return Financial Flag）；
     *        Return whether this is financial type.
     *
     * @return 金融类返回 true（true when financial）。
     */
    public boolean isFinancial() {
        return financial;
    }

    /**
     * @brief 返回是否可冲正（Return Reversible Flag）；
     *        Return whether this type is reversible.
     *
     * @return 可冲正返回 true（true when reversible）。
     */
    public boolean isReversible() {
        return reversible;
    }

    /**
     * @brief 返回业务类型状态（Return Business Type Status）；
     *        Return business type status.
     *
     * @return 类型状态（Type status）。
     */
    public BusinessTypeStatus status() {
        return status;
    }

    /**
     * @brief 标准化业务名称（Normalize Business Name）；
     *        Normalize and validate business name.
     *
     * @param rawName 原始业务名称（Raw business name）。
     * @return 标准化业务名称（Normalized business name）。
     */
    private static String normalizeName(final String rawName) {
        if (rawName == null) {
            throw new IllegalArgumentException("Business name must not be null");
        }
        final String normalized = rawName.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Business name must not be blank");
        }
        if (normalized.length() > 128) {
            throw new IllegalArgumentException("Business name length must be <= 128");
        }
        return normalized;
    }

    /**
     * @brief 标准化可空文本字段（Normalize Nullable Text）；
     *        Normalize nullable text field by trimming and collapsing blank to null.
     *
     * @param rawText 原始文本（Raw text）。
     * @return 标准化文本或 null（Normalized text or null）。
     */
    private static String normalizeNullableText(final String rawText) {
        if (rawText == null) {
            return null;
        }
        final String normalized = rawText.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
