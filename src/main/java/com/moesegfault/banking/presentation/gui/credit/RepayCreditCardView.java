package com.moesegfault.banking.presentation.gui.credit;

import com.moesegfault.banking.application.credit.result.CreditCardAccountResult;
import com.moesegfault.banking.application.credit.result.RepayCreditCardResult;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ModelChangeListener;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @brief 信用卡还款页面视图（Repay Credit Card View），桥接还款表单与汇总表格；
 *        View for repay-credit-card page bridging repayment form and summary table rendering.
 */
public final class RepayCreditCardView implements GuiView<RepayCreditCardModel> {

    /**
     * @brief 表单字段顺序（Form Field Order）；
     *        Canonical field order for repayment form.
     */
    private static final List<String> FORM_FIELD_ORDER = List.of(
            RepayCreditCardModel.FIELD_CREDIT_CARD_ACCOUNT_ID,
            RepayCreditCardModel.FIELD_REPAYMENT_AMOUNT,
            RepayCreditCardModel.FIELD_REPAYMENT_CURRENCY_CODE,
            RepayCreditCardModel.FIELD_STATEMENT_ID,
            RepayCreditCardModel.FIELD_SOURCE_ACCOUNT_ID,
            RepayCreditCardModel.FIELD_AS_OF_DATE);

    /**
     * @brief 汇总表格列定义（Summary Table Columns）；
     *        Columns for repayment summary table.
     */
    private static final List<String> SUMMARY_COLUMNS = List.of("item_key", "item_value");

    /**
     * @brief 表单视图（Form View）；
     *        Form view abstraction.
     */
    private final FormView formView;

    /**
     * @brief 汇总表格视图（Summary Table View）；
     *        Table view abstraction for repayment summary.
     */
    private final TableView tableView;

    /**
     * @brief 模型变更监听器（Model-change Listener）；
     *        Listener that triggers re-render.
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
    private RepayCreditCardModel model;

    /**
     * @brief 构造视图（Construct View）；
     *        Construct repay-credit-card view.
     *
     * @param formView 表单视图（Form view）。
     * @param tableView 汇总表格视图（Summary table view）。
     */
    public RepayCreditCardView(final FormView formView, final TableView tableView) {
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
    public void bindModel(final RepayCreditCardModel model) {
        final RepayCreditCardModel normalizedModel = Objects.requireNonNull(model, "model must not be null");
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
        tableView.setColumns(SUMMARY_COLUMNS);
        formView.onSubmit(() -> {
            final Map<String, Object> attributes = new LinkedHashMap<>();
            formView.values().forEach(attributes::put);
            viewEventHandler.accept(new ViewEvent(RepayCreditCardController.EVENT_SUBMIT, attributes));
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
        final RepayCreditCardModel currentModel = requireBoundModel();
        formView.setValues(currentModel.formValues());
        formView.clearErrors();
        currentModel.fieldErrors().forEach(formView::setFieldError);
        tableView.setRows(currentModel.repayResult().map(this::toSummaryRows).orElseGet(List::of));
    }

    /**
     * @brief 转换还款结果为汇总行（Convert Repayment Result To Summary Rows）；
     *        Convert repayment result into summary rows.
     *
     * @param result 还款结果（Repayment result）。
     * @return 汇总表格行（Summary rows）。
     */
    private List<List<String>> toSummaryRows(final RepayCreditCardResult result) {
        final RepayCreditCardResult normalizedResult = Objects.requireNonNull(result, "result must not be null");
        final List<List<String>> rows = new ArrayList<>();
        rows.add(summaryRow("credit_card_account_id", normalizedResult.creditCardAccount().creditCardAccountId()));
        rows.add(summaryRow("applied_to_account_amount", formatDecimal(normalizedResult.appliedToAccountAmount())));
        rows.add(summaryRow("applied_to_statement_amount", formatDecimal(normalizedResult.appliedToStatementAmount())));
        rows.add(summaryRow("unapplied_amount", formatDecimal(normalizedResult.unappliedAmount())));
        rows.add(summaryRow("currency_code", normalizedResult.currencyCode()));
        rows.add(summaryRow("target_statement_id", normalizeNullableText(normalizedResult.statementIdOrNull())));
        rows.add(summaryRow("affected_statements_total", Integer.toString(normalizedResult.affectedStatements().size())));
        appendAccountSnapshotRows(rows, normalizedResult.creditCardAccount());
        return rows;
    }

    /**
     * @brief 追加账户快照行（Append Account Snapshot Rows）；
     *        Append account snapshot fields to summary rows.
     *
     * @param rows 汇总行列表（Summary rows）。
     * @param account 账户快照（Account snapshot）。
     */
    private void appendAccountSnapshotRows(final List<List<String>> rows, final CreditCardAccountResult account) {
        rows.add(summaryRow("account.credit_limit", formatDecimal(account.creditLimit())));
        rows.add(summaryRow("account.available_credit", formatDecimal(account.availableCredit())));
        rows.add(summaryRow("account.used_credit", formatDecimal(account.usedCredit())));
        rows.add(summaryRow("account.cash_advance_limit", formatDecimal(account.cashAdvanceLimit())));
        rows.add(summaryRow("account.billing_cycle_day", Integer.toString(account.billingCycleDay())));
        rows.add(summaryRow("account.payment_due_day", Integer.toString(account.paymentDueDay())));
        rows.add(summaryRow("account.interest_rate_decimal", formatDecimal(account.interestRateDecimal())));
        rows.add(summaryRow("account.currency_code", account.accountCurrencyCode()));
    }

    /**
     * @brief 构造汇总行（Build Summary Row）；
     *        Build one key-value summary row.
     *
     * @param key 键（Key）。
     * @param value 值（Value）。
     * @return 表格行（Table row）。
     */
    private static List<String> summaryRow(final String key, final String value) {
        return List.of(Objects.requireNonNull(key, "key must not be null"), Objects.requireNonNull(value, "value must not be null"));
    }

    /**
     * @brief 获取已绑定模型（Get Bound Model）；
     *        Get bound model and assert presence.
     *
     * @return 已绑定模型（Bound model）。
     */
    private RepayCreditCardModel requireBoundModel() {
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
     * @brief 规范化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text by empty-string fallback.
     *
     * @param value 值（Value）。
     * @return 规范化文本（Normalized text）。
     */
    private static String normalizeNullableText(final String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
}
