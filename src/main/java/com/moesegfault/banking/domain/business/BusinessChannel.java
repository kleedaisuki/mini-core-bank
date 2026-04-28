package com.moesegfault.banking.domain.business;

/**
 * @brief 业务渠道枚举（Business Channel Enum），严格对齐 `business_transaction.channel` 检查约束；
 *        Business channel enum strictly aligned with `business_transaction.channel` check constraints.
 */
public enum BusinessChannel {

    /**
     * @brief 网点渠道（Branch Channel）；
     *        Branch channel.
     */
    BRANCH,

    /**
     * @brief 手机渠道（Mobile Channel）；
     *        Mobile channel.
     */
    MOBILE,

    /**
     * @brief ATM 渠道（ATM Channel）；
     *        ATM channel.
     */
    ATM,

    /**
     * @brief 在线渠道（Online Channel）；
     *        Online channel.
     */
    ONLINE,

    /**
     * @brief 系统渠道（System Channel）；
     *        System channel.
     */
    SYSTEM
}
