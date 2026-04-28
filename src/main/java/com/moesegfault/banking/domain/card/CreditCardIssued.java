package com.moesegfault.banking.domain.card;

import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 信用卡发卡事件（Credit Card Issued Event），表示主卡或附属卡发行；
 *        Credit-card-issued event indicating issuance of primary or supplementary card.
 */
public final class CreditCardIssued implements DomainEvent {

    /**
     * @brief 信用卡 ID（Credit Card ID）；
     *        Credit card identifier.
     */
    private final CardId creditCardId;

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
     * @brief 卡角色（Card Role）；
     *        Card role.
     */
    private final CardRole cardRole;

    /**
     * @brief 事件时间（Occurred Timestamp）；
     *        Event occurrence timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造信用卡发卡事件（Construct Credit Card Issued Event）；
     *        Construct credit card issued event.
     *
     * @param creditCardId        信用卡 ID（Credit card ID）。
     * @param creditCardAccountId 信用卡账户 ID（Credit card account ID）。
     * @param holderCustomerId    持卡客户 ID（Holder customer ID）。
     * @param cardRole            卡角色（Card role）。
     * @param occurredAt          事件时间（Occurred time）。
     */
    public CreditCardIssued(
            final CardId creditCardId,
            final CreditCardAccountId creditCardAccountId,
            final CustomerId holderCustomerId,
            final CardRole cardRole,
            final Instant occurredAt
    ) {
        this.creditCardId = Objects.requireNonNull(creditCardId, "Credit card ID must not be null");
        this.creditCardAccountId = Objects.requireNonNull(
                creditCardAccountId,
                "Credit card account ID must not be null");
        this.holderCustomerId = Objects.requireNonNull(holderCustomerId, "Holder customer ID must not be null");
        this.cardRole = Objects.requireNonNull(cardRole, "Card role must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
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
     * @brief 返回卡角色（Return Card Role）；
     *        Return card role.
     *
     * @return 卡角色（Card role）。
     */
    public CardRole cardRole() {
        return cardRole;
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
