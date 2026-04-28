package com.moesegfault.banking.presentation.gui.mvc;

import java.util.Map;
import java.util.Objects;

/**
 * @brief 视图事件对象（View Event），表达 UI 动作类型和参数载荷；
 *        View event carrying UI action type and payload attributes.
 *
 * @param type 事件类型（Event type）。
 * @param attributes 事件参数（Event attributes）。
 */
public record ViewEvent(String type, Map<String, Object> attributes) {

    /**
     * @brief 构造并校验视图事件（Construct Validated View Event）；
     *        Construct validated view event and normalize immutable payload.
     *
     * @param type 事件类型（Event type）。
     * @param attributes 事件参数（Event attributes）。
     */
    public ViewEvent {
        type = Objects.requireNonNull(type, "type must not be null");
        attributes = Map.copyOf(Objects.requireNonNull(attributes, "attributes must not be null"));
    }

    /**
     * @brief 创建无参数事件（Create Event Without Attributes）；
     *        Create event with empty attribute payload.
     *
     * @param type 事件类型（Event type）。
     * @return 无参数事件对象（View event without attributes）。
     */
    public static ViewEvent of(final String type) {
        return new ViewEvent(type, Map.of());
    }

    /**
     * @brief 创建单参数事件（Create Event With One Attribute）；
     *        Create event with one key-value attribute.
     *
     * @param type 事件类型（Event type）。
     * @param key 参数键（Attribute key）。
     * @param value 参数值（Attribute value）。
     * @return 单参数事件对象（View event with one attribute）。
     */
    public static ViewEvent of(final String type, final String key, final Object value) {
        return new ViewEvent(type, Map.of(key, value));
    }
}
