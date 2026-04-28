package com.moesegfault.banking.domain.card;

import com.moesegfault.banking.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 扣账附属卡发卡事件（Supplementary Debit Card Issued Event）；
 *        Supplementary-debit-card-issued event.
 */
public final class SupplementaryDebitCardIssued implements DomainEvent {

    /**
     * @brief 附属卡 ID（Supplementary Card ID）；
     *        Supplementary card identifier.
     */
    private final CardId supplementaryCardId;

    /**
     * @brief 主扣账卡 ID（Primary Debit Card ID）；
     *        Primary debit card identifier.
     */
    private final CardId primaryDebitCardId;

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
     * @brief 构造附属扣账卡发卡事件（Construct Supplementary Debit Card Issued Event）；
     *        Construct supplementary debit card issued event.
     *
     * @param supplementaryCardId 附属卡 ID（Supplementary card ID）。
     * @param primaryDebitCardId  主扣账卡 ID（Primary debit card ID）。
     * @param holderCustomerId    持卡客户 ID（Holder customer ID）。
     * @param occurredAt          事件时间（Occurred time）。
     */
    public SupplementaryDebitCardIssued(
            final CardId supplementaryCardId,
            final CardId primaryDebitCardId,
            final CustomerId holderCustomerId,
            final Instant occurredAt
    ) {
        this.supplementaryCardId = Objects.requireNonNull(
                supplementaryCardId,
                "Supplementary card ID must not be null");
        this.primaryDebitCardId = Objects.requireNonNull(primaryDebitCardId, "Primary debit card ID must not be null");
        this.holderCustomerId = Objects.requireNonNull(holderCustomerId, "Holder customer ID must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
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
     * @brief 返回主扣账卡 ID（Return Primary Debit Card ID）；
     *        Return primary debit card ID.
     *
     * @return 主扣账卡 ID（Primary debit card ID）。
     */
    public CardId primaryDebitCardId() {
        return primaryDebitCardId;
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
