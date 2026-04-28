package com.moesegfault.banking.presentation.gui.card;

import com.moesegfault.banking.application.card.query.FindCardHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import java.util.Objects;

/**
 * @brief 卡详情页面工厂（Show Card Page Factory），创建 `card.show` 的 MVC 三件套；
 *        Page factory creating MVC bundle for `card.show`.
 */
public final class ShowCardPageFactory implements GuiPageFactory {

    /**
     * @brief 应用层处理器（Application Handler）；
     *        Application handler for card query.
     */
    private final FindCardHandler applicationHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper for user-facing messages.
     */
    private final GuiExceptionHandler exceptionHandler;

    /**
     * @brief 构造页面工厂（Construct Page Factory）；
     *        Construct show-card page factory.
     *
     * @param applicationHandler 应用层处理器（Application handler）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public ShowCardPageFactory(
            final FindCardHandler applicationHandler,
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
        final ShowCardModel model = new ShowCardModel();
        final ShowCardView view = new ShowCardView();
        final ShowCardController controller = new ShowCardController(model, applicationHandler, exceptionHandler);
        view.bindModel(model);
        return new GuiPage(CardGuiPageIds.SHOW_CARD, model, view, controller);
    }
}
