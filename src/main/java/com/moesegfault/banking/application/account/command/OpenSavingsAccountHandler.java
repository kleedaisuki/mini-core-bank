package com.moesegfault.banking.application.account.command;

import com.moesegfault.banking.application.account.AccountApplicationSupport;
import com.moesegfault.banking.application.account.result.OpenAccountResult;
import com.moesegfault.banking.domain.account.AccountNumber;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.account.CustomerId;
import com.moesegfault.banking.domain.account.SavingsAccount;
import com.moesegfault.banking.domain.account.SavingsAccountId;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.util.Objects;

/**
 * @brief 开储蓄账户处理器（Open Savings Account Handler）；
 *        Application handler that orchestrates savings-account opening.
 */
public final class OpenSavingsAccountHandler {

    /**
     * @brief 账户仓储接口（Account Repository Port）；
     *        Account repository port.
     */
    private final AccountRepository accountRepository;

    /**
     * @brief 客户仓储接口（Customer Repository Port）；
     *        Customer repository port.
     */
    private final CustomerRepository customerRepository;

    /**
     * @brief 事务管理接口（Transaction Manager Port）；
     *        Transaction manager port.
     */
    private final DbTransactionManager transactionManager;

    /**
     * @brief ID 生成器接口（ID Generator Port）；
     *        ID generator port.
     */
    private final IdGenerator idGenerator;

    /**
     * @brief 领域事件发布接口（Domain Event Publisher Port）；
     *        Domain event publisher port.
     */
    private final DomainEventPublisher eventPublisher;

    /**
     * @brief 构造处理器（Construct Handler）；
     *        Construct handler with no-op event publisher.
     *
     * @param accountRepository 账户仓储接口（Account repository port）。
     * @param customerRepository 客户仓储接口（Customer repository port）。
     * @param transactionManager 事务管理接口（Transaction manager port）。
     * @param idGenerator ID 生成器接口（ID generator port）。
     */
    public OpenSavingsAccountHandler(
            final AccountRepository accountRepository,
            final CustomerRepository customerRepository,
            final DbTransactionManager transactionManager,
            final IdGenerator idGenerator
    ) {
        this(accountRepository, customerRepository, transactionManager, idGenerator, DomainEventPublisher.noop());
    }

    /**
     * @brief 构造处理器（Construct Handler）；
     *        Construct handler.
     *
     * @param accountRepository 账户仓储接口（Account repository port）。
     * @param customerRepository 客户仓储接口（Customer repository port）。
     * @param transactionManager 事务管理接口（Transaction manager port）。
     * @param idGenerator ID 生成器接口（ID generator port）。
     * @param eventPublisher 领域事件发布接口（Domain event publisher port）。
     */
    public OpenSavingsAccountHandler(
            final AccountRepository accountRepository,
            final CustomerRepository customerRepository,
            final DbTransactionManager transactionManager,
            final IdGenerator idGenerator,
            final DomainEventPublisher eventPublisher
    ) {
        this.accountRepository = Objects.requireNonNull(accountRepository, "accountRepository must not be null");
        this.customerRepository = Objects.requireNonNull(customerRepository, "customerRepository must not be null");
        this.transactionManager = Objects.requireNonNull(transactionManager, "transactionManager must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher must not be null");
    }

    /**
     * @brief 执行开储蓄账户（Handle Open Savings Account）；
     *        Execute savings-account opening orchestration.
     *
     * @param command 开储蓄账户命令（Open savings-account command）。
     * @return 开户结果（Open-account result）。
     */
    public OpenAccountResult handle(final OpenSavingsAccountCommand command) {
        final OpenSavingsAccountCommand normalized = Objects.requireNonNull(command, "command must not be null");
        return transactionManager.execute(() -> {
            final Customer customer = AccountApplicationSupport.loadCustomerOrThrow(
                    customerRepository,
                    normalized.customerId());
            AccountApplicationSupport.ensureEligibleCustomer(customer);
            final AccountNumber accountNo = AccountNumber.of(normalized.accountNo());
            AccountApplicationSupport.ensureUniqueAccountNumber(accountRepository, accountNo);

            final SavingsAccount savingsAccount = SavingsAccount.open(
                    SavingsAccountId.of(idGenerator.nextId()),
                    CustomerId.of(customer.customerId().value()),
                    accountNo);

            accountRepository.saveSavingsAccount(savingsAccount);
            eventPublisher.publish(savingsAccount.openedEvent());
            return OpenAccountResult.from(savingsAccount.account());
        });
    }
}
