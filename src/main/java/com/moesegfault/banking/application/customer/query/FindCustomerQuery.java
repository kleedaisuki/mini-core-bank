package com.moesegfault.banking.application.customer.query;

import java.util.Objects;

/**
 * @brief 单客户查询请求（Find Customer Query），按 customer_id 查询客户；
 *        Single-customer query request that finds customer by customer_id.
 */
public final class FindCustomerQuery {

    /**
     * @brief 客户 ID（Customer Identifier）；
     *        Customer identifier.
     */
    private final String customerId;

    /**
     * @brief 构造单客户查询请求（Construct Find Customer Query）；
     *        Construct find-customer query.
     *
     * @param customerId 客户 ID（Customer ID）。
     */
    public FindCustomerQuery(final String customerId) {
        this.customerId = Objects.requireNonNull(customerId, "Customer ID must not be null");
    }

    /**
     * @brief 返回客户 ID（Return Customer ID）；
     *        Return customer identifier.
     *
     * @return 客户 ID（Customer ID）。
     */
    public String customerId() {
        return customerId;
    }
}
