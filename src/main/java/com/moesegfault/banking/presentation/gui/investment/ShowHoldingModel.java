package com.moesegfault.banking.presentation.gui.investment;

import com.moesegfault.banking.application.investment.result.HoldingResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import java.util.List;
import java.util.Objects;

/**
 * @brief 投资持仓页面模型（Show Holding Page Model）；
 *        GUI model holding holdings-query condition, loading state and result list.
 */
public final class ShowHoldingModel extends AbstractGuiModel {

    /**
     * @brief 查询账户 ID（Query Account ID）；
     *        Investment account identifier used by current query.
     */
    private String investmentAccountId = "";

    /**
     * @brief 包含产品详情标记（Include Product Details Flag）；
     *        Whether to include product detail fields when querying holdings.
     */
    private boolean includeProductDetails;

    /**
     * @brief 加载中标记（Loading Flag）；
     *        Whether holdings query is currently in progress.
     */
    private boolean loading;

    /**
     * @brief 错误消息（可空）（Error Message, Nullable）；
     *        Error message from failed holdings query.
     */
    private String errorMessageOrNull;

    /**
     * @brief 成功消息（可空）（Success Message, Nullable）；
     *        Success message from successful holdings query.
     */
    private String successMessageOrNull;

    /**
     * @brief 持仓结果列表（Holding Result List）；
     *        Current holdings query result list.
     */
    private List<HoldingResult> holdings = List.of();

    /**
     * @brief 读取查询账户 ID（Read Query Account ID）；
     *        Read investment account id in current model state.
     *
     * @return 投资账户 ID（Investment account ID）。
     */
    public String investmentAccountId() {
        return investmentAccountId;
    }

    /**
     * @brief 读取详情标记（Read Include-Details Flag）；
     *        Read include-product-details flag.
     *
     * @return 包含详情返回 true（true when include details）。
     */
    public boolean includeProductDetails() {
        return includeProductDetails;
    }

    /**
     * @brief 读取加载中标记（Read Loading Flag）；
     *        Read whether query is in loading state.
     *
     * @return 加载中返回 true（true when loading）。
     */
    public boolean loading() {
        return loading;
    }

    /**
     * @brief 读取错误消息（可空）（Read Error Message, Nullable）；
     *        Read error message from failed query.
     *
     * @return 错误消息或 null（Error message or null）。
     */
    public String errorMessageOrNull() {
        return errorMessageOrNull;
    }

    /**
     * @brief 读取成功消息（可空）（Read Success Message, Nullable）；
     *        Read success message from successful query.
     *
     * @return 成功消息或 null（Success message or null）。
     */
    public String successMessageOrNull() {
        return successMessageOrNull;
    }

    /**
     * @brief 读取持仓结果（Read Holding Results）；
     *        Read immutable holdings result list.
     *
     * @return 持仓结果列表（Holding result list）。
     */
    public List<HoldingResult> holdings() {
        return holdings;
    }

    /**
     * @brief 更新查询条件（Update Query Condition）；
     *        Update query condition before executing holdings query.
     *
     * @param accountId 投资账户 ID（Investment account ID）。
     * @param includeDetails 包含产品详情标记（Include product details flag）。
     */
    public void updateQueryCondition(final String accountId, final boolean includeDetails) {
        final String normalizedAccountId = Objects.requireNonNull(accountId, "accountId must not be null").trim();
        this.investmentAccountId = normalizedAccountId;
        this.includeProductDetails = includeDetails;
        fireChanged("investmentAccountId", "includeProductDetails");
    }

    /**
     * @brief 标记开始加载（Mark Query Loading Started）；
     *        Mark model entering holdings-query loading phase.
     */
    public void markLoading() {
        this.loading = true;
        this.errorMessageOrNull = null;
        this.successMessageOrNull = null;
        fireChanged("loading", "errorMessageOrNull", "successMessageOrNull");
    }

    /**
     * @brief 标记加载成功（Mark Query Succeeded）；
     *        Mark model state after successful holdings query.
     *
     * @param results 持仓结果（Holding results）。
     * @param successMessage 成功消息（Success message）。
     */
    public void markSuccess(final List<HoldingResult> results, final String successMessage) {
        this.loading = false;
        this.holdings = List.copyOf(Objects.requireNonNull(results, "results must not be null"));
        this.errorMessageOrNull = null;
        this.successMessageOrNull = Objects.requireNonNull(successMessage, "successMessage must not be null");
        fireChanged("loading", "holdings", "errorMessageOrNull", "successMessageOrNull");
    }

    /**
     * @brief 标记加载失败（Mark Query Failed）；
     *        Mark model state after failed holdings query.
     *
     * @param errorMessage 错误消息（Error message）。
     */
    public void markFailure(final String errorMessage) {
        this.loading = false;
        this.holdings = List.of();
        this.errorMessageOrNull = Objects.requireNonNull(errorMessage, "errorMessage must not be null");
        this.successMessageOrNull = null;
        fireChanged("loading", "holdings", "errorMessageOrNull", "successMessageOrNull");
    }
}
