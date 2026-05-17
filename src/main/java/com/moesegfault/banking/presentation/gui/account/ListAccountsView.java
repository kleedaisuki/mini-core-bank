package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeEvent;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeListener;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import com.moesegfault.banking.presentation.gui.view.EmptyStateView;
import com.moesegfault.banking.presentation.gui.view.ErrorDialogView;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.NativeComponentView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @brief 账户列表页面视图（List Accounts View），负责筛选表单、列表渲染和行选择事件转发；
 *        List-accounts page view rendering filter form, account rows, and row-selection events.
 */
public final class ListAccountsView implements GuiView<ListAccountsModel>, ModelChangeListener, NativeComponentView {

    /**
     * @brief 筛选表单视图端口（Filter Form View Port）;
     *        Filter-form view port.
     */
    private final FormView filterFormView;

    /**
     * @brief 列表表格视图端口（Table View Port）;
     *        List table view port.
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
    private ListAccountsModel model;

    /**
     * @brief 最近展示错误（Last Rendered Error）;
     *        Last rendered error message.
     */
    private String lastRenderedError;

    /**
     * @brief 构造页面视图（Construct Page View）;
     *        Construct list-accounts view.
     *
     * @param filterFormView 筛选表单视图（Filter form view）。
     * @param tableView 列表表格视图（Table view）。
     * @param emptyStateView 空态视图（Empty-state view）。
     * @param errorDialogView 错误弹窗（Error dialog）。
     * @param eventSink 事件转发器（Event sink）。
     */
    public ListAccountsView(
            final FormView filterFormView,
            final TableView tableView,
            final EmptyStateView emptyStateView,
            final ErrorDialogView errorDialogView,
            final Consumer<ViewEvent> eventSink
    ) {
        this.filterFormView = Objects.requireNonNull(filterFormView, "filterFormView must not be null");
        this.tableView = Objects.requireNonNull(tableView, "tableView must not be null");
        this.emptyStateView = Objects.requireNonNull(emptyStateView, "emptyStateView must not be null");
        this.errorDialogView = Objects.requireNonNull(errorDialogView, "errorDialogView must not be null");
        this.eventSink = Objects.requireNonNull(eventSink, "eventSink must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindModel(final ListAccountsModel model) {
        final ListAccountsModel normalizedModel = Objects.requireNonNull(model, "model must not be null");
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
        filterFormView.setFieldOrder(List.of(AccountGuiSchema.CUSTOMER_ID, AccountGuiSchema.INCLUDE_CLOSED_ACCOUNTS));
        filterFormView.onSubmit(() -> eventSink.accept(new ViewEvent(
                ListAccountsController.EVENT_SUBMIT,
                Map.of("values", filterFormView.values()))));
        tableView.setColumns(AccountGuiSchema.ACCOUNT_TABLE_COLUMNS);
        tableView.onRowSelected(selectedIndex -> eventSink.accept(
                ViewEvent.of(ListAccountsController.EVENT_ROW_SELECTED, "selected_row_index", selectedIndex)));
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
        filterFormView.setValues(model.filterValues());
        filterFormView.clearErrors();
        renderRows(model.accountResults());
        renderEmptyState(model.accountResults());
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
     * @brief 渲染账户列表行（Render Account Rows）;
     *        Render account result rows into table.
     *
     * @param accountResults 账户结果列表（Account-result list）。
     */
    private void renderRows(final List<AccountResult> accountResults) {
        final List<List<String>> rows = new ArrayList<>(accountResults.size());
        for (AccountResult accountResult : accountResults) {
            rows.add(AccountGuiMapper.toCanonicalRow(accountResult));
        }
        tableView.setRows(rows);
    }

    /**
     * @brief 渲染空态文案（Render Empty-state Text）;
     *        Render empty-state text by list size.
     *
     * @param accountResults 账户结果列表（Account-result list）。
     */
    private void renderEmptyState(final List<AccountResult> accountResults) {
        if (!accountResults.isEmpty()) {
            emptyStateView.setTitle("accounts_loaded");
            emptyStateView.setDescription("total=" + accountResults.size());
            emptyStateView.setAction("refresh", () -> eventSink.accept(ViewEvent.of(
                    ListAccountsController.EVENT_SUBMIT,
                    "values",
                    filterFormView.values())));
            return;
        }
        emptyStateView.setTitle("no_accounts");
        emptyStateView.setDescription("query returned no account rows");
        emptyStateView.setAction("query", () -> eventSink.accept(ViewEvent.of(
                ListAccountsController.EVENT_SUBMIT,
                "values",
                filterFormView.values())));
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
        errorDialogView.showError("list_accounts_failed", errorMessage);
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
