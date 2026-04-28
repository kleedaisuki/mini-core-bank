package com.moesegfault.banking.presentation.gui.customer;

import com.moesegfault.banking.application.customer.command.RegisterCustomerCommand;
import com.moesegfault.banking.application.customer.command.RegisterCustomerHandler;
import com.moesegfault.banking.application.customer.result.RegisterCustomerResult;
import com.moesegfault.banking.domain.customer.IdentityDocumentType;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 客户注册控制器（Register Customer Controller），处理提交事件并调用应用层注册服务；
 *        Register-customer controller handling submit events and delegating to application service.
 */
public final class RegisterCustomerController implements GuiController {

    /**
     * @brief 页面模型（Page Model）；
     *        Register-customer page model.
     */
    private final RegisterCustomerModel model;

    /**
     * @brief 注册服务（Register Service）；
     *        Register-customer application handler.
     */
    private final RegisterCustomerHandler registerCustomerHandler;

    /**
     * @brief 异常映射器（Exception Mapper）；
     *        GUI exception handler for user message mapping.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 构造客户注册控制器（Construct Register Customer Controller）；
     *        Construct register-customer controller.
     *
     * @param model 页面模型（Page model）。
     * @param registerCustomerHandler 注册服务（Register service）。
     * @param guiExceptionHandler 异常映射器（Exception mapper）。
     */
    public RegisterCustomerController(final RegisterCustomerModel model,
                                      final RegisterCustomerHandler registerCustomerHandler,
                                      final GuiExceptionHandler guiExceptionHandler) {
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.registerCustomerHandler = Objects.requireNonNull(
                registerCustomerHandler,
                "registerCustomerHandler must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
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
        if (!CustomerGuiEventTypes.REGISTER_CUSTOMER_SUBMIT.equals(normalizedEvent.type())) {
            return;
        }

        final Map<String, String> formValues = extractFormValues(normalizedEvent);
        handleSubmit(formValues);
    }

    /**
     * @brief 处理注册提交（Handle Register Submit）；
     *        Handle register-customer form submission.
     *
     * @param formValues 表单值（Form values）。
     */
    private void handleSubmit(final Map<String, String> formValues) {
        model.replaceFormValues(formValues);
        model.clearFieldErrors();
        model.setErrorMessage(null);
        model.setSuccessMessage(null);

        final Map<String, String> validationErrors = validateRequiredFields(formValues);
        if (!validationErrors.isEmpty()) {
            model.replaceFieldErrors(validationErrors);
            model.setErrorMessage("输入参数不合法: 请补全必填字段");
            return;
        }

        model.setSubmitting(true);
        try {
            final RegisterCustomerCommand command = createCommand(formValues);
            final RegisterCustomerResult result = registerCustomerHandler.handle(command);
            model.markRegisterSuccess(result);
            model.setSuccessMessage(String.format(
                    Locale.ROOT,
                    "customer_id=%s customer_status=%s registered_at=%s",
                    result.customerId(),
                    result.customerStatus(),
                    result.registeredAt()));
        } catch (FieldValidationException ex) {
            model.replaceFieldErrors(Map.of(ex.fieldName(), ex.getMessage()));
            model.setErrorMessage(guiExceptionHandler.toUserMessage(ex));
        } catch (RuntimeException ex) {
            model.setErrorMessage(guiExceptionHandler.toUserMessage(ex));
        } finally {
            model.setSubmitting(false);
        }
    }

    /**
     * @brief 提取表单值（Extract Form Values）；
     *        Extract form-value map from view event attributes.
     *
     * @param event 视图事件（View event）。
     * @return 表单值映射（Form-value map）。
     */
    @SuppressWarnings("unchecked")
    private static Map<String, String> extractFormValues(final ViewEvent event) {
        final Object rawFormValues = event.attributes().get(CustomerGuiEventTypes.ATTR_FORM_VALUES);
        if (rawFormValues == null) {
            return Map.of();
        }

        if (!(rawFormValues instanceof Map<?, ?> rawMap)) {
            throw new IllegalArgumentException("form_values must be a map");
        }

        final Map<String, String> normalizedValues = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            normalizedValues.put(String.valueOf(entry.getKey()), Objects.requireNonNullElse(
                    (String) entry.getValue(),
                    ""));
        }
        return Map.copyOf(normalizedValues);
    }

    /**
     * @brief 校验必填字段（Validate Required Fields）；
     *        Validate required register fields.
     *
     * @param formValues 表单值（Form values）。
     * @return 字段错误映射（Field-error map）。
     */
    private static Map<String, String> validateRequiredFields(final Map<String, String> formValues) {
        final Map<String, String> errors = new LinkedHashMap<>();
        for (String fieldName : RegisterCustomerModel.FIELD_ORDER) {
            if (RegisterCustomerModel.FIELD_CRS_INFO.equals(fieldName)) {
                continue;
            }
            final String value = Objects.requireNonNullElse(formValues.get(fieldName), "").trim();
            if (value.isEmpty()) {
                errors.put(fieldName, "必填字段不能为空");
            }
        }
        return Map.copyOf(errors);
    }

    /**
     * @brief 由表单值构建注册命令（Build Register Command from Form Values）；
     *        Build register-customer command from normalized form values.
     *
     * @param formValues 表单值（Form values）。
     * @return 注册命令（Register command）。
     */
    private static RegisterCustomerCommand createCommand(final Map<String, String> formValues) {
        final IdentityDocumentType idType = parseIdentityDocumentType(readField(
                formValues,
                RegisterCustomerModel.FIELD_ID_TYPE));
        final String idNumber = readField(formValues, RegisterCustomerModel.FIELD_ID_NUMBER);
        final String issuingRegion = readField(formValues, RegisterCustomerModel.FIELD_ISSUING_REGION);
        final String mobilePhone = readField(formValues, RegisterCustomerModel.FIELD_MOBILE_PHONE);
        final String residentialAddress = readField(formValues, RegisterCustomerModel.FIELD_RESIDENTIAL_ADDRESS);
        final String mailingAddress = readField(formValues, RegisterCustomerModel.FIELD_MAILING_ADDRESS);
        final boolean usTaxResident = parseBoolean(readField(formValues, RegisterCustomerModel.FIELD_IS_US_TAX_RESIDENT));
        final String crsInfoRawValue = Objects.requireNonNullElse(formValues.get(RegisterCustomerModel.FIELD_CRS_INFO), "");
        final String crsInfo = crsInfoRawValue.trim().isEmpty() ? null : crsInfoRawValue.trim();

        return new RegisterCustomerCommand(
                idType,
                idNumber,
                issuingRegion,
                mobilePhone,
                residentialAddress,
                mailingAddress,
                usTaxResident,
                crsInfo);
    }

    /**
     * @brief 读取并规整字段（Read and Normalize Field）；
     *        Read one field value and trim leading/trailing spaces.
     *
     * @param formValues 表单值（Form values）。
     * @param fieldName 字段名（Field name）。
     * @return 规整字段值（Normalized field value）。
     */
    private static String readField(final Map<String, String> formValues, final String fieldName) {
        final String rawValue = Objects.requireNonNullElse(formValues.get(fieldName), "");
        final String normalizedValue = rawValue.trim();
        if (normalizedValue.isEmpty()) {
            throw new FieldValidationException(fieldName, "必填字段不能为空");
        }
        return normalizedValue;
    }

    /**
     * @brief 解析证件类型（Parse Identity Document Type）；
     *        Parse identity-document type from form value.
     *
     * @param rawValue 原始值（Raw value）。
     * @return 证件类型（Identity document type）。
     */
    private static IdentityDocumentType parseIdentityDocumentType(final String rawValue) {
        try {
            return IdentityDocumentType.fromDatabaseValue(rawValue);
        } catch (IllegalArgumentException ex) {
            throw new FieldValidationException(RegisterCustomerModel.FIELD_ID_TYPE, ex.getMessage());
        }
    }

    /**
     * @brief 解析布尔值（Parse Boolean Value）；
     *        Parse boolean value from accepted textual variants.
     *
     * @param rawValue 原始值（Raw value）。
     * @return 布尔值（Boolean value）。
     */
    private static boolean parseBoolean(final String rawValue) {
        final String normalized = rawValue.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "true", "1", "yes", "y" -> true;
            case "false", "0", "no", "n" -> false;
            default -> throw new FieldValidationException(
                    RegisterCustomerModel.FIELD_IS_US_TAX_RESIDENT,
                    "布尔字段仅支持 true/false/1/0/yes/no/y/n");
        };
    }

    /**
     * @brief 字段级校验异常（Field-level Validation Exception）；
     *        Exception carrying one field name for field-level validation failure.
     */
    private static final class FieldValidationException extends IllegalArgumentException {

        /**
         * @brief 字段名（Field Name）；
         *        Field name associated with this validation error.
         */
        private final String fieldName;

        /**
         * @brief 构造字段校验异常（Construct Field Validation Exception）；
         *        Construct field-validation exception.
         *
         * @param fieldName 字段名（Field name）。
         * @param message 错误消息（Error message）。
         */
        private FieldValidationException(final String fieldName, final String message) {
            super(Objects.requireNonNullElse(message, "字段校验失败"));
            this.fieldName = Objects.requireNonNull(fieldName, "fieldName must not be null");
        }

        /**
         * @brief 返回字段名（Return Field Name）；
         *        Return field name.
         *
         * @return 字段名（Field name）。
         */
        private String fieldName() {
            return fieldName;
        }
    }
}
