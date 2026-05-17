package com.moesegfault.banking.presentation.cli;

import com.moesegfault.banking.application.account.command.FreezeAccountHandler;
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
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.migration.FlywayMigrationRunner;
import com.moesegfault.banking.infrastructure.persistence.Repository;
import com.moesegfault.banking.infrastructure.persistence.jdbc.JdbcRepository;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import com.moesegfault.banking.presentation.cli.account.AccountCliCommandRegistration;
import com.moesegfault.banking.presentation.cli.account.FreezeAccountCliHandler;
import com.moesegfault.banking.presentation.cli.account.ListAccountsCliHandler;
import com.moesegfault.banking.presentation.cli.account.OpenFxAccountCliHandler;
import com.moesegfault.banking.presentation.cli.account.OpenInvestmentAccountCliHandler;
import com.moesegfault.banking.presentation.cli.account.OpenSavingsAccountCliHandler;
import com.moesegfault.banking.presentation.cli.account.ShowAccountCliHandler;
import com.moesegfault.banking.presentation.cli.business.BusinessCliCommandRegistration;
import com.moesegfault.banking.presentation.cli.business.ListBusinessTransactionsCliHandler;
import com.moesegfault.banking.presentation.cli.business.ShowBusinessTransactionCliHandler;
import com.moesegfault.banking.presentation.cli.card.CardCliCommandRegistration;
import com.moesegfault.banking.presentation.cli.card.IssueCreditCardCliHandler;
import com.moesegfault.banking.presentation.cli.card.IssueDebitCardCliHandler;
import com.moesegfault.banking.presentation.cli.card.IssueSupplementaryCreditCardCliHandler;
import com.moesegfault.banking.presentation.cli.card.IssueSupplementaryDebitCardCliHandler;
import com.moesegfault.banking.presentation.cli.card.ShowCardCliHandler;
import com.moesegfault.banking.presentation.cli.credit.CreditCliCommandRegistration;
import com.moesegfault.banking.presentation.cli.credit.GenerateStatementCliHandler;
import com.moesegfault.banking.presentation.cli.credit.RepayCreditCardCliHandler;
import com.moesegfault.banking.presentation.cli.credit.ShowStatementCliHandler;
import com.moesegfault.banking.presentation.cli.customer.CustomerCliCommandRegistration;
import com.moesegfault.banking.presentation.cli.customer.ListCustomersCliHandler;
import com.moesegfault.banking.presentation.cli.customer.RegisterCustomerCliHandler;
import com.moesegfault.banking.presentation.cli.customer.ShowCustomerCliHandler;
import com.moesegfault.banking.presentation.cli.investment.BuyProductCliHandler;
import com.moesegfault.banking.presentation.cli.investment.CreateProductCliHandler;
import com.moesegfault.banking.presentation.cli.investment.InvestmentCliCommandRegistration;
import com.moesegfault.banking.presentation.cli.investment.SellProductCliHandler;
import com.moesegfault.banking.presentation.cli.investment.ShowHoldingCliHandler;
import com.moesegfault.banking.presentation.cli.ledger.LedgerCliCommandRegistration;
import com.moesegfault.banking.presentation.cli.ledger.ShowBalanceCliHandler;
import com.moesegfault.banking.presentation.cli.ledger.ShowEntriesCliHandler;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import javax.sql.DataSource;

/**
 * @brief CLI 主入口（CLI Main Entry Point），装配基础设施、应用服务与命令分发；
 *        CLI main entry point that wires infrastructure, application services, and command dispatch.
 */
public final class Main {

    /**
     * @brief 成功退出码（Success Exit Code）；
     *        Exit code for successful execution.
     */
    private static final int EXIT_SUCCESS = 0;

    /**
     * @brief 用法错误退出码（Usage Error Exit Code）；
     *        Exit code for invalid CLI usage.
     */
    private static final int EXIT_USAGE_ERROR = 2;

    /**
     * @brief 运行时错误退出码（Runtime Error Exit Code）；
     *        Exit code for runtime failures.
     */
    private static final int EXIT_RUNTIME_ERROR = 1;

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility entry point.
     */
    private Main() {
    }

    /**
     * @brief Java 进程入口（Java Process Entry Point）；
     *        Java process entry point.
     *
     * @param args 命令行参数（Command-line arguments）。
     */
    public static void main(final String[] args) {
        final int exitCode = run(args, System.out, System.err);
        if (exitCode != EXIT_SUCCESS) {
            System.exit(exitCode);
        }
    }

    /**
     * @brief 执行 CLI 命令（Run CLI Command）；
     *        Run one CLI command and return a process-style exit code.
     *
     * @param args   命令行参数（Command-line arguments）。
     * @param output 标准输出流（Standard output stream）。
     * @param error  标准错误流（Standard error stream）。
     * @return 退出码（Exit code）。
     */
    public static int run(final String[] args, final PrintStream output, final PrintStream error) {
        final String[] normalizedArgs = Objects.requireNonNull(args, "args must not be null").clone();
        final PrintStream normalizedOutput = Objects.requireNonNull(output, "output must not be null");
        final PrintStream normalizedError = Objects.requireNonNull(error, "error must not be null");
        final CommandRegistry commandRegistry = createCommandRegistry();

        if (normalizedArgs.length == 0 || isHelpRequest(normalizedArgs)) {
            printUsage(normalizedOutput, commandRegistry);
            return normalizedArgs.length == 0 ? EXIT_USAGE_ERROR : EXIT_SUCCESS;
        }

        try {
            final AppConfig appConfig = AppConfig.createDefault();
            try {
                new FlywayMigrationRunner().migrate(appConfig);
                final Repository repository = new JdbcRepository(appConfig.dataSource());
                final Map<Class<? extends CliCommandHandler>, CliCommandHandler> handlers = createHandlers(
                        repository,
                        appConfig.idGenerator());
                final CommandDispatcher dispatcher = new CommandDispatcher(commandRegistry, handlers::get);
                dispatcher.dispatch(new CommandParser().parse(toRawInput(normalizedArgs)));
                return EXIT_SUCCESS;
            } finally {
                closeDataSource(appConfig.dataSource(), normalizedError);
            }
        } catch (IllegalArgumentException exception) {
            normalizedError.println(exception.getMessage());
            return EXIT_USAGE_ERROR;
        } catch (Exception exception) {
            normalizedError.println("CLI command failed: " + exception.getMessage());
            return EXIT_RUNTIME_ERROR;
        }
    }

    /**
     * @brief 创建命令注册表（Create Command Registry）；
     *        Create and populate the CLI command registry.
     *
     * @return 已注册命令表（Registered command registry）。
     */
    private static CommandRegistry createCommandRegistry() {
        final CommandRegistry registry = new CommandRegistry();
        CustomerCliCommandRegistration.register(registry);
        AccountCliCommandRegistration.register(registry);
        CardCliCommandRegistration.register(registry);
        CreditCliCommandRegistration.register(registry);
        LedgerCliCommandRegistration.register(registry);
        BusinessCliCommandRegistration.register(registry);
        InvestmentCliCommandRegistration.register(registry);
        return registry;
    }

    /**
     * @brief 创建 CLI Handler 映射（Create CLI Handler Map）；
     *        Create CLI handler instances keyed by handler type.
     *
     * @param repository  仓储门面（Repository facade）。
     * @param idGenerator ID 生成器（ID generator）。
     * @return CLI handler 映射（CLI handler map）。
     */
    private static Map<Class<? extends CliCommandHandler>, CliCommandHandler> createHandlers(
            final Repository repository,
            final IdGenerator idGenerator
    ) {
        final Repository normalizedRepository = Objects.requireNonNull(repository, "repository must not be null");
        final IdGenerator normalizedIdGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
        final DbTransactionManager transactionManager = new RepositoryTransactionManager(normalizedRepository);
        final DomainEventPublisher eventPublisher = DomainEventPublisher.noop();

        final CustomerRepository customerRepository = normalizedRepository.require(CustomerRepository.class);
        final AccountRepository accountRepository = normalizedRepository.require(AccountRepository.class);
        final CardRepository cardRepository = normalizedRepository.require(CardRepository.class);
        final CreditRepository creditRepository = normalizedRepository.require(CreditRepository.class);
        final InvestmentRepository investmentRepository = normalizedRepository.require(InvestmentRepository.class);
        final BusinessRepository businessRepository = normalizedRepository.require(BusinessRepository.class);
        final LedgerRepository ledgerRepository = normalizedRepository.require(LedgerRepository.class);

        final Map<Class<? extends CliCommandHandler>, CliCommandHandler> handlers = new LinkedHashMap<>();
        putHandler(handlers, RegisterCustomerCliHandler.class, new RegisterCustomerCliHandler(
                new RegisterCustomerHandler(customerRepository, transactionManager, normalizedIdGenerator, eventPublisher)));
        putHandler(handlers, ShowCustomerCliHandler.class, new ShowCustomerCliHandler(
                new FindCustomerHandler(customerRepository)));
        putHandler(handlers, ListCustomersCliHandler.class, new ListCustomersCliHandler(
                new ListCustomersHandler(customerRepository)));

        putHandler(handlers, OpenSavingsAccountCliHandler.class, new OpenSavingsAccountCliHandler(
                new OpenSavingsAccountHandler(
                        accountRepository,
                        customerRepository,
                        transactionManager,
                        normalizedIdGenerator,
                        eventPublisher)));
        putHandler(handlers, OpenFxAccountCliHandler.class, new OpenFxAccountCliHandler(
                new OpenFxAccountHandler(
                        accountRepository,
                        customerRepository,
                        transactionManager,
                        normalizedIdGenerator,
                        eventPublisher)));
        putHandler(handlers, OpenInvestmentAccountCliHandler.class, new OpenInvestmentAccountCliHandler(
                new OpenInvestmentAccountHandler(
                        accountRepository,
                        customerRepository,
                        transactionManager,
                        normalizedIdGenerator,
                        eventPublisher)));
        putHandler(handlers, ShowAccountCliHandler.class, new ShowAccountCliHandler(
                new FindAccountHandler(accountRepository)));
        putHandler(handlers, ListAccountsCliHandler.class, new ListAccountsCliHandler(
                new ListCustomerAccountsHandler(accountRepository)));
        putHandler(handlers, FreezeAccountCliHandler.class, new FreezeAccountCliHandler(
                new FreezeAccountHandler(accountRepository, transactionManager)));

        putHandler(handlers, IssueDebitCardCliHandler.class, new IssueDebitCardCliHandler(new IssueDebitCardHandler(
                cardRepository,
                accountRepository,
                customerRepository,
                normalizedIdGenerator,
                transactionManager,
                eventPublisher)));
        putHandler(handlers, IssueCreditCardCliHandler.class, new IssueCreditCardCliHandler(new IssueCreditCardHandler(
                cardRepository,
                accountRepository,
                customerRepository,
                creditRepository,
                normalizedIdGenerator,
                transactionManager,
                eventPublisher)));
        putHandler(handlers, IssueSupplementaryDebitCardCliHandler.class, new IssueSupplementaryDebitCardCliHandler(
                new IssueSupplementaryDebitCardHandler(
                        cardRepository,
                        customerRepository,
                        normalizedIdGenerator,
                        transactionManager,
                        eventPublisher)));
        putHandler(handlers, IssueSupplementaryCreditCardCliHandler.class, new IssueSupplementaryCreditCardCliHandler(
                new IssueSupplementaryCreditCardHandler(
                        cardRepository,
                        accountRepository,
                        customerRepository,
                        creditRepository,
                        normalizedIdGenerator,
                        transactionManager,
                        eventPublisher)));
        putHandler(handlers, ShowCardCliHandler.class, new ShowCardCliHandler(new FindCardHandler(cardRepository)));

        putHandler(handlers, GenerateStatementCliHandler.class, new GenerateStatementCliHandler(
                new GenerateStatementHandler(
                        creditRepository,
                        transactionManager,
                        normalizedIdGenerator,
                        new UsedCreditStatementAmountCalculator(),
                        eventPublisher)));
        putHandler(handlers, RepayCreditCardCliHandler.class, new RepayCreditCardCliHandler(
                new RepayCreditCardHandler(creditRepository, transactionManager)));
        putHandler(handlers, ShowStatementCliHandler.class, new ShowStatementCliHandler(
                new FindStatementHandler(creditRepository)));

        putHandler(handlers, ShowBalanceCliHandler.class, new ShowBalanceCliHandler(
                new FindBalanceHandler(ledgerRepository)));
        putHandler(handlers, ShowEntriesCliHandler.class, new ShowEntriesCliHandler(
                new ListLedgerEntriesHandler(ledgerRepository)));

        putHandler(handlers, ListBusinessTransactionsCliHandler.class, new ListBusinessTransactionsCliHandler(
                new ListBusinessTransactionsHandler(businessRepository)));
        putHandler(handlers, ShowBusinessTransactionCliHandler.class, new ShowBusinessTransactionCliHandler(
                new FindBusinessTransactionHandler(businessRepository)));

        putHandler(handlers, CreateProductCliHandler.class, new CreateProductCliHandler(
                new CreateInvestmentProductHandler(
                        investmentRepository,
                        normalizedIdGenerator,
                        transactionManager,
                        eventPublisher)));
        putHandler(handlers, BuyProductCliHandler.class, new BuyProductCliHandler(new BuyProductHandler(
                investmentRepository,
                accountRepository,
                businessRepository,
                normalizedIdGenerator,
                transactionManager,
                eventPublisher)));
        putHandler(handlers, SellProductCliHandler.class, new SellProductCliHandler(new SellProductHandler(
                investmentRepository,
                accountRepository,
                businessRepository,
                normalizedIdGenerator,
                transactionManager,
                eventPublisher)));
        putHandler(handlers, ShowHoldingCliHandler.class, new ShowHoldingCliHandler(
                new ListHoldingsHandler(investmentRepository, accountRepository)));
        return Map.copyOf(handlers);
    }

    /**
     * @brief 写入类型安全 Handler 映射（Put Type-safe Handler Mapping）；
     *        Put a handler instance into the map with its concrete handler type.
     *
     * @param <T>      CLI handler 类型（CLI handler type）。
     * @param handlers Handler 映射（Handler map）。
     * @param type     Handler 类型（Handler class type）。
     * @param handler  Handler 实例（Handler instance）。
     */
    private static <T extends CliCommandHandler> void putHandler(
            final Map<Class<? extends CliCommandHandler>, CliCommandHandler> handlers,
            final Class<T> type,
            final T handler
    ) {
        handlers.put(
                Objects.requireNonNull(type, "type must not be null"),
                Objects.requireNonNull(handler, "handler must not be null"));
    }

    /**
     * @brief 判断是否帮助请求（Check Help Request）；
     *        Check whether arguments request usage help.
     *
     * @param args 命令行参数（Command-line arguments）。
     * @return true 表示帮助请求（true when help is requested）。
     */
    private static boolean isHelpRequest(final String[] args) {
        return args.length == 1 && ("--help".equals(args[0]) || "-h".equals(args[0]) || "help".equals(args[0]));
    }

    /**
     * @brief 打印用法（Print Usage）；
     *        Print CLI usage and registered command paths.
     *
     * @param output   输出流（Output stream）。
     * @param registry 命令注册表（Command registry）。
     */
    private static void printUsage(final PrintStream output, final CommandRegistry registry) {
        output.println("Usage: java -jar mini-core-bank.jar <command> [--option value]");
        output.println("Commands:");
        registry.registeredCommandPaths().forEach(commandPath -> output.println("  " + commandPath));
    }

    /**
     * @brief 转换 Java 参数为解析器输入（Convert Java Args to Parser Input）；
     *        Convert Java argument array into command-parser input text.
     *
     * @param args 命令行参数（Command-line arguments）。
     * @return 命令解析器输入文本（Command-parser input text）。
     */
    private static String toRawInput(final String[] args) {
        if (args.length == 1) {
            return args[0];
        }
        return Arrays.stream(args)
                .map(Main::quoteIfNeeded)
                .reduce((left, right) -> left + " " + right)
                .orElse("");
    }

    /**
     * @brief 按需引用参数（Quote Argument if Needed）；
     *        Quote one argument when it contains parser-significant whitespace.
     *
     * @param argument 原始参数（Raw argument）。
     * @return 可安全拼接的参数文本（Safely joinable argument text）。
     */
    private static String quoteIfNeeded(final String argument) {
        final String normalizedArgument = Objects.requireNonNull(argument, "argument must not be null");
        if (normalizedArgument.chars().noneMatch(Character::isWhitespace)) {
            return normalizedArgument;
        }
        return "'" + normalizedArgument.replace("'", "\\'") + "'";
    }

    /**
     * @brief 关闭数据源（Close DataSource）；
     *        Close the datasource when it owns closeable resources.
     *
     * @param dataSource 数据源（DataSource）。
     * @param error      标准错误流（Standard error stream）。
     */
    private static void closeDataSource(final DataSource dataSource, final PrintStream error) {
        if (dataSource instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception exception) {
                error.printf(Locale.ROOT, "Failed to close datasource: %s%n", exception.getMessage());
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
         * @brief 在仓储写事务中执行（Execute in Repository Write Transaction）；
         *        Execute action inside repository-managed write transaction.
         *
         * @param <T>    返回类型（Result type）。
         * @param action 事务动作（Transactional action）。
         * @return 执行结果（Execution result）。
         */
        @Override
        public <T> T execute(final Supplier<T> action) {
            return repository.writeInTransaction(action);
        }
    }
}
