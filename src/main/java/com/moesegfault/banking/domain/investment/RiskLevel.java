package com.moesegfault.banking.domain.investment;

/**
 * @brief 风险等级枚举（Risk Level Enum），采用 R1..R5 的统一语言（Ubiquitous Language）；
 *        Risk-level enum using unified language R1..R5.
 */
public enum RiskLevel {

    /**
     * @brief R1 最低风险（R1 Lowest Risk）；
     *        R1 lowest risk level.
     */
    R1(1),

    /**
     * @brief R2 较低风险（R2 Lower Risk）；
     *        R2 lower risk level.
     */
    R2(2),

    /**
     * @brief R3 中等风险（R3 Medium Risk）；
     *        R3 medium risk level.
     */
    R3(3),

    /**
     * @brief R4 较高风险（R4 Higher Risk）；
     *        R4 higher risk level.
     */
    R4(4),

    /**
     * @brief R5 最高风险（R5 Highest Risk）；
     *        R5 highest risk level.
     */
    R5(5);

    /**
     * @brief 风险序数（Risk Rank）；
     *        Risk rank value.
     */
    private final int rank;

    /**
     * @brief 构造风险等级（Construct Risk Level）；
     *        Construct risk level.
     *
     * @param rank 风险序数（Risk rank）。
     */
    RiskLevel(final int rank) {
        this.rank = rank;
    }

    /**
     * @brief 返回风险序数（Return Risk Rank）；
     *        Return risk rank.
     *
     * @return 风险序数（Risk rank）。
     */
    public int rank() {
        return rank;
    }

    /**
     * @brief 判断是否不高于给定等级（Check Not Higher Than Target Level）；
     *        Check whether this level is not higher than target level.
     *
     * @param targetLevel 目标等级（Target level）。
     * @return 不高于目标等级返回 true（true when not higher）。
     */
    public boolean isNotHigherThan(final RiskLevel targetLevel) {
        return this.rank <= targetLevel.rank;
    }
}
