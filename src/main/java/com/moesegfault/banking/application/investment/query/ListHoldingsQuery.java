package com.moesegfault.banking.application.investment.query;

import java.util.Objects;

/**
 * @brief 投资持仓列表查询（List Holdings Query）；
 *        Query object for listing holdings under an investment account.
 */
public final class ListHoldingsQuery {

    /**
     * @brief 投资账户 ID（Investment Account ID）；
     *        Investment account identifier string.
     */
    private final String investmentAccountId;

    /**
     * @brief 是否包含产品详情（Include Product Details）；
     *        Whether to enrich holdings with product details.
     */
    private final boolean includeProductDetails;

    /**
     * @brief 构造持仓列表查询（Construct Holdings Query）；
     *        Construct list-holdings query.
     *
     * @param investmentAccountId  投资账户 ID（Investment account ID）。
     * @param includeProductDetails 是否包含产品详情（Include product details）。
     */
    public ListHoldingsQuery(
            final String investmentAccountId,
            final boolean includeProductDetails
    ) {
        this.investmentAccountId = Objects.requireNonNull(investmentAccountId, "investmentAccountId must not be null");
        this.includeProductDetails = includeProductDetails;
    }

    /**
     * @brief 返回投资账户 ID（Return Investment Account ID）；
     *        Return investment account ID.
     *
     * @return 投资账户 ID（Investment account ID）。
     */
    public String investmentAccountId() {
        return investmentAccountId;
    }

    /**
     * @brief 返回是否包含产品详情（Return Include-Details Flag）；
     *        Return include-product-details flag.
     *
     * @return 包含返回 true（true when include details）。
     */
    public boolean includeProductDetails() {
        return includeProductDetails;
    }
}
