package com.moesegfault.banking.infrastructure.gui.swing;

import com.moesegfault.banking.presentation.gui.GuiResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * @brief Swing GUI 资源加载器（Swing GUI Resource Loader），从 classpath 加载文案和静态资源；
 *        Swing resource loader reading texts and static assets from classpath.
 */
public final class SwingGuiResourceLoader implements GuiResourceLoader {

    /**
     * @brief 默认文案资源路径（Default Message Resource Path）；
     *        Default classpath location for GUI messages.
     */
    public static final String DEFAULT_MESSAGES_PATH = "gui/messages.properties";

    /**
     * @brief ClassLoader（类加载器）；
     *        ClassLoader used for loading classpath resources.
     */
    private final ClassLoader classLoader;

    /**
     * @brief 文案集合（Message Properties）；
     *        Loaded GUI message properties.
     */
    private final Properties messages;

    /**
     * @brief 使用默认路径构造加载器；
     *        Construct loader using default messages path.
     */
    public SwingGuiResourceLoader() {
        this(SwingGuiResourceLoader.class.getClassLoader(), DEFAULT_MESSAGES_PATH);
    }

    /**
     * @brief 指定 classloader 与文案路径构造加载器；
     *        Construct loader with custom classloader and message file path.
     *
     * @param classLoader 类加载器（ClassLoader）。
     * @param messagesPath 文案资源路径（Message resource path）。
     */
    public SwingGuiResourceLoader(final ClassLoader classLoader, final String messagesPath) {
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader must not be null");
        this.messages = new Properties();
        loadMessages(Objects.requireNonNull(messagesPath, "messagesPath must not be null"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> text(final String key) {
        final String nonNullKey = Objects.requireNonNull(key, "key must not be null");
        final String value = messages.getProperty(nonNullKey);
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String textOrDefault(final String key, final String defaultValue) {
        final String nonNullDefault = Objects.requireNonNull(defaultValue, "defaultValue must not be null");
        return text(key).orElse(nonNullDefault);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<URL> resource(final String path) {
        final String normalizedPath = normalizeResourcePath(path);
        return Optional.ofNullable(classLoader.getResource(normalizedPath));
    }

    /**
     * @brief 加载文案配置（Load Message Properties）；
     *        Load message properties from classpath when file exists.
     *
     * @param messagesPath 文案资源路径（Message resource path）。
     */
    private void loadMessages(final String messagesPath) {
        final String normalizedPath = normalizeResourcePath(messagesPath);
        try (InputStream inputStream = classLoader.getResourceAsStream(normalizedPath)) {
            if (inputStream == null) {
                return;
            }
            messages.load(inputStream);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load GUI messages from: " + normalizedPath, ex);
        }
    }

    /**
     * @brief 规范化资源路径（Normalize Resource Path）；
     *        Normalize classpath resource path by trimming leading slash.
     *
     * @param path 输入路径（Input path）。
     * @return 规范化路径（Normalized path）。
     */
    private static String normalizeResourcePath(final String path) {
        final String trimmed = Objects.requireNonNull(path, "path must not be null").trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("path must not be blank");
        }
        return trimmed.startsWith("/") ? trimmed.substring(1) : trimmed;
    }
}
