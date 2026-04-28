package com.moesegfault.banking.domain.card;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.util.Objects;

/**
 * @brief 附属卡策略（Supplementary Card Policy），约束主卡与附属卡的配对关系；
 *        Supplementary card policy enforcing pairing constraints between primary and supplementary cards.
 */
public final class SupplementaryCardPolicy {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private SupplementaryCardPolicy() {
        // utility class
    }

    /**
     * @brief 校验主扣账卡可发行附属卡（Ensure Primary Debit Card Can Issue Supplementary）；
     *        Ensure primary debit card can issue supplementary card.
     *
     * @param primaryDebitCard 主扣账卡（Primary debit card）。
     */
    public static void ensurePrimaryDebitCardCanIssue(final DebitCard primaryDebitCard) {
        final DebitCard primary = Objects.requireNonNull(primaryDebitCard, "Primary debit card must not be null");
        if (!primary.cardStatus().isUsable()) {
            throw new BusinessRuleViolation(
                    "Primary debit card must be ACTIVE to issue supplementary debit card");
        }
    }

    /**
     * @brief 校验主信用卡可发行附属卡（Ensure Primary Credit Card Can Issue Supplementary）；
     *        Ensure primary credit card can issue supplementary card.
     *
     * @param primaryCreditCard 主信用卡（Primary credit card）。
     */
    public static void ensurePrimaryCreditCardCanIssue(final CreditCard primaryCreditCard) {
        final CreditCard primary = Objects.requireNonNull(primaryCreditCard, "Primary credit card must not be null");
        if (primary.cardRole() != CardRole.PRIMARY) {
            throw new BusinessRuleViolation("Only PRIMARY credit card can issue supplementary credit card");
        }
        if (!primary.cardStatus().isUsable()) {
            throw new BusinessRuleViolation("Primary credit card must be ACTIVE to issue supplementary credit card");
        }
    }
}
