package com.moesegfault.banking.presentation.gui.investment;

import com.moesegfault.banking.application.investment.result.InvestmentProductResult;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @brief 创建投资产品页面视图（Create Product Page View）；
 *        Toolkit-agnostic view adapter for create-product page with event emission helpers.
 */
public final class CreateProductView implements GuiView<CreateProductModel> {

    /**
     * @brief 绑定模型（Bound Model）；
     *        Bound page model.
     */
    private CreateProductModel model;

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
    public void bindModel(final CreateProductModel model) {
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
        final CreateProductModel boundModel = Objects.requireNonNull(model, "model must be bound before render");
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
     *        Build serializable snapshot from investment product result.
     *
     * @param result 产品结果（Product result, nullable）。
     * @return 结果快照（Result snapshot, nullable）。
     */
    private static Map<String, Object> toResultSnapshot(final InvestmentProductResult result) {
        if (result == null) {
            return null;
        }
        final Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("product_id", result.productId());
        snapshot.put("product_code", result.productCode());
        snapshot.put("product_name", result.productName());
        snapshot.put("product_type", result.productType());
        snapshot.put("currency_code", result.currencyCode());
        snapshot.put("risk_level", result.riskLevel());
        snapshot.put("issuer", result.issuer());
        snapshot.put("product_status", result.productStatus());
        return Collections.unmodifiableMap(snapshot);
    }
}
