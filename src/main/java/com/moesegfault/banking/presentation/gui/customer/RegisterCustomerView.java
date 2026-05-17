package com.moesegfault.banking.presentation.gui.customer;

import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeEvent;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeListener;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.NativeComponentView;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @brief 客户注册页面视图（Register Customer View），负责表单渲染与提交事件转发；
 *        Register-customer page view responsible for form rendering and submit-event forwarding.
 */
public final class RegisterCustomerView
        implements GuiView<RegisterCustomerModel>, ModelChangeListener, NativeComponentView {

    /**
     * @brief 注册表单视图（Register Form View）；
     *        Form view used by register-customer page.
     */
    private final FormView formView;

    /**
     * @brief 绑定模型（Bound Model）；
     *        Model instance bound to this view.
     */
    private RegisterCustomerModel model;

    /**
     * @brief 事件处理回调（Event Handler Callback）；
     *        Callback receiving forwarded view events.
     */
    private Consumer<ViewEvent> eventHandler = ignored -> {
    };

    /**
     * @brief 最近渲染反馈消息（Last Rendered Feedback Message）；
     *        Last rendered feedback message for diagnostics.
     */
    private String renderedFeedbackMessage;

    /**
     * @brief 构造客户注册页面视图（Construct Register Customer View）；
     *        Construct register-customer view.
     *
     * @param formView 表单视图（Form view）。
     */
    public RegisterCustomerView(final FormView formView) {
        this.formView = Objects.requireNonNull(formView, "formView must not be null");
        this.formView.setFieldOrder(RegisterCustomerModel.FIELD_ORDER);
        this.formView.onSubmit(() -> {
            final Map<String, String> values = this.formView.values();
            eventHandler.accept(new ViewEvent(
                    CustomerGuiEventTypes.REGISTER_CUSTOMER_SUBMIT,
                    Map.of(CustomerGuiEventTypes.ATTR_FORM_VALUES, values)));
        });
    }

    /**
     * @brief 绑定事件处理器（Bind Event Handler）；
     *        Bind view-event handler for controller callback.
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
    public void bindModel(final RegisterCustomerModel model) {
        final RegisterCustomerModel normalizedModel = Objects.requireNonNull(model, "model must not be null");
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
        final RegisterCustomerModel normalizedModel = requireModel();
        formView.setValues(normalizedModel.formValues());
        formView.clearErrors();
        for (Map.Entry<String, String> fieldError : normalizedModel.fieldErrors().entrySet()) {
            formView.setFieldError(fieldError.getKey(), fieldError.getValue());
        }

        renderedFeedbackMessage = normalizedModel.errorMessage()
                .or(() -> normalizedModel.successMessage())
                .orElse(null);
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
     *        Return toolkit-native component of this view.
     *
     * @return 原生组件（Native component）。
     */
    public Object component() {
        return formView.component();
    }

    /**
     * @brief 返回最近反馈消息（Return Last Feedback Message）；
     *        Return last rendered feedback message.
     *
     * @return 反馈消息可选值（Optional feedback message）。
     */
    public String renderedFeedbackMessage() {
        return renderedFeedbackMessage;
    }

    /**
     * @brief 校验并返回模型（Require Bound Model）；
     *        Require and return bound model.
     *
     * @return 非空模型（Non-null model）。
     */
    private RegisterCustomerModel requireModel() {
        if (model == null) {
            throw new IllegalStateException("RegisterCustomerView model must be bound before rendering");
        }
        return model;
    }
}
