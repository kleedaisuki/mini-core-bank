package com.moesegfault.banking.presentation.gui.customer;

import com.moesegfault.banking.application.customer.query.FindCustomerHandler;
import com.moesegfault.banking.application.customer.query.FindCustomerQuery;
import com.moesegfault.banking.application.customer.result.CustomerResult;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 客户详情控制器（Show Customer Controller），处理 customer_id 查询并更新上下文与模型；
 *        Show-customer controller handling customer-id query and updating context/model.
 */
public final class ShowCustomerController implements GuiController {

    /**
     * @brief 页面模型（Page Model）；
     *        Show-customer page model.
     */
    private final ShowCustomerModel model;

    /**
     * @brief 查询服务（Find Service）；
     *        Find-customer application handler.
     */
    private final FindCustomerHandler findCustomerHandler;

    /**
     * @brief 会话上下文（Session Context）；
     *        GUI session context.
     */
    private final GuiContext context;

    /**
     * @brief 异常处理器（Exception Handler）；
     *        GUI exception handler.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 构造客户详情控制器（Construct Show Customer Controller）；
     *        Construct show-customer controller.
     *
     * @param model 页面模型（Page model）。
     * @param findCustomerHandler 查询服务（Find service）。
     * @param context 会话上下文（Session context）。
     * @param guiExceptionHandler 异常处理器（Exception handler）。
     */
    public ShowCustomerController(final ShowCustomerModel model,
                                  final FindCustomerHandler findCustomerHandler,
                                  final GuiContext context,
                                  final GuiExceptionHandler guiExceptionHandler) {
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.findCustomerHandler = Objects.requireNonNull(findCustomerHandler, "findCustomerHandler must not be null");
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        context.currentCustomerId().ifPresent(model::setCustomerIdInput);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewEvent(final ViewEvent event) {
        final ViewEvent normalizedEvent = Objects.requireNonNull(event, "event must not be null");
        if (!CustomerGuiEventTypes.SHOW_CUSTOMER_SUBMIT.equals(normalizedEvent.type())) {
            return;
        }

        final String customerId = extractCustomerId(normalizedEvent);
        if (customerId.isBlank()) {
            model.setErrorMessage("输入参数不合法: customer_id 不能为空");
            model.clearCustomer();
            return;
        }

        queryCustomer(customerId);
    }

    /**
     * @brief 查询客户详情（Query Customer Details）；
     *        Query customer details by customer ID.
     *
     * @param customerId 客户 ID（Customer ID）。
     */
    private void queryCustomer(final String customerId) {
        model.setCustomerIdInput(customerId);
        model.setErrorMessage(null);
        model.setLoading(true);
        try {
            final Optional<CustomerResult> customerResult = findCustomerHandler.handle(new FindCustomerQuery(customerId));
            if (customerResult.isEmpty()) {
                model.markCustomerNotFound();
                context.clearCurrentCustomerId();
                return;
            }

            final CustomerResult customer = customerResult.orElseThrow();
            model.setCustomer(customer);
            context.setCurrentCustomerId(customer.customerId());
        } catch (RuntimeException ex) {
            model.setErrorMessage(guiExceptionHandler.toUserMessage(ex));
            model.clearCustomer();
        } finally {
            model.setLoading(false);
        }
    }

    /**
     * @brief 提取 customer_id（Extract Customer ID）；
     *        Extract customer-id input from view event.
     *
     * @param event 视图事件（View event）。
     * @return customer_id 文本（Customer-id text）。
     */
    @SuppressWarnings("unchecked")
    private static String extractCustomerId(final ViewEvent event) {
        final Object rawFormValues = event.attributes().get(CustomerGuiEventTypes.ATTR_FORM_VALUES);
        if (rawFormValues instanceof Map<?, ?> rawMap) {
            final Object rawCustomerId = rawMap.get(ShowCustomerModel.FIELD_CUSTOMER_ID);
            return rawCustomerId == null ? "" : String.valueOf(rawCustomerId).trim();
        }
        return "";
    }
}
