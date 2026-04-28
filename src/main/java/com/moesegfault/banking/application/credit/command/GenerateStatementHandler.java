package com.moesegfault.banking.application.credit.command;

import com.moesegfault.banking.application.credit.port.StatementAmountCalculator;
import com.moesegfault.banking.application.credit.result.CreditCardStatementResult;
import com.moesegfault.banking.application.credit.service.UsedCreditStatementAmountCalculator;
import com.moesegfault.banking.domain.credit.CreditCardAccount;
import com.moesegfault.banking.domain.credit.CreditCardAccountId;
import com.moesegfault.banking.domain.credit.CreditCardStatement;
import com.moesegfault.banking.domain.credit.CreditRepository;
import com.moesegfault.banking.domain.credit.MinimumPaymentPolicy;
import com.moesegfault.banking.domain.credit.StatementId;
import com.moesegfault.banking.domain.shared.DateRange;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.domain.shared.Money;
import com.moesegfault.banking.domain.shared.Percentage;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.util.Objects;

/**
 * @brief 生成账单处理器（Generate Statement Handler），负责编排账单生成事务、仓储写入与事件发布；
 *        Generate-statement handler orchestrating transaction boundary, repository persistence, and event publication.
 */
public final class GenerateStatementHandler {

    /**
     * @brief 信用仓储接口（Credit Repository）；
     *        Credit repository contract.
     */
    private final CreditRepository creditRepository;

    /**
     * @brief 事务管理器（Database Transaction Manager）；
     *        Database transaction manager.
     */
    private final DbTransactionManager transactionManager;

    /**
     * @brief ID 生成器（ID Generator）；
     *        Identifier generator.
     */
    private final IdGenerator idGenerator;

    /**
     * @brief 账单金额计算端口（Statement Amount Calculator）；
     *        Statement-amount calculator port.
     */
    private final StatementAmountCalculator statementAmountCalculator;

    /**
     * @brief 领域事件发布器（Domain Event Publisher）；
     *        Domain event publisher.
     */
    private final DomainEventPublisher domainEventPublisher;

    /**
     * @brief 构造生成账单处理器（Construct Generate Statement Handler）；
     *        Construct generate-statement handler.
     *
     * @param creditRepository 信用仓储接口（Credit repository）。
     * @param transactionManager 事务管理器（Database transaction manager）。
     * @param idGenerator ID 生成器（ID generator）。
     */
    public GenerateStatementHandler(
            final CreditRepository creditRepository,
            final DbTransactionManager transactionManager,
            final IdGenerator idGenerator
    ) {
        this(
                creditRepository,
                transactionManager,
                idGenerator,
                new UsedCreditStatementAmountCalculator(),
                DomainEventPublisher.noop());
    }

    /**
     * @brief 构造生成账单处理器（完整依赖）（Construct Generate Statement Handler with Full Dependencies）；
     *        Construct generate-statement handler with explicit dependencies.
     *
     * @param creditRepository 信用仓储接口（Credit repository）。
     * @param transactionManager 事务管理器（Database transaction manager）。
     * @param idGenerator ID 生成器（ID generator）。
     * @param statementAmountCalculator 账单金额计算端口（Statement amount calculator）。
     * @param domainEventPublisher 领域事件发布器（Domain event publisher）。
     */
    public GenerateStatementHandler(
            final CreditRepository creditRepository,
            final DbTransactionManager transactionManager,
            final IdGenerator idGenerator,
            final StatementAmountCalculator statementAmountCalculator,
            final DomainEventPublisher domainEventPublisher
    ) {
        this.creditRepository = Objects.requireNonNull(creditRepository, "creditRepository must not be null");
        this.transactionManager = Objects.requireNonNull(transactionManager, "transactionManager must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
        this.statementAmountCalculator = Objects.requireNonNull(
                statementAmountCalculator,
                "statementAmountCalculator must not be null");
        this.domainEventPublisher = Objects.requireNonNull(
                domainEventPublisher,
                "domainEventPublisher must not be null");
    }

    /**
     * @brief 处理生成账单命令（Handle Generate Statement Command）；
     *        Handle generate-statement command.
     *
     * @param command 生成账单命令（Generate-statement command）。
     * @return 账单应用结果（Statement application result）。
     */
    public CreditCardStatementResult handle(final GenerateStatementCommand command) {
        final GenerateStatementCommand normalizedCommand = Objects.requireNonNull(command, "command must not be null");
        return transactionManager.execute(() -> {
            final CreditCardAccountId creditCardAccountId = CreditCardAccountId.of(
                    normalizedCommand.creditCardAccountId());
            final CreditCardAccount creditCardAccount = creditRepository.findCreditCardAccountById(creditCardAccountId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Credit card account not found: " + creditCardAccountId.value()));

            final DateRange statementPeriod = creditCardAccount.billingCycle().deriveStatementPeriod(
                    normalizedCommand.statementDate());
            ensureNoDuplicateStatement(creditCardAccountId, statementPeriod);

            final Money totalAmountDue = statementAmountCalculator.calculateTotalAmountDue(
                    creditCardAccount,
                    statementPeriod,
                    normalizedCommand.statementDate());
            final Money floorAmount = Money.of(
                    creditCardAccount.accountCurrencyCode(),
                    normalizedCommand.minimumPaymentFloorAmount());
            final Money minimumAmountDue = MinimumPaymentPolicy.calculate(
                    totalAmountDue,
                    Percentage.ofDecimal(normalizedCommand.minimumPaymentRateDecimal()),
                    floorAmount);

            final CreditCardStatement statement = CreditCardStatement.generate(
                    StatementId.of(idGenerator.nextId()),
                    creditCardAccountId,
                    statementPeriod,
                    normalizedCommand.statementDate(),
                    creditCardAccount.billingCycle().resolvePaymentDueDate(normalizedCommand.statementDate()),
                    totalAmountDue,
                    minimumAmountDue,
                    creditCardAccount.accountCurrencyCode());

            creditRepository.saveCreditCardStatement(statement);
            domainEventPublisher.publish(statement.generatedEvent());
            return CreditCardStatementResult.from(statement);
        });
    }

    /**
     * @brief 校验账期唯一性（Ensure Statement Period Uniqueness）；
     *        Ensure statement does not already exist for the same period.
     *
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @param statementPeriod 账期范围（Statement period）。
     */
    private void ensureNoDuplicateStatement(
            final CreditCardAccountId creditCardAccountId,
            final DateRange statementPeriod
    ) {
        creditRepository.findStatementByPeriod(creditCardAccountId, statementPeriod)
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Statement already exists for period: " + statementPeriod);
                });
    }
}
