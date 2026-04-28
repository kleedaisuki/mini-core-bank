package com.moesegfault.banking.presentation.gui.card;

import java.util.List;

/**
 * @brief 信用附属卡发卡页面模型（Issue Supplementary Credit Card Page Model），保存 `card issue-supplementary-credit` 表单与提交状态；
 *        Model for `card issue-supplementary-credit` page storing form fields and submission state.
 */
public final class IssueSupplementaryCreditCardModel extends AbstractCardIssueModel {

    /**
     * @brief 字段顺序定义（Field Order Definition）；
     *        Canonical field order aligned with supplementary-credit-card schema.
     */
    private static final List<String> FIELD_ORDER = List.of(
            CardGuiSchema.HOLDER_CUSTOMER_ID,
            CardGuiSchema.PRIMARY_CREDIT_CARD_ID,
            CardGuiSchema.CREDIT_CARD_ACCOUNT_ID,
            CardGuiSchema.CARD_NO);

    /**
     * @brief 构造页面模型（Construct Page Model）；
     *        Construct issue-supplementary-credit-card page model.
     */
    public IssueSupplementaryCreditCardModel() {
        super(FIELD_ORDER);
    }
}
