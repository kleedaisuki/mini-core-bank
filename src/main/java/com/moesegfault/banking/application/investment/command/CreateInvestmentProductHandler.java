package com.moesegfault.banking.application.investment.command;

import com.moesegfault.banking.application.investment.result.InvestmentProductResult;
import com.moesegfault.banking.domain.investment.InvestmentProduct;
import com.moesegfault.banking.domain.investment.InvestmentRepository;
import com.moesegfault.banking.domain.investment.ProductCode;
import com.moesegfault.banking.domain.investment.ProductId;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.util.Objects;

/**
 * @brief 创建投资产品处理器（Create Investment Product Handler）；
 *        Application handler that creates investment product aggregate and persists it.
 */
public final class CreateInvestmentProductHandler {

    /**
     * @brief 投资仓储接口（Investment Repository Interface）；
     *        Investment repository dependency.
     */
    private final InvestmentRepository investmentRepository;

    /**
     * @brief ID 生成器接口（ID Generator Interface）；
     *        Identifier generator dependency.
     */
    private final IdGenerator idGenerator;

    /**
     * @brief 事务管理接口（Transaction Manager Interface）；
     *        Transaction boundary manager.
     */
    private final DbTransactionManager dbTransactionManager;

    /**
     * @brief 领域事件发布器（Domain Event Publisher）；
     *        Domain-event publisher dependency.
     */
    private final DomainEventPublisher domainEventPublisher;

    /**
     * @brief 构造创建产品处理器（Construct Create-Product Handler）；
     *        Construct create-investment-product handler.
     *
     * @param investmentRepository 投资仓储（Investment repository）。
     * @param idGenerator          ID 生成器（ID generator）。
     * @param dbTransactionManager 事务管理器（Transaction manager）。
     * @param domainEventPublisher 事件发布器（Domain event publisher, nullable means noop）。
     */
    public CreateInvestmentProductHandler(
            final InvestmentRepository investmentRepository,
            final IdGenerator idGenerator,
            final DbTransactionManager dbTransactionManager,
            final DomainEventPublisher domainEventPublisher
    ) {
        this.investmentRepository = Objects.requireNonNull(
                investmentRepository,
                "investmentRepository must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
        this.dbTransactionManager = Objects.requireNonNull(
                dbTransactionManager,
                "dbTransactionManager must not be null");
        this.domainEventPublisher = domainEventPublisher == null
                ? DomainEventPublisher.noop()
                : domainEventPublisher;
    }

    /**
     * @brief 执行创建产品命令（Handle Create-Product Command）；
     *        Handle create-investment-product command.
     *
     * @param command 创建产品命令（Create-product command）。
     * @return 投资产品结果（Investment product result）。
     */
    public InvestmentProductResult handle(final CreateInvestmentProductCommand command) {
        final CreateInvestmentProductCommand normalized = Objects.requireNonNull(command, "command must not be null");
        return dbTransactionManager.execute(() -> {
            final ProductCode productCode = ProductCode.of(normalized.productCode());
            if (investmentRepository.findProductByCode(productCode).isPresent()) {
                throw new BusinessRuleViolation("Investment product code already exists: " + productCode.value());
            }

            final InvestmentProduct product = InvestmentProduct.create(
                    ProductId.of(idGenerator.nextId()),
                    productCode,
                    normalized.productName(),
                    normalized.productType(),
                    normalized.currencyCode(),
                    normalized.riskLevel(),
                    normalized.issuer());

            investmentRepository.saveInvestmentProduct(product);
            domainEventPublisher.publish(product.createdEvent());
            return InvestmentProductResult.from(product);
        });
    }
}
