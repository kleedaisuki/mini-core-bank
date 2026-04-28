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
 * @brief 卖出产品处理器（Sell Product Handler）；
 *        Application handler for sell-product order workflow.
 */
public final class SellProductHandler {

    /**
     * @brief 下单编排服务（Order Orchestration Service）；
     *        Shared order orchestration service.
     */
    private final PlaceProductOrderService placeProductOrderService;

    /**
     * @brief 构造卖出处理器（Construct Sell Handler）；
     *        Construct sell-product handler.
     *
     * @param investmentRepository 投资仓储（Investment repository）。
     * @param accountRepository    账户仓储（Account repository）。
     * @param businessRepository   业务仓储（Business repository）。
     * @param idGenerator          ID 生成器（ID generator）。
     * @param dbTransactionManager 事务管理器（Transaction manager）。
     * @param domainEventPublisher 事件发布器（Domain event publisher, nullable means noop）。
     */
    public SellProductHandler(
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
     * @brief 执行卖出命令（Handle Sell Command）；
     *        Handle sell-product command.
     *
     * @param command 卖出命令（Sell-product command）。
     * @return 订单结果（Investment order result）。
     */
    public InvestmentOrderResult handle(final SellProductCommand command) {
        final SellProductCommand normalized = Objects.requireNonNull(command, "command must not be null");
        return placeProductOrderService.placeAndSettle(
                normalized.investmentAccountId(),
                normalized.productCode(),
                OrderSide.SELL,
                normalized.quantity(),
                normalized.price(),
                normalized.feeAmountOrNull(),
                normalized.initiatorCustomerIdOrNull(),
                normalized.channelOrNull(),
                normalized.referenceNoOrNull(),
                null,
                normalized.tradeAtOrNull());
    }
}
