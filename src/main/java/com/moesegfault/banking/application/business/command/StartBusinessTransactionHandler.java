package com.moesegfault.banking.application.business.command;

import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.domain.business.BusinessReference;
import com.moesegfault.banking.domain.business.BusinessRepository;
import com.moesegfault.banking.domain.business.BusinessTransaction;
import com.moesegfault.banking.domain.business.BusinessTransactionId;
import com.moesegfault.banking.domain.business.BusinessTransactionPolicy;
import com.moesegfault.banking.domain.business.BusinessType;
import com.moesegfault.banking.domain.business.BusinessTypeCode;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.util.Objects;

/**
 * @brief 开始业务流水处理器（Start Business Transaction Handler），负责编排业务流水创建事务；
 *        Start-business-transaction handler orchestrating transactional creation of business flow.
 */
public final class StartBusinessTransactionHandler {

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
     * @brief ID 生成器接口（ID Generator Interface）；
     *        ID generator interface.
     */
    private final IdGenerator idGenerator;

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
     * @param idGenerator ID 生成器（ID generator）。
     */
    public StartBusinessTransactionHandler(
            final BusinessRepository businessRepository,
            final DbTransactionManager dbTransactionManager,
            final IdGenerator idGenerator
    ) {
        this(businessRepository, dbTransactionManager, idGenerator, DomainEventPublisher.noop());
    }

    /**
     * @brief 构造处理器（Construct Handler）；
     *        Construct handler.
     *
     * @param businessRepository 业务仓储（Business repository）。
     * @param dbTransactionManager 事务管理器（DB transaction manager）。
     * @param idGenerator ID 生成器（ID generator）。
     * @param domainEventPublisher 领域事件发布器（Domain event publisher）。
     */
    public StartBusinessTransactionHandler(
            final BusinessRepository businessRepository,
            final DbTransactionManager dbTransactionManager,
            final IdGenerator idGenerator,
            final DomainEventPublisher domainEventPublisher
    ) {
        this.businessRepository = Objects.requireNonNull(businessRepository, "businessRepository must not be null");
        this.dbTransactionManager = Objects.requireNonNull(
                dbTransactionManager,
                "dbTransactionManager must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
        this.domainEventPublisher = Objects.requireNonNull(
                domainEventPublisher,
                "domainEventPublisher must not be null");
    }

    /**
     * @brief 执行创建业务流水（Handle Start Business Transaction）；
     *        Handle start-business-transaction use case.
     *
     * @param command 开始业务流水命令（Start business transaction command）。
     * @return 业务流水结果（Business transaction result）。
     */
    public BusinessTransactionResult handle(final StartBusinessTransactionCommand command) {
        final StartBusinessTransactionCommand normalized = Objects.requireNonNull(
                command,
                "command must not be null");
        return dbTransactionManager.execute(() -> {
            final BusinessTypeCode businessTypeCode = normalized.toBusinessTypeCode();
            final BusinessType businessType = businessRepository.findBusinessTypeByCode(businessTypeCode)
                    .orElseThrow(() -> new BusinessRuleViolation(
                            "Business type not found: " + businessTypeCode.value()));
            BusinessTransactionPolicy.ensureTypeStartable(businessType);

            final BusinessReference referenceNo = normalized.toReferenceNo();
            BusinessTransactionPolicy.ensureReferenceNotUsed(businessRepository, referenceNo);

            final BusinessTransaction transaction = BusinessTransaction.start(
                    newBusinessTransactionId(),
                    businessTypeCode,
                    normalized.toInitiatorCustomerIdOrNull(),
                    normalized.operatorIdOrNull(),
                    normalized.channel(),
                    referenceNo,
                    normalized.remarksOrNull());
            businessRepository.saveTransaction(transaction);
            domainEventPublisher.publish(transaction.startedEvent());
            return BusinessTransactionResult.from(transaction);
        });
    }

    /**
     * @brief 生成业务交易 ID（Generate Business Transaction ID）；
     *        Generate business transaction ID.
     *
     * @return 业务交易 ID（Business transaction ID）。
     * @note 对齐 `business_transaction.transaction_id VARCHAR(36)`，超长会被拒绝；
     *       Aligned with `business_transaction.transaction_id VARCHAR(36)`, over-length is rejected.
     */
    private BusinessTransactionId newBusinessTransactionId() {
        final String generated = Objects.requireNonNull(idGenerator.nextId(), "Generated ID must not be null").trim();
        if (generated.isEmpty()) {
            throw new IllegalArgumentException("Generated transaction ID must not be blank");
        }
        if (generated.length() > 36) {
            throw new IllegalArgumentException("Generated transaction ID length must be <= 36");
        }
        return BusinessTransactionId.of(generated);
    }
}

