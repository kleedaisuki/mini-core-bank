先给出整体思路，再给出完整表族。

---

我们现在的系统可以分成八个互相独立但又有关联的域：

| 领域                                | 主要解决什么                 |
| --------------------------------- | ---------------------- |
| 客户域 Customer Domain               | 谁是银行客户，身份、税务、CRS 信息是什么 |
| 账户域 Account Domain                | 储蓄、外汇、投资、信用卡等账户容器      |
| 余额与分录域 Ledger Domain              | 当前余额、历史入账、资金变动         |
| 卡域 Card Domain                    | 扣账卡、附属卡、信用卡、信用卡附属卡     |
| 投资域 Investment Domain             | 理财产品、持仓、估值、买卖交易        |
| 信用域 Credit Domain                 | 信用额度、账单、还款、最低还款额       |
| 业务交易域 Business Transaction Domain | 开户、开卡、转账、买产品、还款等统一业务流水 |
| 字典/配置域 Reference Domain           | 币种、业务码、渠道、状态、风险等级等     |

一个简化版全局关系图可以这样理解：

```text
Customer
  ├── Account
  │     ├── SavingsAccount
  │     ├── FxAccount
  │     ├── InvestmentAccount
  │     └── CreditCardAccount
  │
  ├── DebitCard
  │     └── SupplementaryDebitCard
  │
  ├── CreditCard
  │     └── SupplementaryCreditCard
  │
  └── BusinessTransaction

Account
  ├── AccountBalance
  └── AccountEntry

InvestmentAccount
  ├── InvestmentHolding
  └── InvestmentOrderDetail

CreditCardAccount
  ├── CreditLimit
  ├── CreditCardStatement
  └── CreditCardRepayment

BusinessTransaction
  ├── TransferDetail
  ├── InvestmentOrderDetail
  ├── CardOperationDetail
  ├── AccountOperationDetail
  ├── CreditCardTransactionDetail
  └── AccountEntry
```

这套模型的核心哲学是：

**客户是主体，账户是容器，卡是访问工具，业务交易是事件，账户分录是资金变化的事实，余额是当前状态快照。**

---

## 1. 客户域：`customer`

客户表是整个系统的根实体之一。

```text
customer(
  customer_id PK,
  id_type,
  id_number,
  issuing_region,
  mobile_phone,
  residential_address,
  mailing_address,
  is_us_tax_resident,
  crs_info,
  customer_status,
  created_at,
  updated_at,

  UNIQUE(id_type, id_number, issuing_region)
)
```

字段说明：

| 字段                    | 说明                                            |
| --------------------- | --------------------------------------------- |
| `customer_id`         | 银行内部客户 ID                                     |
| `id_type`             | 证件类型                                          |
| `id_number`           | 证件号码                                          |
| `issuing_region`      | 签发地区                                          |
| `mobile_phone`        | 流动电话                                          |
| `residential_address` | 居住地址                                          |
| `mailing_address`     | 通信地址                                          |
| `is_us_tax_resident`  | 是否美国税务居民                                      |
| `crs_info`            | CRS 信息，CRS 是 Common Reporting Standard，通用报告准则 |
| `customer_status`     | ACTIVE / FROZEN / CLOSED                      |

这里建议 `id_type + id_number + issuing_region` 唯一，因为同一个证件在同一签发地区应该只对应一个客户。

如果 CRS 信息很复杂，也可以单独拆表：

```text
customer_tax_profile(
  customer_id PK/FK,
  is_us_tax_resident,
  crs_country_code,
  tax_identification_number,
  crs_declaration_date
)
```

但作业里放在 `customer` 里也可以。

---

## 2. 账户超类型：`account`

我们不为储蓄、外汇、投资、信用卡完全独立建四套主表，而是采用 **超类型/子类型建模**，也就是 supertype/subtype modeling。

所有账户共享的信息放在 `account`。

```text
account(
  account_id PK,
  customer_id FK -> customer.customer_id,
  account_no UNIQUE,
  account_type,
  account_status,
  opened_at,
  closed_at,

  UNIQUE(account_id, customer_id)
)
```

账户类型：

| account_type  | 说明    |
| ------------- | ----- |
| `SAVINGS`     | 储蓄账户  |
| `FX`          | 外汇账户  |
| `INVESTMENT`  | 投资账户  |
| `CREDIT_CARD` | 信用卡账户 |

为什么信用卡也可以放进 `account`？

因为信用卡虽然不是“存款账户”，但它仍然是一个可记账的金融账户。它有额度、欠款、账单、还款、利息、费用，也会产生分录。把它放进统一 `account` 体系，可以让 `account_entry` 和 `business_transaction` 统一处理资金事件。

不过要注意：信用卡账户的余额语义和储蓄账户相反。储蓄账户余额是资产，信用卡欠款是负债。这个我们后面用 `account_entry` 和 `credit_card_account` 解释。

---

## 3. 储蓄账户子类型：`savings_account`

```text
savings_account(
  account_id PK/FK -> account.account_id
)
```

储蓄账户本身没有太多额外字段，因为余额已经放进 `account_balance`。

原题里“储蓄账户：美元账面余额、可用余额”会被重构为：

```text
account_balance(account_id, 'USD', ledger_balance, available_balance)
```

这样以后如果储蓄账户也允许多币种，就不用改表。

---

## 4. 外汇账户子类型：`fx_account`

```text
fx_account(
  account_id PK/FK -> account.account_id,
  linked_savings_account_id FK -> savings_account.account_id
)
```

业务规则：

一个外汇账户必须绑定一个储蓄账户。

一个储蓄账户可以绑定多个外汇账户。

外汇账户和它绑定的储蓄账户必须属于同一个客户。

最后这个“同一客户”约束可以用组合外键或触发器实现。概念上必须满足：

```text
account(fx_account.account_id).customer_id
=
account(fx_account.linked_savings_account_id).customer_id
```

外汇账户里的多币种余额也进入 `account_balance`：

```text
CNH, EUR, JPY, SGD, HKD, KRW, AUD, GBP, CAD
```

不再把每种货币写成一组列。

---

## 5. 投资账户子类型：`investment_account`

```text
investment_account(
  account_id PK/FK -> account.account_id
)
```

投资账户本身只是容器。它里面有两类东西：

第一，现金余额，放在 `account_balance`。

第二，产品持仓，放在 `investment_holding`。

原题里的“盈亏”不直接作为投资账户字段。它可以从持仓、成本、产品估值里计算，也可以在持仓表里缓存。

---

## 6. 信用卡账户子类型：`credit_card_account`

加信用卡以后，我们最好区分两个东西：

**信用卡账户**：负责额度、账单、欠款。

**信用卡实体**：具体那张卡，卡号、持卡人、主卡/附属卡关系。

一名客户可以拥有一个或多个信用卡账户。一个信用卡账户可以有一张主卡，也可以有附属卡。

```text
credit_card_account(
  account_id PK/FK -> account.account_id,
  credit_limit,
  available_credit,
  billing_cycle_day,
  payment_due_day,
  interest_rate,
  cash_advance_limit,
  account_currency_code FK -> currency.currency_code
)
```

字段说明：

| 字段                      | 说明           |
| ----------------------- | ------------ |
| `credit_limit`          | 总信用额度        |
| `available_credit`      | 可用额度         |
| `billing_cycle_day`     | 账单日          |
| `payment_due_day`       | 到期还款日        |
| `interest_rate`         | 年化或日利率，具体看定义 |
| `cash_advance_limit`    | 预借现金额度       |
| `account_currency_code` | 信用卡账户主币种     |

这里有一个设计点：`available_credit` 可以从额度、授权占用、已入账欠款计算出来，所以它也是一种快照字段。真实系统中通常会缓存它，以便快速做授权判断。

---

## 7. 币种表：`currency`

```text
currency(
  currency_code PK,
  currency_name,
  minor_unit,
  is_active
)
```

示例：

| currency_code | currency_name     | minor_unit |
| ------------- | ----------------- | ---------: |
| USD           | US Dollar         |          2 |
| CNH           | Offshore Renminbi |          2 |
| EUR           | Euro              |          2 |
| JPY           | Japanese Yen      |          0 |
| SGD           | Singapore Dollar  |          2 |
| HKD           | Hong Kong Dollar  |          2 |
| KRW           | Korean Won        |          0 |
| AUD           | Australian Dollar |          2 |
| GBP           | Pound Sterling    |          2 |
| CAD           | Canadian Dollar   |          2 |

`minor_unit` 表示小数位。JPY 和 KRW 通常是 0，USD、EUR、HKD 通常是 2。

---

## 8. 账户余额表：`account_balance`

```text
account_balance(
  account_id FK -> account.account_id,
  currency_code FK -> currency.currency_code,
  ledger_balance,
  available_balance,
  updated_at,

  PRIMARY KEY(account_id, currency_code)
)
```

字段说明：

| 字段                  | 说明   |
| ------------------- | ---- |
| `ledger_balance`    | 账面余额 |
| `available_balance` | 可用余额 |
| `currency_code`     | 币种   |

对储蓄账户：

```text
ledger_balance = 存款账面余额
available_balance = 可支配余额
```

对外汇账户：

```text
每个币种一行余额
```

对投资账户：

```text
投资账户里的现金余额
```

对信用卡账户：

可以有两种做法。

第一种，信用卡欠款不放 `account_balance`，只放信用卡账单和信用卡分录。

第二种，把信用卡账户也放进统一余额体系。比如欠款记为正数或负数，但必须全系统统一。

我更推荐：

```text
account_balance 主要用于客户资产账户的现金余额；
信用卡欠款主要由 credit_card_statement 和 account_entry 表达。
```

但为了统一记账，也可以允许信用卡账户进入 `account_balance`，只是要清晰定义符号方向。

作业里可以写：

> 信用卡账户的当前欠款通过信用卡账单与账户分录计算，`account_balance` 主要服务储蓄、外汇、投资现金余额。

这样更不容易混。

---

## 9. 账户分录表：`account_entry`

这是资金变动的事实表。

```text
account_entry(
  entry_id PK,
  transaction_id FK -> business_transaction.transaction_id,
  account_id FK -> account.account_id,
  currency_code FK -> currency.currency_code,
  entry_direction,
  amount,
  ledger_balance_after,
  available_balance_after,
  entry_type,
  posted_at
)
```

字段说明：

| 字段                        | 说明                                                |
| ------------------------- | ------------------------------------------------- |
| `entry_id`                | 分录 ID                                             |
| `transaction_id`          | 所属业务交易                                            |
| `account_id`              | 被影响的账户                                            |
| `currency_code`           | 币种                                                |
| `entry_direction`         | DEBIT / CREDIT 或 INCREASE / DECREASE              |
| `amount`                  | 金额                                                |
| `ledger_balance_after`    | 入账后账面余额                                           |
| `available_balance_after` | 入账后可用余额                                           |
| `entry_type`              | PRINCIPAL / FEE / INTEREST / DIVIDEND / REPAYMENT |
| `posted_at`               | 入账时间                                              |

银行味道最浓的地方就在这里：一次业务可以产生多条分录。

例如行内转账 100 USD：

```text
business_transaction: T001 TRANSFER_INTERNAL

account_entry:
  A001 -100 USD
  A002 +100 USD
```

信用卡消费 100 USD：

```text
business_transaction: T002 CREDIT_CARD_PURCHASE

account_entry:
  credit_card_account +100 USD 欠款
```

信用卡还款 100 USD：

```text
business_transaction: T003 CREDIT_CARD_REPAYMENT

account_entry:
  savings_account -100 USD
  credit_card_account -100 USD 欠款
```

这里我们没有强行实现完整复式记账，但已经有足够好的审计轨迹。

---

## 10. 扣账卡表：`debit_card`

扣账卡，也就是 debit card，本质上是访问储蓄账户和外汇账户的工具。

```text
debit_card(
  card_id PK,
  card_no UNIQUE,
  holder_customer_id FK -> customer.customer_id,
  savings_account_id FK -> savings_account.account_id,
  fx_account_id FK -> fx_account.account_id,
  card_status,
  issued_at,
  expired_at
)
```

业务规则：

一张扣账卡必须绑定一个储蓄账户。

一张扣账卡必须绑定一个外汇账户。

一个储蓄账户可以被多张扣账卡绑定。

一个外汇账户可以被多张扣账卡绑定。

扣账卡持有人必须是银行注册客户。

扣账卡绑定的储蓄账户、外汇账户应该属于扣账卡持有人。概念约束：

```text
account(savings_account_id).customer_id = holder_customer_id
account(fx_account_id).customer_id = holder_customer_id
```

---

## 11. 扣账附属卡表：`supplementary_debit_card`

```text
supplementary_debit_card(
  supplementary_card_id PK,
  card_no UNIQUE,
  holder_customer_id FK -> customer.customer_id,
  primary_debit_card_id FK -> debit_card.card_id,
  card_status,
  issued_at,
  expired_at
)
```

业务规则：

附属卡持有人必须是银行注册客户。

一张扣账卡可以绑定 0 或 1 张附属卡。

如果只允许一张附属卡，则需要：

```text
UNIQUE(primary_debit_card_id)
```

如果以后允许多张副卡，就去掉这个唯一约束。

你原题说“一张扣账卡绑定一张附属卡（可以为空）”，所以当前版本应该加唯一约束。

---

## 12. 信用卡实体表：`credit_card`

信用卡和扣账卡最好不要硬塞进同一张 `card` 表，除非我们专门做卡超类型。原因是信用卡有额度、账单、授权、还款、利息等独立语义。

这里先做简单但干净的版本：

```text
credit_card(
  credit_card_id PK,
  card_no UNIQUE,
  holder_customer_id FK -> customer.customer_id,
  credit_card_account_id FK -> credit_card_account.account_id,
  card_role,
  primary_credit_card_id FK -> credit_card.credit_card_id NULL,
  card_status,
  issued_at,
  expired_at
)
```

字段说明：

| 字段                       | 说明                      |
| ------------------------ | ----------------------- |
| `card_role`              | PRIMARY / SUPPLEMENTARY |
| `primary_credit_card_id` | 如果是附属信用卡，指向主信用卡         |
| `credit_card_account_id` | 所属信用卡账户                 |
| `holder_customer_id`     | 持卡人                     |

这样可以同时表达主卡和附属卡。

主卡：

```text
card_role = PRIMARY
primary_credit_card_id = NULL
holder_customer_id = 主卡客户
```

附属卡：

```text
card_role = SUPPLEMENTARY
primary_credit_card_id = 主卡 ID
holder_customer_id = 副卡客户
```

业务规则：

附属信用卡持有人必须是银行注册客户。

附属信用卡挂靠主信用卡。

主卡和附属卡通常共享同一个 `credit_card_account_id`，也就是共享一个信用卡账户和额度。

概念约束：

```text
credit_card(SUPPLEMENTARY).credit_card_account_id
=
credit_card(PRIMARY).credit_card_account_id
```

---

## 13. 信用卡账单表：`credit_card_statement`

信用卡不能只靠余额表。账单是信用卡域的核心对象。

```text
credit_card_statement(
  statement_id PK,
  credit_card_account_id FK -> credit_card_account.account_id,
  statement_period_start,
  statement_period_end,
  statement_date,
  payment_due_date,
  total_amount_due,
  minimum_amount_due,
  paid_amount,
  statement_status,
  currency_code FK -> currency.currency_code
)
```

字段说明：

| 字段                       | 说明                             |
| ------------------------ | ------------------------------ |
| `statement_period_start` | 账单周期开始                         |
| `statement_period_end`   | 账单周期结束                         |
| `statement_date`         | 出账日                            |
| `payment_due_date`       | 到期还款日                          |
| `total_amount_due`       | 本期应还总额                         |
| `minimum_amount_due`     | 最低还款额                          |
| `paid_amount`            | 已还金额                           |
| `statement_status`       | OPEN / PAID / OVERDUE / CLOSED |

信用卡还款会引用账单，也会产生业务交易和账户分录。

---

## 14. 信用卡交易明细：`credit_card_transaction_detail`

信用卡消费、退款、取现、还款、年费、利息，都可以作为业务交易。

```text
credit_card_transaction_detail(
  transaction_id PK/FK -> business_transaction.transaction_id,
  credit_card_id FK -> credit_card.credit_card_id,
  credit_card_account_id FK -> credit_card_account.account_id,
  statement_id FK -> credit_card_statement.statement_id NULL,
  merchant_name,
  merchant_category_code,
  amount,
  currency_code FK -> currency.currency_code,
  authorization_code,
  transaction_at,
  posted_at
)
```

字段说明：

| 字段                       | 说明                                 |
| ------------------------ | ---------------------------------- |
| `merchant_category_code` | 商户类别码，MCC 是 Merchant Category Code |
| `authorization_code`     | 授权码                                |
| `transaction_at`         | 消费发生时间                             |
| `posted_at`              | 入账时间                               |

这里可以支持：

```text
CREDIT_CARD_PURCHASE
CREDIT_CARD_REFUND
CREDIT_CARD_CASH_ADVANCE
CREDIT_CARD_FEE
CREDIT_CARD_INTEREST
CREDIT_CARD_REPAYMENT
```

还款本身也可以有专门表，因为它通常涉及来源账户。

---

## 15. 信用卡还款表：`credit_card_repayment`

```text
credit_card_repayment(
  transaction_id PK/FK -> business_transaction.transaction_id,
  credit_card_account_id FK -> credit_card_account.account_id,
  from_account_id FK -> account.account_id,
  statement_id FK -> credit_card_statement.statement_id NULL,
  repayment_amount,
  currency_code FK -> currency.currency_code,
  repayment_at
)
```

还款会产生分录：

```text
储蓄账户减少
信用卡欠款减少
```

例如：

```text
business_transaction:
  T100 CREDIT_CARD_REPAYMENT

credit_card_repayment:
  from_account_id = SAV001
  credit_card_account_id = CC_ACC001
  amount = 1000 HKD

account_entry:
  SAV001 -1000 HKD
  CC_ACC001 -1000 HKD 欠款
```

---

## 16. 投资产品表：`investment_product`

```text
investment_product(
  product_id PK,
  product_code UNIQUE,
  product_name,
  product_type,
  currency_code FK -> currency.currency_code,
  risk_level,
  issuer,
  status
)
```

产品类型可以包括：

| product_type         | 说明            |
| -------------------- | ------------- |
| `FUND`               | 基金            |
| `BOND`               | 债券            |
| `STRUCTURED_PRODUCT` | 结构性产品         |
| `WEALTH_MANAGEMENT`  | 理财产品          |
| `TIME_DEPOSIT`       | 定期类产品，也可以独立建模 |

---

## 17. 投资持仓表：`investment_holding`

```text
investment_holding(
  holding_id PK,
  investment_account_id FK -> investment_account.account_id,
  product_id FK -> investment_product.product_id,
  quantity,
  average_cost,
  cost_currency_code FK -> currency.currency_code,
  market_value,
  valuation_currency_code FK -> currency.currency_code,
  unrealized_pnl,
  updated_at,

  UNIQUE(investment_account_id, product_id)
)
```

字段说明：

| 字段               | 说明                               |
| ---------------- | -------------------------------- |
| `quantity`       | 当前持有份额                           |
| `average_cost`   | 平均成本                             |
| `market_value`   | 当前市值                             |
| `unrealized_pnl` | 未实现盈亏，unrealized profit and loss |

投资账户的“盈亏”可以从这里聚合：

```text
investment_account_pnl
= sum(investment_holding.unrealized_pnl)
```

如果还要实现已实现盈亏，可以从卖出交易计算，或增加 `realized_pnl` 字段。

---

## 18. 产品估值表：`product_valuation`

```text
product_valuation(
  product_id FK -> investment_product.product_id,
  valuation_date,
  nav,
  currency_code FK -> currency.currency_code,

  PRIMARY KEY(product_id, valuation_date)
)
```

`nav` 是净值，英文是 Net Asset Value。

持仓市值可以这样算：

```text
market_value = quantity * nav
```

其中：

`market_value` 是当前市值。

`quantity` 是持有份额。

`nav` 是产品单位净值。

---

## 19. 业务类型表：`business_type`

这是业务码表。

```text
business_type(
  business_type_code PK,
  business_category,
  business_name,
  description,
  is_financial,
  is_reversible,
  status
)
```

示例业务码：

| business_type_code               | category    | 说明      |
| -------------------------------- | ----------- | ------- |
| `OPEN_SAVINGS_ACCOUNT`           | ACCOUNT     | 开储蓄账户   |
| `OPEN_FX_ACCOUNT`                | ACCOUNT     | 开外汇账户   |
| `OPEN_INVESTMENT_ACCOUNT`        | ACCOUNT     | 开投资账户   |
| `OPEN_DEBIT_CARD`                | CARD        | 开扣账卡    |
| `OPEN_SUPPLEMENTARY_DEBIT_CARD`  | CARD        | 开扣账附属卡  |
| `OPEN_CREDIT_CARD`               | CARD        | 开信用卡    |
| `OPEN_SUPPLEMENTARY_CREDIT_CARD` | CARD        | 开信用卡附属卡 |
| `TRANSFER_INTERNAL`              | TRANSFER    | 行内转账    |
| `FX_EXCHANGE`                    | TRANSFER    | 外汇兑换    |
| `BUY_PRODUCT`                    | INVESTMENT  | 买入理财产品  |
| `SELL_PRODUCT`                   | INVESTMENT  | 卖出或赎回产品 |
| `CREDIT_CARD_PURCHASE`           | CREDIT_CARD | 信用卡消费   |
| `CREDIT_CARD_REFUND`             | CREDIT_CARD | 信用卡退款   |
| `CREDIT_CARD_REPAYMENT`          | CREDIT_CARD | 信用卡还款   |
| `CREDIT_CARD_FEE`                | CREDIT_CARD | 信用卡费用   |
| `CREDIT_CARD_INTEREST`           | CREDIT_CARD | 信用卡利息   |

这个表定义的是“银行支持什么业务”。

---

## 20. 业务交易主表：`business_transaction`

```text
business_transaction(
  transaction_id PK,
  business_type_code FK -> business_type.business_type_code,
  initiator_customer_id FK -> customer.customer_id NULL,
  operator_id NULL,
  channel,
  transaction_status,
  requested_at,
  completed_at NULL,
  reference_no UNIQUE,
  remarks NULL
)
```

字段说明：

| 字段                      | 说明                                      |
| ----------------------- | --------------------------------------- |
| `transaction_id`        | 内部交易 ID                                 |
| `business_type_code`    | 业务码                                     |
| `initiator_customer_id` | 发起客户                                    |
| `operator_id`           | 柜员、系统、后台操作人                             |
| `channel`               | BRANCH / MOBILE / ATM / ONLINE / SYSTEM |
| `transaction_status`    | PENDING / SUCCESS / FAILED / REVERSED   |
| `reference_no`          | 对外流水号                                   |

这个表回答：

```text
谁，在什么时候，通过什么渠道，发起了什么业务，结果如何？
```

它不负责塞所有业务字段。业务专属字段进入对应 detail 表。

---

## 21. 转账明细表：`transfer_detail`

```text
transfer_detail(
  transaction_id PK/FK -> business_transaction.transaction_id,
  from_account_id FK -> account.account_id,
  from_currency_code FK -> currency.currency_code,
  to_account_id FK -> account.account_id,
  to_currency_code FK -> currency.currency_code,
  amount,
  exchange_rate,
  fee_amount,
  fee_currency_code FK -> currency.currency_code NULL
)
```

支持：

普通转账。

跨币种转账。

外汇兑换。

如果未来要更专业，外汇兑换可以单独拆出：

```text
fx_exchange_detail(
  transaction_id,
  quote_id,
  buy_currency,
  sell_currency,
  buy_amount,
  sell_amount,
  exchange_rate,
  spread
)
```

但当前可以先放在 `transfer_detail`。

---

## 22. 投资订单明细：`investment_order_detail`

这个替代之前单独的 `investment_transaction`。

```text
investment_order_detail(
  transaction_id PK/FK -> business_transaction.transaction_id,
  investment_account_id FK -> investment_account.account_id,
  product_id FK -> investment_product.product_id,
  order_side,
  quantity,
  price,
  gross_amount,
  fee_amount,
  currency_code FK -> currency.currency_code,
  trade_at,
  settlement_at
)
```

字段说明：

| 字段              | 说明                                 |
| --------------- | ---------------------------------- |
| `order_side`    | BUY / SELL / REDEMPTION / DIVIDEND |
| `quantity`      | 份额                                 |
| `price`         | 成交价格                               |
| `gross_amount`  | 交易总额                               |
| `settlement_at` | 结算时间                               |

买入产品时：

```text
business_transaction = BUY_PRODUCT
investment_order_detail = 产品、份额、价格
account_entry = 投资现金减少
investment_holding = 持仓增加
```

卖出产品时：

```text
business_transaction = SELL_PRODUCT
investment_order_detail = 产品、份额、价格
account_entry = 投资现金增加
investment_holding = 持仓减少
```

---

## 23. 账户业务明细：`account_operation_detail`

```text
account_operation_detail(
  transaction_id PK/FK -> business_transaction.transaction_id,
  account_id FK -> account.account_id,
  related_account_id FK -> account.account_id NULL,
  operation_reason NULL
)
```

用于：

开户。

销户。

冻结账户。

解冻账户。

开外汇账户并绑定储蓄账户。

开投资账户。

如果是开外汇账户：

```text
account_id = FX_ACCOUNT_ID
related_account_id = SAVINGS_ACCOUNT_ID
```

---

## 24. 卡业务明细：`card_operation_detail`

```text
card_operation_detail(
  transaction_id PK/FK -> business_transaction.transaction_id,
  card_kind,
  debit_card_id FK -> debit_card.card_id NULL,
  supplementary_debit_card_id FK -> supplementary_debit_card.supplementary_card_id NULL,
  credit_card_id FK -> credit_card.credit_card_id NULL,
  related_card_id NULL,
  savings_account_id FK -> savings_account.account_id NULL,
  fx_account_id FK -> fx_account.account_id NULL,
  credit_card_account_id FK -> credit_card_account.account_id NULL
)
```

`card_kind` 可以是：

```text
DEBIT
SUPPLEMENTARY_DEBIT
CREDIT
SUPPLEMENTARY_CREDIT
```

这个表可以记录：

开扣账卡。

开扣账附属卡。

开信用卡。

开信用卡附属卡。

换卡。

挂失。

冻结。

销卡。

严格来说，`related_card_id` 因为可能指向 debit card 或 credit card，会有多态外键问题。更规范的方式是拆成：

```text
debit_card_operation_detail
credit_card_operation_detail
```

但作业模型里，保留一个 `card_operation_detail` 也可以。若要追求数据库强约束，我建议拆成两张。

---

## 25. 信用卡业务明细：`credit_card_transaction_detail`

```text
credit_card_transaction_detail(
  transaction_id PK/FK -> business_transaction.transaction_id,
  credit_card_id FK -> credit_card.credit_card_id,
  credit_card_account_id FK -> credit_card_account.account_id,
  statement_id FK -> credit_card_statement.statement_id NULL,
  merchant_name NULL,
  merchant_category_code NULL,
  amount,
  currency_code FK -> currency.currency_code,
  authorization_code NULL,
  transaction_at,
  posted_at NULL
)
```

用于信用卡消费、退款、取现、年费、利息等。

信用卡还款可以用这张，也可以单独用 `credit_card_repayment`。我建议还款单独建，因为它有来源账户。

---

## 26. 信用卡还款明细：`credit_card_repayment`

```text
credit_card_repayment(
  transaction_id PK/FK -> business_transaction.transaction_id,
  credit_card_account_id FK -> credit_card_account.account_id,
  from_account_id FK -> account.account_id,
  statement_id FK -> credit_card_statement.statement_id NULL,
  repayment_amount,
  currency_code FK -> currency.currency_code,
  repayment_at
)
```

这个表把还款来源账户说清楚。

---

现在，把所有表整理成一个总览。

| 表名                               | 领域   | 作用                |
| -------------------------------- | ---- | ----------------- |
| `customer`                       | 客户   | 注册客户、身份、联系方式、税务信息 |
| `account`                        | 账户   | 所有账户的统一主表         |
| `savings_account`                | 账户   | 储蓄账户子类型           |
| `fx_account`                     | 账户   | 外汇账户子类型，绑定储蓄账户    |
| `investment_account`             | 账户   | 投资账户子类型           |
| `credit_card_account`            | 信用   | 信用卡账户，管理额度和账期     |
| `currency`                       | 字典   | 币种                |
| `account_balance`                | 余额   | 当前现金余额快照          |
| `account_entry`                  | 分录   | 资金变动历史            |
| `debit_card`                     | 卡    | 扣账卡主卡             |
| `supplementary_debit_card`       | 卡    | 扣账附属卡             |
| `credit_card`                    | 卡/信用 | 信用卡主卡和附属卡         |
| `credit_card_statement`          | 信用   | 信用卡账单             |
| `credit_card_transaction_detail` | 信用   | 信用卡消费、退款、费用等      |
| `credit_card_repayment`          | 信用   | 信用卡还款             |
| `investment_product`             | 投资   | 理财产品、基金、债券等       |
| `investment_holding`             | 投资   | 投资账户当前持仓          |
| `product_valuation`              | 投资   | 产品净值/估值历史         |
| `business_type`                  | 业务   | 业务码字典             |
| `business_transaction`           | 业务   | 统一业务流水            |
| `transfer_detail`                | 业务   | 转账/兑换明细           |
| `investment_order_detail`        | 业务   | 投资买卖明细            |
| `account_operation_detail`       | 业务   | 开户、销户、冻结等         |
| `card_operation_detail`          | 业务   | 开卡、挂失、换卡等         |

---

这里有几条关键业务约束，我们要明确写进设计说明。

第一，客户与账户：

```text
customer 1 ─── N account
```

一个客户至少有一个储蓄账户。

一个客户至少有一个外汇账户。

一个客户可以有 0 或 1 个投资账户。

一个客户可以有 0 或多个信用卡账户。
如果你想贴近题目风格，也可以限制为 0 或 1 个信用卡账户，但现实中多卡多账户很常见。

第二，外汇账户与储蓄账户：

```text
savings_account 1 ─── N fx_account
```

每个外汇账户必须绑定一个储蓄账户。

绑定的储蓄账户必须属于同一客户。

第三，扣账卡：

```text
customer 1 ─── N debit_card
debit_card N ─── 1 savings_account
debit_card N ─── 1 fx_account
debit_card 1 ─── 0..1 supplementary_debit_card
```

扣账卡持有人、储蓄账户所有人、外汇账户所有人应为同一客户。

第四，信用卡：

```text
customer 1 ─── N credit_card_account
credit_card_account 1 ─── N credit_card
credit_card 1 ─── N supplementary credit_card
```

如果用 `credit_card.card_role` 表示主副卡，那么：

```text
PRIMARY card: primary_credit_card_id = NULL
SUPPLEMENTARY card: primary_credit_card_id != NULL
```

附属信用卡持有人必须是注册客户。

第五，投资：

```text
investment_account 1 ─── N investment_holding
investment_product 1 ─── N investment_holding
investment_product 1 ─── N product_valuation
```

投资账户可以买多个产品。

同一产品可以被多个客户持有。

第六，业务交易：

```text
business_type 1 ─── N business_transaction
business_transaction 1 ─── 0..1 transfer_detail
business_transaction 1 ─── 0..1 investment_order_detail
business_transaction 1 ─── 0..1 account_operation_detail
business_transaction 1 ─── 0..1 card_operation_detail
business_transaction 1 ─── 0..1 credit_card_transaction_detail
business_transaction 1 ─── 0..1 credit_card_repayment
business_transaction 1 ─── N account_entry
```

不是所有业务都会产生 `account_entry`。例如开卡本身不一定影响余额。但所有影响资金的业务都应该产生分录。

---

我们还需要特别处理“至少一个储蓄账户”和“至少一个外汇账户”。

关系数据库的外键很容易表达“子表必须有父表”，但不容易直接表达“父表至少有一个子表”。

比如：

```text
每个 account 必须属于 customer
```

很好表达。

但：

```text
每个 customer 至少有一个 savings_account
```

不容易用普通外键表达。

实现方式有三种：

| 方法      | 说明                             |
| ------- | ------------------------------ |
| 应用层事务控制 | 创建客户时必须同时创建储蓄和外汇账户             |
| 数据库触发器  | 插入/提交时检查至少一个账户                 |
| 延迟约束    | 用 deferred constraint，但依赖数据库支持 |

作业里可以在设计说明写：

> 客户开户流程必须在同一事务中创建客户、至少一个储蓄账户、至少一个外汇账户；该约束由业务交易 `OPEN_CUSTOMER_PROFILE` 或应用层事务保证。

这就很合理。

---

现在，我们可以把最终模式用更紧凑的伪 DDL 表示出来。

```text
customer(
  customer_id PK,
  id_type,
  id_number,
  issuing_region,
  mobile_phone,
  residential_address,
  mailing_address,
  is_us_tax_resident,
  crs_info,
  customer_status,
  created_at,
  updated_at,
  UNIQUE(id_type, id_number, issuing_region)
)
```

```text
currency(
  currency_code PK,
  currency_name,
  minor_unit,
  is_active
)
```

```text
account(
  account_id PK,
  customer_id FK,
  account_no UNIQUE,
  account_type,
  account_status,
  opened_at,
  closed_at,
  UNIQUE(account_id, customer_id)
)
```

```text
savings_account(
  account_id PK/FK
)
```

```text
fx_account(
  account_id PK/FK,
  linked_savings_account_id FK
)
```

```text
investment_account(
  account_id PK/FK
)
```

```text
credit_card_account(
  account_id PK/FK,
  credit_limit,
  available_credit,
  billing_cycle_day,
  payment_due_day,
  interest_rate,
  cash_advance_limit,
  account_currency_code FK
)
```

```text
account_balance(
  account_id FK,
  currency_code FK,
  ledger_balance,
  available_balance,
  updated_at,
  PRIMARY KEY(account_id, currency_code)
)
```

```text
account_entry(
  entry_id PK,
  transaction_id FK,
  account_id FK,
  currency_code FK,
  entry_direction,
  amount,
  ledger_balance_after,
  available_balance_after,
  entry_type,
  posted_at
)
```

```text
debit_card(
  card_id PK,
  card_no UNIQUE,
  holder_customer_id FK,
  savings_account_id FK,
  fx_account_id FK,
  card_status,
  issued_at,
  expired_at
)
```

```text
supplementary_debit_card(
  supplementary_card_id PK,
  card_no UNIQUE,
  holder_customer_id FK,
  primary_debit_card_id FK,
  card_status,
  issued_at,
  expired_at,
  UNIQUE(primary_debit_card_id)
)
```

```text
credit_card(
  credit_card_id PK,
  card_no UNIQUE,
  holder_customer_id FK,
  credit_card_account_id FK,
  card_role,
  primary_credit_card_id FK NULL,
  card_status,
  issued_at,
  expired_at
)
```

```text
credit_card_statement(
  statement_id PK,
  credit_card_account_id FK,
  statement_period_start,
  statement_period_end,
  statement_date,
  payment_due_date,
  total_amount_due,
  minimum_amount_due,
  paid_amount,
  statement_status,
  currency_code FK
)
```

```text
investment_product(
  product_id PK,
  product_code UNIQUE,
  product_name,
  product_type,
  currency_code FK,
  risk_level,
  issuer,
  status
)
```

```text
investment_holding(
  holding_id PK,
  investment_account_id FK,
  product_id FK,
  quantity,
  average_cost,
  cost_currency_code FK,
  market_value,
  valuation_currency_code FK,
  unrealized_pnl,
  updated_at,
  UNIQUE(investment_account_id, product_id)
)
```

```text
product_valuation(
  product_id FK,
  valuation_date,
  nav,
  currency_code FK,
  PRIMARY KEY(product_id, valuation_date)
)
```

```text
business_type(
  business_type_code PK,
  business_category,
  business_name,
  description,
  is_financial,
  is_reversible,
  status
)
```

```text
business_transaction(
  transaction_id PK,
  business_type_code FK,
  initiator_customer_id FK NULL,
  operator_id NULL,
  channel,
  transaction_status,
  requested_at,
  completed_at NULL,
  reference_no UNIQUE,
  remarks NULL
)
```

```text
transfer_detail(
  transaction_id PK/FK,
  from_account_id FK,
  from_currency_code FK,
  to_account_id FK,
  to_currency_code FK,
  amount,
  exchange_rate,
  fee_amount,
  fee_currency_code FK NULL
)
```

```text
investment_order_detail(
  transaction_id PK/FK,
  investment_account_id FK,
  product_id FK,
  order_side,
  quantity,
  price,
  gross_amount,
  fee_amount,
  currency_code FK,
  trade_at,
  settlement_at
)
```

```text
account_operation_detail(
  transaction_id PK/FK,
  account_id FK,
  related_account_id FK NULL,
  operation_reason NULL
)
```

```text
card_operation_detail(
  transaction_id PK/FK,
  card_kind,
  debit_card_id FK NULL,
  supplementary_debit_card_id FK NULL,
  credit_card_id FK NULL,
  related_card_id NULL,
  savings_account_id FK NULL,
  fx_account_id FK NULL,
  credit_card_account_id FK NULL
)
```

```text
credit_card_transaction_detail(
  transaction_id PK/FK,
  credit_card_id FK,
  credit_card_account_id FK,
  statement_id FK NULL,
  merchant_name NULL,
  merchant_category_code NULL,
  amount,
  currency_code FK,
  authorization_code NULL,
  transaction_at,
  posted_at NULL
)
```

```text
credit_card_repayment(
  transaction_id PK/FK,
  credit_card_account_id FK,
  from_account_id FK,
  statement_id FK NULL,
  repayment_amount,
  currency_code FK,
  repayment_at
)
```

---

有一个我想特别强调的“架构脑”点：**不要因为 AI 能写代码，就低估模型复杂度。**

代码生成解决的是语法劳动，不解决语义污染。一个坏模型会让 AI 写出很多看起来完整、但行为互相冲突的代码。例如：

信用卡欠款到底是正数还是负数？

信用卡还款到底更新 `account_balance`，还是只更新 `statement`？

投资账户盈亏到底存在哪里？

产品交易和业务交易是不是同一件事？

开卡是交易还是卡状态变化？

转账是业务，还是账户余额两条 update？

这些不是代码问题，是建模问题。我们现在做的就是把这些语义边界提前钉死。钉死以后，AI 写 CRUD、DDL、migration、repository、service 就轻松很多。

---

我建议当前版本采用这几个核心设计原则：

第一，统一账户，不统一所有账户细节。

`account` 做超类型，`savings_account / fx_account / investment_account / credit_card_account` 做子类型。

第二，统一业务流水，不做万能交易大表。

`business_transaction` 收拢所有业务的公共字段，细节进入不同 detail 表。

第三，统一资金影响，不在业务表里直接改余额。

所有影响资金的业务都产生 `account_entry`，然后更新 `account_balance` 或信用卡账单。

第四，卡不是账户。

扣账卡和信用卡都是访问工具或支付工具。扣账卡绑定账户，信用卡绑定信用卡账户。

第五，产品不是余额。

投资产品进入 `investment_product`，客户买了什么进入 `investment_holding`，买卖动作进入 `investment_order_detail`。

第六，余额是快照，分录是历史。

`account_balance` 用来快速查当前状态；`account_entry` 用来审计和还原变化。

---

最终结论是：我们现在的完整模式可以称为一个简化银行核心模型，包含：

```text
客户
账户
余额
分录
扣账卡
信用卡
投资产品
持仓
估值
统一业务码
统一业务流水
业务明细
信用卡账单与还款
```
