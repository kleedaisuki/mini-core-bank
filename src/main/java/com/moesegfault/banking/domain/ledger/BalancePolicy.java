package com.moesegfault.banking.domain.ledger;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.Money;
import java.util.Objects;

/**
 * @brief 余额策略（Balance Policy），统一可用余额充足性与币种一致性校验；
 *        Balance policy centralizing available-balance sufficiency and currency-consistency checks.
 */
public final class BalancePolicy {

    /**
     * @brief 工具类私有构造（Utility Private Constructor）；
     *        Private constructor for utility class.
     */
    private BalancePolicy() {
        // Utility class.
    }

    /**
     * @brief 校验可用余额充足（Ensure Sufficient Available Balance）；
     *        Ensure available balance is sufficient for debit-like operations.
     *
     * @param balance 当前余额（Current balance）。
     * @param amount  请求金额（Requested amount）。
     */
    public static void ensureSufficientAvailableBalance(final Balance balance, final Money amount) {
        final Balance normalizedBalance = Objects.requireNonNull(balance, "Balance must not be null");
        final Money normalizedAmount = Objects.requireNonNull(amount, "Amount must not be null");
        if (!normalizedBalance.canDebit(normalizedAmount)) {
            throw new BusinessRuleViolation("Insufficient available balance");
        }
    }

    /**
     * @brief 校验余额币种与金额币种一致（Ensure Currency Consistency Between Balance and Amount）；
     *        Ensure balance currency matches amount currency.
     *
     * @param balance 余额（Balance）。
     * @param amount  金额（Amount）。
     */
    public static void ensureCurrencyConsistency(final Balance balance, final Money amount) {
        final Balance normalizedBalance = Objects.requireNonNull(balance, "Balance must not be null");
        final Money normalizedAmount = Objects.requireNonNull(amount, "Amount must not be null");
        if (!normalizedBalance.currencyCode().equals(normalizedAmount.currencyCode())) {
            throw new BusinessRuleViolation(
                    "Balance currency mismatch: "
                            + normalizedBalance.currencyCode()
                            + " vs "
                            + normalizedAmount.currencyCode());
        }
    }
}
