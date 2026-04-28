package com.moesegfault.banking.application.card.result;

import java.time.Instant;
import java.util.Objects;

/**
 * @brief 卡片查询结果视图（Card Query Result View），统一三张卡表的输出语义；
 *        Card query result view unifying output semantics across three card tables.
 */
public final class CardResult {

    /**
     * @brief 卡片 ID（Card ID）；
     *        Card identifier.
     */
    private final String cardId;

    /**
     * @brief 脱敏卡号（Masked Card Number）；
     *        Masked card number.
     */
    private final String maskedCardNumber;

    /**
     * @brief 持卡客户 ID（Holder Customer ID）；
     *        Holder customer identifier.
     */
    private final String holderCustomerId;

    /**
     * @brief 卡片状态（Card Status）；
     *        Card status.
     */
    private final String cardStatus;

    /**
     * @brief 卡片种类（Card Kind）；
     *        Card kind.
     */
    private final CardKind cardKind;

    /**
     * @brief 发卡时间（Issued Timestamp）；
     *        Issued timestamp.
     */
    private final Instant issuedAt;

    /**
     * @brief 过期时间（可空）（Expired Timestamp, Nullable）；
     *        Expired timestamp, nullable.
     */
    private final Instant expiredAt;

    /**
     * @brief 储蓄账户 ID（可空）（Savings Account ID, Nullable）；
     *        Savings account identifier, nullable.
     */
    private final String savingsAccountId;

    /**
     * @brief 外汇账户 ID（可空）（FX Account ID, Nullable）；
     *        FX account identifier, nullable.
     */
    private final String fxAccountId;

    /**
     * @brief 信用卡账户 ID（可空）（Credit Card Account ID, Nullable）；
     *        Credit card account identifier, nullable.
     */
    private final String creditCardAccountId;

    /**
     * @brief 主卡 ID（可空）（Primary Card ID, Nullable）；
     *        Primary card identifier, nullable.
     */
    private final String primaryCardId;

    /**
     * @brief 构造卡片查询结果（Construct Card Query Result）；
     *        Construct card query result.
     *
     * @param cardId              卡片 ID（Card ID）。
     * @param maskedCardNumber    脱敏卡号（Masked card number）。
     * @param holderCustomerId    持卡客户 ID（Holder customer ID）。
     * @param cardStatus          卡片状态（Card status）。
     * @param cardKind            卡片种类（Card kind）。
     * @param issuedAt            发卡时间（Issued timestamp）。
     * @param expiredAt           过期时间（Expired timestamp, nullable）。
     * @param savingsAccountId    储蓄账户 ID（Savings account ID, nullable）。
     * @param fxAccountId         外汇账户 ID（FX account ID, nullable）。
     * @param creditCardAccountId 信用卡账户 ID（Credit card account ID, nullable）。
     * @param primaryCardId       主卡 ID（Primary card ID, nullable）。
     */
    public CardResult(
            final String cardId,
            final String maskedCardNumber,
            final String holderCustomerId,
            final String cardStatus,
            final CardKind cardKind,
            final Instant issuedAt,
            final Instant expiredAt,
            final String savingsAccountId,
            final String fxAccountId,
            final String creditCardAccountId,
            final String primaryCardId
    ) {
        this.cardId = Objects.requireNonNull(cardId, "cardId must not be null");
        this.maskedCardNumber = Objects.requireNonNull(maskedCardNumber, "maskedCardNumber must not be null");
        this.holderCustomerId = Objects.requireNonNull(holderCustomerId, "holderCustomerId must not be null");
        this.cardStatus = Objects.requireNonNull(cardStatus, "cardStatus must not be null");
        this.cardKind = Objects.requireNonNull(cardKind, "cardKind must not be null");
        this.issuedAt = Objects.requireNonNull(issuedAt, "issuedAt must not be null");
        this.expiredAt = expiredAt;
        this.savingsAccountId = savingsAccountId;
        this.fxAccountId = fxAccountId;
        this.creditCardAccountId = creditCardAccountId;
        this.primaryCardId = primaryCardId;
    }

    /**
     * @brief 返回卡片 ID（Return Card ID）；
     *        Return card identifier.
     *
     * @return 卡片 ID（Card ID）。
     */
    public String cardId() {
        return cardId;
    }

    /**
     * @brief 返回脱敏卡号（Return Masked Card Number）；
     *        Return masked card number.
     *
     * @return 脱敏卡号（Masked card number）。
     */
    public String maskedCardNumber() {
        return maskedCardNumber;
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
     * @brief 返回卡片状态（Return Card Status）；
     *        Return card status.
     *
     * @return 卡片状态（Card status）。
     */
    public String cardStatus() {
        return cardStatus;
    }

    /**
     * @brief 返回卡片种类（Return Card Kind）；
     *        Return card kind.
     *
     * @return 卡片种类（Card kind）。
     */
    public CardKind cardKind() {
        return cardKind;
    }

    /**
     * @brief 返回发卡时间（Return Issued Timestamp）；
     *        Return issued timestamp.
     *
     * @return 发卡时间（Issued timestamp）。
     */
    public Instant issuedAt() {
        return issuedAt;
    }

    /**
     * @brief 返回过期时间（可空）（Return Expired Timestamp, Nullable）；
     *        Return expired timestamp, nullable.
     *
     * @return 过期时间或 null（Expired timestamp or null）。
     */
    public Instant expiredAtOrNull() {
        return expiredAt;
    }

    /**
     * @brief 返回储蓄账户 ID（可空）（Return Savings Account ID, Nullable）；
     *        Return savings account identifier, nullable.
     *
     * @return 储蓄账户 ID 或 null（Savings account ID or null）。
     */
    public String savingsAccountIdOrNull() {
        return savingsAccountId;
    }

    /**
     * @brief 返回外汇账户 ID（可空）（Return FX Account ID, Nullable）；
     *        Return FX account identifier, nullable.
     *
     * @return 外汇账户 ID 或 null（FX account ID or null）。
     */
    public String fxAccountIdOrNull() {
        return fxAccountId;
    }

    /**
     * @brief 返回信用卡账户 ID（可空）（Return Credit Card Account ID, Nullable）；
     *        Return credit-card account identifier, nullable.
     *
     * @return 信用卡账户 ID 或 null（Credit-card account ID or null）。
     */
    public String creditCardAccountIdOrNull() {
        return creditCardAccountId;
    }

    /**
     * @brief 返回主卡 ID（可空）（Return Primary Card ID, Nullable）；
     *        Return primary card identifier, nullable.
     *
     * @return 主卡 ID 或 null（Primary card ID or null）。
     */
    public String primaryCardIdOrNull() {
        return primaryCardId;
    }
}
