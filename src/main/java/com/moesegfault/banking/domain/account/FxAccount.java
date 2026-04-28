package com.moesegfault.banking.domain.account;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.util.Objects;

/**
 * @brief 外汇账户实体（FX Account Entity），对应 `fx_account` 子类型并维护绑定储蓄账户关系；
 *        FX-account entity mapped to `fx_account` subtype and maintaining linked savings-account relation.
 */
public final class FxAccount {

    /**
     * @brief 外汇账户 ID（FX Account ID）；
     *        FX-account identifier.
     */
    private final FxAccountId fxAccountId;

    /**
     * @brief 基础账户实体（Base Account Entity）；
     *        Base account entity.
     */
    private final Account account;

    /**
     * @brief 绑定储蓄账户 ID（Linked Savings Account ID）；
     *        Linked savings-account identifier.
     */
    private final SavingsAccountId linkedSavingsAccountId;

    /**
     * @brief 构造外汇账户实体（Construct FX Account Entity）；
     *        Construct FX-account entity.
     *
     * @param fxAccountId             外汇账户 ID（FX-account ID）。
     * @param account                 基础账户实体（Base account entity）。
     * @param linkedSavingsAccountId 绑定储蓄账户 ID（Linked savings-account ID）。
     */
    private FxAccount(
            final FxAccountId fxAccountId,
            final Account account,
            final SavingsAccountId linkedSavingsAccountId
    ) {
        this.fxAccountId = Objects.requireNonNull(fxAccountId, "FX account ID must not be null");
        this.account = Objects.requireNonNull(account, "Account must not be null");
        this.linkedSavingsAccountId = Objects.requireNonNull(
                linkedSavingsAccountId,
                "Linked savings account ID must not be null");
        if (this.account.accountType() != AccountType.FX) {
            throw new BusinessRuleViolation("FxAccount must wrap an account with type FX");
        }
        if (!AccountId.of(this.fxAccountId.value()).sameValueAs(this.account.accountId())) {
            throw new BusinessRuleViolation("FX account ID must be equal to base account_id");
        }
        if (this.fxAccountId.value().equals(this.linkedSavingsAccountId.value())) {
            throw new BusinessRuleViolation("FX account cannot link to itself as savings account");
        }
    }

    /**
     * @brief 开立外汇账户（Open FX Account）；
     *        Open an FX account.
     *
     * @param fxAccountId             外汇账户 ID（FX-account ID）。
     * @param customerId              所属客户 ID（Owner customer ID）。
     * @param accountNo               账户号（Account number）。
     * @param linkedSavingsAccountId  绑定储蓄账户 ID（Linked savings-account ID）。
     * @return 外汇账户实体（FX-account entity）。
     */
    public static FxAccount open(
            final FxAccountId fxAccountId,
            final CustomerId customerId,
            final AccountNumber accountNo,
            final SavingsAccountId linkedSavingsAccountId
    ) {
        final FxAccountId id = Objects.requireNonNull(fxAccountId, "FX account ID must not be null");
        final Account base = Account.open(
                AccountId.of(id.value()),
                customerId,
                accountNo,
                AccountType.FX);
        return new FxAccount(id, base, linkedSavingsAccountId);
    }

    /**
     * @brief 从持久化状态重建外汇账户（Restore FX Account from Persistence）；
     *        Restore FX-account entity from persistence state.
     *
     * @param account                基础账户实体（Base account entity）。
     * @param linkedSavingsAccountId 绑定储蓄账户 ID（Linked savings-account ID）。
     * @return 外汇账户实体（FX-account entity）。
     */
    public static FxAccount restore(
            final Account account,
            final SavingsAccountId linkedSavingsAccountId
    ) {
        final Account normalized = Objects.requireNonNull(account, "Account must not be null");
        return new FxAccount(
                FxAccountId.of(normalized.accountId().value()),
                normalized,
                linkedSavingsAccountId);
    }

    /**
     * @brief 生成开户事件（Build Opened Event）；
     *        Build FX-account-opened event.
     *
     * @return 开户事件（Opened event）。
     */
    public FxAccountOpened openedEvent() {
        return new FxAccountOpened(
                fxAccountId,
                account.customerId(),
                linkedSavingsAccountId,
                account.accountNo(),
                account.openedAt());
    }

    /**
     * @brief 返回外汇账户 ID（Return FX Account ID）；
     *        Return FX-account identifier.
     *
     * @return 外汇账户 ID（FX-account ID）。
     */
    public FxAccountId fxAccountId() {
        return fxAccountId;
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

    /**
     * @brief 返回绑定储蓄账户 ID（Return Linked Savings Account ID）；
     *        Return linked savings-account identifier.
     *
     * @return 绑定储蓄账户 ID（Linked savings-account ID）。
     */
    public SavingsAccountId linkedSavingsAccountId() {
        return linkedSavingsAccountId;
    }
}
