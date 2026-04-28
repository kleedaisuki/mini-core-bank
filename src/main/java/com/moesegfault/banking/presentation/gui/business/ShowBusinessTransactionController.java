package com.moesegfault.banking.presentation.gui.business;

import com.moesegfault.banking.application.business.query.FindBusinessTransactionHandler;
import com.moesegfault.banking.application.business.query.FindBusinessTransactionQuery;
import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 业务流水详情控制器（Show Business Transaction Controller），处理查询事件并调用应用层服务；
 *        Controller for business-transaction detail page handling query events and delegating to application service.
 */
public final class ShowBusinessTransactionController implements GuiController {

    /**
     * @brief 查询事件类型（Query Event Type）；
     *        View-event type for detail query action.
     */
    public static final String EVENT_QUERY = BusinessGuiEventTypes.SHOW_BUSINESS_TRANSACTION_QUERY;

    /**
     * @brief 单笔业务流水查询应用服务（Find Business Transaction Application Service）；
     *        Application service for loading one business transaction.
     */
    private final FindBusinessTransactionHandler findBusinessTransactionHandler;

    /**
     * @brief 页面模型（Page Model）；
     *        Page model.
     */
    private final ShowBusinessTransactionModel model;

    /**
     * @brief 构造详情控制器（Construct Detail Controller）；
     *        Construct detail controller.
     *
     * @param findBusinessTransactionHandler 单笔业务流水查询应用服务（Find-business-transaction application service）。
     * @param model 页面模型（Page model）。
     */
    public ShowBusinessTransactionController(
            final FindBusinessTransactionHandler findBusinessTransactionHandler,
            final ShowBusinessTransactionModel model
    ) {
        this.findBusinessTransactionHandler = Objects.requireNonNull(
                findBusinessTransactionHandler,
                "findBusinessTransactionHandler must not be null");
        this.model = Objects.requireNonNull(model, "model must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewEvent(final ViewEvent event) {
        final ViewEvent normalizedEvent = Objects.requireNonNull(event, "event must not be null");
        if (!EVENT_QUERY.equals(normalizedEvent.type())) {
            return;
        }
        queryTransaction(normalizedEvent.attributes());
    }

    /**
     * @brief 执行详情查询（Execute Detail Query）；
     *        Execute detail lookup with selector validation and model updates.
     *
     * @param attributes 事件参数（Event attributes）。
     */
    private void queryTransaction(final Map<String, Object> attributes) {
        final String transactionId = optionalAttribute(
                attributes,
                BusinessGuiSchema.TRANSACTION_ID,
                "transaction-id").orElse(null);
        final String referenceNo = optionalAttribute(
                attributes,
                BusinessGuiSchema.REFERENCE_NO,
                "reference-no",
                "reference").orElse(null);

        model.setSelectors(transactionId, referenceNo);
        model.setLoading(true);
        model.setErrorMessage(null);

        try {
            final FindBusinessTransactionQuery query = createQuery(transactionId, referenceNo);
            final Optional<BusinessTransactionResult> result = findBusinessTransactionHandler.handle(query);
            if (result.isPresent()) {
                model.setTransaction(result.orElseThrow());
                return;
            }

            model.setTransaction(null);
            model.setErrorMessage(buildNotFoundMessage(query));
        } finally {
            model.setLoading(false);
        }
    }

    /**
     * @brief 构造查询对象（Create Find Query）；
     *        Create find query while enforcing exactly-one selector rule.
     *
     * @param transactionId 交易 ID（Transaction ID, nullable）。
     * @param referenceNo 参考号（Reference number, nullable）。
     * @return 查询对象（Find query）。
     */
    private static FindBusinessTransactionQuery createQuery(final String transactionId, final String referenceNo) {
        final String normalizedTransactionId = normalizeNullableText(transactionId);
        final String normalizedReferenceNo = normalizeNullableText(referenceNo);
        final boolean hasTransactionId = normalizedTransactionId != null;
        final boolean hasReferenceNo = normalizedReferenceNo != null;
        if (hasTransactionId == hasReferenceNo) {
            throw new IllegalArgumentException("Exactly one selector is required: transaction_id or reference_no");
        }

        if (hasTransactionId) {
            return FindBusinessTransactionQuery.byTransactionId(normalizedTransactionId);
        }
        return FindBusinessTransactionQuery.byReferenceNo(normalizedReferenceNo);
    }

    /**
     * @brief 生成未命中错误文案（Build Not-Found Message）；
     *        Build not-found error message aligned with CLI schema wording.
     *
     * @param query 查询对象（Find query）。
     * @return 未命中文案（Not-found message）。
     */
    private static String buildNotFoundMessage(final FindBusinessTransactionQuery query) {
        if (query.hasTransactionId()) {
            return String.format(Locale.ROOT,
                    "business_transaction_not_found transaction_id=%s",
                    query.transactionIdOrNull());
        }
        return String.format(Locale.ROOT,
                "business_transaction_not_found reference_no=%s",
                query.referenceNoOrNull());
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
