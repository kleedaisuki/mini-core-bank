package com.moesegfault.banking.application.credit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.application.credit.command.GenerateStatementCommand;
import com.moesegfault.banking.application.credit.command.GenerateStatementHandler;
import com.moesegfault.banking.application.credit.command.RepayCreditCardCommand;
import com.moesegfault.banking.application.credit.command.RepayCreditCardHandler;
import com.moesegfault.banking.application.credit.query.FindStatementHandler;
import com.moesegfault.banking.application.credit.query.FindStatementQuery;
import com.moesegfault.banking.application.credit.result.CreditCardStatementResult;
import com.moesegfault.banking.application.credit.result.RepayCreditCardResult;
import com.moesegfault.banking.domain.credit.BillingCycle;
import com.moesegfault.banking.domain.credit.CreditCardAccount;
import com.moesegfault.banking.domain.credit.CreditCardAccountId;
import com.moesegfault.banking.domain.credit.CreditCardStatement;
import com.moesegfault.banking.domain.credit.CreditRepository;
import com.moesegfault.banking.domain.credit.InterestRate;
import com.moesegfault.banking.domain.credit.StatementId;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DateRange;
import com.moesegfault.banking.domain.shared.DomainEvent;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.domain.shared.Money;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.math.BigDecimal;
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
 * @brief Credit 应用层编排测试（Credit Application Orchestration Test），覆盖出账、还款与查询路径；
 *        Credit application orchestration tests covering statement generation, repayment, and query paths.
 */
class CreditApplicationHandlersTest {

    /**
     * @brief 验证出账处理器可生成账单并发布事件；
     *        Verify generate-statement handler creates statement and publishes event.
     */
    @Test
    void shouldGenerateStatementAndPublishEvent() {
        final InMemoryCreditRepository repository = new InMemoryCreditRepository();
        final CreditCardAccount account = openAccount("cc-acc-001", "1000.0000", 15, 25);
        account.authorizeCharge(usd("200.0000"));
        repository.saveCreditCardAccount(account);
        final EventCollector eventCollector = new EventCollector();

        final GenerateStatementHandler handler = new GenerateStatementHandler(
                repository,
                new DirectTransactionManager(),
                () -> "st-001",
                (creditCardAccount, statementPeriod, statementDate) -> creditCardAccount.creditLimit().usedAmount(),
                eventCollector);

        final CreditCardStatementResult result = handler.handle(new GenerateStatementCommand(
                "cc-acc-001",
                LocalDate.of(2026, 3, 31),
                new BigDecimal("0.100000"),
                new BigDecimal("50.0000")));

        assertEquals("st-001", result.statementId());
        assertEquals(new BigDecimal("200.0000"), result.totalAmountDue());
        assertEquals(new BigDecimal("50.0000"), result.minimumAmountDue());
        assertEquals("OPEN", result.statementStatus());
        assertEquals(1, eventCollector.events().size());
    }

    /**
     * @brief 验证同一账期重复出账会失败；
     *        Verify duplicate statement generation for same period fails.
     */
    @Test
    void shouldRejectDuplicateStatementPeriod() {
        final InMemoryCreditRepository repository = new InMemoryCreditRepository();
        final CreditCardAccount account = openAccount("cc-acc-002", "1200.0000", 15, 25);
        repository.saveCreditCardAccount(account);

        final LocalDate statementDate = LocalDate.of(2026, 3, 31);
        final DateRange period = account.billingCycle().deriveStatementPeriod(statementDate);
        repository.saveCreditCardStatement(CreditCardStatement.generate(
                StatementId.of("st-dup"),
                account.creditCardAccountId(),
                period,
                statementDate,
                account.billingCycle().resolvePaymentDueDate(statementDate),
                usd("0.0000"),
                usd("0.0000"),
                CurrencyCode.of("USD")));

        final GenerateStatementHandler handler = new GenerateStatementHandler(
                repository,
                new DirectTransactionManager(),
                () -> "st-new");

        assertThrows(IllegalStateException.class, () -> handler.handle(new GenerateStatementCommand(
                "cc-acc-002",
                statementDate,
                new BigDecimal("0.100000"),
                new BigDecimal("10.0000"))));
    }

    /**
     * @brief 验证自动还款分配会更新账单和账户额度；
     *        Verify automatic repayment allocation updates statements and account credit.
     */
    @Test
    void shouldRepayAndUpdateAccountAndStatements() {
        final InMemoryCreditRepository repository = new InMemoryCreditRepository();
        final CreditCardAccount account = openAccount("cc-acc-003", "1000.0000", 10, 20);
        account.authorizeCharge(usd("700.0000"));
        repository.saveCreditCardAccount(account);

        final CreditCardStatement first = CreditCardStatement.generate(
                StatementId.of("st-101"),
                account.creditCardAccountId(),
                DateRange.of(LocalDate.of(2026, 1, 11), LocalDate.of(2026, 2, 10)),
                LocalDate.of(2026, 2, 10),
                LocalDate.of(2026, 3, 20),
                usd("300.0000"),
                usd("30.0000"),
                CurrencyCode.of("USD"));
        final CreditCardStatement second = CreditCardStatement.generate(
                StatementId.of("st-102"),
                account.creditCardAccountId(),
                DateRange.of(LocalDate.of(2026, 2, 11), LocalDate.of(2026, 3, 10)),
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 4, 20),
                usd("400.0000"),
                usd("40.0000"),
                CurrencyCode.of("USD"));
        repository.saveCreditCardStatement(first);
        repository.saveCreditCardStatement(second);

        final EventCollector eventCollector = new EventCollector();
        final RepayCreditCardHandler handler = new RepayCreditCardHandler(
                repository,
                new DirectTransactionManager(),
                eventCollector,
                Clock.fixed(Instant.parse("2026-04-01T00:00:00Z"), ZoneOffset.UTC));

        final RepayCreditCardResult result = handler.handle(new RepayCreditCardCommand(
                "cc-acc-003",
                new BigDecimal("500.0000"),
                "USD",
                null,
                "sa-001",
                LocalDate.of(2026, 4, 1)));

        assertEquals(new BigDecimal("500.0000"), result.appliedToAccountAmount());
        assertEquals(new BigDecimal("500.0000"), result.appliedToStatementAmount());
        assertEquals(new BigDecimal("0.0000"), result.unappliedAmount());
        assertEquals(new BigDecimal("800.0000"), result.creditCardAccount().availableCredit());
        assertEquals(2, result.affectedStatements().size());
        assertEquals("PAID", result.affectedStatements().get(0).statementStatus());
        assertEquals("OPEN", result.affectedStatements().get(1).statementStatus());
        assertEquals(new BigDecimal("200.0000"), result.affectedStatements().get(1).paidAmount());
        assertEquals(1, eventCollector.events().size());
    }

    /**
     * @brief 验证账单查询支持按 ID 和按账期模式；
     *        Verify statement query supports by-ID and by-period modes.
     */
    @Test
    void shouldFindStatementByIdAndByPeriod() {
        final InMemoryCreditRepository repository = new InMemoryCreditRepository();
        final CreditCardAccount account = openAccount("cc-acc-004", "900.0000", 8, 18);
        repository.saveCreditCardAccount(account);

        final CreditCardStatement statement = CreditCardStatement.generate(
                StatementId.of("st-find"),
                account.creditCardAccountId(),
                DateRange.of(LocalDate.of(2026, 3, 9), LocalDate.of(2026, 4, 8)),
                LocalDate.of(2026, 4, 8),
                LocalDate.of(2026, 5, 18),
                usd("120.0000"),
                usd("20.0000"),
                CurrencyCode.of("USD"));
        repository.saveCreditCardStatement(statement);

        final FindStatementHandler handler = new FindStatementHandler(repository);
        final Optional<CreditCardStatementResult> byId = handler.handle(FindStatementQuery.byStatementId("st-find"));
        final Optional<CreditCardStatementResult> byPeriod = handler.handle(FindStatementQuery.byPeriod(
                "cc-acc-004",
                LocalDate.of(2026, 3, 9),
                LocalDate.of(2026, 4, 8)));

        assertTrue(byId.isPresent());
        assertTrue(byPeriod.isPresent());
        assertEquals("st-find", byId.orElseThrow().statementId());
        assertEquals("st-find", byPeriod.orElseThrow().statementId());
        assertFalse(handler.handle(FindStatementQuery.byStatementId("st-missing")).isPresent());
    }

    /**
     * @brief 打开信用卡账户辅助方法（Open Credit Card Account Helper）；
     *        Helper to open credit-card account.
     *
     * @param accountId 账户 ID（Account ID）。
     * @param totalLimit 总额度（Total limit）。
     * @param billingDay 账单日（Billing day）。
     * @param dueDay 还款日（Due day）。
     * @return 信用卡账户实体（Credit-card-account entity）。
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
     * @brief 内存版信用仓储（In-memory Credit Repository），用于应用层编排测试；
     *        In-memory credit repository for application orchestration tests.
     */
    private static final class InMemoryCreditRepository implements CreditRepository {

        /**
         * @brief 账户存储（Account Storage）；
         *        Credit-card-account storage.
         */
        private final Map<String, CreditCardAccount> accounts = new HashMap<>();

        /**
         * @brief 账单存储（Statement Storage）；
         *        Credit-card-statement storage.
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

    /**
     * @brief 直接事务管理器（Direct Transaction Manager），用于测试中透明执行；
     *        Direct transaction manager executing actions transparently in tests.
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
     * @brief 事件收集器（Event Collector），用于断言事件发布次数；
     *        Event collector for asserting publication count.
     */
    private static final class EventCollector implements DomainEventPublisher {

        /**
         * @brief 事件存储（Event Storage）；
         *        Published event storage.
         */
        private final List<DomainEvent> events = new ArrayList<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void publish(final DomainEvent event) {
            events.add(event);
        }

        /**
         * @brief 返回已发布事件列表（Return Published Events）；
         *        Return published events.
         *
         * @return 事件列表（Event list）。
         */
        public List<DomainEvent> events() {
            assertNotNull(events);
            return events;
        }
    }
}
