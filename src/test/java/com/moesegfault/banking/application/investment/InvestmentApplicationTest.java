package com.moesegfault.banking.application.investment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.moesegfault.banking.application.investment.command.BuyProductCommand;
import com.moesegfault.banking.application.investment.command.BuyProductHandler;
import com.moesegfault.banking.application.investment.command.CreateInvestmentProductCommand;
import com.moesegfault.banking.application.investment.command.CreateInvestmentProductHandler;
import com.moesegfault.banking.application.investment.query.ListHoldingsHandler;
import com.moesegfault.banking.application.investment.query.ListHoldingsQuery;
import com.moesegfault.banking.application.investment.result.HoldingResult;
import com.moesegfault.banking.application.investment.result.InvestmentOrderResult;
import com.moesegfault.banking.application.investment.result.InvestmentProductResult;
import com.moesegfault.banking.domain.account.Account;
import com.moesegfault.banking.domain.account.AccountId;
import com.moesegfault.banking.domain.account.AccountNumber;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.account.AccountType;
import com.moesegfault.banking.domain.account.CustomerId;
import com.moesegfault.banking.domain.account.FxAccount;
import com.moesegfault.banking.domain.account.FxAccountId;
import com.moesegfault.banking.domain.account.InvestmentAccount;
import com.moesegfault.banking.domain.account.InvestmentAccountId;
import com.moesegfault.banking.domain.account.SavingsAccount;
import com.moesegfault.banking.domain.account.SavingsAccountId;
import com.moesegfault.banking.domain.business.BusinessCategory;
import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.business.BusinessReference;
import com.moesegfault.banking.domain.business.BusinessRepository;
import com.moesegfault.banking.domain.business.BusinessTransaction;
import com.moesegfault.banking.domain.business.BusinessTransactionId;
import com.moesegfault.banking.domain.business.BusinessTransactionStatus;
import com.moesegfault.banking.domain.business.BusinessType;
import com.moesegfault.banking.domain.business.BusinessTypeCode;
import com.moesegfault.banking.domain.business.BusinessTypeStatus;
import com.moesegfault.banking.domain.investment.Holding;
import com.moesegfault.banking.domain.investment.HoldingId;
import com.moesegfault.banking.domain.investment.InvestmentOrder;
import com.moesegfault.banking.domain.investment.InvestmentOrderId;
import com.moesegfault.banking.domain.investment.InvestmentProduct;
import com.moesegfault.banking.domain.investment.InvestmentRepository;
import com.moesegfault.banking.domain.investment.NetAssetValue;
import com.moesegfault.banking.domain.investment.OrderSide;
import com.moesegfault.banking.domain.investment.ProductCode;
import com.moesegfault.banking.domain.investment.ProductId;
import com.moesegfault.banking.domain.investment.ProductStatus;
import com.moesegfault.banking.domain.investment.ProductType;
import com.moesegfault.banking.domain.investment.ProductValuation;
import com.moesegfault.banking.domain.investment.Quantity;
import com.moesegfault.banking.domain.investment.RiskLevel;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.domain.shared.Money;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class InvestmentApplicationTest {

    @Test
    void shouldCreateInvestmentProductAndRejectDuplicateCode() {
        final InMemoryInvestmentRepository investmentRepository = new InMemoryInvestmentRepository();
        final CreateInvestmentProductHandler handler = new CreateInvestmentProductHandler(
                investmentRepository,
                new SequenceIdGenerator(),
                new InlineTransactionManager(),
                DomainEventPublisher.noop());

        final CreateInvestmentProductCommand command = new CreateInvestmentProductCommand(
                "FUND_USD_A",
                "USD Growth Fund",
                ProductType.FUND,
                CurrencyCode.of("USD"),
                RiskLevel.R3,
                "MOE Asset");

        final InvestmentProductResult result = handler.handle(command);
        assertNotNull(result.productId());
        assertEquals("FUND_USD_A", result.productCode());
        assertEquals("ACTIVE", result.productStatus());

        assertThrows(BusinessRuleViolation.class, () -> handler.handle(command));
    }

    @Test
    void shouldPlaceBuyOrderAndUpdateHolding() {
        final InMemoryInvestmentRepository investmentRepository = new InMemoryInvestmentRepository();
        final InMemoryBusinessRepository businessRepository = new InMemoryBusinessRepository();
        final InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        final SequenceIdGenerator idGenerator = new SequenceIdGenerator();

        accountRepository.addInvestmentAccount(openInvestmentAccount("inv-acc-001", "cust-001", "ACC-INV-001"));
        businessRepository.saveBusinessType(BusinessType.create(
                InvestmentBusinessTypeCodes.BUY_PRODUCT,
                BusinessCategory.INVESTMENT,
                "Buy product",
                "Buy investment product",
                true,
                true));
        businessRepository.saveBusinessType(BusinessType.create(
                InvestmentBusinessTypeCodes.SELL_PRODUCT,
                BusinessCategory.INVESTMENT,
                "Sell product",
                "Sell investment product",
                true,
                true));

        final InvestmentProduct product = InvestmentProduct.create(
                ProductId.of("prod-001"),
                ProductCode.of("FUND_USD_A"),
                "USD Growth Fund",
                ProductType.FUND,
                CurrencyCode.of("USD"),
                RiskLevel.R3,
                "MOE Asset");
        investmentRepository.saveInvestmentProduct(product);

        final BuyProductHandler handler = new BuyProductHandler(
                investmentRepository,
                accountRepository,
                businessRepository,
                idGenerator,
                new InlineTransactionManager(),
                DomainEventPublisher.noop());

        final InvestmentOrderResult result = handler.handle(new BuyProductCommand(
                "inv-acc-001",
                "FUND_USD_A",
                new BigDecimal("10.000000"),
                new BigDecimal("1.250000"),
                new BigDecimal("0.1000"),
                "cust-001",
                BusinessChannel.ONLINE,
                "INV-BUY-001",
                RiskLevel.R4,
                Instant.parse("2026-04-28T01:00:00Z")));

        assertEquals("SUCCESS", result.transactionStatus());
        assertEquals("SETTLED", result.orderStatus());
        assertEquals(new BigDecimal("10.000000"), result.holdingQuantityAfter());
        assertEquals(new BigDecimal("-12.6000"), result.cashImpact());

        final Holding holding = investmentRepository.listHoldingsByAccountId(
                com.moesegfault.banking.domain.investment.InvestmentAccountId.of("inv-acc-001")).get(0);
        assertEquals(new BigDecimal("12.500000"), holding.marketValue());
        assertEquals(new BigDecimal("0.000000"), holding.unrealizedPnl());
    }

    @Test
    void shouldListHoldingsWithProductDetails() {
        final InMemoryInvestmentRepository investmentRepository = new InMemoryInvestmentRepository();
        final InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();

        accountRepository.addInvestmentAccount(openInvestmentAccount("inv-acc-001", "cust-001", "ACC-INV-001"));

        final InvestmentProduct product = InvestmentProduct.create(
                ProductId.of("prod-001"),
                ProductCode.of("FUND_USD_A"),
                "USD Growth Fund",
                ProductType.FUND,
                CurrencyCode.of("USD"),
                RiskLevel.R3,
                "MOE Asset");
        investmentRepository.saveInvestmentProduct(product);

        final Holding holding = Holding.restore(
                HoldingId.of("hold-001"),
                com.moesegfault.banking.domain.investment.InvestmentAccountId.of("inv-acc-001"),
                ProductId.of("prod-001"),
                Quantity.of(new BigDecimal("5.000000")),
                new BigDecimal("1.100000"),
                CurrencyCode.of("USD"),
                new BigDecimal("6.000000"),
                CurrencyCode.of("USD"),
                new BigDecimal("0.500000"),
                Instant.parse("2026-04-28T08:00:00Z"));
        investmentRepository.saveHolding(holding);

        final ListHoldingsHandler handler = new ListHoldingsHandler(investmentRepository, accountRepository);

        final List<HoldingResult> holdings = handler.handle(new ListHoldingsQuery("inv-acc-001", true));
        assertEquals(1, holdings.size());
        assertEquals("FUND_USD_A", holdings.get(0).productCodeOrNull());
        assertEquals("USD Growth Fund", holdings.get(0).productNameOrNull());
    }

    private static InvestmentAccount openInvestmentAccount(
            final String accountId,
            final String customerId,
            final String accountNo
    ) {
        final Account base = Account.open(
                AccountId.of(accountId),
                CustomerId.of(customerId),
                AccountNumber.of(accountNo),
                AccountType.INVESTMENT);
        return InvestmentAccount.restore(base);
    }

    private static final class InlineTransactionManager implements DbTransactionManager {

        @Override
        public <T> T execute(final java.util.function.Supplier<T> action) {
            return action.get();
        }
    }

    private static final class SequenceIdGenerator implements IdGenerator {

        private final AtomicInteger sequence = new AtomicInteger();

        @Override
        public String nextId() {
            return "id-" + sequence.incrementAndGet();
        }
    }

    private static final class InMemoryAccountRepository implements AccountRepository {

        private final Map<String, Account> accountById = new HashMap<>();

        private final Map<String, InvestmentAccount> investmentAccountById = new HashMap<>();

        void addInvestmentAccount(final InvestmentAccount investmentAccount) {
            accountById.put(investmentAccount.account().accountId().value(), investmentAccount.account());
            investmentAccountById.put(investmentAccount.investmentAccountId().value(), investmentAccount);
        }

        @Override
        public void saveAccount(final Account account) {
            accountById.put(account.accountId().value(), account);
        }

        @Override
        public void saveSavingsAccount(final SavingsAccount savingsAccount) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveFxAccount(final FxAccount fxAccount) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveInvestmentAccount(final InvestmentAccount investmentAccount) {
            addInvestmentAccount(investmentAccount);
        }

        @Override
        public Optional<Account> findAccountById(final AccountId accountId) {
            return Optional.ofNullable(accountById.get(accountId.value()));
        }

        @Override
        public Optional<Account> findAccountByNumber(final AccountNumber accountNo) {
            return accountById.values().stream()
                    .filter(account -> account.accountNo().equals(accountNo))
                    .findFirst();
        }

        @Override
        public Optional<SavingsAccount> findSavingsAccountById(final SavingsAccountId savingsAccountId) {
            return Optional.empty();
        }

        @Override
        public Optional<FxAccount> findFxAccountById(final FxAccountId fxAccountId) {
            return Optional.empty();
        }

        @Override
        public Optional<InvestmentAccount> findInvestmentAccountById(final InvestmentAccountId investmentAccountId) {
            return Optional.ofNullable(investmentAccountById.get(investmentAccountId.value()));
        }

        @Override
        public List<Account> findAccountsByCustomerId(final CustomerId customerId) {
            return accountById.values().stream()
                    .filter(account -> account.customerId().equals(customerId))
                    .toList();
        }

        @Override
        public boolean existsByAccountNumber(final AccountNumber accountNo) {
            return findAccountByNumber(accountNo).isPresent();
        }

        @Override
        public long countInvestmentAccountsByCustomerId(final CustomerId customerId) {
            return investmentAccountById.values().stream()
                    .filter(account -> account.account().customerId().equals(customerId))
                    .count();
        }
    }

    private static final class InMemoryBusinessRepository implements BusinessRepository {

        private final Map<String, BusinessType> typeByCode = new HashMap<>();

        private final Map<String, BusinessTransaction> txById = new HashMap<>();

        private final Map<String, BusinessTransaction> txByReference = new HashMap<>();

        @Override
        public void saveBusinessType(final BusinessType businessType) {
            typeByCode.put(businessType.businessTypeCode().value(), businessType);
        }

        @Override
        public void saveTransaction(final BusinessTransaction businessTransaction) {
            txById.put(businessTransaction.transactionId().value(), businessTransaction);
            txByReference.put(businessTransaction.referenceNo().value(), businessTransaction);
        }

        @Override
        public Optional<BusinessType> findBusinessTypeByCode(final BusinessTypeCode businessTypeCode) {
            return Optional.ofNullable(typeByCode.get(businessTypeCode.value()));
        }

        @Override
        public Optional<BusinessTransaction> findTransactionById(final BusinessTransactionId transactionId) {
            return Optional.ofNullable(txById.get(transactionId.value()));
        }

        @Override
        public Optional<BusinessTransaction> findTransactionByReference(final BusinessReference referenceNo) {
            return Optional.ofNullable(txByReference.get(referenceNo.value()));
        }

        @Override
        public boolean existsTransactionByReference(final BusinessReference referenceNo) {
            return txByReference.containsKey(referenceNo.value());
        }

        @Override
        public List<BusinessTransaction> listTransactionsByCustomerId(
                final com.moesegfault.banking.domain.business.CustomerId initiatorCustomerId
        ) {
            return txById.values().stream()
                    .filter(tx -> tx.initiatorCustomerIdOrNull() != null)
                    .filter(tx -> tx.initiatorCustomerIdOrNull().equals(initiatorCustomerId))
                    .sorted(Comparator.comparing(BusinessTransaction::requestedAt).reversed())
                    .toList();
        }

        @Override
        public List<BusinessTransaction> listTransactionsByStatus(
                final BusinessTransactionStatus transactionStatus
        ) {
            return txById.values().stream()
                    .filter(tx -> tx.transactionStatus() == transactionStatus)
                    .sorted(Comparator.comparing(BusinessTransaction::requestedAt).reversed())
                    .toList();
        }

        @Override
        public List<BusinessTransaction> findAllTransactions() {
            return txById.values().stream()
                    .sorted(Comparator.comparing(BusinessTransaction::requestedAt).reversed())
                    .toList();
        }
    }

    private static final class InMemoryInvestmentRepository implements InvestmentRepository {

        private final Map<String, InvestmentProduct> productById = new HashMap<>();

        private final Map<String, InvestmentProduct> productByCode = new HashMap<>();

        private final Map<String, InvestmentOrder> orderById = new HashMap<>();

        private final Map<String, Holding> holdingById = new HashMap<>();

        private final Map<String, String> holdingIdByAccountProductKey = new HashMap<>();

        private final Map<String, Map<LocalDate, ProductValuation>> valuationsByProductId = new HashMap<>();

        @Override
        public void saveInvestmentProduct(final InvestmentProduct investmentProduct) {
            productById.put(investmentProduct.productId().value(), investmentProduct);
            productByCode.put(investmentProduct.productCode().value(), investmentProduct);
        }

        @Override
        public void saveInvestmentOrder(final InvestmentOrder investmentOrder) {
            orderById.put(investmentOrder.investmentOrderId().value(), investmentOrder);
        }

        @Override
        public void saveHolding(final Holding holding) {
            holdingById.put(holding.holdingId().value(), holding);
            holdingIdByAccountProductKey.put(
                    key(holding.investmentAccountId().value(), holding.productId().value()),
                    holding.holdingId().value());
        }

        @Override
        public void saveProductValuation(final ProductValuation productValuation) {
            valuationsByProductId
                    .computeIfAbsent(productValuation.productId().value(), ignored -> new HashMap<>())
                    .put(productValuation.valuationDate(), productValuation);
        }

        @Override
        public Optional<InvestmentProduct> findProductById(final ProductId productId) {
            return Optional.ofNullable(productById.get(productId.value()));
        }

        @Override
        public Optional<InvestmentProduct> findProductByCode(final ProductCode productCode) {
            return Optional.ofNullable(productByCode.get(productCode.value()));
        }

        @Override
        public Optional<InvestmentOrder> findOrderById(final InvestmentOrderId investmentOrderId) {
            return Optional.ofNullable(orderById.get(investmentOrderId.value()));
        }

        @Override
        public Optional<Holding> findHoldingById(final HoldingId holdingId) {
            return Optional.ofNullable(holdingById.get(holdingId.value()));
        }

        @Override
        public Optional<Holding> findHoldingByAccountAndProduct(
                final com.moesegfault.banking.domain.investment.InvestmentAccountId investmentAccountId,
                final ProductId productId
        ) {
            final String holdingId = holdingIdByAccountProductKey.get(key(investmentAccountId.value(), productId.value()));
            return holdingId == null ? Optional.empty() : Optional.ofNullable(holdingById.get(holdingId));
        }

        @Override
        public List<Holding> listHoldingsByAccountId(
                final com.moesegfault.banking.domain.investment.InvestmentAccountId investmentAccountId
        ) {
            final List<Holding> holdings = new ArrayList<>();
            for (Holding holding : holdingById.values()) {
                if (holding.investmentAccountId().equals(investmentAccountId)) {
                    holdings.add(holding);
                }
            }
            holdings.sort(Comparator.comparing(Holding::updatedAt).reversed());
            return holdings;
        }

        @Override
        public Optional<ProductValuation> findProductValuationByDate(final ProductId productId, final LocalDate valuationDate) {
            final Map<LocalDate, ProductValuation> byDate = valuationsByProductId.get(productId.value());
            if (byDate == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(byDate.get(valuationDate));
        }

        @Override
        public Optional<ProductValuation> findLatestProductValuation(final ProductId productId) {
            final Map<LocalDate, ProductValuation> byDate = valuationsByProductId.get(productId.value());
            if (byDate == null || byDate.isEmpty()) {
                return Optional.empty();
            }
            return byDate.entrySet().stream()
                    .max(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue);
        }

        private static String key(final String accountId, final String productId) {
            return accountId + "::" + productId;
        }
    }
}
