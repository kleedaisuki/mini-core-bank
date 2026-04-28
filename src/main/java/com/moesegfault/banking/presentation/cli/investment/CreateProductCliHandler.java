package com.moesegfault.banking.presentation.cli.investment;

import com.moesegfault.banking.application.investment.command.CreateInvestmentProductCommand;
import com.moesegfault.banking.application.investment.command.CreateInvestmentProductHandler;
import com.moesegfault.banking.application.investment.result.InvestmentProductResult;
import com.moesegfault.banking.domain.investment.ProductType;
import com.moesegfault.banking.domain.investment.RiskLevel;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 投资产品创建 CLI 处理器（Create Product CLI Handler），处理 `investment product-create` 命令；
 *        Investment-product create CLI handler for `investment product-create`.
 */
public final class CreateProductCliHandler implements CliCommandHandler {

    /**
     * @brief 创建产品应用服务（Create Product Application Service）；
     *        Create-product application service.
     */
    private final CreateInvestmentProductHandler createInvestmentProductHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造处理器并默认输出到标准输出（Construct Handler with Standard Output）；
     *        Construct handler using standard output stream.
     *
     * @param createInvestmentProductHandler 创建产品应用服务（Create-product application service）。
     */
    public CreateProductCliHandler(final CreateInvestmentProductHandler createInvestmentProductHandler) {
        this(createInvestmentProductHandler, System.out);
    }

    /**
     * @brief 构造处理器（Construct Create Product CLI Handler）；
     *        Construct create-product CLI handler.
     *
     * @param createInvestmentProductHandler 创建产品应用服务（Create-product application service）。
     * @param output                         输出流（Output stream）。
     */
    public CreateProductCliHandler(
            final CreateInvestmentProductHandler createInvestmentProductHandler,
            final PrintStream output
    ) {
        this.createInvestmentProductHandler = Objects.requireNonNull(
                createInvestmentProductHandler,
                "Create investment product handler must not be null");
        this.output = Objects.requireNonNull(output, "Output stream must not be null");
    }

    /**
     * @brief 处理产品创建命令（Handle Product-Create Command）；
     *        Handle investment product-create command.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final ParsedCommand normalized = Objects.requireNonNull(command, "Parsed command must not be null");

        final String productCode = requiredOption(normalized, "product-code", "product_code");
        final String productName = requiredOption(normalized, "product-name", "product_name");
        final ProductType productType = parseProductType(requiredOption(normalized, "product-type", "product_type"));
        final CurrencyCode currencyCode = CurrencyCode.of(requiredOption(
                normalized,
                "currency-code",
                "currency_code",
                "currency"));
        final RiskLevel riskLevel = parseRiskLevel(requiredOption(normalized, "risk-level", "risk_level"));
        final String issuer = requiredOption(normalized, "issuer");

        final CreateInvestmentProductCommand createCommand = new CreateInvestmentProductCommand(
                productCode,
                productName,
                productType,
                currencyCode,
                riskLevel,
                issuer);

        final InvestmentProductResult result = createInvestmentProductHandler.handle(createCommand);
        output.printf(
                Locale.ROOT,
                "product_id=%s product_code=%s product_name=%s product_type=%s currency_code=%s "
                        + "risk_level=%s issuer=%s product_status=%s%n",
                result.productId(),
                result.productCode(),
                result.productName(),
                result.productType(),
                result.currencyCode(),
                result.riskLevel(),
                result.issuer(),
                result.productStatus());
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
     * @brief 解析产品类型（Parse Product Type）；
     *        Parse product type from CLI option.
     *
     * @param rawValue 原始类型文本（Raw product-type text）。
     * @return 产品类型（Product type）。
     */
    private static ProductType parseProductType(final String rawValue) {
        return ProductType.valueOf(normalizeEnumToken(rawValue));
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
