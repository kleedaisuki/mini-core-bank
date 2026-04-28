package com.moesegfault.banking.domain.business;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.util.Objects;

/**
 * @brief 业务交易策略（Business Transaction Policy），集中定义业务流水状态机与幂等规则；
 *        Business transaction policy centralizing lifecycle and idempotency rules.
 */
public final class BusinessTransactionPolicy {

    /**
     * @brief 私有构造函数（Private Constructor）；
     *        Private constructor for utility class.
     */
    private BusinessTransactionPolicy() {
        // utility class
    }

    /**
     * @brief 校验业务类型可用于发起交易（Ensure Type Is Startable）；
     *        Ensure business type can start new transactions.
     *
     * @param businessType 业务类型实体（Business type entity）。
     */
    public static void ensureTypeStartable(final BusinessType businessType) {
        final BusinessType type = Objects.requireNonNull(businessType, "Business type must not be null");
        if (!type.isStartable()) {
            throw new BusinessRuleViolation(
                    "Business type " + type.businessTypeCode() + " is not active and cannot start transaction");
        }
    }

    /**
     * @brief 校验参考号未被占用（Ensure Reference Not Used）；
     *        Ensure business reference is not used by existing transaction.
     *
     * @param businessRepository 业务仓储（Business repository）。
     * @param referenceNo        业务参考号（Business reference）。
     */
    public static void ensureReferenceNotUsed(
            final BusinessRepository businessRepository,
            final BusinessReference referenceNo
    ) {
        final BusinessRepository repository = Objects.requireNonNull(
                businessRepository,
                "Business repository must not be null");
        final BusinessReference reference = Objects.requireNonNull(referenceNo, "Business reference must not be null");
        if (repository.existsTransactionByReference(reference)) {
            throw new BusinessRuleViolation("Business reference already exists: " + reference);
        }
    }

    /**
     * @brief 校验交易可完成（Ensure Transaction Completable）；
     *        Ensure transaction can transition to success.
     *
     * @param businessTransaction 业务交易实体（Business transaction entity）。
     */
    public static void ensureCanComplete(final BusinessTransaction businessTransaction) {
        final BusinessTransaction transaction = Objects.requireNonNull(
                businessTransaction,
                "Business transaction must not be null");
        if (!transaction.transactionStatus().isCompletable()) {
            throw new BusinessRuleViolation(
                    "Only PENDING transaction can be completed, current status: "
                            + transaction.transactionStatus());
        }
    }

    /**
     * @brief 校验交易可失败（Ensure Transaction Failable）；
     *        Ensure transaction can transition to failed.
     *
     * @param businessTransaction 业务交易实体（Business transaction entity）。
     */
    public static void ensureCanFail(final BusinessTransaction businessTransaction) {
        final BusinessTransaction transaction = Objects.requireNonNull(
                businessTransaction,
                "Business transaction must not be null");
        if (!transaction.transactionStatus().isCompletable()) {
            throw new BusinessRuleViolation(
                    "Only PENDING transaction can be failed, current status: "
                            + transaction.transactionStatus());
        }
    }

    /**
     * @brief 校验交易可冲正（Ensure Transaction Reversible）；
     *        Ensure transaction can be reversed.
     *
     * @param businessTransaction 业务交易实体（Business transaction entity）。
     * @param businessType        业务类型实体（Business type entity）。
     */
    public static void ensureCanReverse(
            final BusinessTransaction businessTransaction,
            final BusinessType businessType
    ) {
        final BusinessTransaction transaction = Objects.requireNonNull(
                businessTransaction,
                "Business transaction must not be null");
        final BusinessType type = Objects.requireNonNull(businessType, "Business type must not be null");
        if (!transaction.businessTypeCode().sameValueAs(type.businessTypeCode())) {
            throw new BusinessRuleViolation("Business type code mismatch for reversal");
        }
        if (!transaction.transactionStatus().isSuccessful()) {
            throw new BusinessRuleViolation(
                    "Only SUCCESS transaction can be reversed, current status: "
                            + transaction.transactionStatus());
        }
        if (!type.isReversible()) {
            throw new BusinessRuleViolation(
                    "Business type " + type.businessTypeCode() + " is not reversible");
        }
    }
}
