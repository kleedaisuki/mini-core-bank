package com.moesegfault.banking.presentation.gui.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.moesegfault.banking.application.ledger.query.FindBalanceHandler;
import com.moesegfault.banking.application.ledger.query.ListLedgerEntriesHandler;
import com.moesegfault.banking.domain.ledger.LedgerRepository;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiRuntime;
import com.moesegfault.banking.presentation.gui.mvc.GuiModel;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.view.EmptyStateView;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.MainMenuView;
import com.moesegfault.banking.presentation.gui.view.MainWindowView;
import com.moesegfault.banking.presentation.gui.view.StatusBarView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.swing.JPanel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @brief Ledger 页面工厂测试（Ledger Page Factory Test），验证页面创建与挂载契约；
 *        Tests for ledger page factories covering page creation and mount contract.
 */
class LedgerPageFactoryTest {

    /**
     * @brief 验证余额页面工厂创建正确页面并可挂载；
     *        Verify balance page factory creates correct page and can be mounted.
     */
    @Test
    void shouldCreateAndMountShowBalancePage() {
        final LedgerRepository repository = Mockito.mock(LedgerRepository.class);
        final RecordingRuntime runtime = new RecordingRuntime();

        final ShowBalancePageFactory factory = new ShowBalancePageFactory(
                new FindBalanceHandler(repository),
                runtime,
                new GuiExceptionHandler(),
                TestFormView::new,
                TestTableView::new,
                TestEmptyStateView::new);

        final GuiPage page = factory.createPage(new GuiContext());
        assertEquals(LedgerGuiPageIds.SHOW_BALANCE, page.pageId());
        page.mount();

        assertNotNull(runtime.mainWindow.lastContent);
    }

    /**
     * @brief 验证分录页面工厂创建正确页面并可挂载；
     *        Verify entries page factory creates correct page and can be mounted.
     */
    @Test
    void shouldCreateAndMountShowEntriesPage() {
        final LedgerRepository repository = Mockito.mock(LedgerRepository.class);
        final RecordingRuntime runtime = new RecordingRuntime();

        final ShowEntriesPageFactory factory = new ShowEntriesPageFactory(
                new ListLedgerEntriesHandler(repository),
                runtime,
                new GuiExceptionHandler(),
                TestFormView::new,
                TestTableView::new,
                TestEmptyStateView::new);

        final GuiPage page = factory.createPage(new GuiContext());
        assertEquals(LedgerGuiPageIds.SHOW_ENTRIES, page.pageId());
        page.mount();

        assertNotNull(runtime.mainWindow.lastContent);
    }

    /**
     * @brief 记录型运行时（Recording Runtime）；
     *        Recording runtime for mounted-content verification.
     */
    private static final class RecordingRuntime implements GuiRuntime {

        /**
         * @brief 记录型主窗口（Recording Main Window）；
         *        Recording main window view.
         */
        private final RecordingMainWindow mainWindow = new RecordingMainWindow();

        /**
         * {@inheritDoc}
         */
        @Override
        public void start() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void stop() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mount(final GuiView<? extends GuiModel> view) {
            view.mount();
            view.render();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MainWindowView mainWindow() {
            return mainWindow;
        }
    }

    /**
     * @brief 记录型主窗口（Recording Main Window）；
     *        Recording main-window stub.
     */
    private static final class RecordingMainWindow implements MainWindowView {

        /**
         * @brief 最近一次内容组件（Last Content Component）；
         *        Last content component passed into main window.
         */
        private Object lastContent;

        /**
         * {@inheritDoc}
         */
        @Override
        public void setTitle(final String title) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setMainMenu(final MainMenuView mainMenuView) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setStatusBar(final StatusBarView statusBarView) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setContent(final Object content) {
            this.lastContent = content;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void show() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void close() {
        }
    }

    /**
     * @brief 测试表单视图（Test Form View）；
     *        Minimal test form view implementation.
     */
    private static final class TestFormView implements FormView {

        /**
         * @brief 值映射（Value Map）；
         *        Form value map.
         */
        private final Map<String, String> values = new LinkedHashMap<>();

        /**
         * @brief 组件（Component）；
         *        Underlying Swing component.
         */
        private final JPanel panel = new JPanel();

        /**
         * @brief 提交回调（Submit Callback）；
         *        Submit callback.
         */
        private Runnable onSubmit = () -> {
        };

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
            onSubmit = submitAction;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object component() {
            return panel;
        }
    }

    /**
     * @brief 测试表格视图（Test Table View）；
     *        Minimal test table view implementation.
     */
    private static final class TestTableView implements TableView {

        /**
         * @brief 组件（Component）；
         *        Underlying Swing component.
         */
        private final JPanel panel = new JPanel();

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
        public void onRowSelected(final Consumer<Integer> listener) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object component() {
            return panel;
        }
    }

    /**
     * @brief 测试空态视图（Test Empty-state View）；
     *        Minimal test empty-state view implementation.
     */
    private static final class TestEmptyStateView implements EmptyStateView {

        /**
         * @brief 组件（Component）；
         *        Underlying Swing component.
         */
        private final JPanel panel = new JPanel();

        /**
         * {@inheritDoc}
         */
        @Override
        public void setTitle(final String title) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setDescription(final String description) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setAction(final String actionLabel, final Runnable action) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object component() {
            return panel;
        }
    }
}
