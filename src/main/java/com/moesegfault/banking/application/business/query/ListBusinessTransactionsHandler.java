package com.moesegfault.banking.application.business.query;

import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.domain.business.BusinessRepository;
import com.moesegfault.banking.domain.business.BusinessTransaction;
import java.util.List;
import java.util.Objects;

/**
 * @brief 业务流水列表查询处理器（List Business Transactions Handler），执行只读筛选编排；
 *        List-business-transactions handler orchestrating read-only filtering and projection.
 */
public final class ListBusinessTransactionsHandler {

    /**
     * @brief 业务仓储接口（Business Repository Interface）；
     *        Business repository interface.
     */
    private final BusinessRepository businessRepository;

    /**
     * @brief 构造查询处理器（Construct Query Handler）；
     *        Construct query handler.
     *
     * @param businessRepository 业务仓储（Business repository）。
     */
    public ListBusinessTransactionsHandler(final BusinessRepository businessRepository) {
        this.businessRepository = Objects.requireNonNull(businessRepository, "businessRepository must not be null");
    }

    /**
     * @brief 执行业务流水列表查询（Handle List Query）；
     *        Handle list-business-transactions query.
     *
     * @param query 查询请求（Query request）。
     * @return 业务流水结果列表（Business transaction result list）。
     */
    public List<BusinessTransactionResult> handle(final ListBusinessTransactionsQuery query) {
        final ListBusinessTransactionsQuery normalized = Objects.requireNonNull(query, "query must not be null");
        final List<BusinessTransaction> transactions = loadTransactions(normalized);
        return transactions.stream()
                .limit(normalized.limit())
                .map(BusinessTransactionResult::from)
                .toList();
    }

    /**
     * @brief 读取并应用过滤条件（Load Transactions with Filters）；
     *        Load transactions and apply query filters.
     *
     * @param query 查询请求（Query request）。
     * @return 业务交易实体列表（Business transaction entities）。
     */
    private List<BusinessTransaction> loadTransactions(final ListBusinessTransactionsQuery query) {
        if (query.hasInitiatorCustomerId()) {
            final List<BusinessTransaction> byCustomer = businessRepository
                    .listTransactionsByCustomerId(query.toInitiatorCustomerIdOrNull());
            if (!query.hasTransactionStatus()) {
                return byCustomer;
            }
            return byCustomer.stream()
                    .filter(transaction -> transaction.transactionStatus() == query.transactionStatusOrNull())
                    .toList();
        }
        if (query.hasTransactionStatus()) {
            return businessRepository.listTransactionsByStatus(query.transactionStatusOrNull());
        }
        return businessRepository.findAllTransactions();
    }
}

