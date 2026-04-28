package com.moesegfault.banking.domain.investment;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.util.Objects;

/**
 * @brief 持仓策略（Holding Policy），集中定义持仓数量相关校验；
 *        Holding policy centralizing quantity-related validations.
 */
public final class HoldingPolicy {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private HoldingPolicy() {
        // utility class
    }

    /**
     * @brief 校验卖出份额是否足够（Ensure Sufficient Quantity for Sell）；
     *        Ensure holding quantity is sufficient for sell/redeem operation.
     *
     * @param holding      持仓实体（Holding entity）。
     * @param sellQuantity 卖出份额（Sell quantity）。
     */
    public static void ensureSufficientQuantity(
            final Holding holding,
            final Quantity sellQuantity
    ) {
        final Holding normalizedHolding = Objects.requireNonNull(holding, "Holding must not be null");
        final Quantity normalizedQuantity = Objects.requireNonNull(
                sellQuantity,
                "Sell quantity must not be null");
        if (!normalizedQuantity.isPositive()) {
            throw new BusinessRuleViolation("Sell quantity must be positive");
        }
        if (normalizedHolding.quantity().compareTo(normalizedQuantity) < 0) {
            throw new BusinessRuleViolation(
                    "Insufficient holding quantity: current=" + normalizedHolding.quantity()
                            + ", required=" + normalizedQuantity);
        }
    }
}
