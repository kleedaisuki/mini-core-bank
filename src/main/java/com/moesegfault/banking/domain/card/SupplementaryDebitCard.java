package com.moesegfault.banking.domain.card;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 扣账附属卡实体（Supplementary Debit Card Entity），映射 `supplementary_debit_card` 表；
 *        Supplementary debit card entity mapped to `supplementary_debit_card` table.
 */
public final class SupplementaryDebitCard {

    /**
     * @brief 附属卡 ID（Supplementary Card ID）；
     *        Supplementary card identifier.
     */
    private final CardId supplementaryCardId;

    /**
     * @brief 卡号（Card Number）；
     *        Card number.
     */
    private final CardNumber cardNumber;

    /**
     * @brief 持卡客户 ID（Holder Customer ID）；
     *        Holder customer identifier.
     */
    private final CustomerId holderCustomerId;

    /**
     * @brief 主扣账卡 ID（Primary Debit Card ID）；
     *        Primary debit card identifier.
     */
    private final CardId primaryDebitCardId;

    /**
     * @brief 卡片状态（Card Status）；
     *        Current card status.
     */
    private CardStatus cardStatus;

    /**
     * @brief 有效期（Card Expiry）；
     *        Card expiry information.
     */
    private CardExpiry cardExpiry;

    /**
     * @brief 构造附属扣账卡（Construct Supplementary Debit Card）；
     *        Construct supplementary debit card.
     *
     * @param supplementaryCardId 附属卡 ID（Supplementary card ID）。
     * @param cardNumber          卡号（Card number）。
     * @param holderCustomerId    持卡客户 ID（Holder customer ID）。
     * @param primaryDebitCardId  主扣账卡 ID（Primary debit card ID）。
     * @param cardStatus          卡片状态（Card status）。
     * @param cardExpiry          卡片有效期（Card expiry）。
     */
    private SupplementaryDebitCard(
            final CardId supplementaryCardId,
            final CardNumber cardNumber,
            final CustomerId holderCustomerId,
            final CardId primaryDebitCardId,
            final CardStatus cardStatus,
            final CardExpiry cardExpiry
    ) {
        this.supplementaryCardId = Objects.requireNonNull(
                supplementaryCardId,
                "Supplementary card ID must not be null");
        this.cardNumber = Objects.requireNonNull(cardNumber, "Card number must not be null");
        this.holderCustomerId = Objects.requireNonNull(holderCustomerId, "Holder customer ID must not be null");
        this.primaryDebitCardId = Objects.requireNonNull(primaryDebitCardId, "Primary debit card ID must not be null");
        this.cardStatus = Objects.requireNonNull(cardStatus, "Card status must not be null");
        this.cardExpiry = Objects.requireNonNull(cardExpiry, "Card expiry must not be null");
    }

    /**
     * @brief 发行附属扣账卡（Issue Supplementary Debit Card）；
     *        Issue supplementary debit card.
     *
     * @param supplementaryCardId 附属卡 ID（Supplementary card ID）。
     * @param cardNumber          卡号（Card number）。
     * @param holderCustomerId    持卡客户 ID（Holder customer ID）。
     * @param primaryDebitCardId  主扣账卡 ID（Primary debit card ID）。
     * @return 附属扣账卡实体（Supplementary debit card entity）。
     */
    public static SupplementaryDebitCard issue(
            final CardId supplementaryCardId,
            final CardNumber cardNumber,
            final CustomerId holderCustomerId,
            final CardId primaryDebitCardId
    ) {
        return new SupplementaryDebitCard(
                supplementaryCardId,
                cardNumber,
                holderCustomerId,
                primaryDebitCardId,
                CardStatus.ACTIVE,
                CardExpiry.issuedNow());
    }

    /**
     * @brief 从持久化状态重建附属扣账卡（Restore Supplementary Debit Card）；
     *        Restore supplementary debit card from persistence state.
     *
     * @param supplementaryCardId 附属卡 ID（Supplementary card ID）。
     * @param cardNumber          卡号（Card number）。
     * @param holderCustomerId    持卡客户 ID（Holder customer ID）。
     * @param primaryDebitCardId  主扣账卡 ID（Primary debit card ID）。
     * @param cardStatus          卡片状态（Card status）。
     * @param cardExpiry          卡片有效期（Card expiry）。
     * @return 附属扣账卡实体（Supplementary debit card entity）。
     */
    public static SupplementaryDebitCard restore(
            final CardId supplementaryCardId,
            final CardNumber cardNumber,
            final CustomerId holderCustomerId,
            final CardId primaryDebitCardId,
            final CardStatus cardStatus,
            final CardExpiry cardExpiry
    ) {
        return new SupplementaryDebitCard(
                supplementaryCardId,
                cardNumber,
                holderCustomerId,
                primaryDebitCardId,
                cardStatus,
                cardExpiry);
    }

    /**
     * @brief 阻断卡片（Block Card）；
     *        Block card.
     */
    public void block() {
        if (cardStatus == CardStatus.BLOCKED) {
            return;
        }
        ensureNotTerminal("block card");
        cardStatus = CardStatus.BLOCKED;
    }

    /**
     * @brief 激活卡片（Activate Card）；
     *        Activate blocked card.
     */
    public void activate() {
        if (cardStatus == CardStatus.ACTIVE) {
            return;
        }
        if (cardStatus != CardStatus.BLOCKED) {
            throw new BusinessRuleViolation("Only BLOCKED supplementary debit card can be activated");
        }
        cardStatus = CardStatus.ACTIVE;
    }

    /**
     * @brief 关闭卡片（Close Card）；
     *        Close card.
     */
    public void close() {
        if (cardStatus == CardStatus.CLOSED) {
            return;
        }
        ensureNotExpired("close card");
        cardStatus = CardStatus.CLOSED;
    }

    /**
     * @brief 标记过期（Mark Expired）；
     *        Mark card expired.
     *
     * @param expiredAt 过期时间（Expired timestamp）。
     */
    public void markExpired(final Instant expiredAt) {
        if (cardStatus == CardStatus.EXPIRED) {
            return;
        }
        ensureNotClosed("mark expired");
        cardExpiry = cardExpiry.expireAt(expiredAt);
        cardStatus = CardStatus.EXPIRED;
    }

    /**
     * @brief 构建附属扣账卡发卡事件（Build Supplementary Debit Card Issued Event）；
     *        Build supplementary-debit-card-issued event.
     *
     * @return 附属扣账卡发卡事件（Supplementary debit card issued event）。
     */
    public SupplementaryDebitCardIssued issuedEvent() {
        return new SupplementaryDebitCardIssued(
                supplementaryCardId,
                primaryDebitCardId,
                holderCustomerId,
                cardExpiry.issuedAt());
    }

    /**
     * @brief 返回附属卡 ID（Return Supplementary Card ID）；
     *        Return supplementary card ID.
     *
     * @return 附属卡 ID（Supplementary card ID）。
     */
    public CardId supplementaryCardId() {
        return supplementaryCardId;
    }

    /**
     * @brief 返回卡号（Return Card Number）；
     *        Return card number.
     *
     * @return 卡号（Card number）。
     */
    public CardNumber cardNumber() {
        return cardNumber;
    }

    /**
     * @brief 返回持卡客户 ID（Return Holder Customer ID）；
     *        Return holder customer ID.
     *
     * @return 持卡客户 ID（Holder customer ID）。
     */
    public CustomerId holderCustomerId() {
        return holderCustomerId;
    }

    /**
     * @brief 返回主扣账卡 ID（Return Primary Debit Card ID）；
     *        Return primary debit card ID.
     *
     * @return 主扣账卡 ID（Primary debit card ID）。
     */
    public CardId primaryDebitCardId() {
        return primaryDebitCardId;
    }

    /**
     * @brief 返回卡片状态（Return Card Status）；
     *        Return card status.
     *
     * @return 卡片状态（Card status）。
     */
    public CardStatus cardStatus() {
        return cardStatus;
    }

    /**
     * @brief 返回发卡时间（Return Issued Time）；
     *        Return issued time.
     *
     * @return 发卡时间（Issued time）。
     */
    public Instant issuedAt() {
        return cardExpiry.issuedAt();
    }

    /**
     * @brief 返回过期时间（可空）（Return Optional Expired Time）；
     *        Return optional expired time.
     *
     * @return 过期时间或 null（Expired time or null）。
     */
    public Instant expiredAtOrNull() {
        return cardExpiry.expiredAtOrNull();
    }

    /**
     * @brief 断言非终态（Ensure Non-terminal State）；
     *        Ensure current status is non-terminal.
     *
     * @param operation 操作名称（Operation name）。
     */
    private void ensureNotTerminal(final String operation) {
        if (cardStatus.isTerminal()) {
            throw new BusinessRuleViolation(
                    "Cannot " + operation + " when supplementary debit card is " + cardStatus);
        }
    }

    /**
     * @brief 断言非过期（Ensure Not Expired）；
     *        Ensure card is not expired.
     *
     * @param operation 操作名称（Operation name）。
     */
    private void ensureNotExpired(final String operation) {
        if (cardStatus == CardStatus.EXPIRED) {
            throw new BusinessRuleViolation(
                    "Cannot " + operation + " when supplementary debit card is EXPIRED");
        }
    }

    /**
     * @brief 断言非关闭（Ensure Not Closed）；
     *        Ensure card is not closed.
     *
     * @param operation 操作名称（Operation name）。
     */
    private void ensureNotClosed(final String operation) {
        if (cardStatus == CardStatus.CLOSED) {
            throw new BusinessRuleViolation(
                    "Cannot " + operation + " when supplementary debit card is CLOSED");
        }
    }
}
