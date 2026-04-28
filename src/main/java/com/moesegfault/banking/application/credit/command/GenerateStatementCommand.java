package com.moesegfault.banking.application.credit.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @brief 生成账单命令（Generate Statement Command），定义信用卡账单生成输入参数；
 *        Generate-statement command defining input parameters for credit-card statement generation.
 */
public final class GenerateStatementCommand {

    /**
     * @brief 信用卡账户 ID（Credit Card Account ID）；
     *        Credit-card-account identifier.
     */
    private final String creditCardAccountId;

    /**
     * @brief 出账日（Statement Date）；
     *        Statement date.
     */
    private final LocalDate statementDate;

    /**
     * @brief 最低还款比例小数值（Minimum Payment Rate Decimal）；
     *        Minimum-payment-rate decimal value.
     */
    private final BigDecimal minimumPaymentRateDecimal;

    /**
     * @brief 最低还款保底金额（Minimum Payment Floor Amount）；
     *        Minimum-payment floor amount.
     */
    private final BigDecimal minimumPaymentFloorAmount;

    /**
     * @brief 构造生成账单命令（Construct Generate Statement Command）；
     *        Construct generate-statement command.
     *
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @param statementDate 出账日期（Statement date）。
     * @param minimumPaymentRateDecimal 最低还款比例小数值（Minimum-payment-rate decimal）。
     * @param minimumPaymentFloorAmount 最低还款保底金额（Minimum-payment floor amount）。
     */
    public GenerateStatementCommand(
            final String creditCardAccountId,
            final LocalDate statementDate,
            final BigDecimal minimumPaymentRateDecimal,
            final BigDecimal minimumPaymentFloorAmount
    ) {
        this.creditCardAccountId = requireText(creditCardAccountId, "creditCardAccountId");
        this.statementDate = Objects.requireNonNull(statementDate, "statementDate must not be null");
        this.minimumPaymentRateDecimal = Objects.requireNonNull(
                minimumPaymentRateDecimal,
                "minimumPaymentRateDecimal must not be null");
        this.minimumPaymentFloorAmount = Objects.requireNonNull(
                minimumPaymentFloorAmount,
                "minimumPaymentFloorAmount must not be null");
    }

    /**
     * @brief 返回信用卡账户 ID（Return Credit Card Account ID）；
     *        Return credit-card-account identifier.
     *
     * @return 信用卡账户 ID（Credit-card-account ID）。
     */
    public String creditCardAccountId() {
        return creditCardAccountId;
    }

    /**
     * @brief 返回出账日（Return Statement Date）；
     *        Return statement date.
     *
     * @return 出账日（Statement date）。
     */
    public LocalDate statementDate() {
        return statementDate;
    }

    /**
     * @brief 返回最低还款比例小数值（Return Minimum Payment Rate Decimal）；
     *        Return minimum-payment-rate decimal value.
     *
     * @return 最低还款比例小数值（Minimum-payment-rate decimal）。
     */
    public BigDecimal minimumPaymentRateDecimal() {
        return minimumPaymentRateDecimal;
    }

    /**
     * @brief 返回最低还款保底金额（Return Minimum Payment Floor Amount）；
     *        Return minimum-payment floor amount.
     *
     * @return 最低还款保底金额（Minimum-payment floor amount）。
     */
    public BigDecimal minimumPaymentFloorAmount() {
        return minimumPaymentFloorAmount;
    }

    /**
     * @brief 校验非空文本（Require Non-blank Text）；
     *        Require non-blank text value.
     *
     * @param value 输入值（Input value）。
     * @param fieldName 字段名（Field name）。
     * @return 归一化文本（Normalized text）。
     */
    private static String requireText(
            final String value,
            final String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
