package com.moesegfault.banking.presentation.gui.card;

import java.util.List;

/**
 * @brief 主信用卡发卡页面模型（Issue Credit Card Page Model），保存 `card issue-credit` 表单与提交状态；
 *        Model for `card issue-credit` page storing form fields and submission state.
 */
public final class IssueCreditCardModel extends AbstractCardIssueModel {

    /**
     * @brief 字段顺序定义（Field Order Definition）；
     *        Canonical field order aligned with primary-credit-card schema.
     */
    private static final List<String> FIELD_ORDER = List.of(
            CardGuiSchema.HOLDER_CUSTOMER_ID,
            CardGuiSchema.CREDIT_CARD_ACCOUNT_ID,
            CardGuiSchema.CARD_NO);

    /**
     * @brief 构造页面模型（Construct Page Model）；
     *        Construct issue-credit-card page model.
     */
    public IssueCreditCardModel() {
        super(FIELD_ORDER);
    }
}
