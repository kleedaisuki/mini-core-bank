package com.moesegfault.banking.domain.account;

import com.moesegfault.banking.domain.customer.CustomerId;
import java.util.List;
import java.util.Optional;

/**
 * @brief 账户仓储接口（Account Repository Interface），定义账户聚合（Aggregate）持久化契约；
 *        Account repository interface defining persistence contracts for account aggregates.
 */
public interface AccountRepository {

    /**
     * @brief 保存账户基础实体（Save Base Account Entity）；
     *        Save base account entity.
     *
     * @param account 账户实体（Account entity）。
     */
    void saveAccount(Account account);

    /**
     * @brief 保存储蓄账户实体（Save Savings Account Entity）；
     *        Save savings-account entity.
     *
     * @param savingsAccount 储蓄账户实体（Savings-account entity）。
     */
    void saveSavingsAccount(SavingsAccount savingsAccount);

    /**
     * @brief 保存外汇账户实体（Save FX Account Entity）；
     *        Save FX-account entity.
     *
     * @param fxAccount 外汇账户实体（FX-account entity）。
     */
    void saveFxAccount(FxAccount fxAccount);

    /**
     * @brief 保存投资账户实体（Save Investment Account Entity）；
     *        Save investment-account entity.
     *
     * @param investmentAccount 投资账户实体（Investment-account entity）。
     */
    void saveInvestmentAccount(InvestmentAccount investmentAccount);

    /**
     * @brief 按账户 ID 查询基础账户（Find Base Account by Account ID）；
     *        Find base account by account ID.
     *
     * @param accountId 账户 ID（Account ID）。
     * @return 账户可选值（Optional account）。
     */
    Optional<Account> findAccountById(AccountId accountId);

    /**
     * @brief 按账户号查询基础账户（Find Base Account by Account Number）；
     *        Find base account by account number.
     *
     * @param accountNo 账户号（Account number）。
     * @return 账户可选值（Optional account）。
     */
    Optional<Account> findAccountByNumber(AccountNumber accountNo);

    /**
     * @brief 按账户 ID 查询储蓄账户（Find Savings Account by ID）；
     *        Find savings account by ID.
     *
     * @param savingsAccountId 储蓄账户 ID（Savings-account ID）。
     * @return 储蓄账户可选值（Optional savings account）。
     */
    Optional<SavingsAccount> findSavingsAccountById(SavingsAccountId savingsAccountId);

    /**
     * @brief 按账户 ID 查询外汇账户（Find FX Account by ID）；
     *        Find FX account by ID.
     *
     * @param fxAccountId 外汇账户 ID（FX-account ID）。
     * @return 外汇账户可选值（Optional FX account）。
     */
    Optional<FxAccount> findFxAccountById(FxAccountId fxAccountId);

    /**
     * @brief 按账户 ID 查询投资账户（Find Investment Account by ID）；
     *        Find investment account by ID.
     *
     * @param investmentAccountId 投资账户 ID（Investment-account ID）。
     * @return 投资账户可选值（Optional investment account）。
     */
    Optional<InvestmentAccount> findInvestmentAccountById(InvestmentAccountId investmentAccountId);

    /**
     * @brief 查询某客户下全部基础账户（Find All Base Accounts by Customer）；
     *        Find all base accounts by customer.
     *
     * @param customerId 客户 ID（Customer ID）。
     * @return 账户列表（Account list）。
     */
    List<Account> findAccountsByCustomerId(CustomerId customerId);

    /**
     * @brief 判断账户号是否存在（Check Account Number Existence）；
     *        Check whether account number exists.
     *
     * @param accountNo 账户号（Account number）。
     * @return 存在返回 true（true when exists）。
     */
    boolean existsByAccountNumber(AccountNumber accountNo);

    /**
     * @brief 统计客户投资账户数量（Count Investment Accounts by Customer）；
     *        Count investment accounts by customer.
     *
     * @param customerId 客户 ID（Customer ID）。
     * @return 投资账户数量（Investment-account count）。
     */
    long countInvestmentAccountsByCustomerId(CustomerId customerId);
}
