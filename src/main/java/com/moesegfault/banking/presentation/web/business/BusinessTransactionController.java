package com.moesegfault.banking.presentation.web.business;

import com.moesegfault.banking.application.business.query.FindBusinessTransactionHandler;
import com.moesegfault.banking.application.business.query.FindBusinessTransactionQuery;
import com.moesegfault.banking.application.business.query.ListBusinessTransactionsHandler;
import com.moesegfault.banking.application.business.query.ListBusinessTransactionsQuery;
import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.domain.business.BusinessTransactionStatus;
import com.moesegfault.banking.presentation.web.WebJsonCodec;
import com.moesegfault.banking.presentation.web.WebRequest;
import com.moesegfault.banking.presentation.web.WebResponse;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 业务流水 Web 控制器（Business Transaction Web Controller），处理业务流水详情与列表查询接口；
 *        Business-transaction web controller handling detail and list query APIs.
 */
public final class BusinessTransactionController {

    /**
     * @brief 业务流水 ID 路径参数名（Transaction-ID Path Parameter Name）；
     *        Path-parameter name for transaction identifier.
     */
    public static final String PATH_PARAM_TRANSACTION_ID = "transactionId";

    /**
     * @brief 业务参考号路径参数名（Reference-Number Path Parameter Name）；
     *        Path-parameter name for reference number.
     */
    public static final String PATH_PARAM_REFERENCE_NO = "referenceNo";

    /**
     * @brief 业务流水列表查询应用服务（List Business Transactions Handler）；
     *        Application handler for listing business transactions.
     */
    private final ListBusinessTransactionsHandler listBusinessTransactionsHandler;

    /**
     * @brief 单笔业务流水查询应用服务（Find Business Transaction Handler）；
     *        Application handler for finding one business transaction.
     */
    private final FindBusinessTransactionHandler findBusinessTransactionHandler;

    /**
     * @brief JSON 编解码器（Web JSON Codec）；
     *        JSON codec for serializing response DTOs.
     */
    private final WebJsonCodec webJsonCodec;

    /**
     * @brief 构造业务流水控制器（Construct Business Transaction Controller）；
     *        Construct business-transaction controller.
     *
     * @param listBusinessTransactionsHandler 列表查询服务（List query handler）。
     * @param findBusinessTransactionHandler 单笔查询服务（Single query handler）。
     * @param webJsonCodec JSON 编解码器（JSON codec）。
     */
    public BusinessTransactionController(
            final ListBusinessTransactionsHandler listBusinessTransactionsHandler,
            final FindBusinessTransactionHandler findBusinessTransactionHandler,
            final WebJsonCodec webJsonCodec
    ) {
        this.listBusinessTransactionsHandler = Objects.requireNonNull(
                listBusinessTransactionsHandler,
                "listBusinessTransactionsHandler must not be null");
        this.findBusinessTransactionHandler = Objects.requireNonNull(
                findBusinessTransactionHandler,
                "findBusinessTransactionHandler must not be null");
        this.webJsonCodec = Objects.requireNonNull(webJsonCodec, "webJsonCodec must not be null");
    }

    /**
     * @brief 查询业务流水列表（List Business Transactions）；
     *        Query business-transaction list with optional filters.
     *
     * @param request Web 请求（Web request）。
     * @return JSON 响应（JSON web response）。
     */
    public WebResponse listBusinessTransactions(final WebRequest request) {
        final WebRequest normalized = Objects.requireNonNull(request, "request must not be null");
        final String initiatorCustomerId = queryParam(
                normalized,
                "initiator_customer_id",
                "initiator-customer-id",
                "customer_id",
                "customer-id").orElse(null);
        final BusinessTransactionStatus status = queryParam(
                normalized,
                "transaction_status",
                "transaction-status",
                "status")
                .map(BusinessTransactionController::parseStatus)
                .orElse(null);
        final int limit = queryParam(normalized, "limit")
                .map(BusinessTransactionController::parseLimit)
                .orElse(ListBusinessTransactionsQuery.DEFAULT_LIMIT);

        final ListBusinessTransactionsQuery query = new ListBusinessTransactionsQuery(
                initiatorCustomerId,
                status,
                limit);
        final List<BusinessTransactionResponseDto> items = listBusinessTransactionsHandler.handle(query).stream()
                .map(BusinessTransactionResponseDto::from)
                .toList();
        final BusinessTransactionListResponseDto response = new BusinessTransactionListResponseDto(items.size(), items);
        return webJsonCodec.toJsonResponse(200, response);
    }

    /**
     * @brief 按交易 ID 查询业务流水详情（Get Business Transaction by Transaction ID）；
     *        Query business-transaction detail by transaction id.
     *
     * @param request Web 请求（Web request）。
     * @return JSON 响应（JSON web response）。
     */
    public WebResponse getBusinessTransactionById(final WebRequest request) {
        final String transactionId = requiredPathParam(
                Objects.requireNonNull(request, "request must not be null"),
                PATH_PARAM_TRANSACTION_ID);
        final FindBusinessTransactionQuery query = FindBusinessTransactionQuery.byTransactionId(transactionId);
        final BusinessTransactionResult transaction = findOrThrow(query);
        return webJsonCodec.toJsonResponse(200, BusinessTransactionResponseDto.from(transaction));
    }

    /**
     * @brief 按参考号查询业务流水详情（Get Business Transaction by Reference Number）；
     *        Query business-transaction detail by reference number.
     *
     * @param request Web 请求（Web request）。
     * @return JSON 响应（JSON web response）。
     */
    public WebResponse getBusinessTransactionByReferenceNo(final WebRequest request) {
        final String referenceNo = requiredPathParam(
                Objects.requireNonNull(request, "request must not be null"),
                PATH_PARAM_REFERENCE_NO);
        final FindBusinessTransactionQuery query = FindBusinessTransactionQuery.byReferenceNo(referenceNo);
        final BusinessTransactionResult transaction = findOrThrow(query);
        return webJsonCodec.toJsonResponse(200, BusinessTransactionResponseDto.from(transaction));
    }

    /**
     * @brief 执行查询并在缺失时抛出 404 异常（Find Transaction or Throw 404 Exception）；
     *        Execute query and throw 404-style exception when missing.
     *
     * @param query 查询对象（Find query）。
     * @return 业务流水结果（Business-transaction result）。
     */
    private BusinessTransactionResult findOrThrow(final FindBusinessTransactionQuery query) {
        final Optional<BusinessTransactionResult> transaction = findBusinessTransactionHandler.handle(query);
        if (transaction.isPresent()) {
            return transaction.orElseThrow();
        }
        if (query.hasTransactionId()) {
            throw new BusinessTransactionNotFoundException(
                    "business_transaction_not_found transaction_id=" + query.transactionIdOrNull());
        }
        throw new BusinessTransactionNotFoundException(
                "business_transaction_not_found reference_no=" + query.referenceNoOrNull());
    }

    /**
     * @brief 读取必填路径参数（Read Required Path Parameter）；
     *        Read one required path parameter.
     *
     * @param request Web 请求（Web request）。
     * @param parameterName 参数名（Parameter name）。
     * @return 参数值（Parameter value）。
     */
    private static String requiredPathParam(final WebRequest request, final String parameterName) {
        return request.pathParam(parameterName)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Missing required path parameter: " + parameterName));
    }

    /**
     * @brief 读取查询参数（Read Query Parameter by Alias）；
     *        Read query parameter by alias names.
     *
     * @param request Web 请求（Web request）。
     * @param names 参数别名列表（Parameter alias names）。
     * @return 查询参数（Query parameter, optional）。
     */
    private static Optional<String> queryParam(final WebRequest request, final String... names) {
        for (String name : names) {
            final Optional<String> value = request.queryParam(name);
            if (value.isPresent()) {
                final String normalized = value.orElseThrow().trim();
                if (!normalized.isEmpty()) {
                    return Optional.of(normalized);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * @brief 解析交易状态参数（Parse Transaction Status Parameter）；
     *        Parse transaction-status parameter as enum.
     *
     * @param rawStatus 原始状态文本（Raw status text）。
     * @return 交易状态枚举（Transaction-status enum）。
     */
    private static BusinessTransactionStatus parseStatus(final String rawStatus) {
        final String normalized = Objects.requireNonNull(rawStatus, "rawStatus must not be null")
                .trim()
                .toUpperCase(Locale.ROOT);
        return BusinessTransactionStatus.valueOf(normalized);
    }

    /**
     * @brief 解析返回上限参数（Parse Limit Parameter）；
     *        Parse limit parameter as positive integer.
     *
     * @param rawLimit 原始上限文本（Raw limit text）。
     * @return 返回上限（Parsed limit）。
     */
    private static int parseLimit(final String rawLimit) {
        final String normalized = Objects.requireNonNull(rawLimit, "rawLimit must not be null").trim();
        try {
            final int limit = Integer.parseInt(normalized);
            if (limit <= 0) {
                throw new IllegalArgumentException("limit must be > 0");
            }
            return limit;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Unsupported limit value: " + rawLimit, exception);
        }
    }
}

