package com.moesegfault.banking.presentation.gui.customer;

import com.moesegfault.banking.application.customer.result.CustomerResult;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeEvent;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeListener;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import com.moesegfault.banking.presentation.gui.view.FormView;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @brief 客户详情页面视图（Show Customer View），负责查询表单与详情渲染；
 *        Show-customer view handling query form and rendered customer details.
 */
public final class ShowCustomerView implements GuiView<ShowCustomerModel>, ModelChangeListener {

    /**
     * @brief 查询表单（Query Form）；
     *        Form view used for customer-id query.
     */
    private final FormView formView;

    /**
     * @brief 绑定模型（Bound Model）；
     *        Model bound to this view.
     */
    private ShowCustomerModel model;

    /**
     * @brief 事件处理器（Event Handler）；
     *        Event handler callback for controller.
     */
    private Consumer<ViewEvent> eventHandler = ignored -> {
    };

    /**
     * @brief 最近渲染详情（Last Rendered Detail Map）；
     *        Last rendered customer-detail map.
     */
    private Map<String, String> renderedCustomerDetails = Map.of();

    /**
     * @brief 最近渲染消息（Last Rendered Message）；
     *        Last rendered status/error/not-found message.
     */
    private String renderedMessage;

    /**
     * @brief 构造客户详情视图（Construct Show Customer View）；
     *        Construct show-customer view.
     *
     * @param formView 查询表单视图（Query form view）。
     */
    public ShowCustomerView(final FormView formView) {
        this.formView = Objects.requireNonNull(formView, "formView must not be null");
        this.formView.setFieldOrder(java.util.List.of(ShowCustomerModel.FIELD_CUSTOMER_ID));
        this.formView.onSubmit(() -> {
            final Map<String, String> values = this.formView.values();
            eventHandler.accept(new ViewEvent(
                    CustomerGuiEventTypes.SHOW_CUSTOMER_SUBMIT,
                    Map.of(CustomerGuiEventTypes.ATTR_FORM_VALUES, values)));
        });
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
    public void bindModel(final ShowCustomerModel model) {
        final ShowCustomerModel normalizedModel = Objects.requireNonNull(model, "model must not be null");
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
        final ShowCustomerModel normalizedModel = requireModel();
        formView.setValues(Map.of(ShowCustomerModel.FIELD_CUSTOMER_ID, normalizedModel.customerIdInput()));

        renderedCustomerDetails = normalizedModel.customer()
                .map(ShowCustomerView::toSchemaMap)
                .orElse(Map.of());

        if (normalizedModel.errorMessage().isPresent()) {
            renderedMessage = normalizedModel.errorMessage().orElseThrow();
            return;
        }
        if (normalizedModel.customerNotFound()) {
            renderedMessage = "customer_not_found customer_id=" + normalizedModel.customerIdInput();
            return;
        }
        renderedMessage = null;
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
     * @brief 返回底层组件（Return Underlying Component）；
     *        Return toolkit-native component.
     *
     * @return 原生组件（Native component）。
     */
    public Object component() {
        return formView.component();
    }

    /**
     * @brief 返回最近渲染详情（Return Last Rendered Details）；
     *        Return last rendered customer details.
     *
     * @return 详情映射（Detail map）。
     */
    public Map<String, String> renderedCustomerDetails() {
        return Map.copyOf(renderedCustomerDetails);
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
     * @brief 转换客户结果为 schema 映射（Map Customer Result to Schema Map）；
     *        Convert customer result to schema-aligned map.
     *
     * @param customer 客户结果（Customer result）。
     * @return schema 映射（Schema map）。
     */
    private static Map<String, String> toSchemaMap(final CustomerResult customer) {
        final Map<String, String> details = new LinkedHashMap<>();
        details.put("customer_id", customer.customerId());
        details.put("id_type", customer.idType());
        details.put("id_number", customer.idNumber());
        details.put("issuing_region", customer.issuingRegion());
        details.put("mobile_phone", customer.mobilePhone());
        details.put("residential_address", customer.residentialAddress());
        details.put("mailing_address", customer.mailingAddress());
        details.put("is_us_tax_resident", String.valueOf(customer.usTaxResident()));
        details.put("crs_info", String.valueOf(customer.crsInfo()));
        details.put("customer_status", customer.customerStatus());
        details.put("created_at", customer.createdAt().toString());
        details.put("updated_at", customer.updatedAt().toString());
        return Map.copyOf(details);
    }

    /**
     * @brief 校验并返回模型（Require Bound Model）；
     *        Require and return bound model.
     *
     * @return 非空模型（Non-null model）。
     */
    private ShowCustomerModel requireModel() {
        if (model == null) {
            throw new IllegalStateException("ShowCustomerView model must be bound before rendering");
        }
        return model;
    }
}
