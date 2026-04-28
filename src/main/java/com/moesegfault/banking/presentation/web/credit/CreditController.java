package com.moesegfault.banking.presentation.web.credit;

import com.moesegfault.banking.application.credit.command.GenerateStatementHandler;
import com.moesegfault.banking.application.credit.command.RepayCreditCardHandler;
import com.moesegfault.banking.application.credit.query.FindStatementHandler;
import com.moesegfault.banking.application.credit.query.FindStatementQuery;
import com.moesegfault.banking.application.credit.result.CreditCardStatementResult;
import com.moesegfault.banking.presentation.web.WebJsonCodec;
import com.moesegfault.banking.presentation.web.WebRequest;
import com.moesegfault.banking.presentation.web.WebResponse;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 信用领域 Web 控制器（Credit Web Controller），负责账单生成、账单查询和信用卡还款适配；
 *        Credit-domain web controller adapting statement generation, statement query, and repayment APIs.
 */
public final class CreditController {

    /**
     * @brief 生成账单应用服务（Generate Statement Application Handler）；
     *        Application handler for statement generation.
     */
    private final GenerateStatementHandler generateStatementHandler;

    /**
     * @brief 还款应用服务（Repay Credit Card Application Handler）；
     *        Application handler for credit-card repayment.
     */
    private final RepayCreditCardHandler repayCreditCardHandler;

    /**
     * @brief 查询账单应用服务（Find Statement Application Handler）；
     *        Application handler for statement query.
     */
    private final FindStatementHandler findStatementHandler;

    /**
     * @brief Web JSON 编解码器（Web JSON Codec）；
     *        Web JSON codec.
     */
    private final WebJsonCodec webJsonCodec;

    /**
     * @brief 构造信用 Web 控制器（Construct Credit Web Controller）；
     *        Construct credit web controller.
     *
     * @param generateStatementHandler 生成账单应用服务（Generate-statement application handler）。
     * @param repayCreditCardHandler 还款应用服务（Repay application handler）。
     * @param findStatementHandler 查询账单应用服务（Find-statement application handler）。
     */
    public CreditController(
            final GenerateStatementHandler generateStatementHandler,
            final RepayCreditCardHandler repayCreditCardHandler,
            final FindStatementHandler findStatementHandler
    ) {
        this(
                generateStatementHandler,
                repayCreditCardHandler,
                findStatementHandler,
                new WebJsonCodec());
    }

    /**
     * @brief 构造信用 Web 控制器（完整依赖）（Construct Credit Web Controller with Full Dependencies）；
     *        Construct credit web controller with explicit dependencies.
     *
     * @param generateStatementHandler 生成账单应用服务（Generate-statement application handler）。
     * @param repayCreditCardHandler 还款应用服务（Repay application handler）。
     * @param findStatementHandler 查询账单应用服务（Find-statement application handler）。
     * @param webJsonCodec Web JSON 编解码器（Web JSON codec）。
     */
    public CreditController(
            final GenerateStatementHandler generateStatementHandler,
            final RepayCreditCardHandler repayCreditCardHandler,
            final FindStatementHandler findStatementHandler,
            final WebJsonCodec webJsonCodec
    ) {
        this.generateStatementHandler = Objects.requireNonNull(
                generateStatementHandler,
                "generateStatementHandler must not be null");
        this.repayCreditCardHandler = Objects.requireNonNull(
                repayCreditCardHandler,
                "repayCreditCardHandler must not be null");
        this.findStatementHandler = Objects.requireNonNull(findStatementHandler, "findStatementHandler must not be null");
        this.webJsonCodec = Objects.requireNonNull(webJsonCodec, "webJsonCodec must not be null");
    }

    /**
     * @brief 处理生成账单请求（Handle Generate Statement Request）；
     *        Handle HTTP request for statement generation.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse generateStatement(final WebRequest request) {
        final GenerateStatementRequestDto requestDto = webJsonCodec.deserialize(
                normalizeRequest(request).bodyText(),
                GenerateStatementRequestDto.class);
        final StatementResponseDto responseDto = StatementResponseDto.from(
                generateStatementHandler.handle(requestDto.toCommand()));
        return webJsonCodec.toJsonResponse(201, responseDto);
    }

    /**
     * @brief 处理信用卡还款请求（Handle Repay Credit Card Request）；
     *        Handle HTTP request for credit-card repayment.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse repayCreditCard(final WebRequest request) {
        final RepayCreditCardRequestDto requestDto = webJsonCodec.deserialize(
                normalizeRequest(request).bodyText(),
                RepayCreditCardRequestDto.class);
        final RepaymentResponseDto responseDto = RepaymentResponseDto.from(
                repayCreditCardHandler.handle(requestDto.toCommand()));
        return webJsonCodec.toJsonResponse(200, responseDto);
    }

    /**
     * @brief 按账单 ID 查询账单（Find Statement by Statement ID）；
     *        Find one statement by statement identifier path parameter.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse findStatementById(final WebRequest request) {
        final WebRequest normalizedRequest = normalizeRequest(request);
        final String statementId = requiredPathParam(normalizedRequest, "statementId");
        final Optional<CreditCardStatementResult> statementResult = findStatementHandler.handle(
                FindStatementQuery.byStatementId(statementId));
        return webJsonCodec.toJsonResponse(200, StatementResponseDto.from(
                statementResult.orElseThrow(() -> new CreditStatementNotFoundException(
                        "Statement not found: " + statementId))));
    }

    /**
     * @brief 按“账户+账期”查询账单（Find Statement by Account and Period）；
     *        Find one statement by account-and-period query tuple.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     */
    public WebResponse findStatementByPeriod(final WebRequest request) {
        final WebRequest normalizedRequest = normalizeRequest(request);
        final String creditCardAccountId = requiredQueryParam(
                normalizedRequest,
                "credit_card_account_id",
                "creditCardAccountId");
        final LocalDate statementPeriodStart = parseRequiredDateQuery(
                normalizedRequest,
                "statement_period_start",
                "statementPeriodStart");
        final LocalDate statementPeriodEnd = parseRequiredDateQuery(
                normalizedRequest,
                "statement_period_end",
                "statementPeriodEnd");

        final Optional<CreditCardStatementResult> statementResult = findStatementHandler.handle(FindStatementQuery.byPeriod(
                creditCardAccountId,
                statementPeriodStart,
                statementPeriodEnd));

        return webJsonCodec.toJsonResponse(200, StatementResponseDto.from(
                statementResult.orElseThrow(() -> new CreditStatementNotFoundException(
                        "Statement not found by period: credit_card_account_id="
                                + creditCardAccountId
                                + ", statement_period_start="
                                + statementPeriodStart
                                + ", statement_period_end="
                                + statementPeriodEnd))));
    }

    /**
     * @brief 规范化请求（Normalize Request）；
     *        Normalize request object and reject null input.
     *
     * @param request Web 请求（Web request）。
     * @return 规范化请求（Normalized request）。
     */
    private WebRequest normalizeRequest(final WebRequest request) {
        return Objects.requireNonNull(request, "request must not be null");
    }

    /**
     * @brief 读取必填路径参数（Read Required Path Parameter）；
     *        Read one required path parameter.
     *
     * @param request Web 请求（Web request）。
     * @param parameterName 参数名（Parameter name）。
     * @return 参数值（Parameter value）。
     */
    private String requiredPathParam(final WebRequest request, final String parameterName) {
        return request.pathParam(parameterName)
                .map(value -> normalizeRequiredText(value, parameterName))
                .orElseThrow(() -> new IllegalArgumentException("Missing path parameter: " + parameterName));
    }

    /**
     * @brief 读取必填查询参数（Read Required Query Parameter）；
     *        Read one required query parameter with alias fallback.
     *
     * @param request Web 请求（Web request）。
     * @param canonicalName 规范参数名（Canonical parameter name）。
     * @param alias 别名（Alias）。
     * @return 参数值（Parameter value）。
     */
    private String requiredQueryParam(final WebRequest request, final String canonicalName, final String alias) {
        final Optional<String> rawValue = request.queryParam(canonicalName).or(() -> request.queryParam(alias));
        return normalizeRequiredText(rawValue.orElseThrow(
                () -> new IllegalArgumentException("Missing query parameter: " + canonicalName)), canonicalName);
    }

    /**
     * @brief 读取并解析必填日期查询参数（Read and Parse Required Date Query Parameter）；
     *        Read required date query parameter and parse into LocalDate.
     *
     * @param request Web 请求（Web request）。
     * @param canonicalName 规范参数名（Canonical parameter name）。
     * @param alias 别名（Alias）。
     * @return 解析后的日期（Parsed date）。
     */
    private LocalDate parseRequiredDateQuery(final WebRequest request, final String canonicalName, final String alias) {
        final String rawDate = requiredQueryParam(request, canonicalName, alias);
        try {
            return LocalDate.parse(rawDate);
        } catch (java.time.format.DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "Invalid query parameter " + canonicalName + ", expected yyyy-MM-dd",
                    exception);
        }
    }

    /**
     * @brief 规范化必填文本（Normalize Required Text）；
     *        Normalize required text and reject blank values.
     *
     * @param value 原始值（Raw value）。
     * @param fieldName 字段名（Field name）。
     * @return 规范化文本（Normalized text）。
     */
    private String normalizeRequiredText(final String value, final String fieldName) {
        final String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
