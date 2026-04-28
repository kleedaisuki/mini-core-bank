```text
src/main/java/com/moesegfault/banking/domain/
# 领域层。保存业务概念、业务规则和值对象。不得依赖 application、presentation、infrastructure。

├── shared/
│   # 真正跨领域共享的基础概念。必须克制，不能变成垃圾桶。

│   ├── EntityId.java
│   │   # 通用实体 ID 基类或接口。可选，用于统一 ID 行为。

│   ├── Money.java
│   │   # 金额值对象。封装 BigDecimal 和 CurrencyCode，禁止 double 存钱。

│   ├── CurrencyCode.java
│   │   # 币种代码值对象，例如 USD、CNH、EUR、JPY。

│   ├── Percentage.java
│   │   # 百分比值对象。用于利率、手续费率、收益率。

│   ├── DateRange.java
│   │   # 日期区间值对象。用于账单周期、统计周期。

│   ├── DomainException.java
│   │   # 领域异常基类。表示业务规则被违反。

│   ├── BusinessRuleViolation.java
│   │   # 业务规则违反异常。用于表达不满足领域不变量。

│   ├── DomainEvent.java
│   │   # 领域事件接口。后续可用于记录 CustomerRegistered、AccountOpened 等事件。

│   └── DomainEventPublisher.java
│       # 领域事件发布接口。当前可以先不用，预留扩展点。
├── customer/
│   # 客户领域。负责客户身份、联系方式、税务信息、客户状态。

│   ├── Customer.java
│   │   # 客户实体。表示银行注册客户，包含身份信息、地址、税务信息、状态。

│   ├── CustomerId.java
│   │   # 客户 ID 值对象。封装内部客户编号。

│   ├── CustomerStatus.java
│   │   # 客户状态枚举，例如 ACTIVE、FROZEN、CLOSED。

│   ├── IdentityDocument.java
│   │   # 证件值对象。包含证件类型、证件号码、签发地区。

│   ├── IdentityDocumentType.java
│   │   # 证件类型枚举，例如 ID_CARD、PASSPORT、HKID。

│   ├── PhoneNumber.java
│   │   # 电话号码值对象。封装流动电话格式和基本校验。

│   ├── Address.java
│   │   # 地址值对象。用于居住地址和通信地址。

│   ├── TaxProfile.java
│   │   # 税务资料值对象。包含是否美国税务居民和 CRS 信息。

│   ├── CrsInfo.java
│   │   # CRS 信息值对象。CRS 是 Common Reporting Standard，通用报告准则。

│   ├── CustomerPolicy.java
│   │   # 客户领域策略。放客户能否开户、能否持卡等规则。

│   ├── CustomerRepository.java
│   │   # 客户仓储接口。定义保存、查询客户的方法，由 infrastructure 实现。

│   └── CustomerRegistered.java
│       # 客户注册领域事件。表示一个客户已经完成注册。
├── account/
│   # 账户领域。负责储蓄账户、外汇账户、投资账户、账户状态和账户关系。

│   ├── Account.java
│   │   # 账户抽象实体或基础类。表示共有的账户身份、户号、客户归属、状态。

│   ├── AccountId.java
│   │   # 账户 ID 值对象。

│   ├── AccountNumber.java
│   │   # 户号值对象。封装账户号码格式和唯一性语义。

│   ├── AccountType.java
│   │   # 账户类型枚举，例如 SAVINGS、FX、INVESTMENT、CREDIT_CARD。

│   ├── AccountStatus.java
│   │   # 账户状态枚举，例如 ACTIVE、FROZEN、CLOSED。

│   ├── SavingsAccount.java
│   │   # 储蓄账户实体。表示客户的基础存款账户。

│   ├── FxAccount.java
│   │   # 外汇账户实体。必须绑定一个储蓄账户，可持有多币种余额。

│   ├── InvestmentAccount.java
│   │   # 投资账户实体。表示客户用于购买理财产品的账户容器。

│   ├── FxAccountLink.java
│   │   # 外汇账户与储蓄账户的绑定关系值对象。

│   ├── AccountOwnershipPolicy.java
│   │   # 账户归属策略。验证账户是否属于指定客户。

│   ├── AccountOpeningPolicy.java
│   │   # 开户策略。检查客户是否允许开某类账户，以及投资账户数量限制。

│   ├── AccountRepository.java
│   │   # 账户仓储接口。保存和查询储蓄、外汇、投资账户。

│   ├── SavingsAccountOpened.java
│   │   # 储蓄账户已开立领域事件。

│   ├── FxAccountOpened.java
│   │   # 外汇账户已开立领域事件。

│   └── InvestmentAccountOpened.java
│       # 投资账户已开立领域事件。
├── card/
│   # 卡领域。负责扣账卡、扣账附属卡、信用卡卡片和卡状态。

│   ├── CardId.java
│   │   # 卡 ID 值对象。

│   ├── CardNumber.java
│   │   # 卡号值对象。封装卡号格式和脱敏显示。

│   ├── CardStatus.java
│   │   # 卡状态枚举，例如 ACTIVE、FROZEN、LOST、CANCELLED、EXPIRED。

│   ├── CardExpiry.java
│   │   # 卡有效期值对象。

│   ├── DebitCard.java
│   │   # 扣账卡实体。绑定一个储蓄账户和一个外汇账户。

│   ├── SupplementaryDebitCard.java
│   │   # 扣账附属卡实体。绑定主扣账卡，持有人必须是注册客户。

│   ├── CreditCard.java
│   │   # 信用卡卡片实体。表示主信用卡或附属信用卡。

│   ├── CardRole.java
│   │   # 卡角色枚举，例如 PRIMARY、SUPPLEMENTARY。

│   ├── DebitCardBinding.java
│   │   # 扣账卡绑定值对象。保存储蓄账户 ID 和外汇账户 ID。

│   ├── SupplementaryCardPolicy.java
│   │   # 附属卡策略。检查副卡持有人和主卡关系是否合法。

│   ├── CardIssuingPolicy.java
│   │   # 发卡策略。检查客户状态、账户绑定、卡数量限制。

│   ├── CardRepository.java
│   │   # 卡仓储接口。保存和查询扣账卡、信用卡。

│   ├── DebitCardIssued.java
│   │   # 扣账卡已发出领域事件。

│   ├── SupplementaryDebitCardIssued.java
│   │   # 扣账附属卡已发出领域事件。

│   ├── CreditCardIssued.java
│   │   # 信用卡已发出领域事件。

│   └── SupplementaryCreditCardIssued.java
│       # 信用卡附属卡已发出领域事件。
├── credit/
│   # 信用领域。负责信用卡账户、额度、账单、还款、逾期。

│   ├── CreditCardAccount.java
│   │   # 信用卡账户实体。管理信用额度、可用额度、账单日、还款日。

│   ├── CreditCardAccountId.java
│   │   # 信用卡账户 ID 值对象。可复用 AccountId，也可单独建模。

│   ├── CreditLimit.java
│   │   # 信用额度值对象。包含总额度、可用额度、预借现金额度。

│   ├── BillingCycle.java
│   │   # 账单周期值对象。包含账单日、还款日、周期范围。

│   ├── InterestRate.java
│   │   # 利率值对象。用于信用卡利息计算。

│   ├── CreditCardStatement.java
│   │   # 信用卡账单实体。表示一个账期的应还、最低还款、已还状态。

│   ├── StatementId.java
│   │   # 账单 ID 值对象。

│   ├── StatementStatus.java
│   │   # 账单状态枚举，例如 OPEN、PAID、OVERDUE、CLOSED。

│   ├── MinimumPaymentPolicy.java
│   │   # 最低还款策略。根据账单金额计算最低还款额。

│   ├── RepaymentAllocationPolicy.java
│   │   # 还款分配策略。决定还款如何抵扣账单、利息、费用、本金。

│   ├── CreditRepository.java
│   │   # 信用领域仓储接口。保存信用账户、账单、还款记录。

│   ├── CreditCardAccountOpened.java
│   │   # 信用卡账户已开立领域事件。

│   ├── CreditCardStatementGenerated.java
│   │   # 信用卡账单已生成领域事件。

│   └── CreditCardRepaymentReceived.java
│       # 信用卡还款已收到领域事件。
├── investment/
│   # 投资领域。负责投资产品、订单、持仓、估值、盈亏。

│   ├── InvestmentProduct.java
│   │   # 投资产品实体。表示理财产品、基金、债券等可购买产品。

│   ├── ProductId.java
│   │   # 产品 ID 值对象。

│   ├── ProductCode.java
│   │   # 产品代码值对象。

│   ├── ProductType.java
│   │   # 产品类型枚举，例如 FUND、BOND、STRUCTURED_PRODUCT、WEALTH_MANAGEMENT。

│   ├── ProductStatus.java
│   │   # 产品状态枚举，例如 LISTED、SUSPENDED、CLOSED。

│   ├── RiskLevel.java
│   │   # 风险等级值对象或枚举。用于产品风险和客户适配检查。

│   ├── InvestmentOrder.java
│   │   # 投资订单实体。表示买入、卖出、赎回、分红等投资交易。

│   ├── InvestmentOrderId.java
│   │   # 投资订单 ID 值对象。

│   ├── OrderSide.java
│   │   # 订单方向枚举，例如 BUY、SELL、REDEMPTION、DIVIDEND。

│   ├── OrderStatus.java
│   │   # 订单状态枚举，例如 PLACED、SETTLED、FAILED、CANCELLED。

│   ├── Holding.java
│   │   # 投资持仓实体。表示投资账户持有某产品的份额、成本、市值。

│   ├── HoldingId.java
│   │   # 持仓 ID 值对象。

│   ├── Quantity.java
│   │   # 份额值对象。用于投资产品份额，禁止负数。

│   ├── NetAssetValue.java
│   │   # 产品净值值对象。NAV，Net Asset Value。

│   ├── ProductValuation.java
│   │   # 产品估值实体或值对象。表示某产品某日期的净值。

│   ├── SuitabilityPolicy.java
│   │   # 适当性策略。检查客户风险承受能力是否匹配产品风险。

│   ├── HoldingPolicy.java
│   │   # 持仓策略。检查持仓是否足够卖出、份额是否非负。

│   ├── InvestmentRepository.java
│   │   # 投资领域仓储接口。保存产品、订单、持仓、估值。

│   ├── InvestmentProductCreated.java
│   │   # 投资产品已创建领域事件。

│   ├── InvestmentOrderPlaced.java
│   │   # 投资订单已提交领域事件。

│   ├── InvestmentOrderSettled.java
│   │   # 投资订单已结算领域事件。

│   └── HoldingChanged.java
│       # 投资持仓已变化领域事件。
├── ledger/
│   # 账务领域。负责余额、分录、入账批次和不可变记账规则。

│   ├── Balance.java
│   │   # 余额实体或状态对象。表示某账户某币种的账面余额和可用余额。

│   ├── LedgerEntry.java
│   │   # 账务分录实体。表示一次业务对某账户某币种造成的资金变化。

│   ├── LedgerEntryId.java
│   │   # 账务分录 ID 值对象。

│   ├── PostingBatch.java
│   │   # 入账批次实体。一次业务可能产生多条分录，批次用于统一提交。

│   ├── PostingBatchId.java
│   │   # 入账批次 ID 值对象。

│   ├── EntryDirection.java
│   │   # 分录方向枚举，例如 INCREASE、DECREASE，或 DEBIT、CREDIT。

│   ├── EntryType.java
│   │   # 分录类型枚举，例如 PRINCIPAL、FEE、INTEREST、DIVIDEND、REPAYMENT。

│   ├── PostingStatus.java
│   │   # 入账状态枚举，例如 PENDING、POSTED、REVERSED。

│   ├── BalancePolicy.java
│   │   # 余额策略。检查是否允许扣款、可用余额是否充足。

│   ├── PostingPolicy.java
│   │   # 入账策略。确保入账幂等、分录不可变、已入账批次不可修改。

│   ├── LedgerRepository.java
│   │   # 账务仓储接口。保存余额、分录、入账批次。

│   ├── LedgerEntryPosted.java
│   │   # 账务分录已入账领域事件。

│   ├── BalanceUpdated.java
│   │   # 余额已更新领域事件。

│   └── PostingReversed.java
│       # 入账已冲正领域事件。
├── business/
│   # 业务流水领域。负责业务码、业务交易、业务状态、审计参考号。

│   ├── BusinessTransaction.java
│   │   # 业务交易实体。表示一次业务操作，例如开户、开卡、转账、买产品。

│   ├── BusinessTransactionId.java
│   │   # 业务交易 ID 值对象。

│   ├── BusinessReference.java
│   │   # 业务参考号值对象。用于幂等和外部查询。

│   ├── BusinessType.java
│   │   # 业务类型实体或枚举。表示业务码和业务分类。

│   ├── BusinessTypeCode.java
│   │   # 业务码值对象，例如 OPEN_SAVINGS_ACCOUNT、BUY_PRODUCT。

│   ├── BusinessCategory.java
│   │   # 业务分类枚举，例如 ACCOUNT、CARD、INVESTMENT、CREDIT、LEDGER。

│   ├── BusinessTransactionStatus.java
│   │   # 业务交易状态枚举，例如 PENDING、SUCCESS、FAILED、REVERSED。

│   ├── BusinessChannel.java
│   │   # 业务渠道枚举，例如 CLI、BRANCH、ATM、MOBILE、SYSTEM。

│   ├── BusinessTransactionPolicy.java
│   │   # 业务流水策略。检查业务能否完成、能否冲正、幂等性语义。

│   ├── BusinessRepository.java
│   │   # 业务流水仓储接口。保存业务类型和业务交易。

│   ├── BusinessTransactionStarted.java
│   │   # 业务交易已开始领域事件。

│   ├── BusinessTransactionCompleted.java
│   │   # 业务交易已完成领域事件。

│   └── BusinessTransactionFailed.java
│       # 业务交易失败领域事件。
```
