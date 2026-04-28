package com.moesegfault.banking.domain.card;

import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 扣账卡实体（Debit Card Entity），映射 `debit_card` 表及其不变量（Invariant）；
 *        Debit card entity mapped to `debit_card` table and invariants.
 */
public final class DebitCard {

    /**
     * @brief 卡片 ID（Card ID）；
     *        Card identifier.
     */
    private final CardId cardId;

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
     * @brief 扣账卡绑定关系（Debit Card Binding）；
     *        Debit card binding.
     */
    private final DebitCardBinding binding;

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
     * @brief 构造扣账卡实体（Construct Debit Card Entity）；
     *        Construct debit card entity.
     *
     * @param cardId           卡片 ID（Card ID）。
     * @param cardNumber       卡号（Card number）。
     * @param holderCustomerId 持卡客户 ID（Holder customer ID）。
     * @param binding          账户绑定（Account binding）。
     * @param cardStatus       卡片状态（Card status）。
     * @param cardExpiry       卡片有效期（Card expiry）。
     */
    private DebitCard(
            final CardId cardId,
            final CardNumber cardNumber,
            final CustomerId holderCustomerId,
            final DebitCardBinding binding,
            final CardStatus cardStatus,
            final CardExpiry cardExpiry
    ) {
        this.cardId = Objects.requireNonNull(cardId, "Card ID must not be null");
        this.cardNumber = Objects.requireNonNull(cardNumber, "Card number must not be null");
        this.holderCustomerId = Objects.requireNonNull(holderCustomerId, "Holder customer ID must not be null");
        this.binding = Objects.requireNonNull(binding, "Debit card binding must not be null");
        this.cardStatus = Objects.requireNonNull(cardStatus, "Card status must not be null");
        this.cardExpiry = Objects.requireNonNull(cardExpiry, "Card expiry must not be null");
    }

    /**
     * @brief 发行新扣账卡（Issue New Debit Card）；
     *        Issue a new debit card.
     *
     * @param cardId           卡片 ID（Card ID）。
     * @param cardNumber       卡号（Card number）。
     * @param holderCustomerId 持卡客户 ID（Holder customer ID）。
     * @param binding          账户绑定（Account binding）。
     * @return 扣账卡实体（Debit card entity）。
     */
    public static DebitCard issue(
            final CardId cardId,
            final CardNumber cardNumber,
            final CustomerId holderCustomerId,
            final DebitCardBinding binding
    ) {
        return new DebitCard(
                cardId,
                cardNumber,
                holderCustomerId,
                binding,
                CardStatus.ACTIVE,
                CardExpiry.issuedNow());
    }

    /**
     * @brief 从持久化状态重建扣账卡（Restore Debit Card from Persistence）；
     *        Restore debit card from persistence state.
     *
     * @param cardId           卡片 ID（Card ID）。
     * @param cardNumber       卡号（Card number）。
     * @param holderCustomerId 持卡客户 ID（Holder customer ID）。
     * @param binding          账户绑定（Account binding）。
     * @param cardStatus       卡片状态（Card status）。
     * @param cardExpiry       卡片有效期（Card expiry）。
     * @return 扣账卡实体（Debit card entity）。
     */
    public static DebitCard restore(
            final CardId cardId,
            final CardNumber cardNumber,
            final CustomerId holderCustomerId,
            final DebitCardBinding binding,
            final CardStatus cardStatus,
            final CardExpiry cardExpiry
    ) {
        return new DebitCard(cardId, cardNumber, holderCustomerId, binding, cardStatus, cardExpiry);
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
            throw new BusinessRuleViolation("Only BLOCKED debit card can be activated");
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
     * @brief 标记卡片过期（Mark Card Expired）；
     *        Mark card as expired.
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
     * @brief 构建扣账卡发卡事件（Build Debit Card Issued Event）；
     *        Build debit-card-issued domain event.
     *
     * @return 扣账卡发卡事件（Debit card issued event）。
     */
    public DebitCardIssued issuedEvent() {
        return new DebitCardIssued(
                cardId,
                holderCustomerId,
                binding.savingsAccountId(),
                binding.fxAccountId(),
                cardExpiry.issuedAt());
    }

    /**
     * @brief 返回卡片 ID（Return Card ID）；
     *        Return card ID.
     *
     * @return 卡片 ID（Card ID）。
     */
    public CardId cardId() {
        return cardId;
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
     * @brief 返回绑定关系（Return Debit Card Binding）；
     *        Return debit card binding.
     *
     * @return 扣账卡绑定关系（Debit card binding）。
     */
    public DebitCardBinding binding() {
        return binding;
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
     * @brief 断言卡片非终态（Ensure Card Not Terminal）；
     *        Ensure card status is not terminal.
     *
     * @param operation 操作名称（Operation name）。
     */
    private void ensureNotTerminal(final String operation) {
        if (cardStatus.isTerminal()) {
            throw new BusinessRuleViolation("Cannot " + operation + " when debit card is " + cardStatus);
        }
    }

    /**
     * @brief 断言卡片非过期（Ensure Card Not Expired）；
     *        Ensure card is not expired.
     *
     * @param operation 操作名称（Operation name）。
     */
    private void ensureNotExpired(final String operation) {
        if (cardStatus == CardStatus.EXPIRED) {
            throw new BusinessRuleViolation("Cannot " + operation + " when debit card is EXPIRED");
        }
    }

    /**
     * @brief 断言卡片非关闭（Ensure Card Not Closed）；
     *        Ensure card is not closed.
     *
     * @param operation 操作名称（Operation name）。
     */
    private void ensureNotClosed(final String operation) {
        if (cardStatus == CardStatus.CLOSED) {
            throw new BusinessRuleViolation("Cannot " + operation + " when debit card is CLOSED");
        }
    }
}
