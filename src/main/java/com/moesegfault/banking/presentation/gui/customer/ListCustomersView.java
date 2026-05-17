package com.moesegfault.banking.presentation.gui.customer;

import com.moesegfault.banking.application.customer.result.CustomerResult;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeEvent;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeListener;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.NativeComponentView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * @brief 客户列表页面视图（List Customers View），负责筛选表单、表格渲染与行选择事件；
 *        Customer-list page view handling filter form, table rendering, and row selection events.
 */
public final class ListCustomersView implements GuiView<ListCustomersModel>, ModelChangeListener, NativeComponentView {

    /**
     * @brief 表格列定义（Table Column Definitions）；
     *        Table column definitions aligned with schema language.
     */
    private static final List<String> COLUMNS = List.of(
            "customer_id",
            "mobile_phone",
            "id_type",
            "issuing_region",
            "id_number",
            "customer_status");

    /**
     * @brief 普通状态颜色（Neutral Status Color）；
     *        Neutral color used for informational status text.
     */
    private static final Color STATUS_NEUTRAL = new Color(80, 88, 99);

    /**
     * @brief 错误状态颜色（Error Status Color）；
     *        Error color used for failed query status text.
     */
    private static final Color STATUS_ERROR = new Color(176, 42, 55);

    /**
     * @brief 成功状态颜色（Success Status Color）；
     *        Success color used for loaded-result status text.
     */
    private static final Color STATUS_SUCCESS = new Color(22, 101, 52);

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
     * @brief 页面根面板（Page Root Panel）；
     *        Root panel composing the filter form and customer table.
     */
    private final JPanel rootPanel = new JPanel(new BorderLayout(8, 8));

    /**
     * @brief 状态标签（Status Label）；
     *        Status label showing query progress and empty-result feedback.
     */
    private final JLabel statusLabel = new JLabel("Ready");

    /**
     * @brief 根面板配置标记（Root Panel Configured Flag）；
     *        Whether the Swing root panel has been configured.
     */
    private boolean rootPanelConfigured;

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
        this.filterFormView.setSubmitLabel("Search");
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
        final ListCustomersModel normalizedModel = Objects.requireNonNull(model, "model must not be null");
        if (this.model != null) {
            this.model.removeChangeListener(this);
        }
        this.model = normalizedModel;
        this.model.addChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mount() {
        requireModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unmount() {
        if (model != null) {
            model.removeChangeListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        final ListCustomersModel normalizedModel = requireModel();
        filterFormView.setValues(Map.of(ListCustomersModel.FIELD_MOBILE_PHONE, normalizedModel.mobilePhoneFilter()));
        tableView.setRows(toRows(normalizedModel.customers()));
        renderStatus(normalizedModel);

        renderedMessage = statusLabel.getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onModelChanged(final ModelChangeEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        render();
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
     * {@inheritDoc}
     */
    @Override
    public Object component() {
        configureRootPanel();
        return rootPanel;
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
            row.add(customer.mobilePhone());
            row.add(customer.idType());
            row.add(customer.issuingRegion());
            row.add(customer.idNumber());
            row.add(customer.customerStatus());
            rows.add(List.copyOf(row));
        }
        return List.copyOf(rows);
    }

    /**
     * @brief 渲染查询状态（Render Query Status）；
     *        Render feedback for loading, empty, success, and error states.
     *
     * @param currentModel 当前模型（Current model）。
     */
    private void renderStatus(final ListCustomersModel currentModel) {
        if (currentModel.loading()) {
            setStatus("Searching customers...", STATUS_NEUTRAL);
            return;
        }

        if (currentModel.errorMessage().isPresent()) {
            setStatus("Query failed: " + currentModel.errorMessage().orElseThrow(), STATUS_ERROR);
            return;
        }

        final int customerCount = currentModel.customers().size();
        if (customerCount == 0) {
            final String filter = currentModel.mobilePhoneFilter().trim();
            if (filter.isEmpty()) {
                setStatus("No customers found. Register a customer first, then refresh this list.", STATUS_NEUTRAL);
                return;
            }
            setStatus("No customers match mobile phone " + filter + ".", STATUS_NEUTRAL);
            return;
        }

        final String suffix = currentModel.selectedCustomerId()
                .map(customerId -> " Selected " + customerId + ".")
                .orElse("");
        setStatus("Loaded " + customerCount + " customer" + (customerCount == 1 ? "" : "s") + "." + suffix, STATUS_SUCCESS);
    }

    /**
     * @brief 更新状态标签（Update Status Label）；
     *        Update status-label text and color.
     *
     * @param text 状态文本（Status text）。
     * @param color 状态颜色（Status color）。
     */
    private void setStatus(final String text, final Color color) {
        statusLabel.setText(Objects.requireNonNull(text, "text must not be null"));
        statusLabel.setForeground(Objects.requireNonNull(color, "color must not be null"));
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

    /**
     * @brief 配置根面板（Configure Root Panel）；
     *        Configure the Swing root panel lazily when the runtime requests a native component.
     */
    private void configureRootPanel() {
        if (rootPanelConfigured) {
            return;
        }

        rootPanel.setBorder(new EmptyBorder(18, 18, 16, 18));
        rootPanel.setBackground(new Color(246, 248, 250));

        final JLabel titleLabel = new JLabel("Customers");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20.0F));

        final JLabel descriptionLabel = new JLabel("Customer directory");
        descriptionLabel.setForeground(STATUS_NEUTRAL);

        final JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(titleLabel);
        titlePanel.add(descriptionLabel);

        final JPanel queryPanel = new JPanel(new BorderLayout());
        queryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(208, 215, 222)),
                new EmptyBorder(10, 10, 10, 10)));
        queryPanel.setBackground(Color.WHITE);
        queryPanel.add(requireComponent(filterFormView.component(), "filterFormView.component"), BorderLayout.CENTER);

        final JPanel topPanel = new JPanel(new BorderLayout(0, 12));
        topPanel.setOpaque(false);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(queryPanel, BorderLayout.CENTER);

        final JComponent tableComponent = requireComponent(tableView.component(), "tableView.component");
        tableComponent.setBorder(BorderFactory.createLineBorder(new Color(208, 215, 222)));

        statusLabel.setBorder(new EmptyBorder(8, 2, 0, 2));
        statusLabel.setForeground(STATUS_NEUTRAL);

        rootPanel.add(topPanel, BorderLayout.NORTH);
        rootPanel.add(tableComponent, BorderLayout.CENTER);
        rootPanel.add(statusLabel, BorderLayout.SOUTH);
        rootPanelConfigured = true;
    }

    /**
     * @brief 校验并转换原生组件（Require Native Component）；
     *        Validate and cast a child native component for Swing composition.
     *
     * @param component 原始组件（Raw component）。
     * @param label 组件标签（Component label）。
     * @return Swing 组件（Swing component）。
     */
    private static JComponent requireComponent(final Object component, final String label) {
        final Object normalized = Objects.requireNonNull(component, label + " must not be null");
        if (!(normalized instanceof JComponent swingComponent)) {
            throw new IllegalArgumentException(
                    label + " must be a Swing JComponent but was: " + normalized.getClass().getName());
        }
        return swingComponent;
    }
}
