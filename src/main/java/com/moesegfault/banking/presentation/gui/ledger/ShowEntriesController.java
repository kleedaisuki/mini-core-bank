package com.moesegfault.banking.presentation.gui.ledger;

import com.moesegfault.banking.application.ledger.query.ListLedgerEntriesHandler;
import com.moesegfault.banking.application.ledger.query.ListLedgerEntriesQuery;
import com.moesegfault.banking.application.ledger.result.LedgerEntryResult;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 分录查询控制器（Show Entries Controller），处理提交事件并调用应用层分录查询服务；
 *        Ledger-entry query controller handling submit events and invoking application-layer query service.
 */
public final class ShowEntriesController implements GuiController {

    /**
     * @brief 查询事件类型（Search Event Type）；
     *        View event type for submitting ledger-entry query.
     */
    public static final String EVENT_SEARCH_ENTRIES = "ledger.entries.search";

    /**
     * @brief 账户编号字段名（Account Identifier Field Name）；
     *        Canonical schema field name for account identifier.
     */
    public static final String FIELD_ACCOUNT_ID = "account_id";

    /**
     * @brief 查询条数字段名（Limit Field Name）；
     *        Canonical schema field name for query limit.
     */
    public static final String FIELD_LIMIT = "limit";

    /**
     * @brief 页面模型（Page Model）；
     *        Page model.
     */
    private final ShowEntriesModel model;

    /**
     * @brief 应用层分录查询服务（Application Ledger-entry Query Service）；
     *        Application-layer ledger-entry query handler.
     */
    private final ListLedgerEntriesHandler applicationHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        GUI exception-to-message mapper.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 构造分录查询控制器（Construct Show Entries Controller）；
     *        Construct ledger-entry query controller.
     *
     * @param model 页面模型（Page model）。
     * @param applicationHandler 应用层分录查询服务（Application ledger-entry query handler）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public ShowEntriesController(final ShowEntriesModel model,
                                 final ListLedgerEntriesHandler applicationHandler,
                                 final GuiExceptionHandler guiExceptionHandler) {
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        model.updateQueryInputs("", Integer.toString(ListLedgerEntriesQuery.DEFAULT_LIMIT));
        model.showHint("请输入 account_id 与 limit 后点击 Submit");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewEvent(final ViewEvent event) {
        final ViewEvent normalizedEvent = Objects.requireNonNull(event, "event must not be null");
        if (!EVENT_SEARCH_ENTRIES.equals(normalizedEvent.type())) {
            return;
        }
        handleSearchEvent(normalizedEvent.attributes());
    }

    /**
     * @brief 处理分录查询事件（Handle Ledger-entry Search Event）；
     *        Handle ledger-entry search event payload.
     *
     * @param attributes 事件属性（Event attributes）。
     */
    private void handleSearchEvent(final Map<String, Object> attributes) {
        model.setLoading(true);
        try {
            final String accountId = requiredTextAttribute(attributes, FIELD_ACCOUNT_ID);
            final String limitText = optionalTextAttribute(attributes, FIELD_LIMIT);
            final int limit = parseLimit(limitText);

            model.updateQueryInputs(accountId, Integer.toString(limit));

            final List<LedgerEntryResult> results =
                    applicationHandler.handle(new ListLedgerEntriesQuery(accountId, limit));
            model.showEntries(results);
            if (results.isEmpty()) {
                model.showHint("total=0");
            }
        } catch (RuntimeException exception) {
            model.showError(guiExceptionHandler.toUserMessage(exception));
        } finally {
            model.setLoading(false);
        }
    }

    /**
     * @brief 解析查询条数（Parse Query Limit）；
     *        Parse limit text with default fallback.
     *
     * @param limitText 条数文本（Limit text）。
     * @return 查询条数（Parsed limit）。
     */
    private static int parseLimit(final String limitText) {
        if (limitText == null || limitText.isBlank()) {
            return ListLedgerEntriesQuery.DEFAULT_LIMIT;
        }

        try {
            return Integer.parseInt(limitText.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer field: " + FIELD_LIMIT + "=" + limitText, exception);
        }
    }

    /**
     * @brief 读取必填文本属性（Read Required Text Attribute）；
     *        Read one required text attribute from event payload.
     *
     * @param attributes 属性映射（Attribute map）。
     * @param fieldName 字段名（Field name）。
     * @return 规范化文本（Normalized text）。
     */
    private static String requiredTextAttribute(final Map<String, Object> attributes, final String fieldName) {
        final Map<String, Object> normalizedAttributes = Objects.requireNonNull(attributes, "attributes must not be null");
        final String normalizedFieldName = Objects.requireNonNull(fieldName, "fieldName must not be null");

        final Object rawValue = normalizedAttributes.get(normalizedFieldName);
        if (rawValue == null) {
            throw new IllegalArgumentException("Missing required field: " + normalizedFieldName);
        }

        final String normalizedValue = rawValue.toString().trim();
        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException("Field must not be blank: " + normalizedFieldName);
        }
        return normalizedValue;
    }

    /**
     * @brief 读取可选文本属性（Read Optional Text Attribute）；
     *        Read optional text attribute from event payload.
     *
     * @param attributes 属性映射（Attribute map）。
     * @param fieldName 字段名（Field name）。
     * @return 规范化文本或 null（Normalized text or null）。
     */
    private static String optionalTextAttribute(final Map<String, Object> attributes, final String fieldName) {
        final Map<String, Object> normalizedAttributes = Objects.requireNonNull(attributes, "attributes must not be null");
        final String normalizedFieldName = Objects.requireNonNull(fieldName, "fieldName must not be null");

        final Object rawValue = normalizedAttributes.get(normalizedFieldName);
        if (rawValue == null) {
            return null;
        }
        return rawValue.toString().trim();
    }
}
