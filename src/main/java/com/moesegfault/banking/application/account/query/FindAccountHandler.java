package com.moesegfault.banking.application.account.query;

import com.moesegfault.banking.application.account.AccountNotFoundException;
import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.domain.account.Account;
import com.moesegfault.banking.domain.account.AccountId;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.account.AccountType;
import com.moesegfault.banking.domain.account.FxAccount;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 查询单个账户处理器（Find Account Handler）；
 *        Application handler that fetches one account by ID or account number.
 */
public final class FindAccountHandler {

    /**
     * @brief 账户仓储接口（Account Repository Port）；
     *        Account repository port.
     */
    private final AccountRepository accountRepository;

    /**
     * @brief 构造处理器（Construct Handler）；
     *        Construct find-account handler.
     *
     * @param accountRepository 账户仓储接口（Account repository port）。
     */
    public FindAccountHandler(final AccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository, "accountRepository must not be null");
    }

    /**
     * @brief 兼容构造处理器（Compatibility Constructor）；
     *        Compatibility constructor that accepts transaction manager for
     *        existing wiring styles.
     *
     * @param accountRepository 账户仓储接口（Account repository port）。
     * @param transactionManager 事务管理器（Transaction manager, currently unused）。
     */
    public FindAccountHandler(
            final AccountRepository accountRepository,
            final DbTransactionManager transactionManager
    ) {
        this(accountRepository);
        Objects.requireNonNull(transactionManager, "transactionManager must not be null");
    }

    /**
     * @brief 执行单账户查询（Handle Find Account）；
     *        Execute account lookup.
     *
     * @param query 查询请求（Find-account query）。
     * @return 账户结果（Account result）。
     */
    public AccountResult handle(final FindAccountQuery query) {
        final FindAccountQuery normalized = Objects.requireNonNull(query, "query must not be null");
        final Account account = loadAccount(normalized);
        if (account.accountType() == AccountType.FX) {
            final Optional<FxAccount> fxAccount = accountRepository.findFxAccountById(
                    com.moesegfault.banking.domain.account.FxAccountId.of(account.accountId().value()));
            return fxAccount.map(value -> AccountResult.from(value.account(), value.linkedSavingsAccountId()))
                    .orElseGet(() -> AccountResult.from(account));
        }
        return AccountResult.from(account);
    }

    /**
     * @brief 根据查询条件读取账户（Load Account by Query）；
     *        Load account according to query condition.
     *
     * @param query 查询请求（Find-account query）。
     * @return 账户实体（Account entity）。
     */
    private Account loadAccount(final FindAccountQuery query) {
        if (query.accountId() != null) {
            return accountRepository.findAccountById(AccountId.of(query.accountId()))
                    .orElseThrow(() -> new AccountNotFoundException("Account not found: " + query.accountId()));
        }
        return accountRepository.findAccountByNumber(com.moesegfault.banking.domain.account.AccountNumber.of(query.accountNo()))
                .orElseThrow(() -> new AccountNotFoundException("Account not found by accountNo: " + query.accountNo()));
    }
}
