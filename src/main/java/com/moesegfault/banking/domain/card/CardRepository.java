package com.moesegfault.banking.domain.card;

import java.util.List;
import java.util.Optional;

/**
 * @brief 卡仓储接口（Card Repository Interface），定义卡领域聚合（Aggregate）持久化契约；
 *        Card repository interface defining persistence contracts for card aggregates.
 */
public interface CardRepository {

    /**
     * @brief 保存扣账卡（Save Debit Card）；
     *        Save debit card entity.
     *
     * @param debitCard 扣账卡实体（Debit card entity）。
     */
    void saveDebitCard(DebitCard debitCard);

    /**
     * @brief 保存扣账附属卡（Save Supplementary Debit Card）；
     *        Save supplementary debit card entity.
     *
     * @param supplementaryDebitCard 附属扣账卡实体（Supplementary debit card entity）。
     */
    void saveSupplementaryDebitCard(SupplementaryDebitCard supplementaryDebitCard);

    /**
     * @brief 保存信用卡（Save Credit Card）；
     *        Save credit card entity.
     *
     * @param creditCard 信用卡实体（Credit card entity）。
     */
    void saveCreditCard(CreditCard creditCard);

    /**
     * @brief 按 ID 查询扣账卡（Find Debit Card by ID）；
     *        Find debit card by ID.
     *
     * @param cardId 卡片 ID（Card ID）。
     * @return 扣账卡可选值（Optional debit card）。
     */
    Optional<DebitCard> findDebitCardById(CardId cardId);

    /**
     * @brief 按 ID 查询扣账附属卡（Find Supplementary Debit Card by ID）；
     *        Find supplementary debit card by ID.
     *
     * @param supplementaryCardId 附属卡 ID（Supplementary card ID）。
     * @return 附属扣账卡可选值（Optional supplementary debit card）。
     */
    Optional<SupplementaryDebitCard> findSupplementaryDebitCardById(CardId supplementaryCardId);

    /**
     * @brief 按 ID 查询信用卡（Find Credit Card by ID）；
     *        Find credit card by ID.
     *
     * @param creditCardId 信用卡 ID（Credit card ID）。
     * @return 信用卡可选值（Optional credit card）。
     */
    Optional<CreditCard> findCreditCardById(CardId creditCardId);

    /**
     * @brief 按卡号查询扣账卡（Find Debit Card by Card Number）；
     *        Find debit card by card number.
     *
     * @param cardNumber 卡号（Card number）。
     * @return 扣账卡可选值（Optional debit card）。
     */
    Optional<DebitCard> findDebitCardByCardNumber(CardNumber cardNumber);

    /**
     * @brief 按卡号查询信用卡（Find Credit Card by Card Number）；
     *        Find credit card by card number.
     *
     * @param cardNumber 卡号（Card number）。
     * @return 信用卡可选值（Optional credit card）。
     */
    Optional<CreditCard> findCreditCardByCardNumber(CardNumber cardNumber);

    /**
     * @brief 检查任一卡表是否已存在该卡号（Exists Any Card by Card Number）；
     *        Check whether card number exists in any card table.
     *
     * @param cardNumber 卡号（Card number）。
     * @return 存在返回 true（true when exists）。
     */
    boolean existsAnyByCardNumber(CardNumber cardNumber);

    /**
     * @brief 查询主扣账卡的附属卡数量（Count Supplementary Cards by Primary Debit Card）；
     *        Count supplementary debit cards by primary debit card ID.
     *
     * @param primaryDebitCardId 主扣账卡 ID（Primary debit card ID）。
     * @return 附属卡数量（Supplementary card count）。
     */
    long countSupplementaryDebitCardsByPrimary(CardId primaryDebitCardId);

    /**
     * @brief 查询信用卡账户下的全部信用卡（Find Credit Cards by Credit Account）；
     *        Find all credit cards under a credit-card account.
     *
     * @param creditCardAccountId 信用卡账户 ID（Credit card account ID）。
     * @return 信用卡列表（Credit card list）。
     */
    List<CreditCard> findCreditCardsByAccountId(CreditCardAccountId creditCardAccountId);
}
