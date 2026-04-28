package com.moesegfault.banking.application.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.application.business.command.CompleteBusinessTransactionCommand;
import com.moesegfault.banking.application.business.command.CompleteBusinessTransactionHandler;
import com.moesegfault.banking.application.business.command.StartBusinessTransactionCommand;
import com.moesegfault.banking.application.business.command.StartBusinessTransactionHandler;
import com.moesegfault.banking.application.business.query.FindBusinessTransactionHandler;
import com.moesegfault.banking.application.business.query.FindBusinessTransactionQuery;
import com.moesegfault.banking.application.business.query.ListBusinessTransactionsHandler;
import com.moesegfault.banking.application.business.query.ListBusinessTransactionsQuery;
import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.domain.business.BusinessCategory;
import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.business.BusinessReference;
import com.moesegfault.banking.domain.business.BusinessRepository;
import com.moesegfault.banking.domain.business.BusinessTransaction;
import com.moesegfault.banking.domain.business.BusinessTransactionFailed;
import com.moesegfault.banking.domain.business.BusinessTransactionId;
import com.moesegfault.banking.domain.business.BusinessTransactionStarted;
import com.moesegfault.banking.domain.business.BusinessTransactionStatus;
import com.moesegfault.banking.domain.business.BusinessType;
import com.moesegfault.banking.domain.business.BusinessTypeCode;
import com.moesegfault.banking.domain.business.BusinessTypeStatus;
import com.moesegfault.banking.domain.business.CustomerId;
import com.moesegfault.banking.domain.shared.DomainEvent;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/**
 * @brief 业务应用层单元测试（Business Application Layer Unit Test），验证命令编排与查询语义；
 *        Business application-layer unit tests verifying command orchestration and query semantics.
 */
class BusinessApplicationTest {

    /**
     * @brief 验证开始业务流水会持久化并发布 started 事件；
     *        Verify starting transaction persists data and publishes started event.
     */
    @Test
    void shouldStartTransactionAndPublishStartedEvent() {
        final InMemoryBusinessRepository repository = new InMemoryBusinessRepository();
        repository.saveBusinessType(BusinessType.restore(
                BusinessTypeCode.of("TRANSFER_INTERNAL"),
                BusinessCategory.TRANSFER,
                "Internal transfer",
                "demo",
                true,
                true,
                BusinessTypeStatus.ACTIVE));
        final List<DomainEvent> events = new ArrayList<>();
        final DomainEventPublisher eventPublisher = events::add;
        final IdGenerator idGenerator = () -> "123e4567-e89b-12d3-a456-426614174000";
        final StartBusinessTransactionHandler handler = new StartBusinessTransactionHandler(
                repository,
                new PassthroughTransactionManager(),
                idGenerator,
                eventPublisher);

        final BusinessTransactionResult result = handler.handle(new StartBusinessTransactionCommand(
                "TRANSFER_INTERNAL",
                "cust-001",
                "operator-01",
                BusinessChannel.ONLINE,
                "REF_20260428_9001",
                "integration-test"));

        assertEquals("123e4567-e89b-12d3-a456-426614174000", result.transactionId());
        assertEquals(BusinessTransactionStatus.PENDING, result.transactionStatus());
        assertEquals("cust-001", result.initiatorCustomerIdOrNull());
        assertEquals(1, events.size());
        assertInstanceOf(BusinessTransactionStarted.class, events.get(0));
    }

    /**
     * @brief 验证失败终态会更新状态并发布 failed 事件；
     *        Verify failed completion updates status and publishes failed event.
     */
    @Test
    void shouldFailTransactionAndPublishFailedEvent() {
        final InMemoryBusinessRepository repository = new InMemoryBusinessRepository();
        repository.saveBusinessType(BusinessType.restore(
                BusinessTypeCode.of("TRANSFER_INTERNAL"),
                BusinessCategory.TRANSFER,
                "Internal transfer",
                "demo",
                true,
                true,
                BusinessTypeStatus.ACTIVE));
        final BusinessTransaction transaction = BusinessTransaction.start(
                BusinessTransactionId.of("txn-101"),
                BusinessTypeCode.of("TRANSFER_INTERNAL"),
                CustomerId.of("cust-001"),
                "operator-01",
                BusinessChannel.BRANCH,
                BusinessReference.of("REF_20260428_9101"),
                null);
        repository.saveTransaction(transaction);

        final List<DomainEvent> events = new ArrayList<>();
        final CompleteBusinessTransactionHandler handler = new CompleteBusinessTransactionHandler(
                repository,
                new PassthroughTransactionManager(),
                events::add);

        final BusinessTransactionResult result = handler.handle(new CompleteBusinessTransactionCommand(
                "txn-101",
                CompleteBusinessTransactionCommand.CompletionAction.FAILED,
                Instant.now().plusSeconds(1),
                "insufficient balance"));

        assertEquals(BusinessTransactionStatus.FAILED, result.transactionStatus());
        assertNotNull(result.completedAtOrNull());
        assertEquals(1, events.size());
        assertInstanceOf(BusinessTransactionFailed.class, events.get(0));
    }

    /**
     * @brief 验证冲正终态会更新状态并保留查询一致性；
     *        Verify reversal updates status while preserving lookup consistency.
     */
    @Test
    void shouldReverseSuccessfulTransaction() {
        final InMemoryBusinessRepository repository = new InMemoryBusinessRepository();
        repository.saveBusinessType(BusinessType.restore(
                BusinessTypeCode.of("TRANSFER_INTERNAL"),
                BusinessCategory.TRANSFER,
                "Internal transfer",
                "demo",
                true,
                true,
                BusinessTypeStatus.ACTIVE));
        final BusinessTransaction successTransaction = BusinessTransaction.restore(
                BusinessTransactionId.of("txn-201"),
                BusinessTypeCode.of("TRANSFER_INTERNAL"),
                CustomerId.of("cust-001"),
                "operator-01",
                BusinessChannel.SYSTEM,
                BusinessTransactionStatus.SUCCESS,
                Instant.parse("2026-04-28T08:00:00Z"),
                Instant.parse("2026-04-28T08:01:00Z"),
                BusinessReference.of("REF_20260428_9201"),
                "done");
        repository.saveTransaction(successTransaction);

        final List<DomainEvent> events = new ArrayList<>();
        final CompleteBusinessTransactionHandler handler = new CompleteBusinessTransactionHandler(
                repository,
                new PassthroughTransactionManager(),
                events::add);

        final BusinessTransactionResult result = handler.handle(new CompleteBusinessTransactionCommand(
                "txn-201",
                CompleteBusinessTransactionCommand.CompletionAction.REVERSED,
                Instant.parse("2026-04-28T08:02:00Z"),
                "manual reversal"));

        assertEquals(BusinessTransactionStatus.REVERSED, result.transactionStatus());
        assertTrue(events.isEmpty());
    }

    /**
     * @brief 验证查询支持按参考号与组合过滤列表；
     *        Verify queries support lookup by reference and combined list filtering.
     */
    @Test
    void shouldFindAndListTransactionsByFilters() {
        final InMemoryBusinessRepository repository = new InMemoryBusinessRepository();
        final BusinessTransaction txnA = BusinessTransaction.restore(
                BusinessTransactionId.of("txn-301"),
                BusinessTypeCode.of("TRANSFER_INTERNAL"),
                CustomerId.of("cust-A"),
                "op-a",
                BusinessChannel.MOBILE,
                BusinessTransactionStatus.SUCCESS,
                Instant.parse("2026-04-28T09:00:00Z"),
                Instant.parse("2026-04-28T09:01:00Z"),
                BusinessReference.of("REF_20260428_9301"),
                "ok");
        final BusinessTransaction txnB = BusinessTransaction.restore(
                BusinessTransactionId.of("txn-302"),
                BusinessTypeCode.of("TRANSFER_INTERNAL"),
                CustomerId.of("cust-A"),
                "op-a",
                BusinessChannel.MOBILE,
                BusinessTransactionStatus.FAILED,
                Instant.parse("2026-04-28T09:10:00Z"),
                Instant.parse("2026-04-28T09:11:00Z"),
                BusinessReference.of("REF_20260428_9302"),
                "failed");
        final BusinessTransaction txnC = BusinessTransaction.restore(
                BusinessTransactionId.of("txn-303"),
                BusinessTypeCode.of("TRANSFER_INTERNAL"),
                CustomerId.of("cust-B"),
                "op-b",
                BusinessChannel.BRANCH,
                BusinessTransactionStatus.SUCCESS,
                Instant.parse("2026-04-28T09:20:00Z"),
                Instant.parse("2026-04-28T09:21:00Z"),
                BusinessReference.of("REF_20260428_9303"),
                "ok");
        repository.saveTransaction(txnA);
        repository.saveTransaction(txnB);
        repository.saveTransaction(txnC);

        final FindBusinessTransactionHandler findHandler = new FindBusinessTransactionHandler(repository);
        final Optional<BusinessTransactionResult> found = findHandler.handle(
                FindBusinessTransactionQuery.byReferenceNo("REF_20260428_9302"));
        assertTrue(found.isPresent());
        assertEquals("txn-302", found.orElseThrow().transactionId());

        final ListBusinessTransactionsHandler listHandler = new ListBusinessTransactionsHandler(repository);
        final List<BusinessTransactionResult> listed = listHandler.handle(new ListBusinessTransactionsQuery(
                "cust-A",
                BusinessTransactionStatus.FAILED,
                10));

        assertEquals(1, listed.size());
        assertEquals("txn-302", listed.get(0).transactionId());
    }

    /**
     * @brief 透传事务管理器（Passthrough Transaction Manager）；
     *        Passthrough transaction manager for tests.
     */
    private static final class PassthroughTransactionManager implements DbTransactionManager {

        /**
         * @brief 在当前线程直接执行事务动作；
         *        Execute transactional action directly in current thread.
         *
         * @param action 事务动作（Transactional action）。
         * @param <T> 返回类型（Result type）。
         * @return 动作执行结果（Action result）。
         */
        @Override
        public <T> T execute(final Supplier<T> action) {
            return Objects.requireNonNull(action, "action must not be null").get();
        }
    }

    /**
     * @brief 内存版业务仓储（In-Memory Business Repository）；
     *        In-memory business repository for application tests.
     */
    private static final class InMemoryBusinessRepository implements BusinessRepository {

        /**
         * @brief 业务类型存储（Business Type Storage）；
         *        In-memory business type storage.
         */
        private final Map<String, BusinessType> businessTypes = new HashMap<>();

        /**
         * @brief 业务交易存储（Business Transaction Storage）；
         *        In-memory business transaction storage.
         */
        private final Map<String, BusinessTransaction> transactions = new HashMap<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void saveBusinessType(final BusinessType businessType) {
            businessTypes.put(businessType.businessTypeCode().value(), businessType);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void saveTransaction(final BusinessTransaction businessTransaction) {
            transactions.put(businessTransaction.transactionId().value(), businessTransaction);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<BusinessType> findBusinessTypeByCode(final BusinessTypeCode businessTypeCode) {
            return Optional.ofNullable(businessTypes.get(businessTypeCode.value()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<BusinessTransaction> findTransactionById(final BusinessTransactionId transactionId) {
            return Optional.ofNullable(transactions.get(transactionId.value()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<BusinessTransaction> findTransactionByReference(final BusinessReference referenceNo) {
            return transactions.values().stream()
                    .filter(transaction -> transaction.referenceNo().equals(referenceNo))
                    .findFirst();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean existsTransactionByReference(final BusinessReference referenceNo) {
            return transactions.values().stream()
                    .anyMatch(transaction -> transaction.referenceNo().equals(referenceNo));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<BusinessTransaction> listTransactionsByCustomerId(final CustomerId initiatorCustomerId) {
            return transactions.values().stream()
                    .filter(transaction -> transaction.initiatorCustomerIdOrNull() != null)
                    .filter(transaction -> transaction.initiatorCustomerIdOrNull().sameValueAs(initiatorCustomerId))
                    .sorted(Comparator.comparing(BusinessTransaction::requestedAt).reversed())
                    .toList();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<BusinessTransaction> listTransactionsByStatus(final BusinessTransactionStatus transactionStatus) {
            return transactions.values().stream()
                    .filter(transaction -> transaction.transactionStatus() == transactionStatus)
                    .sorted(Comparator.comparing(BusinessTransaction::requestedAt).reversed())
                    .toList();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<BusinessTransaction> findAllTransactions() {
            return transactions.values().stream()
                    .sorted(Comparator.comparing(BusinessTransaction::requestedAt).reversed())
                    .toList();
        }
    }
}
