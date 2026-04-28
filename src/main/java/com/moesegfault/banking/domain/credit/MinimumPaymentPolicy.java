package com.moesegfault.banking.domain.credit;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.Money;
import com.moesegfault.banking.domain.shared.Percentage;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @brief 最低还款策略（Minimum Payment Policy），用于计算账单最低还款额；
 *        Minimum-payment policy for calculating statement minimum due.
 */
public final class MinimumPaymentPolicy {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private MinimumPaymentPolicy() {
        // utility class
    }

    /**
     * @brief 根据比例和保底金额计算最低还款额（Calculate Minimum Due by Rate and Floor）；
     *        Calculate minimum due using rate and floor amount.
     *
     * @param totalAmountDue 账单应还总额（Total amount due）。
     * @param minimumRate    最低还款比例（Minimum payment rate）。
     * @param floorAmount    保底金额（Floor amount）。
     * @return 最低还款额（Minimum amount due）。
     */
    public static Money calculate(
            final Money totalAmountDue,
            final Percentage minimumRate,
            final Money floorAmount
    ) {
        final Money normalizedTotal = Objects.requireNonNull(totalAmountDue, "Total amount due must not be null");
        final Percentage normalizedRate = Objects.requireNonNull(minimumRate, "Minimum rate must not be null");
        final Money normalizedFloor = Objects.requireNonNull(floorAmount, "Floor amount must not be null");
        if (normalizedTotal.isNegative()) {
            throw new BusinessRuleViolation("Total amount due must not be negative");
        }
        if (!normalizedFloor.currencyCode().equals(normalizedTotal.currencyCode())) {
            throw new BusinessRuleViolation("Floor amount currency must match total amount due currency");
        }
        if (normalizedFloor.isNegative()) {
            throw new BusinessRuleViolation("Floor amount must not be negative");
        }
        if (normalizedRate.decimalValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleViolation("Minimum payment rate must not be negative");
        }
        final Money rateAmount = normalizedRate.applyTo(normalizedTotal);
        final Money candidate = rateAmount.compareTo(normalizedFloor) >= 0 ? rateAmount : normalizedFloor;
        return candidate.compareTo(normalizedTotal) > 0 ? normalizedTotal : candidate;
    }
}
