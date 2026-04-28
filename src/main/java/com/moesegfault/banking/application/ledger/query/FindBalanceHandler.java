package com.moesegfault.banking.application.ledger.query;

import com.moesegfault.banking.application.ledger.result.BalanceResult;
import com.moesegfault.banking.domain.ledger.LedgerRepository;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 余额查询处理器（Find Balance Handler），基于仓储接口返回余额视图；
 *        Balance query handler returning result view through repository interface.
 */
public final class FindBalanceHandler {

    /**
     * @brief 账务仓储接口（Ledger Repository Interface）；
     *        Ledger repository interface.
     */
    private final LedgerRepository ledgerRepository;

    /**
     * @brief 构造余额查询处理器（Construct Find Balance Handler）；
     *        Construct balance query handler.
     *
     * @param ledgerRepository 账务仓储（Ledger repository）。
     */
    public FindBalanceHandler(final LedgerRepository ledgerRepository) {
        this.ledgerRepository = Objects.requireNonNull(ledgerRepository, "ledgerRepository must not be null");
    }

    /**
     * @brief 执行余额查询（Handle Find Balance Query）；
     *        Handle balance query request.
     *
     * @param query 查询请求（Query request）。
     * @return 余额结果可选值（Optional balance result）。
     */
    public Optional<BalanceResult> handle(final FindBalanceQuery query) {
        final FindBalanceQuery normalized = Objects.requireNonNull(query, "query must not be null");
        return ledgerRepository.findBalance(normalized.accountId(), normalized.currencyCode())
                .map(BalanceResult::fromDomain);
    }
}
