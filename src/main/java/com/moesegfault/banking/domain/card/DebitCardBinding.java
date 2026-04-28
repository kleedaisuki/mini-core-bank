package com.moesegfault.banking.domain.card;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @brief 扣账卡绑定值对象（Debit Card Binding Value Object），保存储蓄与外汇账户绑定关系；
 *        Debit card binding value object storing savings and FX account linkage.
 */
public final class DebitCardBinding implements Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief 储蓄账户 ID（Savings Account ID）；
     *        Savings account identifier.
     */
    private final SavingsAccountId savingsAccountId;

    /**
     * @brief 外汇账户 ID（FX Account ID）；
     *        FX account identifier.
     */
    private final FxAccountId fxAccountId;

    /**
     * @brief 构造扣账卡绑定对象（Construct Debit Card Binding）；
     *        Construct debit card binding.
     *
     * @param savingsAccountId 储蓄账户 ID（Savings account ID）。
     * @param fxAccountId      外汇账户 ID（FX account ID）。
     */
    private DebitCardBinding(
            final SavingsAccountId savingsAccountId,
            final FxAccountId fxAccountId
    ) {
        this.savingsAccountId = Objects.requireNonNull(savingsAccountId, "Savings account ID must not be null");
        this.fxAccountId = Objects.requireNonNull(fxAccountId, "FX account ID must not be null");
    }

    /**
     * @brief 创建扣账卡绑定（Factory Method）；
     *        Create debit card binding.
     *
     * @param savingsAccountId 储蓄账户 ID（Savings account ID）。
     * @param fxAccountId      外汇账户 ID（FX account ID）。
     * @return 扣账卡绑定对象（Debit card binding object）。
     */
    public static DebitCardBinding of(
            final SavingsAccountId savingsAccountId,
            final FxAccountId fxAccountId
    ) {
        return new DebitCardBinding(savingsAccountId, fxAccountId);
    }

    /**
     * @brief 返回储蓄账户 ID（Return Savings Account ID）；
     *        Return savings account ID.
     *
     * @return 储蓄账户 ID（Savings account ID）。
     */
    public SavingsAccountId savingsAccountId() {
        return savingsAccountId;
    }

    /**
     * @brief 返回外汇账户 ID（Return FX Account ID）；
     *        Return FX account ID.
     *
     * @return 外汇账户 ID（FX account ID）。
     */
    public FxAccountId fxAccountId() {
        return fxAccountId;
    }

    /**
     * @brief 值对象相等判定（Value Object Equality）；
     *        Value-object equality check.
     *
     * @param other 对比对象（Object to compare）。
     * @return 同值返回 true（true when equal）。
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DebitCardBinding that)) {
            return false;
        }
        return savingsAccountId.equals(that.savingsAccountId) && fxAccountId.equals(that.fxAccountId);
    }

    /**
     * @brief 计算哈希值（Compute Hash Code）；
     *        Compute hash code.
     *
     * @return 哈希值（Hash code）。
     */
    @Override
    public int hashCode() {
        return Objects.hash(savingsAccountId, fxAccountId);
    }
}
