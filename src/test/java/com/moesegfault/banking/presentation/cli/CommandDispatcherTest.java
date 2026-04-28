package com.moesegfault.banking.presentation.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * @brief 命令分发器单元测试（Command Dispatcher Unit Test），验证路由与调用行为；
 *        Unit tests for command dispatcher routing and invocation behavior.
 */
class CommandDispatcherTest {

    /**
     * @brief 验证命中映射后会调用对应 handler；
     *        Verify matched command mapping invokes corresponding handler.
     */
    @Test
    void shouldDispatchToMappedHandler() {
        final CommandRegistry registry = new CommandRegistry();
        registry.register("customer register", CountingHandler.class);

        final CountingHandler handler = new CountingHandler();
        final CommandDispatcher dispatcher = new CommandDispatcher(
                registry,
                handlerType -> Map.of(CountingHandler.class, handler).get(handlerType));

        dispatcher.dispatch(new ParsedCommand("customer register --phone 1", java.util.List.of("customer", "register"), Map.of("phone", "1")));

        assertEquals(1, handler.invocationCount());
    }

    /**
     * @brief 验证未知命令会抛出异常；
     *        Verify unknown command throws exception.
     */
    @Test
    void shouldRejectUnknownCommandPath() {
        final CommandRegistry registry = new CommandRegistry();
        final CommandDispatcher dispatcher = new CommandDispatcher(registry, handlerType -> null);

        assertThrows(
                IllegalArgumentException.class,
                () -> dispatcher.dispatch(new ParsedCommand("unknown cmd", java.util.List.of("unknown", "cmd"), Map.of())));
    }

    /**
     * @brief 验证 resolver 返回 null 会抛出异常；
     *        Verify resolver returning null throws exception.
     */
    @Test
    void shouldRejectNullResolvedHandler() {
        final CommandRegistry registry = new CommandRegistry();
        registry.register("customer register", CountingHandler.class);

        final CommandDispatcher dispatcher = new CommandDispatcher(registry, handlerType -> null);

        assertThrows(
                NullPointerException.class,
                () -> dispatcher.dispatch(new ParsedCommand(
                        "customer register",
                        java.util.List.of("customer", "register"),
                        Map.of())));
    }

    /**
     * @brief 计数型 handler（Counting Handler）；
     *        Counting handler used to verify invocation count.
     */
    private static final class CountingHandler implements CliCommandHandler {

        /**
         * @brief 调用计数器（Invocation Counter）；
         *        Invocation counter.
         */
        private final AtomicInteger invocationCounter = new AtomicInteger();

        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(final ParsedCommand command) {
            invocationCounter.incrementAndGet();
        }

        /**
         * @brief 获取调用次数（Get Invocation Count）；
         *        Get invocation count.
         *
         * @return 调用次数（Invocation count）。
         */
        public int invocationCount() {
            return invocationCounter.get();
        }
    }
}
