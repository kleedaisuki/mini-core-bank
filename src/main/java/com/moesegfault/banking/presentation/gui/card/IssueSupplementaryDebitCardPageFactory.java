package com.moesegfault.banking.presentation.gui.card;

import com.moesegfault.banking.application.card.command.IssueSupplementaryDebitCardHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import java.util.Objects;

/**
 * @brief 借记附属卡页面工厂（Issue Supplementary Debit Card Page Factory），创建 `card.issue-supplementary-debit` 的 MVC 三件套；
 *        Page factory creating MVC bundle for `card.issue-supplementary-debit`.
 */
public final class IssueSupplementaryDebitCardPageFactory implements GuiPageFactory {

    /**
     * @brief 应用层处理器（Application Handler）；
     *        Application handler for supplementary debit-card issuance.
     */
    private final IssueSupplementaryDebitCardHandler applicationHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper for user-facing messages.
     */
    private final GuiExceptionHandler exceptionHandler;

    /**
     * @brief 构造页面工厂（Construct Page Factory）；
     *        Construct issue-supplementary-debit-card page factory.
     *
     * @param applicationHandler 应用层处理器（Application handler）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public IssueSupplementaryDebitCardPageFactory(
            final IssueSupplementaryDebitCardHandler applicationHandler,
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
        final IssueSupplementaryDebitCardModel model = new IssueSupplementaryDebitCardModel();
        final IssueSupplementaryDebitCardView view = new IssueSupplementaryDebitCardView();
        final IssueSupplementaryDebitCardController controller =
                new IssueSupplementaryDebitCardController(model, applicationHandler, exceptionHandler);
        view.bindModel(model);
        return new GuiPage(CardGuiPageIds.ISSUE_SUPPLEMENTARY_DEBIT_CARD, model, view, controller);
    }
}
