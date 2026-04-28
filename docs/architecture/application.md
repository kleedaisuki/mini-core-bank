```text
src/main/java/com/moesegfault/banking/application/
# 应用层。负责编排一次完整业务操作，处理事务边界，调用领域对象和仓储接口。

├── customer/
│   # 客户应用服务。处理注册客户、查询客户等应用操作。

│   ├── command/
│   │   # 修改状态的客户命令。

│   │   ├── RegisterCustomerCommand.java
│   │   │   # 注册客户命令。包含证件、电话、地址、税务居民信息等输入。

│   │   └── RegisterCustomerHandler.java
│   │       # 注册客户处理器。检查唯一性，创建 Customer，保存并返回结果。

│   ├── query/
│   │   # 不修改状态的客户查询。

│   │   ├── FindCustomerQuery.java
│   │   │   # 查询单个客户的请求对象。

│   │   ├── FindCustomerHandler.java
│   │   │   # 查询单个客户的处理器。

│   │   ├── ListCustomersQuery.java
│   │   │   # 查询客户列表的请求对象。

│   │   └── ListCustomersHandler.java
│   │       # 查询客户列表的处理器。

│   └── result/
│       # 客户应用层返回对象。避免 presentation 直接暴露领域对象。

│       ├── CustomerResult.java
│       │   # 客户结果视图。包含客户 ID、证件摘要、手机号、状态等。

│       └── RegisterCustomerResult.java
│           # 注册客户结果。包含新客户 ID 和注册状态。

├── account/
│   # 账户应用服务。处理开户、账户查询、账户状态变更。

│   ├── command/

│   │   ├── OpenSavingsAccountCommand.java
│   │   │   # 开储蓄账户命令。包含客户 ID、户号、初始币种等输入。

│   │   ├── OpenSavingsAccountHandler.java
│   │   │   # 开储蓄账户处理器。创建 SavingsAccount 并初始化余额。

│   │   ├── OpenFxAccountCommand.java
│   │   │   # 开外汇账户命令。包含客户 ID、绑定储蓄账户 ID 等输入。

│   │   ├── OpenFxAccountHandler.java
│   │   │   # 开外汇账户处理器。验证绑定储蓄账户属于同一客户。

│   │   ├── OpenInvestmentAccountCommand.java
│   │   │   # 开投资账户命令。包含客户 ID 和户号。

│   │   ├── OpenInvestmentAccountHandler.java
│   │   │   # 开投资账户处理器。确保客户最多一个投资账户。

│   │   ├── FreezeAccountCommand.java
│   │   │   # 冻结账户命令。包含账户 ID 和冻结原因。

│   │   └── FreezeAccountHandler.java
│   │       # 冻结账户处理器。改变账户状态并记录业务流水。

│   ├── query/

│   │   ├── FindAccountQuery.java
│   │   │   # 查询单个账户。

│   │   ├── FindAccountHandler.java
│   │   │   # 查询单个账户处理器。

│   │   ├── ListCustomerAccountsQuery.java
│   │   │   # 查询某客户所有账户。

│   │   └── ListCustomerAccountsHandler.java
│   │       # 客户账户列表查询处理器。

│   └── result/
│       ├── AccountResult.java
│       │   # 账户结果视图。包含账户 ID、户号、类型、状态。

│       └── OpenAccountResult.java
│           # 开户结果。包含新账户 ID、户号、账户类型。
├── card/
│   # 卡应用服务。处理扣账卡、扣账附属卡、信用卡、信用卡附属卡。

│   ├── command/
│   │   ├── IssueDebitCardCommand.java
│   │   │   # 开扣账卡命令。包含持卡客户、储蓄账户、外汇账户。

│   │   ├── IssueDebitCardHandler.java
│   │   │   # 开扣账卡处理器。验证账户归属并创建 DebitCard。

│   │   ├── IssueSupplementaryDebitCardCommand.java
│   │   │   # 开扣账附属卡命令。包含副卡持有人和主扣账卡 ID。

│   │   ├── IssueSupplementaryDebitCardHandler.java
│   │   │   # 开扣账附属卡处理器。验证副卡持有人已注册。

│   │   ├── IssueCreditCardCommand.java
│   │   │   # 开信用卡命令。包含客户、信用额度、账单日等信息。

│   │   ├── IssueCreditCardHandler.java
│   │   │   # 开信用卡处理器。创建信用卡账户和主信用卡。

│   │   ├── IssueSupplementaryCreditCardCommand.java
│   │   │   # 开信用卡附属卡命令。包含副卡持有人和主信用卡 ID。

│   │   └── IssueSupplementaryCreditCardHandler.java
│   │       # 开信用卡附属卡处理器。验证主卡状态和副卡持有人。

│   ├── query/
│   │   ├── FindCardQuery.java
│   │   │   # 查询卡信息。

│   │   └── FindCardHandler.java
│   │       # 查询卡处理器。

│   └── result/
│       ├── DebitCardResult.java
│       │   # 扣账卡结果视图。

│       └── CreditCardResult.java
│           # 信用卡结果视图。

├── investment/
│   # 投资应用服务。处理产品创建、买入、卖出、持仓查询。

│   ├── command/
│   │   ├── CreateInvestmentProductCommand.java
│   │   │   # 创建投资产品命令。

│   │   ├── CreateInvestmentProductHandler.java
│   │   │   # 创建投资产品处理器。

│   │   ├── BuyProductCommand.java
│   │   │   # 买入产品命令。包含投资账户、产品、份额、价格。

│   │   ├── BuyProductHandler.java
│   │   │   # 买入产品处理器。创建订单、记账、更新持仓。

│   │   ├── SellProductCommand.java
│   │   │   # 卖出产品命令。

│   │   └── SellProductHandler.java
│   │       # 卖出产品处理器。减少持仓、生成现金入账。

│   ├── query/
│   │   ├── ListHoldingsQuery.java
│   │   │   # 查询投资账户持仓。

│   │   └── ListHoldingsHandler.java
│   │       # 持仓查询处理器。

│   └── result/
│       ├── InvestmentProductResult.java
│       │   # 投资产品结果视图。

│       └── HoldingResult.java
│           # 投资持仓结果视图。
├── credit/
│   # 信用应用服务。处理额度、账单、还款、信用卡入账。

│   ├── command/
│   │   ├── GenerateStatementCommand.java
│   │   │   # 生成信用卡账单命令。

│   │   ├── GenerateStatementHandler.java
│   │   │   # 生成账单处理器。聚合账期内交易，生成 Statement。

│   │   ├── RepayCreditCardCommand.java
│   │   │   # 信用卡还款命令。包含来源账户、信用卡账户、金额。

│   │   └── RepayCreditCardHandler.java
│   │       # 信用卡还款处理器。记账并更新账单还款状态。

│   ├── query/
│   │   ├── FindStatementQuery.java
│   │   │   # 查询信用卡账单。

│   │   └── FindStatementHandler.java
│   │       # 信用卡账单查询处理器。

│   └── result/
│       ├── CreditCardAccountResult.java
│       │   # 信用卡账户结果视图。

│       └── CreditCardStatementResult.java
│           # 信用卡账单结果视图。

├── ledger/
│   # 账务应用服务。处理入账、余额查询、分录查询。

│   ├── command/
│   │   ├── PostEntriesCommand.java
│   │   │   # 入账命令。包含业务参考号和若干记账请求。

│   │   └── PostEntriesHandler.java
│   │       # 入账处理器。创建分录并更新余额。

│   ├── query/
│   │   ├── FindBalanceQuery.java
│   │   │   # 查询账户余额。

│   │   ├── FindBalanceHandler.java
│   │   │   # 账户余额查询处理器。

│   │   ├── ListLedgerEntriesQuery.java
│   │   │   # 查询账户分录。

│   │   └── ListLedgerEntriesHandler.java
│   │       # 账户分录查询处理器。

│   └── result/
│       ├── BalanceResult.java
│       │   # 余额结果视图。

│       └── LedgerEntryResult.java
│           # 分录结果视图。

└── business/
    # 业务流水应用服务。处理业务码、业务交易、审计查询。

    ├── command/
    │   ├── StartBusinessTransactionCommand.java
    │   │   # 开始业务交易命令。用于创建统一业务流水。

    │   ├── StartBusinessTransactionHandler.java
    │   │   # 创建业务流水处理器。

    │   ├── CompleteBusinessTransactionCommand.java
    │   │   # 完成业务交易命令。

    │   └── CompleteBusinessTransactionHandler.java
    │       # 完成业务交易处理器。

    ├── query/
    │   ├── FindBusinessTransactionQuery.java
    │   │   # 查询单笔业务流水。

    │   ├── FindBusinessTransactionHandler.java
    │   │   # 查询单笔业务流水处理器。

    │   ├── ListBusinessTransactionsQuery.java
    │   │   # 查询业务流水列表。

    │   └── ListBusinessTransactionsHandler.java
    │       # 查询业务流水列表处理器。

    └── result/
        └── BusinessTransactionResult.java
            # 业务流水结果视图。
```
