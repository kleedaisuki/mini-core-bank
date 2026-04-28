package com.moesegfault.banking.domain.ledger;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 账户余额实体（Account Balance Entity），对齐 `account_balance` 账面余额与可用余额语义；
 *        Account-balance entity aligned with `account_balance` ledger/available balance semantics.
 */
public final class Balance {

    /**
     * @brief 账户 ID（Account ID），对应 `account_balance.account_id`；
     *        Account identifier mapped to `account_balance.account_id`.
     */
    private final String accountId;

    /**
     * @brief 币种代码（Currency Code），对应 `account_balance.currency_code`；
     *        Currency code mapped to `account_balance.currency_code`.
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
     * @brief 最后更新时间（Updated Timestamp）；
     *        Last updated timestamp.
     */
    private final Instant updatedAt;

    /**
     * @brief 构造余额实体（Construct Balance Entity）；
     *        Construct balance entity.
     *
     * @param accountId        账户 ID（Account ID）。
     * @param currencyCode     币种代码（Currency code）。
     * @param ledgerBalance    账面余额（Ledger balance）。
     * @param availableBalance 可用余额（Available balance）。
     * @param updatedAt        更新时间（Updated timestamp）。
     */
    private Balance(
            final String accountId,
            final CurrencyCode currencyCode,
            final Money ledgerBalance,
            final Money availableBalance,
            final Instant updatedAt
    ) {
        this.accountId = normalizeRequiredId(accountId, "Account ID");
        this.currencyCode = Objects.requireNonNull(currencyCode, "Currency code must not be null");
        this.ledgerBalance = Objects.requireNonNull(ledgerBalance, "Ledger balance must not be null");
        this.availableBalance = Objects.requireNonNull(availableBalance, "Available balance must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated-at must not be null");
        ensureCurrencyConsistency(this.ledgerBalance, this.currencyCode, "Ledger balance");
        ensureCurrencyConsistency(this.availableBalance, this.currencyCode, "Available balance");
        if (this.availableBalance.compareTo(this.ledgerBalance) > 0) {
            throw new BusinessRuleViolation("Available balance must not exceed ledger balance");
        }
    }

    /**
     * @brief 初始化零余额（Initialize Zero Balance）；
     *        Initialize zero balance for an account currency.
     *
     * @param accountId    账户 ID（Account ID）。
     * @param currencyCode 币种代码（Currency code）。
     * @param asOfTime     时间点（As-of timestamp）。
     * @return 零余额实体（Zero balance entity）。
     */
    public static Balance initialize(
            final String accountId,
            final CurrencyCode currencyCode,
            final Instant asOfTime
    ) {
        final CurrencyCode normalizedCurrency = Objects.requireNonNull(currencyCode, "Currency code must not be null");
        return new Balance(
                accountId,
                normalizedCurrency,
                Money.zero(normalizedCurrency),
                Money.zero(normalizedCurrency),
                Objects.requireNonNull(asOfTime, "As-of time must not be null"));
    }

    /**
     * @brief 从持久化状态重建余额（Restore Balance from Persistence）；
     *        Restore balance from persistence state.
     *
     * @param accountId        账户 ID（Account ID）。
     * @param currencyCode     币种代码（Currency code）。
     * @param ledgerBalance    账面余额（Ledger balance）。
     * @param availableBalance 可用余额（Available balance）。
     * @param updatedAt        更新时间（Updated timestamp）。
     * @return 重建后的余额实体（Restored balance entity）。
     */
    public static Balance restore(
            final String accountId,
            final CurrencyCode currencyCode,
            final Money ledgerBalance,
            final Money availableBalance,
            final Instant updatedAt
    ) {
        return new Balance(accountId, currencyCode, ledgerBalance, availableBalance, updatedAt);
    }

    /**
     * @brief 应用分录方向与金额得到新余额（Apply Entry to Derive New Balance）；
     *        Apply entry direction and amount to derive a new balance snapshot.
     *
     * @param entryDirection 分录方向（Entry direction）。
     * @param amount         分录金额（Entry amount）。
     * @param asOfTime       更新时间（As-of timestamp）。
     * @return 更新后的余额实体（Updated balance entity）。
     */
    public Balance applyEntry(
            final EntryDirection entryDirection,
            final Money amount,
            final Instant asOfTime
    ) {
        final EntryDirection direction = Objects.requireNonNull(entryDirection, "Entry direction must not be null");
        final Money normalizedAmount = requirePositiveAmountWithSameCurrency(amount);
        if (direction.isIncreaseLike()) {
            return new Balance(
                    accountId,
                    currencyCode,
                    ledgerBalance.add(normalizedAmount),
                    availableBalance.add(normalizedAmount),
                    Objects.requireNonNull(asOfTime, "As-of time must not be null"));
        }
        if (availableBalance.compareTo(normalizedAmount) < 0) {
            throw new BusinessRuleViolation("Insufficient available balance for entry posting");
        }
        return new Balance(
                accountId,
                currencyCode,
                ledgerBalance.subtract(normalizedAmount),
                availableBalance.subtract(normalizedAmount),
                Objects.requireNonNull(asOfTime, "As-of time must not be null"));
    }

    /**
     * @brief 预占可用余额（Reserve Available Balance）；
     *        Reserve amount from available balance without changing ledger balance.
     *
     * @param amount   预占金额（Reserved amount）。
     * @param asOfTime 更新时间（As-of timestamp）。
     * @return 更新后的余额实体（Updated balance entity）。
     */
    public Balance reserve(final Money amount, final Instant asOfTime) {
        final Money normalizedAmount = requirePositiveAmountWithSameCurrency(amount);
        if (availableBalance.compareTo(normalizedAmount) < 0) {
            throw new BusinessRuleViolation("Insufficient available balance for reservation");
        }
        return new Balance(
                accountId,
                currencyCode,
                ledgerBalance,
                availableBalance.subtract(normalizedAmount),
                Objects.requireNonNull(asOfTime, "As-of time must not be null"));
    }

    /**
     * @brief 释放已预占余额（Release Reserved Balance）；
     *        Release previously reserved amount back to available balance.
     *
     * @param amount   释放金额（Release amount）。
     * @param asOfTime 更新时间（As-of timestamp）。
     * @return 更新后的余额实体（Updated balance entity）。
     */
    public Balance releaseReservation(final Money amount, final Instant asOfTime) {
        final Money normalizedAmount = requirePositiveAmountWithSameCurrency(amount);
        final Money releasedAvailable = availableBalance.add(normalizedAmount);
        if (releasedAvailable.compareTo(ledgerBalance) > 0) {
            throw new BusinessRuleViolation("Released available balance must not exceed ledger balance");
        }
        return new Balance(
                accountId,
                currencyCode,
                ledgerBalance,
                releasedAvailable,
                Objects.requireNonNull(asOfTime, "As-of time must not be null"));
    }

    /**
     * @brief 判断是否可用余额充足（Check Available-Balance Sufficiency）；
     *        Check whether available balance is sufficient for amount.
     *
     * @param amount 请求金额（Requested amount）。
     * @return 充足返回 true（true when sufficient）。
     */
    public boolean canDebit(final Money amount) {
        final Money normalizedAmount = requirePositiveAmountWithSameCurrency(amount);
        return availableBalance.compareTo(normalizedAmount) >= 0;
    }

    /**
     * @brief 构建余额更新事件（Build Balance Updated Event）；
     *        Build balance-updated domain event.
     *
     * @return 余额更新事件（Balance-updated event）。
     */
    public BalanceUpdated updatedEvent() {
        return new BalanceUpdated(
                accountId,
                currencyCode,
                ledgerBalance,
                availableBalance,
                Instant.now());
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
     * @brief 返回更新时间（Return Updated Timestamp）；
     *        Return updated timestamp.
     *
     * @return 更新时间（Updated timestamp）。
     */
    public Instant updatedAt() {
        return updatedAt;
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

    /**
     * @brief 要求金额为正且币种一致（Require Positive Amount with Same Currency）；
     *        Require amount to be positive and in same currency.
     *
     * @param amount 金额（Amount）。
     * @return 归一化金额（Validated amount）。
     */
    private Money requirePositiveAmountWithSameCurrency(final Money amount) {
        final Money normalizedAmount = Objects.requireNonNull(amount, "Amount must not be null");
        ensureCurrencyConsistency(normalizedAmount, currencyCode, "Amount");
        if (!normalizedAmount.isPositive()) {
            throw new BusinessRuleViolation("Amount must be positive");
        }
        return normalizedAmount;
    }

    /**
     * @brief 校验金额币种一致（Validate Money Currency Consistency）；
     *        Validate money currency consistency.
     *
     * @param money              金额对象（Money object）。
     * @param expectedCurrency   期望币种（Expected currency）。
     * @param moneyFieldLabel    字段标签（Money field label）。
     */
    private static void ensureCurrencyConsistency(
            final Money money,
            final CurrencyCode expectedCurrency,
            final String moneyFieldLabel
    ) {
        if (!money.currencyCode().equals(expectedCurrency)) {
            throw new BusinessRuleViolation(
                    moneyFieldLabel + " currency must be " + expectedCurrency + ", got " + money.currencyCode());
        }
    }
}
