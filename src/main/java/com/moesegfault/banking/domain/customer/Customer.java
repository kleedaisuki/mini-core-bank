package com.moesegfault.banking.domain.customer;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 客户实体（Customer Entity），对应 `customer` 表并维护核心不变量（Invariant）；
 *        Customer entity mapped to `customer` table and enforcing core invariants.
 */
public final class Customer {

    /**
     * @brief 客户 ID（Customer Identifier）；
     *        Customer identifier.
     */
    private final CustomerId customerId;

    /**
     * @brief 身份证件（Identity Document）；
     *        Identity document.
     */
    private final IdentityDocument identityDocument;

    /**
     * @brief 手机号（Mobile Phone Number）；
     *        Mobile phone number.
     */
    private PhoneNumber mobilePhone;

    /**
     * @brief 居住地址（Residential Address）；
     *        Residential address.
     */
    private Address residentialAddress;

    /**
     * @brief 通信地址（Mailing Address）；
     *        Mailing address.
     */
    private Address mailingAddress;

    /**
     * @brief 税务资料（Tax Profile）；
     *        Tax profile.
     */
    private TaxProfile taxProfile;

    /**
     * @brief 客户状态（Customer Status）；
     *        Customer status.
     */
    private CustomerStatus customerStatus;

    /**
     * @brief 创建时间（Creation Timestamp）；
     *        Entity creation timestamp.
     */
    private final Instant createdAt;

    /**
     * @brief 更新时间（Last Updated Timestamp）；
     *        Last updated timestamp.
     */
    private Instant updatedAt;

    /**
     * @brief 构造客户实体（Construct Customer Entity）；
     *        Construct customer entity.
     *
     * @param customerId         客户 ID（Customer ID）。
     * @param identityDocument   身份证件（Identity document）。
     * @param mobilePhone        手机号（Mobile phone）。
     * @param residentialAddress 居住地址（Residential address）。
     * @param mailingAddress     通信地址（Mailing address）。
     * @param taxProfile         税务资料（Tax profile）。
     * @param customerStatus     客户状态（Customer status）。
     * @param createdAt          创建时间（Creation timestamp）。
     * @param updatedAt          更新时间（Updated timestamp）。
     */
    private Customer(
            final CustomerId customerId,
            final IdentityDocument identityDocument,
            final PhoneNumber mobilePhone,
            final Address residentialAddress,
            final Address mailingAddress,
            final TaxProfile taxProfile,
            final CustomerStatus customerStatus,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.customerId = Objects.requireNonNull(customerId, "Customer ID must not be null");
        this.identityDocument = Objects.requireNonNull(identityDocument, "Identity document must not be null");
        this.mobilePhone = Objects.requireNonNull(mobilePhone, "Mobile phone must not be null");
        this.residentialAddress = Objects.requireNonNull(
                residentialAddress,
                "Residential address must not be null");
        this.mailingAddress = Objects.requireNonNull(mailingAddress, "Mailing address must not be null");
        this.taxProfile = Objects.requireNonNull(taxProfile, "Tax profile must not be null");
        this.customerStatus = Objects.requireNonNull(customerStatus, "Customer status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created-at must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated-at must not be null");
        if (updatedAt.isBefore(createdAt)) {
            throw new BusinessRuleViolation("Customer updated-at must not be before created-at");
        }
    }

    /**
     * @brief 注册新客户（Register New Customer）；
     *        Register a new customer.
     *
     * @param customerId         客户 ID（Customer ID）。
     * @param identityDocument   身份证件（Identity document）。
     * @param mobilePhone        手机号（Mobile phone）。
     * @param residentialAddress 居住地址（Residential address）。
     * @param mailingAddress     通信地址（Mailing address）。
     * @param taxProfile         税务资料（Tax profile）。
     * @return 新客户实体（New customer entity）。
     */
    public static Customer register(
            final CustomerId customerId,
            final IdentityDocument identityDocument,
            final PhoneNumber mobilePhone,
            final Address residentialAddress,
            final Address mailingAddress,
            final TaxProfile taxProfile
    ) {
        final Instant now = Instant.now();
        return new Customer(
                customerId,
                identityDocument,
                mobilePhone,
                residentialAddress,
                mailingAddress,
                taxProfile,
                CustomerStatus.ACTIVE,
                now,
                now);
    }

    /**
     * @brief 从持久化数据重建客户（Reconstruct from Persistence）；
     *        Reconstruct customer from persistence state.
     *
     * @param customerId         客户 ID（Customer ID）。
     * @param identityDocument   身份证件（Identity document）。
     * @param mobilePhone        手机号（Mobile phone）。
     * @param residentialAddress 居住地址（Residential address）。
     * @param mailingAddress     通信地址（Mailing address）。
     * @param taxProfile         税务资料（Tax profile）。
     * @param customerStatus     客户状态（Customer status）。
     * @param createdAt          创建时间（Creation timestamp）。
     * @param updatedAt          更新时间（Updated timestamp）。
     * @return 重建后的客户实体（Reconstructed customer entity）。
     */
    public static Customer restore(
            final CustomerId customerId,
            final IdentityDocument identityDocument,
            final PhoneNumber mobilePhone,
            final Address residentialAddress,
            final Address mailingAddress,
            final TaxProfile taxProfile,
            final CustomerStatus customerStatus,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new Customer(
                customerId,
                identityDocument,
                mobilePhone,
                residentialAddress,
                mailingAddress,
                taxProfile,
                customerStatus,
                createdAt,
                updatedAt);
    }

    /**
     * @brief 更新手机号（Update Mobile Phone）；
     *        Update mobile phone.
     *
     * @param newPhone 新手机号（New mobile phone）。
     */
    public void updateMobilePhone(final PhoneNumber newPhone) {
        ensureNotClosed("update mobile phone");
        final PhoneNumber normalized = Objects.requireNonNull(newPhone, "New phone must not be null");
        if (!normalized.equals(this.mobilePhone)) {
            this.mobilePhone = normalized;
            touch();
        }
    }

    /**
     * @brief 更新地址（Update Addresses）；
     *        Update residential and mailing addresses.
     *
     * @param newResidentialAddress 新居住地址（New residential address）。
     * @param newMailingAddress     新通信地址（New mailing address）。
     */
    public void updateAddresses(
            final Address newResidentialAddress,
            final Address newMailingAddress
    ) {
        ensureNotClosed("update addresses");
        final Address normalizedResidential = Objects.requireNonNull(
                newResidentialAddress,
                "New residential address must not be null");
        final Address normalizedMailing = Objects.requireNonNull(
                newMailingAddress,
                "New mailing address must not be null");
        if (!normalizedResidential.equals(this.residentialAddress)
                || !normalizedMailing.equals(this.mailingAddress)) {
            this.residentialAddress = normalizedResidential;
            this.mailingAddress = normalizedMailing;
            touch();
        }
    }

    /**
     * @brief 更新税务资料（Update Tax Profile）；
     *        Update tax profile.
     *
     * @param newTaxProfile 新税务资料（New tax profile）。
     */
    public void updateTaxProfile(final TaxProfile newTaxProfile) {
        ensureNotClosed("update tax profile");
        final TaxProfile normalized = Objects.requireNonNull(newTaxProfile, "New tax profile must not be null");
        if (!normalized.equals(this.taxProfile)) {
            this.taxProfile = normalized;
            touch();
        }
    }

    /**
     * @brief 冻结客户（Freeze Customer）；
     *        Freeze customer.
     */
    public void freeze() {
        if (customerStatus == CustomerStatus.FROZEN) {
            return;
        }
        if (customerStatus != CustomerStatus.ACTIVE) {
            throw new BusinessRuleViolation(
                    "Only ACTIVE customer can be frozen, current status: " + customerStatus);
        }
        customerStatus = CustomerStatus.FROZEN;
        touch();
    }

    /**
     * @brief 解冻客户（Activate Frozen Customer）；
     *        Activate a frozen customer.
     */
    public void activate() {
        if (customerStatus == CustomerStatus.ACTIVE) {
            return;
        }
        if (customerStatus != CustomerStatus.FROZEN) {
            throw new BusinessRuleViolation(
                    "Only FROZEN customer can be activated, current status: " + customerStatus);
        }
        customerStatus = CustomerStatus.ACTIVE;
        touch();
    }

    /**
     * @brief 关闭客户（Close Customer）；
     *        Close customer.
     */
    public void close() {
        if (customerStatus == CustomerStatus.CLOSED) {
            return;
        }
        customerStatus = CustomerStatus.CLOSED;
        touch();
    }

    /**
     * @brief 构造“客户已注册”事件（Build Customer Registered Event）；
     *        Build customer-registered domain event.
     *
     * @return 客户注册事件（Customer registered event）。
     */
    public CustomerRegistered registeredEvent() {
        return new CustomerRegistered(customerId, identityDocument, createdAt);
    }

    /**
     * @brief 返回客户 ID（Return Customer ID）；
     *        Return customer ID.
     *
     * @return 客户 ID（Customer ID）。
     */
    public CustomerId customerId() {
        return customerId;
    }

    /**
     * @brief 返回身份证件（Return Identity Document）；
     *        Return identity document.
     *
     * @return 身份证件（Identity document）。
     */
    public IdentityDocument identityDocument() {
        return identityDocument;
    }

    /**
     * @brief 返回手机号（Return Mobile Phone）；
     *        Return mobile phone.
     *
     * @return 手机号（Mobile phone）。
     */
    public PhoneNumber mobilePhone() {
        return mobilePhone;
    }

    /**
     * @brief 返回居住地址（Return Residential Address）；
     *        Return residential address.
     *
     * @return 居住地址（Residential address）。
     */
    public Address residentialAddress() {
        return residentialAddress;
    }

    /**
     * @brief 返回通信地址（Return Mailing Address）；
     *        Return mailing address.
     *
     * @return 通信地址（Mailing address）。
     */
    public Address mailingAddress() {
        return mailingAddress;
    }

    /**
     * @brief 返回税务资料（Return Tax Profile）；
     *        Return tax profile.
     *
     * @return 税务资料（Tax profile）。
     */
    public TaxProfile taxProfile() {
        return taxProfile;
    }

    /**
     * @brief 返回客户状态（Return Customer Status）；
     *        Return customer status.
     *
     * @return 客户状态（Customer status）。
     */
    public CustomerStatus customerStatus() {
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

    /**
     * @brief 更新时间戳（Touch Updated Timestamp）；
     *        Update last-updated timestamp to current instant.
     */
    private void touch() {
        this.updatedAt = Instant.now();
    }

    /**
     * @brief 断言客户未关闭（Ensure Customer Not Closed）；
     *        Ensure customer is not closed.
     *
     * @param operation 操作名称（Operation name）。
     */
    private void ensureNotClosed(final String operation) {
        if (customerStatus == CustomerStatus.CLOSED) {
            throw new BusinessRuleViolation("Cannot " + operation + " for CLOSED customer");
        }
    }
}
