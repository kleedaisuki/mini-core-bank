package com.moesegfault.banking.application.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.application.customer.command.RegisterCustomerCommand;
import com.moesegfault.banking.application.customer.command.RegisterCustomerHandler;
import com.moesegfault.banking.application.customer.result.RegisterCustomerResult;
import com.moesegfault.banking.domain.customer.Address;
import com.moesegfault.banking.domain.customer.CrsInfo;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.customer.CustomerRegistered;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.customer.IdentityDocument;
import com.moesegfault.banking.domain.customer.IdentityDocumentType;
import com.moesegfault.banking.domain.customer.PhoneNumber;
import com.moesegfault.banking.domain.customer.TaxProfile;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.DomainEvent;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/**
 * @brief RegisterCustomerHandler 单元测试（Unit Test），验证注册编排、去重、事务与事件；
 *        RegisterCustomerHandler unit tests covering orchestration, uniqueness, transaction, and events.
 */
class RegisterCustomerHandlerTest {

    /**
     * @brief 验证成功注册会写仓储、发布事件并返回结果；
     *        Verify successful registration writes repository, publishes event, and returns result.
     */
    @Test
    void shouldRegisterCustomerAndPublishEvent() {
        final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        final TrackingTransactionManager transactionManager = new TrackingTransactionManager();
        final IdGenerator idGenerator = () -> "cust-new-001";
        final CapturingDomainEventPublisher eventPublisher = new CapturingDomainEventPublisher();

        final RegisterCustomerHandler handler = new RegisterCustomerHandler(
                repository,
                transactionManager,
                idGenerator,
                eventPublisher);

        final RegisterCustomerResult result = handler.handle(new RegisterCustomerCommand(
                IdentityDocumentType.PASSPORT,
                "E1234567",
                "CN",
                "+86 13800000000",
                "Shanghai Pudong",
                "Shanghai Pudong",
                false,
                null));

        assertEquals("cust-new-001", result.customerId());
        assertEquals("ACTIVE", result.customerStatus());
        assertEquals(1, transactionManager.executedCount());
        assertTrue(repository.findById(CustomerId.of("cust-new-001")).isPresent());
        assertEquals(1, eventPublisher.events().size());
        assertInstanceOf(CustomerRegistered.class, eventPublisher.events().get(0));
    }

    /**
     * @brief 验证同证件重复注册会被拒绝；
     *        Verify duplicate registration by the same identity document is rejected.
     */
    @Test
    void shouldRejectDuplicateIdentityDocument() {
        final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        repository.save(Customer.register(
                CustomerId.of("cust-existing-001"),
                IdentityDocument.of(IdentityDocumentType.ID_CARD, "110101199901010011", "CN"),
                PhoneNumber.of("+8613900000000"),
                Address.of("Beijing Haidian"),
                Address.of("Beijing Chaoyang"),
                TaxProfile.of(true, CrsInfo.of("TIN: CN-001"))));

        final RegisterCustomerHandler handler = new RegisterCustomerHandler(
                repository,
                new DbTransactionManager() {
                    @Override
                    public <T> T execute(final Supplier<T> action) {
                        return action.get();
                    }
                },
                () -> "cust-new-002",
                DomainEventPublisher.noop());

        assertThrows(
                BusinessRuleViolation.class,
                () -> handler.handle(new RegisterCustomerCommand(
                        IdentityDocumentType.ID_CARD,
                        "110101199901010011",
                        "CN",
                        "+8613800000000",
                        "Beijing Haidian",
                        "Beijing Chaoyang",
                        true,
                        "TIN: CN-002")));
    }

    /**
     * @brief 内存客户仓储（In-memory Customer Repository）；
     *        In-memory customer repository for application-layer unit tests.
     */
    private static final class InMemoryCustomerRepository implements CustomerRepository {

        /**
         * @brief 按客户 ID 保存客户（Customers by ID）；
         *        Customer map indexed by customer ID.
         */
        private final Map<CustomerId, Customer> customers = new LinkedHashMap<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void save(final Customer customer) {
            customers.put(customer.customerId(), customer);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<Customer> findById(final CustomerId customerId) {
            return Optional.ofNullable(customers.get(customerId));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<Customer> findByIdentityDocument(final IdentityDocument identityDocument) {
            return customers.values()
                    .stream()
                    .filter(customer -> customer.identityDocument().equals(identityDocument))
                    .findFirst();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean existsByIdentityDocument(final IdentityDocument identityDocument) {
            return findByIdentityDocument(identityDocument).isPresent();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<Customer> findByMobilePhone(final PhoneNumber mobilePhone) {
            return customers.values()
                    .stream()
                    .filter(customer -> customer.mobilePhone().equals(mobilePhone))
                    .toList();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<Customer> findAll() {
            return List.copyOf(customers.values());
        }
    }

    /**
     * @brief 事务调用追踪器（Transaction Invocation Tracker）；
     *        Transaction manager test double tracking invocation count.
     */
    private static final class TrackingTransactionManager implements DbTransactionManager {

        /**
         * @brief 执行次数（Execution Count）；
         *        Number of executed transaction actions.
         */
        private int executedCount;

        /**
         * {@inheritDoc}
         */
        @Override
        public <T> T execute(final Supplier<T> action) {
            executedCount++;
            return action.get();
        }

        /**
         * @brief 返回执行次数（Return Execution Count）；
         *        Return execution count.
         *
         * @return 执行次数（Execution count）。
         */
        public int executedCount() {
            return executedCount;
        }
    }

    /**
     * @brief 领域事件捕获器（Domain Event Capturer）；
     *        Domain-event publisher test double capturing published events.
     */
    private static final class CapturingDomainEventPublisher implements DomainEventPublisher {

        /**
         * @brief 已发布事件列表（Published Events）；
         *        List of published events.
         */
        private final List<DomainEvent> events = new ArrayList<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void publish(final DomainEvent event) {
            events.add(event);
        }

        /**
         * @brief 返回已发布事件（Return Published Events）；
         *        Return published events.
         *
         * @return 事件列表（Event list）。
         */
        public List<DomainEvent> events() {
            return events;
        }
    }
}
