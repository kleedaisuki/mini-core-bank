package com.moesegfault.banking.presentation.web.card;

import com.moesegfault.banking.presentation.web.RouteRegistrar;
import com.moesegfault.banking.presentation.web.WebRuntime;
import java.util.Objects;

/**
 * @brief 卡路由注册器（Card Route Registrar），集中注册 card 子域 REST 路由；
 *        Card route registrar that centralizes card-subdomain REST route registration.
 */
public final class CardRoutes implements RouteRegistrar {

    /**
     * @brief 卡控制器（Card Controller）；
     *        Card REST controller.
     */
    private final CardController cardController;

    /**
     * @brief 构造卡路由注册器（Construct Card Route Registrar）；
     *        Construct card route registrar.
     *
     * @param cardController 卡控制器（Card controller）。
     */
    public CardRoutes(final CardController cardController) {
        this.cardController = Objects.requireNonNull(cardController, "cardController must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRoutes(final WebRuntime runtime) {
        final WebRuntime normalizedRuntime = Objects.requireNonNull(runtime, "runtime must not be null");
        normalizedRuntime.addRoute("POST", CardWebSchema.PATH_ISSUE_DEBIT, cardController::issueDebitCard);
        normalizedRuntime.addRoute(
                "POST",
                CardWebSchema.PATH_ISSUE_SUPPLEMENTARY_DEBIT,
                cardController::issueSupplementaryDebitCard);
        normalizedRuntime.addRoute("POST", CardWebSchema.PATH_ISSUE_CREDIT, cardController::issueCreditCard);
        normalizedRuntime.addRoute(
                "POST",
                CardWebSchema.PATH_ISSUE_SUPPLEMENTARY_CREDIT,
                cardController::issueSupplementaryCreditCard);
        normalizedRuntime.addRoute("GET", CardWebSchema.PATH_CARD_DETAIL, cardController::showCard);
    }
}

