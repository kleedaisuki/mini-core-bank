package com.moesegfault.banking.infrastructure.id;

/**
 * @brief ID 生成器接口（Id Generator Interface），用于生成系统内部唯一标识；
 * ID generator interface for producing unique internal identifiers.
 */
@FunctionalInterface
public interface IdGenerator {

    /**
     * @brief 生成下一个唯一 ID（Unique Identifier）；
     * Generate the next unique identifier.
     *
     * @return 新生成的唯一 ID 字符串；A newly generated unique identifier string.
     */
    String nextId();
}
