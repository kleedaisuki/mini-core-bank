package com.moesegfault.banking.presentation.gui.card;

import com.moesegfault.banking.application.card.command.IssueCreditCardHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import java.util.Objects;

/**
 * @brief 主信用卡页面工厂（Issue Credit Card Page Factory），创建 `card.issue-credit` 的 MVC 三件套；
 *        Page factory creating MVC bundle for `card.issue-credit`.
 */
public final class IssueCreditCardPageFactory implements GuiPageFactory {

    /**
     * @brief 应用层处理器（Application Handler）；
     *        Application handler for primary credit-card issuance.
     */
    private final IssueCreditCardHandler applicationHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper for user-facing messages.
     */
    private final GuiExceptionHandler exceptionHandler;

    /**
     * @brief 构造页面工厂（Construct Page Factory）；
     *        Construct issue-credit-card page factory.
     *
     * @param applicationHandler 应用层处理器（Application handler）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public IssueCreditCardPageFactory(
            final IssueCreditCardHandler applicationHandler,
            final GuiExceptionHandler exceptionHandler
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.exceptionHandler = Objects.requireNonNull(exceptionHandler, "exceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuiPage createPage(final GuiContext context) {
        Objects.requireNonNull(context, "context must not be null");
        final IssueCreditCardModel model = new IssueCreditCardModel();
        final IssueCreditCardView view = new IssueCreditCardView();
        final IssueCreditCardController controller = new IssueCreditCardController(model, applicationHandler, exceptionHandler);
        view.bindModel(model);
        return new GuiPage(CardGuiPageIds.ISSUE_CREDIT_CARD, model, view, controller);
    }
}
