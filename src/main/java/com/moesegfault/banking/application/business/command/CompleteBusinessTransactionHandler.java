package com.moesegfault.banking.application.business.command;

import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.domain.business.BusinessRepository;
import com.moesegfault.banking.domain.business.BusinessTransaction;
import com.moesegfault.banking.domain.business.BusinessType;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 完成业务流水处理器（Complete Business Transaction Handler），编排交易终态流转事务；
 *        Complete-business-transaction handler orchestrating transactional terminal-state transitions.
 */
public final class CompleteBusinessTransactionHandler {

    /**
     * @brief 业务仓储接口（Business Repository Interface）；
     *        Business repository interface.
     */
    private final BusinessRepository businessRepository;

    /**
     * @brief 事务管理接口（Database Transaction Manager Interface）；
     *        Database transaction manager interface.
     */
    private final DbTransactionManager dbTransactionManager;

    /**
     * @brief 领域事件发布器（Domain Event Publisher）；
     *        Domain event publisher.
     */
    private final DomainEventPublisher domainEventPublisher;

    /**
     * @brief 构造处理器（Construct Handler）；
     *        Construct handler with no-op event publisher.
     *
     * @param businessRepository 业务仓储（Business repository）。
     * @param dbTransactionManager 事务管理器（DB transaction manager）。
     */
    public CompleteBusinessTransactionHandler(
            final BusinessRepository businessRepository,
            final DbTransactionManager dbTransactionManager
    ) {
        this(businessRepository, dbTransactionManager, DomainEventPublisher.noop());
    }

    /**
     * @brief 构造处理器（Construct Handler）；
     *        Construct handler.
     *
     * @param businessRepository 业务仓储（Business repository）。
     * @param dbTransactionManager 事务管理器（DB transaction manager）。
     * @param domainEventPublisher 领域事件发布器（Domain event publisher）。
     */
    public CompleteBusinessTransactionHandler(
            final BusinessRepository businessRepository,
            final DbTransactionManager dbTransactionManager,
            final DomainEventPublisher domainEventPublisher
    ) {
        this.businessRepository = Objects.requireNonNull(businessRepository, "businessRepository must not be null");
        this.dbTransactionManager = Objects.requireNonNull(
                dbTransactionManager,
                "dbTransactionManager must not be null");
        this.domainEventPublisher = Objects.requireNonNull(
                domainEventPublisher,
                "domainEventPublisher must not be null");
    }

    /**
     * @brief 执行完成业务流水（Handle Complete Business Transaction）；
     *        Handle complete-business-transaction use case.
     *
     * @param command 完成业务流水命令（Complete business transaction command）。
     * @return 业务流水结果（Business transaction result）。
     */
    public BusinessTransactionResult handle(final CompleteBusinessTransactionCommand command) {
        final CompleteBusinessTransactionCommand normalized = Objects.requireNonNull(
                command,
                "command must not be null");
        return dbTransactionManager.execute(() -> {
            final BusinessTransaction transaction = businessRepository.findTransactionById(normalized.toTransactionId())
                    .orElseThrow(() -> new BusinessRuleViolation(
                            "Business transaction not found: " + normalized.transactionId()));
            final Instant completedAt = normalized.resolveCompletedAt();
            switch (normalized.completionAction()) {
                case SUCCESS -> {
                    transaction.completeSuccess(completedAt, normalized.remarksOrNull());
                    domainEventPublisher.publish(transaction.completedEvent());
                }
                case FAILED -> {
                    transaction.fail(completedAt, normalized.remarksOrNull());
                    domainEventPublisher.publish(transaction.failedEvent());
                }
                case REVERSED -> {
                    final BusinessType businessType = businessRepository
                            .findBusinessTypeByCode(transaction.businessTypeCode())
                            .orElseThrow(() -> new BusinessRuleViolation(
                                    "Business type not found: " + transaction.businessTypeCode().value()));
                    transaction.reverse(businessType, completedAt, normalized.remarksOrNull());
                }
                default -> throw new IllegalStateException(
                        "Unsupported completion action: " + normalized.completionAction());
            }
            businessRepository.saveTransaction(transaction);
            return BusinessTransactionResult.from(transaction);
        });
    }
}

