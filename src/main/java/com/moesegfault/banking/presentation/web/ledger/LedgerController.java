package com.moesegfault.banking.presentation.web.ledger;

import com.moesegfault.banking.application.ledger.query.FindBalanceHandler;
import com.moesegfault.banking.application.ledger.query.FindBalanceQuery;
import com.moesegfault.banking.application.ledger.query.ListLedgerEntriesHandler;
import com.moesegfault.banking.application.ledger.query.ListLedgerEntriesQuery;
import com.moesegfault.banking.application.ledger.result.BalanceResult;
import com.moesegfault.banking.application.ledger.result.LedgerEntryResult;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.presentation.web.WebJsonCodec;
import com.moesegfault.banking.presentation.web.WebRequest;
import com.moesegfault.banking.presentation.web.WebResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief Ledger Web 控制器（Ledger Web Controller），处理余额查询与分录查询 REST API；
 *        Ledger web controller handling balance-query and ledger-entry-query REST APIs.
 */
public final class LedgerController {

    /**
     * @brief 路径参数名：账户 ID（Path Parameter Name: Account ID）；
     *        Path parameter name for account identifier.
     */
    public static final String PATH_PARAM_ACCOUNT_ID = "accountId";

    /**
     * @brief 路径参数名：币种代码（Path Parameter Name: Currency Code）；
     *        Path parameter name for currency code.
     */
    public static final String PATH_PARAM_CURRENCY_CODE = "currencyCode";

    /**
     * @brief 查询参数名：条数上限（Query Parameter Name: Limit）；
     *        Query parameter name for result limit.
     */
    public static final String QUERY_PARAM_LIMIT = "limit";

    /**
     * @brief 余额查询应用服务（Find Balance Application Handler）；
     *        Application handler for balance query.
     */
    private final FindBalanceHandler findBalanceHandler;

    /**
     * @brief 分录查询应用服务（List Ledger Entries Application Handler）；
     *        Application handler for ledger-entry list query.
     */
    private final ListLedgerEntriesHandler listLedgerEntriesHandler;

    /**
     * @brief JSON 编解码器（JSON Codec）；
     *        JSON codec for web response serialization.
     */
    private final WebJsonCodec webJsonCodec;

    /**
     * @brief 构造 Ledger Web 控制器（Construct Ledger Web Controller）；
     *        Construct ledger web controller with injected application handlers.
     *
     * @param findBalanceHandler 余额查询应用服务（Balance-query application handler）。
     * @param listLedgerEntriesHandler 分录查询应用服务（Ledger-entry-query application handler）。
     */
    public LedgerController(final FindBalanceHandler findBalanceHandler,
                            final ListLedgerEntriesHandler listLedgerEntriesHandler) {
        this(findBalanceHandler, listLedgerEntriesHandler, new WebJsonCodec());
    }

    /**
     * @brief 构造 Ledger Web 控制器（Construct Ledger Web Controller）；
     *        Construct ledger web controller with injected handlers and JSON codec.
     *
     * @param findBalanceHandler 余额查询应用服务（Balance-query application handler）。
     * @param listLedgerEntriesHandler 分录查询应用服务（Ledger-entry-query application handler）。
     * @param webJsonCodec JSON 编解码器（JSON codec）。
     */
    public LedgerController(final FindBalanceHandler findBalanceHandler,
                            final ListLedgerEntriesHandler listLedgerEntriesHandler,
                            final WebJsonCodec webJsonCodec) {
        this.findBalanceHandler = Objects.requireNonNull(findBalanceHandler, "findBalanceHandler must not be null");
        this.listLedgerEntriesHandler = Objects.requireNonNull(
                listLedgerEntriesHandler,
                "listLedgerEntriesHandler must not be null");
        this.webJsonCodec = Objects.requireNonNull(webJsonCodec, "webJsonCodec must not be null");
    }

    /**
     * @brief 查询余额（Find Balance）；
     *        Query one account-currency balance snapshot.
     *
     * @param request Web 请求（Web request）。
     * @return Web JSON 响应（Web JSON response）。
     */
    public WebResponse findBalance(final WebRequest request) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final String accountId = requirePathParam(normalizedRequest, PATH_PARAM_ACCOUNT_ID);
        final String currencyCodeRaw = requirePathParam(normalizedRequest, PATH_PARAM_CURRENCY_CODE);

        final CurrencyCode currencyCode = CurrencyCode.of(currencyCodeRaw);
        final FindBalanceQuery query = new FindBalanceQuery(accountId, currencyCode);
        final Optional<BalanceResult> maybeBalance = findBalanceHandler.handle(query);
        if (maybeBalance.isEmpty()) {
            throw new LedgerBalanceNotFoundException(
                    "ledger_balance_not_found account_id=" + accountId + " currency_code=" + currencyCode.value());
        }

        final BalanceResponseDto responseDto = BalanceResponseDto.fromResult(maybeBalance.orElseThrow());
        return webJsonCodec.toJsonResponse(200, responseDto);
    }

    /**
     * @brief 查询分录列表（List Ledger Entries）；
     *        Query recent ledger entries for one account.
     *
     * @param request Web 请求（Web request）。
     * @return Web JSON 响应（Web JSON response）。
     */
    public WebResponse listLedgerEntries(final WebRequest request) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final String accountId = requirePathParam(normalizedRequest, PATH_PARAM_ACCOUNT_ID);
        final int limit = parseLimit(normalizedRequest.queryParam(QUERY_PARAM_LIMIT).orElse(null));

        final ListLedgerEntriesQuery query = new ListLedgerEntriesQuery(accountId, limit);
        final List<LedgerEntryResult> results = listLedgerEntriesHandler.handle(query);
        final List<LedgerEntryResponseDto> items = results.stream().map(LedgerEntryResponseDto::fromResult).toList();

        final LedgerEntriesResponseDto responseDto = new LedgerEntriesResponseDto(accountId, limit, items.size(), items);
        return webJsonCodec.toJsonResponse(200, responseDto);
    }

    /**
     * @brief 读取必填路径参数（Read Required Path Parameter）；
     *        Read one required path parameter.
     *
     * @param request Web 请求（Web request）。
     * @param parameterName 参数名（Parameter name）。
     * @return 参数值（Parameter value）。
     */
    private static String requirePathParam(final WebRequest request, final String parameterName) {
        final String normalizedParameterName = Objects.requireNonNull(
                parameterName,
                "parameterName must not be null");
        return request.pathParam(normalizedParameterName)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Missing required path parameter: " + normalizedParameterName));
    }

    /**
     * @brief 解析分录条数上限（Parse Ledger-entry Limit）；
     *        Parse ledger-entry result limit with default fallback.
     *
     * @param rawLimit 原始 limit 文本（Raw limit text）。
     * @return 解析后的条数上限（Parsed limit）。
     */
    private static int parseLimit(final String rawLimit) {
        if (rawLimit == null || rawLimit.trim().isEmpty()) {
            return ListLedgerEntriesQuery.DEFAULT_LIMIT;
        }
        try {
            return Integer.parseInt(rawLimit.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("limit must be an integer", exception);
        }
    }
}
