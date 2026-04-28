package com.moesegfault.banking.presentation.web.account;

import com.moesegfault.banking.application.account.command.FreezeAccountCommand;
import com.moesegfault.banking.application.account.command.FreezeAccountHandler;
import com.moesegfault.banking.application.account.command.OpenFxAccountCommand;
import com.moesegfault.banking.application.account.command.OpenFxAccountHandler;
import com.moesegfault.banking.application.account.command.OpenInvestmentAccountCommand;
import com.moesegfault.banking.application.account.command.OpenInvestmentAccountHandler;
import com.moesegfault.banking.application.account.command.OpenSavingsAccountCommand;
import com.moesegfault.banking.application.account.command.OpenSavingsAccountHandler;
import com.moesegfault.banking.application.account.query.FindAccountHandler;
import com.moesegfault.banking.application.account.query.FindAccountQuery;
import com.moesegfault.banking.application.account.query.ListCustomerAccountsHandler;
import com.moesegfault.banking.application.account.query.ListCustomerAccountsQuery;
import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.application.account.result.OpenAccountResult;
import com.moesegfault.banking.presentation.web.WebJsonCodec;
import com.moesegfault.banking.presentation.web.WebRequest;
import com.moesegfault.banking.presentation.web.WebResponse;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 账户 Web 控制器（Account Web Controller），承接 account 子域 REST 请求并调用应用层用例；
 *        Account web controller handling account-subdomain REST requests via application-layer use cases.
 */
public final class AccountController {

    /**
     * @brief 开立储蓄账户应用服务（Open Savings Account Application Service）；
     *        Application handler for opening savings account.
     */
    private final OpenSavingsAccountHandler openSavingsAccountHandler;

    /**
     * @brief 开立外汇账户应用服务（Open FX Account Application Service）；
     *        Application handler for opening FX account.
     */
    private final OpenFxAccountHandler openFxAccountHandler;

    /**
     * @brief 开立投资账户应用服务（Open Investment Account Application Service）；
     *        Application handler for opening investment account.
     */
    private final OpenInvestmentAccountHandler openInvestmentAccountHandler;

    /**
     * @brief 查询单账户应用服务（Find Account Application Service）；
     *        Application handler for finding one account.
     */
    private final FindAccountHandler findAccountHandler;

    /**
     * @brief 列举客户账户应用服务（List Customer Accounts Application Service）；
     *        Application handler for listing customer accounts.
     */
    private final ListCustomerAccountsHandler listCustomerAccountsHandler;

    /**
     * @brief 冻结账户应用服务（Freeze Account Application Service）；
     *        Application handler for freezing account.
     */
    private final FreezeAccountHandler freezeAccountHandler;

    /**
     * @brief Web JSON 编解码器（Web JSON Codec）；
     *        JSON codec for request and response payload.
     */
    private final WebJsonCodec webJsonCodec;

    /**
     * @brief 构造账户控制器（Construct Account Controller）；
     *        Construct account controller with default JSON codec.
     *
     * @param openSavingsAccountHandler 开立储蓄账户应用服务（Open savings-account handler）。
     * @param openFxAccountHandler 开立外汇账户应用服务（Open FX-account handler）。
     * @param openInvestmentAccountHandler 开立投资账户应用服务（Open investment-account handler）。
     * @param findAccountHandler 查询单账户应用服务（Find-account handler）。
     * @param listCustomerAccountsHandler 客户账户列表应用服务（List-customer-accounts handler）。
     * @param freezeAccountHandler 冻结账户应用服务（Freeze-account handler）。
     */
    public AccountController(
            final OpenSavingsAccountHandler openSavingsAccountHandler,
            final OpenFxAccountHandler openFxAccountHandler,
            final OpenInvestmentAccountHandler openInvestmentAccountHandler,
            final FindAccountHandler findAccountHandler,
            final ListCustomerAccountsHandler listCustomerAccountsHandler,
            final FreezeAccountHandler freezeAccountHandler
    ) {
        this(
                openSavingsAccountHandler,
                openFxAccountHandler,
                openInvestmentAccountHandler,
                findAccountHandler,
                listCustomerAccountsHandler,
                freezeAccountHandler,
                new WebJsonCodec());
    }

    /**
     * @brief 构造账户控制器（Construct Account Controller）；
     *        Construct account controller with injected JSON codec.
     *
     * @param openSavingsAccountHandler 开立储蓄账户应用服务（Open savings-account handler）。
     * @param openFxAccountHandler 开立外汇账户应用服务（Open FX-account handler）。
     * @param openInvestmentAccountHandler 开立投资账户应用服务（Open investment-account handler）。
     * @param findAccountHandler 查询单账户应用服务（Find-account handler）。
     * @param listCustomerAccountsHandler 客户账户列表应用服务（List-customer-accounts handler）。
     * @param freezeAccountHandler 冻结账户应用服务（Freeze-account handler）。
     * @param webJsonCodec Web JSON 编解码器（Web JSON codec）。
     */
    public AccountController(
            final OpenSavingsAccountHandler openSavingsAccountHandler,
            final OpenFxAccountHandler openFxAccountHandler,
            final OpenInvestmentAccountHandler openInvestmentAccountHandler,
            final FindAccountHandler findAccountHandler,
            final ListCustomerAccountsHandler listCustomerAccountsHandler,
            final FreezeAccountHandler freezeAccountHandler,
            final WebJsonCodec webJsonCodec
    ) {
        this.openSavingsAccountHandler = Objects.requireNonNull(
                openSavingsAccountHandler,
                "openSavingsAccountHandler must not be null");
        this.openFxAccountHandler = Objects.requireNonNull(openFxAccountHandler, "openFxAccountHandler must not be null");
        this.openInvestmentAccountHandler = Objects.requireNonNull(
                openInvestmentAccountHandler,
                "openInvestmentAccountHandler must not be null");
        this.findAccountHandler = Objects.requireNonNull(findAccountHandler, "findAccountHandler must not be null");
        this.listCustomerAccountsHandler = Objects.requireNonNull(
                listCustomerAccountsHandler,
                "listCustomerAccountsHandler must not be null");
        this.freezeAccountHandler = Objects.requireNonNull(freezeAccountHandler, "freezeAccountHandler must not be null");
        this.webJsonCodec = Objects.requireNonNull(webJsonCodec, "webJsonCodec must not be null");
    }

    /**
     * @brief 处理储蓄开户请求（Handle Open Savings Account Request）；
     *        Handle `POST /accounts/savings` request.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse openSavingsAccount(final WebRequest request) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final OpenSavingsAccountRequestDto body = webJsonCodec.deserialize(
                normalizedRequest.bodyText(),
                OpenSavingsAccountRequestDto.class);

        final OpenAccountResult result = openSavingsAccountHandler.handle(
                new OpenSavingsAccountCommand(body.customerId(), body.accountNo()));
        final AccountResponseDto responseDto = AccountResponseDto.from(result);
        return webJsonCodec.toJsonResponse(201, responseDto)
                .withHeader("Location", AccountWebSchema.PATH_ACCOUNTS + "/" + responseDto.accountId());
    }

    /**
     * @brief 处理外汇开户请求（Handle Open FX Account Request）；
     *        Handle `POST /accounts/fx` request.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse openFxAccount(final WebRequest request) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final OpenFxAccountRequestDto body = webJsonCodec.deserialize(
                normalizedRequest.bodyText(),
                OpenFxAccountRequestDto.class);

        final OpenAccountResult result = openFxAccountHandler.handle(
                new OpenFxAccountCommand(body.customerId(), body.accountNo(), body.linkedSavingsAccountId()));
        final AccountResponseDto responseDto = AccountResponseDto.from(result);
        return webJsonCodec.toJsonResponse(201, responseDto)
                .withHeader("Location", AccountWebSchema.PATH_ACCOUNTS + "/" + responseDto.accountId());
    }

    /**
     * @brief 处理投资开户请求（Handle Open Investment Account Request）；
     *        Handle `POST /accounts/investment` request.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse openInvestmentAccount(final WebRequest request) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final OpenInvestmentAccountRequestDto body = webJsonCodec.deserialize(
                normalizedRequest.bodyText(),
                OpenInvestmentAccountRequestDto.class);

        final OpenAccountResult result = openInvestmentAccountHandler.handle(
                new OpenInvestmentAccountCommand(body.customerId(), body.accountNo()));
        final AccountResponseDto responseDto = AccountResponseDto.from(result);
        return webJsonCodec.toJsonResponse(201, responseDto)
                .withHeader("Location", AccountWebSchema.PATH_ACCOUNTS + "/" + responseDto.accountId());
    }

    /**
     * @brief 处理按账户 ID 查询请求（Handle Find Account by ID Request）；
     *        Handle `GET /accounts/{accountId}` request.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse findAccountById(final WebRequest request) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final String accountId = requiredPathParam(normalizedRequest, AccountWebSchema.PATH_PARAM_ACCOUNT_ID);
        final AccountResult result = findAccountHandler.handle(FindAccountQuery.byAccountId(accountId));
        return webJsonCodec.toJsonResponse(200, AccountResponseDto.from(result));
    }

    /**
     * @brief 处理按账户号查询请求（Handle Find Account by Account Number Request）；
     *        Handle `GET /accounts/by-account-no/{accountNo}` request.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse findAccountByAccountNo(final WebRequest request) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final String accountNo = requiredPathParam(normalizedRequest, AccountWebSchema.PATH_PARAM_ACCOUNT_NO);
        final AccountResult result = findAccountHandler.handle(FindAccountQuery.byAccountNo(accountNo));
        return webJsonCodec.toJsonResponse(200, AccountResponseDto.from(result));
    }

    /**
     * @brief 处理客户账户列表请求（Handle List Customer Accounts Request）；
     *        Handle `GET /accounts?customer_id=...` request.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse listCustomerAccounts(final WebRequest request) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final String customerId = requiredQueryParam(
                normalizedRequest,
                AccountWebSchema.CUSTOMER_ID,
                "customer-id",
                "customerId");
        final boolean includeClosedAccounts = optionalBooleanQueryParam(
                normalizedRequest,
                AccountWebSchema.INCLUDE_CLOSED_ACCOUNTS,
                false,
                "include-closed-accounts",
                "includeClosedAccounts");

        final List<AccountResult> results = listCustomerAccountsHandler.handle(
                new ListCustomerAccountsQuery(customerId, includeClosedAccounts));
        return webJsonCodec.toJsonResponse(200, AccountListResponseDto.fromResults(results));
    }

    /**
     * @brief 处理冻结账户请求（Handle Freeze Account Request）；
     *        Handle `PATCH /accounts/{accountId}` request.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse freezeAccount(final WebRequest request) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final String accountId = requiredPathParam(normalizedRequest, AccountWebSchema.PATH_PARAM_ACCOUNT_ID);
        final FreezeAccountRequestDto body = webJsonCodec.deserialize(
                normalizedRequest.bodyText(),
                FreezeAccountRequestDto.class);

        ensureFreezeStatus(body.accountStatus());
        final AccountResult result = freezeAccountHandler.handle(new FreezeAccountCommand(accountId, body.freezeReason()));
        return webJsonCodec.toJsonResponse(200, AccountResponseDto.from(result));
    }

    /**
     * @brief 校验冻结状态字段（Validate Freeze Status Field）；
     *        Validate freeze request status transition.
     *
     * @param accountStatus 账户状态（Account status from request）。
     */
    private void ensureFreezeStatus(final String accountStatus) {
        if (!"FROZEN".equals(accountStatus)) {
            throw new IllegalArgumentException(
                    "Only account_status=FROZEN is supported for this endpoint");
        }
    }

    /**
     * @brief 读取必填路径参数（Read Required Path Parameter）；
     *        Read one required path parameter by name.
     *
     * @param request Web 请求（Web request）。
     * @param name 参数名（Parameter name）。
     * @return 参数值（Parameter value）。
     */
    private String requiredPathParam(final WebRequest request, final String name) {
        final Optional<String> maybeValue = request.pathParam(name).map(String::trim).filter(value -> !value.isEmpty());
        return maybeValue.orElseThrow(() -> new IllegalArgumentException("Missing required path parameter: " + name));
    }

    /**
     * @brief 读取必填查询参数（Read Required Query Parameter）；
     *        Read one required query parameter from canonical name and aliases.
     *
     * @param request Web 请求（Web request）。
     * @param canonicalName 规范参数名（Canonical query-parameter name）。
     * @param aliases 参数别名（Query-parameter aliases）。
     * @return 参数值（Parameter value）。
     */
    private String requiredQueryParam(final WebRequest request, final String canonicalName, final String... aliases) {
        return optionalQueryParam(request, canonicalName, aliases).orElseThrow(
                () -> new IllegalArgumentException("Missing required query parameter: " + canonicalName));
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
    private Optional<String> optionalQueryParam(final WebRequest request, final String canonicalName, final String... aliases) {
        final String normalizedCanonicalName = normalizeName(canonicalName, "canonicalName");
        final String primary = normalizeOptionalText(request.queryParam(normalizedCanonicalName).orElse(null));
        if (primary != null) {
            return Optional.of(primary);
        }
        for (String alias : aliases) {
            final String normalizedAlias = normalizeName(alias, "alias");
            final String value = normalizeOptionalText(request.queryParam(normalizedAlias).orElse(null));
            if (value != null) {
                return Optional.of(value);
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
    private boolean optionalBooleanQueryParam(
            final WebRequest request,
            final String canonicalName,
            final boolean defaultValue,
            final String... aliases
    ) {
        final Optional<String> maybeRawValue = optionalQueryParam(request, canonicalName, aliases);
        if (maybeRawValue.isEmpty()) {
            return defaultValue;
        }
        final String normalizedValue = maybeRawValue.get().toLowerCase(Locale.ROOT);
        return switch (normalizedValue) {
            case "true", "1", "yes", "y", "on" -> true;
            case "false", "0", "no", "n", "off" -> false;
            default -> throw new IllegalArgumentException(
                    "Invalid boolean query parameter: "
                            + canonicalName
                            + ", expected one of true/false/1/0/yes/no");
        };
    }

    /**
     * @brief 规范化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text by trimming and converting blank to null.
     *
     * @param value 原始值（Raw value, nullable）。
     * @return 规范化值或 null（Normalized text or null）。
     */
    private String normalizeOptionalText(final String value) {
        if (value == null) {
            return null;
        }
        final String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    /**
     * @brief 规范化名称（Normalize Name）；
     *        Normalize non-blank name.
     *
     * @param rawName 原始名称（Raw name）。
     * @param field 字段名（Field name）。
     * @return 规范化名称（Normalized name）。
     */
    private String normalizeName(final String rawName, final String field) {
        final String normalized = Objects.requireNonNull(rawName, field + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return normalized;
    }
}
