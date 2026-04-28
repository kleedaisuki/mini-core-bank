package com.moesegfault.banking.domain.account;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.util.Objects;

/**
 * @brief 投资账户实体（Investment Account Entity），对应 `investment_account` 子类型；
 *        Investment-account entity mapped to `investment_account` subtype.
 */
public final class InvestmentAccount {

    /**
     * @brief 投资账户 ID（Investment Account ID）；
     *        Investment-account identifier.
     */
    private final InvestmentAccountId investmentAccountId;

    /**
     * @brief 基础账户实体（Base Account Entity）；
     *        Base account entity.
     */
    private final Account account;

    /**
     * @brief 构造投资账户实体（Construct Investment Account Entity）；
     *        Construct investment-account entity.
     *
     * @param investmentAccountId 投资账户 ID（Investment-account ID）。
     * @param account             基础账户实体（Base account entity）。
     */
    private InvestmentAccount(
            final InvestmentAccountId investmentAccountId,
            final Account account
    ) {
        this.investmentAccountId = Objects.requireNonNull(
                investmentAccountId,
                "Investment account ID must not be null");
        this.account = Objects.requireNonNull(account, "Account must not be null");
        if (this.account.accountType() != AccountType.INVESTMENT) {
            throw new BusinessRuleViolation("InvestmentAccount must wrap an account with type INVESTMENT");
        }
        if (!AccountId.of(this.investmentAccountId.value()).sameValueAs(this.account.accountId())) {
            throw new BusinessRuleViolation("Investment account ID must be equal to base account_id");
        }
    }

    /**
     * @brief 开立投资账户（Open Investment Account）；
     *        Open an investment account.
     *
     * @param investmentAccountId 投资账户 ID（Investment-account ID）。
     * @param customerId          所属客户 ID（Owner customer ID）。
     * @param accountNo           账户号（Account number）。
     * @return 投资账户实体（Investment-account entity）。
     */
    public static InvestmentAccount open(
            final InvestmentAccountId investmentAccountId,
            final CustomerId customerId,
            final AccountNumber accountNo
    ) {
        final InvestmentAccountId id = Objects.requireNonNull(
                investmentAccountId,
                "Investment account ID must not be null");
        final Account base = Account.open(
                AccountId.of(id.value()),
                customerId,
                accountNo,
                AccountType.INVESTMENT);
        return new InvestmentAccount(id, base);
    }

    /**
     * @brief 从持久化状态重建投资账户（Restore Investment Account from Persistence）；
     *        Restore investment-account entity from persistence state.
     *
     * @param account 基础账户实体（Base account entity）。
     * @return 投资账户实体（Investment-account entity）。
     */
    public static InvestmentAccount restore(final Account account) {
        final Account normalized = Objects.requireNonNull(account, "Account must not be null");
        return new InvestmentAccount(InvestmentAccountId.of(normalized.accountId().value()), normalized);
    }

    /**
     * @brief 生成开户事件（Build Opened Event）；
     *        Build investment-account-opened event.
     *
     * @return 开户事件（Opened event）。
     */
    public InvestmentAccountOpened openedEvent() {
        return new InvestmentAccountOpened(
                investmentAccountId,
                account.customerId(),
                account.accountNo(),
                account.openedAt());
    }

    /**
     * @brief 返回投资账户 ID（Return Investment Account ID）；
     *        Return investment-account identifier.
     *
     * @return 投资账户 ID（Investment-account ID）。
     */
    public InvestmentAccountId investmentAccountId() {
        return investmentAccountId;
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
