package com.moesegfault.banking.presentation.gui.card;

import java.util.List;

/**
 * @brief 主借记卡发卡页面模型（Issue Debit Card Page Model），保存 `card issue-debit` 表单与提交状态；
 *        Model for `card issue-debit` page storing form fields and submission state.
 */
public final class IssueDebitCardModel extends AbstractCardIssueModel {

    /**
     * @brief 字段顺序定义（Field Order Definition）；
     *        Canonical field order aligned with debit-card schema.
     */
    private static final List<String> FIELD_ORDER = List.of(
            CardGuiSchema.HOLDER_CUSTOMER_ID,
            CardGuiSchema.SAVINGS_ACCOUNT_ID,
            CardGuiSchema.FX_ACCOUNT_ID,
            CardGuiSchema.CARD_NO);

    /**
     * @brief 构造页面模型（Construct Page Model）；
     *        Construct issue-debit-card page model.
     */
    public IssueDebitCardModel() {
        super(FIELD_ORDER);
    }
}
