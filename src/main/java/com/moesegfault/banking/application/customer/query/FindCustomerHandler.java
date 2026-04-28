package com.moesegfault.banking.application.customer.query;

import com.moesegfault.banking.application.customer.result.CustomerResult;
import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 单客户查询处理器（Find Customer Handler），按 customer_id 查询并映射应用结果；
 *        Find-customer handler querying by customer_id and mapping to application result.
 */
public final class FindCustomerHandler {

    /**
     * @brief 客户仓储（Customer Repository）；
     *        Customer repository.
     */
    private final CustomerRepository customerRepository;

    /**
     * @brief 构造查询处理器（Construct Find Customer Handler）；
     *        Construct find-customer handler.
     *
     * @param customerRepository 客户仓储（Customer repository）。
     */
    public FindCustomerHandler(final CustomerRepository customerRepository) {
        this.customerRepository = Objects.requireNonNull(customerRepository, "Customer repository must not be null");
    }

    /**
     * @brief 执行客户查询（Handle Find Customer Query）；
     *        Handle find-customer query.
     *
     * @param query 查询请求（Find-customer query）。
     * @return 客户结果可选值（Optional customer result）。
     */
    public Optional<CustomerResult> handle(final FindCustomerQuery query) {
        final FindCustomerQuery normalized = Objects.requireNonNull(query, "Query must not be null");
        return customerRepository.findById(CustomerId.of(normalized.customerId())).map(CustomerResult::fromDomain);
    }
}
