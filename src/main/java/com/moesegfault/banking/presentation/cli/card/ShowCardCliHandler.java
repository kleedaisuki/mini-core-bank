package com.moesegfault.banking.presentation.cli.card;

import com.moesegfault.banking.application.card.query.FindCardHandler;
import com.moesegfault.banking.application.card.query.FindCardQuery;
import com.moesegfault.banking.application.card.result.CardResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.Objects;

/**
 * @brief 卡详情 CLI 处理器（Show Card CLI Handler），处理 `card show` 命令并查询统一卡视图；
 *        CLI handler for `card show`, querying unified card read model.
 */
public final class ShowCardCliHandler implements CliCommandHandler {

    /**
     * @brief 查询卡应用服务（Find Card Application Service）；
     *        Application handler for finding one card.
     */
    private final FindCardHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct show-card CLI handler using `System.out`.
     *
     * @param applicationHandler 查询卡应用服务（Application handler）。
     */
    public ShowCardCliHandler(final FindCardHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct show-card CLI handler.
     *
     * @param applicationHandler 查询卡应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public ShowCardCliHandler(final FindCardHandler applicationHandler, final PrintStream output) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for showing one card.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final String cardId = CardCliOptionReader.requiredOption(command, "card_id", "card-id", "cardId");
        final CardResult result = applicationHandler.handle(new FindCardQuery(cardId));
        CardCliOutputFormatter.printCardResult(output, result);
    }
}
