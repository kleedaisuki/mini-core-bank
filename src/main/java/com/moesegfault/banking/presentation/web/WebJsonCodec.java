package com.moesegfault.banking.presentation.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;

/**
 * @brief Web JSON 编解码器（Web JSON Codec），负责 JSON 请求体解析与响应序列化；
 *        Web JSON codec responsible for request-body deserialization and response serialization.
 */
public final class WebJsonCodec {

    /**
     * @brief Jackson 映射器（Jackson ObjectMapper）；
     *        Jackson object mapper.
     */
    private final ObjectMapper objectMapper;

    /**
     * @brief 构造默认 JSON 编解码器（Construct Default JSON Codec）；
     *        Construct JSON codec with default ObjectMapper.
     */
    public WebJsonCodec() {
        this(new ObjectMapper().findAndRegisterModules());
    }

    /**
     * @brief 构造 JSON 编解码器（Construct JSON Codec）；
     *        Construct JSON codec with injected ObjectMapper.
     *
     * @param objectMapper Jackson 映射器（Jackson object mapper）。
     */
    public WebJsonCodec(final ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    /**
     * @brief 反序列化 JSON 文本（Deserialize JSON Text）；
     *        Deserialize JSON text into target DTO type.
     *
     * @param jsonText JSON 文本（JSON text）。
     * @param targetType 目标类型（Target type）。
     * @param <T> 类型参数（Type parameter）。
     * @return 反序列化结果（Deserialized value）。
     */
    public <T> T deserialize(final String jsonText, final Class<T> targetType) {
        final String normalizedJson = Objects.requireNonNull(jsonText, "jsonText must not be null").trim();
        if (normalizedJson.isEmpty()) {
            throw new IllegalArgumentException("Request JSON body must not be blank");
        }

        final Class<T> normalizedTargetType = Objects.requireNonNull(targetType, "targetType must not be null");
        try {
            return objectMapper.readValue(normalizedJson, normalizedTargetType);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid JSON request body", exception);
        }
    }

    /**
     * @brief 序列化对象为 JSON（Serialize Object as JSON）；
     *        Serialize one object into JSON text.
     *
     * @param value 任意对象（Any object value）。
     * @return JSON 文本（JSON text）。
     */
    public String serialize(final Object value) {
        final Object normalizedValue = Objects.requireNonNull(value, "value must not be null");
        try {
            return objectMapper.writeValueAsString(normalizedValue);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize JSON response payload", exception);
        }
    }

    /**
     * @brief 创建 JSON 响应（Create JSON Response）；
     *        Create one JSON web response from payload object.
     *
     * @param statusCode HTTP 状态码（HTTP status code）。
     * @param payload 响应载荷对象（Response payload object）。
     * @return Web 响应（Web response）。
     */
    public WebResponse toJsonResponse(final int statusCode, final Object payload) {
        return WebResponse.json(statusCode, serialize(payload));
    }
}

