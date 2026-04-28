package com.moesegfault.banking.presentation.gui.business;

import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeEvent;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeListener;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @brief 业务流水列表页面视图（List Business Transactions View），监听模型并维护列表 schema 快照；
 *        List-page view observing model changes and maintaining transaction-list schema snapshot.
 */
public final class ListBusinessTransactionsView implements GuiView<ListBusinessTransactionsModel>, ModelChangeListener {

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
    private ListBusinessTransactionsModel model;

    /**
     * @brief 是否已挂载（Mounted Flag）；
     *        Mounted lifecycle flag.
     */
    private boolean mounted;

    /**
     * @brief 最近渲染头部快照（Last Header Snapshot）；
     *        Last rendered header snapshot.
     */
    private Map<String, String> lastRenderedHeader = Map.of();

    /**
     * @brief 最近渲染行快照（Last Row Snapshot）；
     *        Last rendered row snapshot.
     */
    private List<Map<String, String>> lastRenderedRows = List.of();

    /**
     * @brief 构造列表页面视图（Construct List Page View）；
     *        Construct list page view.
     *
     */
    public ListBusinessTransactionsView() {
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
    public void bindModel(final ListBusinessTransactionsModel model) {
        final ListBusinessTransactionsModel normalizedModel = Objects.requireNonNull(model, "model must not be null");
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
            lastRenderedHeader = Map.of();
            lastRenderedRows = List.of();
            return;
        }
        lastRenderedHeader = buildHeaderSnapshot(model);
        lastRenderedRows = buildRowSnapshot(model.transactions());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onModelChanged(final ModelChangeEvent event) {
        render();
    }

    /**
     * @brief 提交列表查询（Submit List Query）；
     *        Submit list query event with filter fields.
     *
     * @param initiatorCustomerId 发起客户 ID（Initiator customer id, nullable）。
     * @param transactionStatus 交易状态（Transaction status, nullable）。
     * @param limit 返回上限（Result limit, nullable）。
     */
    public void submitQuery(
            final String initiatorCustomerId,
            final String transactionStatus,
            final Integer limit
    ) {
        final Map<String, Object> attributes = new LinkedHashMap<>();
        if (initiatorCustomerId != null) {
            attributes.put(BusinessGuiSchema.INITIATOR_CUSTOMER_ID, initiatorCustomerId);
        }
        if (transactionStatus != null) {
            attributes.put(BusinessGuiSchema.TRANSACTION_STATUS, transactionStatus);
        }
        if (limit != null) {
            attributes.put(BusinessGuiSchema.LIMIT, Integer.toString(limit));
        }
        eventHandler.accept(new ViewEvent(ListBusinessTransactionsController.EVENT_QUERY, attributes));
    }

    /**
     * @brief 提交行选择事件（Submit Row-Selection Event）；
     *        Submit row-selection event.
     *
     * @param rowIndex 行下标（Row index）。
     */
    public void selectRow(final int rowIndex) {
        eventHandler.accept(new ViewEvent(
                ListBusinessTransactionsController.EVENT_SELECT_ROW,
                Map.of(BusinessGuiSchema.ROW_INDEX, Integer.toString(rowIndex))));
    }

    /**
     * @brief 获取最近渲染头部快照（Get Last Header Snapshot）；
     *        Get last rendered header snapshot.
     *
     * @return 头部快照（Header snapshot）。
     */
    public Map<String, String> lastRenderedHeader() {
        return lastRenderedHeader;
    }

    /**
     * @brief 获取最近渲染行快照（Get Last Row Snapshot）；
     *        Get last rendered row snapshot.
     *
     * @return 行快照（Row snapshot）。
     */
    public List<Map<String, String>> lastRenderedRows() {
        return lastRenderedRows;
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
     * @brief 构造头部快照（Build Header Snapshot）；
     *        Build list header snapshot from model state.
     *
     * @param model 页面模型（Page model）。
     * @return 头部快照（Header snapshot）。
     */
    private static Map<String, String> buildHeaderSnapshot(final ListBusinessTransactionsModel model) {
        final Map<String, String> header = new LinkedHashMap<>();
        header.put("loading", Boolean.toString(model.isLoading()));
        header.put(BusinessGuiSchema.INITIATOR_CUSTOMER_ID, safeText(model.initiatorCustomerIdOrNull()));
        header.put(BusinessGuiSchema.TRANSACTION_STATUS, safeText(model.transactionStatusOrNull()));
        header.put(BusinessGuiSchema.LIMIT, Integer.toString(model.limit()));
        header.put("selected_transaction_id", safeText(model.selectedTransactionIdOrNull()));
        header.put("error_message", safeText(model.errorMessageOrNull()));
        header.put(BusinessGuiSchema.TOTAL, Integer.toString(model.transactions().size()));
        return Map.copyOf(header);
    }

    /**
     * @brief 构造行快照（Build Row Snapshot）；
     *        Build per-row schema snapshot list.
     *
     * @param transactions 业务流水列表（Business-transaction list）。
     * @return 行快照列表（Row snapshot list）。
     */
    private static List<Map<String, String>> buildRowSnapshot(final List<BusinessTransactionResult> transactions) {
        final List<Map<String, String>> rows = new ArrayList<>();
        for (BusinessTransactionResult transaction : transactions) {
            final Map<String, String> row = new LinkedHashMap<>();
            row.put(BusinessGuiSchema.TRANSACTION_ID, transaction.transactionId());
            row.put("business_type_code", transaction.businessTypeCode());
            row.put(BusinessGuiSchema.INITIATOR_CUSTOMER_ID, safeText(transaction.initiatorCustomerIdOrNull()));
            row.put("operator_id", safeText(transaction.operatorIdOrNull()));
            row.put("channel", transaction.channel().name());
            row.put(BusinessGuiSchema.TRANSACTION_STATUS, transaction.transactionStatus().name());
            row.put("requested_at", transaction.requestedAt().toString());
            row.put("completed_at", safeText(transaction.completedAtOrNull()));
            row.put(BusinessGuiSchema.REFERENCE_NO, transaction.referenceNo());
            row.put("remarks", safeText(transaction.remarksOrNull()));
            rows.add(Map.copyOf(row));
        }
        return List.copyOf(rows);
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
