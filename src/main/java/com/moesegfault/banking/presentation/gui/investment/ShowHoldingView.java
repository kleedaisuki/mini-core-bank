package com.moesegfault.banking.presentation.gui.investment;

import com.moesegfault.banking.application.investment.result.HoldingResult;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @brief 投资持仓页面视图（Show Holding Page View）；
 *        Toolkit-agnostic view adapter for holdings page with query event helper.
 */
public final class ShowHoldingView implements GuiView<ShowHoldingModel> {

    /**
     * @brief 绑定模型（Bound Model）；
     *        Bound page model.
     */
    private ShowHoldingModel model;

    /**
     * @brief 事件监听器（View Event Listener）；
     *        Event listener receiving view events.
     */
    private Consumer<ViewEvent> viewEventListener = event -> {
    };

    /**
     * @brief 挂载标记（Mounted Flag）；
     *        Whether view is mounted.
     */
    private boolean mounted;

    /**
     * @brief 渲染快照（Rendered Snapshot）；
     *        Last rendered state snapshot for toolkit adapter/testing.
     */
    private Map<String, Object> renderedSnapshot = Map.of();

    /**
     * @brief 设置事件监听器（Set View Event Listener）；
     *        Set callback used to forward view events to controller.
     *
     * @param listener 事件监听器（Event listener）。
     */
    public void setViewEventListener(final Consumer<ViewEvent> listener) {
        this.viewEventListener = Objects.requireNonNull(listener, "listener must not be null");
    }

    /**
     * @brief 触发持仓查询（Emit Holdings Query）；
     *        Emit query event with provided payload.
     *
     * @param attributes 查询字段（Query payload）。
     */
    public void query(final Map<String, Object> attributes) {
        viewEventListener.accept(new ViewEvent(InvestmentGuiSchema.EVENT_QUERY, attributes));
    }

    /**
     * @brief 读取渲染快照（Read Rendered Snapshot）；
     *        Read immutable copy of last rendered view snapshot.
     *
     * @return 渲染快照（Rendered snapshot）。
     */
    public Map<String, Object> renderedSnapshot() {
        return Map.copyOf(renderedSnapshot);
    }

    /**
     * @brief 读取挂载标记（Read Mounted Flag）；
     *        Read whether view is mounted.
     *
     * @return 挂载返回 true（true when mounted）。
     */
    public boolean mounted() {
        return mounted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindModel(final ShowHoldingModel model) {
        this.model = Objects.requireNonNull(model, "model must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mount() {
        this.mounted = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unmount() {
        this.mounted = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        final ShowHoldingModel boundModel = Objects.requireNonNull(model, "model must be bound before render");
        final Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put(InvestmentGuiSchema.FIELD_INVESTMENT_ACCOUNT_ID, boundModel.investmentAccountId());
        snapshot.put(InvestmentGuiSchema.FIELD_INCLUDE_PRODUCT_DETAILS, boundModel.includeProductDetails());
        snapshot.put("loading", boundModel.loading());
        snapshot.put("error_message", boundModel.errorMessageOrNull());
        snapshot.put("success_message", boundModel.successMessageOrNull());
        snapshot.put("holdings", toHoldingSnapshots(boundModel.holdings()));
        renderedSnapshot = Collections.unmodifiableMap(snapshot);
    }

    /**
     * @brief 转换持仓快照列表（Convert Holding Snapshot List）；
     *        Convert holding results to serializable snapshot list.
     *
     * @param holdings 持仓结果（Holding results）。
     * @return 快照列表（Snapshot list）。
     */
    private static List<Map<String, Object>> toHoldingSnapshots(final List<HoldingResult> holdings) {
        return Objects.requireNonNull(holdings, "holdings must not be null")
                .stream()
                .map(ShowHoldingView::toHoldingSnapshot)
                .toList();
    }

    /**
     * @brief 转换单条持仓快照（Convert One Holding Snapshot）；
     *        Convert one holding result into serializable snapshot.
     *
     * @param holding 持仓结果（Holding result）。
     * @return 快照映射（Snapshot map）。
     */
    private static Map<String, Object> toHoldingSnapshot(final HoldingResult holding) {
        final Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("holding_id", holding.holdingId());
        snapshot.put("investment_account_id", holding.investmentAccountId());
        snapshot.put("product_id", holding.productId());
        snapshot.put("product_code", holding.productCodeOrNull());
        snapshot.put("product_name", holding.productNameOrNull());
        snapshot.put("product_type", holding.productTypeOrNull());
        snapshot.put("quantity", holding.quantity());
        snapshot.put("average_cost", holding.averageCost());
        snapshot.put("cost_currency_code", holding.costCurrencyCode());
        snapshot.put("market_value", holding.marketValue());
        snapshot.put("valuation_currency_code", holding.valuationCurrencyCode());
        snapshot.put("unrealized_pnl", holding.unrealizedPnl());
        snapshot.put("updated_at", holding.updatedAt());
        return Collections.unmodifiableMap(snapshot);
    }
}
