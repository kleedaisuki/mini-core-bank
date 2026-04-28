package com.moesegfault.banking.application.investment.query;

import com.moesegfault.banking.application.investment.result.HoldingResult;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.investment.Holding;
import com.moesegfault.banking.domain.investment.InvestmentAccountId;
import com.moesegfault.banking.domain.investment.InvestmentProduct;
import com.moesegfault.banking.domain.investment.InvestmentRepository;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @brief 投资持仓查询处理器（List Holdings Handler）；
 *        Query handler for listing holdings under an investment account.
 */
public final class ListHoldingsHandler {

    /**
     * @brief 投资仓储接口（Investment Repository Interface）；
     *        Investment repository dependency.
     */
    private final InvestmentRepository investmentRepository;

    /**
     * @brief 账户仓储接口（Account Repository Interface）；
     *        Account repository dependency.
     */
    private final AccountRepository accountRepository;

    /**
     * @brief 构造持仓查询处理器（Construct Holdings Query Handler）；
     *        Construct list-holdings handler.
     *
     * @param investmentRepository 投资仓储（Investment repository）。
     * @param accountRepository    账户仓储（Account repository）。
     */
    public ListHoldingsHandler(
            final InvestmentRepository investmentRepository,
            final AccountRepository accountRepository
    ) {
        this.investmentRepository = Objects.requireNonNull(
                investmentRepository,
                "investmentRepository must not be null");
        this.accountRepository = Objects.requireNonNull(accountRepository, "accountRepository must not be null");
    }

    /**
     * @brief 执行持仓查询（Handle Holdings Query）；
     *        Handle list-holdings query.
     *
     * @param query 持仓查询对象（List-holdings query）。
     * @return 持仓结果列表（Holding result list）。
     */
    public List<HoldingResult> handle(final ListHoldingsQuery query) {
        final ListHoldingsQuery normalized = Objects.requireNonNull(query, "query must not be null");
        final InvestmentAccountId investmentAccountId = InvestmentAccountId.of(normalized.investmentAccountId());
        ensureInvestmentAccountExists(investmentAccountId);

        final List<Holding> holdings = investmentRepository.listHoldingsByAccountId(investmentAccountId);
        final List<HoldingResult> results = new ArrayList<>(holdings.size());

        for (Holding holding : holdings) {
            if (!normalized.includeProductDetails()) {
                results.add(HoldingResult.fromHolding(holding));
                continue;
            }
            final InvestmentProduct product = investmentRepository.findProductById(holding.productId()).orElse(null);
            results.add(HoldingResult.fromHoldingAndProduct(holding, product));
        }

        return List.copyOf(results);
    }

    /**
     * @brief 校验投资账户存在（Ensure Investment Account Exists）；
     *        Ensure referenced investment account exists.
     *
     * @param investmentAccountId 投资账户 ID（Investment account ID）。
     */
    private void ensureInvestmentAccountExists(final InvestmentAccountId investmentAccountId) {
        final com.moesegfault.banking.domain.account.InvestmentAccountId accountId =
                com.moesegfault.banking.domain.account.InvestmentAccountId.of(investmentAccountId.value());
        if (accountRepository.findInvestmentAccountById(accountId).isEmpty()) {
            throw new BusinessRuleViolation("Investment account does not exist: " + investmentAccountId.value());
        }
    }
}
