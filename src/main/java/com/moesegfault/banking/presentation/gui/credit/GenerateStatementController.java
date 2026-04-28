package com.moesegfault.banking.presentation.gui.credit;

import com.moesegfault.banking.application.credit.command.GenerateStatementCommand;
import com.moesegfault.banking.application.credit.command.GenerateStatementHandler;
import com.moesegfault.banking.application.credit.result.CreditCardStatementResult;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 生成信用卡账单控制器（Generate Statement Controller），处理提交事件并调用应用服务；
 *        Controller for generate-statement page that handles submit event and invokes application service.
 */
public final class GenerateStatementController implements GuiController {

    /**
     * @brief 提交事件类型（Submit Event Type）；
     *        View-event type for statement generation submit.
     */
    public static final String EVENT_SUBMIT = "credit.generate-statement.submit";

    /**
     * @brief 生成账单应用服务（Generate Statement Application Service）；
     *        Application handler for statement generation.
     */
    private final GenerateStatementHandler applicationHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper from throwable to user-facing message.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 页面模型（Page Model）；
     *        Generate-statement page model.
     */
    private final GenerateStatementModel model;

    /**
     * @brief 构造控制器（Construct Controller）；
     *        Construct generate-statement controller.
     *
     * @param applicationHandler 生成账单应用服务（Application handler）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     * @param model 页面模型（Page model）。
     */
    public GenerateStatementController(
            final GenerateStatementHandler applicationHandler,
            final GuiExceptionHandler guiExceptionHandler,
            final GenerateStatementModel model
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
     *        Handle statement-generation submit payload.
     *
     * @param attributes 事件参数（Event attributes）。
     */
    private void handleSubmit(final Map<String, Object> attributes) {
        model.replaceFormValues(toStringMap(attributes));
        model.clearFieldErrors();
        model.setUserMessageOrNull(null);
        model.setSubmitting(true);
        try {
            final String creditCardAccountId = requiredText(
                    attributes,
                    GenerateStatementModel.FIELD_CREDIT_CARD_ACCOUNT_ID,
                    "credit_card_account_id is required");
            final LocalDate statementDate = requiredDate(
                    attributes,
                    GenerateStatementModel.FIELD_STATEMENT_DATE,
                    "statement_date must be yyyy-MM-dd");
            final BigDecimal minimumPaymentRateDecimal = requiredDecimal(
                    attributes,
                    GenerateStatementModel.FIELD_MINIMUM_PAYMENT_RATE_DECIMAL,
                    "minimum_payment_rate_decimal must be decimal");
            final BigDecimal minimumPaymentFloorAmount = requiredDecimal(
                    attributes,
                    GenerateStatementModel.FIELD_MINIMUM_PAYMENT_FLOOR_AMOUNT,
                    "minimum_payment_floor_amount must be decimal");

            if (!model.fieldErrors().isEmpty()) {
                return;
            }

            final CreditCardStatementResult result = applicationHandler.handle(new GenerateStatementCommand(
                    creditCardAccountId,
                    statementDate,
                    minimumPaymentRateDecimal,
                    minimumPaymentFloorAmount));
            model.setStatementResult(result);
            model.setUserMessageOrNull("statement_generated statement_id=" + result.statementId());
        } catch (RuntimeException exception) {
            model.setUserMessageOrNull(guiExceptionHandler.toUserMessage(exception));
        } finally {
            model.setSubmitting(false);
        }
    }

    /**
     * @brief 读取必填文本字段（Read Required Text Field）；
     *        Read one required text field from event payload.
     *
     * @param attributes 事件参数（Event attributes）。
     * @param fieldName 字段名（Field name）。
     * @param errorMessage 错误消息（Error message）。
     * @return 规范化文本（Normalized text）。
     */
    private String requiredText(final Map<String, Object> attributes, final String fieldName, final String errorMessage) {
        final String raw = toTrimmedTextOrEmpty(attributes.get(fieldName));
        if (raw.isEmpty()) {
            model.setFieldError(fieldName, errorMessage);
        }
        return raw;
    }

    /**
     * @brief 读取必填日期字段（Read Required Date Field）；
     *        Read one required date field in ISO format.
     *
     * @param attributes 事件参数（Event attributes）。
     * @param fieldName 字段名（Field name）。
     * @param errorMessage 错误消息（Error message）。
     * @return 日期对象（Date value）。
     */
    private LocalDate requiredDate(final Map<String, Object> attributes, final String fieldName, final String errorMessage) {
        final String raw = requiredText(attributes, fieldName, errorMessage);
        if (raw.isEmpty()) {
            return LocalDate.MIN;
        }
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException exception) {
            model.setFieldError(fieldName, errorMessage);
            return LocalDate.MIN;
        }
    }

    /**
     * @brief 读取必填金额字段（Read Required Decimal Field）；
     *        Read one required decimal field.
     *
     * @param attributes 事件参数（Event attributes）。
     * @param fieldName 字段名（Field name）。
     * @param errorMessage 错误消息（Error message）。
     * @return 十进制金额（Decimal value）。
     */
    private BigDecimal requiredDecimal(
            final Map<String, Object> attributes,
            final String fieldName,
            final String errorMessage
    ) {
        final String raw = requiredText(attributes, fieldName, errorMessage);
        if (raw.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(raw);
        } catch (NumberFormatException exception) {
            model.setFieldError(fieldName, errorMessage);
            return BigDecimal.ZERO;
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
        final String text = value.toString().trim();
        return text;
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
