package com.moesegfault.banking.infrastructure.id;

import java.util.UUID;

/**
 * @brief 基于 UUID（Universally Unique Identifier）的 ID 生成器实现；
 * UUID-based implementation of the ID generator.
 */
public final class UuidIdGenerator implements IdGenerator {

    /**
     * @brief 生成 UUID 形式的唯一 ID；
     * Generate a UUID-formatted unique identifier.
     *
     * @return UUID 字符串（例如 xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx）；
     * UUID string in canonical textual format.
     */
    @Override
    public String nextId() {
        return UUID.randomUUID().toString();
    }
}
