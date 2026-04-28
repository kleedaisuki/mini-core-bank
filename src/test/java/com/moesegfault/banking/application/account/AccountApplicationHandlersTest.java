package com.moesegfault.banking.application.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.moesegfault.banking.application.account.command.FreezeAccountCommand;
import com.moesegfault.banking.application.account.command.FreezeAccountHandler;
import com.moesegfault.banking.application.account.command.OpenFxAccountCommand;
import com.moesegfault.banking.application.account.command.OpenFxAccountHandler;
import com.moesegfault.banking.application.account.command.OpenInvestmentAccountCommand;
import com.moesegfault.banking.application.account.command.OpenInvestmentAccountHandler;
import com.moesegfault.banking.application.account.command.OpenSavingsAccountCommand;
import com.moesegfault.banking.application.account.command.OpenSavingsAccountHandler;
import com.moesegfault.banking.application.account.query.FindAccountHandler;
import com.moesegfault.banking.application.account.query.FindAccountQuery;
import com.moesegfault.banking.application.account.query.ListCustomerAccountsHandler;
import com.moesegfault.banking.application.account.query.ListCustomerAccountsQuery;
import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.application.account.result.OpenAccountResult;
import com.moesegfault.banking.domain.account.Account;
import com.moesegfault.banking.domain.account.AccountId;
import com.moesegfault.banking.domain.account.AccountNumber;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.account.AccountStatus;
import com.moesegfault.banking.domain.account.AccountType;
import com.moesegfault.banking.domain.account.CustomerId;
import com.moesegfault.banking.domain.account.FxAccount;
import com.moesegfault.banking.domain.account.FxAccountId;
import com.moesegfault.banking.domain.account.InvestmentAccount;
import com.moesegfault.banking.domain.account.InvestmentAccountId;
import com.moesegfault.banking.domain.account.SavingsAccount;
import com.moesegfault.banking.domain.account.SavingsAccountId;
import com.moesegfault.banking.domain.customer.Address;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.customer.IdentityDocument;
import com.moesegfault.banking.domain.customer.IdentityDocumentType;
import com.moesegfault.banking.domain.customer.PhoneNumber;
import com.moesegfault.banking.domain.customer.TaxProfile;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.DomainEvent;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/**
 * @brief 账户应用层处理器测试（Account Application Handlers Test）；
 *        Application-layer tests for account command/query handlers.
 */
class AccountApplicationHandlersTest {

    /**
     * @brief 验证外汇开户成功并发布事件（Verify FX Opening Success and Event Publication）；
     *        Verify FX opening succeeds and publishes domain event.
     */
    @Test
    void shouldOpenFxAccountAndPublishOpenedEvent() {
        final InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        final InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
        final CapturingEventPublisher eventPublisher = new CapturingEventPublisher();
        final DbTransactionManager transactionManager = new NoopTransactionManager();
        final IdGenerator idGenerator = new SequenceIdGenerator("fx-acc-001");

        customerRepository.save(activeCustomer("cust-001", "ID-001"));
        accountRepository.saveSavingsAccount(SavingsAccount.open(
                SavingsAccountId.of("sav-001"),
                CustomerId.of("cust-001"),
                AccountNumber.of("SA-001")));

        final OpenFxAccountHandler handler = new OpenFxAccountHandler(
                accountRepository,
                customerRepository,
                transactionManager,
                idGenerator,
                eventPublisher);

        final OpenAccountResult result = handler.handle(new OpenFxAccountCommand("cust-001", "FX-001", "sav-001"));

        assertEquals("fx-acc-001", result.accountId());
        assertEquals("cust-001", result.customerId());
        assertEquals("FX", result.accountType());
        assertEquals("sav-001", result.linkedSavingsAccountId());
        assertNotNull(result.openedAt());
        assertEquals(1, eventPublisher.events().size());
        assertInstanceOf(com.moesegfault.banking.domain.account.FxAccountOpened.class, eventPublisher.events().get(0));
    }

    /**
     * @brief 验证跨客户绑定储蓄账户会被拒绝（Verify Cross-Customer FX-Savings Link Is Rejected）；
     *        Verify FX opening rejects linked savings account owned by another customer.
     */
    @Test
    void shouldRejectFxOpeningWhenLinkedSavingsBelongsToAnotherCustomer() {
        final InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        final InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
        final DbTransactionManager transactionManager = new NoopTransactionManager();
        final IdGenerator idGenerator = new SequenceIdGenerator("fx-acc-002");

        customerRepository.save(activeCustomer("cust-a", "ID-A"));
        customerRepository.save(activeCustomer("cust-b", "ID-B"));
        accountRepository.saveSavingsAccount(SavingsAccount.open(
                SavingsAccountId.of("sav-b"),
                CustomerId.of("cust-b"),
                AccountNumber.of("SA-B-001")));

        final OpenFxAccountHandler handler = new OpenFxAccountHandler(
                accountRepository,
                customerRepository,
                transactionManager,
                idGenerator);

        assertThrows(
                BusinessRuleViolation.class,
                () -> handler.handle(new OpenFxAccountCommand("cust-a", "FX-A-001", "sav-b")));
    }

    /**
     * @brief 验证同一客户仅允许一个投资账户（Verify Single Investment Account per Customer）；
     *        Verify one customer can open at most one investment account.
     */
    @Test
    void shouldAllowOnlySingleInvestmentAccountPerCustomer() {
        final InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        final InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
        final DbTransactionManager transactionManager = new NoopTransactionManager();
        final IdGenerator idGenerator = new SequenceIdGenerator("inv-001", "inv-002");

        customerRepository.save(activeCustomer("cust-100", "ID-100"));

        final OpenInvestmentAccountHandler handler = new OpenInvestmentAccountHandler(
                accountRepository,
                customerRepository,
                transactionManager,
                idGenerator);

        handler.handle(new OpenInvestmentAccountCommand("cust-100", "INV-001"));
        assertThrows(
                BusinessRuleViolation.class,
                () -> handler.handle(new OpenInvestmentAccountCommand("cust-100", "INV-002")));
    }

    /**
     * @brief 验证储蓄开户、冻结、查询和列表流程（Verify Savings Open, Freeze, Find, and List Flow）；
     *        Verify end-to-end lifecycle flow for savings account in application layer.
     */
    @Test
    void shouldOpenFreezeAndQuerySavingsAccount() {
        final InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        final InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
        final DbTransactionManager transactionManager = new NoopTransactionManager();
        final IdGenerator idGenerator = new SequenceIdGenerator("sav-acc-001");

        customerRepository.save(activeCustomer("cust-q-1", "ID-Q-1"));

        final OpenSavingsAccountHandler openHandler = new OpenSavingsAccountHandler(
                accountRepository,
                customerRepository,
                transactionManager,
                idGenerator);
        final OpenAccountResult openResult = openHandler.handle(new OpenSavingsAccountCommand("cust-q-1", "SA-Q-001"));
        assertEquals("SAVINGS", openResult.accountType());

        final FreezeAccountHandler freezeHandler = new FreezeAccountHandler(accountRepository, transactionManager);
        final AccountResult frozen = freezeHandler.handle(new FreezeAccountCommand(openResult.accountId(), "risk-review"));
        assertEquals(AccountStatus.FROZEN.name(), frozen.accountStatus());

        final FindAccountHandler findHandler = new FindAccountHandler(accountRepository);
        final AccountResult foundByNo = findHandler.handle(FindAccountQuery.byAccountNo("SA-Q-001"));
        assertEquals(openResult.accountId(), foundByNo.accountId());
        assertEquals(AccountStatus.FROZEN.name(), foundByNo.accountStatus());

        final ListCustomerAccountsHandler listHandler = new ListCustomerAccountsHandler(accountRepository);
        final List<AccountResult> listed = listHandler.handle(new ListCustomerAccountsQuery("cust-q-1", true));
        assertEquals(1, listed.size());
        assertEquals(AccountType.SAVINGS.name(), listed.get(0).accountType());
    }

    /**
     * @brief 构造激活客户（Build Active Customer）；
     *        Build active customer fixture.
     *
     * @param customerId 客户 ID（Customer ID）。
     * @param idNumber   证件号码（Identity number）。
     * @return 激活客户（Active customer）。
     */
    private static Customer activeCustomer(final String customerId, final String idNumber) {
        return Customer.register(
                com.moesegfault.banking.domain.customer.CustomerId.of(customerId),
                IdentityDocument.of(IdentityDocumentType.ID_CARD, idNumber, "CN"),
                PhoneNumber.of("+8613800000000"),
                Address.of("Shenzhen Nanshan"),
                Address.of("Shenzhen Nanshan"),
                TaxProfile.of(false, (String) null));
    }

    /**
     * @brief 无操作事务管理器（No-op Transaction Manager）；
     *        In-memory transaction manager for unit tests.
     */
    private static final class NoopTransactionManager implements DbTransactionManager {

        /**
         * @brief 在“事务”中执行（Execute in Pseudo Transaction）；
         *        Execute action in pseudo transaction.
         *
         * @param action 事务动作（Transactional action）。
         * @param <T> 结果类型（Result type）。
         * @return 执行结果（Execution result）。
         */
        @Override
        public <T> T execute(final Supplier<T> action) {
            return action.get();
        }
    }

    /**
     * @brief 序列 ID 生成器（Sequence ID Generator）；
     *        Queue-based ID generator for deterministic tests.
     */
    private static final class SequenceIdGenerator implements IdGenerator {

        /**
         * @brief 待发放 ID 队列（Pending ID Queue）；
         *        Queue of pending IDs.
         */
        private final Deque<String> ids;

        /**
         * @brief 构造序列生成器（Construct Sequence Generator）；
         *        Construct sequence ID generator.
         *
         * @param ids 顺序 ID（Ordered IDs）。
         */
        private SequenceIdGenerator(final String... ids) {
            this.ids = new ArrayDeque<>(List.of(ids));
        }

        /**
         * @brief 生成下一个 ID（Generate Next ID）；
         *        Generate next identifier.
         *
         * @return 下一个 ID（Next identifier）。
         */
        @Override
        public String nextId() {
            final String id = ids.pollFirst();
            if (id == null) {
                throw new IllegalStateException("No more test IDs available");
            }
            return id;
        }
    }

    /**
     * @brief 事件捕获发布器（Capturing Event Publisher）；
     *        Event publisher that captures published events for assertions.
     */
    private static final class CapturingEventPublisher implements DomainEventPublisher {

        /**
         * @brief 已发布事件列表（Published Event List）；
         *        Captured published events.
         */
        private final List<DomainEvent> events = new ArrayList<>();

        /**
         * @brief 发布事件（Publish Event）；
         *        Publish one domain event.
         *
         * @param event 领域事件（Domain event）。
         */
        @Override
        public void publish(final DomainEvent event) {
            events.add(event);
        }

        /**
         * @brief 获取已发布事件（Get Published Events）；
         *        Get captured events.
         *
         * @return 已发布事件列表（Published events）。
         */
        private List<DomainEvent> events() {
            return events;
        }
    }

    /**
     * @brief 内存账户仓储（In-memory Account Repository）；
     *        In-memory implementation of account repository for tests.
     */
    private static final class InMemoryAccountRepository implements AccountRepository {

        /**
         * @brief 基础账户存储（Base Account Store）；
         *        Base account storage.
         */
        private final Map<String, Account> accounts = new HashMap<>();

        /**
         * @brief 储蓄账户存储（Savings Account Store）；
         *        Savings-account storage.
         */
        private final Map<String, SavingsAccount> savingsAccounts = new HashMap<>();

        /**
         * @brief 外汇账户存储（FX Account Store）；
         *        FX-account storage.
         */
        private final Map<String, FxAccount> fxAccounts = new HashMap<>();

        /**
         * @brief 投资账户存储（Investment Account Store）；
         *        Investment-account storage.
         */
        private final Map<String, InvestmentAccount> investmentAccounts = new HashMap<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void saveAccount(final Account account) {
            accounts.put(account.accountId().value(), account);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void saveSavingsAccount(final SavingsAccount savingsAccount) {
            saveAccount(savingsAccount.account());
            savingsAccounts.put(savingsAccount.savingsAccountId().value(), savingsAccount);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void saveFxAccount(final FxAccount fxAccount) {
            saveAccount(fxAccount.account());
            fxAccounts.put(fxAccount.fxAccountId().value(), fxAccount);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void saveInvestmentAccount(final InvestmentAccount investmentAccount) {
            saveAccount(investmentAccount.account());
            investmentAccounts.put(investmentAccount.investmentAccountId().value(), investmentAccount);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<Account> findAccountById(final AccountId accountId) {
            return Optional.ofNullable(accounts.get(accountId.value()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<Account> findAccountByNumber(final AccountNumber accountNo) {
            return accounts.values().stream()
                    .filter(value -> value.accountNo().equals(accountNo))
                    .findFirst();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<SavingsAccount> findSavingsAccountById(final SavingsAccountId savingsAccountId) {
            return Optional.ofNullable(savingsAccounts.get(savingsAccountId.value()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<FxAccount> findFxAccountById(final FxAccountId fxAccountId) {
            return Optional.ofNullable(fxAccounts.get(fxAccountId.value()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<InvestmentAccount> findInvestmentAccountById(final InvestmentAccountId investmentAccountId) {
            return Optional.ofNullable(investmentAccounts.get(investmentAccountId.value()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<Account> findAccountsByCustomerId(final CustomerId customerId) {
            return accounts.values().stream()
                    .filter(value -> value.customerId().sameValueAs(customerId))
                    .sorted(Comparator.comparing(Account::openedAt).reversed())
                    .toList();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean existsByAccountNumber(final AccountNumber accountNo) {
            return accounts.values().stream().anyMatch(value -> value.accountNo().equals(accountNo));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long countInvestmentAccountsByCustomerId(final CustomerId customerId) {
            return investmentAccounts.values().stream()
                    .map(InvestmentAccount::account)
                    .filter(value -> value.customerId().sameValueAs(customerId))
                    .count();
        }
    }

    /**
     * @brief 内存客户仓储（In-memory Customer Repository）；
     *        In-memory implementation of customer repository for tests.
     */
    private static final class InMemoryCustomerRepository implements CustomerRepository {

        /**
         * @brief 客户存储（Customer Store）；
         *        Customer storage.
         */
        private final Map<String, Customer> customers = new HashMap<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void save(final Customer customer) {
            customers.put(customer.customerId().value(), customer);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<Customer> findById(final com.moesegfault.banking.domain.customer.CustomerId customerId) {
            return Optional.ofNullable(customers.get(customerId.value()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<Customer> findByIdentityDocument(final IdentityDocument identityDocument) {
            return customers.values().stream()
                    .filter(value -> value.identityDocument().equals(identityDocument))
                    .findFirst();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean existsByIdentityDocument(final IdentityDocument identityDocument) {
            return findByIdentityDocument(identityDocument).isPresent();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<Customer> findByMobilePhone(final PhoneNumber mobilePhone) {
            return customers.values().stream()
                    .filter(value -> value.mobilePhone().equals(mobilePhone))
                    .toList();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<Customer> findAll() {
            return new ArrayList<>(customers.values());
        }
    }
}
