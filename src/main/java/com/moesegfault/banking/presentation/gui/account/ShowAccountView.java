package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeEvent;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeListener;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import com.moesegfault.banking.presentation.gui.view.EmptyStateView;
import com.moesegfault.banking.presentation.gui.view.ErrorDialogView;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @brief 账户详情页面视图（Show Account View），负责查询输入、结果表格与空态提示渲染；
 *        Show-account page view that renders query form, result table, and empty-state hints.
 */
public final class ShowAccountView implements GuiView<ShowAccountModel>, ModelChangeListener {

    /**
     * @brief 查询表单视图端口（Query Form View Port）;
     *        Query-form view port.
     */
    private final FormView queryFormView;

    /**
     * @brief 结果表格视图端口（Result Table View Port）;
     *        Result-table view port.
     */
    private final TableView tableView;

    /**
     * @brief 空态视图端口（Empty-state View Port）;
     *        Empty-state view port.
     */
    private final EmptyStateView emptyStateView;

    /**
     * @brief 错误弹窗端口（Error Dialog Port）;
     *        Error-dialog port.
     */
    private final ErrorDialogView errorDialogView;

    /**
     * @brief 事件转发器（Event Sink）;
     *        View-event sink.
     */
    private final Consumer<ViewEvent> eventSink;

    /**
     * @brief 绑定模型（Bound Model）;
     *        Currently bound model.
     */
    private ShowAccountModel model;

    /**
     * @brief 最近展示错误（Last Rendered Error）;
     *        Last rendered error message.
     */
    private String lastRenderedError;

    /**
     * @brief 构造页面视图（Construct Page View）;
     *        Construct show-account view.
     *
     * @param queryFormView 查询表单视图（Query form view）。
     * @param tableView 结果表格视图（Result table view）。
     * @param emptyStateView 空态视图（Empty-state view）。
     * @param errorDialogView 错误弹窗（Error dialog）。
     * @param eventSink 事件转发器（Event sink）。
     */
    public ShowAccountView(
            final FormView queryFormView,
            final TableView tableView,
            final EmptyStateView emptyStateView,
            final ErrorDialogView errorDialogView,
            final Consumer<ViewEvent> eventSink
    ) {
        this.queryFormView = Objects.requireNonNull(queryFormView, "queryFormView must not be null");
        this.tableView = Objects.requireNonNull(tableView, "tableView must not be null");
        this.emptyStateView = Objects.requireNonNull(emptyStateView, "emptyStateView must not be null");
        this.errorDialogView = Objects.requireNonNull(errorDialogView, "errorDialogView must not be null");
        this.eventSink = Objects.requireNonNull(eventSink, "eventSink must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindModel(final ShowAccountModel model) {
        final ShowAccountModel normalizedModel = Objects.requireNonNull(model, "model must not be null");
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
        queryFormView.setFieldOrder(List.of(AccountGuiSchema.ACCOUNT_ID, AccountGuiSchema.ACCOUNT_NO));
        queryFormView.onSubmit(() -> eventSink.accept(new ViewEvent(
                ShowAccountController.EVENT_SUBMIT,
                Map.of("values", queryFormView.values()))));
        tableView.setColumns(AccountGuiSchema.ACCOUNT_TABLE_COLUMNS);
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
        queryFormView.setValues(model.queryValues());
        queryFormView.clearErrors();
        renderResult(model.accountResult());
        renderEmptyState(model.accountResult());
        maybeRenderError(model.errorMessage());
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
     * @brief 获取页面原生组件（Get Page Native Component）;
     *        Get toolkit-native page component.
     *
     * @return 页面原生组件（Page native component）。
     */
    public Object component() {
        return tableView.component();
    }

    /**
     * @brief 渲染查询结果（Render Query Result）;
     *        Render account-result row into table.
     *
     * @param accountResult 账户结果（Account result, nullable）。
     */
    private void renderResult(final AccountResult accountResult) {
        if (accountResult == null) {
            tableView.setRows(List.of());
            return;
        }
        tableView.setRows(List.of(AccountGuiMapper.toCanonicalRow(accountResult)));
    }

    /**
     * @brief 渲染空态文案（Render Empty-state Text）;
     *        Render empty-state text for no-result condition.
     *
     * @param accountResult 账户结果（Account result, nullable）。
     */
    private void renderEmptyState(final AccountResult accountResult) {
        if (accountResult != null) {
            emptyStateView.setTitle("account_found");
            emptyStateView.setDescription("query returned one account record");
            emptyStateView.setAction("refresh", () -> eventSink.accept(ViewEvent.of(ShowAccountController.EVENT_SUBMIT,
                    "values",
                    queryFormView.values())));
            return;
        }
        emptyStateView.setTitle("no_account_result");
        emptyStateView.setDescription("provide exactly one of account_id or account_no");
        emptyStateView.setAction("query", () -> eventSink.accept(ViewEvent.of(ShowAccountController.EVENT_SUBMIT,
                "values",
                queryFormView.values())));
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
        errorDialogView.showError("show_account_failed", errorMessage);
        lastRenderedError = errorMessage;
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
