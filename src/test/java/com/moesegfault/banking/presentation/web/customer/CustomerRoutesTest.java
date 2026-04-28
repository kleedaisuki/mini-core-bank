package com.moesegfault.banking.presentation.web.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.moesegfault.banking.application.customer.command.RegisterCustomerHandler;
import com.moesegfault.banking.application.customer.query.FindCustomerHandler;
import com.moesegfault.banking.application.customer.query.ListCustomersHandler;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.customer.IdentityDocument;
import com.moesegfault.banking.domain.customer.PhoneNumber;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import com.moesegfault.banking.presentation.web.WebRouteHandler;
import com.moesegfault.banking.presentation.web.WebRuntime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/**
 * @brief 客户路由注册测试（Customer Route Registration Test），验证 /customers 路由注册完整性；
 *        Customer-route registration tests verifying completeness of /customers route bindings.
 */
class CustomerRoutesTest {

    /**
     * @brief 验证注册器会注册三条客户路由；
     *        Verify registrar registers three customer routes.
     */
    @Test
    void shouldRegisterAllCustomerRoutes() {
        final RecordingRuntime runtime = new RecordingRuntime();
        final CustomerRoutes routes = new CustomerRoutes(newController());

        routes.registerRoutes(runtime);

        assertEquals(3, runtime.routes().size());
        assertEquals("POST /customers", runtime.routes().get(0).key());
        assertEquals("GET /customers", runtime.routes().get(1).key());
        assertEquals("GET /customers/{customerId}", runtime.routes().get(2).key());
    }

    /**
     * @brief 构造控制器（Build Controller）；
     *        Build controller with lightweight in-memory dependencies.
     *
     * @return 客户控制器（Customer controller）。
     */
    private static CustomerController newController() {
        final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        final DbTransactionManager transactionManager = new DbTransactionManager() {
            @Override
            public <T> T execute(final Supplier<T> action) {
                return action.get();
            }
        };

        final RegisterCustomerHandler registerHandler = new RegisterCustomerHandler(
                repository,
                transactionManager,
                () -> "cust-route-test");
        final FindCustomerHandler findHandler = new FindCustomerHandler(repository);
        final ListCustomersHandler listHandler = new ListCustomersHandler(repository);
        return new CustomerController(registerHandler, findHandler, listHandler);
    }

    /**
     * @brief 记录型 Runtime（Recording Runtime）；
     *        Recording runtime for route-registration assertions.
     */
    private static final class RecordingRuntime implements WebRuntime {

        /**
         * @brief 路由条目（Route Entries）；
         *        Collected route entries.
         */
        private final List<RouteEntry> routes = new ArrayList<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void addRoute(final String method, final String pathPattern, final WebRouteHandler handler) {
            routes.add(new RouteEntry(method, pathPattern, handler));
        }

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
         * @brief 获取路由快照（Get Route Snapshot）；
         *        Get immutable route snapshot.
         *
         * @return 路由列表（Route list）。
         */
        List<RouteEntry> routes() {
            return List.copyOf(routes);
        }
    }

    /**
     * @brief 路由条目（Route Entry）；
     *        Route entry record.
     *
     * @param method HTTP 方法（HTTP method）。
     * @param pathPattern 路径模式（Path pattern）。
     * @param handler 路由处理器（Route handler）。
     */
    private record RouteEntry(String method, String pathPattern, WebRouteHandler handler) {

        /**
         * @brief 生成路由键（Build Route Key）；
         *        Build route key by method and path pattern.
         *
         * @return 路由键（Route key）。
         */
        private String key() {
            return method + " " + pathPattern;
        }
    }

    /**
     * @brief 内存客户仓储（In-memory Customer Repository）；
     *        In-memory customer repository for route-registration tests.
     */
    private static final class InMemoryCustomerRepository implements CustomerRepository {

        /**
         * @brief 客户映射（Customer Mapping）；
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
