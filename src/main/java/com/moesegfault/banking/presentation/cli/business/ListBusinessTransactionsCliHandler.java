package com.moesegfault.banking.presentation.cli.business;

import com.moesegfault.banking.application.business.query.ListBusinessTransactionsHandler;
import com.moesegfault.banking.application.business.query.ListBusinessTransactionsQuery;
import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.domain.business.BusinessTransactionStatus;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 业务流水列表 CLI 处理器（List Business Transactions CLI Handler），处理 `business list` 命令并输出列表；
 *        Business-transaction list CLI handler for `business list`, querying and printing transactions.
 */
public final class ListBusinessTransactionsCliHandler implements CliCommandHandler {

    /**
     * @brief 业务流水列表查询应用服务（List Business Transactions Application Service）；
     *        List-business-transactions application service.
     */
    private final ListBusinessTransactionsHandler listBusinessTransactionsHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造处理器并默认输出到标准输出（Construct Handler with Standard Output）；
     *        Construct handler using standard output stream.
     *
     * @param listBusinessTransactionsHandler 业务流水列表查询应用服务（List-business-transactions application service）。
     */
    public ListBusinessTransactionsCliHandler(final ListBusinessTransactionsHandler listBusinessTransactionsHandler) {
        this(listBusinessTransactionsHandler, System.out);
    }

    /**
     * @brief 构造处理器（Construct List Business Transactions CLI Handler）；
     *        Construct list-business-transactions CLI handler.
     *
     * @param listBusinessTransactionsHandler 业务流水列表查询应用服务（List-business-transactions application service）。
     * @param output                          输出流（Output stream）。
     */
    public ListBusinessTransactionsCliHandler(
            final ListBusinessTransactionsHandler listBusinessTransactionsHandler,
            final PrintStream output
    ) {
        this.listBusinessTransactionsHandler = Objects.requireNonNull(
                listBusinessTransactionsHandler,
                "List business transactions handler must not be null");
        this.output = Objects.requireNonNull(output, "Output stream must not be null");
    }

    /**
     * @brief 处理业务流水列表命令（Handle Business Transaction List Command）；
     *        Handle business-transaction list command.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final ParsedCommand normalized = Objects.requireNonNull(command, "Parsed command must not be null");

        final String initiatorCustomerId = optionalOption(
                normalized,
                "initiator-customer-id",
                "initiator_customer_id",
                "customer-id",
                "customer_id").orElse(null);

        final BusinessTransactionStatus transactionStatus = optionalOption(
                normalized,
                "transaction-status",
                "transaction_status",
                "status")
                .map(ListBusinessTransactionsCliHandler::parseTransactionStatus)
                .orElse(null);

        final int limit = optionalOption(normalized, "limit")
                .map(ListBusinessTransactionsCliHandler::parseLimit)
                .orElse(ListBusinessTransactionsQuery.DEFAULT_LIMIT);

        final ListBusinessTransactionsQuery query = new ListBusinessTransactionsQuery(
                initiatorCustomerId,
                transactionStatus,
                limit);

        final List<BusinessTransactionResult> transactions = listBusinessTransactionsHandler.handle(query);
        output.printf(Locale.ROOT, "total=%d%n", transactions.size());

        for (BusinessTransactionResult transaction : transactions) {
            output.printf(
                    Locale.ROOT,
                    "transaction_id=%s business_type_code=%s initiator_customer_id=%s operator_id=%s channel=%s "
                            + "transaction_status=%s requested_at=%s completed_at=%s reference_no=%s remarks=%s%n",
                    transaction.transactionId(),
                    transaction.businessTypeCode(),
                    transaction.initiatorCustomerIdOrNull(),
                    transaction.operatorIdOrNull(),
                    transaction.channel(),
                    transaction.transactionStatus(),
                    transaction.requestedAt(),
                    transaction.completedAtOrNull(),
                    transaction.referenceNo(),
                    transaction.remarksOrNull());
        }
    }

    /**
     * @brief 解析业务交易状态（Parse Business Transaction Status）；
     *        Parse business-transaction status from CLI string.
     *
     * @param rawStatus 原始状态文本（Raw status text）。
     * @return 业务交易状态枚举（Business transaction status enum）。
     */
    private static BusinessTransactionStatus parseTransactionStatus(final String rawStatus) {
        final String normalized = rawStatus.trim().toUpperCase(Locale.ROOT);
        return BusinessTransactionStatus.valueOf(normalized);
    }

    /**
     * @brief 解析返回上限（Parse Result Limit）；
     *        Parse result-limit option.
     *
     * @param rawLimit 原始上限文本（Raw limit text）。
     * @return 返回上限（Result limit）。
     */
    private static int parseLimit(final String rawLimit) {
        try {
            return Integer.parseInt(rawLimit.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Unsupported limit value: " + rawLimit, exception);
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
