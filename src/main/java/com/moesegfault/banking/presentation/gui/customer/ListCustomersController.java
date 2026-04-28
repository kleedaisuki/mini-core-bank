package com.moesegfault.banking.presentation.gui.customer;

import com.moesegfault.banking.application.customer.query.ListCustomersHandler;
import com.moesegfault.banking.application.customer.query.ListCustomersQuery;
import com.moesegfault.banking.application.customer.result.CustomerResult;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 客户列表控制器（List Customers Controller），处理筛选刷新和行选择事件；
 *        Customer-list controller handling filter refresh and row-selection events.
 */
public final class ListCustomersController implements GuiController {

    /**
     * @brief 页面模型（Page Model）；
     *        Customer-list model.
     */
    private final ListCustomersModel model;

    /**
     * @brief 查询服务（Query Service）；
     *        List-customers application handler.
     */
    private final ListCustomersHandler listCustomersHandler;

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
     * @brief 构造客户列表控制器（Construct List Customers Controller）；
     *        Construct customer-list controller.
     *
     * @param model 页面模型（Page model）。
     * @param listCustomersHandler 查询服务（Query service）。
     * @param context 会话上下文（Session context）。
     * @param guiExceptionHandler 异常处理器（Exception handler）。
     */
    public ListCustomersController(final ListCustomersModel model,
                                   final ListCustomersHandler listCustomersHandler,
                                   final GuiContext context,
                                   final GuiExceptionHandler guiExceptionHandler) {
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.listCustomersHandler = Objects.requireNonNull(listCustomersHandler, "listCustomersHandler must not be null");
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        refreshCustomers(model.mobilePhoneFilter());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewEvent(final ViewEvent event) {
        final ViewEvent normalizedEvent = Objects.requireNonNull(event, "event must not be null");

        if (CustomerGuiEventTypes.LIST_CUSTOMERS_QUERY.equals(normalizedEvent.type())) {
            final String mobilePhone = extractMobilePhoneFilter(normalizedEvent);
            model.setMobilePhoneFilter(mobilePhone);
            refreshCustomers(mobilePhone);
            return;
        }

        if (CustomerGuiEventTypes.LIST_CUSTOMERS_ROW_SELECTED.equals(normalizedEvent.type())) {
            final Integer rowIndex = extractRowIndex(normalizedEvent);
            if (rowIndex == null) {
                model.setSelectedRowIndex(null);
                context.clearCurrentCustomerId();
                return;
            }

            model.setSelectedRowIndex(rowIndex);
            model.selectedCustomerId().ifPresent(context::setCurrentCustomerId);
        }
    }

    /**
     * @brief 刷新客户列表（Refresh Customer List）；
     *        Refresh customer list by current/updated filter.
     *
     * @param mobilePhoneFilter 手机号筛选值（Mobile-phone filter value）。
     */
    private void refreshCustomers(final String mobilePhoneFilter) {
        model.setErrorMessage(null);
        model.setLoading(true);
        try {
            final ListCustomersQuery query = mobilePhoneFilter == null || mobilePhoneFilter.trim().isEmpty()
                    ? ListCustomersQuery.all()
                    : ListCustomersQuery.byMobilePhone(mobilePhoneFilter.trim());
            final List<CustomerResult> customers = listCustomersHandler.handle(query);
            model.setCustomers(customers);
        } catch (RuntimeException ex) {
            model.setCustomers(List.of());
            model.setSelectedRowIndex(null);
            context.clearCurrentCustomerId();
            model.setErrorMessage(guiExceptionHandler.toUserMessage(ex));
        } finally {
            model.setLoading(false);
        }
    }

    /**
     * @brief 提取手机号筛选（Extract Mobile-phone Filter）；
     *        Extract mobile-phone filter from event.
     *
     * @param event 视图事件（View event）。
     * @return 手机号筛选值（Mobile-phone filter value）。
     */
    private static String extractMobilePhoneFilter(final ViewEvent event) {
        final Object rawFormValues = event.attributes().get(CustomerGuiEventTypes.ATTR_FORM_VALUES);
        if (!(rawFormValues instanceof Map<?, ?> rawMap)) {
            return "";
        }

        final Object rawPhone = rawMap.get(ListCustomersModel.FIELD_MOBILE_PHONE);
        return rawPhone == null ? "" : String.valueOf(rawPhone).trim();
    }

    /**
     * @brief 提取行索引（Extract Row Index）；
     *        Extract selected row index from event.
     *
     * @param event 视图事件（View event）。
     * @return 行索引（Row index）。
     */
    private static Integer extractRowIndex(final ViewEvent event) {
        final Object rawRowIndex = event.attributes().get(CustomerGuiEventTypes.ATTR_ROW_INDEX);
        if (rawRowIndex == null) {
            return null;
        }
        if (rawRowIndex instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(rawRowIndex));
    }
}
