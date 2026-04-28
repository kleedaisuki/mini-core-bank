package com.moesegfault.banking.domain.customer;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 税务资料值对象（Tax Profile Value Object），映射 `is_us_tax_resident/crs_info`；
 *        Tax profile value object mapped to `is_us_tax_resident/crs_info`.
 */
public final class TaxProfile implements Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief 是否美国税务居民（US Tax Resident Flag）；
     *        Whether customer is a US tax resident.
     */
    private final boolean usTaxResident;

    /**
     * @brief CRS 信息（CRS Information）；
     *        Optional CRS information.
     */
    private final CrsInfo crsInfo;

    /**
     * @brief 构造税务资料（Construct Tax Profile）；
     *        Construct tax profile.
     *
     * @param usTaxResident 是否美国税务居民（US tax resident flag）。
     * @param crsInfo       CRS 信息（CRS information, nullable）。
     */
    private TaxProfile(final boolean usTaxResident, final CrsInfo crsInfo) {
        this.usTaxResident = usTaxResident;
        this.crsInfo = crsInfo;
    }

    /**
     * @brief 创建税务资料（Factory Method）；
     *        Create tax profile.
     *
     * @param usTaxResident 是否美国税务居民（US tax resident flag）。
     * @param crsInfo       CRS 信息（CRS information, nullable）。
     * @return 税务资料值对象（Tax profile value object）。
     */
    public static TaxProfile of(final boolean usTaxResident, final CrsInfo crsInfo) {
        return new TaxProfile(usTaxResident, crsInfo);
    }

    /**
     * @brief 以原始字符串创建税务资料（Factory from Raw CRS String）；
     *        Create tax profile from raw CRS string.
     *
     * @param usTaxResident 是否美国税务居民（US tax resident flag）。
     * @param rawCrsInfo    原始 CRS 文本（Raw CRS text, nullable）。
     * @return 税务资料值对象（Tax profile value object）。
     */
    public static TaxProfile of(final boolean usTaxResident, final String rawCrsInfo) {
        final CrsInfo parsed = rawCrsInfo == null ? null : CrsInfo.of(rawCrsInfo);
        return new TaxProfile(usTaxResident, parsed);
    }

    /**
     * @brief 是否美国税务居民（Check US Tax Residency）；
     *        Check whether customer is a US tax resident.
     *
     * @return 美国税务居民返回 true（true if US tax resident）。
     */
    public boolean isUsTaxResident() {
        return usTaxResident;
    }

    /**
     * @brief 返回 CRS 信息（Return CRS Information）；
     *        Return optional CRS information.
     *
     * @return CRS 信息可选值（Optional CRS information）。
     */
    public Optional<CrsInfo> crsInfo() {
        return Optional.ofNullable(crsInfo);
    }

    /**
     * @brief 返回 CRS 文本或 null（Return CRS Text or Null）；
     *        Return CRS text or null when absent.
     *
     * @return CRS 文本或 null（CRS text or null）。
     */
    public String crsInfoOrNull() {
        return crsInfo == null ? null : crsInfo.value();
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
        if (!(other instanceof TaxProfile that)) {
            return false;
        }
        return usTaxResident == that.usTaxResident && Objects.equals(crsInfo, that.crsInfo);
    }

    /**
     * @brief 计算哈希值（Compute Hash Code）；
     *        Compute hash code.
     *
     * @return 哈希值（Hash code）。
     */
    @Override
    public int hashCode() {
        return Objects.hash(usTaxResident, crsInfo);
    }

    /**
     * @brief 返回字符串表示（String Representation）；
     *        Return string representation.
     *
     * @return 税务资料字符串（Tax profile string）。
     */
    @Override
    public String toString() {
        return "TaxProfile{usTaxResident=" + usTaxResident
                + ", crsInfo=" + crsInfoOrNull()
                + '}';
    }
}
