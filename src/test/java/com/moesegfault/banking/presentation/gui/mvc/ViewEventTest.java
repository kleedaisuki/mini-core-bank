package com.moesegfault.banking.presentation.gui.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * @brief 视图事件测试（View Event Test），验证工厂方法和不可变约束；
 *        Tests for view event factory methods and immutability guarantees.
 */
class ViewEventTest {

    /**
     * @brief 验证 `of(type)` 创建空参数事件；
     *        Verify `of(type)` creates an event with empty attributes.
     */
    @Test
    void shouldCreateEventWithoutAttributes() {
        final ViewEvent event = ViewEvent.of("refresh");
        assertEquals("refresh", event.type());
        assertEquals(Map.of(), event.attributes());
    }

    /**
     * @brief 验证 `of(type,key,value)` 创建单参数事件；
     *        Verify `of(type,key,value)` creates an event with one attribute.
     */
    @Test
    void shouldCreateEventWithSingleAttribute() {
        final ViewEvent event = ViewEvent.of("submit", "customerId", "C001");
        assertEquals("submit", event.type());
        assertEquals(Map.of("customerId", "C001"), event.attributes());
    }

    /**
     * @brief 验证属性映射会被防御性复制；
     *        Verify attributes map is defensively copied.
     */
    @Test
    void shouldDefensivelyCopyAttributes() {
        final Map<String, Object> payload = new HashMap<>();
        payload.put("amount", "88.00");

        final ViewEvent event = new ViewEvent("repay", payload);
        payload.put("amount", "99.00");

        assertEquals("88.00", event.attributes().get("amount"));
    }

    /**
     * @brief 验证空事件类型会被拒绝；
     *        Verify null event type is rejected.
     */
    @Test
    void shouldRejectNullType() {
        assertThrows(NullPointerException.class, () -> new ViewEvent(null, Map.of()));
    }
}
