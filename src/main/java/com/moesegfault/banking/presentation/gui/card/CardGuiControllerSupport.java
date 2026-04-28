package com.moesegfault.banking.presentation.gui.card;

import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 卡页面控制器辅助工具（Card-page Controller Support Utility），封装事件取值与消息构造；
 *        Utility for card-page controllers to read event attributes and construct user messages.
 */
final class CardGuiControllerSupport {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private CardGuiControllerSupport() {
    }

    /**
     * @brief 读取可选字符串属性（Read Optional String Attribute）；
     *        Read optional string attribute from one view event.
     *
     * @param event 视图事件（View event）。
     * @param key 属性键（Attribute key）。
     * @return 属性值（Attribute value），不存在时返回空字符串。
     */
    static String optionalStringAttribute(final ViewEvent event, final String key) {
        final ViewEvent normalizedEvent = Objects.requireNonNull(event, "event must not be null");
        final String normalizedKey = Objects.requireNonNull(key, "key must not be null");
        final Map<String, Object> attributes = normalizedEvent.attributes();
        final Object rawValue = attributes.get(normalizedKey);
        if (rawValue == null) {
            return "";
        }
        return rawValue.toString().trim();
    }

    /**
     * @brief 构造默认成功消息（Build Default Success Message）；
     *        Build default success message from card issuance result.
     *
     * @param cardId 卡片 ID（Card ID）。
     * @param cardKind 卡片类型（Card kind）。
     * @return 成功消息（Success message）。
     */
    static String buildIssueSuccessMessage(final String cardId, final String cardKind) {
        return "发卡成功: card_id=" + Objects.requireNonNull(cardId, "cardId must not be null")
                + ", card_kind=" + Objects.requireNonNull(cardKind, "cardKind must not be null");
    }
}
