package com.moesegfault.banking.presentation.cli;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief CLI 帮助目录（CLI Help Catalog），集中维护命令用途、参数与示例；
 *        CLI help catalog that centralizes command purpose, options, and examples.
 */
public final class CliHelpCatalog {

    /**
     * @brief 命令帮助条目列表（Command Help Entry List）；
     *        Command help entry list.
     */
    private static final List<CommandHelp> COMMANDS = List.of(
            command(
                    "gui",
                    "Launch the desktop GUI from one-shot CLI mode.",
                    List.of(),
                    List.of("--toolkit <swing>"),
                    "gui --toolkit swing"),
            command(
                    "customer register",
                    "Register a new customer profile after identity, address, phone, and tax checks.",
                    List.of(
                            "--id-type <type>",
                            "--id-number <number>",
                            "--issuing-region <region>",
                            "--mobile-phone <phone>",
                            "--residential-address <address>",
                            "--mailing-address <address>",
                            "--is-us-tax-resident <true|false>"),
                    List.of("--crs-info <text>"),
                    "customer register --id-type HKID --id-number A1234567 --issuing-region HK "
                            + "--mobile-phone +85260000000 --residential-address 'Hong Kong' "
                            + "--mailing-address 'Hong Kong' --is-us-tax-resident false"),
            command(
                    "customer show",
                    "Show one customer by customer ID.",
                    List.of("--customer-id <id>"),
                    List.of(),
                    "customer show --customer-id cust-001"),
            command(
                    "customer list",
                    "List customers, optionally filtered by mobile phone.",
                    List.of(),
                    List.of("--mobile-phone <phone>"),
                    "customer list --mobile-phone +85260000000"),
            command(
                    "account open-savings",
                    "Open a savings account for an eligible customer.",
                    List.of("--customer-id <id>", "--account-no <number>"),
                    List.of(),
                    "account open-savings --customer-id cust-001 --account-no SAV-0001"),
            command(
                    "account open-fx",
                    "Open an FX account linked to an existing savings account.",
                    List.of("--customer-id <id>", "--account-no <number>", "--linked-savings-account-id <id>"),
                    List.of(),
                    "account open-fx --customer-id cust-001 --account-no FX-0001 --linked-savings-account-id sav-001"),
            command(
                    "account open-investment",
                    "Open an investment account for an eligible customer.",
                    List.of("--customer-id <id>", "--account-no <number>"),
                    List.of(),
                    "account open-investment --customer-id cust-001 --account-no INV-0001"),
            command(
                    "account show",
                    "Show one account by account ID or account number.",
                    List.of("exactly one of --account-id <id> or --account-no <number>"),
                    List.of(),
                    "account show --account-id acc-001"),
            command(
                    "account list",
                    "List accounts owned by one customer.",
                    List.of("--customer-id <id>"),
                    List.of("--include-closed-accounts <true|false>"),
                    "account list --customer-id cust-001 --include-closed-accounts false"),
            command(
                    "account freeze",
                    "Freeze an account with a business reason.",
                    List.of("--account-id <id>", "--freeze-reason <reason>"),
                    List.of(),
                    "account freeze --account-id acc-001 --freeze-reason 'risk review'"),
            command(
                    "card issue-debit",
                    "Issue a primary debit card bound to savings and FX accounts.",
                    List.of(
                            "--holder-customer-id <id>",
                            "--savings-account-id <id>",
                            "--fx-account-id <id>",
                            "--card-no <number>"),
                    List.of(),
                    "card issue-debit --holder-customer-id cust-001 --savings-account-id sav-001 "
                            + "--fx-account-id fx-001 --card-no 4000000000000001"),
            command(
                    "card issue-supplementary-debit",
                    "Issue one supplementary debit card under a primary debit card.",
                    List.of("--holder-customer-id <id>", "--primary-debit-card-id <id>", "--card-no <number>"),
                    List.of(),
                    "card issue-supplementary-debit --holder-customer-id cust-002 "
                            + "--primary-debit-card-id card-001 --card-no 4000000000000002"),
            command(
                    "card issue-credit",
                    "Issue a primary credit card for a credit-card account.",
                    List.of("--holder-customer-id <id>", "--credit-card-account-id <id>", "--card-no <number>"),
                    List.of(),
                    "card issue-credit --holder-customer-id cust-001 --credit-card-account-id cca-001 "
                            + "--card-no 5000000000000001"),
            command(
                    "card issue-supplementary-credit",
                    "Issue a supplementary credit card under a primary credit card.",
                    List.of(
                            "--holder-customer-id <id>",
                            "--primary-credit-card-id <id>",
                            "--credit-card-account-id <id>",
                            "--card-no <number>"),
                    List.of(),
                    "card issue-supplementary-credit --holder-customer-id cust-002 "
                            + "--primary-credit-card-id card-001 --credit-card-account-id cca-001 "
                            + "--card-no 5000000000000002"),
            command(
                    "card show",
                    "Show one card from the unified card read model.",
                    List.of("--card-id <id>"),
                    List.of(),
                    "card show --card-id card-001"),
            command(
                    "credit generate-statement",
                    "Generate a credit-card statement for the derived billing period.",
                    List.of(
                            "--credit-card-account-id <id>",
                            "--statement-date <yyyy-mm-dd>",
                            "--minimum-payment-rate-decimal <decimal>",
                            "--minimum-payment-floor-amount <amount>"),
                    List.of(),
                    "credit generate-statement --credit-card-account-id cca-001 --statement-date 2026-05-01 "
                            + "--minimum-payment-rate-decimal 0.10 --minimum-payment-floor-amount 50.00"),
            command(
                    "credit repay",
                    "Apply a credit-card repayment to one account and optionally one statement.",
                    List.of(
                            "--credit-card-account-id <id>",
                            "--repayment-amount <amount>",
                            "--repayment-currency-code <currency>"),
                    List.of("--statement-id <id>", "--source-account-id <id>", "--as-of-date <yyyy-mm-dd>"),
                    "credit repay --credit-card-account-id cca-001 --repayment-amount 100.00 "
                            + "--repayment-currency-code USD"),
            command(
                    "credit statement",
                    "Show one credit-card statement by statement ID or by account and period.",
                    List.of(
                            "either --statement-id <id>",
                            "or --credit-card-account-id <id> --statement-period-start <yyyy-mm-dd> "
                                    + "--statement-period-end <yyyy-mm-dd>"),
                    List.of(),
                    "credit statement --statement-id stmt-001"),
            command(
                    "ledger balance",
                    "Show the ledger and available balance for one account and currency.",
                    List.of("--account-id <id>", "--currency-code <currency>"),
                    List.of(),
                    "ledger balance --account-id acc-001 --currency-code USD"),
            command(
                    "ledger entries",
                    "List recent ledger entries for one account.",
                    List.of("--account-id <id>"),
                    List.of("--limit <count>"),
                    "ledger entries --account-id acc-001 --limit 20"),
            command(
                    "business list",
                    "List business transactions with optional customer/status filters.",
                    List.of(),
                    List.of("--initiator-customer-id <id>", "--transaction-status <status>", "--limit <count>"),
                    "business list --initiator-customer-id cust-001 --transaction-status COMPLETED --limit 20"),
            command(
                    "business show",
                    "Show one business transaction by transaction ID or reference number.",
                    List.of("exactly one of --transaction-id <id> or --reference-no <number>"),
                    List.of(),
                    "business show --transaction-id txn-001"),
            command(
                    "investment product-create",
                    "Create an investment product that can later be bought or sold.",
                    List.of(
                            "--product-code <code>",
                            "--product-name <name>",
                            "--product-type <type>",
                            "--currency-code <currency>",
                            "--risk-level <level>",
                            "--issuer <issuer>"),
                    List.of(),
                    "investment product-create --product-code FUND001 --product-name 'Income Fund' "
                            + "--product-type FUND --currency-code USD --risk-level MEDIUM --issuer 'Mini Bank'"),
            command(
                    "investment buy",
                    "Place and settle a buy order for an investment product.",
                    List.of(
                            "--investment-account-id <id>",
                            "--product-code <code>",
                            "--quantity <decimal>",
                            "--price <amount>"),
                    List.of(
                            "--fee-amount <amount>",
                            "--initiator-customer-id <id>",
                            "--channel <channel>",
                            "--reference-no <number>",
                            "--customer-risk-tolerance <level>",
                            "--trade-at <instant>"),
                    "investment buy --investment-account-id inv-001 --product-code FUND001 "
                            + "--quantity 10 --price 12.34"),
            command(
                    "investment sell",
                    "Place and settle a sell order for an investment product.",
                    List.of(
                            "--investment-account-id <id>",
                            "--product-code <code>",
                            "--quantity <decimal>",
                            "--price <amount>"),
                    List.of(
                            "--fee-amount <amount>",
                            "--initiator-customer-id <id>",
                            "--channel <channel>",
                            "--reference-no <number>",
                            "--trade-at <instant>"),
                    "investment sell --investment-account-id inv-001 --product-code FUND001 "
                            + "--quantity 5 --price 12.50"),
            command(
                    "investment holdings",
                    "List holdings under one investment account.",
                    List.of("--investment-account-id <id>"),
                    List.of("--include-product-details <true|false>"),
                    "investment holdings --investment-account-id inv-001 --include-product-details true"));

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for catalog utility class.
     */
    private CliHelpCatalog() {
    }

    /**
     * @brief 返回全部命令帮助（Get All Command Help Entries）；
     *        Get all command help entries.
     *
     * @return 命令帮助条目列表（Command help entry list）。
     */
    public static List<CommandHelp> commands() {
        return COMMANDS;
    }

    /**
     * @brief 按命令路径查找帮助（Find Help by Command Path）；
     *        Find help entry by command path.
     *
     * @param commandPath 命令路径（Command path）。
     * @return 命令帮助条目可选值（Optional command help entry）。
     */
    public static Optional<CommandHelp> find(final String commandPath) {
        final String normalizedCommandPath = Objects.requireNonNull(commandPath, "commandPath must not be null").trim();
        return COMMANDS.stream()
                .filter(command -> command.commandPath().equals(normalizedCommandPath))
                .findFirst();
    }

    /**
     * @brief 创建命令帮助条目（Create Command Help Entry）；
     *        Create one command help entry.
     *
     * @param commandPath     命令路径（Command path）。
     * @param summary         摘要说明（Summary）。
     * @param requiredOptions 必填参数列表（Required option list）。
     * @param optionalOptions 可选参数列表（Optional option list）。
     * @param example         示例命令（Example command）。
     * @return 命令帮助条目（Command help entry）。
     */
    private static CommandHelp command(
            final String commandPath,
            final String summary,
            final List<String> requiredOptions,
            final List<String> optionalOptions,
            final String example
    ) {
        return new CommandHelp(commandPath, summary, requiredOptions, optionalOptions, example);
    }

    /**
     * @brief 命令帮助条目（Command Help Entry），描述一个 CLI 命令的人类可读说明；
     *        Command help entry that describes one CLI command for humans.
     *
     * @param commandPath     命令路径（Command path）。
     * @param summary         摘要说明（Summary）。
     * @param requiredOptions 必填参数列表（Required option list）。
     * @param optionalOptions 可选参数列表（Optional option list）。
     * @param example         示例命令（Example command）。
     */
    public record CommandHelp(
            String commandPath,
            String summary,
            List<String> requiredOptions,
            List<String> optionalOptions,
            String example) {

        /**
         * @brief 构造并校验命令帮助条目（Construct and Validate Command Help Entry）；
         *        Construct and validate command help entry.
         */
        public CommandHelp {
            if (commandPath == null || commandPath.isBlank()) {
                throw new IllegalArgumentException("commandPath must not be blank");
            }
            if (summary == null || summary.isBlank()) {
                throw new IllegalArgumentException("summary must not be blank");
            }
            requiredOptions = List.copyOf(Objects.requireNonNull(requiredOptions, "requiredOptions must not be null"));
            optionalOptions = List.copyOf(Objects.requireNonNull(optionalOptions, "optionalOptions must not be null"));
            if (example == null || example.isBlank()) {
                throw new IllegalArgumentException("example must not be blank");
            }
        }
    }
}
