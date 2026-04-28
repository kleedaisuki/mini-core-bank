package com.moesegfault.banking.domain.investment;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.util.Objects;

/**
 * @brief 适当性策略（Suitability Policy），校验客户风险承受等级与产品风险等级匹配关系；
 *        Suitability policy validating match between customer risk tolerance and product risk level.
 */
public final class SuitabilityPolicy {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private SuitabilityPolicy() {
        // utility class
    }

    /**
     * @brief 判断客户是否可购买产品（Check Product Suitability）；
     *        Check whether customer risk tolerance can cover product risk.
     *
     * @param customerRiskTolerance 客户风险承受等级（Customer risk tolerance）。
     * @param productRiskLevel      产品风险等级（Product risk level）。
     * @return 可匹配返回 true（true when suitable）。
     */
    public static boolean isSuitable(
            final RiskLevel customerRiskTolerance,
            final RiskLevel productRiskLevel
    ) {
        final RiskLevel normalizedCustomer = Objects.requireNonNull(
                customerRiskTolerance,
                "Customer risk tolerance must not be null");
        final RiskLevel normalizedProduct = Objects.requireNonNull(
                productRiskLevel,
                "Product risk level must not be null");
        return normalizedProduct.isNotHigherThan(normalizedCustomer);
    }

    /**
     * @brief 对产品实体做适当性校验（Ensure Product Suitability）；
     *        Ensure suitability against product entity.
     *
     * @param customerRiskTolerance 客户风险承受等级（Customer risk tolerance）。
     * @param product               产品实体（Investment product）。
     */
    public static void ensureSuitable(
            final RiskLevel customerRiskTolerance,
            final InvestmentProduct product
    ) {
        final InvestmentProduct normalizedProduct = Objects.requireNonNull(product, "Product must not be null");
        if (!isSuitable(customerRiskTolerance, normalizedProduct.riskLevel())) {
            throw new BusinessRuleViolation(
                    "Customer risk tolerance " + customerRiskTolerance
                            + " does not satisfy product risk level " + normalizedProduct.riskLevel());
        }
    }
}
