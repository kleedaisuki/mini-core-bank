package com.moesegfault.banking.application.card.command;

import java.util.Objects;

/**
 * @brief 借记附属卡发卡命令（Issue Supplementary Debit Card Command），对应 `supplementary_debit_card` 写入输入；
 *        Issue-supplementary-debit-card command aligned with `supplementary_debit_card` write input.
 */
public final class IssueSupplementaryDebitCardCommand {

    /**
     * @brief 持卡客户 ID（Holder Customer ID）；
     *        Holder customer identifier.
     */
    private final String holderCustomerId;

    /**
     * @brief 主借记卡 ID（Primary Debit Card ID）；
     *        Primary debit card identifier.
     */
    private final String primaryDebitCardId;

    /**
     * @brief 卡号（Card Number）；
     *        Card number.
     */
    private final String cardNo;

    /**
     * @brief 构造借记附属卡发卡命令（Construct Issue Supplementary Debit Card Command）；
     *        Construct issue-supplementary-debit-card command.
     *
     * @param holderCustomerId  持卡客户 ID（Holder customer ID）。
     * @param primaryDebitCardId 主借记卡 ID（Primary debit card ID）。
     * @param cardNo            卡号（Card number）。
     */
    public IssueSupplementaryDebitCardCommand(
            final String holderCustomerId,
            final String primaryDebitCardId,
            final String cardNo
    ) {
        this.holderCustomerId = normalize(holderCustomerId, "holderCustomerId");
        this.primaryDebitCardId = normalize(primaryDebitCardId, "primaryDebitCardId");
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
     * @brief 返回主借记卡 ID（Return Primary Debit Card ID）；
     *        Return primary debit card identifier.
     *
     * @return 主借记卡 ID（Primary debit card ID）。
     */
    public String primaryDebitCardId() {
        return primaryDebitCardId;
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
