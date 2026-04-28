package com.moesegfault.banking.domain.investment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class InvestmentDomainTest {

    @Test
    void shouldManageProductLifecycleWithSchemaAlignedStatus() {
        final InvestmentProduct product = InvestmentProduct.create(
                ProductId.of("prod-001"),
                ProductCode.of("FUND_USD_A"),
                "USD Growth Fund",
                ProductType.FUND,
                CurrencyCode.of("USD"),
                RiskLevel.R3,
                "MOE Asset");

        assertEquals(ProductStatus.ACTIVE, product.productStatus());

        product.deactivate();
        assertEquals(ProductStatus.INACTIVE, product.productStatus());

        product.activate();
        assertEquals(ProductStatus.ACTIVE, product.productStatus());

        product.close();
        assertEquals(ProductStatus.CLOSED, product.productStatus());
        assertThrows(BusinessRuleViolation.class, product::activate);
    }

    @Test
    void shouldPlaceAndSettleOrder() {
        final InvestmentOrder order = InvestmentOrder.place(
                InvestmentOrderId.of("txn-001"),
                InvestmentAccountId.of("inv-acc-001"),
                ProductId.of("prod-001"),
                OrderSide.BUY,
                Quantity.of(new BigDecimal("10.000000")),
                NetAssetValue.of(new BigDecimal("1.250000"), CurrencyCode.of("USD")),
                Money.of(CurrencyCode.of("USD"), new BigDecimal("12.5000")),
                Money.of(CurrencyCode.of("USD"), new BigDecimal("0.1000")),
                Instant.parse("2026-04-28T00:00:00Z"));

        assertEquals(OrderStatus.PLACED, order.orderStatus());

        order.settle(Instant.parse("2026-04-29T00:00:00Z"));

        assertEquals(OrderStatus.SETTLED, order.orderStatus());
        assertEquals(Instant.parse("2026-04-29T00:00:00Z"), order.settlementAtOrNull());
        assertEquals(InvestmentOrderId.of("txn-001"), order.settledEvent().investmentOrderId());
    }

    @Test
    void shouldRejectSellWhenHoldingQuantityInsufficient() {
        final Holding holding = Holding.open(
                HoldingId.of("hold-001"),
                InvestmentAccountId.of("inv-acc-001"),
                ProductId.of("prod-001"),
                CurrencyCode.of("USD"));

        holding.buy(
                Quantity.of(new BigDecimal("5.000000")),
                NetAssetValue.of(new BigDecimal("2.000000"), CurrencyCode.of("USD")));

        assertThrows(
                BusinessRuleViolation.class,
                () -> holding.sell(Quantity.of(new BigDecimal("6.000000"))));
    }

    @Test
    void shouldMarkToMarketAndComputeUnrealizedPnl() {
        final Holding holding = Holding.open(
                HoldingId.of("hold-002"),
                InvestmentAccountId.of("inv-acc-001"),
                ProductId.of("prod-001"),
                CurrencyCode.of("USD"));

        holding.buy(
                Quantity.of(new BigDecimal("10.000000")),
                NetAssetValue.of(new BigDecimal("1.000000"), CurrencyCode.of("USD")));

        final ProductValuation valuation = ProductValuation.of(
                ProductId.of("prod-001"),
                LocalDate.of(2026, 4, 28),
                NetAssetValue.of(new BigDecimal("1.200000"), CurrencyCode.of("USD")));

        holding.markToMarket(valuation);

        assertEquals(new BigDecimal("12.000000"), holding.marketValue());
        assertEquals(new BigDecimal("2.000000"), holding.unrealizedPnl());
    }

    @Test
    void shouldApplySuitabilityPolicyByRiskLevel() {
        assertTrue(SuitabilityPolicy.isSuitable(RiskLevel.R4, RiskLevel.R3));
        assertThrows(
                BusinessRuleViolation.class,
                () -> SuitabilityPolicy.ensureSuitable(
                        RiskLevel.R2,
                        InvestmentProduct.create(
                                ProductId.of("prod-009"),
                                ProductCode.of("BOND_HK_01"),
                                "HK Bond",
                                ProductType.BOND,
                                CurrencyCode.of("HKD"),
                                RiskLevel.R4,
                                "Issuer")));
    }
}
