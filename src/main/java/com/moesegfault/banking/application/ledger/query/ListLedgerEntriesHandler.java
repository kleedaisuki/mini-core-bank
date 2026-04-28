package com.moesegfault.banking.application.ledger.query;

import com.moesegfault.banking.application.ledger.result.LedgerEntryResult;
import com.moesegfault.banking.domain.ledger.LedgerRepository;
import java.util.List;
import java.util.Objects;

/**
 * @brief 分录列表查询处理器（List Ledger Entries Handler），返回账户最近分录结果视图；
 *        Ledger-entry list query handler returning recent account-entry result views.
 */
public final class ListLedgerEntriesHandler {

    /**
     * @brief 账务仓储接口（Ledger Repository Interface）；
     *        Ledger repository interface.
     */
    private final LedgerRepository ledgerRepository;

    /**
     * @brief 构造分录列表查询处理器（Construct List Ledger Entries Handler）；
     *        Construct ledger-entry list query handler.
     *
     * @param ledgerRepository 账务仓储（Ledger repository）。
     */
    public ListLedgerEntriesHandler(final LedgerRepository ledgerRepository) {
        this.ledgerRepository = Objects.requireNonNull(ledgerRepository, "ledgerRepository must not be null");
    }

    /**
     * @brief 执行分录列表查询（Handle List Ledger Entries Query）；
     *        Handle ledger-entry list query.
     *
     * @param query 查询请求（Query request）。
     * @return 分录结果列表（Ledger-entry result list）。
     */
    public List<LedgerEntryResult> handle(final ListLedgerEntriesQuery query) {
        final ListLedgerEntriesQuery normalized = Objects.requireNonNull(query, "query must not be null");
        return ledgerRepository.listRecentEntriesByAccountId(normalized.accountId(), normalized.limit())
                .stream()
                .map(LedgerEntryResult::fromDomain)
                .toList();
    }
}
