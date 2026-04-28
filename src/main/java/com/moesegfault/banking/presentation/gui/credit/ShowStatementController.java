package com.moesegfault.banking.presentation.gui.credit;

import com.moesegfault.banking.application.credit.query.FindStatementHandler;
import com.moesegfault.banking.application.credit.query.FindStatementQuery;
import com.moesegfault.banking.application.credit.result.CreditCardStatementResult;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 信用卡账单查询控制器（Show Statement Controller），处理查询事件并协调两种查询模式；
 *        Controller for show-statement page handling query event with two exclusive query modes.
 */
public final class ShowStatementController implements GuiController {

    /**
     * @brief 提交事件类型（Submit Event Type）；
     *        View-event type for statement query submit.
     */
    public static final String EVENT_SUBMIT = "credit.statement.submit";

    /**
     * @brief 账单查询应用服务（Find Statement Application Service）；
     *        Application handler for statement query.
     */
    private final FindStatementHandler applicationHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper from throwable to user-facing message.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 页面模型（Page Model）；
     *        Show-statement page model.
     */
    private final ShowStatementModel model;

    /**
     * @brief 构造控制器（Construct Controller）；
     *        Construct show-statement controller.
     *
     * @param applicationHandler 账单查询应用服务（Application handler）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     * @param model 页面模型（Page model）。
     */
    public ShowStatementController(
            final FindStatementHandler applicationHandler,
            final GuiExceptionHandler guiExceptionHandler,
            final ShowStatementModel model
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
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
        if (!EVENT_SUBMIT.equals(normalizedEvent.type())) {
            return;
        }
        handleSubmit(normalizedEvent.attributes());
    }

    /**
     * @brief 处理提交事件（Handle Submit Event）；
     *        Handle statement-query submit payload.
     *
     * @param attributes 事件参数（Event attributes）。
     */
    private void handleSubmit(final Map<String, Object> attributes) {
        model.replaceFormValues(toStringMap(attributes));
        model.clearFieldErrors();
        model.setUserMessageOrNull(null);
        model.setNotFound(false);
        model.clearStatementResult();
        model.setLoading(true);
        try {
            final String statementId = optionalText(attributes, ShowStatementModel.FIELD_STATEMENT_ID);
            final String creditCardAccountId = optionalText(attributes, ShowStatementModel.FIELD_CREDIT_CARD_ACCOUNT_ID);
            final LocalDate statementPeriodStart = optionalDate(
                    attributes,
                    ShowStatementModel.FIELD_STATEMENT_PERIOD_START,
                    "statement_period_start must be yyyy-MM-dd");
            final LocalDate statementPeriodEnd = optionalDate(
                    attributes,
                    ShowStatementModel.FIELD_STATEMENT_PERIOD_END,
                    "statement_period_end must be yyyy-MM-dd");

            final boolean hasStatementId = statementId != null;
            final boolean hasAnyPeriodField = creditCardAccountId != null
                    || statementPeriodStart != null
                    || statementPeriodEnd != null;
            final boolean hasFullPeriodTuple = creditCardAccountId != null
                    && statementPeriodStart != null
                    && statementPeriodEnd != null;

            if (hasStatementId && hasAnyPeriodField) {
                model.setFieldError(ShowStatementModel.FIELD_STATEMENT_ID, "statement_id mode cannot mix period fields");
                model.setUserMessageOrNull("Exactly one mode is allowed: statement_id OR account+period");
                return;
            }
            if (!hasStatementId && !hasFullPeriodTuple) {
                if (creditCardAccountId == null) {
                    model.setFieldError(ShowStatementModel.FIELD_CREDIT_CARD_ACCOUNT_ID, "credit_card_account_id is required");
                }
                if (statementPeriodStart == null) {
                    model.setFieldError(ShowStatementModel.FIELD_STATEMENT_PERIOD_START, "statement_period_start is required");
                }
                if (statementPeriodEnd == null) {
                    model.setFieldError(ShowStatementModel.FIELD_STATEMENT_PERIOD_END, "statement_period_end is required");
                }
                model.setUserMessageOrNull("Exactly one mode is allowed: statement_id OR account+period");
                return;
            }
            if (!model.fieldErrors().isEmpty()) {
                return;
            }

            final Optional<CreditCardStatementResult> result = hasStatementId
                    ? applicationHandler.handle(FindStatementQuery.byStatementId(statementId))
                    : applicationHandler.handle(FindStatementQuery.byPeriod(
                            creditCardAccountId,
                            statementPeriodStart,
                            statementPeriodEnd));
            if (result.isEmpty()) {
                model.setNotFound(true);
                model.setUserMessageOrNull("statement_not_found");
                return;
            }

            final CreditCardStatementResult statementResult = result.orElseThrow();
            model.setStatementResult(statementResult);
            model.setUserMessageOrNull("statement_loaded statement_id=" + statementResult.statementId());
        } catch (RuntimeException exception) {
            model.setUserMessageOrNull(guiExceptionHandler.toUserMessage(exception));
        } finally {
            model.setLoading(false);
        }
    }

    /**
     * @brief 读取可选文本字段（Read Optional Text Field）；
     *        Read one optional text field from payload.
     *
     * @param attributes 事件参数（Event attributes）。
     * @param fieldName 字段名（Field name）。
     * @return 规范化文本或 null（Normalized text or null）。
     */
    private String optionalText(final Map<String, Object> attributes, final String fieldName) {
        final String raw = toTrimmedTextOrEmpty(attributes.get(fieldName));
        return raw.isEmpty() ? null : raw;
    }

    /**
     * @brief 读取可选日期字段（Read Optional Date Field）；
     *        Read one optional date field in ISO format.
     *
     * @param attributes 事件参数（Event attributes）。
     * @param fieldName 字段名（Field name）。
     * @param errorMessage 错误消息（Error message）。
     * @return 日期对象或 null（Date value or null）。
     */
    private LocalDate optionalDate(
            final Map<String, Object> attributes,
            final String fieldName,
            final String errorMessage
    ) {
        final String raw = optionalText(attributes, fieldName);
        if (raw == null) {
            return null;
        }
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException exception) {
            model.setFieldError(fieldName, errorMessage);
            return null;
        }
    }

    /**
     * @brief 转换为裁剪文本（Convert To Trimmed Text）；
     *        Convert payload object to trimmed text or empty string.
     *
     * @param value 原始值（Raw value）。
     * @return 裁剪文本（Trimmed text）。
     */
    private static String toTrimmedTextOrEmpty(final Object value) {
        if (value == null) {
            return "";
        }
        return value.toString().trim();
    }

    /**
     * @brief 转换为字符串映射（Convert To String Map）；
     *        Convert object payload map to trimmed string map.
     *
     * @param attributes 事件参数（Event attributes）。
     * @return 字符串映射（String map）。
     */
    private static Map<String, String> toStringMap(final Map<String, Object> attributes) {
        final Map<String, String> mappedValues = new java.util.LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            mappedValues.put(entry.getKey(), toTrimmedTextOrEmpty(entry.getValue()));
        }
        return mappedValues;
    }
}
