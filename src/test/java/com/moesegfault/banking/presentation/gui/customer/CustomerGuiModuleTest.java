package com.moesegfault.banking.presentation.gui.customer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.application.customer.command.RegisterCustomerHandler;
import com.moesegfault.banking.application.customer.query.FindCustomerHandler;
import com.moesegfault.banking.application.customer.query.ListCustomersHandler;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.customer.IdentityDocument;
import com.moesegfault.banking.domain.customer.PhoneNumber;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPageRegistrar;
import com.moesegfault.banking.presentation.gui.GuiPageRegistry;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/**
 * @brief 客户 GUI 模块测试（Customer GUI Module Test），验证 customer 页面注册；
 *        Tests for customer GUI module page registration.
 */
class CustomerGuiModuleTest {

    /**
     * @brief 验证模块会注册三张 customer 页面；
     *        Verify module registers three customer pages.
     */
    @Test
    void shouldRegisterAllCustomerPages() {
        final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        final CustomerGuiModule module = new CustomerGuiModule(
                new RegisterCustomerHandler(repository, passthroughTransactionManager(), () -> "cust-001"),
                new FindCustomerHandler(repository),
                new ListCustomersHandler(repository),
                NoopFormView::new,
                NoopTableView::new,
                new GuiExceptionHandler());

        final GuiPageRegistry registry = new GuiPageRegistry();
        module.registerPages(new GuiPageRegistrar(registry));

        assertTrue(registry.findPageFactory(CustomerGuiPageIds.REGISTER_CUSTOMER).isPresent());
        assertTrue(registry.findPageFactory(CustomerGuiPageIds.SHOW_CUSTOMER).isPresent());
        assertTrue(registry.findPageFactory(CustomerGuiPageIds.LIST_CUSTOMERS).isPresent());
    }

    /**
     * @brief 直通事务管理器（Pass-through Transaction Manager）；
     *        Build transaction manager that executes action directly.
     *
     * @return 事务管理器（Transaction manager）。
     */
    private static DbTransactionManager passthroughTransactionManager() {
        return new DbTransactionManager() {
            /** {@inheritDoc} */
            @Override
            public <T> T execute(final Supplier<T> action) {
                return action.get();
            }
        };
    }

    /**
     * @brief 内存客户仓储（In-memory Customer Repository）；
     *        In-memory customer repository for module test.
     */
    private static final class InMemoryCustomerRepository implements CustomerRepository {

        /**
         * @brief 客户映射（Customer Map）；
         *        Customer map keyed by customer ID.
         */
        private final Map<CustomerId, Customer> customers = new LinkedHashMap<>();

        /** {@inheritDoc} */
        @Override
        public void save(final Customer customer) {
            customers.put(customer.customerId(), customer);
        }

        /** {@inheritDoc} */
        @Override
        public Optional<Customer> findById(final CustomerId customerId) {
            return Optional.ofNullable(customers.get(customerId));
        }

        /** {@inheritDoc} */
        @Override
        public Optional<Customer> findByIdentityDocument(final IdentityDocument identityDocument) {
            return customers.values().stream().filter(customer -> customer.identityDocument().equals(identityDocument)).findFirst();
        }

        /** {@inheritDoc} */
        @Override
        public boolean existsByIdentityDocument(final IdentityDocument identityDocument) {
            return findByIdentityDocument(identityDocument).isPresent();
        }

        /** {@inheritDoc} */
        @Override
        public List<Customer> findByMobilePhone(final PhoneNumber mobilePhone) {
            return customers.values().stream().filter(customer -> customer.mobilePhone().equals(mobilePhone)).toList();
        }

        /** {@inheritDoc} */
        @Override
        public List<Customer> findAll() {
            return List.copyOf(customers.values());
        }
    }

    /**
     * @brief 空表单视图（No-op Form View）；
     *        No-op form view used for module wiring tests.
     */
    private static final class NoopFormView implements FormView {

        /** @brief 字段值映射（Field Values）；Field-value map. */
        private Map<String, String> values = Map.of();

        /** {@inheritDoc} */
        @Override
        public void setFieldOrder(final List<String> fieldNames) {
        }

        /** {@inheritDoc} */
        @Override
        public void setValues(final Map<String, String> values) {
            this.values = Map.copyOf(values);
        }

        /** {@inheritDoc} */
        @Override
        public Map<String, String> values() {
            return values;
        }

        /** {@inheritDoc} */
        @Override
        public void setFieldError(final String fieldName, final String message) {
        }

        /** {@inheritDoc} */
        @Override
        public void clearErrors() {
        }

        /** {@inheritDoc} */
        @Override
        public void onSubmit(final Runnable submitAction) {
        }

        /** {@inheritDoc} */
        @Override
        public Object component() {
            return new Object();
        }
    }

    /**
     * @brief 空表格视图（No-op Table View）；
     *        No-op table view used for module wiring tests.
     */
    private static final class NoopTableView implements TableView {

        /** {@inheritDoc} */
        @Override
        public void setColumns(final List<String> columns) {
        }

        /** {@inheritDoc} */
        @Override
        public void setRows(final List<List<String>> rows) {
        }

        /** {@inheritDoc} */
        @Override
        public Optional<Integer> selectedRowIndex() {
            return Optional.empty();
        }

        /** {@inheritDoc} */
        @Override
        public void onRowSelected(final java.util.function.Consumer<Integer> listener) {
        }

        /** {@inheritDoc} */
        @Override
        public Object component() {
            return new Object();
        }
    }
}
