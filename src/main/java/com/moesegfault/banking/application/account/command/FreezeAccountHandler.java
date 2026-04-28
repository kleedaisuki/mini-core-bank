package com.moesegfault.banking.application.account.command;

import com.moesegfault.banking.application.account.AccountNotFoundException;
import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.domain.account.Account;
import com.moesegfault.banking.domain.account.AccountId;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.util.Objects;

/**
 * @brief 冻结账户处理器（Freeze Account Handler）；
 *        Application handler that orchestrates account freezing lifecycle step.
 */
public final class FreezeAccountHandler {

    /**
     * @brief 账户仓储接口（Account Repository Port）；
     *        Account repository port.
     */
    private final AccountRepository accountRepository;

    /**
     * @brief 事务管理接口（Transaction Manager Port）；
     *        Transaction manager port.
     */
    private final DbTransactionManager transactionManager;

    /**
     * @brief 构造处理器（Construct Handler）；
     *        Construct freeze-account handler.
     *
     * @param accountRepository 账户仓储接口（Account repository port）。
     * @param transactionManager 事务管理接口（Transaction manager port）。
     */
    public FreezeAccountHandler(
            final AccountRepository accountRepository,
            final DbTransactionManager transactionManager
    ) {
        this.accountRepository = Objects.requireNonNull(accountRepository, "accountRepository must not be null");
        this.transactionManager = Objects.requireNonNull(transactionManager, "transactionManager must not be null");
    }

    /**
     * @brief 执行冻结账户（Handle Freeze Account）；
     *        Execute account freezing orchestration.
     *
     * @param command 冻结账户命令（Freeze-account command）。
     * @return 冻结后账户结果（Frozen account result）。
     */
    public AccountResult handle(final FreezeAccountCommand command) {
        final FreezeAccountCommand normalized = Objects.requireNonNull(command, "command must not be null");
        return transactionManager.execute(() -> {
            final Account account = accountRepository.findAccountById(AccountId.of(normalized.accountId()))
                    .orElseThrow(() -> new AccountNotFoundException(
                            "Account not found: " + normalized.accountId()));
            account.freeze();
            accountRepository.saveAccount(account);
            return AccountResult.from(account);
        });
    }
}
