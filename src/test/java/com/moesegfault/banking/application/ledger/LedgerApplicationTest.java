package com.moesegfault.banking.application.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moesegfault.banking.application.ledger.command.PostEntriesCommand;
import com.moesegfault.banking.application.ledger.command.PostEntriesHandler;
import com.moesegfault.banking.application.ledger.query.FindBalanceHandler;
import com.moesegfault.banking.application.ledger.query.FindBalanceQuery;
import com.moesegfault.banking.application.ledger.query.ListLedgerEntriesHandler;
import com.moesegfault.banking.application.ledger.query.ListLedgerEntriesQuery;
import com.moesegfault.banking.application.ledger.result.BalanceResult;
import com.moesegfault.banking.application.ledger.result.LedgerEntryResult;
import com.moesegfault.banking.domain.account.Account;
import com.moesegfault.banking.domain.account.AccountId;
import com.moesegfault.banking.domain.account.AccountNumber;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.account.AccountStatus;
import com.moesegfault.banking.domain.account.AccountType;
import com.moesegfault.banking.domain.account.CustomerId;
import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.business.BusinessReference;
import com.moesegfault.banking.domain.business.BusinessRepository;
import com.moesegfault.banking.domain.business.BusinessTransaction;
import com.moesegfault.banking.domain.business.BusinessTransactionId;
import com.moesegfault.banking.domain.business.BusinessTransactionStatus;
import com.moesegfault.banking.domain.business.BusinessTypeCode;
import com.moesegfault.banking.domain.ledger.Balance;
import com.moesegfault.banking.domain.ledger.EntryDirection;
import com.moesegfault.banking.domain.ledger.EntryType;
import com.moesegfault.banking.domain.ledger.LedgerEntry;
import com.moesegfault.banking.domain.ledger.LedgerEntryId;
import com.moesegfault.banking.domain.ledger.LedgerRepository;
import com.moesegfault.banking.domain.ledger.PostingBatch;
import com.moesegfault.banking.domain.ledger.PostingBatchId;
import com.moesegfault.banking.domain.ledger.PostingStatus;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.domain.shared.Money;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @brief Ledger 应用层单元测试（Ledger Application Unit Test），覆盖入账、幂等与查询映射；
 *        Ledger application unit tests covering posting, idempotency, and query mappings.
 */
class LedgerApplicationTest {

    /**
     * @brief 验证入账命令会创建批次、分录、余额并发布事件；
     *        Verify posting command creates batch/entries/balance and publishes events.
     */
    @Test
    void shouldPostEntriesAndPublishEvents() {
        final LedgerRepository ledgerRepository = Mockito.mock(LedgerRepository.class);
        final BusinessRepository businessRepository = Mockito.mock(BusinessRepository.class);
        final AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        final DomainEventPublisher eventPublisher = Mockito.mock(DomainEventPublisher.class);
        final IdGenerator idGenerator = sequentialIdGenerator("batch-001", "entry-001");
        final DbTransactionManager transactionManager = passthroughTransactionManager();

        final Instant now = Instant.now().plusSeconds(1);
        when(businessRepository.findTransactionById(BusinessTransactionId.of("txn-001")))
                .thenReturn(Optional.of(businessTransaction("txn-001", now)));
        when(accountRepository.findAccountById(AccountId.of("acc-001")))
                .thenReturn(Optional.of(activeAccount("acc-001", now)));
        when(ledgerRepository.findPostingBatchByIdempotencyKey("idem-001"))
                .thenReturn(Optional.empty());
        when(ledgerRepository.findPostingBatchByTransactionId("txn-001"))
                .thenReturn(Optional.empty());
        when(ledgerRepository.findBalance("acc-001", CurrencyCode.of("USD")))
                .thenReturn(Optional.empty());

        final PostEntriesHandler handler = new PostEntriesHandler(
                ledgerRepository,
                businessRepository,
                accountRepository,
                transactionManager,
                idGenerator,
                eventPublisher);
        final PostEntriesCommand command = new PostEntriesCommand(
                "txn-001",
                "idem-001",
                List.of(new PostEntriesCommand.PostingRequest(
                        "acc-001",
                        CurrencyCode.of("USD"),
                        EntryDirection.CREDIT,
                        usd("100.0000"),
                        EntryType.PRINCIPAL)),
                now.plusSeconds(1));

        final PostingBatchId postedBatchId = handler.handle(command);
        assertEquals("batch-001", postedBatchId.value());

        final ArgumentCaptor<PostingBatch> batchCaptor = ArgumentCaptor.forClass(PostingBatch.class);
        verify(ledgerRepository, times(1)).savePostingBatch(batchCaptor.capture());
        assertEquals(PostingStatus.POSTED, batchCaptor.getValue().batchStatus());
        assertEquals("txn-001", batchCaptor.getValue().transactionId());

        final ArgumentCaptor<List<LedgerEntry>> entriesCaptor = ArgumentCaptor.forClass(List.class);
        verify(ledgerRepository, times(1)).saveEntries(entriesCaptor.capture());
        assertEquals(1, entriesCaptor.getValue().size());
        assertEquals("entry-001", entriesCaptor.getValue().get(0).entryId().value());
        assertEquals("batch-001", entriesCaptor.getValue().get(0).batchIdOrNull().value());

        final ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);
        verify(ledgerRepository, times(1)).saveBalance(balanceCaptor.capture());
        assertEquals(usd("100.0000"), balanceCaptor.getValue().ledgerBalance());
        assertEquals(usd("100.0000"), balanceCaptor.getValue().availableBalance());

        verify(eventPublisher, times(2)).publish(any());
    }

    /**
     * @brief 验证幂等键命中已入账批次时直接复用批次 ID；
     *        Verify existing posted batch is reused when idempotency key hits.
     */
    @Test
    void shouldReturnExistingPostedBatchWhenIdempotencyHit() {
        final LedgerRepository ledgerRepository = Mockito.mock(LedgerRepository.class);
        final BusinessRepository businessRepository = Mockito.mock(BusinessRepository.class);
        final AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        final IdGenerator idGenerator = sequentialIdGenerator("batch-new", "entry-new");
        final DbTransactionManager transactionManager = passthroughTransactionManager();

        final PostingBatch existingBatch = PostingBatch.restore(
                PostingBatchId.of("batch-existing"),
                "txn-002",
                "idem-002",
                PostingStatus.POSTED,
                Instant.parse("2026-04-28T12:01:00Z"),
                Instant.parse("2026-04-28T12:00:59Z"),
                List.of());
        when(businessRepository.findTransactionById(BusinessTransactionId.of("txn-002")))
                .thenReturn(Optional.of(businessTransaction("txn-002", Instant.parse("2026-04-28T12:00:00Z"))));
        when(ledgerRepository.findPostingBatchByIdempotencyKey("idem-002"))
                .thenReturn(Optional.of(existingBatch));

        final PostEntriesHandler handler = new PostEntriesHandler(
                ledgerRepository,
                businessRepository,
                accountRepository,
                transactionManager,
                idGenerator);
        final PostEntriesCommand command = new PostEntriesCommand(
                "txn-002",
                "idem-002",
                List.of(new PostEntriesCommand.PostingRequest(
                        "acc-002",
                        CurrencyCode.of("USD"),
                        EntryDirection.CREDIT,
                        usd("10.0000"),
                        EntryType.ADJUSTMENT)));

        final PostingBatchId batchId = handler.handle(command);
        assertEquals("batch-existing", batchId.value());
        verify(ledgerRepository, never()).saveEntries(any());
        verify(ledgerRepository, never()).saveBalance(any());
    }

    /**
     * @brief 验证余额查询与分录查询处理器映射结果；
     *        Verify query handlers map balance and entry domain objects to results.
     */
    @Test
    void shouldMapQueryResults() {
        final LedgerRepository ledgerRepository = Mockito.mock(LedgerRepository.class);
        final FindBalanceHandler findBalanceHandler = new FindBalanceHandler(ledgerRepository);
        final ListLedgerEntriesHandler listEntriesHandler = new ListLedgerEntriesHandler(ledgerRepository);

        final Balance balance = Balance.restore(
                "acc-003",
                CurrencyCode.of("USD"),
                usd("20.0000"),
                usd("15.0000"),
                Instant.parse("2026-04-28T12:10:00Z"));
        when(ledgerRepository.findBalance("acc-003", CurrencyCode.of("USD")))
                .thenReturn(Optional.of(balance));

        final LedgerEntry entry = LedgerEntry.restore(
                LedgerEntryId.of("entry-003"),
                "txn-003",
                PostingBatchId.of("batch-003"),
                "acc-003",
                CurrencyCode.of("USD"),
                EntryDirection.DEBIT,
                usd("5.0000"),
                usd("20.0000"),
                usd("15.0000"),
                EntryType.FEE,
                Instant.parse("2026-04-28T12:10:01Z"));
        when(ledgerRepository.listRecentEntriesByAccountId("acc-003", 10))
                .thenReturn(List.of(entry));

        final Optional<BalanceResult> balanceResult = findBalanceHandler.handle(
                new FindBalanceQuery("acc-003", CurrencyCode.of("USD")));
        assertTrue(balanceResult.isPresent());
        assertEquals(usd("20.0000"), balanceResult.get().ledgerBalance());

        final List<LedgerEntryResult> entryResults = listEntriesHandler.handle(new ListLedgerEntriesQuery("acc-003", 10));
        assertEquals(1, entryResults.size());
        assertEquals("entry-003", entryResults.get(0).entryId());
    }

    /**
     * @brief 构建透传事务管理器（Build Passthrough Transaction Manager）；
     *        Build passthrough transaction manager for unit tests.
     *
     * @return 透传事务管理器（Passthrough transaction manager）。
     */
    private static DbTransactionManager passthroughTransactionManager() {
        return new DbTransactionManager() {
            @Override
            public <T> T execute(final Supplier<T> action) {
                return action.get();
            }
        };
    }

    /**
     * @brief 构建顺序 ID 生成器（Build Sequential ID Generator）；
     *        Build sequential id generator from predefined values.
     *
     * @param ids ID 序列（ID sequence）。
     * @return 顺序 ID 生成器（Sequential id generator）。
     */
    private static IdGenerator sequentialIdGenerator(final String... ids) {
        final AtomicInteger index = new AtomicInteger(0);
        return () -> ids[index.getAndIncrement()];
    }

    /**
     * @brief 创建活动账户实体（Create Active Account Entity）；
     *        Create active account entity for tests.
     *
     * @param accountId 账户 ID（Account ID）。
     * @param openedAt  开户时间（Opened timestamp）。
     * @return 账户实体（Account entity）。
     */
    private static Account activeAccount(final String accountId, final Instant openedAt) {
        return Account.restore(
                AccountId.of(accountId),
                CustomerId.of("cust-001"),
                AccountNumber.of("A-10001"),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE,
                openedAt,
                null);
    }

    /**
     * @brief 创建业务交易实体（Create Business Transaction Entity）；
     *        Create business transaction entity for tests.
     *
     * @param transactionId 交易 ID（Transaction ID）。
     * @param requestedAt   发起时间（Requested timestamp）。
     * @return 业务交易实体（Business transaction entity）。
     */
    private static BusinessTransaction businessTransaction(final String transactionId, final Instant requestedAt) {
        return BusinessTransaction.restore(
                BusinessTransactionId.of(transactionId),
                BusinessTypeCode.of("TRANSFER_INTERNAL"),
                null,
                null,
                BusinessChannel.SYSTEM,
                BusinessTransactionStatus.PENDING,
                requestedAt,
                null,
                BusinessReference.of("REF_" + transactionId.toUpperCase()),
                null);
    }

    /**
     * @brief 创建 USD 金额（Create USD Money）；
     *        Create USD money helper.
     *
     * @param amount 金额字符串（Amount string）。
     * @return USD 金额对象（USD money object）。
     */
    private static Money usd(final String amount) {
        return Money.of(CurrencyCode.of("USD"), new BigDecimal(amount));
    }
}
