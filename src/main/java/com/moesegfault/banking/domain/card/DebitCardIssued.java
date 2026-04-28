package com.moesegfault.banking.domain.card;

import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 扣账卡发卡事件（Debit Card Issued Event），表示主扣账卡发行完成；
 *        Debit-card-issued event indicating primary debit card issuance completion.
 */
public final class DebitCardIssued implements DomainEvent {

    /**
     * @brief 卡片 ID（Card ID）；
     *        Issued card identifier.
     */
    private final CardId cardId;

    /**
     * @brief 持卡客户 ID（Holder Customer ID）；
     *        Holder customer identifier.
     */
    private final CustomerId holderCustomerId;

    /**
     * @brief 储蓄账户 ID（Savings Account ID）；
     *        Bound savings account ID.
     */
    private final SavingsAccountId savingsAccountId;

    /**
     * @brief 外汇账户 ID（FX Account ID）；
     *        Bound FX account ID.
     */
    private final FxAccountId fxAccountId;

    /**
     * @brief 事件时间（Occurred Timestamp）；
     *        Event occurrence timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造扣账卡发卡事件（Construct Debit Card Issued Event）；
     *        Construct debit card issued event.
     *
     * @param cardId           卡片 ID（Card ID）。
     * @param holderCustomerId 持卡客户 ID（Holder customer ID）。
     * @param savingsAccountId 储蓄账户 ID（Savings account ID）。
     * @param fxAccountId      外汇账户 ID（FX account ID）。
     * @param occurredAt       事件时间（Occurred time）。
     */
    public DebitCardIssued(
            final CardId cardId,
            final CustomerId holderCustomerId,
            final SavingsAccountId savingsAccountId,
            final FxAccountId fxAccountId,
            final Instant occurredAt
    ) {
        this.cardId = Objects.requireNonNull(cardId, "Card ID must not be null");
        this.holderCustomerId = Objects.requireNonNull(holderCustomerId, "Holder customer ID must not be null");
        this.savingsAccountId = Objects.requireNonNull(savingsAccountId, "Savings account ID must not be null");
        this.fxAccountId = Objects.requireNonNull(fxAccountId, "FX account ID must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
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
     * @brief 返回持卡客户 ID（Return Holder Customer ID）；
     *        Return holder customer ID.
     *
     * @return 持卡客户 ID（Holder customer ID）。
     */
    public CustomerId holderCustomerId() {
        return holderCustomerId;
    }

    /**
     * @brief 返回储蓄账户 ID（Return Savings Account ID）；
     *        Return savings account ID.
     *
     * @return 储蓄账户 ID（Savings account ID）。
     */
    public SavingsAccountId savingsAccountId() {
        return savingsAccountId;
    }

    /**
     * @brief 返回外汇账户 ID（Return FX Account ID）；
     *        Return FX account ID.
     *
     * @return 外汇账户 ID（FX account ID）。
     */
    public FxAccountId fxAccountId() {
        return fxAccountId;
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
