package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.application.account.result.OpenAccountResult;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeEvent;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeListener;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import com.moesegfault.banking.presentation.gui.view.ErrorDialogView;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.SuccessDialogView;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @brief 开立储蓄账户页面视图（Open Savings Account View），负责表单渲染并转发提交事件；
 *        Open-savings-account page view that renders form state and forwards submit events.
 */
public final class OpenSavingsAccountView implements GuiView<OpenSavingsAccountModel>, ModelChangeListener {

    /**
     * @brief 表单视图端口（Form View Port）;
     *        Form-view port.
     */
    private final FormView formView;

    /**
     * @brief 错误弹窗端口（Error Dialog Port）;
     *        Error-dialog port.
     */
    private final ErrorDialogView errorDialogView;

    /**
     * @brief 成功弹窗端口（Success Dialog Port）;
     *        Success-dialog port.
     */
    private final SuccessDialogView successDialogView;

    /**
     * @brief 事件转发器（Event Sink）;
     *        View-event sink.
     */
    private final Consumer<ViewEvent> eventSink;

    /**
     * @brief 绑定模型（Bound Model）;
     *        Currently bound model.
     */
    private OpenSavingsAccountModel model;

    /**
     * @brief 最近展示错误（Last Rendered Error）;
     *        Last rendered error message.
     */
    private String lastRenderedError;

    /**
     * @brief 最近展示成功账户 ID（Last Rendered Success Account ID）;
     *        Last rendered success account identifier.
     */
    private String lastRenderedSuccessAccountId;

    /**
     * @brief 构造页面视图（Construct Page View）;
     *        Construct open-savings-account view.
     *
     * @param formView 表单视图（Form view）。
     * @param errorDialogView 错误弹窗（Error dialog）。
     * @param successDialogView 成功弹窗（Success dialog）。
     * @param eventSink 事件转发器（Event sink）。
     */
    public OpenSavingsAccountView(
            final FormView formView,
            final ErrorDialogView errorDialogView,
            final SuccessDialogView successDialogView,
            final Consumer<ViewEvent> eventSink
    ) {
        this.formView = Objects.requireNonNull(formView, "formView must not be null");
        this.errorDialogView = Objects.requireNonNull(errorDialogView, "errorDialogView must not be null");
        this.successDialogView = Objects.requireNonNull(successDialogView, "successDialogView must not be null");
        this.eventSink = Objects.requireNonNull(eventSink, "eventSink must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindModel(final OpenSavingsAccountModel model) {
        final OpenSavingsAccountModel normalizedModel = Objects.requireNonNull(model, "model must not be null");
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
        ensureModelBound();
        formView.setFieldOrder(List.of(AccountGuiSchema.CUSTOMER_ID, AccountGuiSchema.ACCOUNT_NO));
        formView.onSubmit(() -> eventSink.accept(new ViewEvent(
                OpenSavingsAccountController.EVENT_SUBMIT,
                Map.of("values", formView.values()))));
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
        ensureModelBound();
        formView.setValues(model.formValues());
        formView.clearErrors();
        maybeRenderError(model.errorMessage());
        maybeRenderSuccess(model.openAccountResult());
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
     * @brief 获取原生组件（Get Native Component）;
     *        Get toolkit-native component from form view.
     *
     * @return 原生组件（Native component）。
     */
    public Object component() {
        return formView.component();
    }

    /**
     * @brief 条件展示错误弹窗（Render Error Dialog Conditionally）;
     *        Render error dialog only when message changed.
     *
     * @param errorMessage 错误消息（Error message）。
     */
    private void maybeRenderError(final String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            lastRenderedError = null;
            return;
        }
        if (errorMessage.equals(lastRenderedError)) {
            return;
        }
        errorDialogView.showError("open_savings_account_failed", errorMessage);
        lastRenderedError = errorMessage;
    }

    /**
     * @brief 条件展示成功弹窗（Render Success Dialog Conditionally）;
     *        Render success dialog only when result changed.
     *
     * @param result 开户结果（Open-account result）。
     */
    private void maybeRenderSuccess(final OpenAccountResult result) {
        if (result == null) {
            lastRenderedSuccessAccountId = null;
            return;
        }
        final String accountId = result.accountId();
        if (accountId.equals(lastRenderedSuccessAccountId)) {
            return;
        }
        successDialogView.showSuccess(
                "open_savings_account_succeeded",
                "account_id=" + accountId + ", account_no=" + result.accountNo());
        lastRenderedSuccessAccountId = accountId;
    }

    /**
     * @brief 校验模型绑定状态（Ensure Model Bound）;
     *        Ensure model has been bound before lifecycle methods.
     */
    private void ensureModelBound() {
        if (model == null) {
            throw new IllegalStateException("model must be bound before view lifecycle");
        }
    }
}
