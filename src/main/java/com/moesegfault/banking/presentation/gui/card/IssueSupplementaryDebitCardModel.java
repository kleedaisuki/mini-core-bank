package com.moesegfault.banking.presentation.gui.card;

import java.util.List;

/**
 * @brief 借记附属卡发卡页面模型（Issue Supplementary Debit Card Page Model），保存 `card issue-supplementary-debit` 表单与提交状态；
 *        Model for `card issue-supplementary-debit` page storing form fields and submission state.
 */
public final class IssueSupplementaryDebitCardModel extends AbstractCardIssueModel {

    /**
     * @brief 字段顺序定义（Field Order Definition）；
     *        Canonical field order aligned with supplementary-debit-card schema.
     */
    private static final List<String> FIELD_ORDER = List.of(
            CardGuiSchema.HOLDER_CUSTOMER_ID,
            CardGuiSchema.PRIMARY_DEBIT_CARD_ID,
            CardGuiSchema.CARD_NO);

    /**
     * @brief 构造页面模型（Construct Page Model）；
     *        Construct issue-supplementary-debit-card page model.
     */
    public IssueSupplementaryDebitCardModel() {
        super(FIELD_ORDER);
    }
}
