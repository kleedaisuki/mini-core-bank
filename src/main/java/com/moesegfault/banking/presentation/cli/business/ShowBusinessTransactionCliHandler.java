package com.moesegfault.banking.presentation.cli.business;

import com.moesegfault.banking.application.business.query.FindBusinessTransactionHandler;
import com.moesegfault.banking.application.business.query.FindBusinessTransactionQuery;
import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 业务流水详情 CLI 处理器（Show Business Transaction CLI Handler），处理 `business show` 命令并查询单笔流水；
 *        Business-transaction show CLI handler for `business show`, querying one transaction.
 */
public final class ShowBusinessTransactionCliHandler implements CliCommandHandler {

    /**
     * @brief 单笔业务流水查询应用服务（Find Business Transaction Application Service）；
     *        Find-business-transaction application service.
     */
    private final FindBusinessTransactionHandler findBusinessTransactionHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造处理器并默认输出到标准输出（Construct Handler with Standard Output）；
     *        Construct handler using standard output stream.
     *
     * @param findBusinessTransactionHandler 单笔业务流水查询应用服务（Find-business-transaction application service）。
     */
    public ShowBusinessTransactionCliHandler(final FindBusinessTransactionHandler findBusinessTransactionHandler) {
        this(findBusinessTransactionHandler, System.out);
    }

    /**
     * @brief 构造处理器（Construct Show Business Transaction CLI Handler）；
     *        Construct show-business-transaction CLI handler.
     *
     * @param findBusinessTransactionHandler 单笔业务流水查询应用服务（Find-business-transaction application service）。
     * @param output                         输出流（Output stream）。
     */
    public ShowBusinessTransactionCliHandler(
            final FindBusinessTransactionHandler findBusinessTransactionHandler,
            final PrintStream output
    ) {
        this.findBusinessTransactionHandler = Objects.requireNonNull(
                findBusinessTransactionHandler,
                "Find business transaction handler must not be null");
        this.output = Objects.requireNonNull(output, "Output stream must not be null");
    }

    /**
     * @brief 处理业务流水详情命令（Handle Business Transaction Show Command）；
     *        Handle business-transaction show command.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final ParsedCommand normalized = Objects.requireNonNull(command, "Parsed command must not be null");

        final Optional<String> transactionIdOption = optionalOption(
                normalized,
                "transaction-id",
                "transaction_id");
        final Optional<String> referenceNoOption = optionalOption(
                normalized,
                "reference-no",
                "reference_no",
                "reference");

        final FindBusinessTransactionQuery query = createQuery(transactionIdOption, referenceNoOption);
        final Optional<BusinessTransactionResult> transaction = findBusinessTransactionHandler.handle(query);

        if (transaction.isEmpty()) {
            printNotFound(query);
            return;
        }

        printTransaction(transaction.orElseThrow());
    }

    /**
     * @brief 创建查询对象（Create Find Query）；
     *        Create find-business-transaction query from optional selectors.
     *
     * @param transactionIdOption 交易 ID 选择器（Transaction-ID selector option）。
     * @param referenceNoOption   参考号选择器（Reference-number selector option）。
     * @return 查询对象（Find query）。
     */
    private static FindBusinessTransactionQuery createQuery(
            final Optional<String> transactionIdOption,
            final Optional<String> referenceNoOption
    ) {
        final boolean hasTransactionId = transactionIdOption.isPresent();
        final boolean hasReferenceNo = referenceNoOption.isPresent();
        if (hasTransactionId == hasReferenceNo) {
            throw new IllegalArgumentException(
                    "Exactly one selector is required: --transaction-id or --reference-no");
        }

        if (hasTransactionId) {
            return FindBusinessTransactionQuery.byTransactionId(transactionIdOption.orElseThrow());
        }
        return FindBusinessTransactionQuery.byReferenceNo(referenceNoOption.orElseThrow());
    }

    /**
     * @brief 打印未命中提示（Print Not-Found Message）；
     *        Print not-found message for the applied selector.
     *
     * @param query 查询对象（Find query）。
     */
    private void printNotFound(final FindBusinessTransactionQuery query) {
        if (query.hasTransactionId()) {
            output.printf(Locale.ROOT, "business_transaction_not_found transaction_id=%s%n", query.transactionIdOrNull());
            return;
        }
        output.printf(Locale.ROOT, "business_transaction_not_found reference_no=%s%n", query.referenceNoOrNull());
    }

    /**
     * @brief 打印业务流水详情（Print Business Transaction Detail）；
     *        Print business-transaction detail in key-value schema.
     *
     * @param transaction 业务流水结果（Business transaction result）。
     */
    private void printTransaction(final BusinessTransactionResult transaction) {
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
