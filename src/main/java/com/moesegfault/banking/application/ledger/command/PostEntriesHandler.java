package com.moesegfault.banking.application.ledger.command;

import com.moesegfault.banking.domain.account.Account;
import com.moesegfault.banking.domain.account.AccountId;
import com.moesegfault.banking.domain.account.AccountPolicy;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.business.BusinessRepository;
import com.moesegfault.banking.domain.business.BusinessTransaction;
import com.moesegfault.banking.domain.business.BusinessTransactionId;
import com.moesegfault.banking.domain.business.BusinessTransactionStatus;
import com.moesegfault.banking.domain.ledger.Balance;
import com.moesegfault.banking.domain.ledger.BalancePolicy;
import com.moesegfault.banking.domain.ledger.LedgerEntry;
import com.moesegfault.banking.domain.ledger.LedgerEntryId;
import com.moesegfault.banking.domain.ledger.LedgerRepository;
import com.moesegfault.banking.domain.ledger.PostingBatch;
import com.moesegfault.banking.domain.ledger.PostingBatchId;
import com.moesegfault.banking.domain.ledger.PostingPolicy;
import com.moesegfault.banking.domain.ledger.PostingStatus;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 入账处理器（Post Entries Handler），编排批次、分录、余额与幂等事务流程；
 *        Posting handler orchestrating batch, entries, balances, and idempotent transactional flow.
 */
public final class PostEntriesHandler {

    /**
     * @brief 账务仓储接口（Ledger Repository Interface）；
     *        Ledger repository interface.
     */
    private final LedgerRepository ledgerRepository;

    /**
     * @brief 业务流水仓储接口（Business Repository Interface）；
     *        Business repository interface.
     */
    private final BusinessRepository businessRepository;

    /**
     * @brief 账户仓储接口（Account Repository Interface）；
     *        Account repository interface.
     */
    private final AccountRepository accountRepository;

    /**
     * @brief 事务管理接口（Transaction Manager Interface）；
     *        Transaction manager interface.
     */
    private final DbTransactionManager transactionManager;

    /**
     * @brief ID 生成器接口（ID Generator Interface）；
     *        ID generator interface.
     */
    private final IdGenerator idGenerator;

    /**
     * @brief 领域事件发布器（Domain Event Publisher）；
     *        Domain event publisher.
     */
    private final DomainEventPublisher eventPublisher;

    /**
     * @brief 构造入账处理器（Construct Post Entries Handler）；
     *        Construct posting handler.
     *
     * @param ledgerRepository   账务仓储（Ledger repository）。
     * @param businessRepository 业务仓储（Business repository）。
     * @param accountRepository  账户仓储（Account repository）。
     * @param transactionManager 事务管理器（Transaction manager）。
     * @param idGenerator        ID 生成器（ID generator）。
     */
    public PostEntriesHandler(
            final LedgerRepository ledgerRepository,
            final BusinessRepository businessRepository,
            final AccountRepository accountRepository,
            final DbTransactionManager transactionManager,
            final IdGenerator idGenerator
    ) {
        this(
                ledgerRepository,
                businessRepository,
                accountRepository,
                transactionManager,
                idGenerator,
                DomainEventPublisher.noop());
    }

    /**
     * @brief 构造入账处理器并注入事件发布器（Construct Handler with Event Publisher）；
     *        Construct posting handler with explicit event publisher.
     *
     * @param ledgerRepository   账务仓储（Ledger repository）。
     * @param businessRepository 业务仓储（Business repository）。
     * @param accountRepository  账户仓储（Account repository）。
     * @param transactionManager 事务管理器（Transaction manager）。
     * @param idGenerator        ID 生成器（ID generator）。
     * @param eventPublisher     事件发布器（Event publisher）。
     */
    public PostEntriesHandler(
            final LedgerRepository ledgerRepository,
            final BusinessRepository businessRepository,
            final AccountRepository accountRepository,
            final DbTransactionManager transactionManager,
            final IdGenerator idGenerator,
            final DomainEventPublisher eventPublisher
    ) {
        this.ledgerRepository = Objects.requireNonNull(ledgerRepository, "ledgerRepository must not be null");
        this.businessRepository = Objects.requireNonNull(businessRepository, "businessRepository must not be null");
        this.accountRepository = Objects.requireNonNull(accountRepository, "accountRepository must not be null");
        this.transactionManager = Objects.requireNonNull(transactionManager, "transactionManager must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher must not be null");
    }

    /**
     * @brief 执行入账命令（Handle Post Entries Command）；
     *        Handle posting command.
     *
     * @param command 入账命令（Posting command）。
     * @return 入账批次 ID（Posting batch ID）。
     */
    public PostingBatchId handle(final PostEntriesCommand command) {
        final PostEntriesCommand normalized = Objects.requireNonNull(command, "command must not be null");
        return transactionManager.execute(() -> postInTransaction(normalized));
    }

    /**
     * @brief 在事务中执行入账（Post Entries in Transaction）；
     *        Execute posting workflow inside transaction.
     *
     * @param command 入账命令（Posting command）。
     * @return 入账批次 ID（Posting batch ID）。
     */
    private PostingBatchId postInTransaction(final PostEntriesCommand command) {
        ensureBusinessTransactionPostable(command.transactionId());
        final Optional<PostingBatch> existingIdempotentBatch = findExistingIdempotentBatch(command);
        if (existingIdempotentBatch.isPresent()) {
            return resolveExistingBatch(existingIdempotentBatch.get());
        }

        final Instant postedAt = command.effectivePostedAt();
        final PostingBatch postingBatch = PostingBatch.createPending(
                PostingBatchId.of(idGenerator.nextId()),
                command.transactionId(),
                command.idempotencyKey(),
                Instant.now());
        final Map<BalanceKey, Balance> workingBalances = new LinkedHashMap<>();
        final Map<String, Account> cachedAccounts = new LinkedHashMap<>();

        for (PostEntriesCommand.PostingRequest postingRequest : command.postingRequests()) {
            final String accountId = postingRequest.accountId();
            final CurrencyCode currencyCode = postingRequest.currencyCode();
            ensureAccountOperable(accountId, cachedAccounts);
            final Balance currentBalance = loadWorkingBalance(accountId, currencyCode, postedAt, workingBalances);
            if (postingRequest.entryDirection().isDecreaseLike()) {
                BalancePolicy.ensureSufficientAvailableBalance(currentBalance, postingRequest.amount());
            }
            final Balance nextBalance = currentBalance.applyEntry(
                    postingRequest.entryDirection(),
                    postingRequest.amount(),
                    postedAt);
            workingBalances.put(BalanceKey.of(accountId, currencyCode), nextBalance);

            final LedgerEntry entry = LedgerEntry.create(
                    LedgerEntryId.of(idGenerator.nextId()),
                    command.transactionId(),
                    accountId,
                    currencyCode,
                    postingRequest.entryDirection(),
                    postingRequest.amount(),
                    postingRequest.entryType(),
                    postedAt).withBalanceAfter(nextBalance);
            postingBatch.addEntry(entry);
        }

        postingBatch.markPosted(postedAt);
        ledgerRepository.savePostingBatch(postingBatch);
        ledgerRepository.saveEntries(postingBatch.entries());
        final List<Balance> persistedBalances = workingBalances.values().stream().toList();
        for (Balance balance : persistedBalances) {
            ledgerRepository.saveBalance(balance);
        }
        publishDomainEvents(postingBatch.entries(), persistedBalances);
        return postingBatch.batchId();
    }

    /**
     * @brief 解析已存在批次的幂等返回行为（Resolve Existing Batch Idempotent Behavior）；
     *        Resolve idempotent return behavior for existing batch.
     *
     * @param existingBatch 已存在批次（Existing batch）。
     * @return 批次 ID（Batch ID）。
     */
    private static PostingBatchId resolveExistingBatch(final PostingBatch existingBatch) {
        if (existingBatch.batchStatus() == PostingStatus.POSTED) {
            return existingBatch.batchId();
        }
        throw new BusinessRuleViolation(
                "Existing posting batch status is not idempotent-success: " + existingBatch.batchStatus());
    }

    /**
     * @brief 查询已存在的幂等批次（Find Existing Idempotent Batch）；
     *        Find existing idempotent batch by idempotency key or transaction id.
     *
     * @param command 入账命令（Posting command）。
     * @return 批次可选值（Optional posting batch）。
     */
    private Optional<PostingBatch> findExistingIdempotentBatch(final PostEntriesCommand command) {
        if (command.idempotencyKey() != null) {
            final Optional<PostingBatch> existingByIdempotencyKey = ledgerRepository.findPostingBatchByIdempotencyKey(
                    command.idempotencyKey());
            PostingPolicy.ensureTransactionIdempotency(existingByIdempotencyKey, command.transactionId());
            if (existingByIdempotencyKey.isPresent()) {
                return existingByIdempotencyKey;
            }
        }
        return ledgerRepository.findPostingBatchByTransactionId(command.transactionId());
    }

    /**
     * @brief 校验业务交易可入账（Ensure Business Transaction Is Postable）；
     *        Ensure business transaction exists and can be posted.
     *
     * @param transactionId 交易 ID（Transaction ID）。
     */
    private void ensureBusinessTransactionPostable(final String transactionId) {
        final BusinessTransaction transaction = businessRepository.findTransactionById(BusinessTransactionId.of(transactionId))
                .orElseThrow(() -> new BusinessRuleViolation("Business transaction does not exist: " + transactionId));
        final BusinessTransactionStatus status = transaction.transactionStatus();
        if (status == BusinessTransactionStatus.FAILED || status == BusinessTransactionStatus.REVERSED) {
            throw new BusinessRuleViolation("Business transaction is not postable in status: " + status);
        }
    }

    /**
     * @brief 校验账户可操作（Ensure Account Is Operable）；
     *        Ensure account exists and is operable.
     *
     * @param accountId      账户 ID（Account ID）。
     * @param cachedAccounts 账户缓存（Account cache）。
     */
    private void ensureAccountOperable(final String accountId, final Map<String, Account> cachedAccounts) {
        if (cachedAccounts.containsKey(accountId)) {
            return;
        }
        final Account account = accountRepository.findAccountById(AccountId.of(accountId))
                .orElseThrow(() -> new BusinessRuleViolation("Account does not exist: " + accountId));
        AccountPolicy.ensureOperable(account, "post entries");
        cachedAccounts.put(accountId, account);
    }

    /**
     * @brief 加载或初始化工作余额（Load or Initialize Working Balance）；
     *        Load or initialize working balance in current posting context.
     *
     * @param accountId       账户 ID（Account ID）。
     * @param currencyCode    币种代码（Currency code）。
     * @param asOfTime        时间点（As-of time）。
     * @param workingBalances 工作余额缓存（Working balance cache）。
     * @return 当前工作余额（Current working balance）。
     */
    private Balance loadWorkingBalance(
            final String accountId,
            final CurrencyCode currencyCode,
            final Instant asOfTime,
            final Map<BalanceKey, Balance> workingBalances
    ) {
        final BalanceKey balanceKey = BalanceKey.of(accountId, currencyCode);
        final Balance cached = workingBalances.get(balanceKey);
        if (cached != null) {
            return cached;
        }
        final Balance loaded = ledgerRepository.findBalance(accountId, currencyCode)
                .orElseGet(() -> Balance.initialize(accountId, currencyCode, asOfTime));
        workingBalances.put(balanceKey, loaded);
        return loaded;
    }

    /**
     * @brief 发布入账相关领域事件（Publish Posting-related Domain Events）；
     *        Publish domain events related to posting entries and balances.
     *
     * @param entries  分录列表（Entry list）。
     * @param balances 余额列表（Balance list）。
     */
    private void publishDomainEvents(
            final List<LedgerEntry> entries,
            final List<Balance> balances
    ) {
        for (LedgerEntry entry : entries) {
            eventPublisher.publish(entry.postedEvent());
        }
        for (Balance balance : balances) {
            eventPublisher.publish(balance.updatedEvent());
        }
    }

    /**
     * @brief 余额键（Balance Key），用于同事务内余额缓存；
     *        Balance key used for in-transaction working-balance cache.
     *
     * @param accountId    账户 ID（Account ID）。
     * @param currencyCode 币种代码（Currency code）。
     */
    private record BalanceKey(
            String accountId,
            CurrencyCode currencyCode
    ) {

        /**
         * @brief 创建并标准化余额键（Create Normalized Balance Key）；
         *        Create normalized balance key.
         *
         * @param accountId    账户 ID（Account ID）。
         * @param currencyCode 币种代码（Currency code）。
         * @return 余额键（Balance key）。
         */
        static BalanceKey of(final String accountId, final CurrencyCode currencyCode) {
            return new BalanceKey(
                    normalizeRequiredId(accountId, "Account ID"),
                    Objects.requireNonNull(currencyCode, "Currency code must not be null"));
        }
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
}
