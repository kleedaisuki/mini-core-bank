package com.moesegfault.banking.application.card.command;

import com.moesegfault.banking.application.card.CardApplicationException;
import com.moesegfault.banking.application.card.CardApplicationSupport;
import com.moesegfault.banking.application.card.result.CardKind;
import com.moesegfault.banking.application.card.result.IssueCardResult;
import com.moesegfault.banking.domain.card.CardId;
import com.moesegfault.banking.domain.card.CardNumber;
import com.moesegfault.banking.domain.card.CardRepository;
import com.moesegfault.banking.domain.card.CustomerId;
import com.moesegfault.banking.domain.card.DebitCard;
import com.moesegfault.banking.domain.card.SupplementaryCardPolicy;
import com.moesegfault.banking.domain.card.SupplementaryDebitCard;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.util.Objects;

/**
 * @brief 借记附属卡发卡处理器（Issue Supplementary Debit Card Handler），编排主卡约束与附属卡发行；
 *        Issue-supplementary-debit-card handler orchestrating primary-card constraints and issuance.
 */
public final class IssueSupplementaryDebitCardHandler {

    /**
     * @brief 卡仓储接口（Card Repository Interface）；
     *        Card repository interface.
     */
    private final CardRepository cardRepository;

    /**
     * @brief 客户仓储接口（Customer Repository Interface）；
     *        Customer repository interface.
     */
    private final CustomerRepository customerRepository;

    /**
     * @brief ID 生成器接口（ID Generator Interface）；
     *        ID generator interface.
     */
    private final IdGenerator idGenerator;

    /**
     * @brief 事务管理接口（Transaction Manager Interface）；
     *        Transaction manager interface.
     */
    private final DbTransactionManager transactionManager;

    /**
     * @brief 领域事件发布接口（Domain Event Publisher Interface）；
     *        Domain event publisher interface.
     */
    private final DomainEventPublisher eventPublisher;

    /**
     * @brief 构造借记附属卡发卡处理器（Construct Issue Supplementary Debit Card Handler）；
     *        Construct issue-supplementary-debit-card handler.
     *
     * @param cardRepository     卡仓储接口（Card repository interface）。
     * @param customerRepository 客户仓储接口（Customer repository interface）。
     * @param idGenerator        ID 生成器接口（ID generator interface）。
     * @param transactionManager 事务管理接口（Transaction manager interface）。
     * @param eventPublisher     领域事件发布接口（Domain event publisher interface）。
     */
    public IssueSupplementaryDebitCardHandler(
            final CardRepository cardRepository,
            final CustomerRepository customerRepository,
            final IdGenerator idGenerator,
            final DbTransactionManager transactionManager,
            final DomainEventPublisher eventPublisher
    ) {
        this.cardRepository = Objects.requireNonNull(cardRepository, "cardRepository must not be null");
        this.customerRepository = Objects.requireNonNull(customerRepository, "customerRepository must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
        this.transactionManager = Objects.requireNonNull(transactionManager, "transactionManager must not be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher must not be null");
    }

    /**
     * @brief 执行借记附属卡发卡（Handle Supplementary Debit Card Issuance）；
     *        Handle supplementary debit-card issuance.
     *
     * @param command 发卡命令（Issuance command）。
     * @return 发卡结果（Issuance result）。
     */
    public IssueCardResult handle(final IssueSupplementaryDebitCardCommand command) {
        final IssueSupplementaryDebitCardCommand normalizedCommand =
                Objects.requireNonNull(command, "command must not be null");
        return transactionManager.execute(() -> issueInTransaction(normalizedCommand));
    }

    /**
     * @brief 在事务中执行发卡编排（Execute Issuance Orchestration in Transaction）；
     *        Execute issuance orchestration inside one transaction.
     *
     * @param command 发卡命令（Issuance command）。
     * @return 发卡结果（Issuance result）。
     */
    private IssueCardResult issueInTransaction(final IssueSupplementaryDebitCardCommand command) {
        final com.moesegfault.banking.domain.customer.CustomerId holderCustomerId =
                com.moesegfault.banking.domain.customer.CustomerId.of(command.holderCustomerId());
        final Customer holder = customerRepository.findById(holderCustomerId)
                .orElseThrow(() -> new CardApplicationException(
                        "holder_customer_id not found in customer schema: " + command.holderCustomerId()));
        CardApplicationSupport.ensureEligibleCustomer(holder);

        final CardId primaryDebitCardId = CardId.of(command.primaryDebitCardId());
        final DebitCard primaryDebitCard = cardRepository.findDebitCardById(primaryDebitCardId)
                .orElseThrow(() -> new CardApplicationException(
                        "primary_debit_card_id not found in debit_card schema: " + command.primaryDebitCardId()));
        SupplementaryCardPolicy.ensurePrimaryDebitCardCanIssue(primaryDebitCard);

        final long supplementaryCount = cardRepository.countSupplementaryDebitCardsByPrimary(primaryDebitCardId);
        if (supplementaryCount > 0L) {
            throw new CardApplicationException(
                    "primary_debit_card_id already has supplementary debit card in supplementary_debit_card schema");
        }

        final CardNumber cardNumber = CardNumber.of(command.cardNo());
        CardApplicationSupport.ensureUniqueCardNumber(cardRepository, cardNumber);

        final SupplementaryDebitCard supplementaryDebitCard = SupplementaryDebitCard.issue(
                CardId.of(idGenerator.nextId()),
                cardNumber,
                CustomerId.of(holder.customerId().value()),
                primaryDebitCardId);
        cardRepository.saveSupplementaryDebitCard(supplementaryDebitCard);
        eventPublisher.publish(supplementaryDebitCard.issuedEvent());

        return new IssueCardResult(
                supplementaryDebitCard.supplementaryCardId().value(),
                supplementaryDebitCard.cardNumber().masked(),
                CardKind.SUPPLEMENTARY_DEBIT,
                supplementaryDebitCard.cardStatus().name(),
                supplementaryDebitCard.holderCustomerId().value(),
                supplementaryDebitCard.issuedAt(),
                supplementaryDebitCard.primaryDebitCardId().value());
    }
}
