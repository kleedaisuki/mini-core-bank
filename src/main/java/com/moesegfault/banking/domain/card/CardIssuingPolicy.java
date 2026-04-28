package com.moesegfault.banking.domain.card;

import com.moesegfault.banking.domain.account.CreditCardAccountId;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.customer.CustomerPolicy;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.util.Objects;

/**
 * @brief 发卡策略（Card Issuing Policy），集中校验发卡时跨对象一致性；
 *        Card issuing policy centralizing cross-object consistency checks.
 */
public final class CardIssuingPolicy {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private CardIssuingPolicy() {
        // utility class
    }

    /**
     * @brief 校验客户是否具备发卡资格（Ensure Customer Eligibility for Card Issuance）；
     *        Ensure customer is eligible for card issuance.
     *
     * @param customer 客户实体（Customer entity）。
     */
    public static void ensureEligibleCustomer(final Customer customer) {
        CustomerPolicy.ensureEligibleForCardIssuance(Objects.requireNonNull(customer, "Customer must not be null"));
    }

    /**
     * @brief 校验扣账卡账户归属一致性（Ensure Debit Card Binding Ownership Consistency）；
     *        Ensure debit-card bound accounts belong to the holder.
     *
     * @param holderCustomerId  持卡客户 ID（Holder customer ID）。
     * @param savingsOwnerId    储蓄账户所有者 ID（Savings-account owner ID）。
     * @param fxOwnerId         外汇账户所有者 ID（FX-account owner ID）。
     */
    public static void ensureDebitCardBindingOwnership(
            final CustomerId holderCustomerId,
            final CustomerId savingsOwnerId,
            final CustomerId fxOwnerId
    ) {
        final CustomerId holder = Objects.requireNonNull(holderCustomerId, "Holder customer ID must not be null");
        final CustomerId savingsOwner = Objects.requireNonNull(
                savingsOwnerId,
                "Savings owner customer ID must not be null");
        final CustomerId fxOwner = Objects.requireNonNull(fxOwnerId, "FX owner customer ID must not be null");
        if (!holder.sameValueAs(savingsOwner) || !holder.sameValueAs(fxOwner)) {
            throw new BusinessRuleViolation(
                    "Debit card holder must own both savings and FX accounts");
        }
    }

    /**
     * @brief 校验附属信用卡与主卡账户一致（Ensure Supplementary Credit Card Account Consistency）；
     *        Ensure supplementary credit card shares the same account as primary card.
     *
     * @param primaryAccountId       主卡账户 ID（Primary card account ID）。
     * @param supplementaryAccountId 附属卡账户 ID（Supplementary card account ID）。
     */
    public static void ensureSupplementaryCreditAccountConsistency(
            final CreditCardAccountId primaryAccountId,
            final CreditCardAccountId supplementaryAccountId
    ) {
        final CreditCardAccountId primary = Objects.requireNonNull(
                primaryAccountId,
                "Primary account ID must not be null");
        final CreditCardAccountId supplementary = Objects.requireNonNull(
                supplementaryAccountId,
                "Supplementary account ID must not be null");
        if (!primary.sameValueAs(supplementary)) {
            throw new BusinessRuleViolation(
                    "Supplementary credit card must use the same credit_card_account_id as primary card");
        }
    }
}
