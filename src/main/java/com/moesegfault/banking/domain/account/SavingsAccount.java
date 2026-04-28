package com.moesegfault.banking.domain.account;

import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.util.Objects;

/**
 * @brief 储蓄账户实体（Savings Account Entity），对应 `savings_account` 子类型并复用 `account` 基础字段；
 *        Savings-account entity mapped to `savings_account` subtype and reusing `account` base fields.
 */
public final class SavingsAccount {

    /**
     * @brief 储蓄账户 ID（Savings Account ID）；
     *        Savings-account identifier.
     */
    private final SavingsAccountId savingsAccountId;

    /**
     * @brief 基础账户实体（Base Account Entity）；
     *        Base account entity.
     */
    private final Account account;

    /**
     * @brief 构造储蓄账户实体（Construct Savings Account Entity）；
     *        Construct savings-account entity.
     *
     * @param savingsAccountId 储蓄账户 ID（Savings-account ID）。
     * @param account          基础账户实体（Base account entity）。
     */
    private SavingsAccount(
            final SavingsAccountId savingsAccountId,
            final Account account
    ) {
        this.savingsAccountId = Objects.requireNonNull(savingsAccountId, "Savings account ID must not be null");
        this.account = Objects.requireNonNull(account, "Account must not be null");
        if (this.account.accountType() != AccountType.SAVINGS) {
            throw new BusinessRuleViolation("SavingsAccount must wrap an account with type SAVINGS");
        }
        if (!AccountId.of(this.savingsAccountId.value()).sameValueAs(this.account.accountId())) {
            throw new BusinessRuleViolation("Savings account ID must be equal to base account_id");
        }
    }

    /**
     * @brief 开立储蓄账户（Open Savings Account）；
     *        Open a savings account.
     *
     * @param savingsAccountId 储蓄账户 ID（Savings-account ID）。
     * @param customerId       所属客户 ID（Owner customer ID）。
     * @param accountNo        账户号（Account number）。
     * @return 储蓄账户实体（Savings-account entity）。
     */
    public static SavingsAccount open(
            final SavingsAccountId savingsAccountId,
            final CustomerId customerId,
            final AccountNumber accountNo
    ) {
        final SavingsAccountId id = Objects.requireNonNull(savingsAccountId, "Savings account ID must not be null");
        final Account base = Account.open(
                AccountId.of(id.value()),
                customerId,
                accountNo,
                AccountType.SAVINGS);
        return new SavingsAccount(id, base);
    }

    /**
     * @brief 从持久化状态重建储蓄账户（Restore Savings Account from Persistence）；
     *        Restore savings-account entity from persistence state.
     *
     * @param account 基础账户实体（Base account entity）。
     * @return 储蓄账户实体（Savings-account entity）。
     */
    public static SavingsAccount restore(final Account account) {
        final Account normalized = Objects.requireNonNull(account, "Account must not be null");
        return new SavingsAccount(SavingsAccountId.of(normalized.accountId().value()), normalized);
    }

    /**
     * @brief 生成开户事件（Build Opened Event）；
     *        Build savings-account-opened event.
     *
     * @return 开户事件（Opened event）。
     */
    public SavingsAccountOpened openedEvent() {
        return new SavingsAccountOpened(
                savingsAccountId,
                account.customerId(),
                account.accountNo(),
                account.openedAt());
    }

    /**
     * @brief 返回储蓄账户 ID（Return Savings Account ID）；
     *        Return savings-account identifier.
     *
     * @return 储蓄账户 ID（Savings-account ID）。
     */
    public SavingsAccountId savingsAccountId() {
        return savingsAccountId;
    }

    /**
     * @brief 返回基础账户实体（Return Base Account）；
     *        Return base account entity.
     *
     * @return 基础账户实体（Base account entity）。
     */
    public Account account() {
        return account;
    }
}
