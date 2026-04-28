package com.moesegfault.banking.presentation.gui.card;

import com.moesegfault.banking.application.card.command.IssueDebitCardHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import java.util.Objects;

/**
 * @brief 主借记卡页面工厂（Issue Debit Card Page Factory），创建 `card.issue-debit` 的 MVC 三件套；
 *        Page factory creating MVC bundle for `card.issue-debit`.
 */
public final class IssueDebitCardPageFactory implements GuiPageFactory {

    /**
     * @brief 应用层处理器（Application Handler）；
     *        Application handler for debit-card issuance.
     */
    private final IssueDebitCardHandler applicationHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper for user-facing messages.
     */
    private final GuiExceptionHandler exceptionHandler;

    /**
     * @brief 构造页面工厂（Construct Page Factory）；
     *        Construct issue-debit-card page factory.
     *
     * @param applicationHandler 应用层处理器（Application handler）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public IssueDebitCardPageFactory(
            final IssueDebitCardHandler applicationHandler,
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
        final IssueDebitCardModel model = new IssueDebitCardModel();
        final IssueDebitCardView view = new IssueDebitCardView();
        final IssueDebitCardController controller = new IssueDebitCardController(model, applicationHandler, exceptionHandler);
        view.bindModel(model);
        return new GuiPage(CardGuiPageIds.ISSUE_DEBIT_CARD, model, view, controller);
    }
}
