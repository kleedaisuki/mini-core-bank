package com.moesegfault.banking.domain.customer;

import java.util.List;
import java.util.Optional;

/**
 * @brief 客户仓储接口（Customer Repository Interface），定义客户聚合（Aggregate）持久化契约；
 *        Customer repository interface defining persistence contract for customer aggregate.
 */
public interface CustomerRepository {

    /**
     * @brief 保存客户实体（Save Customer Entity）；
     *        Save customer entity.
     *
     * @param customer 客户实体（Customer entity）。
     */
    void save(Customer customer);

    /**
     * @brief 按客户 ID 查询（Find by Customer ID）；
     *        Find customer by customer ID.
     *
     * @param customerId 客户 ID（Customer ID）。
     * @return 客户可选值（Optional customer）。
     */
    Optional<Customer> findById(CustomerId customerId);

    /**
     * @brief 按证件查询（Find by Identity Document）；
     *        Find customer by identity document.
     *
     * @param identityDocument 身份证件（Identity document）。
     * @return 客户可选值（Optional customer）。
     */
    Optional<Customer> findByIdentityDocument(IdentityDocument identityDocument);

    /**
     * @brief 判断证件是否已注册（Check Identity Registration Existence）；
     *        Check whether identity document is already registered.
     *
     * @param identityDocument 身份证件（Identity document）。
     * @return 已存在返回 true（true when exists）。
     */
    boolean existsByIdentityDocument(IdentityDocument identityDocument);

    /**
     * @brief 按手机号查询客户列表（Find Customers by Mobile Phone）；
     *        Find customers by mobile phone.
     *
     * @param mobilePhone 手机号（Mobile phone）。
     * @return 客户列表（Customer list）。
     */
    List<Customer> findByMobilePhone(PhoneNumber mobilePhone);

    /**
     * @brief 查询全部客户（Find All Customers）；
     *        Find all customers.
     *
     * @return 全部客户列表（All customers）。
     */
    List<Customer> findAll();
}
