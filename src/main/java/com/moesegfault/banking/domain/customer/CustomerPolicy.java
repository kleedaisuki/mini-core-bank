package com.moesegfault.banking.domain.customer;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.util.Objects;

/**
 * @brief 客户领域策略（Customer Domain Policy），集中定义客户可执行业务资格规则；
 *        Customer domain policy centralizing customer eligibility rules.
 */
public final class CustomerPolicy {

    /**
     * @brief 私有构造函数（Private Constructor）；
     *        Private constructor for utility class.
     */
    private CustomerPolicy() {
        // utility class
    }

    /**
     * @brief 校验客户是否可开户（Ensure Account-Opening Eligibility）；
     *        Ensure customer is eligible to open accounts.
     *
     * @param customer 客户实体（Customer entity）。
     */
    public static void ensureEligibleForAccountOpening(final Customer customer) {
        ensureOperableStatus(customer, "open account");
    }

    /**
     * @brief 校验客户是否可持卡（Ensure Card-Issuing Eligibility）；
     *        Ensure customer is eligible for card issuing.
     *
     * @param customer 客户实体（Customer entity）。
     */
    public static void ensureEligibleForCardIssuance(final Customer customer) {
        ensureOperableStatus(customer, "issue card");
    }

    /**
     * @brief 判断客户是否可关闭（Check Closing Eligibility）；
     *        Check whether customer can be closed.
     *
     * @param customer 客户实体（Customer entity）。
     * @return 非终态返回 true（true when status is not terminal）。
     */
    public static boolean canBeClosed(final Customer customer) {
        Objects.requireNonNull(customer, "Customer must not be null");
        return !customer.customerStatus().isTerminal();
    }

    /**
     * @brief 按客户状态校验操作资格（Ensure Operation Eligibility by Status）；
     *        Ensure operation eligibility by customer status.
     *
     * @param customer  客户实体（Customer entity）。
     * @param operation 操作名称（Operation name）。
     */
    private static void ensureOperableStatus(final Customer customer, final String operation) {
        Objects.requireNonNull(customer, "Customer must not be null");
        if (!customer.customerStatus().isOperable()) {
            throw new BusinessRuleViolation(
                    "Customer " + customer.customerId() + " cannot " + operation
                            + " when status is " + customer.customerStatus());
        }
    }
}
