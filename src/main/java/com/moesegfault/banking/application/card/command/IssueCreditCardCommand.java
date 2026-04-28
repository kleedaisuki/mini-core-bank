package com.moesegfault.banking.application.card.command;

import java.util.Objects;

/**
 * @brief 主信用卡发卡命令（Issue Credit Card Command），对应 `credit_card` 主卡写入输入；
 *        Issue-credit-card command aligned with primary-card write input in `credit_card`.
 */
public final class IssueCreditCardCommand {

    /**
     * @brief 持卡客户 ID（Holder Customer ID）；
     *        Holder customer identifier.
     */
    private final String holderCustomerId;

    /**
     * @brief 信用卡账户 ID（Credit Card Account ID）；
     *        Credit card account identifier.
     */
    private final String creditCardAccountId;

    /**
     * @brief 卡号（Card Number）；
     *        Card number.
     */
    private final String cardNo;

    /**
     * @brief 构造主信用卡发卡命令（Construct Issue Credit Card Command）；
     *        Construct issue-credit-card command.
     *
     * @param holderCustomerId   持卡客户 ID（Holder customer ID）。
     * @param creditCardAccountId 信用卡账户 ID（Credit card account ID）。
     * @param cardNo             卡号（Card number）。
     */
    public IssueCreditCardCommand(
            final String holderCustomerId,
            final String creditCardAccountId,
            final String cardNo
    ) {
        this.holderCustomerId = normalize(holderCustomerId, "holderCustomerId");
        this.creditCardAccountId = normalize(creditCardAccountId, "creditCardAccountId");
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
     * @brief 返回信用卡账户 ID（Return Credit Card Account ID）；
     *        Return credit-card account identifier.
     *
     * @return 信用卡账户 ID（Credit card account ID）。
     */
    public String creditCardAccountId() {
        return creditCardAccountId;
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
