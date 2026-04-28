package com.moesegfault.banking.domain.card;

import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 信用卡实体（Credit Card Entity），映射 `credit_card` 表并维护主副卡一致性；
 *        Credit card entity mapped to `credit_card` table with role consistency.
 */
public final class CreditCard {

    /**
     * @brief 信用卡 ID（Credit Card ID）；
     *        Credit card identifier.
     */
    private final CardId creditCardId;

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
     * @brief 信用卡账户 ID（Credit Card Account ID）；
     *        Credit card account identifier.
     */
    private final CreditCardAccountId creditCardAccountId;

    /**
     * @brief 卡角色（Card Role）；
     *        Card role.
     */
    private final CardRole cardRole;

    /**
     * @brief 主信用卡 ID（Primary Credit Card ID）；
     *        Primary credit card identifier for supplementary cards.
     */
    private final CardId primaryCreditCardId;

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
     * @brief 构造信用卡实体（Construct Credit Card Entity）；
     *        Construct credit card entity.
     *
     * @param creditCardId         信用卡 ID（Credit card ID）。
     * @param cardNumber           卡号（Card number）。
     * @param holderCustomerId     持卡客户 ID（Holder customer ID）。
     * @param creditCardAccountId  信用卡账户 ID（Credit card account ID）。
     * @param cardRole             卡角色（Card role）。
     * @param primaryCreditCardId  主信用卡 ID（Primary credit card ID, nullable）。
     * @param cardStatus           卡片状态（Card status）。
     * @param cardExpiry           卡片有效期（Card expiry）。
     */
    private CreditCard(
            final CardId creditCardId,
            final CardNumber cardNumber,
            final CustomerId holderCustomerId,
            final CreditCardAccountId creditCardAccountId,
            final CardRole cardRole,
            final CardId primaryCreditCardId,
            final CardStatus cardStatus,
            final CardExpiry cardExpiry
    ) {
        this.creditCardId = Objects.requireNonNull(creditCardId, "Credit card ID must not be null");
        this.cardNumber = Objects.requireNonNull(cardNumber, "Card number must not be null");
        this.holderCustomerId = Objects.requireNonNull(holderCustomerId, "Holder customer ID must not be null");
        this.creditCardAccountId = Objects.requireNonNull(
                creditCardAccountId,
                "Credit card account ID must not be null");
        this.cardRole = Objects.requireNonNull(cardRole, "Card role must not be null");
        this.primaryCreditCardId = primaryCreditCardId;
        this.cardStatus = Objects.requireNonNull(cardStatus, "Card status must not be null");
        this.cardExpiry = Objects.requireNonNull(cardExpiry, "Card expiry must not be null");
        ensureRoleParentConsistency(this.cardRole, this.primaryCreditCardId);
    }

    /**
     * @brief 发行主信用卡（Issue Primary Credit Card）；
     *        Issue primary credit card.
     *
     * @param creditCardId        信用卡 ID（Credit card ID）。
     * @param cardNumber          卡号（Card number）。
     * @param holderCustomerId    持卡客户 ID（Holder customer ID）。
     * @param creditCardAccountId 信用卡账户 ID（Credit card account ID）。
     * @return 信用卡实体（Credit card entity）。
     */
    public static CreditCard issuePrimary(
            final CardId creditCardId,
            final CardNumber cardNumber,
            final CustomerId holderCustomerId,
            final CreditCardAccountId creditCardAccountId
    ) {
        return new CreditCard(
                creditCardId,
                cardNumber,
                holderCustomerId,
                creditCardAccountId,
                CardRole.PRIMARY,
                null,
                CardStatus.ACTIVE,
                CardExpiry.issuedNow());
    }

    /**
     * @brief 发行附属信用卡（Issue Supplementary Credit Card）；
     *        Issue supplementary credit card.
     *
     * @param creditCardId         信用卡 ID（Credit card ID）。
     * @param cardNumber           卡号（Card number）。
     * @param holderCustomerId     持卡客户 ID（Holder customer ID）。
     * @param creditCardAccountId  信用卡账户 ID（Credit card account ID）。
     * @param primaryCreditCardId  主信用卡 ID（Primary credit card ID）。
     * @return 信用卡实体（Credit card entity）。
     */
    public static CreditCard issueSupplementary(
            final CardId creditCardId,
            final CardNumber cardNumber,
            final CustomerId holderCustomerId,
            final CreditCardAccountId creditCardAccountId,
            final CardId primaryCreditCardId
    ) {
        return new CreditCard(
                creditCardId,
                cardNumber,
                holderCustomerId,
                creditCardAccountId,
                CardRole.SUPPLEMENTARY,
                Objects.requireNonNull(primaryCreditCardId, "Primary credit card ID must not be null"),
                CardStatus.ACTIVE,
                CardExpiry.issuedNow());
    }

    /**
     * @brief 从持久化状态重建信用卡（Restore Credit Card）；
     *        Restore credit card from persistence state.
     *
     * @param creditCardId         信用卡 ID（Credit card ID）。
     * @param cardNumber           卡号（Card number）。
     * @param holderCustomerId     持卡客户 ID（Holder customer ID）。
     * @param creditCardAccountId  信用卡账户 ID（Credit card account ID）。
     * @param cardRole             卡角色（Card role）。
     * @param primaryCreditCardId  主信用卡 ID（Primary credit card ID, nullable）。
     * @param cardStatus           卡片状态（Card status）。
     * @param cardExpiry           卡片有效期（Card expiry）。
     * @return 信用卡实体（Credit card entity）。
     */
    public static CreditCard restore(
            final CardId creditCardId,
            final CardNumber cardNumber,
            final CustomerId holderCustomerId,
            final CreditCardAccountId creditCardAccountId,
            final CardRole cardRole,
            final CardId primaryCreditCardId,
            final CardStatus cardStatus,
            final CardExpiry cardExpiry
    ) {
        return new CreditCard(
                creditCardId,
                cardNumber,
                holderCustomerId,
                creditCardAccountId,
                cardRole,
                primaryCreditCardId,
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
            throw new BusinessRuleViolation("Only BLOCKED credit card can be activated");
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
     * @brief 判断是否附属卡（Check Supplementary Role）；
     *        Check whether this card is supplementary.
     *
     * @return 附属卡返回 true（true for supplementary card）。
     */
    public boolean isSupplementary() {
        return cardRole == CardRole.SUPPLEMENTARY;
    }

    /**
     * @brief 构建发卡事件（Build Credit Card Issued Event）；
     *        Build credit-card-issued domain event.
     *
     * @return 信用卡发卡事件（Credit card issued event）。
     */
    public CreditCardIssued issuedEvent() {
        return new CreditCardIssued(
                creditCardId,
                creditCardAccountId,
                holderCustomerId,
                cardRole,
                cardExpiry.issuedAt());
    }

    /**
     * @brief 构建附属卡发卡事件（Build Supplementary Credit Card Issued Event）；
     *        Build supplementary-credit-card-issued domain event.
     *
     * @return 附属信用卡发卡事件（Supplementary credit card issued event）。
     */
    public SupplementaryCreditCardIssued supplementaryIssuedEvent() {
        if (!isSupplementary()) {
            throw new BusinessRuleViolation("Only supplementary credit card can emit supplementary issued event");
        }
        return new SupplementaryCreditCardIssued(
                creditCardId,
                Objects.requireNonNull(primaryCreditCardId, "Primary credit card ID must not be null"),
                creditCardAccountId,
                holderCustomerId,
                cardExpiry.issuedAt());
    }

    /**
     * @brief 返回信用卡 ID（Return Credit Card ID）；
     *        Return credit card ID.
     *
     * @return 信用卡 ID（Credit card ID）。
     */
    public CardId creditCardId() {
        return creditCardId;
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
     * @brief 返回信用卡账户 ID（Return Credit Card Account ID）；
     *        Return credit card account ID.
     *
     * @return 信用卡账户 ID（Credit card account ID）。
     */
    public CreditCardAccountId creditCardAccountId() {
        return creditCardAccountId;
    }

    /**
     * @brief 返回卡角色（Return Card Role）；
     *        Return card role.
     *
     * @return 卡角色（Card role）。
     */
    public CardRole cardRole() {
        return cardRole;
    }

    /**
     * @brief 返回主卡 ID（可空）（Return Optional Primary Card ID）；
     *        Return primary card ID, nullable.
     *
     * @return 主卡 ID 或 null（Primary card ID or null）。
     */
    public CardId primaryCreditCardIdOrNull() {
        return primaryCreditCardId;
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
     * @brief 校验角色和主卡一致性（Ensure Role-parent Consistency）；
     *        Ensure consistency between role and primary-card linkage.
     *
     * @param role            卡角色（Card role）。
     * @param primaryCardId   主卡 ID（Primary card ID, nullable）。
     */
    private static void ensureRoleParentConsistency(
            final CardRole role,
            final CardId primaryCardId
    ) {
        if (role == CardRole.PRIMARY && primaryCardId != null) {
            throw new BusinessRuleViolation("PRIMARY credit card must not have primary_credit_card_id");
        }
        if (role == CardRole.SUPPLEMENTARY && primaryCardId == null) {
            throw new BusinessRuleViolation("SUPPLEMENTARY credit card must have primary_credit_card_id");
        }
    }

    /**
     * @brief 断言非终态（Ensure Non-terminal State）；
     *        Ensure card is not in terminal status.
     *
     * @param operation 操作名称（Operation name）。
     */
    private void ensureNotTerminal(final String operation) {
        if (cardStatus.isTerminal()) {
            throw new BusinessRuleViolation("Cannot " + operation + " when credit card is " + cardStatus);
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
            throw new BusinessRuleViolation("Cannot " + operation + " when credit card is EXPIRED");
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
            throw new BusinessRuleViolation("Cannot " + operation + " when credit card is CLOSED");
        }
    }
}
