package com.moesegfault.banking.presentation.gui.credit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moesegfault.banking.application.credit.command.GenerateStatementCommand;
import com.moesegfault.banking.application.credit.command.GenerateStatementHandler;
import com.moesegfault.banking.application.credit.command.RepayCreditCardCommand;
import com.moesegfault.banking.application.credit.command.RepayCreditCardHandler;
import com.moesegfault.banking.application.credit.query.FindStatementHandler;
import com.moesegfault.banking.application.credit.query.FindStatementQuery;
import com.moesegfault.banking.application.credit.result.CreditCardAccountResult;
import com.moesegfault.banking.application.credit.result.CreditCardStatementResult;
import com.moesegfault.banking.application.credit.result.RepayCreditCardResult;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageRegistrar;
import com.moesegfault.banking.presentation.gui.GuiPageRegistry;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @brief Credit GUI 展示层测试（Credit GUI Presentation Test），覆盖控制器命令映射和模块注册；
 *        Tests for credit GUI presentation layer covering controller command mapping and module registration.
 */
class CreditGuiPresentationTest {

    /**
     * @brief 验证生成账单控制器映射 canonical schema 并调用应用层；
     *        Verify generate-statement controller maps canonical schema and invokes application handler.
     */
    @Test
    void shouldMapGenerateStatementFormToApplicationCommand() {
        final GenerateStatementHandler applicationHandler = Mockito.mock(GenerateStatementHandler.class);
        when(applicationHandler.handle(any(GenerateStatementCommand.class))).thenReturn(new CreditCardStatementResult(
                "stmt-001",
                "cc-acc-001",
                LocalDate.parse("2026-04-01"),
                LocalDate.parse("2026-04-30"),
                LocalDate.parse("2026-05-01"),
                LocalDate.parse("2026-05-21"),
                new BigDecimal("1000.00"),
                new BigDecimal("100.00"),
                new BigDecimal("0.00"),
                new BigDecimal("1000.00"),
                "GENERATED",
                "CNY"));

        final GenerateStatementModel model = new GenerateStatementModel();
        final GenerateStatementController controller = new GenerateStatementController(
                applicationHandler,
                new GuiExceptionHandler(),
                model);

        controller.onViewEvent(new ViewEvent(
                GenerateStatementController.EVENT_SUBMIT,
                Map.of(
                        GenerateStatementModel.FIELD_CREDIT_CARD_ACCOUNT_ID, "cc-acc-001",
                        GenerateStatementModel.FIELD_STATEMENT_DATE, "2026-05-01",
                        GenerateStatementModel.FIELD_MINIMUM_PAYMENT_RATE_DECIMAL, "0.1",
                        GenerateStatementModel.FIELD_MINIMUM_PAYMENT_FLOOR_AMOUNT, "100")));

        final ArgumentCaptor<GenerateStatementCommand> commandCaptor = ArgumentCaptor.forClass(GenerateStatementCommand.class);
        verify(applicationHandler).handle(commandCaptor.capture());
        final GenerateStatementCommand command = commandCaptor.getValue();
        assertEquals("cc-acc-001", command.creditCardAccountId());
        assertEquals(LocalDate.parse("2026-05-01"), command.statementDate());
        assertEquals(new BigDecimal("0.1"), command.minimumPaymentRateDecimal());
        assertEquals(new BigDecimal("100"), command.minimumPaymentFloorAmount());
        assertTrue(model.statementResult().isPresent());
    }

    /**
     * @brief 验证无效日期会触发字段错误并阻断应用层调用；
     *        Verify invalid date creates field error and blocks application invocation.
     */
    @Test
    void shouldRejectGenerateStatementWhenDateInvalid() {
        final GenerateStatementHandler applicationHandler = Mockito.mock(GenerateStatementHandler.class);
        final GenerateStatementModel model = new GenerateStatementModel();
        final GenerateStatementController controller = new GenerateStatementController(
                applicationHandler,
                new GuiExceptionHandler(),
                model);

        controller.onViewEvent(new ViewEvent(
                GenerateStatementController.EVENT_SUBMIT,
                Map.of(
                        GenerateStatementModel.FIELD_CREDIT_CARD_ACCOUNT_ID, "cc-acc-001",
                        GenerateStatementModel.FIELD_STATEMENT_DATE, "2026/05/01",
                        GenerateStatementModel.FIELD_MINIMUM_PAYMENT_RATE_DECIMAL, "0.1",
                        GenerateStatementModel.FIELD_MINIMUM_PAYMENT_FLOOR_AMOUNT, "100")));

        verify(applicationHandler, never()).handle(any(GenerateStatementCommand.class));
        assertTrue(model.fieldErrors().containsKey(GenerateStatementModel.FIELD_STATEMENT_DATE));
    }

    /**
     * @brief 验证还款控制器映射 canonical schema 并调用应用层；
     *        Verify repay controller maps canonical schema and invokes application handler.
     */
    @Test
    void shouldMapRepayFormToApplicationCommand() {
        final RepayCreditCardHandler applicationHandler = Mockito.mock(RepayCreditCardHandler.class);
        when(applicationHandler.handle(any(RepayCreditCardCommand.class))).thenReturn(new RepayCreditCardResult(
                new CreditCardAccountResult(
                        "cc-acc-001",
                        new BigDecimal("20000"),
                        new BigDecimal("18000"),
                        new BigDecimal("2000"),
                        new BigDecimal("6000"),
                        1,
                        21,
                        new BigDecimal("0.001"),
                        "CNY"),
                new BigDecimal("2000"),
                new BigDecimal("2000"),
                new BigDecimal("0"),
                "CNY",
                null,
                List.of()));

        final RepayCreditCardModel model = new RepayCreditCardModel();
        final RepayCreditCardController controller = new RepayCreditCardController(
                applicationHandler,
                new GuiExceptionHandler(),
                model);

        controller.onViewEvent(new ViewEvent(
                RepayCreditCardController.EVENT_SUBMIT,
                Map.of(
                        RepayCreditCardModel.FIELD_CREDIT_CARD_ACCOUNT_ID, "cc-acc-001",
                        RepayCreditCardModel.FIELD_REPAYMENT_AMOUNT, "2000",
                        RepayCreditCardModel.FIELD_REPAYMENT_CURRENCY_CODE, "CNY",
                        RepayCreditCardModel.FIELD_STATEMENT_ID, "",
                        RepayCreditCardModel.FIELD_SOURCE_ACCOUNT_ID, "sav-001",
                        RepayCreditCardModel.FIELD_AS_OF_DATE, "2026-05-03")));

        final ArgumentCaptor<RepayCreditCardCommand> commandCaptor = ArgumentCaptor.forClass(RepayCreditCardCommand.class);
        verify(applicationHandler).handle(commandCaptor.capture());
        final RepayCreditCardCommand command = commandCaptor.getValue();
        assertEquals("cc-acc-001", command.creditCardAccountId());
        assertEquals(new BigDecimal("2000"), command.repaymentAmount());
        assertEquals("CNY", command.repaymentCurrencyCode());
        assertEquals("sav-001", command.sourceAccountIdOrNull());
        assertEquals(LocalDate.parse("2026-05-03"), command.asOfDateOrNull());
        assertTrue(model.repayResult().isPresent());
    }

    /**
     * @brief 验证账单查询控制器按 statement_id 模式调用应用层；
     *        Verify show-statement controller invokes application handler in statement-id mode.
     */
    @Test
    void shouldQueryStatementByStatementId() {
        final FindStatementHandler applicationHandler = Mockito.mock(FindStatementHandler.class);
        when(applicationHandler.handle(any(FindStatementQuery.class))).thenReturn(Optional.of(new CreditCardStatementResult(
                "stmt-001",
                "cc-acc-001",
                LocalDate.parse("2026-04-01"),
                LocalDate.parse("2026-04-30"),
                LocalDate.parse("2026-05-01"),
                LocalDate.parse("2026-05-21"),
                new BigDecimal("1000.00"),
                new BigDecimal("100.00"),
                new BigDecimal("0.00"),
                new BigDecimal("1000.00"),
                "GENERATED",
                "CNY")));

        final ShowStatementModel model = new ShowStatementModel();
        final ShowStatementController controller = new ShowStatementController(
                applicationHandler,
                new GuiExceptionHandler(),
                model);

        controller.onViewEvent(new ViewEvent(
                ShowStatementController.EVENT_SUBMIT,
                Map.of(
                        ShowStatementModel.FIELD_STATEMENT_ID, "stmt-001",
                        ShowStatementModel.FIELD_CREDIT_CARD_ACCOUNT_ID, "",
                        ShowStatementModel.FIELD_STATEMENT_PERIOD_START, "",
                        ShowStatementModel.FIELD_STATEMENT_PERIOD_END, "")));

        verify(applicationHandler).handle(any(FindStatementQuery.class));
        assertTrue(model.statementResult().isPresent());
    }

    /**
     * @brief 验证 credit GUI 模块完成 3 个页面注册；
     *        Verify credit GUI module registers three expected pages.
     */
    @Test
    void shouldRegisterCreditPagesViaGuiModule() {
        final GenerateStatementPageFactory generateStatementPageFactory = new GenerateStatementPageFactory(
                Mockito.mock(GenerateStatementHandler.class),
                new GuiExceptionHandler(),
                NoopFormView::new,
                NoopTableView::new);
        final RepayCreditCardPageFactory repayCreditCardPageFactory = new RepayCreditCardPageFactory(
                Mockito.mock(RepayCreditCardHandler.class),
                new GuiExceptionHandler(),
                NoopFormView::new,
                NoopTableView::new);
        final ShowStatementPageFactory showStatementPageFactory = new ShowStatementPageFactory(
                Mockito.mock(FindStatementHandler.class),
                new GuiExceptionHandler(),
                NoopFormView::new,
                NoopTableView::new);
        final CreditGuiModule module = new CreditGuiModule(
                generateStatementPageFactory,
                repayCreditCardPageFactory,
                showStatementPageFactory);
        final GuiPageRegistry pageRegistry = new GuiPageRegistry();
        final GuiPageRegistrar registrar = new GuiPageRegistrar(pageRegistry);

        module.registerPages(registrar);

        assertTrue(pageRegistry.findPageFactory(CreditGuiPageIds.GENERATE_STATEMENT).isPresent());
        assertTrue(pageRegistry.findPageFactory(CreditGuiPageIds.REPAY_CREDIT_CARD).isPresent());
        assertTrue(pageRegistry.findPageFactory(CreditGuiPageIds.SHOW_STATEMENT).isPresent());
        final GuiPage page = pageRegistry.findPageFactory(CreditGuiPageIds.SHOW_STATEMENT)
                .orElseThrow()
                .createPage(new com.moesegfault.banking.presentation.gui.GuiContext());
        assertEquals(CreditGuiPageIds.SHOW_STATEMENT, page.pageId());
    }

    /**
     * @brief 空实现表单视图（No-op Form View）；
     *        No-op form view for page-factory tests.
     */
    private static final class NoopFormView implements FormView {

        /**
         * @brief 当前字段值（Current Values）；
         *        Current field values.
         */
        private final Map<String, String> values = new LinkedHashMap<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void setFieldOrder(final List<String> fieldNames) {
            values.clear();
            for (String fieldName : fieldNames) {
                values.put(fieldName, "");
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setValues(final Map<String, String> values) {
            this.values.clear();
            this.values.putAll(values);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<String, String> values() {
            return Map.copyOf(values);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setFieldError(final String fieldName, final String message) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clearErrors() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onSubmit(final Runnable submitAction) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object component() {
            return this;
        }
    }

    /**
     * @brief 空实现表格视图（No-op Table View）；
     *        No-op table view for page-factory tests.
     */
    private static final class NoopTableView implements TableView {

        /**
         * {@inheritDoc}
         */
        @Override
        public void setColumns(final List<String> columns) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setRows(final List<List<String>> rows) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<Integer> selectedRowIndex() {
            return Optional.empty();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onRowSelected(final java.util.function.Consumer<Integer> listener) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object component() {
            return this;
        }
    }
}
