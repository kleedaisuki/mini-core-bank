package com.moesegfault.banking.application.ledger.result;

import com.moesegfault.banking.domain.ledger.Balance;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 余额结果视图（Balance Result View），隔离 application 输出与 domain 实体；
 *        Balance result view isolating application output from domain entities.
 *
 * @param accountId        账户 ID（Account ID）。
 * @param currencyCode     币种代码（Currency code）。
 * @param ledgerBalance    账面余额（Ledger balance）。
 * @param availableBalance 可用余额（Available balance）。
 * @param updatedAt        最后更新时间（Updated timestamp）。
 */
public record BalanceResult(
        String accountId,
        CurrencyCode currencyCode,
        Money ledgerBalance,
        Money availableBalance,
        Instant updatedAt
) {

    /**
     * @brief 紧凑构造并校验核心字段（Compact Constructor with Invariant Checks）；
     *        Compact constructor validating core fields.
     */
    public BalanceResult {
        accountId = normalizeRequiredId(accountId, "Account ID");
        currencyCode = Objects.requireNonNull(currencyCode, "Currency code must not be null");
        ledgerBalance = Objects.requireNonNull(ledgerBalance, "Ledger balance must not be null");
        availableBalance = Objects.requireNonNull(availableBalance, "Available balance must not be null");
        updatedAt = Objects.requireNonNull(updatedAt, "Updated-at must not be null");
        if (!ledgerBalance.currencyCode().equals(currencyCode)) {
            throw new IllegalArgumentException("Ledger balance currency must match currency code");
        }
        if (!availableBalance.currencyCode().equals(currencyCode)) {
            throw new IllegalArgumentException("Available balance currency must match currency code");
        }
    }

    /**
     * @brief 从领域余额转换结果视图（Convert from Domain Balance）；
     *        Convert domain balance to application result view.
     *
     * @param balance 领域余额实体（Domain balance entity）。
     * @return 余额结果视图（Balance result view）。
     */
    public static BalanceResult fromDomain(final Balance balance) {
        final Balance normalized = Objects.requireNonNull(balance, "Balance must not be null");
        return new BalanceResult(
                normalized.accountId(),
                normalized.currencyCode(),
                normalized.ledgerBalance(),
                normalized.availableBalance(),
                normalized.updatedAt());
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
