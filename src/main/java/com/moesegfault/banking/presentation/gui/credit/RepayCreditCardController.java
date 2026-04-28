package com.moesegfault.banking.presentation.gui.credit;

import com.moesegfault.banking.application.credit.command.RepayCreditCardCommand;
import com.moesegfault.banking.application.credit.command.RepayCreditCardHandler;
import com.moesegfault.banking.application.credit.result.RepayCreditCardResult;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 信用卡还款控制器（Repay Credit Card Controller），处理还款提交并调用应用服务；
 *        Controller for repay-credit-card page handling submit event and application invocation.
 */
public final class RepayCreditCardController implements GuiController {

    /**
     * @brief 提交事件类型（Submit Event Type）；
     *        View-event type for credit-card repayment submit.
     */
    public static final String EVENT_SUBMIT = "credit.repay.submit";

    /**
     * @brief 信用卡还款应用服务（Repay Credit Card Application Service）；
     *        Application handler for credit-card repayment.
     */
    private final RepayCreditCardHandler applicationHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper from throwable to user-facing message.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 页面模型（Page Model）；
     *        Repay-credit-card page model.
     */
    private final RepayCreditCardModel model;

    /**
     * @brief 构造控制器（Construct Controller）；
     *        Construct repay-credit-card controller.
     *
     * @param applicationHandler 信用卡还款应用服务（Application handler）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     * @param model 页面模型（Page model）。
     */
    public RepayCreditCardController(
            final RepayCreditCardHandler applicationHandler,
            final GuiExceptionHandler guiExceptionHandler,
            final RepayCreditCardModel model
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
     *        Handle repayment submit payload.
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
                    RepayCreditCardModel.FIELD_CREDIT_CARD_ACCOUNT_ID,
                    "credit_card_account_id is required");
            final BigDecimal repaymentAmount = requiredDecimal(
                    attributes,
                    RepayCreditCardModel.FIELD_REPAYMENT_AMOUNT,
                    "repayment_amount must be decimal");
            final String repaymentCurrencyCode = requiredText(
                    attributes,
                    RepayCreditCardModel.FIELD_REPAYMENT_CURRENCY_CODE,
                    "repayment_currency_code is required");
            final String statementIdOrNull = optionalText(attributes, RepayCreditCardModel.FIELD_STATEMENT_ID);
            final String sourceAccountIdOrNull = optionalText(attributes, RepayCreditCardModel.FIELD_SOURCE_ACCOUNT_ID);
            final LocalDate asOfDateOrNull = optionalDate(
                    attributes,
                    RepayCreditCardModel.FIELD_AS_OF_DATE,
                    "as_of_date must be yyyy-MM-dd");

            if (!model.fieldErrors().isEmpty()) {
                return;
            }

            final RepayCreditCardResult result = applicationHandler.handle(new RepayCreditCardCommand(
                    creditCardAccountId,
                    repaymentAmount,
                    repaymentCurrencyCode,
                    statementIdOrNull,
                    sourceAccountIdOrNull,
                    asOfDateOrNull));
            model.setRepayResult(result);
            model.setUserMessageOrNull("repayment_applied credit_card_account_id=" + result.creditCardAccount().creditCardAccountId());
        } catch (RuntimeException exception) {
            model.setUserMessageOrNull(guiExceptionHandler.toUserMessage(exception));
        } finally {
            model.setSubmitting(false);
        }
    }

    /**
     * @brief 读取必填文本字段（Read Required Text Field）；
     *        Read one required text field from payload.
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
