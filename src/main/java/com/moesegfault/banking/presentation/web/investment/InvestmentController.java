package com.moesegfault.banking.presentation.web.investment;

import com.moesegfault.banking.application.investment.command.BuyProductHandler;
import com.moesegfault.banking.application.investment.command.CreateInvestmentProductHandler;
import com.moesegfault.banking.application.investment.command.SellProductHandler;
import com.moesegfault.banking.application.investment.query.ListHoldingsHandler;
import com.moesegfault.banking.application.investment.query.ListHoldingsQuery;
import com.moesegfault.banking.application.investment.result.HoldingResult;
import com.moesegfault.banking.application.investment.result.InvestmentOrderResult;
import com.moesegfault.banking.application.investment.result.InvestmentProductResult;
import com.moesegfault.banking.presentation.web.WebJsonCodec;
import com.moesegfault.banking.presentation.web.WebRequest;
import com.moesegfault.banking.presentation.web.WebResponse;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 投资 Web 控制器（Investment Web Controller），处理产品创建、买入、卖出与持仓查询；
 *        Investment web controller handling create-product, buy/sell order, and holdings-query requests.
 */
public final class InvestmentController {

    /**
     * @brief 创建产品应用服务（Create Product Application Service）；
     *        Application handler for create-product command.
     */
    private final CreateInvestmentProductHandler createInvestmentProductHandler;

    /**
     * @brief 买入应用服务（Buy Product Application Service）；
     *        Application handler for buy-product command.
     */
    private final BuyProductHandler buyProductHandler;

    /**
     * @brief 卖出应用服务（Sell Product Application Service）；
     *        Application handler for sell-product command.
     */
    private final SellProductHandler sellProductHandler;

    /**
     * @brief 持仓查询应用服务（List Holdings Application Service）；
     *        Application handler for list-holdings query.
     */
    private final ListHoldingsHandler listHoldingsHandler;

    /**
     * @brief JSON 编解码器（JSON Codec）；
     *        JSON codec for request deserialization and response serialization.
     */
    private final WebJsonCodec webJsonCodec;

    /**
     * @brief 构造控制器并使用默认 JSON 编解码器（Construct Controller with Default JSON Codec）；
     *        Construct investment controller with default web JSON codec.
     *
     * @param createInvestmentProductHandler 创建产品服务（Create-product handler）。
     * @param buyProductHandler 买入服务（Buy-product handler）。
     * @param sellProductHandler 卖出服务（Sell-product handler）。
     * @param listHoldingsHandler 持仓查询服务（List-holdings handler）。
     */
    public InvestmentController(
            final CreateInvestmentProductHandler createInvestmentProductHandler,
            final BuyProductHandler buyProductHandler,
            final SellProductHandler sellProductHandler,
            final ListHoldingsHandler listHoldingsHandler
    ) {
        this(
                createInvestmentProductHandler,
                buyProductHandler,
                sellProductHandler,
                listHoldingsHandler,
                new WebJsonCodec());
    }

    /**
     * @brief 构造投资控制器（Construct Investment Controller）；
     *        Construct investment controller with injected dependencies.
     *
     * @param createInvestmentProductHandler 创建产品服务（Create-product handler）。
     * @param buyProductHandler 买入服务（Buy-product handler）。
     * @param sellProductHandler 卖出服务（Sell-product handler）。
     * @param listHoldingsHandler 持仓查询服务（List-holdings handler）。
     * @param webJsonCodec JSON 编解码器（JSON codec）。
     */
    public InvestmentController(
            final CreateInvestmentProductHandler createInvestmentProductHandler,
            final BuyProductHandler buyProductHandler,
            final SellProductHandler sellProductHandler,
            final ListHoldingsHandler listHoldingsHandler,
            final WebJsonCodec webJsonCodec
    ) {
        this.createInvestmentProductHandler = Objects.requireNonNull(
                createInvestmentProductHandler,
                "createInvestmentProductHandler must not be null");
        this.buyProductHandler = Objects.requireNonNull(buyProductHandler, "buyProductHandler must not be null");
        this.sellProductHandler = Objects.requireNonNull(sellProductHandler, "sellProductHandler must not be null");
        this.listHoldingsHandler = Objects.requireNonNull(listHoldingsHandler, "listHoldingsHandler must not be null");
        this.webJsonCodec = Objects.requireNonNull(webJsonCodec, "webJsonCodec must not be null");
    }

    /**
     * @brief 处理创建产品请求（Handle Create Product Request）；
     *        Handle one create-investment-product request.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse createProduct(final WebRequest request) {
        final WebRequest normalized = Objects.requireNonNull(request, "request must not be null");
        final CreateProductRequestDto requestDto = webJsonCodec.deserialize(
                normalized.bodyText(),
                CreateProductRequestDto.class);
        final InvestmentProductResult result = createInvestmentProductHandler.handle(requestDto.toCommand());
        return webJsonCodec.toJsonResponse(201, InvestmentProductResponseDto.fromResult(result));
    }

    /**
     * @brief 处理买入下单请求（Handle Buy Order Request）；
     *        Handle one buy-product order request.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse buyProduct(final WebRequest request) {
        final WebRequest normalized = Objects.requireNonNull(request, "request must not be null");
        final BuyProductRequestDto requestDto = webJsonCodec.deserialize(normalized.bodyText(), BuyProductRequestDto.class);
        final InvestmentOrderResult result = buyProductHandler.handle(requestDto.toCommand());
        return webJsonCodec.toJsonResponse(201, InvestmentOrderResponseDto.fromResult(result));
    }

    /**
     * @brief 处理卖出下单请求（Handle Sell Order Request）；
     *        Handle one sell-product order request.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse sellProduct(final WebRequest request) {
        final WebRequest normalized = Objects.requireNonNull(request, "request must not be null");
        final SellProductRequestDto requestDto = webJsonCodec.deserialize(normalized.bodyText(), SellProductRequestDto.class);
        final InvestmentOrderResult result = sellProductHandler.handle(requestDto.toCommand());
        return webJsonCodec.toJsonResponse(201, InvestmentOrderResponseDto.fromResult(result));
    }

    /**
     * @brief 处理持仓查询请求（Handle Holdings Query Request）；
     *        Handle holdings query request by investment-account path and include-details query flag.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse listHoldings(final WebRequest request) {
        final WebRequest normalized = Objects.requireNonNull(request, "request must not be null");
        final String investmentAccountId = requiredPathParam(
                normalized,
                InvestmentWebSchema.PATH_PARAM_INVESTMENT_ACCOUNT_ID);

        final boolean includeProductDetails = optionalBooleanQueryParam(
                normalized,
                InvestmentWebSchema.QUERY_INCLUDE_PRODUCT_DETAILS,
                false,
                "include-product-details",
                "includeProductDetails");

        final List<HoldingResult> resultList = listHoldingsHandler.handle(new ListHoldingsQuery(
                investmentAccountId,
                includeProductDetails));
        final List<HoldingResponseDto> items = resultList.stream()
                .map(HoldingResponseDto::fromResult)
                .toList();
        return webJsonCodec.toJsonResponse(200, new HoldingListResponseDto(
                investmentAccountId,
                includeProductDetails,
                items.size(),
                items));
    }

    /**
     * @brief 读取必填路径参数（Read Required Path Parameter）；
     *        Read one required path parameter by name.
     *
     * @param request Web 请求（Web request）。
     * @param name 参数名（Parameter name）。
     * @return 参数值（Parameter value）。
     */
    private static String requiredPathParam(final WebRequest request, final String name) {
        final Optional<String> maybeValue = request.pathParam(name)
                .map(String::trim)
                .filter(value -> !value.isEmpty());
        return maybeValue.orElseThrow(() -> new IllegalArgumentException("Missing required path parameter: " + name));
    }

    /**
     * @brief 读取可选查询参数（Read Optional Query Parameter）；
     *        Read one optional query parameter from canonical name and aliases.
     *
     * @param request Web 请求（Web request）。
     * @param canonicalName 规范参数名（Canonical query-parameter name）。
     * @param aliases 参数别名（Query-parameter aliases）。
     * @return 参数值（Parameter value, empty when absent）。
     */
    private static Optional<String> optionalQueryParam(final WebRequest request,
                                                       final String canonicalName,
                                                       final String... aliases) {
        final Optional<String> canonicalValue = normalizeOptionalText(request.queryParam(canonicalName).orElse(null));
        if (canonicalValue.isPresent()) {
            return canonicalValue;
        }
        for (String alias : aliases) {
            final Optional<String> aliasValue = normalizeOptionalText(request.queryParam(alias).orElse(null));
            if (aliasValue.isPresent()) {
                return aliasValue;
            }
        }
        return Optional.empty();
    }

    /**
     * @brief 读取可选布尔查询参数（Read Optional Boolean Query Parameter）；
     *        Read one optional boolean query parameter with default fallback.
     *
     * @param request Web 请求（Web request）。
     * @param canonicalName 规范参数名（Canonical query-parameter name）。
     * @param defaultValue 默认值（Default value）。
     * @param aliases 参数别名（Query-parameter aliases）。
     * @return 布尔值（Boolean value）。
     */
    private static boolean optionalBooleanQueryParam(final WebRequest request,
                                                     final String canonicalName,
                                                     final boolean defaultValue,
                                                     final String... aliases) {
        final Optional<String> maybeRawValue = optionalQueryParam(request, canonicalName, aliases);
        if (maybeRawValue.isEmpty()) {
            return defaultValue;
        }
        final String normalized = maybeRawValue.orElseThrow().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "true", "1", "yes", "y", "on" -> true;
            case "false", "0", "no", "n", "off" -> false;
            default -> throw new IllegalArgumentException(
                    "Unsupported boolean value for " + canonicalName + ": " + maybeRawValue.orElseThrow());
        };
    }

    /**
     * @brief 规范化可选文本（Normalize Optional Text）；
     *        Normalize optional text and convert blank to empty result.
     *
     * @param rawValue 原始值（Raw value）。
     * @return 可选文本（Optional normalized text）。
     */
    private static Optional<String> normalizeOptionalText(final String rawValue) {
        if (rawValue == null) {
            return Optional.empty();
        }
        final String normalized = rawValue.trim();
        if (normalized.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(normalized);
    }
}
