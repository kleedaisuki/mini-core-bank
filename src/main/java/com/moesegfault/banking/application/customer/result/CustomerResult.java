package com.moesegfault.banking.application.customer.result;

import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.IdentityDocument;
import com.moesegfault.banking.domain.customer.TaxProfile;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 客户查询结果（Customer Result），作为 application 层对外只读视图；
 *        Customer query result as read-only outward view of the application layer.
 */
public final class CustomerResult {

    /**
     * @brief 客户 ID（Customer Identifier）；
     *        Customer identifier.
     */
    private final String customerId;

    /**
     * @brief 证件类型（Identity Document Type）；
     *        Identity document type.
     */
    private final String idType;

    /**
     * @brief 证件号码（Identity Document Number）；
     *        Identity document number.
     */
    private final String idNumber;

    /**
     * @brief 签发地区（Issuing Region）；
     *        Issuing region.
     */
    private final String issuingRegion;

    /**
     * @brief 手机号（Mobile Phone）；
     *        Mobile phone number.
     */
    private final String mobilePhone;

    /**
     * @brief 居住地址（Residential Address）；
     *        Residential address.
     */
    private final String residentialAddress;

    /**
     * @brief 通信地址（Mailing Address）；
     *        Mailing address.
     */
    private final String mailingAddress;

    /**
     * @brief 是否美国税务居民（US Tax Resident Flag）；
     *        Whether customer is a US tax resident.
     */
    private final boolean usTaxResident;

    /**
     * @brief CRS 信息（CRS Information）；
     *        CRS information text, nullable.
     */
    private final String crsInfo;

    /**
     * @brief 客户状态（Customer Status）；
     *        Customer status.
     */
    private final String customerStatus;

    /**
     * @brief 创建时间（Creation Timestamp）；
     *        Creation timestamp.
     */
    private final Instant createdAt;

    /**
     * @brief 更新时间（Last Updated Timestamp）；
     *        Last updated timestamp.
     */
    private final Instant updatedAt;

    /**
     * @brief 构造客户查询结果（Construct Customer Result）；
     *        Construct customer result.
     *
     * @param customerId         客户 ID（Customer ID）。
     * @param idType             证件类型（Identity document type）。
     * @param idNumber           证件号码（Identity document number）。
     * @param issuingRegion      签发地区（Issuing region）。
     * @param mobilePhone        手机号（Mobile phone）。
     * @param residentialAddress 居住地址（Residential address）。
     * @param mailingAddress     通信地址（Mailing address）。
     * @param usTaxResident      是否美国税务居民（US tax resident flag）。
     * @param crsInfo            CRS 信息（CRS info, nullable）。
     * @param customerStatus     客户状态（Customer status）。
     * @param createdAt          创建时间（Creation timestamp）。
     * @param updatedAt          更新时间（Updated timestamp）。
     */
    public CustomerResult(
            final String customerId,
            final String idType,
            final String idNumber,
            final String issuingRegion,
            final String mobilePhone,
            final String residentialAddress,
            final String mailingAddress,
            final boolean usTaxResident,
            final String crsInfo,
            final String customerStatus,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.customerId = Objects.requireNonNull(customerId, "Customer ID must not be null");
        this.idType = Objects.requireNonNull(idType, "Identity document type must not be null");
        this.idNumber = Objects.requireNonNull(idNumber, "Identity document number must not be null");
        this.issuingRegion = Objects.requireNonNull(issuingRegion, "Issuing region must not be null");
        this.mobilePhone = Objects.requireNonNull(mobilePhone, "Mobile phone must not be null");
        this.residentialAddress = Objects.requireNonNull(
                residentialAddress,
                "Residential address must not be null");
        this.mailingAddress = Objects.requireNonNull(mailingAddress, "Mailing address must not be null");
        this.usTaxResident = usTaxResident;
        this.crsInfo = crsInfo;
        this.customerStatus = Objects.requireNonNull(customerStatus, "Customer status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created-at must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated-at must not be null");
    }

    /**
     * @brief 从领域实体映射结果对象（Map from Domain Customer）；
     *        Map from domain customer entity to customer result.
     *
     * @param customer 客户实体（Customer entity）。
     * @return 客户结果对象（Customer result object）。
     */
    public static CustomerResult fromDomain(final Customer customer) {
        final Customer normalized = Objects.requireNonNull(customer, "Customer must not be null");
        final IdentityDocument identityDocument = normalized.identityDocument();
        final TaxProfile taxProfile = normalized.taxProfile();
        return new CustomerResult(
                normalized.customerId().value(),
                identityDocument.idType().name(),
                identityDocument.idNumber(),
                identityDocument.issuingRegion(),
                normalized.mobilePhone().value(),
                normalized.residentialAddress().value(),
                normalized.mailingAddress().value(),
                taxProfile.isUsTaxResident(),
                taxProfile.crsInfoOrNull(),
                normalized.customerStatus().name(),
                normalized.createdAt(),
                normalized.updatedAt());
    }

    /**
     * @brief 返回客户 ID（Return Customer ID）；
     *        Return customer ID.
     *
     * @return 客户 ID（Customer ID）。
     */
    public String customerId() {
        return customerId;
    }

    /**
     * @brief 返回证件类型（Return Identity Document Type）；
     *        Return identity document type.
     *
     * @return 证件类型（Identity document type）。
     */
    public String idType() {
        return idType;
    }

    /**
     * @brief 返回证件号码（Return Identity Document Number）；
     *        Return identity document number.
     *
     * @return 证件号码（Identity document number）。
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
     * @brief 返回手机号（Return Mobile Phone）；
     *        Return mobile phone.
     *
     * @return 手机号（Mobile phone）。
     */
    public String mobilePhone() {
        return mobilePhone;
    }

    /**
     * @brief 返回居住地址（Return Residential Address）；
     *        Return residential address.
     *
     * @return 居住地址（Residential address）。
     */
    public String residentialAddress() {
        return residentialAddress;
    }

    /**
     * @brief 返回通信地址（Return Mailing Address）；
     *        Return mailing address.
     *
     * @return 通信地址（Mailing address）。
     */
    public String mailingAddress() {
        return mailingAddress;
    }

    /**
     * @brief 返回美国税务居民标记（Return US Tax Resident Flag）；
     *        Return US tax resident flag.
     *
     * @return 美国税务居民返回 true（true when US tax resident）。
     */
    public boolean usTaxResident() {
        return usTaxResident;
    }

    /**
     * @brief 返回 CRS 信息（Return CRS Information）；
     *        Return CRS information.
     *
     * @return CRS 信息（CRS info, nullable）。
     */
    public String crsInfo() {
        return crsInfo;
    }

    /**
     * @brief 返回客户状态（Return Customer Status）；
     *        Return customer status.
     *
     * @return 客户状态（Customer status）。
     */
    public String customerStatus() {
        return customerStatus;
    }

    /**
     * @brief 返回创建时间（Return Creation Timestamp）；
     *        Return creation timestamp.
     *
     * @return 创建时间（Creation timestamp）。
     */
    public Instant createdAt() {
        return createdAt;
    }

    /**
     * @brief 返回更新时间（Return Updated Timestamp）；
     *        Return updated timestamp.
     *
     * @return 更新时间（Updated timestamp）。
     */
    public Instant updatedAt() {
        return updatedAt;
    }
}
