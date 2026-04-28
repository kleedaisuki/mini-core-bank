package com.moesegfault.banking.presentation.web.credit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moesegfault.banking.application.credit.command.GenerateStatementHandler;
import com.moesegfault.banking.application.credit.command.RepayCreditCardHandler;
import com.moesegfault.banking.application.credit.query.FindStatementHandler;
import com.moesegfault.banking.domain.credit.BillingCycle;
import com.moesegfault.banking.domain.credit.CreditCardAccount;
import com.moesegfault.banking.domain.credit.CreditCardAccountId;
import com.moesegfault.banking.domain.credit.CreditCardStatement;
import com.moesegfault.banking.domain.credit.CreditRepository;
import com.moesegfault.banking.domain.credit.InterestRate;
import com.moesegfault.banking.domain.credit.StatementId;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DateRange;
import com.moesegfault.banking.domain.shared.Money;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import com.moesegfault.banking.presentation.web.WebRequest;
import com.moesegfault.banking.presentation.web.WebResponse;
import com.moesegfault.banking.presentation.web.WebRouteHandler;
import com.moesegfault.banking.presentation.web.WebRuntime;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * @brief Credit Web 展示层测试（Credit Web Presentation Test），验证 credit 子领域 REST 适配行为；
 *        Credit web-presentation tests verifying REST adaptation behavior for credit subdomain.
 */
class CreditWebPresentationTest {

    /**
     * @brief JSON 解析器（JSON ObjectMapper）；
     *        Object mapper for response assertions.
     */
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    /**
     * @brief 验证生成账单接口返回 201 与规范字段；
     *        Verify generate-statement endpoint returns HTTP 201 and canonical fields.
     *
     * @throws Exception JSON 解析异常（JSON parsing exception）。
     */
    @Test
    void shouldGenerateStatementWithCanonicalSchema() throws Exception {
        final InMemoryCreditRepository repository = new InMemoryCreditRepository();
        final CreditCardAccount creditCardAccount = openAccount("cc-acc-web-001", "1000.0000", 15, 25);
        creditCardAccount.authorizeCharge(usd("220.0000"));
        repository.saveCreditCardAccount(creditCardAccount);

        final CreditController controller = createController(repository, () -> "st-web-001");
        final FakeRequest request = new FakeRequest(
                "POST",
                "/credit/statements",
                Map.of(),
                Map.of(),
                """
                        {
                          "credit_card_account_id": "cc-acc-web-001",
                          "statement_date": "2026-03-31",
                          "minimum_payment_rate_decimal": 0.10,
                          "minimum_payment_floor_amount": 50.0000
                        }
                        """);

        final WebResponse response = controller.generateStatement(request);

        assertEquals(201, response.statusCode());
        final Map<String, Object> payload = decodeJson(response);
        assertEquals("st-web-001", payload.get("statement_id"));
        assertEquals("cc-acc-web-001", payload.get("credit_card_account_id"));
        assertEquals("OPEN", payload.get("statement_status"));
        assertEquals("USD", payload.get("currency_code"));
    }

    /**
     * @brief 验证还款接口返回分配结果与受影响账单；
     *        Verify repayment endpoint returns allocation summary and affected statements.
     *
     * @throws Exception JSON 解析异常（JSON parsing exception）。
     */
    @Test
    void shouldRepayCreditCardAndReturnAllocationSnapshot() throws Exception {
        final InMemoryCreditRepository repository = new InMemoryCreditRepository();
        final CreditCardAccount creditCardAccount = openAccount("cc-acc-web-002", "1000.0000", 10, 20);
        creditCardAccount.authorizeCharge(usd("700.0000"));
        repository.saveCreditCardAccount(creditCardAccount);

        repository.saveCreditCardStatement(CreditCardStatement.generate(
                StatementId.of("st-web-101"),
                creditCardAccount.creditCardAccountId(),
                DateRange.of(LocalDate.of(2026, 1, 11), LocalDate.of(2026, 2, 10)),
                LocalDate.of(2026, 2, 10),
                LocalDate.of(2026, 3, 20),
                usd("300.0000"),
                usd("30.0000"),
                CurrencyCode.of("USD")));
        repository.saveCreditCardStatement(CreditCardStatement.generate(
                StatementId.of("st-web-102"),
                creditCardAccount.creditCardAccountId(),
                DateRange.of(LocalDate.of(2026, 2, 11), LocalDate.of(2026, 3, 10)),
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 4, 20),
                usd("400.0000"),
                usd("40.0000"),
                CurrencyCode.of("USD")));

        final CreditController controller = createControllerWithFixedClock(
                repository,
                () -> "ignored-statement-id",
                Clock.fixed(Instant.parse("2026-04-01T00:00:00Z"), ZoneOffset.UTC));
        final FakeRequest request = new FakeRequest(
                "POST",
                "/credit/repayments",
                Map.of(),
                Map.of(),
                """
                        {
                          "credit_card_account_id": "cc-acc-web-002",
                          "repayment_amount": 500.0000,
                          "repayment_currency_code": "USD",
                          "as_of_date": "2026-04-01"
                        }
                        """);

        final WebResponse response = controller.repayCreditCard(request);

        assertEquals(200, response.statusCode());
        final Map<String, Object> payload = decodeJson(response);
        assertEquals(0, new BigDecimal("500.0000").compareTo(new BigDecimal(payload.get("applied_to_account_amount").toString())));
        assertEquals(0, new BigDecimal("500.0000").compareTo(new BigDecimal(payload.get("applied_to_statement_amount").toString())));
        assertEquals(0, new BigDecimal("0.0000").compareTo(new BigDecimal(payload.get("unapplied_amount").toString())));
        assertTrue(payload.containsKey("credit_card_account"));
        assertTrue(payload.containsKey("affected_statements"));
    }

    /**
     * @brief 验证按账单 ID 查询不到时抛出 NotFound 异常；
     *        Verify by-ID statement query throws not-found exception when statement is absent.
     */
    @Test
    void shouldThrowNotFoundWhenStatementByIdIsMissing() {
        final InMemoryCreditRepository repository = new InMemoryCreditRepository();
        final CreditController controller = createController(repository, () -> "ignored");
        final FakeRequest request = new FakeRequest(
                "GET",
                "/credit/statements/st-not-found",
                Map.of("statementId", "st-not-found"),
                Map.of(),
                "");

        assertThrows(CreditStatementNotFoundException.class, () -> controller.findStatementById(request));
    }

    /**
     * @brief 验证路由注册路径集合；
     *        Verify route registrar registers expected route set.
     */
    @Test
    void shouldRegisterExpectedCreditRoutes() {
        final InMemoryCreditRepository repository = new InMemoryCreditRepository();
        final CreditController controller = createController(repository, () -> "ignored");
        final CreditRoutes routes = new CreditRoutes(controller);
        final RecordingRuntime runtime = new RecordingRuntime();

        routes.registerRoutes(runtime);

        assertEquals(4, runtime.routes().size());
        assertTrue(runtime.contains("POST", "/credit/statements"));
        assertTrue(runtime.contains("GET", "/credit/statements"));
        assertTrue(runtime.contains("GET", "/credit/statements/{statementId}"));
        assertTrue(runtime.contains("POST", "/credit/repayments"));
    }

    /**
     * @brief 创建控制器（Create Controller）；
     *        Create controller with deterministic dependencies.
     *
     * @param repository 信用仓储（Credit repository）。
     * @param idGenerator ID 生成器（ID generator）。
     * @return 控制器（Controller）。
     */
    private static CreditController createController(
            final InMemoryCreditRepository repository,
            final com.moesegfault.banking.infrastructure.id.IdGenerator idGenerator
    ) {
        return createControllerWithFixedClock(repository, idGenerator, Clock.systemUTC());
    }

    /**
     * @brief 创建带固定时钟的控制器（Create Controller with Fixed Clock）；
     *        Create controller with explicit clock for deterministic repayment behavior.
     *
     * @param repository 信用仓储（Credit repository）。
     * @param idGenerator ID 生成器（ID generator）。
     * @param clock 系统时钟（System clock）。
     * @return 控制器（Controller）。
     */
    private static CreditController createControllerWithFixedClock(
            final InMemoryCreditRepository repository,
            final com.moesegfault.banking.infrastructure.id.IdGenerator idGenerator,
            final Clock clock
    ) {
        final DbTransactionManager transactionManager = new DirectTransactionManager();
        return new CreditController(
                new GenerateStatementHandler(repository, transactionManager, idGenerator),
                new RepayCreditCardHandler(repository, transactionManager, com.moesegfault.banking.domain.shared.DomainEventPublisher.noop(), clock),
                new FindStatementHandler(repository));
    }

    /**
     * @brief 打开信用卡账户（Open Credit Card Account）；
     *        Open one credit-card account for tests.
     *
     * @param accountId 账户 ID（Account ID）。
     * @param totalLimit 总额度（Total limit）。
     * @param billingDay 账单日（Billing day）。
     * @param dueDay 还款日（Due day）。
     * @return 信用卡账户（Credit-card account）。
     */
    private static CreditCardAccount openAccount(
            final String accountId,
            final String totalLimit,
            final int billingDay,
            final int dueDay
    ) {
        return CreditCardAccount.open(
                CreditCardAccountId.of(accountId),
                usd(totalLimit),
                BillingCycle.of(billingDay, dueDay),
                InterestRate.ofDecimal(new BigDecimal("0.015000")),
                usd("300.0000"),
                CurrencyCode.of("USD"));
    }

    /**
     * @brief 创建 USD 金额（Create USD Money）；
     *        Create USD money helper.
     *
     * @param amount 金额字符串（Amount string）。
     * @return USD 金额（USD money）。
     */
    private static Money usd(final String amount) {
        return Money.of(CurrencyCode.of("USD"), new BigDecimal(amount));
    }

    /**
     * @brief 解析 JSON 响应（Decode JSON Response）；
     *        Decode JSON body from web response.
     *
     * @param response Web 响应（Web response）。
     * @return JSON 键值映射（JSON map）。
     * @throws Exception JSON 解析异常（JSON parsing exception）。
     */
    private Map<String, Object> decodeJson(final WebResponse response) throws Exception {
        return objectMapper.readValue(
                new String(response.body(), StandardCharsets.UTF_8),
                new TypeReference<>() {
                });
    }

    /**
     * @brief 假 Web 请求（Fake Web Request），用于控制器单元测试；
     *        Fake web request used by controller unit tests.
     */
    private record FakeRequest(
            String method,
            String path,
            Map<String, String> pathParams,
            Map<String, String> queryParams,
            String body
    ) implements WebRequest {

        /**
         * @brief 构造并复制入参（Construct and Copy Inputs）；
         *        Construct fake request and copy collections.
         */
        private FakeRequest {
            pathParams = Map.copyOf(pathParams);
            queryParams = Map.copyOf(queryParams);
            body = body == null ? "" : body;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<String> pathParam(final String name) {
            return Optional.ofNullable(pathParams.get(name));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<String> queryParam(final String name) {
            return Optional.ofNullable(queryParams.get(name));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<String> header(final String name) {
            return Optional.empty();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<String> headers(final String name) {
            return List.of();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public byte[] bodyBytes() {
            return body.getBytes(StandardCharsets.UTF_8);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String bodyText() {
            return body;
        }
    }

    /**
     * @brief 记录型 Runtime（Recording Runtime），用于验证路由注册结果；
     *        Recording runtime for asserting registered route set.
     */
    private static final class RecordingRuntime implements WebRuntime {

        /**
         * @brief 已注册路由列表（Registered Route List）；
         *        Registered route list.
         */
        private final List<RouteEntry> routes = new ArrayList<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void addRoute(final String method, final String pathPattern, final WebRouteHandler handler) {
            routes.add(new RouteEntry(method, pathPattern));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void start() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void stop() {
        }

        /**
         * @brief 获取路由快照（Get Route Snapshot）；
         *        Get immutable route snapshot.
         *
         * @return 路由快照（Route snapshot）。
         */
        public List<RouteEntry> routes() {
            return List.copyOf(routes);
        }

        /**
         * @brief 判断是否包含指定路由（Check Route Existence）；
         *        Check whether route list contains specific method/path pair.
         *
         * @param method HTTP 方法（HTTP method）。
         * @param path 路径模式（Path pattern）。
         * @return 是否存在（Whether exists）。
         */
        public boolean contains(final String method, final String path) {
            return routes.contains(new RouteEntry(method, path));
        }
    }

    /**
     * @brief 路由条目（Route Entry）；
     *        Simple route entry record.
     *
     * @param method HTTP 方法（HTTP method）。
     * @param path 路径模式（Path pattern）。
     */
    private record RouteEntry(String method, String path) {
    }

    /**
     * @brief 直接事务管理器（Direct Transaction Manager），测试中直接执行事务体；
     *        Direct transaction manager executing supplied action immediately in tests.
     */
    private static final class DirectTransactionManager implements DbTransactionManager {

        /**
         * {@inheritDoc}
         */
        @Override
        public <T> T execute(final java.util.function.Supplier<T> action) {
            return action.get();
        }
    }

    /**
     * @brief 内存信用仓储（In-memory Credit Repository），用于展示层测试；
     *        In-memory credit repository for presentation-layer tests.
     */
    private static final class InMemoryCreditRepository implements CreditRepository {

        /**
         * @brief 账户存储（Account Storage）；
         *        Credit-card-account storage.
         */
        private final Map<String, CreditCardAccount> accounts = new HashMap<>();

        /**
         * @brief 账单存储（Statement Storage）；
         *        Statement storage.
         */
        private final Map<String, CreditCardStatement> statements = new HashMap<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void saveCreditCardAccount(final CreditCardAccount creditCardAccount) {
            accounts.put(creditCardAccount.creditCardAccountId().value(), creditCardAccount);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void saveCreditCardStatement(final CreditCardStatement creditCardStatement) {
            statements.put(creditCardStatement.statementId().value(), creditCardStatement);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<CreditCardAccount> findCreditCardAccountById(final CreditCardAccountId creditCardAccountId) {
            return Optional.ofNullable(accounts.get(creditCardAccountId.value()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<CreditCardStatement> findStatementById(final StatementId statementId) {
            return Optional.ofNullable(statements.get(statementId.value()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<CreditCardStatement> findStatementByPeriod(
                final CreditCardAccountId creditCardAccountId,
                final DateRange statementPeriod
        ) {
            return statements.values().stream()
                    .filter(statement -> statement.creditCardAccountId().equals(creditCardAccountId))
                    .filter(statement -> statement.statementPeriod().equals(statementPeriod))
                    .findFirst();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<CreditCardStatement> listRepayableStatementsByAccountId(final CreditCardAccountId creditCardAccountId) {
            return statements.values().stream()
                    .filter(statement -> statement.creditCardAccountId().equals(creditCardAccountId))
                    .filter(statement -> statement.statementStatus().canAcceptRepayment())
                    .toList();
        }
    }
}
