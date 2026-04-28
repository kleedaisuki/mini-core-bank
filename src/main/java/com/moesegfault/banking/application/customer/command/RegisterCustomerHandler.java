package com.moesegfault.banking.application.customer.command;

import com.moesegfault.banking.application.customer.result.RegisterCustomerResult;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.customer.IdentityDocument;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.util.Objects;

/**
 * @brief 注册客户处理器（Register Customer Handler），负责编排唯一性校验、事务与事件发布；
 *        Register-customer handler orchestrating uniqueness checks, transaction boundary, and event publishing.
 */
public final class RegisterCustomerHandler {

    /**
     * @brief 客户仓储（Customer Repository）；
     *        Customer repository.
     */
    private final CustomerRepository customerRepository;

    /**
     * @brief 事务管理器（Database Transaction Manager）；
     *        Database transaction manager.
     */
    private final DbTransactionManager transactionManager;

    /**
     * @brief ID 生成器（Identifier Generator）；
     *        Identifier generator.
     */
    private final IdGenerator idGenerator;

    /**
     * @brief 领域事件发布器（Domain Event Publisher）；
     *        Domain event publisher.
     */
    private final DomainEventPublisher domainEventPublisher;

    /**
     * @brief 构造处理器并使用空事件发布器（Construct with No-op Event Publisher）；
     *        Construct handler using no-op event publisher.
     *
     * @param customerRepository 客户仓储（Customer repository）。
     * @param transactionManager 事务管理器（Transaction manager）。
     * @param idGenerator        ID 生成器（ID generator）。
     */
    public RegisterCustomerHandler(
            final CustomerRepository customerRepository,
            final DbTransactionManager transactionManager,
            final IdGenerator idGenerator
    ) {
        this(customerRepository, transactionManager, idGenerator, DomainEventPublisher.noop());
    }

    /**
     * @brief 构造处理器（Construct Register Customer Handler）；
     *        Construct register-customer handler.
     *
     * @param customerRepository  客户仓储（Customer repository）。
     * @param transactionManager  事务管理器（Transaction manager）。
     * @param idGenerator         ID 生成器（ID generator）。
     * @param domainEventPublisher 领域事件发布器（Domain event publisher）。
     */
    public RegisterCustomerHandler(
            final CustomerRepository customerRepository,
            final DbTransactionManager transactionManager,
            final IdGenerator idGenerator,
            final DomainEventPublisher domainEventPublisher
    ) {
        this.customerRepository = Objects.requireNonNull(customerRepository, "Customer repository must not be null");
        this.transactionManager = Objects.requireNonNull(transactionManager, "Transaction manager must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "ID generator must not be null");
        this.domainEventPublisher = Objects.requireNonNull(
                domainEventPublisher,
                "Domain event publisher must not be null");
    }

    /**
     * @brief 执行注册客户（Handle Register Customer Command）；
     *        Handle register-customer command.
     *
     * @param command 注册命令（Register-customer command）。
     * @return 注册结果（Register-customer result）。
     * @note 同一证件（id_type/id_number/issuing_region）重复注册会抛出业务异常；
     *       Duplicate identity-document registration throws business-rule violation.
     */
    public RegisterCustomerResult handle(final RegisterCustomerCommand command) {
        final RegisterCustomerCommand normalized = Objects.requireNonNull(command, "Command must not be null");
        return transactionManager.execute(() -> {
            final IdentityDocument identityDocument = normalized.toIdentityDocument();
            if (customerRepository.existsByIdentityDocument(identityDocument)) {
                throw new BusinessRuleViolation(
                        "Customer identity document already registered: " + identityDocument);
            }

            final Customer customer = Customer.register(
                    CustomerId.of(idGenerator.nextId()),
                    identityDocument,
                    normalized.toPhoneNumber(),
                    normalized.toResidentialAddress(),
                    normalized.toMailingAddress(),
                    normalized.toTaxProfile());

            customerRepository.save(customer);
            domainEventPublisher.publish(customer.registeredEvent());
            return new RegisterCustomerResult(
                    customer.customerId().value(),
                    customer.customerStatus().name(),
                    customer.createdAt());
        });
    }
}
