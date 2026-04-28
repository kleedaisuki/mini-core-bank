package com.moesegfault.banking.presentation.cli.investment;

import com.moesegfault.banking.application.investment.query.ListHoldingsHandler;
import com.moesegfault.banking.application.investment.query.ListHoldingsQuery;
import com.moesegfault.banking.application.investment.result.HoldingResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 投资持仓 CLI 处理器（Show Holding CLI Handler），处理 `investment holdings` 命令；
 *        Holdings-query CLI handler for `investment holdings`.
 */
public final class ShowHoldingCliHandler implements CliCommandHandler {

    /**
     * @brief 持仓查询应用服务（List Holdings Application Service）；
     *        List-holdings application service.
     */
    private final ListHoldingsHandler listHoldingsHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造处理器并默认输出到标准输出（Construct Handler with Standard Output）；
     *        Construct handler using standard output stream.
     *
     * @param listHoldingsHandler 持仓查询应用服务（List-holdings application service）。
     */
    public ShowHoldingCliHandler(final ListHoldingsHandler listHoldingsHandler) {
        this(listHoldingsHandler, System.out);
    }

    /**
     * @brief 构造处理器（Construct Show Holding CLI Handler）；
     *        Construct show-holding CLI handler.
     *
     * @param listHoldingsHandler 持仓查询应用服务（List-holdings application service）。
     * @param output              输出流（Output stream）。
     */
    public ShowHoldingCliHandler(final ListHoldingsHandler listHoldingsHandler, final PrintStream output) {
        this.listHoldingsHandler = Objects.requireNonNull(
                listHoldingsHandler,
                "List holdings handler must not be null");
        this.output = Objects.requireNonNull(output, "Output stream must not be null");
    }

    /**
     * @brief 处理持仓命令（Handle Holdings Command）；
     *        Handle investment-holdings command.
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
        final boolean includeProductDetails = optionalOption(
                normalized,
                "include-product-details",
                "include_product_details",
                "details").map(ShowHoldingCliHandler::parseBoolean).orElse(false);

        final List<HoldingResult> holdings = listHoldingsHandler.handle(new ListHoldingsQuery(
                investmentAccountId,
                includeProductDetails));

        output.printf(Locale.ROOT, "total=%d%n", holdings.size());
        for (HoldingResult holding : holdings) {
            output.printf(
                    Locale.ROOT,
                    "holding_id=%s investment_account_id=%s product_id=%s product_code=%s product_name=%s "
                            + "product_type=%s quantity=%s average_cost=%s cost_currency_code=%s market_value=%s "
                            + "valuation_currency_code=%s unrealized_pnl=%s updated_at=%s%n",
                    holding.holdingId(),
                    holding.investmentAccountId(),
                    holding.productId(),
                    holding.productCodeOrNull(),
                    holding.productNameOrNull(),
                    holding.productTypeOrNull(),
                    holding.quantity(),
                    holding.averageCost(),
                    holding.costCurrencyCode(),
                    holding.marketValue(),
                    holding.valuationCurrencyCode(),
                    holding.unrealizedPnl(),
                    holding.updatedAt());
        }
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
     * @brief 解析布尔参数（Parse Boolean Option）；
     *        Parse boolean option from common CLI values.
     *
     * @param rawValue 原始布尔文本（Raw boolean text）。
     * @return 布尔值（Parsed boolean value）。
     */
    private static boolean parseBoolean(final String rawValue) {
        final String normalized = Objects.requireNonNull(rawValue, "Boolean value must not be null")
                .trim()
                .toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "true", "1", "yes", "y" -> true;
            case "false", "0", "no", "n" -> false;
            default -> throw new IllegalArgumentException("Unsupported boolean value: " + rawValue);
        };
    }
}
