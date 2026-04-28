package com.moesegfault.banking.presentation.gui.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.moesegfault.banking.application.ledger.query.FindBalanceHandler;
import com.moesegfault.banking.domain.ledger.Balance;
import com.moesegfault.banking.domain.ledger.LedgerRepository;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @brief 余额查询控制器测试（Show Balance Controller Test），验证输入 schema 与应用层调用链路；
 *        Tests for balance-query controller covering schema input handling and app-layer invocation.
 */
class ShowBalanceControllerTest {

    /**
     * @brief 验证查询命中时会写入余额结果；
     *        Verify successful query writes balance result into model.
     */
    @Test
    void shouldLoadBalanceResultWhenFound() {
        final LedgerRepository ledgerRepository = Mockito.mock(LedgerRepository.class);
        when(ledgerRepository.findBalance(eq("acc-001"), eq(CurrencyCode.of("USD"))))
                .thenReturn(Optional.of(Balance.restore(
                        "acc-001",
                        CurrencyCode.of("USD"),
                        usd("100.00"),
                        usd("88.00"),
                        Instant.parse("2026-04-28T10:10:10Z"))));

        final ShowBalanceModel model = new ShowBalanceModel();
        final ShowBalanceController controller = new ShowBalanceController(
                model,
                new FindBalanceHandler(ledgerRepository),
                new GuiExceptionHandler());

        controller.onViewEvent(new ViewEvent(
                ShowBalanceController.EVENT_SEARCH_BALANCE,
                Map.of(
                        ShowBalanceController.FIELD_ACCOUNT_ID, "acc-001",
                        ShowBalanceController.FIELD_CURRENCY_CODE, "usd")));

        assertTrue(model.balanceResult().isPresent());
        assertEquals("acc-001", model.balanceResult().orElseThrow().accountId());
        assertEquals("", model.errorMessage());
        assertFalse(model.loading());
    }

    /**
     * @brief 验证查询未命中时会显示 schema 风格提示；
     *        Verify not-found query shows schema-style hint message.
     */
    @Test
    void shouldShowNotFoundHintWhenBalanceMissing() {
        final LedgerRepository ledgerRepository = Mockito.mock(LedgerRepository.class);
        when(ledgerRepository.findBalance(eq("acc-404"), eq(CurrencyCode.of("USD"))))
                .thenReturn(Optional.empty());

        final ShowBalanceModel model = new ShowBalanceModel();
        final ShowBalanceController controller = new ShowBalanceController(
                model,
                new FindBalanceHandler(ledgerRepository),
                new GuiExceptionHandler());

        controller.onViewEvent(new ViewEvent(
                ShowBalanceController.EVENT_SEARCH_BALANCE,
                Map.of(
                        ShowBalanceController.FIELD_ACCOUNT_ID, "acc-404",
                        ShowBalanceController.FIELD_CURRENCY_CODE, "USD")));

        assertTrue(model.balanceResult().isEmpty());
        assertEquals("ledger_balance_not_found account_id=acc-404 currency_code=USD", model.hintMessage());
        assertEquals("", model.errorMessage());
        assertFalse(model.loading());
    }

    /**
     * @brief 验证缺少必填字段会映射为可读错误；
     *        Verify missing required field is mapped to readable error message.
     */
    @Test
    void shouldMapValidationFailureToUserErrorMessage() {
        final LedgerRepository ledgerRepository = Mockito.mock(LedgerRepository.class);
        final ShowBalanceModel model = new ShowBalanceModel();
        final ShowBalanceController controller = new ShowBalanceController(
                model,
                new FindBalanceHandler(ledgerRepository),
                new GuiExceptionHandler());

        controller.onViewEvent(new ViewEvent(
                ShowBalanceController.EVENT_SEARCH_BALANCE,
                Map.of(ShowBalanceController.FIELD_CURRENCY_CODE, "USD")));

        assertTrue(model.errorMessage().startsWith("输入参数不合法:"));
        assertTrue(model.balanceResult().isEmpty());
        assertFalse(model.loading());
    }

    /**
     * @brief 构造 USD 金额（Build USD Money）；
     *        Build USD money helper.
     *
     * @param amount 金额字符串（Amount string）。
     * @return USD 金额（USD money）。
     */
    private static Money usd(final String amount) {
        return Money.of(CurrencyCode.of("USD"), new BigDecimal(amount));
    }
}
