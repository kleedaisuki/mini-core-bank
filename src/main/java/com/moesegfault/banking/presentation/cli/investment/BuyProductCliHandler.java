package com.moesegfault.banking.presentation.cli.investment;

import com.moesegfault.banking.application.investment.command.BuyProductCommand;
import com.moesegfault.banking.application.investment.command.BuyProductHandler;
import com.moesegfault.banking.application.investment.result.InvestmentOrderResult;
import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.investment.RiskLevel;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 投资买入 CLI 处理器（Buy Product CLI Handler），处理 `investment buy` 命令；
 *        Buy-product CLI handler for `investment buy`.
 */
public final class BuyProductCliHandler implements CliCommandHandler {

    /**
     * @brief 买入应用服务（Buy Product Application Service）；
     *        Buy-product application service.
     */
    private final BuyProductHandler buyProductHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造处理器并默认输出到标准输出（Construct Handler with Standard Output）；
     *        Construct handler using standard output stream.
     *
     * @param buyProductHandler 买入应用服务（Buy-product application service）。
     */
    public BuyProductCliHandler(final BuyProductHandler buyProductHandler) {
        this(buyProductHandler, System.out);
    }

    /**
     * @brief 构造处理器（Construct Buy Product CLI Handler）；
     *        Construct buy-product CLI handler.
     *
     * @param buyProductHandler 买入应用服务（Buy-product application service）。
     * @param output            输出流（Output stream）。
     */
    public BuyProductCliHandler(final BuyProductHandler buyProductHandler, final PrintStream output) {
        this.buyProductHandler = Objects.requireNonNull(
                buyProductHandler,
                "Buy product handler must not be null");
        this.output = Objects.requireNonNull(output, "Output stream must not be null");
    }

    /**
     * @brief 处理买入命令（Handle Buy Command）；
     *        Handle investment-buy command.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final ParsedCommand normalized = Objects.requireNonNull(command, "Parsed command must not be null");

        final String investmentAccountId = requiredOption(
                normalized,
                "investment-account-id",
                "investment_account_id");
        final String productCode = requiredOption(normalized, "product-code", "product_code");
        final BigDecimal quantity = parseBigDecimal(requiredOption(normalized, "quantity"));
        final BigDecimal price = parseBigDecimal(requiredOption(normalized, "price"));
        final BigDecimal feeAmount = optionalOption(normalized, "fee-amount", "fee_amount").map(
                BuyProductCliHandler::parseBigDecimal).orElse(null);
        final String initiatorCustomerId = optionalOption(
                normalized,
                "initiator-customer-id",
                "initiator_customer_id",
                "customer-id",
                "customer_id").orElse(null);
        final BusinessChannel businessChannel = optionalOption(normalized, "channel")
                .map(BuyProductCliHandler::parseBusinessChannel)
                .orElse(null);
        final String referenceNo = optionalOption(normalized, "reference-no", "reference_no").orElse(null);
        final RiskLevel customerRiskTolerance = optionalOption(
                normalized,
                "customer-risk-tolerance",
                "customer_risk_tolerance",
                "risk-tolerance",
                "risk_tolerance").map(BuyProductCliHandler::parseRiskLevel).orElse(null);
        final Instant tradeAt = optionalOption(normalized, "trade-at", "trade_at")
                .map(BuyProductCliHandler::parseInstant)
                .orElse(null);

        final BuyProductCommand buyCommand = new BuyProductCommand(
                investmentAccountId,
                productCode,
                quantity,
                price,
                feeAmount,
                initiatorCustomerId,
                businessChannel,
                referenceNo,
                customerRiskTolerance,
                tradeAt);
        final InvestmentOrderResult result = buyProductHandler.handle(buyCommand);
        printOrderResult(result);
    }

    /**
     * @brief 打印订单结果（Print Order Result）；
     *        Print investment order result in one-line key-value format.
     *
     * @param result 订单结果（Investment order result）。
     */
    private void printOrderResult(final InvestmentOrderResult result) {
        output.printf(
                Locale.ROOT,
                "order_id=%s reference_no=%s transaction_status=%s investment_account_id=%s product_id=%s "
                        + "product_code=%s order_side=%s quantity=%s price=%s gross_amount=%s fee_amount=%s "
                        + "currency_code=%s order_status=%s trade_at=%s settlement_at=%s cash_impact=%s "
                        + "holding_quantity_after=%s%n",
                result.orderId(),
                result.referenceNo(),
                result.transactionStatus(),
                result.investmentAccountId(),
                result.productId(),
                result.productCode(),
                result.orderSide(),
                result.quantity(),
                result.price(),
                result.grossAmount(),
                result.feeAmount(),
                result.currencyCode(),
                result.orderStatus(),
                result.tradeAt(),
                result.settlementAtOrNull(),
                result.cashImpact(),
                result.holdingQuantityAfter());
    }

    /**
     * @brief 读取必填参数（Read Required Option）；
     *        Read required option with multiple alias names.
     *
     * @param command     已解析命令（Parsed command）。
     * @param optionNames 参数名列表（Option-name aliases）。
     * @return 参数值（Option value）。
     */
    private static String requiredOption(final ParsedCommand command, final String... optionNames) {
        return optionalOption(command, optionNames)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Missing required option: --" + String.join(" / --", optionNames)));
    }

    /**
     * @brief 读取可选参数（Read Optional Option）；
     *        Read optional option with multiple alias names.
     *
     * @param command     已解析命令（Parsed command）。
     * @param optionNames 参数名列表（Option-name aliases）。
     * @return 参数值可选（Optional option value）。
     */
    private static Optional<String> optionalOption(final ParsedCommand command, final String... optionNames) {
        for (String optionName : optionNames) {
            final Optional<String> value = command.option(optionName);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }

    /**
     * @brief 解析十进制数（Parse Decimal）；
     *        Parse decimal value from CLI option.
     *
     * @param rawValue 原始文本（Raw decimal text）。
     * @return 十进制值（Decimal value）。
     */
    private static BigDecimal parseBigDecimal(final String rawValue) {
        return new BigDecimal(Objects.requireNonNull(rawValue, "Decimal value must not be null").trim());
    }

    /**
     * @brief 解析业务渠道（Parse Business Channel）；
     *        Parse business channel from CLI option.
     *
     * @param rawValue 原始渠道文本（Raw channel text）。
     * @return 业务渠道（Business channel）。
     */
    private static BusinessChannel parseBusinessChannel(final String rawValue) {
        return BusinessChannel.valueOf(normalizeEnumToken(rawValue));
    }

    /**
     * @brief 解析风险等级（Parse Risk Level）；
     *        Parse risk level from CLI option.
     *
     * @param rawValue 原始风险等级文本（Raw risk-level text）。
     * @return 风险等级（Risk level）。
     */
    private static RiskLevel parseRiskLevel(final String rawValue) {
        return RiskLevel.valueOf(normalizeEnumToken(rawValue));
    }

    /**
     * @brief 解析时间戳（Parse Instant）；
     *        Parse ISO-8601 timestamp to instant.
     *
     * @param rawValue 原始时间文本（Raw timestamp text）。
     * @return 时间戳（Instant）。
     */
    private static Instant parseInstant(final String rawValue) {
        return Instant.parse(Objects.requireNonNull(rawValue, "Instant value must not be null").trim());
    }

    /**
     * @brief 规范化枚举文本（Normalize Enum Token）；
     *        Normalize enum token by uppercasing and replacing hyphens with underscores.
     *
     * @param rawValue 原始枚举文本（Raw enum token）。
     * @return 规范化文本（Normalized enum token）。
     */
    private static String normalizeEnumToken(final String rawValue) {
        return Objects.requireNonNull(rawValue, "Enum token must not be null")
                .trim()
                .toUpperCase(Locale.ROOT)
                .replace('-', '_');
    }
}
