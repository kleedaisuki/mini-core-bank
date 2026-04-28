package com.moesegfault.banking.presentation.cli.customer;

import com.moesegfault.banking.application.customer.query.FindCustomerHandler;
import com.moesegfault.banking.application.customer.query.FindCustomerQuery;
import com.moesegfault.banking.application.customer.result.CustomerResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 客户详情 CLI 处理器（Show Customer CLI Handler），处理 `customer show` 命令并查询单客户；
 *        Customer-show CLI handler for `customer show`, querying one customer.
 */
public final class ShowCustomerCliHandler implements CliCommandHandler {

    /**
     * @brief 查询客户应用服务（Find Customer Application Service）；
     *        Find-customer application service.
     */
    private final FindCustomerHandler findCustomerHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造处理器并默认输出到标准输出（Construct Handler with Standard Output）；
     *        Construct handler using standard output stream.
     *
     * @param findCustomerHandler 查询客户应用服务（Find-customer application service）。
     */
    public ShowCustomerCliHandler(final FindCustomerHandler findCustomerHandler) {
        this(findCustomerHandler, System.out);
    }

    /**
     * @brief 构造处理器（Construct Show Customer CLI Handler）；
     *        Construct show-customer CLI handler.
     *
     * @param findCustomerHandler 查询客户应用服务（Find-customer application service）。
     * @param output              输出流（Output stream）。
     */
    public ShowCustomerCliHandler(final FindCustomerHandler findCustomerHandler, final PrintStream output) {
        this.findCustomerHandler = Objects.requireNonNull(
                findCustomerHandler,
                "Find customer handler must not be null");
        this.output = Objects.requireNonNull(output, "Output stream must not be null");
    }

    /**
     * @brief 处理客户详情命令（Handle Customer Show Command）；
     *        Handle customer-show command.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final ParsedCommand normalized = Objects.requireNonNull(command, "Parsed command must not be null");
        final String customerId = requiredOption(normalized, "customer-id", "customer_id");

        final Optional<CustomerResult> customer = findCustomerHandler.handle(new FindCustomerQuery(customerId));
        if (customer.isEmpty()) {
            output.printf(Locale.ROOT, "customer_not_found customer_id=%s%n", customerId);
            return;
        }

        final CustomerResult result = customer.orElseThrow();
        output.printf(
                Locale.ROOT,
                "customer_id=%s id_type=%s id_number=%s issuing_region=%s mobile_phone=%s residential_address=%s "
                        + "mailing_address=%s is_us_tax_resident=%s crs_info=%s customer_status=%s created_at=%s updated_at=%s%n",
                result.customerId(),
                result.idType(),
                result.idNumber(),
                result.issuingRegion(),
                result.mobilePhone(),
                result.residentialAddress(),
                result.mailingAddress(),
                result.usTaxResident(),
                result.crsInfo(),
                result.customerStatus(),
                result.createdAt(),
                result.updatedAt());
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
        for (String optionName : optionNames) {
            final Optional<String> value = command.option(optionName);
            if (value.isPresent()) {
                return value.orElseThrow();
            }
        }
        throw new IllegalArgumentException("Missing required option: --" + String.join(" / --", optionNames));
    }
}
