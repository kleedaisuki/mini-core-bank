package com.moesegfault.banking.domain.credit;

import com.moesegfault.banking.domain.shared.DomainEvent;
import com.moesegfault.banking.domain.shared.Money;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 信用卡还款到账事件（Credit Card Repayment Received Event）；
 *        Credit-card-repayment-received event.
 */
public final class CreditCardRepaymentReceived implements DomainEvent {

    /**
     * @brief 信用卡账户 ID（Credit Card Account ID）；
     *        Credit-card-account identifier.
     */
    private final CreditCardAccountId creditCardAccountId;

    /**
     * @brief 关联账单 ID（可空）（Related Statement ID, Nullable）；
     *        Related statement identifier, nullable.
     */
    private final StatementId statementId;

    /**
     * @brief 还款金额（Repayment Amount）；
     *        Repayment amount.
     */
    private final Money repaymentAmount;

    /**
     * @brief 事件时间（Occurred Timestamp）；
     *        Event occurred timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造信用卡还款到账事件（Construct Repayment Received Event）；
     *        Construct credit-card-repayment-received event.
     *
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @param statementId         关联账单 ID（Related statement ID, nullable）。
     * @param repaymentAmount     还款金额（Repayment amount）。
     * @param occurredAt          事件时间（Occurred timestamp）。
     */
    public CreditCardRepaymentReceived(
            final CreditCardAccountId creditCardAccountId,
            final StatementId statementId,
            final Money repaymentAmount,
            final Instant occurredAt
    ) {
        this.creditCardAccountId = Objects.requireNonNull(creditCardAccountId, "Credit-card-account ID must not be null");
        this.statementId = statementId;
        this.repaymentAmount = Objects.requireNonNull(repaymentAmount, "Repayment amount must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
    }

    /**
     * @brief 返回信用卡账户 ID（Return Credit Card Account ID）；
     *        Return credit-card-account identifier.
     *
     * @return 信用卡账户 ID（Credit-card-account ID）。
     */
    public CreditCardAccountId creditCardAccountId() {
        return creditCardAccountId;
    }

    /**
     * @brief 返回关联账单 ID（可空）（Return Optional Statement ID）；
     *        Return related statement ID, nullable.
     *
     * @return 账单 ID 或 null（Statement ID or null）。
     */
    public StatementId statementIdOrNull() {
        return statementId;
    }

    /**
     * @brief 返回还款金额（Return Repayment Amount）；
     *        Return repayment amount.
     *
     * @return 还款金额（Repayment amount）。
     */
    public Money repaymentAmount() {
        return repaymentAmount;
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
