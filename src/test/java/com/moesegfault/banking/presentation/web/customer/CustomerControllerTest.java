package com.moesegfault.banking.presentation.web.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moesegfault.banking.application.customer.command.RegisterCustomerHandler;
import com.moesegfault.banking.application.customer.query.FindCustomerHandler;
import com.moesegfault.banking.application.customer.query.ListCustomersHandler;
import com.moesegfault.banking.domain.customer.Address;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.customer.IdentityDocument;
import com.moesegfault.banking.domain.customer.IdentityDocumentType;
import com.moesegfault.banking.domain.customer.PhoneNumber;
import com.moesegfault.banking.domain.customer.TaxProfile;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import com.moesegfault.banking.presentation.web.WebRequest;
import com.moesegfault.banking.presentation.web.WebResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/**
 * @brief 客户 Web 控制器测试（Customer Web Controller Test），验证 REST 输入输出契约与核心行为；
 *        Customer web-controller tests verifying REST request/response contract and core behavior.
 */
class CustomerControllerTest {

    /**
     * @brief JSON 解析器（JSON Parser）；
     *        JSON parser used by assertions.
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

    /**
     * @brief 验证注册客户接口返回 201 与完整客户响应；
     *        Verify register-customer API returns HTTP 201 and full customer response payload.
     *
     * @throws Exception JSON 解析异常（JSON parsing exception）。
     */
    @Test
    void shouldRegisterCustomerAndReturnCreatedResource() throws Exception {
        final CustomerController controller = newController(new InMemoryCustomerRepository(), () -> "cust-web-001");
        final FakeRequest request = new FakeRequest(
                "POST",
                "/customers",
                Map.of(),
                Map.of(),
                """
                        {
                          "id_type":"PASSPORT",
                          "id_number":"E12345678",
                          "issuing_region":"CN",
                          "mobile_phone":"+86 13800000000",
                          "residential_address":"Shanghai Pudong",
                          "mailing_address":"Shanghai Pudong",
                          "is_us_tax_resident":false,
                          "crs_info":"TIN: CN-100"
                        }
                        """);

        final WebResponse response = controller.registerCustomer(request);
        final JsonNode body = OBJECT_MAPPER.readTree(new String(response.body(), StandardCharsets.UTF_8));

        assertEquals(201, response.statusCode());
        assertEquals("/customers/cust-web-001", response.headers().get("Location").get(0));
        assertEquals("cust-web-001", body.get("customer_id").asText());
        assertEquals("PASSPORT", body.get("id_type").asText());
        assertEquals("ACTIVE", body.get("customer_status").asText());
    }

    /**
     * @brief 验证客户详情接口返回 200；
     *        Verify customer-detail API returns HTTP 200 for existing customer.
     *
     * @throws Exception JSON 解析异常（JSON parsing exception）。
     */
    @Test
    void shouldReturnCustomerDetailByCustomerId() throws Exception {
        final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        repository.save(sampleCustomer("cust-show-001", "+8613600000000", "E-CUST-SHOW"));
        final CustomerController controller = newController(repository, () -> "unused-id");

        final FakeRequest request = new FakeRequest(
                "GET",
                "/customers/cust-show-001",
                Map.of("customerId", "cust-show-001"),
                Map.of(),
                "");

        final WebResponse response = controller.findCustomer(request);
        final JsonNode body = OBJECT_MAPPER.readTree(new String(response.body(), StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());
        assertEquals("cust-show-001", body.get("customer_id").asText());
        assertEquals("+8613600000000", body.get("mobile_phone").asText());
    }

    /**
     * @brief 验证客户不存在时抛出 NotFound 异常；
     *        Verify missing customer raises not-found exception.
     */
    @Test
    void shouldThrowCustomerNotFoundExceptionWhenCustomerMissing() {
        final CustomerController controller = newController(new InMemoryCustomerRepository(), () -> "unused-id");
        final FakeRequest request = new FakeRequest(
                "GET",
                "/customers/cust-missing",
                Map.of("customerId", "cust-missing"),
                Map.of(),
                "");

        assertThrows(CustomerNotFoundException.class, () -> controller.findCustomer(request));
    }

    /**
     * @brief 验证列表接口支持过滤、分页与排序；
     *        Verify list API supports filter, paging, and sorting.
     *
     * @throws Exception JSON 解析异常（JSON parsing exception）。
     */
    @Test
    void shouldListCustomersWithMobileFilterPagingAndSort() throws Exception {
        final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        repository.save(sampleCustomer("cust-a", "+8613800000000", "E-A"));
        repository.save(sampleCustomer("cust-b", "+8613900000000", "E-B"));
        repository.save(sampleCustomer("cust-c", "+8613800000000", "E-C"));

        final CustomerController controller = newController(repository, () -> "unused-id");
        final FakeRequest request = new FakeRequest(
                "GET",
                "/customers",
                Map.of(),
                Map.of(
                        "mobile_phone", "+8613800000000",
                        "page", "1",
                        "size", "1",
                        "sort", "customer_id"),
                "");

        final WebResponse response = controller.listCustomers(request);
        final JsonNode body = OBJECT_MAPPER.readTree(new String(response.body(), StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());
        assertEquals(2, body.get("total").asInt());
        assertEquals(1, body.get("items").size());
        assertEquals("cust-a", body.get("items").get(0).get("customer_id").asText());
    }

    /**
     * @brief 验证非法排序表达式会被拒绝；
     *        Verify unsupported sort expression is rejected.
     */
    @Test
    void shouldRejectUnsupportedSortExpression() {
        final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        repository.save(sampleCustomer("cust-a", "+8613800000000", "E-A"));
        final CustomerController controller = newController(repository, () -> "unused-id");

        final FakeRequest request = new FakeRequest(
                "GET",
                "/customers",
                Map.of(),
                Map.of("sort", "unknown_field"),
                "");

        assertThrows(IllegalArgumentException.class, () -> controller.listCustomers(request));
    }

    /**
     * @brief 构造客户控制器（Build Customer Controller）；
     *        Build customer controller with in-memory dependencies.
     *
     * @param repository 客户仓储（Customer repository）。
     * @param idGenerator ID 生成器（ID generator）。
     * @return 客户控制器（Customer controller）。
     */
    private static CustomerController newController(
            final InMemoryCustomerRepository repository,
            final IdGenerator idGenerator
    ) {
        final DbTransactionManager transactionManager = new DbTransactionManager() {
            @Override
            public <T> T execute(final Supplier<T> action) {
                return action.get();
            }
        };

        final RegisterCustomerHandler registerHandler = new RegisterCustomerHandler(
                repository,
                transactionManager,
                idGenerator);
        final FindCustomerHandler findHandler = new FindCustomerHandler(repository);
        final ListCustomersHandler listHandler = new ListCustomersHandler(repository);
        return new CustomerController(registerHandler, findHandler, listHandler);
    }

    /**
     * @brief 构造样例客户（Build Sample Customer）；
     *        Build sample customer entity.
     *
     * @param customerId 客户 ID（Customer ID）。
     * @param mobilePhone 手机号（Mobile phone）。
     * @param idNumber 证件号（Identity-document number）。
     * @return 客户实体（Customer entity）。
     */
    private static Customer sampleCustomer(final String customerId, final String mobilePhone, final String idNumber) {
        return Customer.register(
                CustomerId.of(customerId),
                IdentityDocument.of(IdentityDocumentType.PASSPORT, idNumber, "CN"),
                PhoneNumber.of(mobilePhone),
                Address.of("Shanghai Pudong"),
                Address.of("Shanghai Minhang"),
                TaxProfile.of(false, (String) null));
    }

    /**
     * @brief 假请求对象（Fake Web Request）；
     *        Fake web request for controller tests.
     */
    private static final class FakeRequest implements WebRequest {

        /**
         * @brief HTTP 方法（HTTP Method）；
         *        Request HTTP method.
         */
        private final String method;

        /**
         * @brief 请求路径（Request Path）；
         *        Request path.
         */
        private final String path;

        /**
         * @brief 路径参数（Path Parameters）；
         *        Path-parameter map.
         */
        private final Map<String, String> pathParams;

        /**
         * @brief 查询参数（Query Parameters）；
         *        Query-parameter map.
         */
        private final Map<String, String> queryParams;

        /**
         * @brief 请求体文本（Body Text）；
         *        Request body text.
         */
        private final String bodyText;

        /**
         * @brief 构造假请求（Construct Fake Request）；
         *        Construct fake request.
         *
         * @param method HTTP 方法（HTTP method）。
         * @param path 请求路径（Request path）。
         * @param pathParams 路径参数（Path parameters）。
         * @param queryParams 查询参数（Query parameters）。
         * @param bodyText 请求体文本（Body text）。
         */
        private FakeRequest(final String method,
                            final String path,
                            final Map<String, String> pathParams,
                            final Map<String, String> queryParams,
                            final String bodyText) {
            this.method = method;
            this.path = path;
            this.pathParams = Map.copyOf(pathParams);
            this.queryParams = Map.copyOf(queryParams);
            this.bodyText = bodyText;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String method() {
            return method;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String path() {
            return path;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<String> pathParam(final String name) {
            return Optional.ofNullable(pathParams.get(name));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<String> queryParam(final String name) {
            return Optional.ofNullable(queryParams.get(name));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<String> header(final String name) {
            return Optional.empty();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<String> headers(final String name) {
            return List.of();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public byte[] bodyBytes() {
            return bodyText.getBytes(StandardCharsets.UTF_8);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String bodyText() {
            return bodyText;
        }
    }

    /**
     * @brief 内存客户仓储（In-memory Customer Repository）；
     *        In-memory customer repository for controller tests.
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
