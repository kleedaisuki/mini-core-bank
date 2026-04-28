package com.moesegfault.banking.domain.account;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.util.Objects;

/**
 * @brief 账户领域策略（Account Domain Policy），集中定义开户和账户操作的跨对象规则；
 *        Account domain policy centralizing cross-object rules for opening and operating accounts.
 */
public final class AccountPolicy {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private AccountPolicy() {
        // utility class
    }

    /**
     * @brief 校验客户是否具备开户资格（Ensure Customer Eligibility for Account Opening）；
     *        Ensure customer status is eligible for account opening.
     *
     * @param customerStatus 客户状态（Customer status）。
     */
    public static void ensureEligibleCustomer(final CustomerStatus customerStatus) {
        final CustomerStatus normalized = Objects.requireNonNull(
                customerStatus,
                "Customer status must not be null");
        if (!normalized.canOpenAccount()) {
            throw new BusinessRuleViolation(
                    "Customer is not eligible for account opening when status is " + normalized);
        }
    }

    /**
     * @brief 校验账户是否可进行指定操作（Ensure Account Operable for Operation）；
     *        Ensure account status allows a specific operation.
     *
     * @param account   账户实体（Account entity）。
     * @param operation 操作名称（Operation name）。
     */
    public static void ensureOperable(final Account account, final String operation) {
        final Account normalized = Objects.requireNonNull(account, "Account must not be null");
        if (!normalized.accountStatus().isOperable()) {
            throw new BusinessRuleViolation(
                    "Account " + normalized.accountId() + " cannot " + operation
                            + " when status is " + normalized.accountStatus());
        }
    }

    /**
     * @brief 校验外汇账户与绑定储蓄账户归属一致（Ensure FX-Savings Ownership Consistency）；
     *        Ensure FX account and linked savings account belong to the same customer.
     *
     * @param fxAccount      外汇账户（FX account）。
     * @param savingsAccount 储蓄账户（Savings account）。
     */
    public static void ensureFxLinkedSavingsOwnership(
            final FxAccount fxAccount,
            final SavingsAccount savingsAccount
    ) {
        final FxAccount normalizedFx = Objects.requireNonNull(fxAccount, "FX account must not be null");
        final SavingsAccount normalizedSavings = Objects.requireNonNull(
                savingsAccount,
                "Savings account must not be null");
        if (!normalizedFx.account().customerId().sameValueAs(normalizedSavings.account().customerId())) {
            throw new BusinessRuleViolation(
                    "FX account and linked savings account must belong to the same customer");
        }
        if (!normalizedFx.linkedSavingsAccountId().sameValueAs(normalizedSavings.savingsAccountId())) {
            throw new BusinessRuleViolation(
                    "FX account linked_savings_account_id must match the target savings account_id");
        }
    }

    /**
     * @brief 校验投资账户数量上限（Ensure Investment Account Count Limit）；
     *        Ensure investment-account count does not exceed policy limit.
     *
     * @param existingCount 当前客户已存在投资账户数量（Existing investment-account count）。
     */
    public static void ensureSingleInvestmentAccountPerCustomer(final long existingCount) {
        if (existingCount < 0) {
            throw new IllegalArgumentException("Existing investment account count must not be negative");
        }
        if (existingCount > 0) {
            throw new BusinessRuleViolation(
                    "Customer already has an investment account, duplicate opening is not allowed");
        }
    }
}
