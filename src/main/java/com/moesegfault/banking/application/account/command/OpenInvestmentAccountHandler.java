package com.moesegfault.banking.application.account.command;

import com.moesegfault.banking.application.account.AccountApplicationSupport;
import com.moesegfault.banking.application.account.result.OpenAccountResult;
import com.moesegfault.banking.domain.account.AccountNumber;
import com.moesegfault.banking.domain.account.AccountPolicy;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.account.CustomerId;
import com.moesegfault.banking.domain.account.InvestmentAccount;
import com.moesegfault.banking.domain.account.InvestmentAccountId;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.util.Objects;

/**
 * @brief 开投资账户处理器（Open Investment Account Handler）；
 *        Application handler that orchestrates investment-account opening.
 */
public final class OpenInvestmentAccountHandler {

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
    public OpenInvestmentAccountHandler(
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
    public OpenInvestmentAccountHandler(
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
     * @brief 执行开投资账户（Handle Open Investment Account）；
     *        Execute investment-account opening orchestration.
     *
     * @param command 开投资账户命令（Open investment-account command）。
     * @return 开户结果（Open-account result）。
     */
    public OpenAccountResult handle(final OpenInvestmentAccountCommand command) {
        final OpenInvestmentAccountCommand normalized = Objects.requireNonNull(command, "command must not be null");
        return transactionManager.execute(() -> {
            final Customer customer = AccountApplicationSupport.loadCustomerOrThrow(
                    customerRepository,
                    normalized.customerId());
            AccountApplicationSupport.ensureEligibleCustomer(customer);
            final AccountNumber accountNo = AccountNumber.of(normalized.accountNo());
            AccountApplicationSupport.ensureUniqueAccountNumber(accountRepository, accountNo);

            final CustomerId ownerId = CustomerId.of(customer.customerId().value());
            AccountPolicy.ensureSingleInvestmentAccountPerCustomer(
                    accountRepository.countInvestmentAccountsByCustomerId(ownerId));

            final InvestmentAccount investmentAccount = InvestmentAccount.open(
                    InvestmentAccountId.of(idGenerator.nextId()),
                    ownerId,
                    accountNo);

            accountRepository.saveInvestmentAccount(investmentAccount);
            eventPublisher.publish(investmentAccount.openedEvent());
            return OpenAccountResult.from(investmentAccount.account());
        });
    }
}
