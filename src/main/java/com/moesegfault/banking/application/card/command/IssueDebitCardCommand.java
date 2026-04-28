package com.moesegfault.banking.application.card.command;

import java.util.Objects;

/**
 * @brief 主借记卡发卡命令（Issue Debit Card Command），对应 `debit_card` 写入输入；
 *        Issue-debit-card command aligned with `debit_card` write input.
 */
public final class IssueDebitCardCommand {

    /**
     * @brief 持卡客户 ID（Holder Customer ID）；
     *        Holder customer identifier.
     */
    private final String holderCustomerId;

    /**
     * @brief 储蓄账户 ID（Savings Account ID）；
     *        Savings account identifier.
     */
    private final String savingsAccountId;

    /**
     * @brief 外汇账户 ID（FX Account ID）；
     *        FX account identifier.
     */
    private final String fxAccountId;

    /**
     * @brief 卡号（Card Number）；
     *        Card number.
     */
    private final String cardNo;

    /**
     * @brief 构造主借记卡发卡命令（Construct Issue Debit Card Command）；
     *        Construct issue-debit-card command.
     *
     * @param holderCustomerId 持卡客户 ID（Holder customer ID）。
     * @param savingsAccountId 储蓄账户 ID（Savings account ID）。
     * @param fxAccountId      外汇账户 ID（FX account ID）。
     * @param cardNo           卡号（Card number）。
     */
    public IssueDebitCardCommand(
            final String holderCustomerId,
            final String savingsAccountId,
            final String fxAccountId,
            final String cardNo
    ) {
        this.holderCustomerId = normalize(holderCustomerId, "holderCustomerId");
        this.savingsAccountId = normalize(savingsAccountId, "savingsAccountId");
        this.fxAccountId = normalize(fxAccountId, "fxAccountId");
        this.cardNo = normalize(cardNo, "cardNo");
    }

    /**
     * @brief 返回持卡客户 ID（Return Holder Customer ID）；
     *        Return holder customer identifier.
     *
     * @return 持卡客户 ID（Holder customer ID）。
     */
    public String holderCustomerId() {
        return holderCustomerId;
    }

    /**
     * @brief 返回储蓄账户 ID（Return Savings Account ID）；
     *        Return savings account identifier.
     *
     * @return 储蓄账户 ID（Savings account ID）。
     */
    public String savingsAccountId() {
        return savingsAccountId;
    }

    /**
     * @brief 返回外汇账户 ID（Return FX Account ID）；
     *        Return FX account identifier.
     *
     * @return 外汇账户 ID（FX account ID）。
     */
    public String fxAccountId() {
        return fxAccountId;
    }

    /**
     * @brief 返回卡号（Return Card Number）；
     *        Return card number.
     *
     * @return 卡号（Card number）。
     */
    public String cardNo() {
        return cardNo;
    }

    /**
     * @brief 标准化字符串字段（Normalize String Field）；
     *        Normalize string field.
     *
     * @param rawValue  原始值（Raw value）。
     * @param fieldName 字段名称（Field name）。
     * @return 标准化字段值（Normalized field value）。
     */
    private static String normalize(final String rawValue, final String fieldName) {
        final String normalized = Objects.requireNonNull(rawValue, fieldName + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
