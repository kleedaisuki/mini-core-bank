package com.moesegfault.banking.application.credit.command;

import com.moesegfault.banking.application.credit.result.CreditCardAccountResult;
import com.moesegfault.banking.application.credit.result.CreditCardStatementResult;
import com.moesegfault.banking.application.credit.result.RepayCreditCardResult;
import com.moesegfault.banking.domain.credit.CreditCardAccount;
import com.moesegfault.banking.domain.credit.CreditCardAccountId;
import com.moesegfault.banking.domain.credit.CreditCardRepaymentReceived;
import com.moesegfault.banking.domain.credit.CreditCardStatement;
import com.moesegfault.banking.domain.credit.CreditRepository;
import com.moesegfault.banking.domain.credit.RepaymentAllocationPolicy;
import com.moesegfault.banking.domain.credit.StatementId;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.domain.shared.Money;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @brief 信用卡还款处理器（Repay Credit Card Handler），编排还款分配、额度回补、事件发布；
 *        Repay-credit-card handler orchestrating allocation, credit restoration, and event publication.
 */
public final class RepayCreditCardHandler {

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
     * @brief 领域事件发布器（Domain Event Publisher）；
     *        Domain event publisher.
     */
    private final DomainEventPublisher domainEventPublisher;

    /**
     * @brief 系统时钟（System Clock）；
     *        Clock used for deterministic time operations.
     */
    private final Clock clock;

    /**
     * @brief 构造信用卡还款处理器（Construct Repay Credit Card Handler）；
     *        Construct repay-credit-card handler.
     *
     * @param creditRepository 信用仓储接口（Credit repository）。
     * @param transactionManager 事务管理器（Database transaction manager）。
     */
    public RepayCreditCardHandler(
            final CreditRepository creditRepository,
            final DbTransactionManager transactionManager
    ) {
        this(
                creditRepository,
                transactionManager,
                DomainEventPublisher.noop(),
                Clock.systemUTC());
    }

    /**
     * @brief 构造信用卡还款处理器（完整依赖）（Construct Repay Credit Card Handler with Full Dependencies）；
     *        Construct repay-credit-card handler with explicit dependencies.
     *
     * @param creditRepository 信用仓储接口（Credit repository）。
     * @param transactionManager 事务管理器（Database transaction manager）。
     * @param domainEventPublisher 领域事件发布器（Domain event publisher）。
     * @param clock 系统时钟（System clock）。
     */
    public RepayCreditCardHandler(
            final CreditRepository creditRepository,
            final DbTransactionManager transactionManager,
            final DomainEventPublisher domainEventPublisher,
            final Clock clock
    ) {
        this.creditRepository = Objects.requireNonNull(creditRepository, "creditRepository must not be null");
        this.transactionManager = Objects.requireNonNull(transactionManager, "transactionManager must not be null");
        this.domainEventPublisher = Objects.requireNonNull(domainEventPublisher, "domainEventPublisher must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    /**
     * @brief 处理信用卡还款命令（Handle Repay Credit Card Command）；
     *        Handle repay-credit-card command.
     *
     * @param command 还款命令（Repay-credit-card command）。
     * @return 还款结果（Repayment result）。
     */
    public RepayCreditCardResult handle(final RepayCreditCardCommand command) {
        final RepayCreditCardCommand normalizedCommand = Objects.requireNonNull(command, "command must not be null");
        return transactionManager.execute(() -> {
            final CreditCardAccountId creditCardAccountId = CreditCardAccountId.of(
                    normalizedCommand.creditCardAccountId());
            final CreditCardAccount creditCardAccount = creditRepository.findCreditCardAccountById(creditCardAccountId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Credit card account not found: " + creditCardAccountId.value()));

            final CurrencyCode currencyCode = CurrencyCode.of(normalizedCommand.repaymentCurrencyCode());
            final Money repaymentAmount = Money.of(currencyCode, normalizedCommand.repaymentAmount());
            if (!repaymentAmount.isPositive()) {
                throw new IllegalArgumentException("repaymentAmount must be positive");
            }
            if (!creditCardAccount.accountCurrencyCode().equals(currencyCode)) {
                throw new IllegalArgumentException("repayment currency must match account currency");
            }

            final LocalDate asOfDate = normalizedCommand.asOfDateOrNull() != null
                    ? normalizedCommand.asOfDateOrNull()
                    : LocalDate.now(clock);

            final AllocationOutcome allocationOutcome = allocateToStatements(
                    creditCardAccountId,
                    repaymentAmount,
                    normalizedCommand.statementIdOrNull(),
                    asOfDate);

            final Money usedBeforeRepayment = creditCardAccount.creditLimit().usedAmount();
            final Money appliedToAccountAmount = minMoney(repaymentAmount, usedBeforeRepayment);
            if (allocationOutcome.appliedToStatementsAmount().compareTo(appliedToAccountAmount) > 0) {
                throw new IllegalStateException(
                        "Applied-to-statement amount exceeds used credit snapshot on account");
            }
            if (appliedToAccountAmount.isPositive()) {
                creditCardAccount.receiveRepayment(appliedToAccountAmount);
                creditRepository.saveCreditCardAccount(creditCardAccount);
                domainEventPublisher.publish(new CreditCardRepaymentReceived(
                        creditCardAccountId,
                        allocationOutcome.targetStatementIdOrNull(),
                        appliedToAccountAmount,
                        Instant.now(clock)));
            }

            return new RepayCreditCardResult(
                    CreditCardAccountResult.from(creditCardAccount),
                    appliedToAccountAmount.amount(),
                    allocationOutcome.appliedToStatementsAmount().amount(),
                    repaymentAmount.subtract(appliedToAccountAmount).amount(),
                    currencyCode.value(),
                    allocationOutcome.targetStatementIdOrNull() == null
                            ? null
                            : allocationOutcome.targetStatementIdOrNull().value(),
                    allocationOutcome.affectedStatements().stream()
                            .map(CreditCardStatementResult::from)
                            .collect(Collectors.toList()));
        });
    }

    /**
     * @brief 对账单分配还款（Allocate Repayment to Statements）；
     *        Allocate repayment amount to statements.
     *
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @param repaymentAmount 还款金额（Repayment amount）。
     * @param statementIdOrNull 定向账单 ID（可空）（Target statement ID, nullable）。
     * @param asOfDate 业务日期（As-of date）。
     * @return 分配结果（Allocation outcome）。
     */
    private AllocationOutcome allocateToStatements(
            final CreditCardAccountId creditCardAccountId,
            final Money repaymentAmount,
            final String statementIdOrNull,
            final LocalDate asOfDate
    ) {
        if (statementIdOrNull != null) {
            final StatementId statementId = StatementId.of(statementIdOrNull);
            final CreditCardStatement statement = creditRepository.findStatementById(statementId)
                    .orElseThrow(() -> new IllegalArgumentException("Statement not found: " + statementId.value()));
            if (!statement.creditCardAccountId().equals(creditCardAccountId)) {
                throw new IllegalArgumentException("Statement does not belong to credit-card account");
            }
            statement.markOverdue(asOfDate);
            final Money remainder = statement.applyRepayment(repaymentAmount);
            creditRepository.saveCreditCardStatement(statement);
            return new AllocationOutcome(
                    List.of(statement),
                    repaymentAmount.subtract(remainder),
                    statementId);
        }

        final List<CreditCardStatement> repayableStatements = new ArrayList<>(
                creditRepository.listRepayableStatementsByAccountId(creditCardAccountId));
        for (CreditCardStatement repayableStatement : repayableStatements) {
            repayableStatement.markOverdue(asOfDate);
        }
        final Money remainder = RepaymentAllocationPolicy.allocateToStatements(repaymentAmount, repayableStatements);
        for (CreditCardStatement repayableStatement : repayableStatements) {
            creditRepository.saveCreditCardStatement(repayableStatement);
        }
        repayableStatements.sort(Comparator
                .comparing(CreditCardStatement::paymentDueDate)
                .thenComparing(CreditCardStatement::statementDate)
                .thenComparing(candidate -> candidate.statementId().value()));
        return new AllocationOutcome(
                repayableStatements,
                repaymentAmount.subtract(remainder),
                null);
    }

    /**
     * @brief 计算两个金额最小值（Calculate Minimum Money）；
     *        Calculate minimum of two money amounts.
     *
     * @param left 左侧金额（Left amount）。
     * @param right 右侧金额（Right amount）。
     * @return 最小金额（Minimum amount）。
     */
    private Money minMoney(
            final Money left,
            final Money right
    ) {
        return left.compareTo(right) <= 0 ? left : right;
    }

    /**
     * @brief 还款分配结果（Allocation Outcome），承载分配过程快照；
     *        Allocation outcome record carrying allocation snapshot.
     *
     * @param affectedStatements 受影响账单（Affected statements）。
     * @param appliedToStatementsAmount 分配到账单金额（Applied-to-statements amount）。
     * @param targetStatementIdOrNull 定向账单 ID（可空）（Target statement ID, nullable）。
     */
    private record AllocationOutcome(
            List<CreditCardStatement> affectedStatements,
            Money appliedToStatementsAmount,
            StatementId targetStatementIdOrNull) {

        /**
         * @brief 构造并校验分配结果（Construct and Validate Allocation Outcome）；
         *        Construct and validate allocation outcome.
         *
         * @param affectedStatements 受影响账单（Affected statements）。
         * @param appliedToStatementsAmount 分配到账单金额（Applied-to-statements amount）。
         * @param targetStatementIdOrNull 定向账单 ID（可空）（Target statement ID, nullable）。
         */
        private AllocationOutcome {
            affectedStatements = List.copyOf(Objects.requireNonNull(
                    affectedStatements,
                    "affectedStatements must not be null"));
            appliedToStatementsAmount = Objects.requireNonNull(
                    appliedToStatementsAmount,
                    "appliedToStatementsAmount must not be null");
        }
    }
}
