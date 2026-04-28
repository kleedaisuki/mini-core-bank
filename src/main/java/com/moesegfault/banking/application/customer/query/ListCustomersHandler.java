package com.moesegfault.banking.application.customer.query;

import com.moesegfault.banking.application.customer.result.CustomerResult;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.customer.PhoneNumber;
import java.util.List;
import java.util.Objects;

/**
 * @brief 客户列表查询处理器（List Customers Handler），支持全量与手机号过滤查询；
 *        List-customers handler supporting all-customers and mobile-phone filtered queries.
 */
public final class ListCustomersHandler {

    /**
     * @brief 客户仓储（Customer Repository）；
     *        Customer repository.
     */
    private final CustomerRepository customerRepository;

    /**
     * @brief 构造列表查询处理器（Construct List Customers Handler）；
     *        Construct list-customers handler.
     *
     * @param customerRepository 客户仓储（Customer repository）。
     */
    public ListCustomersHandler(final CustomerRepository customerRepository) {
        this.customerRepository = Objects.requireNonNull(customerRepository, "Customer repository must not be null");
    }

    /**
     * @brief 执行客户列表查询（Handle List Customers Query）；
     *        Handle list-customers query.
     *
     * @param query 列表查询请求（List-customers query）。
     * @return 客户结果列表（Customer result list）。
     */
    public List<CustomerResult> handle(final ListCustomersQuery query) {
        final ListCustomersQuery normalized = Objects.requireNonNull(query, "Query must not be null");
        final List<Customer> customers = normalized.hasMobilePhoneFilter()
                ? customerRepository.findByMobilePhone(PhoneNumber.of(normalized.mobilePhone()))
                : customerRepository.findAll();
        return customers.stream().map(CustomerResult::fromDomain).toList();
    }
}
