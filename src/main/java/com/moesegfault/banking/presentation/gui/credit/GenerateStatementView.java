package com.moesegfault.banking.presentation.gui.credit;

import com.moesegfault.banking.application.credit.result.CreditCardStatementResult;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeListener;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @brief 生成信用卡账单页面视图（Generate Statement View），桥接表单与结果表格；
 *        View for generate-statement page bridging form input and result table rendering.
 */
public final class GenerateStatementView implements GuiView<GenerateStatementModel> {

    /**
     * @brief 表单字段顺序（Form Field Order）；
     *        Canonical field order for form rendering.
     */
    private static final List<String> FORM_FIELD_ORDER = List.of(
            GenerateStatementModel.FIELD_CREDIT_CARD_ACCOUNT_ID,
            GenerateStatementModel.FIELD_STATEMENT_DATE,
            GenerateStatementModel.FIELD_MINIMUM_PAYMENT_RATE_DECIMAL,
            GenerateStatementModel.FIELD_MINIMUM_PAYMENT_FLOOR_AMOUNT);

    /**
     * @brief 账单结果列定义（Statement Result Columns）；
     *        Canonical columns for statement-result table.
     */
    private static final List<String> STATEMENT_COLUMNS = List.of(
            "statement_id",
            "credit_card_account_id",
            "statement_period_start",
            "statement_period_end",
            "statement_date",
            "payment_due_date",
            "total_amount_due",
            "minimum_amount_due",
            "paid_amount",
            "outstanding_amount",
            "statement_status",
            "currency_code");

    /**
     * @brief 表单视图（Form View）；
     *        Form view abstraction.
     */
    private final FormView formView;

    /**
     * @brief 结果表格视图（Result Table View）；
     *        Table view abstraction for statement result.
     */
    private final TableView tableView;

    /**
     * @brief 模型变更监听器（Model-change Listener）；
     *        Listener that triggers view re-render.
     */
    private final ModelChangeListener modelChangeListener = ignored -> render();

    /**
     * @brief 视图事件处理器（View-event Handler）；
     *        Consumer receiving submit events.
     */
    private Consumer<ViewEvent> viewEventHandler = ignored -> {
    };

    /**
     * @brief 绑定模型（Bound Model）；
     *        Currently bound model instance.
     */
    private GenerateStatementModel model;

    /**
     * @brief 构造视图（Construct View）；
     *        Construct generate-statement view.
     *
     * @param formView 表单视图（Form view）。
     * @param tableView 结果表格视图（Result table view）。
     */
    public GenerateStatementView(final FormView formView, final TableView tableView) {
        this.formView = Objects.requireNonNull(formView, "formView must not be null");
        this.tableView = Objects.requireNonNull(tableView, "tableView must not be null");
    }

    /**
     * @brief 绑定视图事件处理器（Bind View-event Handler）；
     *        Bind one view-event handler receiving submit events.
     *
     * @param viewEventHandler 事件处理器（View-event handler）。
     */
    public void setViewEventHandler(final Consumer<ViewEvent> viewEventHandler) {
        this.viewEventHandler = Objects.requireNonNull(viewEventHandler, "viewEventHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindModel(final GenerateStatementModel model) {
        final GenerateStatementModel normalizedModel = Objects.requireNonNull(model, "model must not be null");
        if (this.model != null) {
            this.model.removeChangeListener(modelChangeListener);
        }
        this.model = normalizedModel;
        this.model.addChangeListener(modelChangeListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mount() {
        requireBoundModel();
        formView.setFieldOrder(FORM_FIELD_ORDER);
        tableView.setColumns(STATEMENT_COLUMNS);
        formView.onSubmit(() -> {
            final Map<String, Object> attributes = new LinkedHashMap<>();
            formView.values().forEach(attributes::put);
            viewEventHandler.accept(new ViewEvent(GenerateStatementController.EVENT_SUBMIT, attributes));
        });
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
        final GenerateStatementModel currentModel = requireBoundModel();
        formView.setValues(currentModel.formValues());
        formView.clearErrors();
        currentModel.fieldErrors().forEach(formView::setFieldError);
        tableView.setRows(currentModel.statementResult()
                .map(this::toStatementRows)
                .orElseGet(List::of));
    }

    /**
     * @brief 转换账单结果为表格行（Convert Statement Result To Table Rows）；
     *        Convert one statement result into table rows.
     *
     * @param result 账单结果（Statement result）。
     * @return 表格行（Table rows）。
     */
    private List<List<String>> toStatementRows(final CreditCardStatementResult result) {
        final CreditCardStatementResult normalizedResult = Objects.requireNonNull(result, "result must not be null");
        final List<String> row = new ArrayList<>(STATEMENT_COLUMNS.size());
        row.add(normalizedResult.statementId());
        row.add(normalizedResult.creditCardAccountId());
        row.add(formatDate(normalizedResult.statementPeriodStart()));
        row.add(formatDate(normalizedResult.statementPeriodEnd()));
        row.add(formatDate(normalizedResult.statementDate()));
        row.add(formatDate(normalizedResult.paymentDueDate()));
        row.add(formatDecimal(normalizedResult.totalAmountDue()));
        row.add(formatDecimal(normalizedResult.minimumAmountDue()));
        row.add(formatDecimal(normalizedResult.paidAmount()));
        row.add(formatDecimal(normalizedResult.outstandingAmount()));
        row.add(normalizedResult.statementStatus());
        row.add(normalizedResult.currencyCode());
        return List.of(row);
    }

    /**
     * @brief 获取已绑定模型（Get Bound Model）；
     *        Get bound model and assert presence.
     *
     * @return 已绑定模型（Bound model）。
     */
    private GenerateStatementModel requireBoundModel() {
        if (model == null) {
            throw new IllegalStateException("model is not bound");
        }
        return model;
    }

    /**
     * @brief 格式化金额（Format Decimal Amount）；
     *        Format decimal amount as plain string.
     *
     * @param amount 金额（Amount）。
     * @return 格式化金额（Formatted amount）。
     */
    private static String formatDecimal(final BigDecimal amount) {
        return Objects.requireNonNull(amount, "amount must not be null").toPlainString();
    }

    /**
     * @brief 格式化日期（Format Local Date）；
     *        Format local date as ISO text.
     *
     * @param date 日期（Date）。
     * @return 日期文本（Date text）。
     */
    private static String formatDate(final LocalDate date) {
        return Objects.requireNonNull(date, "date must not be null").toString();
    }
}
