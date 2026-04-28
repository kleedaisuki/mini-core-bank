package com.moesegfault.banking.presentation.gui.investment;

import com.moesegfault.banking.application.investment.result.InvestmentOrderResult;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @brief 买入投资产品页面视图（Buy Product Page View）；
 *        Toolkit-agnostic view adapter for buy-product page with event emission helpers.
 */
public final class BuyProductView implements GuiView<BuyProductModel> {

    /**
     * @brief 绑定模型（Bound Model）；
     *        Bound page model.
     */
    private BuyProductModel model;

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
     * @brief 触发表单提交（Emit Form Submit）；
     *        Emit submit event with provided payload.
     *
     * @param attributes 表单字段（Form field payload）。
     */
    public void submit(final Map<String, Object> attributes) {
        viewEventListener.accept(new ViewEvent(InvestmentGuiSchema.EVENT_SUBMIT, attributes));
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
    public void bindModel(final BuyProductModel model) {
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
        final BuyProductModel boundModel = Objects.requireNonNull(model, "model must be bound before render");
        final Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("form_values", boundModel.formValues());
        snapshot.put("submitting", boundModel.submitting());
        snapshot.put("error_message", boundModel.errorMessageOrNull());
        snapshot.put("success_message", boundModel.successMessageOrNull());
        snapshot.put("result", toResultSnapshot(boundModel.latestResultOrNull()));
        renderedSnapshot = Collections.unmodifiableMap(snapshot);
    }

    /**
     * @brief 构造结果快照（Build Result Snapshot）；
     *        Build serializable snapshot from investment order result.
     *
     * @param result 订单结果（Order result, nullable）。
     * @return 结果快照（Result snapshot, nullable）。
     */
    private static Map<String, Object> toResultSnapshot(final InvestmentOrderResult result) {
        if (result == null) {
            return null;
        }
        final Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("order_id", result.orderId());
        snapshot.put("reference_no", result.referenceNo());
        snapshot.put("transaction_status", result.transactionStatus());
        snapshot.put("investment_account_id", result.investmentAccountId());
        snapshot.put("product_id", result.productId());
        snapshot.put("product_code", result.productCode());
        snapshot.put("order_side", result.orderSide());
        snapshot.put("quantity", result.quantity());
        snapshot.put("price", result.price());
        snapshot.put("gross_amount", result.grossAmount());
        snapshot.put("fee_amount", result.feeAmount());
        snapshot.put("currency_code", result.currencyCode());
        snapshot.put("order_status", result.orderStatus());
        snapshot.put("trade_at", result.tradeAt());
        snapshot.put("settlement_at", result.settlementAtOrNull());
        snapshot.put("cash_impact", result.cashImpact());
        snapshot.put("holding_quantity_after", result.holdingQuantityAfter());
        return Collections.unmodifiableMap(snapshot);
    }
}
