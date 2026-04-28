package com.moesegfault.banking.presentation.gui.customer;

import com.moesegfault.banking.application.customer.result.CustomerResult;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @brief 客户列表页面视图（List Customers View），负责筛选表单、表格渲染与行选择事件；
 *        Customer-list page view handling filter form, table rendering, and row selection events.
 */
public final class ListCustomersView implements GuiView<ListCustomersModel> {

    /**
     * @brief 表格列定义（Table Column Definitions）；
     *        Table column definitions aligned with schema language.
     */
    private static final List<String> COLUMNS = List.of(
            "customer_id",
            "id_type",
            "id_number",
            "issuing_region",
            "mobile_phone",
            "customer_status");

    /**
     * @brief 筛选表单（Filter Form）；
     *        Filter form view.
     */
    private final FormView filterFormView;

    /**
     * @brief 客户表格（Customer Table）；
     *        Customer table view.
     */
    private final TableView tableView;

    /**
     * @brief 绑定模型（Bound Model）；
     *        Model bound to this view.
     */
    private ListCustomersModel model;

    /**
     * @brief 事件处理器（Event Handler）；
     *        Event handler callback for controller.
     */
    private Consumer<ViewEvent> eventHandler = ignored -> {
    };

    /**
     * @brief 最近渲染消息（Last Rendered Message）；
     *        Last rendered status/error message.
     */
    private String renderedMessage;

    /**
     * @brief 构造客户列表视图（Construct List Customers View）；
     *        Construct customer-list view.
     *
     * @param filterFormView 筛选表单视图（Filter form view）。
     * @param tableView 客户表格视图（Customer table view）。
     */
    public ListCustomersView(final FormView filterFormView, final TableView tableView) {
        this.filterFormView = Objects.requireNonNull(filterFormView, "filterFormView must not be null");
        this.tableView = Objects.requireNonNull(tableView, "tableView must not be null");

        this.filterFormView.setFieldOrder(List.of(ListCustomersModel.FIELD_MOBILE_PHONE));
        this.filterFormView.onSubmit(() -> {
            final Map<String, String> formValues = this.filterFormView.values();
            eventHandler.accept(new ViewEvent(
                    CustomerGuiEventTypes.LIST_CUSTOMERS_QUERY,
                    Map.of(CustomerGuiEventTypes.ATTR_FORM_VALUES, formValues)));
        });

        this.tableView.setColumns(COLUMNS);
        this.tableView.onRowSelected(selectedRow -> eventHandler.accept(new ViewEvent(
                CustomerGuiEventTypes.LIST_CUSTOMERS_ROW_SELECTED,
                Map.of(CustomerGuiEventTypes.ATTR_ROW_INDEX, selectedRow))));
    }

    /**
     * @brief 绑定事件处理器（Bind Event Handler）；
     *        Bind event handler for forwarded view events.
     *
     * @param eventHandler 事件处理器（Event handler）。
     */
    public void bindEventHandler(final Consumer<ViewEvent> eventHandler) {
        this.eventHandler = Objects.requireNonNull(eventHandler, "eventHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindModel(final ListCustomersModel model) {
        this.model = Objects.requireNonNull(model, "model must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mount() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unmount() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        final ListCustomersModel normalizedModel = requireModel();
        filterFormView.setValues(Map.of(ListCustomersModel.FIELD_MOBILE_PHONE, normalizedModel.mobilePhoneFilter()));
        tableView.setRows(toRows(normalizedModel.customers()));

        renderedMessage = normalizedModel.errorMessage().orElse(null);
    }

    /**
     * @brief 返回筛选表单组件（Return Filter-form Component）；
     *        Return native component of filter form.
     *
     * @return 原生组件（Native component）。
     */
    public Object filterComponent() {
        return filterFormView.component();
    }

    /**
     * @brief 返回表格组件（Return Table Component）；
     *        Return native component of table view.
     *
     * @return 原生组件（Native component）。
     */
    public Object tableComponent() {
        return tableView.component();
    }

    /**
     * @brief 返回最近渲染消息（Return Last Rendered Message）；
     *        Return last rendered message.
     *
     * @return 渲染消息（Rendered message）。
     */
    public String renderedMessage() {
        return renderedMessage;
    }

    /**
     * @brief 转换客户列表为表格行（Convert Customers to Table Rows）；
     *        Convert customer results to table row matrix.
     *
     * @param customers 客户列表（Customer list）。
     * @return 表格行矩阵（Table row matrix）。
     */
    private static List<List<String>> toRows(final List<CustomerResult> customers) {
        final List<List<String>> rows = new ArrayList<>();
        for (CustomerResult customer : customers) {
            final List<String> row = new ArrayList<>();
            row.add(customer.customerId());
            row.add(customer.idType());
            row.add(customer.idNumber());
            row.add(customer.issuingRegion());
            row.add(customer.mobilePhone());
            row.add(customer.customerStatus());
            rows.add(List.copyOf(row));
        }
        return List.copyOf(rows);
    }

    /**
     * @brief 校验并返回模型（Require Bound Model）；
     *        Require and return bound model.
     *
     * @return 非空模型（Non-null model）。
     */
    private ListCustomersModel requireModel() {
        if (model == null) {
            throw new IllegalStateException("ListCustomersView model must be bound before rendering");
        }
        return model;
    }
}
