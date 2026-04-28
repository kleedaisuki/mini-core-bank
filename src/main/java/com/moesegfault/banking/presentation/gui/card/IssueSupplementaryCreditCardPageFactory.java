package com.moesegfault.banking.presentation.gui.card;

import com.moesegfault.banking.application.card.command.IssueSupplementaryCreditCardHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import java.util.Objects;

/**
 * @brief 信用附属卡页面工厂（Issue Supplementary Credit Card Page Factory），创建 `card.issue-supplementary-credit` 的 MVC 三件套；
 *        Page factory creating MVC bundle for `card.issue-supplementary-credit`.
 */
public final class IssueSupplementaryCreditCardPageFactory implements GuiPageFactory {

    /**
     * @brief 应用层处理器（Application Handler）；
     *        Application handler for supplementary credit-card issuance.
     */
    private final IssueSupplementaryCreditCardHandler applicationHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper for user-facing messages.
     */
    private final GuiExceptionHandler exceptionHandler;

    /**
     * @brief 构造页面工厂（Construct Page Factory）；
     *        Construct issue-supplementary-credit-card page factory.
     *
     * @param applicationHandler 应用层处理器（Application handler）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public IssueSupplementaryCreditCardPageFactory(
            final IssueSupplementaryCreditCardHandler applicationHandler,
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
        final IssueSupplementaryCreditCardModel model = new IssueSupplementaryCreditCardModel();
        final IssueSupplementaryCreditCardView view = new IssueSupplementaryCreditCardView();
        final IssueSupplementaryCreditCardController controller =
                new IssueSupplementaryCreditCardController(model, applicationHandler, exceptionHandler);
        view.bindModel(model);
        return new GuiPage(CardGuiPageIds.ISSUE_SUPPLEMENTARY_CREDIT_CARD, model, view, controller);
    }
}
