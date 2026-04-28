package com.moesegfault.banking.application.credit.query;

import com.moesegfault.banking.application.credit.result.CreditCardStatementResult;
import com.moesegfault.banking.domain.credit.CreditCardAccountId;
import com.moesegfault.banking.domain.credit.CreditRepository;
import com.moesegfault.banking.domain.credit.StatementId;
import com.moesegfault.banking.domain.shared.DateRange;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 查询账单处理器（Find Statement Handler），编排账单查询路径并返回应用层结果；
 *        Find-statement handler orchestrating statement query paths and returning application-layer result.
 */
public final class FindStatementHandler {

    /**
     * @brief 信用仓储接口（Credit Repository）；
     *        Credit repository contract.
     */
    private final CreditRepository creditRepository;

    /**
     * @brief 构造查询处理器（Construct Find Statement Handler）；
     *        Construct find-statement handler.
     *
     * @param creditRepository 信用仓储接口（Credit repository）。
     */
    public FindStatementHandler(final CreditRepository creditRepository) {
        this.creditRepository = Objects.requireNonNull(creditRepository, "creditRepository must not be null");
    }

    /**
     * @brief 处理查询请求（Handle Find Statement Query）；
     *        Handle find-statement query.
     *
     * @param query 查询请求（Find-statement query）。
     * @return 账单结果可选值（Optional statement result）。
     */
    public Optional<CreditCardStatementResult> handle(final FindStatementQuery query) {
        final FindStatementQuery normalizedQuery = Objects.requireNonNull(query, "query must not be null");
        final boolean hasStatementId = normalizedQuery.statementIdOrNull() != null;
        final boolean hasPeriodTuple = normalizedQuery.creditCardAccountIdOrNull() != null
                && normalizedQuery.statementPeriodStartOrNull() != null
                && normalizedQuery.statementPeriodEndOrNull() != null;

        if (hasStatementId == hasPeriodTuple) {
            throw new IllegalArgumentException(
                    "FindStatementQuery must specify either statementId or accountId+period");
        }

        if (hasStatementId) {
            return creditRepository.findStatementById(StatementId.of(normalizedQuery.statementIdOrNull()))
                    .map(CreditCardStatementResult::from);
        }

        return creditRepository.findStatementByPeriod(
                        CreditCardAccountId.of(normalizedQuery.creditCardAccountIdOrNull()),
                        DateRange.of(
                                normalizedQuery.statementPeriodStartOrNull(),
                                normalizedQuery.statementPeriodEndOrNull()))
                .map(CreditCardStatementResult::from);
    }
}
