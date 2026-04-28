package com.moesegfault.banking.presentation.gui.business;

import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeEvent;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeListener;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @brief 业务流水详情页面视图（Show Business Transaction View），监听模型并维护可渲染 schema 快照；
 *        Detail-page view observing model changes and maintaining renderable schema snapshot.
 */
public final class ShowBusinessTransactionView implements GuiView<ShowBusinessTransactionModel>, ModelChangeListener {

    /**
     * @brief 事件控制器（Event Controller）；
     *        Controller receiving user events.
     */
    private Consumer<ViewEvent> eventHandler = ignored -> {
    };

    /**
     * @brief 绑定模型（Bound Model）；
     *        Bound model instance.
     */
    private ShowBusinessTransactionModel model;

    /**
     * @brief 是否已挂载（Mounted Flag）；
     *        Mounted lifecycle flag.
     */
    private boolean mounted;

    /**
     * @brief 最近渲染快照（Last Render Snapshot）；
     *        Last rendered schema snapshot.
     */
    private Map<String, String> lastRenderedSnapshot = Map.of();

    /**
     * @brief 构造详情页面视图（Construct Detail Page View）；
     *        Construct detail page view.
     *
     */
    public ShowBusinessTransactionView() {
    }

    /**
     * @brief 绑定事件处理器（Bind Event Handler）；
     *        Bind view-event handler for controller callback.
     *
     * @param eventHandler 事件处理器（Event handler）。
     */
    public void bindEventHandler(final Consumer<ViewEvent> eventHandler) {
        this.eventHandler = Objects.requireNonNull(eventHandler, "eventHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindModel(final ShowBusinessTransactionModel model) {
        final ShowBusinessTransactionModel normalizedModel = Objects.requireNonNull(model, "model must not be null");
        if (this.model != null) {
            this.model.removeChangeListener(this);
        }
        this.model = normalizedModel;
        this.model.addChangeListener(this);
        render();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mount() {
        mounted = true;
        render();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unmount() {
        mounted = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        if (model == null) {
            lastRenderedSnapshot = Map.of();
            return;
        }
        lastRenderedSnapshot = Map.copyOf(buildSnapshot(model));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onModelChanged(final ModelChangeEvent event) {
        render();
    }

    /**
     * @brief 触发按交易 ID 查询（Submit Query By Transaction ID）；
     *        Submit query event using transaction-id selector.
     *
     * @param transactionId 交易 ID（Transaction ID）。
     */
    public void submitByTransactionId(final String transactionId) {
        eventHandler.accept(new ViewEvent(
                ShowBusinessTransactionController.EVENT_QUERY,
                Map.of(BusinessGuiSchema.TRANSACTION_ID, Objects.requireNonNull(transactionId, "transactionId must not be null"))));
    }

    /**
     * @brief 触发按参考号查询（Submit Query By Reference Number）；
     *        Submit query event using reference-number selector.
     *
     * @param referenceNo 参考号（Reference number）。
     */
    public void submitByReferenceNo(final String referenceNo) {
        eventHandler.accept(new ViewEvent(
                ShowBusinessTransactionController.EVENT_QUERY,
                Map.of(BusinessGuiSchema.REFERENCE_NO, Objects.requireNonNull(referenceNo, "referenceNo must not be null"))));
    }

    /**
     * @brief 获取最近渲染快照（Get Last Render Snapshot）；
     *        Get last rendered schema snapshot.
     *
     * @return 渲染快照（Render snapshot）。
     */
    public Map<String, String> lastRenderedSnapshot() {
        return lastRenderedSnapshot;
    }

    /**
     * @brief 判断挂载状态（Check Mounted State）；
     *        Check whether view is mounted.
     *
     * @return 已挂载返回 true（true when mounted）。
     */
    public boolean isMounted() {
        return mounted;
    }

    /**
     * @brief 构造渲染快照（Build Render Snapshot）；
     *        Build key-value schema snapshot from current model state.
     *
     * @param model 页面模型（Page model）。
     * @return 快照字典（Snapshot map）。
     */
    private static Map<String, String> buildSnapshot(final ShowBusinessTransactionModel model) {
        final Map<String, String> snapshot = new LinkedHashMap<>();
        snapshot.put("loading", Boolean.toString(model.isLoading()));
        snapshot.put(BusinessGuiSchema.TRANSACTION_ID + "_selector", safeText(model.transactionIdOrNull()));
        snapshot.put(BusinessGuiSchema.REFERENCE_NO + "_selector", safeText(model.referenceNoOrNull()));
        snapshot.put("error_message", safeText(model.errorMessageOrNull()));

        final BusinessTransactionResult transaction = model.transactionOrNull();
        if (transaction == null) {
            return snapshot;
        }

        snapshot.put(BusinessGuiSchema.TRANSACTION_ID, transaction.transactionId());
        snapshot.put("business_type_code", transaction.businessTypeCode());
        snapshot.put(BusinessGuiSchema.INITIATOR_CUSTOMER_ID, safeText(transaction.initiatorCustomerIdOrNull()));
        snapshot.put("operator_id", safeText(transaction.operatorIdOrNull()));
        snapshot.put("channel", transaction.channel().name());
        snapshot.put(BusinessGuiSchema.TRANSACTION_STATUS, transaction.transactionStatus().name());
        snapshot.put("requested_at", transaction.requestedAt().toString());
        snapshot.put("completed_at", safeText(transaction.completedAtOrNull()));
        snapshot.put(BusinessGuiSchema.REFERENCE_NO, transaction.referenceNo());
        snapshot.put("remarks", safeText(transaction.remarksOrNull()));
        return snapshot;
    }

    /**
     * @brief 安全文本转换（Safe Text Conversion）；
     *        Convert nullable value to schema-friendly text.
     *
     * @param value 原始值（Raw value, nullable）。
     * @return 文本值（Text value）。
     */
    private static String safeText(final Object value) {
        return value == null ? "null" : value.toString();
    }
}
