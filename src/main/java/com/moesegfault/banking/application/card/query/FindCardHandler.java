package com.moesegfault.banking.application.card.query;

import com.moesegfault.banking.application.card.CardApplicationException;
import com.moesegfault.banking.application.card.result.CardKind;
import com.moesegfault.banking.application.card.result.CardResult;
import com.moesegfault.banking.domain.card.CardId;
import com.moesegfault.banking.domain.card.CardRepository;
import com.moesegfault.banking.domain.card.CreditCard;
import com.moesegfault.banking.domain.card.DebitCard;
import com.moesegfault.banking.domain.card.SupplementaryDebitCard;
import java.util.Objects;

/**
 * @brief 卡片查询处理器（Find Card Handler），聚合三张卡表并返回统一读模型；
 *        Card query handler aggregating three card tables into a unified read model.
 */
public final class FindCardHandler {

    /**
     * @brief 卡仓储接口（Card Repository Interface）；
     *        Card repository interface.
     */
    private final CardRepository cardRepository;

    /**
     * @brief 构造查询处理器（Construct Query Handler）；
     *        Construct query handler.
     *
     * @param cardRepository 卡仓储接口（Card repository interface）。
     */
    public FindCardHandler(final CardRepository cardRepository) {
        this.cardRepository = Objects.requireNonNull(cardRepository, "cardRepository must not be null");
    }

    /**
     * @brief 执行卡片查询（Handle Card Query）；
     *        Handle card query and return unified card result.
     *
     * @param query 查询请求（Query request）。
     * @return 卡片结果（Card query result）。
     */
    public CardResult handle(final FindCardQuery query) {
        final FindCardQuery normalizedQuery = Objects.requireNonNull(query, "query must not be null");
        final CardId cardId = CardId.of(normalizedQuery.cardId());

        final DebitCard debitCard = cardRepository.findDebitCardById(cardId).orElse(null);
        if (debitCard != null) {
            return new CardResult(
                    debitCard.cardId().value(),
                    debitCard.cardNumber().masked(),
                    debitCard.holderCustomerId().value(),
                    debitCard.cardStatus().name(),
                    CardKind.DEBIT,
                    debitCard.issuedAt(),
                    debitCard.expiredAtOrNull(),
                    debitCard.binding().savingsAccountId().value(),
                    debitCard.binding().fxAccountId().value(),
                    null,
                    null);
        }

        final SupplementaryDebitCard supplementaryDebitCard =
                cardRepository.findSupplementaryDebitCardById(cardId).orElse(null);
        if (supplementaryDebitCard != null) {
            return new CardResult(
                    supplementaryDebitCard.supplementaryCardId().value(),
                    supplementaryDebitCard.cardNumber().masked(),
                    supplementaryDebitCard.holderCustomerId().value(),
                    supplementaryDebitCard.cardStatus().name(),
                    CardKind.SUPPLEMENTARY_DEBIT,
                    supplementaryDebitCard.issuedAt(),
                    supplementaryDebitCard.expiredAtOrNull(),
                    null,
                    null,
                    null,
                    supplementaryDebitCard.primaryDebitCardId().value());
        }

        final CreditCard creditCard = cardRepository.findCreditCardById(cardId).orElse(null);
        if (creditCard != null) {
            final CardKind cardKind = creditCard.isSupplementary()
                    ? CardKind.SUPPLEMENTARY_CREDIT
                    : CardKind.PRIMARY_CREDIT;
            return new CardResult(
                    creditCard.creditCardId().value(),
                    creditCard.cardNumber().masked(),
                    creditCard.holderCustomerId().value(),
                    creditCard.cardStatus().name(),
                    cardKind,
                    creditCard.issuedAt(),
                    creditCard.expiredAtOrNull(),
                    null,
                    null,
                    creditCard.creditCardAccountId().value(),
                    creditCard.primaryCreditCardIdOrNull() == null ? null : creditCard.primaryCreditCardIdOrNull().value());
        }

        throw new CardApplicationException("Card not found by card_id: " + cardId.value());
    }
}
