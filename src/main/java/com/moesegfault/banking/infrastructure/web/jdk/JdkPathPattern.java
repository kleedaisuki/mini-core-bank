package com.moesegfault.banking.infrastructure.web.jdk;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @brief JDK 路径模式匹配器（JDK Path Pattern Matcher），支持字面量与 `{param}` 语法；
 *        JDK runtime path-pattern matcher supporting literal segments and `{param}` syntax.
 */
public final class JdkPathPattern {

    /**
     * @brief 参数名校验规则（Parameter Name Validation Rule）；
     *        Validation rule for path-parameter names.
     */
    private static final Pattern PARAMETER_NAME_PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");

    /**
     * @brief 规范化路径模式（Normalized Path Pattern）；
     *        Normalized path pattern.
     */
    private final String normalizedPattern;

    /**
     * @brief 模式段定义（Pattern Segment Definitions）；
     *        Parsed pattern segments.
     */
    private final List<Segment> segments;

    /**
     * @brief 构造路径模式匹配器（Construct Path Pattern Matcher）；
     *        Construct matcher from one path pattern.
     *
     * @param pathPattern 路径模式（Path pattern）。
     */
    public JdkPathPattern(final String pathPattern) {
        this.normalizedPattern = normalizePath(Objects.requireNonNull(pathPattern, "pathPattern must not be null"));
        this.segments = parseSegments(normalizedPattern);
    }

    /**
     * @brief 匹配请求路径（Match Request Path）；
     *        Match request path and extract path parameters.
     *
     * @param requestPath 请求路径（Request path）。
     * @return 匹配结果（Match result），包含参数映射；不匹配则 empty。
     */
    public Optional<Map<String, String>> match(final String requestPath) {
        final String normalizedPath = normalizePath(Objects.requireNonNull(requestPath, "requestPath must not be null"));
        final List<String> pathSegments = splitSegments(normalizedPath);

        if (pathSegments.size() != segments.size()) {
            return Optional.empty();
        }

        final Map<String, String> pathParams = new LinkedHashMap<>();
        for (int index = 0; index < segments.size(); index++) {
            final Segment segment = segments.get(index);
            final String requestSegment = pathSegments.get(index);

            if (segment.parameterName != null) {
                pathParams.put(segment.parameterName, requestSegment);
                continue;
            }

            if (!segment.literal.equals(requestSegment)) {
                return Optional.empty();
            }
        }

        return Optional.of(pathParams);
    }

    /**
     * @brief 返回规范化模式（Get Normalized Pattern）；
     *        Get normalized path pattern.
     *
     * @return 规范化路径模式（Normalized pattern）。
     */
    public String normalizedPattern() {
        return normalizedPattern;
    }

    /**
     * @brief 规范化路径（Normalize Path）；
     *        Normalize path by validating and trimming trailing slash.
     *
     * @param rawPath 原始路径（Raw path）。
     * @return 规范化路径（Normalized path）。
     */
    private static String normalizePath(final String rawPath) {
        final String trimmed = rawPath.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("path must not be empty");
        }
        if (!trimmed.startsWith("/")) {
            throw new IllegalArgumentException("path must start with '/'");
        }

        if (trimmed.length() > 1 && trimmed.endsWith("/")) {
            return trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    /**
     * @brief 解析路径段（Parse Path Segments）；
     *        Parse normalized path into typed segments.
     *
     * @param normalizedPath 规范化路径（Normalized path）。
     * @return 路径段列表（Path segments）。
     */
    private static List<Segment> parseSegments(final String normalizedPath) {
        final List<String> rawSegments = splitSegments(normalizedPath);
        final List<Segment> parsedSegments = new ArrayList<>(rawSegments.size());

        for (String rawSegment : rawSegments) {
            if (rawSegment.isEmpty()) {
                throw new IllegalArgumentException("path must not contain empty segment");
            }
            parsedSegments.add(parseSegment(rawSegment));
        }
        return List.copyOf(parsedSegments);
    }

    /**
     * @brief 解析单个路径段（Parse One Segment）；
     *        Parse one raw segment into literal or parameter.
     *
     * @param rawSegment 原始段（Raw segment）。
     * @return 段对象（Segment object）。
     */
    private static Segment parseSegment(final String rawSegment) {
        if (rawSegment.startsWith("{") || rawSegment.endsWith("}")) {
            if (!rawSegment.startsWith("{") || !rawSegment.endsWith("}")) {
                throw new IllegalArgumentException("invalid parameter segment: " + rawSegment);
            }
            if (rawSegment.length() <= 2) {
                throw new IllegalArgumentException("path parameter name must not be empty");
            }

            final String parameterName = rawSegment.substring(1, rawSegment.length() - 1);
            if (!PARAMETER_NAME_PATTERN.matcher(parameterName).matches()) {
                throw new IllegalArgumentException("invalid path parameter name: " + parameterName);
            }
            return Segment.parameter(parameterName);
        }

        if (rawSegment.contains("{") || rawSegment.contains("}")) {
            throw new IllegalArgumentException("invalid literal segment: " + rawSegment);
        }
        return Segment.literal(rawSegment);
    }

    /**
     * @brief 拆分规范化路径（Split Normalized Path）；
     *        Split normalized path into raw segments.
     *
     * @param normalizedPath 规范化路径（Normalized path）。
     * @return 原始路径段（Raw path segments）。
     */
    private static List<String> splitSegments(final String normalizedPath) {
        if ("/".equals(normalizedPath)) {
            return List.of();
        }
        return List.of(normalizedPath.substring(1).split("/", -1));
    }

    /**
     * @brief 路径段定义（Path Segment Definition）；
     *        One parsed segment definition.
     */
    private static final class Segment {

        /**
         * @brief 字面量值（Literal Value）；
         *        Literal segment value.
         */
        private final String literal;

        /**
         * @brief 原始参数名（Original Parameter Name）；
         *        Original parameter name for extracted map key.
         */
        private final String parameterName;

        /**
         * @brief 构造路径段（Construct Segment）；
         *        Construct one segment.
         *
         * @param literal 字面量（Literal）。
         * @param parameterName 参数名（Parameter name）。
         */
        private Segment(final String literal, final String parameterName) {
            this.literal = literal;
            this.parameterName = parameterName;
        }

        /**
         * @brief 创建字面量段（Create Literal Segment）；
         *        Create literal segment.
         *
         * @param value 字面量值（Literal value）。
         * @return 路径段（Segment）。
         */
        private static Segment literal(final String value) {
            return new Segment(value, null);
        }

        /**
         * @brief 创建参数段（Create Parameter Segment）；
         *        Create parameter segment.
         *
         * @param name 参数名（Parameter name）。
         * @return 路径段（Segment）。
         */
        private static Segment parameter(final String name) {
            return new Segment(null, name);
        }
    }
}
