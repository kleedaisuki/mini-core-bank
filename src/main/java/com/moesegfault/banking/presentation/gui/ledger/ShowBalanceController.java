package com.moesegfault.banking.presentation.gui.ledger;

import com.moesegfault.banking.application.ledger.query.FindBalanceHandler;
import com.moesegfault.banking.application.ledger.query.FindBalanceQuery;
import com.moesegfault.banking.application.ledger.result.BalanceResult;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 余额查询控制器（Show Balance Controller），处理提交事件并调用应用层余额查询服务；
 *        Balance-query controller handling submit events and invoking application-layer query service.
 */
public final class ShowBalanceController implements GuiController {

    /**
     * @brief 查询事件类型（Search Event Type）；
     *        View event type for submitting balance query.
     */
    public static final String EVENT_SEARCH_BALANCE = "ledger.balance.search";

    /**
     * @brief 账户编号字段名（Account Identifier Field Name）；
     *        Canonical schema field name for account identifier.
     */
    public static final String FIELD_ACCOUNT_ID = "account_id";

    /**
     * @brief 币种代码字段名（Currency Code Field Name）；
     *        Canonical schema field name for currency code.
     */
    public static final String FIELD_CURRENCY_CODE = "currency_code";

    /**
     * @brief 页面模型（Page Model）；
     *        Page model.
     */
    private final ShowBalanceModel model;

    /**
     * @brief 应用层余额查询服务（Application Balance Query Service）；
     *        Application-layer balance query handler.
     */
    private final FindBalanceHandler applicationHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        GUI exception-to-message mapper.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 构造余额查询控制器（Construct Show Balance Controller）；
     *        Construct balance-query controller.
     *
     * @param model 页面模型（Page model）。
     * @param applicationHandler 应用层余额查询服务（Application balance query handler）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public ShowBalanceController(final ShowBalanceModel model,
                                 final FindBalanceHandler applicationHandler,
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
        model.showHint("请输入 account_id 与 currency_code 后点击 Submit");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewEvent(final ViewEvent event) {
        final ViewEvent normalizedEvent = Objects.requireNonNull(event, "event must not be null");
        if (!EVENT_SEARCH_BALANCE.equals(normalizedEvent.type())) {
            return;
        }
        handleSearchEvent(normalizedEvent.attributes());
    }

    /**
     * @brief 处理余额查询事件（Handle Balance Search Event）；
     *        Handle balance-search event payload.
     *
     * @param attributes 事件属性（Event attributes）。
     */
    private void handleSearchEvent(final Map<String, Object> attributes) {
        model.setLoading(true);
        try {
            final String accountId = requiredTextAttribute(attributes, FIELD_ACCOUNT_ID);
            final String currencyCodeValue = requiredTextAttribute(attributes, FIELD_CURRENCY_CODE);

            model.updateQueryInputs(accountId, currencyCodeValue);

            final CurrencyCode currencyCode = CurrencyCode.of(currencyCodeValue);
            final Optional<BalanceResult> maybeResult =
                    applicationHandler.handle(new FindBalanceQuery(accountId, currencyCode));
            if (maybeResult.isPresent()) {
                model.showBalance(maybeResult.orElseThrow());
                return;
            }

            model.showHint("ledger_balance_not_found account_id=" + accountId + " currency_code=" + currencyCode.value());
        } catch (RuntimeException exception) {
            model.showError(guiExceptionHandler.toUserMessage(exception));
        } finally {
            model.setLoading(false);
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
}
