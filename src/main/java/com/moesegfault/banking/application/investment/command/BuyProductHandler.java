package com.moesegfault.banking.application.investment.command;

import com.moesegfault.banking.application.investment.result.InvestmentOrderResult;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.business.BusinessRepository;
import com.moesegfault.banking.domain.investment.InvestmentRepository;
import com.moesegfault.banking.domain.investment.OrderSide;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.util.Objects;

/**
 * @brief 买入产品处理器（Buy Product Handler）；
 *        Application handler for buy-product order workflow.
 */
public final class BuyProductHandler {

    /**
     * @brief 下单编排服务（Order Orchestration Service）；
     *        Shared order orchestration service.
     */
    private final PlaceProductOrderService placeProductOrderService;

    /**
     * @brief 构造买入处理器（Construct Buy Handler）；
     *        Construct buy-product handler.
     *
     * @param investmentRepository 投资仓储（Investment repository）。
     * @param accountRepository    账户仓储（Account repository）。
     * @param businessRepository   业务仓储（Business repository）。
     * @param idGenerator          ID 生成器（ID generator）。
     * @param dbTransactionManager 事务管理器（Transaction manager）。
     * @param domainEventPublisher 事件发布器（Domain event publisher, nullable means noop）。
     */
    public BuyProductHandler(
            final InvestmentRepository investmentRepository,
            final AccountRepository accountRepository,
            final BusinessRepository businessRepository,
            final IdGenerator idGenerator,
            final DbTransactionManager dbTransactionManager,
            final DomainEventPublisher domainEventPublisher
    ) {
        this.placeProductOrderService = new PlaceProductOrderService(
                Objects.requireNonNull(investmentRepository, "investmentRepository must not be null"),
                Objects.requireNonNull(accountRepository, "accountRepository must not be null"),
                Objects.requireNonNull(businessRepository, "businessRepository must not be null"),
                Objects.requireNonNull(idGenerator, "idGenerator must not be null"),
                Objects.requireNonNull(dbTransactionManager, "dbTransactionManager must not be null"),
                domainEventPublisher);
    }

    /**
     * @brief 执行买入命令（Handle Buy Command）；
     *        Handle buy-product command.
     *
     * @param command 买入命令（Buy-product command）。
     * @return 订单结果（Investment order result）。
     */
    public InvestmentOrderResult handle(final BuyProductCommand command) {
        final BuyProductCommand normalized = Objects.requireNonNull(command, "command must not be null");
        return placeProductOrderService.placeAndSettle(
                normalized.investmentAccountId(),
                normalized.productCode(),
                OrderSide.BUY,
                normalized.quantity(),
                normalized.price(),
                normalized.feeAmountOrNull(),
                normalized.initiatorCustomerIdOrNull(),
                normalized.channelOrNull(),
                normalized.referenceNoOrNull(),
                normalized.customerRiskToleranceOrNull(),
                normalized.tradeAtOrNull());
    }
}
