package com.moesegfault.banking.presentation.gui.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.application.customer.query.ListCustomersHandler;
import com.moesegfault.banking.domain.customer.Address;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.customer.IdentityDocument;
import com.moesegfault.banking.domain.customer.IdentityDocumentType;
import com.moesegfault.banking.domain.customer.PhoneNumber;
import com.moesegfault.banking.domain.customer.TaxProfile;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * @brief 客户列表控制器测试（List Customers Controller Test），验证筛选查询与行选择行为；
 *        Tests for list-customers controller covering filtered query and row-selection behavior.
 */
class ListCustomersControllerTest {

    /**
     * @brief 验证初始化会加载全量客户列表；
     *        Verify init loads all customers.
     */
    @Test
    void shouldLoadAllCustomersOnInit() {
        final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        repository.save(sampleCustomer("cust-a", "+8613800000000"));
        repository.save(sampleCustomer("cust-b", "+8613900000000"));

        final ListCustomersModel model = new ListCustomersModel();
        final ListCustomersController controller = new ListCustomersController(
                model,
                new ListCustomersHandler(repository),
                new GuiContext(),
                new GuiExceptionHandler());

        controller.init();

        assertEquals(2, model.customers().size());
        assertFalse(model.loading());
        assertTrue(model.errorMessage().isEmpty());
    }

    /**
     * @brief 验证手机号筛选事件会收敛到对应客户；
     *        Verify mobile-phone filter event narrows customer result list.
     */
    @Test
    void shouldFilterCustomersByMobilePhone() {
        final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        repository.save(sampleCustomer("cust-a", "+8613800000000"));
        repository.save(sampleCustomer("cust-b", "+8613800000000"));
        repository.save(sampleCustomer("cust-c", "+8613900000000"));

        final ListCustomersModel model = new ListCustomersModel();
        final ListCustomersController controller = new ListCustomersController(
                model,
                new ListCustomersHandler(repository),
                new GuiContext(),
                new GuiExceptionHandler());

        controller.onViewEvent(new ViewEvent(
                CustomerGuiEventTypes.LIST_CUSTOMERS_QUERY,
                Map.of(CustomerGuiEventTypes.ATTR_FORM_VALUES, Map.of(ListCustomersModel.FIELD_MOBILE_PHONE, "+86 13800000000"))));

        assertEquals(2, model.customers().size());
        assertTrue(model.customers().stream().allMatch(customer -> customer.mobilePhone().equals("+8613800000000")));
    }

    /**
     * @brief 验证行选择会同步当前客户到上下文；
     *        Verify row selection synchronizes current customer into context.
     */
    @Test
    void shouldUpdateContextWhenRowSelected() {
        final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        repository.save(sampleCustomer("cust-a", "+8613800000000"));
        repository.save(sampleCustomer("cust-b", "+8613900000000"));

        final GuiContext context = new GuiContext();
        final ListCustomersModel model = new ListCustomersModel();
        final ListCustomersController controller = new ListCustomersController(
                model,
                new ListCustomersHandler(repository),
                context,
                new GuiExceptionHandler());

        controller.init();
        controller.onViewEvent(new ViewEvent(
                CustomerGuiEventTypes.LIST_CUSTOMERS_ROW_SELECTED,
                Map.of(CustomerGuiEventTypes.ATTR_ROW_INDEX, 1)));

        assertEquals(1, model.selectedRowIndex().orElseThrow());
        assertEquals(model.customers().get(1).customerId(), context.currentCustomerId().orElseThrow());
    }

    /**
     * @brief 创建样例客户（Create Sample Customer）；
     *        Create one sample customer.
     *
     * @param customerId 客户 ID（Customer ID）。
     * @param mobilePhone 手机号（Mobile phone）。
     * @return 客户实体（Customer entity）。
     */
    private static Customer sampleCustomer(final String customerId, final String mobilePhone) {
        return Customer.register(
                CustomerId.of(customerId),
                IdentityDocument.of(IdentityDocumentType.PASSPORT, "E-" + customerId, "CN"),
                PhoneNumber.of(mobilePhone),
                Address.of("Shanghai Pudong"),
                Address.of("Shanghai Pudong"),
                TaxProfile.of(false, (String) null));
    }

    /**
     * @brief 内存客户仓储（In-memory Customer Repository）；
     *        In-memory customer repository for controller unit tests.
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
}
