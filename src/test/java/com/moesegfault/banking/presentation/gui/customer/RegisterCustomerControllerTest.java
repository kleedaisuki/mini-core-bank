package com.moesegfault.banking.presentation.gui.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.application.customer.command.RegisterCustomerHandler;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.customer.IdentityDocument;
import com.moesegfault.banking.domain.customer.PhoneNumber;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/**
 * @brief 客户注册控制器测试（Register Customer Controller Test），验证 schema 字段映射和应用层调用；
 *        Tests for register-customer controller covering schema mapping and application-service invocation.
 */
class RegisterCustomerControllerTest {

    /**
     * @brief 验证合法输入会完成注册并写入成功消息；
     *        Verify valid input triggers registration and writes success message.
     */
    @Test
    void shouldRegisterCustomerWhenInputValid() {
        final RegisterCustomerModel model = new RegisterCustomerModel();
        final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        final RegisterCustomerHandler handler = new RegisterCustomerHandler(
                repository,
                passthroughTransactionManager(),
                fixedIdGenerator("cust-new-001"));
        final RegisterCustomerController controller = new RegisterCustomerController(
                model,
                handler,
                new GuiExceptionHandler());

        controller.onViewEvent(new ViewEvent(
                CustomerGuiEventTypes.REGISTER_CUSTOMER_SUBMIT,
                Map.of(CustomerGuiEventTypes.ATTR_FORM_VALUES, validRegisterFormValues())));

        assertTrue(model.successMessage().isPresent());
        assertTrue(model.successMessage().orElseThrow().contains("customer_id=cust-new-001"));
        assertTrue(model.lastRegisterResult().isPresent());
        assertEquals("cust-new-001", model.lastRegisterResult().orElseThrow().customerId());
        assertTrue(model.fieldErrors().isEmpty());
        assertFalse(model.submitting());
        assertTrue(repository.findById(CustomerId.of("cust-new-001")).isPresent());
    }

    /**
     * @brief 验证缺失必填字段会返回字段级错误；
     *        Verify missing required fields produce field-level errors.
     */
    @Test
    void shouldExposeFieldErrorsWhenRequiredFieldsMissing() {
        final RegisterCustomerModel model = new RegisterCustomerModel();
        final RegisterCustomerController controller = new RegisterCustomerController(
                model,
                new RegisterCustomerHandler(
                        new InMemoryCustomerRepository(),
                        passthroughTransactionManager(),
                        fixedIdGenerator("cust-new-002")),
                new GuiExceptionHandler());

        controller.onViewEvent(new ViewEvent(
                CustomerGuiEventTypes.REGISTER_CUSTOMER_SUBMIT,
                Map.of(CustomerGuiEventTypes.ATTR_FORM_VALUES, Map.of())));

        assertTrue(model.errorMessage().isPresent());
        assertTrue(model.fieldErrors().containsKey(RegisterCustomerModel.FIELD_ID_TYPE));
        assertTrue(model.fieldErrors().containsKey(RegisterCustomerModel.FIELD_MOBILE_PHONE));
        assertTrue(model.successMessage().isEmpty());
        assertFalse(model.submitting());
    }

    /**
     * @brief 验证布尔字段非法时会标注对应字段错误；
     *        Verify invalid boolean field is mapped to specific field error.
     */
    @Test
    void shouldExposeFieldErrorWhenBooleanValueInvalid() {
        final RegisterCustomerModel model = new RegisterCustomerModel();
        final RegisterCustomerController controller = new RegisterCustomerController(
                model,
                new RegisterCustomerHandler(
                        new InMemoryCustomerRepository(),
                        passthroughTransactionManager(),
                        fixedIdGenerator("cust-new-003")),
                new GuiExceptionHandler());

        final Map<String, String> values = new LinkedHashMap<>(validRegisterFormValues());
        values.put(RegisterCustomerModel.FIELD_IS_US_TAX_RESIDENT, "maybe");
        controller.onViewEvent(new ViewEvent(
                CustomerGuiEventTypes.REGISTER_CUSTOMER_SUBMIT,
                Map.of(CustomerGuiEventTypes.ATTR_FORM_VALUES, values)));

        assertTrue(model.errorMessage().isPresent());
        assertEquals(
                "布尔字段仅支持 true/false/1/0/yes/no/y/n",
                model.fieldErrors().get(RegisterCustomerModel.FIELD_IS_US_TAX_RESIDENT));
        assertTrue(model.successMessage().isEmpty());
    }

    /**
     * @brief 创建合法注册表单（Create Valid Register Form Values）；
     *        Create valid register form map aligned with schema fields.
     *
     * @return 合法表单值（Valid form values）。
     */
    private static Map<String, String> validRegisterFormValues() {
        final Map<String, String> values = new LinkedHashMap<>();
        values.put(RegisterCustomerModel.FIELD_ID_TYPE, "PASSPORT");
        values.put(RegisterCustomerModel.FIELD_ID_NUMBER, "E12345678");
        values.put(RegisterCustomerModel.FIELD_ISSUING_REGION, "CN");
        values.put(RegisterCustomerModel.FIELD_MOBILE_PHONE, "+8613800000000");
        values.put(RegisterCustomerModel.FIELD_RESIDENTIAL_ADDRESS, "Shanghai Pudong");
        values.put(RegisterCustomerModel.FIELD_MAILING_ADDRESS, "Shanghai Minhang");
        values.put(RegisterCustomerModel.FIELD_IS_US_TAX_RESIDENT, "false");
        values.put(RegisterCustomerModel.FIELD_CRS_INFO, "");
        return values;
    }

    /**
     * @brief 固定 ID 生成器（Fixed ID Generator）；
     *        Create fixed-value ID generator.
     *
     * @param fixedId 固定 ID（Fixed ID）。
     * @return ID 生成器（ID generator）。
     */
    private static IdGenerator fixedIdGenerator(final String fixedId) {
        return () -> fixedId;
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
