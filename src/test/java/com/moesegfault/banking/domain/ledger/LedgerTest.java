package com.moesegfault.banking.domain.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

/**
 * @brief Ledger 领域单元测试（Ledger Domain Unit Test），覆盖余额更新、批次状态流转与分录约束；
 *        Ledger domain unit tests covering balance update, batch status transitions, and entry invariants.
 */
class LedgerTest {

    /**
     * @brief 验证余额应用增加与减少分录后快照正确；
     *        Verify balance snapshots after increase-like and decrease-like entries.
     */
    @Test
    void shouldApplyIncreaseAndDecreaseEntries() {
        final Balance initial = Balance.restore(
                "acc-001",
                CurrencyCode.of("USD"),
                usd("100.0000"),
                usd("80.0000"),
                Instant.parse("2026-04-28T00:00:00Z"));

        final Balance increased = initial.applyEntry(
                EntryDirection.CREDIT,
                usd("20.0000"),
                Instant.parse("2026-04-28T00:01:00Z"));

        final Balance decreased = increased.applyEntry(
                EntryDirection.DEBIT,
                usd("30.0000"),
                Instant.parse("2026-04-28T00:02:00Z"));

        assertEquals(usd("120.0000"), increased.ledgerBalance());
        assertEquals(usd("100.0000"), increased.availableBalance());
        assertEquals(usd("90.0000"), decreased.ledgerBalance());
        assertEquals(usd("70.0000"), decreased.availableBalance());
    }

    /**
     * @brief 验证可用余额不足时会拒绝减少型分录；
     *        Verify decrease-like entry is rejected when available balance is insufficient.
     */
    @Test
    void shouldRejectDecreaseWhenAvailableIsInsufficient() {
        final Balance initial = Balance.restore(
                "acc-002",
                CurrencyCode.of("USD"),
                usd("100.0000"),
                usd("10.0000"),
                Instant.parse("2026-04-28T00:00:00Z"));

        assertThrows(
                BusinessRuleViolation.class,
                () -> initial.applyEntry(
                        EntryDirection.DEBIT,
                        usd("20.0000"),
                        Instant.parse("2026-04-28T00:01:00Z"))
        );
    }

    /**
     * @brief 验证入账批次可接收分录并完成 PENDING 到 POSTED 的状态流转；
     *        Verify posting batch accepts entry and transitions from PENDING to POSTED.
     */
    @Test
    void shouldAcceptEntryAndPostBatch() {
        final Instant createdAt = Instant.parse("2026-04-28T00:00:00Z");
        final PostingBatch batch = PostingBatch.createPending(
                PostingBatchId.of("batch-001"),
                "txn-001",
                "idem-001",
                createdAt);

        final LedgerEntry entry = LedgerEntry.create(
                LedgerEntryId.of("entry-001"),
                "txn-001",
                "acc-003",
                CurrencyCode.of("USD"),
                EntryDirection.CREDIT,
                usd("50.0000"),
                EntryType.PRINCIPAL,
                Instant.parse("2026-04-28T00:00:01Z"));

        batch.addEntry(entry);
        batch.markPosted(Instant.parse("2026-04-28T00:00:02Z"));

        assertEquals(1, batch.entries().size());
        assertEquals(PostingStatus.POSTED, batch.batchStatus());
        assertEquals(PostingBatchId.of("batch-001"), batch.entries().get(0).batchIdOrNull());
        assertNotNull(batch.postedAtOrNull());
    }

    /**
     * @brief 验证空批次不能直接入账；
     *        Verify empty batch cannot be posted directly.
     */
    @Test
    void shouldRejectPostingWhenBatchHasNoEntries() {
        final PostingBatch batch = PostingBatch.createPending(
                PostingBatchId.of("batch-002"),
                "txn-002",
                "idem-002",
                Instant.parse("2026-04-28T00:00:00Z"));

        assertThrows(
                BusinessRuleViolation.class,
                () -> batch.markPosted(Instant.parse("2026-04-28T00:00:01Z"))
        );
    }

    /**
     * @brief 验证仅 POSTED 批次可冲正并产出冲正事件；
     *        Verify only POSTED batch can be reversed and emits reversed event.
     */
    @Test
    void shouldReverseOnlyPostedBatch() {
        final PostingBatch batch = PostingBatch.createPending(
                PostingBatchId.of("batch-003"),
                "txn-003",
                "idem-003",
                Instant.parse("2026-04-28T00:00:00Z"));

        assertThrows(
                BusinessRuleViolation.class,
                () -> batch.reverse(Instant.parse("2026-04-28T00:00:01Z"))
        );

        batch.addEntry(LedgerEntry.create(
                LedgerEntryId.of("entry-003"),
                "txn-003",
                "acc-005",
                CurrencyCode.of("USD"),
                EntryDirection.DEBIT,
                usd("10.0000"),
                EntryType.FEE,
                Instant.parse("2026-04-28T00:00:01Z")));
        batch.markPosted(Instant.parse("2026-04-28T00:00:02Z"));
        batch.reverse(Instant.parse("2026-04-28T00:00:03Z"));

        assertEquals(PostingStatus.REVERSED, batch.batchStatus());
        assertEquals("txn-003", batch.reversedEvent().transactionId());
    }

    /**
     * @brief 创建 USD 金额（Create USD Money Helper）；
     *        Create USD money helper.
     *
     * @param amount 金额字符串（Amount string）。
     * @return USD 金额对象（USD money object）。
     */
    private static Money usd(final String amount) {
        return Money.of(CurrencyCode.of("USD"), new BigDecimal(amount));
    }
}
