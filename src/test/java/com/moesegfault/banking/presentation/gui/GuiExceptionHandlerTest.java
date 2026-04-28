package com.moesegfault.banking.presentation.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.moesegfault.banking.domain.shared.DomainException;
import org.junit.jupiter.api.Test;

/**
 * @brief GUI 异常处理器测试（GUI Exception Handler Test），验证异常消息映射；
 *        Tests for GUI exception handler message mapping behavior.
 */
class GuiExceptionHandlerTest {

    /**
     * @brief 验证领域异常会保留业务消息；
     *        Verify domain exception keeps business message.
     */
    @Test
    void shouldKeepDomainExceptionMessage() {
        final GuiExceptionHandler handler = new GuiExceptionHandler();
        final String message = handler.toUserMessage(new DomainException("账户已冻结"));
        assertEquals("账户已冻结", message);
    }

    /**
     * @brief 验证参数异常会附加输入提示；
     *        Verify illegal argument exception maps to input-hint message.
     */
    @Test
    void shouldMapIllegalArgumentToInputHint() {
        final GuiExceptionHandler handler = new GuiExceptionHandler();
        final String message = handler.toUserMessage(new IllegalArgumentException("缺少 customerId"));
        assertEquals("输入参数不合法: 缺少 customerId", message);
    }

    /**
     * @brief 验证未知异常返回通用提示；
     *        Verify unknown exception maps to generic fallback message.
     */
    @Test
    void shouldMapUnknownExceptionToFallbackMessage() {
        final GuiExceptionHandler handler = new GuiExceptionHandler();
        final String message = handler.toUserMessage(new RuntimeException("network error"));
        assertEquals("系统暂时不可用，请稍后重试", message);
    }
}
