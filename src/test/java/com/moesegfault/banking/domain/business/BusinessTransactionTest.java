package com.moesegfault.banking.domain.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.time.Instant;
import org.junit.jupiter.api.Test;

/**
 * @brief Business 领域单元测试（Business Domain Unit Test），覆盖交易状态机、类型约束与 schema 边界；
 *        Business domain unit tests covering transaction state machine, type constraints, and schema boundaries.
 */
class BusinessTransactionTest {

    /**
     * @brief 验证发起交易后状态为 PENDING 且完成时间为空；
     *        Verify started transaction is `PENDING` and has null completed-at.
     */
    @Test
    void shouldStartPendingTransaction() {
        final BusinessTransaction transaction = BusinessTransaction.start(
                BusinessTransactionId.of("txn-001"),
                BusinessTypeCode.of("OPEN_SAVINGS_ACCOUNT"),
                null,
                "system",
                BusinessChannel.SYSTEM,
                BusinessReference.of("REF_20260428_0001"),
                "initial request");

        assertEquals(BusinessTransactionStatus.PENDING, transaction.transactionStatus());
        assertNull(transaction.completedAtOrNull());
        assertNotNull(transaction.startedEvent().occurredAt());
    }

    /**
     * @brief 验证待处理交易可以完成并写入完成事件；
     *        Verify pending transaction can be completed and emits completion event.
     */
    @Test
    void shouldCompletePendingTransaction() {
        final BusinessTransaction transaction = newPendingTransaction("txn-002", "REF_20260428_0002");
        final Instant completedAt = transaction.requestedAt().plusSeconds(10);

        transaction.completeSuccess(completedAt, "done");

        assertEquals(BusinessTransactionStatus.SUCCESS, transaction.transactionStatus());
        assertEquals(completedAt, transaction.completedAtOrNull());
        assertEquals(completedAt, transaction.completedEvent().occurredAt());
    }

    /**
     * @brief 验证非待处理状态不可再次完成；
     *        Verify non-pending transaction cannot be completed again.
     */
    @Test
    void shouldRejectCompletingNonPendingTransaction() {
        final BusinessTransaction transaction = newPendingTransaction("txn-003", "REF_20260428_0003");
        transaction.completeSuccess(transaction.requestedAt().plusSeconds(1), "done");

        assertThrows(
                BusinessRuleViolation.class,
                () -> transaction.completeSuccess(transaction.requestedAt().plusSeconds(2), "again")
        );
    }

    /**
     * @brief 验证冲正要求业务码匹配且类型支持可冲正；
     *        Verify reversal requires matching business type code and reversible type.
     */
    @Test
    void shouldEnforceReversePolicyByType() {
        final BusinessTransaction successTransaction = BusinessTransaction.restore(
                BusinessTransactionId.of("txn-004"),
                BusinessTypeCode.of("TRANSFER_INTERNAL"),
                null,
                "system",
                BusinessChannel.SYSTEM,
                BusinessTransactionStatus.SUCCESS,
                Instant.parse("2026-04-28T00:00:00Z"),
                Instant.parse("2026-04-28T00:00:05Z"),
                BusinessReference.of("REF_20260428_0004"),
                "done");

        final BusinessType mismatchedType = BusinessType.restore(
                BusinessTypeCode.of("BUY_PRODUCT"),
                BusinessCategory.INVESTMENT,
                "Buy product",
                null,
                true,
                true,
                BusinessTypeStatus.ACTIVE);

        final BusinessType nonReversibleType = BusinessType.restore(
                BusinessTypeCode.of("TRANSFER_INTERNAL"),
                BusinessCategory.TRANSFER,
                "Internal transfer",
                null,
                true,
                false,
                BusinessTypeStatus.ACTIVE);

        final BusinessType reversibleType = BusinessType.restore(
                BusinessTypeCode.of("TRANSFER_INTERNAL"),
                BusinessCategory.TRANSFER,
                "Internal transfer",
                "transfer",
                true,
                true,
                BusinessTypeStatus.ACTIVE);

        assertThrows(
                BusinessRuleViolation.class,
                () -> successTransaction.reverse(
                        mismatchedType,
                        Instant.parse("2026-04-28T00:00:06Z"),
                        "mismatch")
        );

        assertThrows(
                BusinessRuleViolation.class,
                () -> successTransaction.reverse(
                        nonReversibleType,
                        Instant.parse("2026-04-28T00:00:06Z"),
                        "not reversible")
        );

        successTransaction.reverse(reversibleType, Instant.parse("2026-04-28T00:00:06Z"), "manual reverse");
        assertEquals(BusinessTransactionStatus.REVERSED, successTransaction.transactionStatus());
    }

    /**
     * @brief 验证业务码和参考号遵守 `VARCHAR(64)` 上限；
     *        Verify business code and reference comply with `VARCHAR(64)` max length.
     */
    @Test
    void shouldRejectValueExceedingSchemaLength() {
        final String over64 = "X".repeat(65);

        assertThrows(IllegalArgumentException.class, () -> BusinessTypeCode.of(over64));
        assertThrows(IllegalArgumentException.class, () -> BusinessReference.of(over64));
    }

    /**
     * @brief 创建待处理业务交易（Create Pending Transaction Helper）；
     *        Create pending business transaction helper.
     *
     * @param transactionId 交易 ID（Transaction ID）。
     * @param referenceNo   参考号（Reference number）。
     * @return 待处理业务交易（Pending business transaction）。
     */
    private static BusinessTransaction newPendingTransaction(final String transactionId, final String referenceNo) {
        return BusinessTransaction.start(
                BusinessTransactionId.of(transactionId),
                BusinessTypeCode.of("TRANSFER_INTERNAL"),
                null,
                "system",
                BusinessChannel.SYSTEM,
                BusinessReference.of(referenceNo),
                null);
    }
}
