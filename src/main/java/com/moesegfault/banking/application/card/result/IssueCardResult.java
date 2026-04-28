package com.moesegfault.banking.application.card.result;

import java.time.Instant;
import java.util.Objects;

/**
 * @brief 发卡结果视图（Card Issuance Result View），提供 presentation 需要的稳定输出；
 *        Card issuance result view providing stable output for presentation layer.
 */
public final class IssueCardResult {

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
     * @brief 卡片种类（Card Kind）；
     *        Card kind.
     */
    private final CardKind cardKind;

    /**
     * @brief 卡片状态（Card Status）；
     *        Card status.
     */
    private final String cardStatus;

    /**
     * @brief 持卡客户 ID（Holder Customer ID）；
     *        Holder customer identifier.
     */
    private final String holderCustomerId;

    /**
     * @brief 发卡时间（Issued Timestamp）；
     *        Issued timestamp.
     */
    private final Instant issuedAt;

    /**
     * @brief 主卡 ID（可空）（Primary Card ID, Nullable）；
     *        Primary card identifier, nullable.
     */
    private final String primaryCardId;

    /**
     * @brief 构造发卡结果（Construct Card Issuance Result）；
     *        Construct card issuance result.
     *
     * @param cardId           卡片 ID（Card ID）。
     * @param maskedCardNumber 脱敏卡号（Masked card number）。
     * @param cardKind         卡片种类（Card kind）。
     * @param cardStatus       卡片状态（Card status）。
     * @param holderCustomerId 持卡客户 ID（Holder customer ID）。
     * @param issuedAt         发卡时间（Issued timestamp）。
     * @param primaryCardId    主卡 ID（Primary card ID, nullable）。
     */
    public IssueCardResult(
            final String cardId,
            final String maskedCardNumber,
            final CardKind cardKind,
            final String cardStatus,
            final String holderCustomerId,
            final Instant issuedAt,
            final String primaryCardId
    ) {
        this.cardId = Objects.requireNonNull(cardId, "cardId must not be null");
        this.maskedCardNumber = Objects.requireNonNull(maskedCardNumber, "maskedCardNumber must not be null");
        this.cardKind = Objects.requireNonNull(cardKind, "cardKind must not be null");
        this.cardStatus = Objects.requireNonNull(cardStatus, "cardStatus must not be null");
        this.holderCustomerId = Objects.requireNonNull(holderCustomerId, "holderCustomerId must not be null");
        this.issuedAt = Objects.requireNonNull(issuedAt, "issuedAt must not be null");
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
     * @brief 返回卡片种类（Return Card Kind）；
     *        Return card kind.
     *
     * @return 卡片种类（Card kind）。
     */
    public CardKind cardKind() {
        return cardKind;
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
     * @brief 返回持卡客户 ID（Return Holder Customer ID）；
     *        Return holder customer identifier.
     *
     * @return 持卡客户 ID（Holder customer ID）。
     */
    public String holderCustomerId() {
        return holderCustomerId;
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
     * @brief 返回主卡 ID（可空）（Return Primary Card ID, Nullable）；
     *        Return primary card ID, nullable.
     *
     * @return 主卡 ID 或 null（Primary card ID or null）。
     */
    public String primaryCardIdOrNull() {
        return primaryCardId;
    }
}
