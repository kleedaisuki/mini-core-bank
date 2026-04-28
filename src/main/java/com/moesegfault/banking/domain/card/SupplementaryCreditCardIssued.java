package com.moesegfault.banking.domain.card;

import com.moesegfault.banking.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 信用卡附属卡发卡事件（Supplementary Credit Card Issued Event）；
 *        Supplementary-credit-card-issued event.
 */
public final class SupplementaryCreditCardIssued implements DomainEvent {

    /**
     * @brief 附属信用卡 ID（Supplementary Credit Card ID）；
     *        Supplementary credit card identifier.
     */
    private final CardId supplementaryCreditCardId;

    /**
     * @brief 主信用卡 ID（Primary Credit Card ID）；
     *        Primary credit card identifier.
     */
    private final CardId primaryCreditCardId;

    /**
     * @brief 信用卡账户 ID（Credit Card Account ID）；
     *        Credit card account identifier.
     */
    private final CreditCardAccountId creditCardAccountId;

    /**
     * @brief 持卡客户 ID（Holder Customer ID）；
     *        Holder customer identifier.
     */
    private final CustomerId holderCustomerId;

    /**
     * @brief 事件时间（Occurred Timestamp）；
     *        Event occurrence timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造附属信用卡发卡事件（Construct Supplementary Credit Card Issued Event）；
     *        Construct supplementary credit card issued event.
     *
     * @param supplementaryCreditCardId 附属信用卡 ID（Supplementary credit card ID）。
     * @param primaryCreditCardId       主信用卡 ID（Primary credit card ID）。
     * @param creditCardAccountId       信用卡账户 ID（Credit card account ID）。
     * @param holderCustomerId          持卡客户 ID（Holder customer ID）。
     * @param occurredAt                事件时间（Occurred time）。
     */
    public SupplementaryCreditCardIssued(
            final CardId supplementaryCreditCardId,
            final CardId primaryCreditCardId,
            final CreditCardAccountId creditCardAccountId,
            final CustomerId holderCustomerId,
            final Instant occurredAt
    ) {
        this.supplementaryCreditCardId = Objects.requireNonNull(
                supplementaryCreditCardId,
                "Supplementary credit card ID must not be null");
        this.primaryCreditCardId = Objects.requireNonNull(primaryCreditCardId, "Primary credit card ID must not be null");
        this.creditCardAccountId = Objects.requireNonNull(
                creditCardAccountId,
                "Credit card account ID must not be null");
        this.holderCustomerId = Objects.requireNonNull(holderCustomerId, "Holder customer ID must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
    }

    /**
     * @brief 返回附属信用卡 ID（Return Supplementary Credit Card ID）；
     *        Return supplementary credit card ID.
     *
     * @return 附属信用卡 ID（Supplementary credit card ID）。
     */
    public CardId supplementaryCreditCardId() {
        return supplementaryCreditCardId;
    }

    /**
     * @brief 返回主信用卡 ID（Return Primary Credit Card ID）；
     *        Return primary credit card ID.
     *
     * @return 主信用卡 ID（Primary credit card ID）。
     */
    public CardId primaryCreditCardId() {
        return primaryCreditCardId;
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
     * @brief 返回持卡客户 ID（Return Holder Customer ID）；
     *        Return holder customer ID.
     *
     * @return 持卡客户 ID（Holder customer ID）。
     */
    public CustomerId holderCustomerId() {
        return holderCustomerId;
    }

    /**
     * @brief 返回事件时间（Return Occurred Timestamp）；
     *        Return occurred timestamp.
     *
     * @return 事件时间（Occurred timestamp）。
     */
    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
