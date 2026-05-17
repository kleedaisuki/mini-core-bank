package com.moesegfault.banking.presentation.cli.gui;

import com.moesegfault.banking.application.account.command.OpenFxAccountHandler;
import com.moesegfault.banking.application.account.command.OpenInvestmentAccountHandler;
import com.moesegfault.banking.application.account.command.OpenSavingsAccountHandler;
import com.moesegfault.banking.application.account.query.FindAccountHandler;
import com.moesegfault.banking.application.account.query.ListCustomerAccountsHandler;
import com.moesegfault.banking.application.business.query.FindBusinessTransactionHandler;
import com.moesegfault.banking.application.business.query.ListBusinessTransactionsHandler;
import com.moesegfault.banking.application.card.command.IssueCreditCardHandler;
import com.moesegfault.banking.application.card.command.IssueDebitCardHandler;
import com.moesegfault.banking.application.card.command.IssueSupplementaryCreditCardHandler;
import com.moesegfault.banking.application.card.command.IssueSupplementaryDebitCardHandler;
import com.moesegfault.banking.application.card.query.FindCardHandler;
import com.moesegfault.banking.application.credit.command.GenerateStatementHandler;
import com.moesegfault.banking.application.credit.command.RepayCreditCardHandler;
import com.moesegfault.banking.application.credit.query.FindStatementHandler;
import com.moesegfault.banking.application.credit.service.UsedCreditStatementAmountCalculator;
import com.moesegfault.banking.application.customer.command.RegisterCustomerHandler;
import com.moesegfault.banking.application.customer.query.FindCustomerHandler;
import com.moesegfault.banking.application.customer.query.ListCustomersHandler;
import com.moesegfault.banking.application.investment.command.BuyProductHandler;
import com.moesegfault.banking.application.investment.command.CreateInvestmentProductHandler;
import com.moesegfault.banking.application.investment.command.SellProductHandler;
import com.moesegfault.banking.application.investment.query.ListHoldingsHandler;
import com.moesegfault.banking.application.ledger.query.FindBalanceHandler;
import com.moesegfault.banking.application.ledger.query.ListLedgerEntriesHandler;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.business.BusinessRepository;
import com.moesegfault.banking.domain.card.CardRepository;
import com.moesegfault.banking.domain.credit.CreditRepository;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.investment.InvestmentRepository;
import com.moesegfault.banking.domain.ledger.LedgerRepository;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.infrastructure.config.AppConfig;
import com.moesegfault.banking.infrastructure.gui.swing.SwingGuiRuntime;
import com.moesegfault.banking.infrastructure.gui.swing.view.SwingEmptyStateView;
import com.moesegfault.banking.infrastructure.gui.swing.view.SwingErrorDialogView;
import com.moesegfault.banking.infrastructure.gui.swing.view.SwingFormView;
import com.moesegfault.banking.infrastructure.gui.swing.view.SwingSuccessDialogView;
import com.moesegfault.banking.infrastructure.gui.swing.view.SwingTableView;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.migration.FlywayMigrationRunner;
import com.moesegfault.banking.infrastructure.persistence.Repository;
import com.moesegfault.banking.infrastructure.persistence.jdbc.JdbcRepository;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import com.moesegfault.banking.presentation.gui.BankingGui;
import com.moesegfault.banking.presentation.gui.GuiBootstrap;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiModule;
import com.moesegfault.banking.presentation.gui.GuiRuntime;
import com.moesegfault.banking.presentation.gui.GuiToolkitType;
import com.moesegfault.banking.presentation.gui.account.AccountGuiModule;
import com.moesegfault.banking.presentation.gui.account.ListAccountsPageFactory;
import com.moesegfault.banking.presentation.gui.account.OpenFxAccountPageFactory;
import com.moesegfault.banking.presentation.gui.account.OpenInvestmentAccountPageFactory;
import com.moesegfault.banking.presentation.gui.account.OpenSavingsAccountPageFactory;
import com.moesegfault.banking.presentation.gui.account.ShowAccountPageFactory;
import com.moesegfault.banking.presentation.gui.business.BusinessGuiModule;
import com.moesegfault.banking.presentation.gui.card.CardGuiModule;
import com.moesegfault.banking.presentation.gui.card.IssueCreditCardPageFactory;
import com.moesegfault.banking.presentation.gui.card.IssueDebitCardPageFactory;
import com.moesegfault.banking.presentation.gui.card.IssueSupplementaryCreditCardPageFactory;
import com.moesegfault.banking.presentation.gui.card.IssueSupplementaryDebitCardPageFactory;
import com.moesegfault.banking.presentation.gui.card.ShowCardPageFactory;
import com.moesegfault.banking.presentation.gui.credit.CreditGuiModule;
import com.moesegfault.banking.presentation.gui.credit.GenerateStatementPageFactory;
import com.moesegfault.banking.presentation.gui.credit.RepayCreditCardPageFactory;
import com.moesegfault.banking.presentation.gui.credit.ShowStatementPageFactory;
import com.moesegfault.banking.presentation.gui.customer.CustomerGuiModule;
import com.moesegfault.banking.presentation.gui.customer.CustomerGuiPageIds;
import com.moesegfault.banking.presentation.gui.investment.InvestmentGuiModule;
import com.moesegfault.banking.presentation.gui.ledger.LedgerGuiModule;
import com.moesegfault.banking.presentation.gui.ledger.ShowBalancePageFactory;
import com.moesegfault.banking.presentation.gui.ledger.ShowEntriesPageFactory;
import java.util.Objects;
import java.util.function.Supplier;
import javax.sql.DataSource;

/**
 * @brief 默认 CLI GUI 启动器（Default CLI GUI Launcher），装配数据库、应用服务与 Swing GUI；
 *        Default CLI GUI launcher that wires database, application services, and Swing GUI.
 */
public final class DefaultGuiCliLauncher implements GuiCliLauncher {

    /**
     * {@inheritDoc}
     */
    @Override
    public void launch(final GuiToolkitType toolkitType) {
        final GuiToolkitType normalizedToolkitType = Objects.requireNonNull(toolkitType, "toolkitType must not be null");
        final AppConfig appConfig = AppConfig.createDefault();
        boolean success = false;
        try {
            new FlywayMigrationRunner().migrate(appConfig);
            final Repository repository = new JdbcRepository(appConfig.dataSource());
            final GuiRuntime guiRuntime = createRuntime(normalizedToolkitType);
            final GuiBootstrap guiBootstrap = new GuiBootstrap(
                    ignoredToolkit -> guiRuntime,
                    GuiContext::new,
                    CustomerGuiPageIds.LIST_CUSTOMERS,
                    createModules(repository, appConfig.idGenerator(), guiRuntime));
            new BankingGui(guiBootstrap).launch(normalizedToolkitType);
            registerShutdownClose(appConfig.dataSource());
            success = true;
        } finally {
            if (!success) {
                closeDataSource(appConfig.dataSource());
            }
        }
    }

    /**
     * @brief 创建 GUI 运行时（Create GUI Runtime）；
     *        Create GUI runtime for a supported toolkit.
     *
     * @param toolkitType GUI 技术栈（GUI toolkit type）。
     * @return GUI 运行时（GUI runtime）。
     */
    private static GuiRuntime createRuntime(final GuiToolkitType toolkitType) {
        return switch (toolkitType) {
            case SWING -> new SwingGuiRuntime();
            case JAVAFX -> throw new IllegalArgumentException("Unsupported GUI toolkit: JAVAFX. Available toolkit: SWING");
        };
    }

    /**
     * @brief 创建 GUI 模块数组（Create GUI Module Array）；
     *        Create GUI modules wired to application handlers.
     *
     * @param repository 仓储门面（Repository facade）。
     * @param idGenerator ID 生成器（ID generator）。
     * @param guiRuntime GUI 运行时（GUI runtime）。
     * @return GUI 模块数组（GUI module array）。
     */
    private static GuiModule[] createModules(
            final Repository repository,
            final IdGenerator idGenerator,
            final GuiRuntime guiRuntime
    ) {
        final Repository normalizedRepository = Objects.requireNonNull(repository, "repository must not be null");
        final IdGenerator normalizedIdGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
        final GuiRuntime normalizedGuiRuntime = Objects.requireNonNull(guiRuntime, "guiRuntime must not be null");
        final DbTransactionManager transactionManager = new RepositoryTransactionManager(normalizedRepository);
        final DomainEventPublisher eventPublisher = DomainEventPublisher.noop();
        final GuiExceptionHandler exceptionHandler = new GuiExceptionHandler();

        final CustomerRepository customerRepository = normalizedRepository.require(CustomerRepository.class);
        final AccountRepository accountRepository = normalizedRepository.require(AccountRepository.class);
        final CardRepository cardRepository = normalizedRepository.require(CardRepository.class);
        final CreditRepository creditRepository = normalizedRepository.require(CreditRepository.class);
        final InvestmentRepository investmentRepository = normalizedRepository.require(InvestmentRepository.class);
        final BusinessRepository businessRepository = normalizedRepository.require(BusinessRepository.class);
        final LedgerRepository ledgerRepository = normalizedRepository.require(LedgerRepository.class);

        return new GuiModule[] {
                createCustomerModule(customerRepository, transactionManager, normalizedIdGenerator, eventPublisher, exceptionHandler),
                createAccountModule(
                        accountRepository,
                        customerRepository,
                        transactionManager,
                        normalizedIdGenerator,
                        eventPublisher,
                        exceptionHandler),
                createCardModule(
                        cardRepository,
                        accountRepository,
                        customerRepository,
                        creditRepository,
                        transactionManager,
                        normalizedIdGenerator,
                        eventPublisher,
                        exceptionHandler),
                createCreditModule(creditRepository, transactionManager, normalizedIdGenerator, eventPublisher, exceptionHandler),
                createLedgerModule(ledgerRepository, normalizedGuiRuntime, exceptionHandler),
                createBusinessModule(businessRepository),
                createInvestmentModule(
                        investmentRepository,
                        accountRepository,
                        businessRepository,
                        transactionManager,
                        normalizedIdGenerator,
                        eventPublisher,
                        exceptionHandler)
        };
    }

    /**
     * @brief 创建客户 GUI 模块（Create Customer GUI Module）；
     *        Create customer GUI module.
     *
     * @param customerRepository 客户仓储（Customer repository）。
     * @param transactionManager 事务管理器（Transaction manager）。
     * @param idGenerator ID 生成器（ID generator）。
     * @param eventPublisher 领域事件发布器（Domain event publisher）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     * @return 客户 GUI 模块（Customer GUI module）。
     */
    private static GuiModule createCustomerModule(
            final CustomerRepository customerRepository,
            final DbTransactionManager transactionManager,
            final IdGenerator idGenerator,
            final DomainEventPublisher eventPublisher,
            final GuiExceptionHandler exceptionHandler
    ) {
        return new CustomerGuiModule(
                new RegisterCustomerHandler(customerRepository, transactionManager, idGenerator, eventPublisher),
                new FindCustomerHandler(customerRepository),
                new ListCustomersHandler(customerRepository),
                SwingFormView::new,
                SwingTableView::new,
                exceptionHandler);
    }

    /**
     * @brief 创建账户 GUI 模块（Create Account GUI Module）；
     *        Create account GUI module.
     *
     * @param accountRepository 账户仓储（Account repository）。
     * @param customerRepository 客户仓储（Customer repository）。
     * @param transactionManager 事务管理器（Transaction manager）。
     * @param idGenerator ID 生成器（ID generator）。
     * @param eventPublisher 领域事件发布器（Domain event publisher）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     * @return 账户 GUI 模块（Account GUI module）。
     */
    private static GuiModule createAccountModule(
            final AccountRepository accountRepository,
            final CustomerRepository customerRepository,
            final DbTransactionManager transactionManager,
            final IdGenerator idGenerator,
            final DomainEventPublisher eventPublisher,
            final GuiExceptionHandler exceptionHandler
    ) {
        final SwingErrorDialogView errorDialogView = new SwingErrorDialogView();
        final SwingSuccessDialogView successDialogView = new SwingSuccessDialogView();
        return new AccountGuiModule(
                new OpenSavingsAccountPageFactory(
                        new OpenSavingsAccountHandler(
                                accountRepository,
                                customerRepository,
                                transactionManager,
                                idGenerator,
                                eventPublisher),
                        SwingFormView::new,
                        errorDialogView,
                        successDialogView,
                        exceptionHandler),
                new OpenFxAccountPageFactory(
                        new OpenFxAccountHandler(accountRepository, customerRepository, transactionManager, idGenerator, eventPublisher),
                        SwingFormView::new,
                        errorDialogView,
                        successDialogView,
                        exceptionHandler),
                new OpenInvestmentAccountPageFactory(
                        new OpenInvestmentAccountHandler(
                                accountRepository,
                                customerRepository,
                                transactionManager,
                                idGenerator,
                                eventPublisher),
                        SwingFormView::new,
                        errorDialogView,
                        successDialogView,
                        exceptionHandler),
                new ShowAccountPageFactory(
                        new FindAccountHandler(accountRepository),
                        SwingFormView::new,
                        SwingTableView::new,
                        SwingEmptyStateView::new,
                        errorDialogView,
                        exceptionHandler),
                new ListAccountsPageFactory(
                        new ListCustomerAccountsHandler(accountRepository),
                        SwingFormView::new,
                        SwingTableView::new,
                        SwingEmptyStateView::new,
                        errorDialogView,
                        exceptionHandler));
    }

    /**
     * @brief 创建卡 GUI 模块（Create Card GUI Module）；
     *        Create card GUI module.
     *
     * @param cardRepository 卡仓储（Card repository）。
     * @param accountRepository 账户仓储（Account repository）。
     * @param customerRepository 客户仓储（Customer repository）。
     * @param creditRepository 信用仓储（Credit repository）。
     * @param transactionManager 事务管理器（Transaction manager）。
     * @param idGenerator ID 生成器（ID generator）。
     * @param eventPublisher 领域事件发布器（Domain event publisher）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     * @return 卡 GUI 模块（Card GUI module）。
     */
    private static GuiModule createCardModule(
            final CardRepository cardRepository,
            final AccountRepository accountRepository,
            final CustomerRepository customerRepository,
            final CreditRepository creditRepository,
            final DbTransactionManager transactionManager,
            final IdGenerator idGenerator,
            final DomainEventPublisher eventPublisher,
            final GuiExceptionHandler exceptionHandler
    ) {
        return new CardGuiModule(
                new IssueDebitCardPageFactory(
                        new IssueDebitCardHandler(
                                cardRepository,
                                accountRepository,
                                customerRepository,
                                idGenerator,
                                transactionManager,
                                eventPublisher),
                        exceptionHandler),
                new IssueSupplementaryDebitCardPageFactory(
                        new IssueSupplementaryDebitCardHandler(
                                cardRepository,
                                customerRepository,
                                idGenerator,
                                transactionManager,
                                eventPublisher),
                        exceptionHandler),
                new IssueCreditCardPageFactory(
                        new IssueCreditCardHandler(
                                cardRepository,
                                accountRepository,
                                customerRepository,
                                creditRepository,
                                idGenerator,
                                transactionManager,
                                eventPublisher),
                        exceptionHandler),
                new IssueSupplementaryCreditCardPageFactory(
                        new IssueSupplementaryCreditCardHandler(
                                cardRepository,
                                accountRepository,
                                customerRepository,
                                creditRepository,
                                idGenerator,
                                transactionManager,
                                eventPublisher),
                        exceptionHandler),
                new ShowCardPageFactory(new FindCardHandler(cardRepository), exceptionHandler));
    }

    /**
     * @brief 创建信用 GUI 模块（Create Credit GUI Module）；
     *        Create credit GUI module.
     *
     * @param creditRepository 信用仓储（Credit repository）。
     * @param transactionManager 事务管理器（Transaction manager）。
     * @param idGenerator ID 生成器（ID generator）。
     * @param eventPublisher 领域事件发布器（Domain event publisher）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     * @return 信用 GUI 模块（Credit GUI module）。
     */
    private static GuiModule createCreditModule(
            final CreditRepository creditRepository,
            final DbTransactionManager transactionManager,
            final IdGenerator idGenerator,
            final DomainEventPublisher eventPublisher,
            final GuiExceptionHandler exceptionHandler
    ) {
        return new CreditGuiModule(
                new GenerateStatementPageFactory(
                        new GenerateStatementHandler(
                                creditRepository,
                                transactionManager,
                                idGenerator,
                                new UsedCreditStatementAmountCalculator(),
                                eventPublisher),
                        exceptionHandler,
                        SwingFormView::new,
                        SwingTableView::new),
                new RepayCreditCardPageFactory(
                        new RepayCreditCardHandler(creditRepository, transactionManager),
                        exceptionHandler,
                        SwingFormView::new,
                        SwingTableView::new),
                new ShowStatementPageFactory(
                        new FindStatementHandler(creditRepository),
                        exceptionHandler,
                        SwingFormView::new,
                        SwingTableView::new));
    }

    /**
     * @brief 创建账务 GUI 模块（Create Ledger GUI Module）；
     *        Create ledger GUI module.
     *
     * @param ledgerRepository 账务仓储（Ledger repository）。
     * @param guiRuntime GUI 运行时（GUI runtime）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     * @return 账务 GUI 模块（Ledger GUI module）。
     */
    private static GuiModule createLedgerModule(
            final LedgerRepository ledgerRepository,
            final GuiRuntime guiRuntime,
            final GuiExceptionHandler exceptionHandler
    ) {
        return new LedgerGuiModule(
                new ShowBalancePageFactory(
                        new FindBalanceHandler(ledgerRepository),
                        guiRuntime,
                        exceptionHandler,
                        SwingFormView::new,
                        SwingTableView::new,
                        SwingEmptyStateView::new),
                new ShowEntriesPageFactory(
                        new ListLedgerEntriesHandler(ledgerRepository),
                        guiRuntime,
                        exceptionHandler,
                        SwingFormView::new,
                        SwingTableView::new,
                        SwingEmptyStateView::new));
    }

    /**
     * @brief 创建业务流水 GUI 模块（Create Business GUI Module）；
     *        Create business transaction GUI module.
     *
     * @param businessRepository 业务仓储（Business repository）。
     * @return 业务流水 GUI 模块（Business GUI module）。
     */
    private static GuiModule createBusinessModule(final BusinessRepository businessRepository) {
        return new BusinessGuiModule(
                new FindBusinessTransactionHandler(businessRepository),
                new ListBusinessTransactionsHandler(businessRepository));
    }

    /**
     * @brief 创建投资 GUI 模块（Create Investment GUI Module）；
     *        Create investment GUI module.
     *
     * @param investmentRepository 投资仓储（Investment repository）。
     * @param accountRepository 账户仓储（Account repository）。
     * @param businessRepository 业务仓储（Business repository）。
     * @param transactionManager 事务管理器（Transaction manager）。
     * @param idGenerator ID 生成器（ID generator）。
     * @param eventPublisher 领域事件发布器（Domain event publisher）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     * @return 投资 GUI 模块（Investment GUI module）。
     */
    private static GuiModule createInvestmentModule(
            final InvestmentRepository investmentRepository,
            final AccountRepository accountRepository,
            final BusinessRepository businessRepository,
            final DbTransactionManager transactionManager,
            final IdGenerator idGenerator,
            final DomainEventPublisher eventPublisher,
            final GuiExceptionHandler exceptionHandler
    ) {
        return new InvestmentGuiModule(
                new CreateInvestmentProductHandler(investmentRepository, idGenerator, transactionManager, eventPublisher),
                new BuyProductHandler(
                        investmentRepository,
                        accountRepository,
                        businessRepository,
                        idGenerator,
                        transactionManager,
                        eventPublisher),
                new SellProductHandler(
                        investmentRepository,
                        accountRepository,
                        businessRepository,
                        idGenerator,
                        transactionManager,
                        eventPublisher),
                new ListHoldingsHandler(investmentRepository, accountRepository),
                exceptionHandler);
    }

    /**
     * @brief 注册关闭钩子释放数据源（Register Shutdown Hook To Close DataSource）；
     *        Register shutdown hook that closes datasource resources.
     *
     * @param dataSource 数据源（DataSource）。
     */
    private static void registerShutdownClose(final DataSource dataSource) {
        final DataSource normalizedDataSource = Objects.requireNonNull(dataSource, "dataSource must not be null");
        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> closeDataSourceQuietly(normalizedDataSource),
                "mini-core-bank-gui-shutdown"));
    }

    /**
     * @brief 安静关闭数据源（Close DataSource Quietly）；
     *        Close datasource without throwing from shutdown hook.
     *
     * @param dataSource 数据源（DataSource）。
     */
    private static void closeDataSourceQuietly(final DataSource dataSource) {
        try {
            closeDataSource(dataSource);
        } catch (Exception ignored) {
            // Shutdown hooks cannot report failures reliably.
        }
    }

    /**
     * @brief 关闭数据源（Close DataSource）；
     *        Close the datasource when it owns closeable resources.
     *
     * @param dataSource 数据源（DataSource）。
     */
    private static void closeDataSource(final DataSource dataSource) {
        if (dataSource instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception exception) {
                throw new IllegalStateException("Failed to close datasource", exception);
            }
        }
    }

    /**
     * @brief 仓储事务管理适配器（Repository Transaction Manager Adapter）；
     *        Transaction-manager adapter backed by the repository facade.
     */
    private static final class RepositoryTransactionManager implements DbTransactionManager {

        /**
         * @brief 仓储门面（Repository Facade）；
         *        Repository facade.
         */
        private final Repository repository;

        /**
         * @brief 构造适配器（Construct Adapter）；
         *        Construct repository-backed transaction manager.
         *
         * @param repository 仓储门面（Repository facade）。
         */
        private RepositoryTransactionManager(final Repository repository) {
            this.repository = Objects.requireNonNull(repository, "repository must not be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T> T execute(final Supplier<T> action) {
            return repository.writeInTransaction(action);
        }
    }
}
