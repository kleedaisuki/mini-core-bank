package com.moesegfault.banking.application.card;

import com.moesegfault.banking.domain.account.Account;
import com.moesegfault.banking.domain.account.AccountStatus;
import com.moesegfault.banking.domain.account.AccountType;
import com.moesegfault.banking.domain.card.CardNumber;
import com.moesegfault.banking.domain.card.CardRepository;
import com.moesegfault.banking.domain.customer.Customer;
import java.util.Objects;

/**
 * @brief 卡应用层支撑工具（Card Application Support Utility），统一跨聚合校验与类型映射；
 *        Card application support utility centralizing cross-aggregate checks and type mappings.
 */
public final class CardApplicationSupport {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private CardApplicationSupport() {
    }

    /**
     * @brief 确保客户可发卡（Ensure Customer Can Issue Card）；
     *        Ensure customer status allows card issuance.
     *
     * @param customer 客户实体（Customer entity）。
     */
    public static void ensureEligibleCustomer(final Customer customer) {
        final Customer normalized = Objects.requireNonNull(customer, "customer must not be null");
        final com.moesegfault.banking.domain.card.CustomerStatus cardCustomerStatus =
                com.moesegfault.banking.domain.card.CustomerStatus.valueOf(normalized.customerStatus().name());
        com.moesegfault.banking.domain.card.CardIssuingPolicy.ensureEligibleCustomer(cardCustomerStatus);
    }

    /**
     * @brief 确保卡号在三张卡表中唯一（Ensure Card Number Is Globally Unique）；
     *        Ensure card number is globally unique across all card tables.
     *
     * @param cardRepository 卡仓储接口（Card repository interface）。
     * @param cardNumber     卡号值对象（Card number value object）。
     */
    public static void ensureUniqueCardNumber(final CardRepository cardRepository, final CardNumber cardNumber) {
        final CardRepository normalizedRepository = Objects.requireNonNull(
                cardRepository,
                "cardRepository must not be null");
        final CardNumber normalizedCardNumber = Objects.requireNonNull(cardNumber, "cardNumber must not be null");
        if (normalizedRepository.existsAnyByCardNumber(normalizedCardNumber)) {
            throw new CardApplicationException("Card number already exists in card schema");
        }
    }

    /**
     * @brief 确保基础账户类型与状态满足要求（Ensure Base Account Type and Status）；
     *        Ensure base account has expected type and ACTIVE status.
     *
     * @param account      基础账户实体（Base account entity）。
     * @param expectedType 期望账户类型（Expected account type）。
     * @param schemaName   领域语义名称（Schema-aligned domain label）。
     */
    public static void ensureActiveAccount(
            final Account account,
            final AccountType expectedType,
            final String schemaName
    ) {
        final Account normalizedAccount = Objects.requireNonNull(account, "account must not be null");
        final AccountType normalizedExpectedType = Objects.requireNonNull(
                expectedType,
                "expectedType must not be null");
        if (normalizedAccount.accountType() != normalizedExpectedType) {
            throw new CardApplicationException(
                    schemaName + " must be account_type=" + normalizedExpectedType
                            + ", but actual type is " + normalizedAccount.accountType());
        }
        if (normalizedAccount.accountStatus() != AccountStatus.ACTIVE) {
            throw new CardApplicationException(
                    schemaName + " must be ACTIVE, but current status is " + normalizedAccount.accountStatus());
        }
    }

    /**
     * @brief 确保主卡归属与账户归属一致（Ensure Primary Card Ownership Consistency）；
     *        Ensure primary card holder is the same owner as account owner.
     *
     * @param primaryCardHolderId 主卡持卡客户 ID（Primary card holder customer ID）。
     * @param accountOwnerId      基础账户所有者客户 ID（Base account owner customer ID）。
     */
    public static void ensurePrimaryCardOwnershipConsistency(
            final String primaryCardHolderId,
            final String accountOwnerId
    ) {
        final String normalizedPrimaryHolder = normalizeNonBlank(primaryCardHolderId, "primaryCardHolderId");
        final String normalizedAccountOwner = normalizeNonBlank(accountOwnerId, "accountOwnerId");
        if (!normalizedPrimaryHolder.equals(normalizedAccountOwner)) {
            throw new CardApplicationException(
                    "Primary credit card holder must match credit_card_account owner");
        }
    }

    /**
     * @brief 断言字符串非空白（Ensure String Is Not Blank）；
     *        Ensure string value is non-null and non-blank.
     *
     * @param rawValue  原始值（Raw value）。
     * @param fieldName 字段名称（Field name）。
     * @return 标准化字符串（Normalized string）。
     */
    public static String normalizeNonBlank(final String rawValue, final String fieldName) {
        final String normalized = Objects.requireNonNull(rawValue, fieldName + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
