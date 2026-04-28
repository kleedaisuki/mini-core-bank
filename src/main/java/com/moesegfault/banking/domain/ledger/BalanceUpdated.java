package com.moesegfault.banking.domain.ledger;

import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DomainEvent;
import com.moesegfault.banking.domain.shared.Money;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 余额更新事件（Balance Updated Event），表示 `account_balance` 快照已变更；
 *        Balance-updated event indicating `account_balance` snapshot has changed.
 */
public final class BalanceUpdated implements DomainEvent {

    /**
     * @brief 账户 ID（Account ID）；
     *        Account identifier.
     */
    private final String accountId;

    /**
     * @brief 币种代码（Currency Code）；
     *        Currency code.
     */
    private final CurrencyCode currencyCode;

    /**
     * @brief 账面余额（Ledger Balance）；
     *        Ledger balance.
     */
    private final Money ledgerBalance;

    /**
     * @brief 可用余额（Available Balance）；
     *        Available balance.
     */
    private final Money availableBalance;

    /**
     * @brief 事件时间（Occurred Timestamp）；
     *        Event occurred timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造余额更新事件（Construct Balance Updated Event）；
     *        Construct balance-updated event.
     *
     * @param accountId        账户 ID（Account ID）。
     * @param currencyCode     币种代码（Currency code）。
     * @param ledgerBalance    账面余额（Ledger balance）。
     * @param availableBalance 可用余额（Available balance）。
     * @param occurredAt       事件时间（Occurred timestamp）。
     */
    public BalanceUpdated(
            final String accountId,
            final CurrencyCode currencyCode,
            final Money ledgerBalance,
            final Money availableBalance,
            final Instant occurredAt
    ) {
        this.accountId = normalizeRequiredId(accountId, "Account ID");
        this.currencyCode = Objects.requireNonNull(currencyCode, "Currency code must not be null");
        this.ledgerBalance = Objects.requireNonNull(ledgerBalance, "Ledger balance must not be null");
        this.availableBalance = Objects.requireNonNull(availableBalance, "Available balance must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
        if (!this.ledgerBalance.currencyCode().equals(this.currencyCode)) {
            throw new IllegalArgumentException("Ledger balance currency must match event currency code");
        }
        if (!this.availableBalance.currencyCode().equals(this.currencyCode)) {
            throw new IllegalArgumentException("Available balance currency must match event currency code");
        }
        if (this.availableBalance.compareTo(this.ledgerBalance) > 0) {
            throw new IllegalArgumentException("Available balance must not exceed ledger balance");
        }
    }

    /**
     * @brief 返回账户 ID（Return Account ID）；
     *        Return account identifier.
     *
     * @return 账户 ID（Account ID）。
     */
    public String accountId() {
        return accountId;
    }

    /**
     * @brief 返回币种代码（Return Currency Code）；
     *        Return currency code.
     *
     * @return 币种代码（Currency code）。
     */
    public CurrencyCode currencyCode() {
        return currencyCode;
    }

    /**
     * @brief 返回账面余额（Return Ledger Balance）；
     *        Return ledger balance.
     *
     * @return 账面余额（Ledger balance）。
     */
    public Money ledgerBalance() {
        return ledgerBalance;
    }

    /**
     * @brief 返回可用余额（Return Available Balance）；
     *        Return available balance.
     *
     * @return 可用余额（Available balance）。
     */
    public Money availableBalance() {
        return availableBalance;
    }

    /**
     * @brief 返回事件时间（Return Occurred Timestamp）；
     *        Return event occurred timestamp.
     *
     * @return 事件时间（Occurred timestamp）。
     */
    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    /**
     * @brief 标准化并校验必填标识（Normalize Required Identifier）；
     *        Normalize and validate required identifier.
     *
     * @param rawValue 原始值（Raw value）。
     * @param label    字段标签（Field label）。
     * @return 标准化标识（Normalized identifier）。
     */
    private static String normalizeRequiredId(final String rawValue, final String label) {
        if (rawValue == null) {
            throw new IllegalArgumentException(label + " must not be null");
        }
        final String normalized = rawValue.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
        return normalized;
    }
}
