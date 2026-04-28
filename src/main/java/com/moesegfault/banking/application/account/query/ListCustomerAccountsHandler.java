package com.moesegfault.banking.application.account.query;

import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.domain.account.Account;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.account.AccountStatus;
import com.moesegfault.banking.domain.account.AccountType;
import com.moesegfault.banking.domain.account.CustomerId;
import com.moesegfault.banking.domain.account.FxAccount;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 客户账户列表查询处理器（List Customer Accounts Handler）；
 *        Application handler that lists accounts owned by one customer.
 */
public final class ListCustomerAccountsHandler {

    /**
     * @brief 账户仓储接口（Account Repository Port）；
     *        Account repository port.
     */
    private final AccountRepository accountRepository;

    /**
     * @brief 构造处理器（Construct Handler）；
     *        Construct list-customer-accounts handler.
     *
     * @param accountRepository 账户仓储接口（Account repository port）。
     */
    public ListCustomerAccountsHandler(final AccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository, "accountRepository must not be null");
    }

    /**
     * @brief 执行客户账户列表查询（Handle List Customer Accounts）；
     *        Execute customer-account listing query.
     *
     * @param query 查询请求（List-customer-accounts query）。
     * @return 账户结果列表（Account-result list）。
     */
    public List<AccountResult> handle(final ListCustomerAccountsQuery query) {
        final ListCustomerAccountsQuery normalized = Objects.requireNonNull(query, "query must not be null");
        final List<Account> accounts = accountRepository.findAccountsByCustomerId(CustomerId.of(normalized.customerId()));
        final List<AccountResult> results = new ArrayList<>(accounts.size());
        for (Account account : accounts) {
            if (!normalized.includeClosedAccounts() && account.accountStatus() == AccountStatus.CLOSED) {
                continue;
            }
            results.add(toResult(account));
        }
        return results;
    }

    /**
     * @brief 转换账户结果（Convert to Account Result）；
     *        Convert account entity to account result.
     *
     * @param account 账户实体（Account entity）。
     * @return 账户结果（Account result）。
     */
    private AccountResult toResult(final Account account) {
        if (account.accountType() != AccountType.FX) {
            return AccountResult.from(account);
        }
        final Optional<FxAccount> fxAccount = accountRepository.findFxAccountById(
                com.moesegfault.banking.domain.account.FxAccountId.of(account.accountId().value()));
        return fxAccount.map(value -> AccountResult.from(value.account(), value.linkedSavingsAccountId()))
                .orElseGet(() -> AccountResult.from(account));
    }
}
