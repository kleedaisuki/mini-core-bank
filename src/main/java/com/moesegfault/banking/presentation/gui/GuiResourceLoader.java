package com.moesegfault.banking.presentation.gui;

import java.net.URL;
import java.util.Optional;

/**
 * @brief GUI 资源加载抽象（GUI Resource Loader Abstraction），读取文案与静态资源；
 *        GUI resource loader abstraction for i18n texts and static resources.
 */
public interface GuiResourceLoader {

    /**
     * @brief 按 key 读取文案（Load Text by Key）；
     *        Load one text value by key.
     *
     * @param key 文案键（Text key）。
     * @return 文案值（Text value），缺失则 empty。
     */
    Optional<String> text(String key);

    /**
     * @brief 读取文案并提供默认值（Load Text with Default）；
     *        Load text by key or return provided default value.
     *
     * @param key 文案键（Text key）。
     * @param defaultValue 默认文案（Default text）。
     * @return 文案值（Resolved text）。
     */
    String textOrDefault(String key, String defaultValue);

    /**
     * @brief 按路径读取资源 URL（Load Resource URL）；
     *        Load resource URL from classpath.
     *
     * @param path 资源路径（Resource path）。
     * @return 资源 URL（Resource URL），缺失则 empty。
     */
    Optional<URL> resource(String path);
}
