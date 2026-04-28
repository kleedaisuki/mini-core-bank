package com.moesegfault.banking.application.investment.command;

import com.moesegfault.banking.application.investment.InvestmentBusinessTypeCodes;
import com.moesegfault.banking.application.investment.result.InvestmentOrderResult;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.business.BusinessReference;
import com.moesegfault.banking.domain.business.BusinessRepository;
import com.moesegfault.banking.domain.business.BusinessTransaction;
import com.moesegfault.banking.domain.business.BusinessTransactionId;
import com.moesegfault.banking.domain.business.BusinessTransactionPolicy;
import com.moesegfault.banking.domain.business.BusinessType;
import com.moesegfault.banking.domain.business.BusinessTypeCode;
import com.moesegfault.banking.domain.investment.Holding;
import com.moesegfault.banking.domain.investment.HoldingId;
import com.moesegfault.banking.domain.investment.InvestmentAccountId;
import com.moesegfault.banking.domain.investment.InvestmentOrder;
import com.moesegfault.banking.domain.investment.InvestmentOrderId;
import com.moesegfault.banking.domain.investment.InvestmentProduct;
import com.moesegfault.banking.domain.investment.InvestmentRepository;
import com.moesegfault.banking.domain.investment.NetAssetValue;
import com.moesegfault.banking.domain.investment.OrderSide;
import com.moesegfault.banking.domain.investment.ProductCode;
import com.moesegfault.banking.domain.investment.ProductValuation;
import com.moesegfault.banking.domain.investment.Quantity;
import com.moesegfault.banking.domain.investment.RiskLevel;
import com.moesegfault.banking.domain.investment.SuitabilityPolicy;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.domain.shared.Money;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * @brief 投资下单编排服务（Investment Order Orchestration Service）；
 *        Shared application service orchestrating buy/sell lifecycle across investment and business domains.
 */
final class PlaceProductOrderService {

    /**
     * @brief 资金金额小数位（Money Scale）；
     *        Money scale aligned with `NUMERIC(19,4)`.
     */
    private static final int MONEY_SCALE = 4;

    /**
     * @brief 默认业务渠道（Default Business Channel）；
     *        Default business channel when caller does not provide one.
     */
    private static final BusinessChannel DEFAULT_CHANNEL = BusinessChannel.ONLINE;

    /**
     * @brief 投资仓储接口（Investment Repository Interface）；
     *        Investment repository dependency.
     */
    private final InvestmentRepository investmentRepository;

    /**
     * @brief 账户仓储接口（Account Repository Interface）；
     *        Account repository dependency.
     */
    private final AccountRepository accountRepository;

    /**
     * @brief 业务仓储接口（Business Repository Interface）；
     *        Business repository dependency.
     */
    private final BusinessRepository businessRepository;

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
     * @brief 构造下单编排服务（Construct Order-Orchestration Service）；
     *        Construct order-orchestration service.
     *
     * @param investmentRepository 投资仓储（Investment repository）。
     * @param accountRepository    账户仓储（Account repository）。
     * @param businessRepository   业务仓储（Business repository）。
     * @param idGenerator          ID 生成器（ID generator）。
     * @param dbTransactionManager 事务管理器（Transaction manager）。
     * @param domainEventPublisher 事件发布器（Domain event publisher, nullable means noop）。
     */
    PlaceProductOrderService(
            final InvestmentRepository investmentRepository,
            final AccountRepository accountRepository,
            final BusinessRepository businessRepository,
            final IdGenerator idGenerator,
            final DbTransactionManager dbTransactionManager,
            final DomainEventPublisher domainEventPublisher
    ) {
        this.investmentRepository = Objects.requireNonNull(
                investmentRepository,
                "investmentRepository must not be null");
        this.accountRepository = Objects.requireNonNull(accountRepository, "accountRepository must not be null");
        this.businessRepository = Objects.requireNonNull(businessRepository, "businessRepository must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
        this.dbTransactionManager = Objects.requireNonNull(
                dbTransactionManager,
                "dbTransactionManager must not be null");
        this.domainEventPublisher = domainEventPublisher == null
                ? DomainEventPublisher.noop()
                : domainEventPublisher;
    }

    /**
     * @brief 提交并结算订单（Place and Settle Order）；
     *        Place and synchronously settle a buy/sell order.
     *
     * @param rawInvestmentAccountId 投资账户 ID（Investment account ID）。
     * @param rawProductCode         产品代码（Product code）。
     * @param orderSide              订单方向（Order side）。
     * @param rawQuantity            份额（Quantity decimal）。
     * @param rawPrice               价格（Price decimal）。
     * @param rawFeeAmountOrNull     手续费（Fee decimal, nullable）。
     * @param rawInitiatorCustomerIdOrNull 发起客户 ID（Initiator customer ID, nullable）。
     * @param channelOrNull          业务渠道（Business channel, nullable）。
     * @param rawReferenceNoOrNull   业务参考号（Business reference, nullable）。
     * @param customerRiskToleranceOrNull 客户风险承受等级（Risk tolerance, nullable）。
     * @param tradeAtOrNull          交易时间（Trade timestamp, nullable）。
     * @return 投资订单结果（Investment order result）。
     */
    InvestmentOrderResult placeAndSettle(
            final String rawInvestmentAccountId,
            final String rawProductCode,
            final OrderSide orderSide,
            final BigDecimal rawQuantity,
            final BigDecimal rawPrice,
            final BigDecimal rawFeeAmountOrNull,
            final String rawInitiatorCustomerIdOrNull,
            final BusinessChannel channelOrNull,
            final String rawReferenceNoOrNull,
            final RiskLevel customerRiskToleranceOrNull,
            final Instant tradeAtOrNull
    ) {
        final String normalizedInvestmentAccountId = Objects.requireNonNull(
                rawInvestmentAccountId,
                "rawInvestmentAccountId must not be null");
        final String normalizedProductCode = Objects.requireNonNull(rawProductCode, "rawProductCode must not be null");
        final OrderSide normalizedOrderSide = Objects.requireNonNull(orderSide, "orderSide must not be null");
        final BigDecimal normalizedQuantity = Objects.requireNonNull(rawQuantity, "rawQuantity must not be null");
        final BigDecimal normalizedPrice = Objects.requireNonNull(rawPrice, "rawPrice must not be null");

        return dbTransactionManager.execute(() -> {
            final InvestmentAccountId investmentAccountId = InvestmentAccountId.of(normalizedInvestmentAccountId);
            ensureInvestmentAccountExists(investmentAccountId);

            final InvestmentProduct product = investmentRepository.findProductByCode(ProductCode.of(normalizedProductCode))
                    .orElseThrow(() -> new BusinessRuleViolation(
                            "Investment product does not exist: " + normalizedProductCode));
            product.ensureTradable();

            if (customerRiskToleranceOrNull != null) {
                SuitabilityPolicy.ensureSuitable(customerRiskToleranceOrNull, product);
            }

            final BusinessTypeCode businessTypeCode = mapBusinessTypeCode(normalizedOrderSide);
            final BusinessType businessType = businessRepository.findBusinessTypeByCode(businessTypeCode)
                    .orElseThrow(() -> new BusinessRuleViolation(
                            "Business type code does not exist: " + businessTypeCode.value()));
            BusinessTransactionPolicy.ensureTypeStartable(businessType);

            final BusinessReference businessReference = resolveReference(
                    normalizedOrderSide,
                    rawReferenceNoOrNull);
            BusinessTransactionPolicy.ensureReferenceNotUsed(businessRepository, businessReference);

            final BusinessChannel businessChannel = channelOrNull == null ? DEFAULT_CHANNEL : channelOrNull;
            final Instant tradeAt = tradeAtOrNull == null ? Instant.now() : tradeAtOrNull;
            final NetAssetValue price = NetAssetValue.of(normalizedPrice, product.currencyCode());
            final Quantity quantity = Quantity.of(normalizedQuantity);
            final Money grossAmount = Money.of(
                    product.currencyCode(),
                    normalizeMoneyScale(quantity.value().multiply(price.value())));
            final Money feeAmount = Money.of(
                    product.currencyCode(),
                    normalizeMoneyScale(rawFeeAmountOrNull == null ? BigDecimal.ZERO : rawFeeAmountOrNull));

            final String transactionIdValue = idGenerator.nextId();
            final InvestmentOrder order = InvestmentOrder.place(
                    InvestmentOrderId.of(transactionIdValue),
                    investmentAccountId,
                    product.productId(),
                    normalizedOrderSide,
                    quantity,
                    price,
                    grossAmount,
                    feeAmount,
                    tradeAt);

            final BusinessTransaction transaction = BusinessTransaction.start(
                    BusinessTransactionId.of(transactionIdValue),
                    businessTypeCode,
                    mapInitiatorCustomerId(rawInitiatorCustomerIdOrNull),
                    null,
                    businessChannel,
                    businessReference,
                    normalizedOrderSide.name() + " product order");

            businessRepository.saveTransaction(transaction);
            investmentRepository.saveInvestmentOrder(order);
            domainEventPublisher.publish(transaction.startedEvent());
            domainEventPublisher.publish(order.placedEvent());

            final Instant settledAt = computeSettlementAt(tradeAt);
            order.settle(settledAt);
            final Holding holding = loadOrOpenHolding(investmentAccountId, product);
            holding.applySettledOrder(order);

            final ProductValuation valuation = ProductValuation.of(
                    product.productId(),
                    LocalDate.ofInstant(settledAt, ZoneOffset.UTC),
                    price);
            holding.markToMarket(valuation);

            investmentRepository.saveProductValuation(valuation);
            investmentRepository.saveHolding(holding);
            investmentRepository.saveInvestmentOrder(order);

            transaction.completeSuccess(settledAt, normalizedOrderSide.name() + " order settled");
            businessRepository.saveTransaction(transaction);

            domainEventPublisher.publish(order.settledEvent());
            domainEventPublisher.publish(holding.changedEvent());
            domainEventPublisher.publish(transaction.completedEvent());

            return InvestmentOrderResult.from(order, transaction, product, holding);
        });
    }

    /**
     * @brief 校验投资账户存在（Ensure Investment Account Exists）；
     *        Ensure referenced investment account exists.
     *
     * @param investmentAccountId 投资账户 ID（Investment account ID）。
     */
    private void ensureInvestmentAccountExists(final InvestmentAccountId investmentAccountId) {
        final com.moesegfault.banking.domain.account.InvestmentAccountId accountId =
                com.moesegfault.banking.domain.account.InvestmentAccountId.of(investmentAccountId.value());
        if (accountRepository.findInvestmentAccountById(accountId).isEmpty()) {
            throw new BusinessRuleViolation("Investment account does not exist: " + investmentAccountId.value());
        }
    }

    /**
     * @brief 映射订单方向到业务类型码（Map Order Side to Business Type Code）；
     *        Map buy/sell side to corresponding business type code.
     *
     * @param orderSide 订单方向（Order side）。
     * @return 业务类型码（Business type code）。
     */
    private static BusinessTypeCode mapBusinessTypeCode(final OrderSide orderSide) {
        if (orderSide == OrderSide.BUY) {
            return InvestmentBusinessTypeCodes.BUY_PRODUCT;
        }
        if (orderSide == OrderSide.SELL) {
            return InvestmentBusinessTypeCodes.SELL_PRODUCT;
        }
        throw new BusinessRuleViolation("Unsupported order side for investment lifecycle: " + orderSide);
    }

    /**
     * @brief 解析业务参考号（Resolve Business Reference）；
     *        Resolve business reference from caller value or generated default.
     *
     * @param orderSide         订单方向（Order side）。
     * @param rawReferenceOrNull 参考号（Reference, nullable）。
     * @return 业务参考号（Business reference）。
     */
    private static BusinessReference resolveReference(
            final OrderSide orderSide,
            final String rawReferenceOrNull
    ) {
        if (rawReferenceOrNull != null && !rawReferenceOrNull.isBlank()) {
            return BusinessReference.of(rawReferenceOrNull);
        }
        final long epochMillis = Instant.now().toEpochMilli();
        return BusinessReference.of("INV_" + orderSide.name() + "_" + epochMillis);
    }

    /**
     * @brief 读取或开立持仓（Load or Open Holding）；
     *        Load existing holding by account+product or open an empty one.
     *
     * @param investmentAccountId 投资账户 ID（Investment account ID）。
     * @param product             投资产品实体（Investment product）。
     * @return 持仓实体（Holding entity）。
     */
    private Holding loadOrOpenHolding(
            final InvestmentAccountId investmentAccountId,
            final InvestmentProduct product
    ) {
        return investmentRepository
                .findHoldingByAccountAndProduct(investmentAccountId, product.productId())
                .orElseGet(() -> Holding.open(
                        HoldingId.of(idGenerator.nextId()),
                        investmentAccountId,
                        product.productId(),
                        product.currencyCode()));
    }

    /**
     * @brief 归一化发起客户 ID（Normalize Initiator Customer ID）；
     *        Normalize nullable initiator-customer ID.
     *
     * @param rawInitiatorCustomerIdOrNull 原始客户 ID（Raw customer ID, nullable）。
     * @return 业务域客户 ID 或 null（Business-domain customer ID or null）。
     */
    private static com.moesegfault.banking.domain.business.CustomerId mapInitiatorCustomerId(
            final String rawInitiatorCustomerIdOrNull
    ) {
        if (rawInitiatorCustomerIdOrNull == null || rawInitiatorCustomerIdOrNull.isBlank()) {
            return null;
        }
        return com.moesegfault.banking.domain.business.CustomerId.of(rawInitiatorCustomerIdOrNull);
    }

    /**
     * @brief 计算结算时间（Compute Settlement Timestamp）；
     *        Compute settlement timestamp not earlier than trade timestamp.
     *
     * @param tradeAt 交易时间（Trade timestamp）。
     * @return 结算时间（Settlement timestamp）。
     */
    private static Instant computeSettlementAt(final Instant tradeAt) {
        final Instant now = Instant.now();
        if (now.isBefore(tradeAt)) {
            return tradeAt;
        }
        return now;
    }

    /**
     * @brief 标准化金额小数位（Normalize Money Scale）；
     *        Normalize amount to money scale with `HALF_UP` rounding.
     *
     * @param amount 原始金额（Raw amount）。
     * @return 标准化金额（Normalized amount）。
     */
    private static BigDecimal normalizeMoneyScale(final BigDecimal amount) {
        return Objects.requireNonNull(amount, "amount must not be null")
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
