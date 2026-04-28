package com.moesegfault.banking.presentation.gui.business;

import com.moesegfault.banking.application.business.query.ListBusinessTransactionsHandler;
import com.moesegfault.banking.application.business.query.ListBusinessTransactionsQuery;
import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.domain.business.BusinessTransactionStatus;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 业务流水列表控制器（List Business Transactions Controller），处理筛选查询与行选择事件；
 *        Controller for business-transaction list page handling filter query and row-selection events.
 */
public final class ListBusinessTransactionsController implements GuiController {

    /**
     * @brief 列表查询事件类型（List Query Event Type）；
     *        View-event type for list query action.
     */
    public static final String EVENT_QUERY = BusinessGuiEventTypes.LIST_BUSINESS_TRANSACTIONS_QUERY;

    /**
     * @brief 行选择事件类型（Row Select Event Type）；
     *        View-event type for table row selection.
     */
    public static final String EVENT_SELECT_ROW = BusinessGuiEventTypes.LIST_BUSINESS_TRANSACTIONS_ROW_SELECTED;

    /**
     * @brief 业务流水列表查询应用服务（List Business Transactions Application Service）；
     *        Application service for listing business transactions.
     */
    private final ListBusinessTransactionsHandler listBusinessTransactionsHandler;

    /**
     * @brief 页面模型（Page Model）；
     *        Page model.
     */
    private final ListBusinessTransactionsModel model;

    /**
     * @brief 构造列表控制器（Construct List Controller）；
     *        Construct list controller.
     *
     * @param listBusinessTransactionsHandler 业务流水列表查询应用服务（List-business-transactions application service）。
     * @param model 页面模型（Page model）。
     */
    public ListBusinessTransactionsController(
            final ListBusinessTransactionsHandler listBusinessTransactionsHandler,
            final ListBusinessTransactionsModel model
    ) {
        this.listBusinessTransactionsHandler = Objects.requireNonNull(
                listBusinessTransactionsHandler,
                "listBusinessTransactionsHandler must not be null");
        this.model = Objects.requireNonNull(model, "model must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        queryTransactions(Map.of("limit", Integer.toString(model.limit())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewEvent(final ViewEvent event) {
        final ViewEvent normalizedEvent = Objects.requireNonNull(event, "event must not be null");
        if (EVENT_QUERY.equals(normalizedEvent.type())) {
            queryTransactions(normalizedEvent.attributes());
            return;
        }
        if (EVENT_SELECT_ROW.equals(normalizedEvent.type())) {
            selectTransaction(normalizedEvent.attributes());
        }
    }

    /**
     * @brief 执行列表查询（Execute List Query）；
     *        Execute list query with optional filters.
     *
     * @param attributes 事件参数（Event attributes）。
     */
    private void queryTransactions(final Map<String, Object> attributes) {
        final String initiatorCustomerId = optionalAttribute(
                attributes,
                BusinessGuiSchema.INITIATOR_CUSTOMER_ID,
                "initiator-customer-id",
                "customer_id",
                "customer-id").orElse(null);

        final String statusRaw = optionalAttribute(
                attributes,
                BusinessGuiSchema.TRANSACTION_STATUS,
                "transaction-status",
                "status").orElse(null);

        final int limit = optionalAttribute(attributes, BusinessGuiSchema.LIMIT)
                .map(ListBusinessTransactionsController::parseLimit)
                .orElse(ListBusinessTransactionsQuery.DEFAULT_LIMIT);

        final BusinessTransactionStatus status = parseStatusOrNull(statusRaw);
        model.setFilters(initiatorCustomerId, statusRaw, limit);
        model.setLoading(true);
        model.setErrorMessage(null);

        try {
            final ListBusinessTransactionsQuery query = new ListBusinessTransactionsQuery(
                    initiatorCustomerId,
                    status,
                    limit);
            final List<BusinessTransactionResult> transactions = listBusinessTransactionsHandler.handle(query);
            model.setTransactions(transactions);
            model.setSelectedTransactionId(null);
        } finally {
            model.setLoading(false);
        }
    }

    /**
     * @brief 处理列表选中（Handle Row Selection）；
     *        Handle row-selection event and map to selected transaction id.
     *
     * @param attributes 事件参数（Event attributes）。
     */
    private void selectTransaction(final Map<String, Object> attributes) {
        final Optional<String> rowIndexOption = optionalAttribute(
                attributes,
                BusinessGuiSchema.ROW_INDEX,
                "rowIndex");
        if (rowIndexOption.isEmpty()) {
            return;
        }

        final int rowIndex = parseRowIndex(rowIndexOption.orElseThrow());
        final List<BusinessTransactionResult> transactions = model.transactions();
        if (rowIndex < 0 || rowIndex >= transactions.size()) {
            throw new IllegalArgumentException("Row index out of range: " + rowIndex);
        }

        model.setSelectedTransactionId(transactions.get(rowIndex).transactionId());
    }

    /**
     * @brief 解析可空状态值（Parse Nullable Status）；
     *        Parse nullable transaction-status value.
     *
     * @param rawStatus 原始状态（Raw status, nullable）。
     * @return 状态枚举（Status enum, nullable）。
     */
    private static BusinessTransactionStatus parseStatusOrNull(final String rawStatus) {
        final String normalized = normalizeNullableText(rawStatus);
        if (normalized == null) {
            return null;
        }
        return BusinessTransactionStatus.valueOf(normalized.toUpperCase(Locale.ROOT));
    }

    /**
     * @brief 解析返回上限（Parse Result Limit）；
     *        Parse result-limit text as positive integer.
     *
     * @param rawLimit 原始上限文本（Raw limit text）。
     * @return 返回上限（Result limit）。
     */
    private static int parseLimit(final String rawLimit) {
        try {
            final int parsed = Integer.parseInt(rawLimit.trim());
            if (parsed <= 0) {
                throw new IllegalArgumentException("limit must be > 0");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Unsupported limit value: " + rawLimit, exception);
        }
    }

    /**
     * @brief 解析行下标（Parse Row Index）；
     *        Parse row-index text as integer.
     *
     * @param rawRowIndex 原始下标文本（Raw row-index text）。
     * @return 行下标（Row index）。
     */
    private static int parseRowIndex(final String rawRowIndex) {
        try {
            return Integer.parseInt(rawRowIndex.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Unsupported row index value: " + rawRowIndex, exception);
        }
    }

    /**
     * @brief 读取可选事件参数（Read Optional Event Attribute）；
     *        Read optional event attribute with alias names.
     *
     * @param attributes 事件参数（Event attributes）。
     * @param names 参数别名（Attribute aliases）。
     * @return 参数值（Attribute value, optional）。
     */
    private static Optional<String> optionalAttribute(final Map<String, Object> attributes, final String... names) {
        for (String name : names) {
            final Object value = attributes.get(name);
            if (value != null) {
                return Optional.of(value.toString());
            }
        }
        return Optional.empty();
    }

    /**
     * @brief 标准化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text by trimming and collapsing blank to null.
     *
     * @param rawValue 原始文本（Raw text, nullable）。
     * @return 标准化文本（Normalized text or null）。
     */
    private static String normalizeNullableText(final String rawValue) {
        if (rawValue == null) {
            return null;
        }
        final String normalized = rawValue.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
