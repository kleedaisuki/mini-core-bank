package com.moesegfault.banking.presentation.cli.customer;

import com.moesegfault.banking.application.customer.command.RegisterCustomerCommand;
import com.moesegfault.banking.application.customer.command.RegisterCustomerHandler;
import com.moesegfault.banking.application.customer.result.RegisterCustomerResult;
import com.moesegfault.banking.domain.customer.IdentityDocumentType;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 客户注册 CLI 处理器（Register Customer CLI Handler），处理 `customer register` 命令并调用应用层；
 *        Customer-register CLI handler for `customer register`, delegating to application-layer service.
 */
public final class RegisterCustomerCliHandler implements CliCommandHandler {

    /**
     * @brief 注册客户应用服务（Register Customer Application Service）；
     *        Register-customer application service.
     */
    private final RegisterCustomerHandler registerCustomerHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造处理器并默认输出到标准输出（Construct Handler with Standard Output）；
     *        Construct handler using standard output stream.
     *
     * @param registerCustomerHandler 注册客户应用服务（Register-customer application service）。
     */
    public RegisterCustomerCliHandler(final RegisterCustomerHandler registerCustomerHandler) {
        this(registerCustomerHandler, System.out);
    }

    /**
     * @brief 构造处理器（Construct Register Customer CLI Handler）；
     *        Construct register-customer CLI handler.
     *
     * @param registerCustomerHandler 注册客户应用服务（Register-customer application service）。
     * @param output                  输出流（Output stream）。
     */
    public RegisterCustomerCliHandler(
            final RegisterCustomerHandler registerCustomerHandler,
            final PrintStream output
    ) {
        this.registerCustomerHandler = Objects.requireNonNull(
                registerCustomerHandler,
                "Register customer handler must not be null");
        this.output = Objects.requireNonNull(output, "Output stream must not be null");
    }

    /**
     * @brief 处理客户注册命令（Handle Customer Register Command）；
     *        Handle customer-register command.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final ParsedCommand normalized = Objects.requireNonNull(command, "Parsed command must not be null");

        final IdentityDocumentType idType = parseIdentityDocumentType(
                requiredOption(normalized, "id-type", "id_type"));
        final String idNumber = requiredOption(normalized, "id-number", "id_number");
        final String issuingRegion = requiredOption(normalized, "issuing-region", "issuing_region");
        final String mobilePhone = requiredOption(normalized, "mobile-phone", "mobile_phone", "phone");
        final String residentialAddress = requiredOption(
                normalized,
                "residential-address",
                "residential_address");
        final String mailingAddress = requiredOption(normalized, "mailing-address", "mailing_address");
        final boolean usTaxResident = parseBoolean(requiredOption(
                normalized,
                "is-us-tax-resident",
                "is_us_tax_resident",
                "us-tax-resident"));
        final String crsInfo = optionalOption(normalized, "crs-info", "crs_info")
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElse(null);

        final RegisterCustomerCommand registerCommand = new RegisterCustomerCommand(
                idType,
                idNumber,
                issuingRegion,
                mobilePhone,
                residentialAddress,
                mailingAddress,
                usTaxResident,
                crsInfo);

        final RegisterCustomerResult result = registerCustomerHandler.handle(registerCommand);
        output.printf(
                Locale.ROOT,
                "customer_id=%s customer_status=%s registered_at=%s%n",
                result.customerId(),
                result.customerStatus(),
                result.registeredAt());
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
     * @brief 解析证件类型（Parse Identity Document Type）；
     *        Parse identity-document type from CLI option.
     *
     * @param rawType 原始证件类型（Raw identity-document type）。
     * @return 证件类型枚举（Identity-document type enum）。
     */
    private static IdentityDocumentType parseIdentityDocumentType(final String rawType) {
        return IdentityDocumentType.fromDatabaseValue(rawType);
    }

    /**
     * @brief 解析布尔参数（Parse Boolean Option）；
     *        Parse boolean option from common CLI values.
     *
     * @param rawValue 原始布尔文本（Raw boolean text）。
     * @return 布尔值（Parsed boolean value）。
     */
    private static boolean parseBoolean(final String rawValue) {
        final String normalized = rawValue.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "true", "1", "yes", "y" -> true;
            case "false", "0", "no", "n" -> false;
            default -> throw new IllegalArgumentException("Unsupported boolean value: " + rawValue);
        };
    }
}
