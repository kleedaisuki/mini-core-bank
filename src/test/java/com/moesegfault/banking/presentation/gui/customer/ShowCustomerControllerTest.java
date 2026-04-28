package com.moesegfault.banking.presentation.gui.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.application.customer.query.FindCustomerHandler;
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
 * @brief 客户详情控制器测试（Show Customer Controller Test），验证查询结果和上下文联动；
 *        Tests for show-customer controller covering query outcomes and context synchronization.
 */
class ShowCustomerControllerTest {

    /**
     * @brief 验证初始化会读取上下文中的当前客户；
     *        Verify init pre-fills model from current customer in context.
     */
    @Test
    void shouldPrefillCustomerIdFromContextOnInit() {
        final GuiContext context = new GuiContext();
        context.setCurrentCustomerId("cust-prefill-001");

        final ShowCustomerModel model = new ShowCustomerModel();
        final ShowCustomerController controller = new ShowCustomerController(
                model,
                new FindCustomerHandler(new InMemoryCustomerRepository()),
                context,
                new GuiExceptionHandler());

        controller.init();

        assertEquals("cust-prefill-001", model.customerIdInput());
    }

    /**
     * @brief 验证查询命中会写入详情并更新上下文客户；
     *        Verify found customer writes detail and updates context current customer.
     */
    @Test
    void shouldPopulateModelAndContextWhenCustomerFound() {
        final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        repository.save(sampleCustomer("cust-found-001", "+8613900000000"));

        final GuiContext context = new GuiContext();
        final ShowCustomerModel model = new ShowCustomerModel();
        final ShowCustomerController controller = new ShowCustomerController(
                model,
                new FindCustomerHandler(repository),
                context,
                new GuiExceptionHandler());

        controller.onViewEvent(submitEvent("cust-found-001"));

        assertTrue(model.customer().isPresent());
        assertEquals("cust-found-001", model.customer().orElseThrow().customerId());
        assertFalse(model.customerNotFound());
        assertEquals("cust-found-001", context.currentCustomerId().orElseThrow());
    }

    /**
     * @brief 验证查询未命中会标记 not-found 并清理上下文客户；
     *        Verify not-found query marks not-found state and clears context current customer.
     */
    @Test
    void shouldMarkNotFoundAndClearContextWhenCustomerMissing() {
        final GuiContext context = new GuiContext();
        context.setCurrentCustomerId("cust-old-001");

        final ShowCustomerModel model = new ShowCustomerModel();
        final ShowCustomerController controller = new ShowCustomerController(
                model,
                new FindCustomerHandler(new InMemoryCustomerRepository()),
                context,
                new GuiExceptionHandler());

        controller.onViewEvent(submitEvent("cust-missing-001"));

        assertTrue(model.customer().isEmpty());
        assertTrue(model.customerNotFound());
        assertTrue(context.currentCustomerId().isEmpty());
    }

    /**
     * @brief 构造提交事件（Build Submit Event）；
     *        Build show-customer submit event.
     *
     * @param customerId 客户 ID（Customer ID）。
     * @return 视图事件（View event）。
     */
    private static ViewEvent submitEvent(final String customerId) {
        return new ViewEvent(
                CustomerGuiEventTypes.SHOW_CUSTOMER_SUBMIT,
                Map.of(CustomerGuiEventTypes.ATTR_FORM_VALUES, Map.of(ShowCustomerModel.FIELD_CUSTOMER_ID, customerId)));
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
