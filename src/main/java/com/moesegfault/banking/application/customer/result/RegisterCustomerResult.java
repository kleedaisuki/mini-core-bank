package com.moesegfault.banking.application.customer.result;

import java.time.Instant;
import java.util.Objects;

/**
 * @brief 注册客户结果（Register Customer Result），返回新客户标识与注册状态；
 *        Register-customer result returning new customer identifier and registration status.
 */
public final class RegisterCustomerResult {

    /**
     * @brief 新客户 ID（New Customer Identifier）；
     *        New customer identifier.
     */
    private final String customerId;

    /**
     * @brief 客户状态（Customer Status）；
     *        Customer status after registration.
     */
    private final String customerStatus;

    /**
     * @brief 注册时间（Registered Timestamp）；
     *        Registration timestamp.
     */
    private final Instant registeredAt;

    /**
     * @brief 构造注册结果（Construct Register Customer Result）；
     *        Construct register-customer result.
     *
     * @param customerId     新客户 ID（New customer ID）。
     * @param customerStatus 客户状态（Customer status）。
     * @param registeredAt   注册时间（Registered timestamp）。
     */
    public RegisterCustomerResult(
            final String customerId,
            final String customerStatus,
            final Instant registeredAt
    ) {
        this.customerId = Objects.requireNonNull(customerId, "Customer ID must not be null");
        this.customerStatus = Objects.requireNonNull(customerStatus, "Customer status must not be null");
        this.registeredAt = Objects.requireNonNull(registeredAt, "Registered-at must not be null");
    }

    /**
     * @brief 返回新客户 ID（Return New Customer ID）；
     *        Return new customer identifier.
     *
     * @return 新客户 ID（New customer ID）。
     */
    public String customerId() {
        return customerId;
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
     * @brief 返回注册时间（Return Registered Timestamp）；
     *        Return registration timestamp.
     *
     * @return 注册时间（Registered timestamp）。
     */
    public Instant registeredAt() {
        return registeredAt;
    }
}
