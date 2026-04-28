package com.moesegfault.banking.presentation.cli.customer;

import com.moesegfault.banking.application.customer.query.ListCustomersHandler;
import com.moesegfault.banking.application.customer.query.ListCustomersQuery;
import com.moesegfault.banking.application.customer.result.CustomerResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 客户列表 CLI 处理器（List Customers CLI Handler），处理 `customer list` 命令并输出列表；
 *        Customer-list CLI handler for `customer list`, querying and printing customers.
 */
public final class ListCustomersCliHandler implements CliCommandHandler {

    /**
     * @brief 客户列表查询应用服务（List Customers Application Service）；
     *        List-customers application service.
     */
    private final ListCustomersHandler listCustomersHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造处理器并默认输出到标准输出（Construct Handler with Standard Output）；
     *        Construct handler using standard output stream.
     *
     * @param listCustomersHandler 客户列表查询应用服务（List-customers application service）。
     */
    public ListCustomersCliHandler(final ListCustomersHandler listCustomersHandler) {
        this(listCustomersHandler, System.out);
    }

    /**
     * @brief 构造处理器（Construct List Customers CLI Handler）；
     *        Construct list-customers CLI handler.
     *
     * @param listCustomersHandler 客户列表查询应用服务（List-customers application service）。
     * @param output               输出流（Output stream）。
     */
    public ListCustomersCliHandler(final ListCustomersHandler listCustomersHandler, final PrintStream output) {
        this.listCustomersHandler = Objects.requireNonNull(
                listCustomersHandler,
                "List customers handler must not be null");
        this.output = Objects.requireNonNull(output, "Output stream must not be null");
    }

    /**
     * @brief 处理客户列表命令（Handle Customer List Command）；
     *        Handle customer-list command.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final ParsedCommand normalized = Objects.requireNonNull(command, "Parsed command must not be null");

        final Optional<String> mobilePhoneOption = optionalOption(
                normalized,
                "mobile-phone",
                "mobile_phone",
                "phone");
        final ListCustomersQuery query = mobilePhoneOption
                .map(ListCustomersQuery::byMobilePhone)
                .orElseGet(ListCustomersQuery::all);

        final List<CustomerResult> customers = listCustomersHandler.handle(query);
        output.printf(Locale.ROOT, "total=%d%n", customers.size());

        for (CustomerResult customer : customers) {
            output.printf(
                    Locale.ROOT,
                    "customer_id=%s id_type=%s id_number=%s issuing_region=%s mobile_phone=%s "
                            + "customer_status=%s%n",
                    customer.customerId(),
                    customer.idType(),
                    customer.idNumber(),
                    customer.issuingRegion(),
                    customer.mobilePhone(),
                    customer.customerStatus());
        }
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
}
