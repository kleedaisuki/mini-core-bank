package com.moesegfault.banking.presentation.gui.ledger;

import com.moesegfault.banking.application.ledger.result.BalanceResult;
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
import java.awt.Component;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @brief 余额查询页面视图（Show Balance View），渲染查询表单、结果表格与空态提示；
 *        Balance-query page view rendering query form, result table and empty-state hints.
 */
public final class ShowBalanceView implements GuiView<ShowBalanceModel> {

    /**
     * @brief 结果表格列定义（Result Table Columns）；
     *        Canonical schema columns for balance result table.
     */
    private static final List<String> RESULT_COLUMNS = List.of(
            "account_id",
            "currency_code",
            "ledger_balance",
            "available_balance",
            "updated_at");

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
     *        Form view used for account/currency inputs.
     */
    private final FormView formView;

    /**
     * @brief 结果表格视图（Result Table View）；
     *        Table view used for balance result rendering.
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
    private ShowBalanceModel model;

    /**
     * @brief 构造余额查询视图（Construct Show Balance View）；
     *        Construct balance-query view.
     *
     * @param guiRuntime GUI 运行时（GUI runtime）。
     * @param controller 页面控制器（Page controller）。
     * @param formView 查询表单视图（Query form view）。
     * @param tableView 结果表格视图（Result table view）。
     * @param emptyStateView 空态视图（Empty-state view）。
     */
    public ShowBalanceView(final GuiRuntime guiRuntime,
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
    public void bindModel(final ShowBalanceModel model) {
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
        final ShowBalanceModel currentModel = model;
        if (currentModel == null) {
            return;
        }

        formView.setValues(Map.of(
                ShowBalanceController.FIELD_ACCOUNT_ID, currentModel.accountIdInput(),
                ShowBalanceController.FIELD_CURRENCY_CODE, currentModel.currencyCodeInput()));
        formView.clearErrors();

        if (!currentModel.errorMessage().isBlank()) {
            formView.setFieldError(ShowBalanceController.FIELD_ACCOUNT_ID, currentModel.errorMessage());
            statusLabel.setText(currentModel.errorMessage());
        } else if (currentModel.loading()) {
            statusLabel.setText("loading...");
        } else if (!currentModel.hintMessage().isBlank()) {
            statusLabel.setText(currentModel.hintMessage());
        } else {
            statusLabel.setText("ready");
        }

        if (currentModel.balanceResult().isPresent()) {
            tableView.setRows(List.of(toResultRow(currentModel.balanceResult().orElseThrow())));
            resultCardLayout.show(resultPanel, CARD_RESULT);
            return;
        }

        emptyStateView.setTitle("No Balance Result");
        emptyStateView.setDescription(statusLabel.getText());
        resultCardLayout.show(resultPanel, CARD_EMPTY);
    }

    /**
     * @brief 配置子视图（Configure Sub Views）；
     *        Configure form/table/empty-state and root panel layout.
     */
    private void configureViews() {
        formView.setFieldOrder(List.of(
                ShowBalanceController.FIELD_ACCOUNT_ID,
                ShowBalanceController.FIELD_CURRENCY_CODE));
        formView.onSubmit(this::submitQuery);

        tableView.setColumns(RESULT_COLUMNS);
        tableView.setRows(List.of());

        emptyStateView.setTitle("Balance Query");
        emptyStateView.setDescription("请输入 account_id 与 currency_code 后点击 Submit");
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
     * @brief 提交余额查询（Submit Balance Query）；
     *        Read form values and emit balance-search event.
     */
    private void submitQuery() {
        final Map<String, String> values = formView.values();
        controller.onViewEvent(new ViewEvent(
                ShowBalanceController.EVENT_SEARCH_BALANCE,
                Map.of(
                        ShowBalanceController.FIELD_ACCOUNT_ID,
                        Objects.requireNonNullElse(values.get(ShowBalanceController.FIELD_ACCOUNT_ID), ""),
                        ShowBalanceController.FIELD_CURRENCY_CODE,
                        Objects.requireNonNullElse(values.get(ShowBalanceController.FIELD_CURRENCY_CODE), ""))));
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
     * @brief 构造结果行（Build Result Row）；
     *        Build one result-table row from balance result.
     *
     * @param result 余额结果（Balance result）。
     * @return 表格行（Table row）。
     */
    private static List<String> toResultRow(final BalanceResult result) {
        final BalanceResult normalized = Objects.requireNonNull(result, "result must not be null");
        return List.of(
                normalized.accountId(),
                normalized.currencyCode().value(),
                normalized.ledgerBalance().amount().toPlainString(),
                normalized.availableBalance().amount().toPlainString(),
                normalized.updatedAt().toString());
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
