package com.moesegfault.banking.infrastructure.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * @brief 配置加载器（Configuration Loader），合并 `application.properties` 与 classpath 下 `configs/*.json`；
 * Configuration loader that merges `application.properties` with `configs/*.json` from classpath.
 */
public final class PropertiesLoader {

    /**
     * @brief 默认 properties 资源名；
     * Default properties resource name.
     */
    public static final String DEFAULT_PROPERTIES_RESOURCE = "application.properties";

    /**
     * @brief 默认 JSON 配置目录；
     * Default JSON configuration directory.
     */
    public static final String DEFAULT_JSON_CONFIG_DIRECTORY = "configs";

    /**
     * @brief JSON 解析器（ObjectMapper）；
     * JSON parser instance.
     */
    private final ObjectMapper objectMapper;

    /**
     * @brief 资源模式解析器（Resource Pattern Resolver）；
     * Resolver used for classpath resource pattern matching.
     */
    private final ResourcePatternResolver resourceResolver;

    /**
     * @brief 构造默认配置加载器；
     * Construct a loader with default ObjectMapper and resolver.
     */
    public PropertiesLoader() {
        this(new ObjectMapper(), new PathMatchingResourcePatternResolver());
    }

    /**
     * @brief 构造可注入依赖的配置加载器；
     * Construct a loader with injected dependencies.
     *
     * @param objectMapper JSON 解析器；JSON parser.
     * @param resourceResolver 资源解析器；Resource resolver.
     */
    public PropertiesLoader(final ObjectMapper objectMapper,
                            final ResourcePatternResolver resourceResolver) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.resourceResolver = Objects.requireNonNull(resourceResolver, "resourceResolver must not be null");
    }

    /**
     * @brief 加载默认配置；
     * Load configuration using default resource names.
     *
     * @return 合并后的 Properties；Merged properties.
     */
    public Properties load() {
        return load(DEFAULT_PROPERTIES_RESOURCE, DEFAULT_JSON_CONFIG_DIRECTORY);
    }

    /**
     * @brief 按指定资源位置加载并合并配置；
     * Load and merge configuration from explicit resource locations.
     *
     * @param propertiesResource properties 资源路径；Properties resource path.
     * @param jsonDirectory JSON 目录路径；JSON directory path.
     * @return 合并后的 Properties；Merged properties.
     */
    public Properties load(final String propertiesResource, final String jsonDirectory) {
        final Properties merged = new Properties();
        loadPropertiesResource(merged, propertiesResource);
        loadJsonResources(merged, jsonDirectory);
        return merged;
    }

    /**
     * @brief 从 properties 资源加载键值；
     * Load key-value pairs from a .properties resource.
     *
     * @param target 目标 Properties；Target properties.
     * @param propertiesResource properties 资源路径；Properties resource path.
     */
    private void loadPropertiesResource(final Properties target, final String propertiesResource) {
        final String normalizedResource = requireText(propertiesResource, "propertiesResource");
        final ClassPathResource resource = new ClassPathResource(normalizedResource);
        if (!resource.exists()) {
            throw new IllegalStateException("Properties resource not found: " + normalizedResource);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            target.load(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load properties resource: " + normalizedResource, exception);
        }
    }

    /**
     * @brief 从 JSON 目录加载并覆盖配置；
     * Load JSON files from a directory and override existing properties.
     *
     * @param target 目标 Properties；Target properties.
     * @param jsonDirectory JSON 目录路径；JSON directory path.
     */
    private void loadJsonResources(final Properties target, final String jsonDirectory) {
        if (jsonDirectory == null || jsonDirectory.trim().isEmpty()) {
            return;
        }

        final String pattern = buildJsonPattern(jsonDirectory);
        try {
            final Resource[] resources = resourceResolver.getResources(pattern);
            Arrays.sort(resources, (left, right) -> safeFilename(left).compareTo(safeFilename(right)));

            for (Resource resource : resources) {
                if (!resource.exists()) {
                    continue;
                }
                loadSingleJsonResource(target, resource);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load JSON configs from: " + jsonDirectory, exception);
        }
    }

    /**
     * @brief 加载单个 JSON 资源到 Properties；
     * Load one JSON resource into target properties.
     *
     * @param target 目标 Properties；Target properties.
     * @param resource JSON 资源；JSON resource.
     */
    private void loadSingleJsonResource(final Properties target, final Resource resource) {
        try (InputStream inputStream = resource.getInputStream()) {
            final JsonNode root = objectMapper.readTree(inputStream);
            if (root == null || root.isNull()) {
                return;
            }
            flattenJsonNode("", root, target);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to parse JSON config: " + resourceDescription(resource), exception);
        }
    }

    /**
     * @brief 将 JSON 结构拍平为点路径键；
     * Flatten JSON structure into dotted property keys.
     *
     * @param prefix 当前键前缀；Current key prefix.
     * @param node 当前 JSON 节点；Current JSON node.
     * @param target 目标 Properties；Target properties.
     */
    private void flattenJsonNode(final String prefix, final JsonNode node, final Properties target) {
        if (node.isObject()) {
            final Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                final Map.Entry<String, JsonNode> field = fields.next();
                final String nextPrefix = prefix.isEmpty() ? field.getKey() : prefix + "." + field.getKey();
                flattenJsonNode(nextPrefix, field.getValue(), target);
            }
            return;
        }

        if (node.isArray()) {
            for (int index = 0; index < node.size(); index++) {
                final String nextPrefix = prefix + "[" + index + "]";
                flattenJsonNode(nextPrefix, node.get(index), target);
            }
            return;
        }

        if (!prefix.isEmpty() && !node.isNull()) {
            target.setProperty(prefix, node.asText());
        }
    }

    /**
     * @brief 构建 JSON 扫描 pattern；
     * Build classpath pattern for JSON file discovery.
     *
     * @param jsonDirectory JSON 目录；JSON directory.
     * @return `classpath*:` pattern；Classpath scanning pattern.
     */
    private String buildJsonPattern(final String jsonDirectory) {
        final String sanitizedDirectory = jsonDirectory.replace('\\', '/').replaceAll("^/+", "").replaceAll("/+$", "");
        return "classpath*:" + sanitizedDirectory + "/*.json";
    }

    /**
     * @brief 获取资源文件名（空安全）；
     * Get resource filename with null-safety.
     *
     * @param resource 资源对象；Resource object.
     * @return 文件名；Filename.
     */
    private String safeFilename(final Resource resource) {
        final String filename = resource.getFilename();
        return filename == null ? "" : filename;
    }

    /**
     * @brief 返回可读资源描述；
     * Return readable resource description.
     *
     * @param resource 资源对象；Resource object.
     * @return 描述文本；Description text.
     */
    private String resourceDescription(final Resource resource) {
        return resource.getDescription();
    }

    /**
     * @brief 校验字符串参数非空白；
     * Ensure a string argument is not blank.
     *
     * @param value 输入值；Input value.
     * @param argumentName 参数名；Argument name.
     * @return 去除首尾空白后的字符串；Trimmed non-blank string.
     */
    private String requireText(final String value, final String argumentName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(argumentName + " must not be blank");
        }
        return value.trim();
    }
}
