package com.moesegfault.banking.infrastructure.persistence;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @brief 仓储门面基础接口（Repository Facade Base Interface），统一仓储能力协商与版本描述；
 *        Repository facade base interface that unifies capability negotiation
 *        and version metadata.
 *
 * @note 该接口位于基础设施层（Infrastructure Layer），不直接承载领域规则；
 *       This interface belongs to the infrastructure layer and does not carry
 *       domain rules directly.
 */
public interface Repository extends AutoCloseable {

    /**
     * @brief 返回仓储描述符（Get Repository Descriptor）；
     *        Get repository descriptor.
     *
     * @return 仓储描述符（Repository descriptor）。
     */
    RepositoryDescriptor descriptor();

    /**
     * @brief 解析读路径契约（Resolve Read Contract）；
     *        Resolve read-path contract.
     *
     * @param <T>          contract 类型（Contract type）。
     * @param contractType 契约类型（Contract class type）。
     * @return 契约实现可选值（Optional contract implementation）。
     */
    <T> Optional<T> reader(Class<T> contractType);

    /**
     * @brief 解析写路径契约（Resolve Write Contract）；
     *        Resolve write-path contract.
     *
     * @param <T>          contract 类型（Contract type）。
     * @param contractType 契约类型（Contract class type）。
     * @return 契约实现可选值（Optional contract implementation）。
     */
    <T> Optional<T> writer(Class<T> contractType);

    /**
     * @brief 在仓储管理的写事务内执行（Execute Within Repository-managed Write Transaction）；
     *        Execute action inside repository-managed write transaction.
     *
     * @param <T>    返回类型（Result type）。
     * @param action 事务动作（Transactional action）。
     * @return 执行结果（Execution result）。
     */
    <T> T writeInTransaction(Supplier<T> action);

    /**
     * @brief 在仓储管理的写事务内执行无返回动作（Execute Side-effect Action in Write Transaction）；
     *        Execute side-effect action inside repository-managed write
     *        transaction.
     *
     * @param action 事务动作（Transactional action）。
     */
    default void writeInTransaction(final Runnable action) {
        Objects.requireNonNull(action, "action must not be null");
        writeInTransaction(() -> {
            action.run();
            return null;
        });
    }

    /**
     * @brief 按能力域解析契约（Resolve Contract by Capability Scope）；
     *        Resolve contract by capability scope.
     *
     * @param <T>          contract 类型（Contract type）。
     * @param scope        能力域（Capability scope）。
     * @param contractType 契约类型（Contract class type）。
     * @return 契约实现可选值（Optional contract implementation）。
     */
    default <T> Optional<T> resolve(final CapabilityScope scope, final Class<T> contractType) {
        Objects.requireNonNull(scope, "scope must not be null");
        Objects.requireNonNull(contractType, "contractType must not be null");
        return switch (scope) {
            case READ -> reader(contractType);
            case WRITE -> writer(contractType);
        };
    }

    /**
     * @brief 按默认策略解析契约（Resolve Contract by Default Policy）；
     *        Resolve contract by default policy.
     *
     * @param <T>          contract 类型（Contract type）。
     * @param contractType 契约类型（Contract class type）。
     * @return 契约实现可选值（Optional contract implementation）。
     * @note 默认顺序：WRITE -> READ；
     *       Default precedence: WRITE -> READ.
     */
    default <T> Optional<T> resolve(final Class<T> contractType) {
        Objects.requireNonNull(contractType, "contractType must not be null");
        return writer(contractType)
                .or(() -> reader(contractType));
    }

    /**
     * @brief 判断是否支持指定仓储契约（Check Whether Contract Is Supported）；
     *        Check whether the given contract type is supported.
     *
     * @param contractType 契约类型（Contract class type）。
     * @return 支持返回 true（true when supported）。
     */
    default boolean supports(final CapabilityScope scope, final Class<?> contractType) {
        Objects.requireNonNull(scope, "scope must not be null");
        Objects.requireNonNull(contractType, "contractType must not be null");
        return descriptor().supportsCapability(scope, contractType);
    }

    /**
     * @brief 判断任意能力域是否支持契约（Check Contract Support in Any Scope）；
     *        Check whether the contract is supported in any capability scope.
     *
     * @param contractType 契约类型（Contract class type）。
     * @return 支持返回 true（true when supported）。
     */
    default boolean supports(final Class<?> contractType) {
        Objects.requireNonNull(contractType, "contractType must not be null");
        return supports(CapabilityScope.READ, contractType)
                || supports(CapabilityScope.WRITE, contractType);
    }

    /**
     * @brief 解析并强制获取指定仓储契约（Resolve and Require Contract）；
     *        Resolve and require the given repository contract.
     *
     * @param <T>          contract 类型（Contract type）。
     * @param contractType 契约类型（Contract class type）。
     * @return 契约实现（Contract implementation）。
     * @throws IllegalStateException 当契约未被支持时抛出（Thrown when the contract is
     *                               unsupported）。
     */
    default <T> T require(final Class<T> contractType) {
        Objects.requireNonNull(contractType, "contractType must not be null");
        return resolve(contractType).orElseThrow(() -> new IllegalStateException(
                "Unsupported repository contract: " + contractType.getName()));
    }

    /**
     * @brief 关闭仓储资源（Close Repository Resources）；
     *        Close repository resources.
     *
     * @note 默认实现为空操作（no-op），便于无状态实现复用；
     *       Default implementation is no-op for stateless implementations.
     */
    @Override
    default void close() {
        // intentional no-op
    }

    /**
     * @brief 仓储描述符（Repository Descriptor），用于跨版本能力识别；
     *        Repository descriptor used for cross-version capability
     *        identification.
     *
     * @param implementation 实现名（Implementation name）。
     * @param apiVersion     API 版本（API version）。
     * @param capabilities   按能力域组织的反射契约集合（Reflective contracts grouped by
     *                       capability scope）。
     * @param attributes     可扩展属性（Extensible attributes）。
     */
    record RepositoryDescriptor(
            String implementation,
            SemanticVersion apiVersion,
            Map<CapabilityScope, Set<Class<?>>> capabilities,
            Map<String, String> attributes) {

        /**
         * @brief 构造并校验仓储描述符（Construct and Validate Descriptor）；
         *        Construct and validate repository descriptor.
         *
         * @param implementation 实现名（Implementation name）。
         * @param apiVersion     API 版本（API version）。
         * @param capabilities   按能力域组织的反射契约集合（Reflective contracts grouped by
         *                       capability scope）。
         * @param attributes     可扩展属性（Extensible attributes）。
         */
        public RepositoryDescriptor {
            if (implementation == null || implementation.isBlank()) {
                throw new IllegalArgumentException("implementation must not be blank");
            }
            Objects.requireNonNull(apiVersion, "apiVersion must not be null");
            final Map<CapabilityScope, Set<Class<?>>> normalized = Map.copyOf(
                    Objects.requireNonNull(capabilities, "capabilities must not be null"));
            for (CapabilityScope scope : CapabilityScope.values()) {
                if (!normalized.containsKey(scope)) {
                    throw new IllegalArgumentException("capabilities missing scope: " + scope);
                }
                normalized.get(scope)
                        .forEach(type -> Objects.requireNonNull(type, "capability contract type must not be null"));
            }
            capabilities = normalized.entrySet().stream()
                    .collect(java.util.stream.Collectors.toUnmodifiableMap(
                            Map.Entry::getKey,
                            entry -> Set.copyOf(entry.getValue())));
            attributes = Map.copyOf(Objects.requireNonNull(attributes, "attributes must not be null"));
        }

        /**
         * @brief 判断能力是否可用（Check Capability Availability）；
         *        Check whether a capability is available.
         *
         * @param scope        能力域（Capability scope）。
         * @param contractType 契约类型（Contract class type）。
         * @return 可用返回 true（true when available）。
         */
        public boolean supportsCapability(final CapabilityScope scope, final Class<?> contractType) {
            Objects.requireNonNull(scope, "scope must not be null");
            Objects.requireNonNull(contractType, "contractType must not be null");
            return capabilities.get(scope).contains(contractType);
        }
    }

    /**
     * @brief 仓储能力域（Repository Capability Scope），区分读写通道；
     *        Repository capability scope that separates read/write channels.
     */
    enum CapabilityScope {
        /**
         * @brief 读路径能力域（Read Scope）；
         *        Read-path capability scope.
         */
        READ,
        /**
         * @brief 写路径能力域（Write Scope）；
         *        Write-path capability scope.
         */
        WRITE
    }

    /**
     * @brief 语义化版本（Semantic Version），用于接口兼容判断；
     *        Semantic version used for interface compatibility checks.
     *
     * @param major 主版本号（Major version）。
     * @param minor 次版本号（Minor version）。
     * @param patch 修订版本号（Patch version）。
     */
    record SemanticVersion(int major, int minor, int patch) {

        /**
         * @brief 构造并校验语义化版本（Construct and Validate Semantic Version）；
         *        Construct and validate semantic version.
         *
         * @param major 主版本号（Major version）。
         * @param minor 次版本号（Minor version）。
         * @param patch 修订版本号（Patch version）。
         */
        public SemanticVersion {
            if (major < 0 || minor < 0 || patch < 0) {
                throw new IllegalArgumentException("Version numbers must be non-negative");
            }
        }

        /**
         * @brief 判断是否兼容目标版本（Check Compatibility With Target Version）；
         *        Check compatibility with the target version.
         *
         * @param target 目标版本（Target version）。
         * @return 兼容返回 true（true when compatible）。
         * @note 默认规则：同 major 且当前 minor 不小于目标 minor 视为兼容；
         *       Default rule: compatible when major is equal and current minor is not
         *       less than target minor.
         */
        public boolean isCompatibleWith(final SemanticVersion target) {
            Objects.requireNonNull(target, "target must not be null");
            return this.major == target.major && this.minor >= target.minor;
        }

        /**
         * @brief 以标准字符串渲染版本（Render Version as Canonical String）；
         *        Render semantic version as canonical string.
         *
         * @return `major.minor.patch` 字符串（`major.minor.patch` string）。
         */
        @Override
        public String toString() {
            return major + "." + minor + "." + patch;
        }
    }
}
