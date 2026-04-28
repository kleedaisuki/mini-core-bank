package com.moesegfault.banking.presentation.web.customer;

import com.moesegfault.banking.application.customer.command.RegisterCustomerHandler;
import com.moesegfault.banking.application.customer.query.FindCustomerHandler;
import com.moesegfault.banking.application.customer.query.FindCustomerQuery;
import com.moesegfault.banking.application.customer.query.ListCustomersHandler;
import com.moesegfault.banking.application.customer.query.ListCustomersQuery;
import com.moesegfault.banking.application.customer.result.CustomerResult;
import com.moesegfault.banking.application.customer.result.RegisterCustomerResult;
import com.moesegfault.banking.presentation.web.WebJsonCodec;
import com.moesegfault.banking.presentation.web.WebRequest;
import com.moesegfault.banking.presentation.web.WebResponse;
import com.moesegfault.banking.presentation.web.dto.PageRequestDto;
import com.moesegfault.banking.presentation.web.dto.PageResponseDto;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 客户 Web 控制器（Customer Web Controller），处理注册、详情与列表 REST 请求；
 *        Customer web controller handling register, detail, and list REST requests.
 */
public final class CustomerController {

    /**
     * @brief 注册客户处理器（Register Customer Handler）；
     *        Register-customer application handler.
     */
    private final RegisterCustomerHandler registerCustomerHandler;

    /**
     * @brief 查询单客户处理器（Find Customer Handler）；
     *        Find-customer application handler.
     */
    private final FindCustomerHandler findCustomerHandler;

    /**
     * @brief 查询客户列表处理器（List Customers Handler）；
     *        List-customers application handler.
     */
    private final ListCustomersHandler listCustomersHandler;

    /**
     * @brief JSON 编解码器（JSON Codec）；
     *        JSON codec for request parsing and response serialization.
     */
    private final WebJsonCodec webJsonCodec;

    /**
     * @brief 构造控制器并使用默认 JSON 编解码器（Construct Controller with Default JSON Codec）；
     *        Construct controller with default JSON codec.
     *
     * @param registerCustomerHandler 注册客户处理器（Register-customer handler）。
     * @param findCustomerHandler 查询单客户处理器（Find-customer handler）。
     * @param listCustomersHandler 查询客户列表处理器（List-customers handler）。
     */
    public CustomerController(final RegisterCustomerHandler registerCustomerHandler,
                              final FindCustomerHandler findCustomerHandler,
                              final ListCustomersHandler listCustomersHandler) {
        this(registerCustomerHandler, findCustomerHandler, listCustomersHandler, new WebJsonCodec());
    }

    /**
     * @brief 构造控制器（Construct Customer Controller）；
     *        Construct customer controller with injected collaborators.
     *
     * @param registerCustomerHandler 注册客户处理器（Register-customer handler）。
     * @param findCustomerHandler 查询单客户处理器（Find-customer handler）。
     * @param listCustomersHandler 查询客户列表处理器（List-customers handler）。
     * @param webJsonCodec JSON 编解码器（JSON codec）。
     */
    public CustomerController(final RegisterCustomerHandler registerCustomerHandler,
                              final FindCustomerHandler findCustomerHandler,
                              final ListCustomersHandler listCustomersHandler,
                              final WebJsonCodec webJsonCodec) {
        this.registerCustomerHandler = Objects.requireNonNull(
                registerCustomerHandler,
                "registerCustomerHandler must not be null");
        this.findCustomerHandler = Objects.requireNonNull(findCustomerHandler, "findCustomerHandler must not be null");
        this.listCustomersHandler = Objects.requireNonNull(listCustomersHandler, "listCustomersHandler must not be null");
        this.webJsonCodec = Objects.requireNonNull(webJsonCodec, "webJsonCodec must not be null");
    }

    /**
     * @brief 处理创建客户请求（Handle Create Customer Request）；
     *        Handle POST /customers request.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse registerCustomer(final WebRequest request) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final RegisterCustomerRequestDto requestDto = webJsonCodec.deserialize(
                normalizedRequest.bodyText(),
                RegisterCustomerRequestDto.class);

        final RegisterCustomerResult registerResult = registerCustomerHandler.handle(requestDto.toCommand());
        final CustomerResult createdCustomer = findCustomerHandler
                .handle(new FindCustomerQuery(registerResult.customerId()))
                .orElseThrow(() -> new IllegalStateException(
                        "Registered customer not found after commit: " + registerResult.customerId()));

        final CustomerResponseDto responseDto = CustomerResponseDto.fromResult(createdCustomer);
        return webJsonCodec.toJsonResponse(201, responseDto)
                .withHeader("Location", "/customers/" + responseDto.customerId());
    }

    /**
     * @brief 处理客户详情请求（Handle Customer Detail Request）；
     *        Handle GET /customers/{customerId} request.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse findCustomer(final WebRequest request) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final String customerId = readRequiredPathParam(normalizedRequest, "customerId");
        final CustomerResult customer = findCustomerHandler
                .handle(new FindCustomerQuery(customerId))
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
        return webJsonCodec.toJsonResponse(200, CustomerResponseDto.fromResult(customer));
    }

    /**
     * @brief 处理客户列表请求（Handle Customer List Request）；
     *        Handle GET /customers request.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse listCustomers(final WebRequest request) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final PageRequestDto pageRequest = PageRequestDto.fromQuery(normalizedRequest);
        final String mobilePhoneFilter = readOptionalQueryParam(normalizedRequest, "mobile_phone")
                .or(() -> readOptionalQueryParam(normalizedRequest, "mobilePhone"))
                .orElse(null);

        final ListCustomersQuery query = mobilePhoneFilter == null
                ? ListCustomersQuery.all()
                : ListCustomersQuery.byMobilePhone(mobilePhoneFilter);

        final List<CustomerResult> customerResults = applySort(
                listCustomersHandler.handle(query),
                pageRequest.sort());
        final long total = customerResults.size();

        final int fromIndex = Math.min(pageRequest.offset(), customerResults.size());
        final int toIndex = Math.min(fromIndex + pageRequest.size(), customerResults.size());
        final List<CustomerResponseDto> pageItems = customerResults.subList(fromIndex, toIndex)
                .stream()
                .map(CustomerResponseDto::fromResult)
                .toList();

        final PageResponseDto<CustomerResponseDto> pageResponse = new PageResponseDto<>(
                pageItems,
                pageRequest.page(),
                pageRequest.size(),
                total);

        return webJsonCodec.toJsonResponse(200, CustomerListResponseDto.fromPage(pageResponse));
    }

    /**
     * @brief 读取必填路径参数（Read Required Path Parameter）；
     *        Read required path parameter and reject blank value.
     *
     * @param request Web 请求（Web request）。
     * @param name 参数名（Parameter name）。
     * @return 参数值（Parameter value）。
     */
    private static String readRequiredPathParam(final WebRequest request, final String name) {
        final Optional<String> rawValue = request.pathParam(name);
        if (rawValue.isEmpty()) {
            throw new IllegalArgumentException("Missing required path parameter: " + name);
        }
        final String normalized = rawValue.orElseThrow().trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Path parameter must not be blank: " + name);
        }
        return normalized;
    }

    /**
     * @brief 读取可选查询参数（Read Optional Query Parameter）；
     *        Read optional query parameter and normalize blank to empty.
     *
     * @param request Web 请求（Web request）。
     * @param name 参数名（Parameter name）。
     * @return 参数可选值（Optional parameter value）。
     */
    private static Optional<String> readOptionalQueryParam(final WebRequest request, final String name) {
        return request.queryParam(name)
                .map(String::trim)
                .filter(value -> !value.isEmpty());
    }

    /**
     * @brief 应用排序表达式（Apply Sort Expression）；
     *        Apply list sort expression aligned to REST query parameter `sort`.
     *
     * @param customers 客户结果列表（Customer result list）。
     * @param sortExpression 排序表达式（Sort expression）。
     * @return 排序后的客户结果列表（Sorted customer result list）。
     */
    private static List<CustomerResult> applySort(final List<CustomerResult> customers, final String sortExpression) {
        final List<CustomerResult> source = List.copyOf(Objects.requireNonNull(customers, "customers must not be null"));
        final String normalizedSort = Objects.requireNonNull(sortExpression, "sortExpression must not be null").trim();
        if (normalizedSort.isEmpty()) {
            return source;
        }

        final Comparator<CustomerResult> comparator = switch (normalizedSort) {
            case "customer_id" -> Comparator.comparing(CustomerResult::customerId);
            case "-customer_id" -> Comparator.comparing(CustomerResult::customerId).reversed();
            case "created_at" -> Comparator.comparing(CustomerResult::createdAt)
                    .thenComparing(CustomerResult::customerId);
            case "-created_at" -> Comparator.comparing(CustomerResult::createdAt)
                    .thenComparing(CustomerResult::customerId)
                    .reversed();
            default -> throw new IllegalArgumentException(
                    "Unsupported sort expression: " + normalizedSort
                            + ". Supported values: customer_id, -customer_id, created_at, -created_at");
        };

        final List<CustomerResult> sorted = new ArrayList<>(source);
        sorted.sort(comparator);
        return List.copyOf(sorted);
    }
}
