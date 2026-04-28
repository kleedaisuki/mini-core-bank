package com.moesegfault.banking.application.account;

import com.moesegfault.banking.domain.account.AccountNumber;
import com.moesegfault.banking.domain.account.AccountPolicy;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.account.SavingsAccount;
import com.moesegfault.banking.domain.account.SavingsAccountId;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.util.Objects;

/**
 * @brief 账户应用层支撑工具（Account Application Support Utility），统一跨聚合读取与校验；
 *        Account application support utility centralizing cross-aggregate loading and validation.
 */
public final class AccountApplicationSupport {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private AccountApplicationSupport() {
    }

    /**
     * @brief 读取客户并在不存在时抛异常（Load Customer or Throw）；
     *        Load customer by ID and throw when absent.
     *
     * @param customerRepository 客户仓储接口（Customer repository port）。
     * @param customerIdRaw      原始客户 ID（Raw customer ID）。
     * @return 客户实体（Customer entity）。
     */
    public static Customer loadCustomerOrThrow(final CustomerRepository customerRepository, final String customerIdRaw) {
        final CustomerRepository repository = Objects.requireNonNull(
                customerRepository,
                "customerRepository must not be null");
        final String normalizedCustomerId = normalizeNonBlank(customerIdRaw, "customerId");
        return repository.findById(com.moesegfault.banking.domain.customer.CustomerId.of(normalizedCustomerId))
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + normalizedCustomerId));
    }

    /**
     * @brief 读取储蓄账户并在不存在时抛异常（Load Savings Account or Throw）；
     *        Load savings account by ID and throw when absent.
     *
     * @param accountRepository   账户仓储接口（Account repository port）。
     * @param savingsAccountIdRaw 原始储蓄账户 ID（Raw savings-account ID）。
     * @return 储蓄账户实体（Savings-account entity）。
     */
    public static SavingsAccount loadSavingsAccountOrThrow(
            final AccountRepository accountRepository,
            final String savingsAccountIdRaw
    ) {
        final AccountRepository repository = Objects.requireNonNull(accountRepository, "accountRepository must not be null");
        final String normalizedSavingsId = normalizeNonBlank(savingsAccountIdRaw, "linkedSavingsAccountId");
        return repository.findSavingsAccountById(SavingsAccountId.of(normalizedSavingsId))
                .orElseThrow(() -> new AccountNotFoundException(
                        "Linked savings account not found: " + normalizedSavingsId));
    }

    /**
     * @brief 校验客户开户资格（Ensure Customer Eligibility for Account Opening）；
     *        Ensure customer status allows account opening.
     *
     * @param customer 客户实体（Customer entity）。
     */
    public static void ensureEligibleCustomer(final Customer customer) {
        final Customer normalized = Objects.requireNonNull(customer, "customer must not be null");
        final com.moesegfault.banking.domain.account.CustomerStatus accountCustomerStatus =
                com.moesegfault.banking.domain.account.CustomerStatus.valueOf(normalized.customerStatus().name());
        AccountPolicy.ensureEligibleCustomer(accountCustomerStatus);
    }

    /**
     * @brief 校验账户号唯一性（Ensure Account Number Uniqueness）；
     *        Ensure account number is globally unique in account schema.
     *
     * @param accountRepository 账户仓储接口（Account repository port）。
     * @param accountNo         账户号值对象（Account-number value object）。
     */
    public static void ensureUniqueAccountNumber(
            final AccountRepository accountRepository,
            final AccountNumber accountNo
    ) {
        final AccountRepository repository = Objects.requireNonNull(accountRepository, "accountRepository must not be null");
        final AccountNumber normalizedAccountNo = Objects.requireNonNull(accountNo, "accountNo must not be null");
        if (repository.existsByAccountNumber(normalizedAccountNo)) {
            throw new BusinessRuleViolation("Account number already exists: " + normalizedAccountNo.value());
        }
    }

    /**
     * @brief 断言字符串非空白（Ensure String Is Not Blank）；
     *        Ensure string value is non-null and non-blank.
     *
     * @param rawValue  原始值（Raw value）。
     * @param fieldName 字段名称（Field name）。
     * @return 标准化字符串（Normalized string）。
     */
    public static String normalizeNonBlank(final String rawValue, final String fieldName) {
        final String normalized = Objects.requireNonNull(rawValue, fieldName + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
