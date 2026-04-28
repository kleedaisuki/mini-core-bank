package com.moesegfault.banking.application.business.query;

import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.domain.business.BusinessRepository;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 查询单笔业务流水处理器（Find Business Transaction Handler），执行只读查询编排；
 *        Find-business-transaction handler orchestrating read-only single transaction lookup.
 */
public final class FindBusinessTransactionHandler {

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
    public FindBusinessTransactionHandler(final BusinessRepository businessRepository) {
        this.businessRepository = Objects.requireNonNull(businessRepository, "businessRepository must not be null");
    }

    /**
     * @brief 执行查询（Handle Query）；
     *        Handle single business-transaction query.
     *
     * @param query 查询请求（Query request）。
     * @return 查询结果（Query result as optional）。
     */
    public Optional<BusinessTransactionResult> handle(final FindBusinessTransactionQuery query) {
        final FindBusinessTransactionQuery normalized = Objects.requireNonNull(query, "query must not be null");
        if (normalized.hasTransactionId()) {
            return businessRepository.findTransactionById(normalized.toTransactionId())
                    .map(BusinessTransactionResult::from);
        }
        return businessRepository.findTransactionByReference(normalized.toReferenceNo())
                .map(BusinessTransactionResult::from);
    }
}

