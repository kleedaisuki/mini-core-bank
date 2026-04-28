package com.moesegfault.banking.application.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.application.customer.query.FindCustomerHandler;
import com.moesegfault.banking.application.customer.query.FindCustomerQuery;
import com.moesegfault.banking.application.customer.query.ListCustomersHandler;
import com.moesegfault.banking.application.customer.query.ListCustomersQuery;
import com.moesegfault.banking.application.customer.result.CustomerResult;
import com.moesegfault.banking.domain.customer.Address;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.customer.IdentityDocument;
import com.moesegfault.banking.domain.customer.IdentityDocumentType;
import com.moesegfault.banking.domain.customer.PhoneNumber;
import com.moesegfault.banking.domain.customer.TaxProfile;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * @brief 客户查询处理器单元测试（Customer Query Handlers Unit Test），覆盖 find/list 映射逻辑；
 *        Unit tests for customer query handlers covering find/list mapping logic.
 */
class CustomerQueryHandlersTest {

    /**
     * @brief 验证可按 customer_id 查询客户；
     *        Verify customer can be found by customer_id.
     */
    @Test
    void shouldFindCustomerById() {
        final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        repository.save(sampleCustomer("cust-001", "+8613800000000"));

        final FindCustomerHandler handler = new FindCustomerHandler(repository);
        final Optional<CustomerResult> result = handler.handle(new FindCustomerQuery("cust-001"));

        assertTrue(result.isPresent());
        assertEquals("cust-001", result.orElseThrow().customerId());
        assertEquals("PASSPORT", result.orElseThrow().idType());
    }

    /**
     * @brief 验证查询不存在客户返回 empty；
     *        Verify querying non-existing customer returns empty.
     */
    @Test
    void shouldReturnEmptyWhenCustomerNotFound() {
        final FindCustomerHandler handler = new FindCustomerHandler(new InMemoryCustomerRepository());
        final Optional<CustomerResult> result = handler.handle(new FindCustomerQuery("cust-missing"));

        assertFalse(result.isPresent());
    }

    /**
     * @brief 验证列表查询支持全量与手机号过滤；
     *        Verify list query supports all-customers and mobile-phone filtering.
     */
    @Test
    void shouldListCustomersWithOrWithoutMobileFilter() {
        final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        repository.save(sampleCustomer("cust-a", "+8613800000000"));
        repository.save(sampleCustomer("cust-b", "+8613800000000"));
        repository.save(sampleCustomer("cust-c", "+8613900000000"));

        final ListCustomersHandler handler = new ListCustomersHandler(repository);
        final List<CustomerResult> allCustomers = handler.handle(ListCustomersQuery.all());
        final List<CustomerResult> filteredCustomers = handler.handle(
                ListCustomersQuery.byMobilePhone("+86 13800000000"));

        assertEquals(3, allCustomers.size());
        assertEquals(2, filteredCustomers.size());
        assertTrue(filteredCustomers.stream().allMatch(customer -> customer.mobilePhone().equals("+8613800000000")));
    }

    /**
     * @brief 创建样例客户（Create Sample Customer）；
     *        Create a sample customer for query tests.
     *
     * @param customerId  客户 ID（Customer ID）。
     * @param mobilePhone 手机号（Mobile phone）。
     * @return 客户实体（Customer entity）。
     */
    private static Customer sampleCustomer(final String customerId, final String mobilePhone) {
        return Customer.register(
                CustomerId.of(customerId),
                IdentityDocument.of(IdentityDocumentType.PASSPORT, "E" + customerId, "CN"),
                PhoneNumber.of(mobilePhone),
                Address.of("Shanghai Pudong"),
                Address.of("Shanghai Minhang"),
                TaxProfile.of(false, (String) null));
    }

    /**
     * @brief 内存客户仓储（In-memory Customer Repository）；
     *        In-memory customer repository for query-handler tests.
     */
    private static final class InMemoryCustomerRepository implements CustomerRepository {

        /**
         * @brief 客户存储映射（Customer Storage Map）；
         *        Customer map keyed by customer ID.
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
}
