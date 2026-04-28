package com.moesegfault.banking.presentation.gui.ledger;

import com.moesegfault.banking.application.ledger.result.LedgerEntryResult;
import com.moesegfault.banking.presentation.gui.GuiRuntime;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeEvent;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeListener;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import com.moesegfault.banking.presentation.gui.view.EmptyStateView;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @brief 分录查询页面视图（Show Entries View），渲染查询表单、分录表格与空态提示；
 *        Ledger-entry query page view rendering query form, entry table and empty-state hints.
 */
public final class ShowEntriesView implements GuiView<ShowEntriesModel> {

    /**
     * @brief 分录表格列定义（Entry Table Columns）；
     *        Canonical schema columns for ledger-entry table.
     */
    private static final List<String> ENTRY_COLUMNS = List.of(
            "entry_id",
            "transaction_id",
            "batch_id",
            "account_id",
            "currency_code",
            "entry_direction",
            "amount",
            "ledger_balance_after",
            "available_balance_after",
            "entry_type",
            "posted_at");

    /**
     * @brief 空态卡片名（Empty-state Card Name）；
     *        Card name for empty-state component.
     */
    private static final String CARD_EMPTY = "empty";

    /**
     * @brief 结果卡片名（Result Card Name）；
     *        Card name for result table component.
     */
    private static final String CARD_RESULT = "result";

    /**
     * @brief GUI 运行时（GUI Runtime）；
     *        GUI runtime used for mounting page content.
     */
    private final GuiRuntime guiRuntime;

    /**
     * @brief 页面控制器（Page Controller）；
     *        Page controller receiving submit events.
     */
    private final GuiController controller;

    /**
     * @brief 查询表单视图（Query Form View）；
     *        Form view used for account/limit inputs.
     */
    private final FormView formView;

    /**
     * @brief 分录表格视图（Entry Table View）；
     *        Table view used for entry result rendering.
     */
    private final TableView tableView;

    /**
     * @brief 空态视图（Empty-state View）；
     *        Empty-state view used before/after query.
     */
    private final EmptyStateView emptyStateView;

    /**
     * @brief 根面板（Root Panel）；
     *        Root Swing panel mounted into main window.
     */
    private final JPanel rootPanel = new JPanel(new BorderLayout(8, 8));

    /**
     * @brief 卡片布局（Card Layout）；
     *        Card layout switching between empty-state and result table.
     */
    private final CardLayout resultCardLayout = new CardLayout();

    /**
     * @brief 结果区域面板（Result-area Panel）；
     *        Panel hosting result-table/empty-state cards.
     */
    private final JPanel resultPanel = new JPanel(resultCardLayout);

    /**
     * @brief 底部状态标签（Status Label）；
     *        Bottom status label for loading/error/info messages.
     */
    private final JLabel statusLabel = new JLabel("Ready");

    /**
     * @brief 模型变更监听器（Model-change Listener）；
     *        Listener rerendering view on model updates.
     */
    private final ModelChangeListener modelChangeListener = this::onModelChanged;

    /**
     * @brief 当前绑定模型（Bound Model）；
     *        Currently bound page model.
     */
    private ShowEntriesModel model;

    /**
     * @brief 构造分录查询视图（Construct Show Entries View）；
     *        Construct ledger-entry query view.
     *
     * @param guiRuntime GUI 运行时（GUI runtime）。
     * @param controller 页面控制器（Page controller）。
     * @param formView 查询表单视图（Query form view）。
     * @param tableView 分录表格视图（Entry table view）。
     * @param emptyStateView 空态视图（Empty-state view）。
     */
    public ShowEntriesView(final GuiRuntime guiRuntime,
                           final GuiController controller,
                           final FormView formView,
                           final TableView tableView,
                           final EmptyStateView emptyStateView) {
        this.guiRuntime = Objects.requireNonNull(guiRuntime, "guiRuntime must not be null");
        this.controller = Objects.requireNonNull(controller, "controller must not be null");
        this.formView = Objects.requireNonNull(formView, "formView must not be null");
        this.tableView = Objects.requireNonNull(tableView, "tableView must not be null");
        this.emptyStateView = Objects.requireNonNull(emptyStateView, "emptyStateView must not be null");

        configureViews();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindModel(final ShowEntriesModel model) {
        if (this.model != null) {
            this.model.removeChangeListener(modelChangeListener);
        }
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.model.addChangeListener(modelChangeListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mount() {
        guiRuntime.mainWindow().setContent(rootPanel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unmount() {
        if (model != null) {
            model.removeChangeListener(modelChangeListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        final ShowEntriesModel currentModel = model;
        if (currentModel == null) {
            return;
        }

        formView.setValues(Map.of(
                ShowEntriesController.FIELD_ACCOUNT_ID, currentModel.accountIdInput(),
                ShowEntriesController.FIELD_LIMIT, currentModel.limitInput()));
        formView.clearErrors();

        if (!currentModel.errorMessage().isBlank()) {
            formView.setFieldError(ShowEntriesController.FIELD_ACCOUNT_ID, currentModel.errorMessage());
            statusLabel.setText(currentModel.errorMessage());
        } else if (currentModel.loading()) {
            statusLabel.setText("loading...");
        } else if (!currentModel.hintMessage().isBlank()) {
            statusLabel.setText(currentModel.hintMessage());
        } else {
            statusLabel.setText("total=" + currentModel.entries().size());
        }

        if (!currentModel.entries().isEmpty()) {
            tableView.setRows(currentModel.entries().stream().map(ShowEntriesView::toResultRow).toList());
            resultCardLayout.show(resultPanel, CARD_RESULT);
            return;
        }

        tableView.setRows(List.of());
        emptyStateView.setTitle("No Ledger Entries");
        emptyStateView.setDescription(statusLabel.getText());
        resultCardLayout.show(resultPanel, CARD_EMPTY);
    }

    /**
     * @brief 配置子视图（Configure Sub Views）；
     *        Configure form/table/empty-state and root panel layout.
     */
    private void configureViews() {
        formView.setFieldOrder(List.of(
                ShowEntriesController.FIELD_ACCOUNT_ID,
                ShowEntriesController.FIELD_LIMIT));
        formView.onSubmit(this::submitQuery);

        tableView.setColumns(ENTRY_COLUMNS);
        tableView.setRows(List.of());

        emptyStateView.setTitle("Ledger Entries Query");
        emptyStateView.setDescription("请输入 account_id 与 limit 后点击 Submit");
        emptyStateView.setAction("", () -> {
        });

        resultPanel.add(requireSwingComponent(emptyStateView.component(), "emptyStateView.component"), CARD_EMPTY);
        resultPanel.add(requireSwingComponent(tableView.component(), "tableView.component"), CARD_RESULT);

        rootPanel.add(requireSwingComponent(formView.component(), "formView.component"), BorderLayout.NORTH);
        rootPanel.add(resultPanel, BorderLayout.CENTER);
        rootPanel.add(statusLabel, BorderLayout.SOUTH);
        resultCardLayout.show(resultPanel, CARD_EMPTY);
    }

    /**
     * @brief 提交分录查询（Submit Entries Query）；
     *        Read form values and emit entries-search event.
     */
    private void submitQuery() {
        final Map<String, String> values = formView.values();
        controller.onViewEvent(new ViewEvent(
                ShowEntriesController.EVENT_SEARCH_ENTRIES,
                Map.of(
                        ShowEntriesController.FIELD_ACCOUNT_ID,
                        Objects.requireNonNullElse(values.get(ShowEntriesController.FIELD_ACCOUNT_ID), ""),
                        ShowEntriesController.FIELD_LIMIT,
                        Objects.requireNonNullElse(values.get(ShowEntriesController.FIELD_LIMIT), ""))));
    }

    /**
     * @brief 处理模型变更（Handle Model Change）；
     *        Handle model-change event by rerendering view.
     *
     * @param ignored 变更事件（Change event）。
     */
    private void onModelChanged(final ModelChangeEvent ignored) {
        render();
    }

    /**
     * @brief 构造分录行（Build Ledger-entry Row）；
     *        Build one table row from ledger-entry result.
     *
     * @param result 分录结果（Ledger-entry result）。
     * @return 表格行（Table row）。
     */
    private static List<String> toResultRow(final LedgerEntryResult result) {
        final LedgerEntryResult normalized = Objects.requireNonNull(result, "result must not be null");
        return List.of(
                normalized.entryId(),
                normalized.transactionId(),
                nullableText(normalized.batchId()),
                normalized.accountId(),
                normalized.currencyCode().value(),
                normalized.entryDirection().name(),
                normalized.amount().amount().toPlainString(),
                normalized.ledgerBalanceAfter() == null ? "" : normalized.ledgerBalanceAfter().amount().toPlainString(),
                normalized.availableBalanceAfter() == null ? "" : normalized.availableBalanceAfter().amount().toPlainString(),
                normalized.entryType().name(),
                normalized.postedAt().toString());
    }

    /**
     * @brief 规范化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text by trimming and replacing null with empty string.
     *
     * @param value 原始文本（Raw text）。
     * @return 规范化文本（Normalized text）。
     */
    private static String nullableText(final String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    /**
     * @brief 校验并转换为 Swing 组件（Require Swing Component）；
     *        Validate and cast toolkit component to Swing JComponent.
     *
     * @param component 原始组件（Raw component）。
     * @param label 组件标签（Component label）。
     * @return Swing 组件（Swing component）。
     */
    private static JComponent requireSwingComponent(final Object component, final String label) {
        final Object normalized = Objects.requireNonNull(component, label + " must not be null");
        if (!(normalized instanceof JComponent swingComponent)) {
            throw new IllegalArgumentException(label + " must be a Swing JComponent but was: " + normalized.getClass().getName());
        }
        return swingComponent;
    }
}
