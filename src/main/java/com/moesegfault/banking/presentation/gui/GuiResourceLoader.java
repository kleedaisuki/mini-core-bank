package com.moesegfault.banking.presentation.gui;

import java.net.URL;
import java.util.Optional;

/**
 * @brief GUI 资源加载接口（GUI Resource Loader Interface），用于读取主题与文案资源；
 *        GUI resource loader contract for theme and message resource retrieval.
 */
public interface GuiResourceLoader {

    /**
     * @brief 读取文案资源（Load Optional Text Resource）；
     *        Load optional text resource by key.
     *
     * @param key 资源键（Resource key）。
     * @return 可选资源值（Optional resource value）。
     */
    Optional<String> text(String key);

    /**
     * @brief 读取文案并提供默认值（Load Text Or Fallback）；
     *        Load text resource with default fallback.
     *
     * @param key 资源键（Resource key）。
     * @param defaultValue 默认值（Default value）。
     * @return 文案值（Resolved text value）。
     */
    default String textOrDefault(final String key, final String defaultValue) {
        return text(key).orElse(defaultValue);
    }

    /**
     * @brief 读取二进制资源路径（Load Optional Binary Resource）；
     *        Load optional binary resource URL by classpath path.
     *
     * @param path 资源路径（Resource path）。
     * @return 可选 URL（Optional resource URL）。
     */
    Optional<URL> resource(String path);

    /**
     * @brief 读取必须文案（Load Required Message）；
     *        Load required message text by key.
     *
     * @param key 资源键（Resource key）。
     * @return 文案值（Message text）。
     */
    default String message(final String key) {
        return textOrDefault(key, key);
    }
}
