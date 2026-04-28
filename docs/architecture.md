```text
mini-core-bank/
├── pom.xml
│   # Maven 构建文件。声明 Java 17、PostgreSQL JDBC、Flyway、JUnit 等依赖。
│
├── README.md
│   # 项目说明。写项目目标、运行方式、领域边界、主要命令示例。
│
├── docs/
│   # 项目文档目录。放领域知识、架构决策、业务规则，不放代码实现细节。
│
│   ├── glossary.md
│   │   # 统一语言表。解释 Customer、Account、Ledger、Debit Card、Credit Card、Holding 等领域术语。
│
│   ├── domain-model.md
│   │   # 领域模型说明。描述实体、值对象、领域服务、主要业务规则。
│
│   ├── architecture.md
│   │   # 架构说明。解释四层架构、依赖方向、CLI 命令分发、数据库迁移策略。
│
│   ├── cli-commands.md
│   │   # CLI 命令手册。列出 customer register、account open-savings 等命令格式。
│
│   └── database-model.md
│       # 数据库模型说明。解释表结构、外键、唯一约束、迁移顺序。
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/moesegfault/banking/
    │   │       # 项目根包。
    │   │
    │   │       ├── Main.java
    │   │       │   # 程序入口。负责启动配置、运行 Flyway、组装 Repository/Application/CLI，并启动命令循环。
    │   │
    │   │       ├── presentation/
    │   │       │   # 展示层。负责和用户交互。当前提供 CLI，未来可以加 GUI。
    │   │       │
    │   │       │   ├── cli/
    │   │       │   │   # 命令行界面实现。只处理输入、输出和命令分发，不写领域规则。
    │   │       │   │
    │   │       │   │   ├── BankingCli.java
    │   │       │   │   │   # CLI 主循环。读取用户输入，调用 CommandParser 和 CommandDispatcher。
    │   │       │   │
    │   │       │   │   ├── CliSession.java
    │   │       │   │   │   # CLI 会话状态。保存当前输入输出流、是否退出、当前登录客户等临时状态。
    │   │       │   │
    │   │       │   │   ├── ConsolePrinter.java
    │   │       │   │   │   # 控制台输出工具。负责打印表格、错误信息、成功信息、帮助文本。
    │   │       │   │
    │   │       │   │   ├── CommandParser.java
    │   │       │   │   │   # 命令解析器。把原始字符串解析为 CliCommand，例如 "customer register --phone ..."。
    │   │       │   │
    │   │       │   │   ├── ParsedCommand.java
    │   │       │   │   │   # 解析后的命令对象。包含 command path、参数 map、原始输入。
    │   │       │   │
    │   │       │   │   ├── CommandDispatcher.java
    │   │       │   │   │   # 命令分发器。根据 ParsedCommand 找到对应的 CliCommandHandler 并执行。
    │   │       │   │
    │   │       │   │   ├── CliCommandHandler.java
    │   │       │   │   │   # CLI 命令处理器接口。所有具体 CLI handler 都实现它。
    │   │       │   │
    │   │       │   │   ├── CommandRegistry.java
    │   │       │   │   │   # 命令注册表。维护 "customer register" -> handler 的映射。
    │   │       │   │
    │   │       │   │   ├── CliExceptionHandler.java
    │   │       │   │   │   # CLI 异常处理器。把领域异常、应用异常、系统异常转换成用户可读消息。
    │   │       │   │
    │   │       │   │   ├── HelpCommandHandler.java
    │   │       │   │   │   # help 命令。列出所有可用命令和参数说明。
    │   │       │   │
    │   │       │   │   ├── ExitCommandHandler.java
    │   │       │   │   │   # exit / quit 命令。结束 CLI 会话。
    │   │       │   │
    │   │       │   │   ├── customer/
    │   │       │   │   │   # 客户相关 CLI 命令 handler。
    │   │       │   │   │
    │   │       │   │   │   ├── RegisterCustomerCliHandler.java
    │   │       │   │   │   │   # 处理 customer register 命令，并调用 RegisterCustomerHandler。
    │   │       │   │   │
    │   │       │   │   │   ├── ShowCustomerCliHandler.java
    │   │       │   │   │   │   # 处理 customer show 命令，查询并展示客户信息。
    │   │       │   │   │
    │   │       │   │   │   └── ListCustomersCliHandler.java
    │   │       │   │   │       # 处理 customer list 命令，展示客户列表。
    │   │       │   │   │
    │   │       │   │   ├── account/
    │   │       │   │   │   # 账户相关 CLI 命令 handler。
    │   │       │   │   │
    │   │       │   │   │   ├── OpenSavingsAccountCliHandler.java
    │   │       │   │   │   │   # 处理 account open-savings 命令。
    │   │       │   │   │
    │   │       │   │   │   ├── OpenFxAccountCliHandler.java
    │   │       │   │   │   │   # 处理 account open-fx 命令。
    │   │       │   │   │
    │   │       │   │   │   ├── OpenInvestmentAccountCliHandler.java
    │   │       │   │   │   │   # 处理 account open-investment 命令。
    │   │       │   │   │
    │   │       │   │   │   ├── ShowAccountCliHandler.java
    │   │       │   │   │   │   # 处理 account show 命令。
    │   │       │   │   │
    │   │       │   │   │   └── ListAccountsCliHandler.java
    │   │       │   │   │       # 处理 account list 命令。
    │   │       │   │   │
    │   │       │   │   ├── card/
    │   │       │   │   │   # 卡相关 CLI 命令 handler。
    │   │       │   │   │
    │   │       │   │   │   ├── IssueDebitCardCliHandler.java
    │   │       │   │   │   │   # 处理 card issue-debit 命令。
    │   │       │   │   │
    │   │       │   │   │   ├── IssueSupplementaryDebitCardCliHandler.java
    │   │       │   │   │   │   # 处理 card issue-supplementary-debit 命令。
    │   │       │   │   │
    │   │       │   │   │   ├── IssueCreditCardCliHandler.java
    │   │       │   │   │   │   # 处理 card issue-credit 命令。
    │   │       │   │   │
    │   │       │   │   │   ├── IssueSupplementaryCreditCardCliHandler.java
    │   │       │   │   │   │   # 处理 card issue-supplementary-credit 命令。
    │   │       │   │   │
    │   │       │   │   │   └── ShowCardCliHandler.java
    │   │       │   │   │       # 处理 card show 命令。
    │   │       │   │   │
    │   │       │   │   ├── investment/
    │   │       │   │   │   # 投资相关 CLI 命令 handler。
    │   │       │   │   │
    │   │       │   │   │   ├── CreateProductCliHandler.java
    │   │       │   │   │   │   # 处理 investment product-create 命令。
    │   │       │   │   │
    │   │       │   │   │   ├── BuyProductCliHandler.java
    │   │       │   │   │   │   # 处理 investment buy 命令。
    │   │       │   │   │
    │   │       │   │   │   ├── SellProductCliHandler.java
    │   │       │   │   │   │   # 处理 investment sell 命令。
    │   │       │   │   │
    │   │       │   │   │   └── ShowHoldingCliHandler.java
    │   │       │   │   │       # 处理 investment holdings 命令。
    │   │       │   │   │
    │   │       │   │   ├── credit/
    │   │       │   │   │   # 信用卡账户、账单、还款相关 CLI 命令 handler。
    │   │       │   │   │
    │   │       │   │   │   ├── GenerateStatementCliHandler.java
    │   │       │   │   │   │   # 处理 credit generate-statement 命令。
    │   │       │   │   │
    │   │       │   │   │   ├── RepayCreditCardCliHandler.java
    │   │       │   │   │   │   # 处理 credit repay 命令。
    │   │       │   │   │
    │   │       │   │   │   └── ShowStatementCliHandler.java
    │   │       │   │   │       # 处理 credit statement 命令。
    │   │       │   │   │
    │   │       │   │   ├── ledger/
    │   │       │   │   │   # 账务相关 CLI 命令 handler。
    │   │       │   │   │
    │   │       │   │   │   ├── ShowBalanceCliHandler.java
    │   │       │   │   │   │   # 处理 ledger balance 命令。
    │   │       │   │   │
    │   │       │   │   │   └── ShowEntriesCliHandler.java
    │   │       │   │   │       # 处理 ledger entries 命令。
    │   │       │   │   │
    │   │       │   │   └── business/
    │   │       │   │       # 业务流水相关 CLI 命令 handler。
    │   │       │   │
    │   │       │   │       ├── ShowBusinessTransactionCliHandler.java
    │   │       │   │       │   # 处理 business show 命令。
    │   │       │   │
    │   │       │   │       └── ListBusinessTransactionsCliHandler.java
    │   │       │   │           # 处理 business list 命令。
    │   │       │   │
    │   │       │   └── gui/
    │   │       │       # GUI 展示层预留。后续如果课程要求图形界面，可以在这里接入 JavaFX 或 Swing。
    │   │       │
    │   │       │       └── README.md
    │   │       │           # 说明 GUI 目前预留，尚未实现。
    │   │       ├── application/
    │   │       │   # 应用层。负责编排一次完整业务操作，处理事务边界，调用领域对象和仓储接口。
    │   │       │
    │   │       │   ├── customer/
    │   │       │   │   # 客户应用服务。处理注册客户、查询客户等应用操作。
    │   │       │   │
    │   │       │   │   ├── command/
    │   │       │   │   │   # 修改状态的客户命令。
    │   │       │   │   │
    │   │       │   │   │   ├── RegisterCustomerCommand.java
    │   │       │   │   │   │   # 注册客户命令。包含证件、电话、地址、税务居民信息等输入。
    │   │       │   │   │
    │   │       │   │   │   └── RegisterCustomerHandler.java
    │   │       │   │   │       # 注册客户处理器。检查唯一性，创建 Customer，保存并返回结果。
    │   │       │   │   │
    │   │       │   │   ├── query/
    │   │       │   │   │   # 不修改状态的客户查询。
    │   │       │   │   │
    │   │       │   │   │   ├── FindCustomerQuery.java
    │   │       │   │   │   │   # 查询单个客户的请求对象。
    │   │       │   │   │
    │   │       │   │   │   ├── FindCustomerHandler.java
    │   │       │   │   │   │   # 查询单个客户的处理器。
    │   │       │   │   │
    │   │       │   │   │   ├── ListCustomersQuery.java
    │   │       │   │   │   │   # 查询客户列表的请求对象。
    │   │       │   │   │
    │   │       │   │   │   └── ListCustomersHandler.java
    │   │       │   │   │       # 查询客户列表的处理器。
    │   │       │   │   │
    │   │       │   │   └── result/
    │   │       │   │       # 客户应用层返回对象。避免 presentation 直接暴露领域对象。
    │   │       │   │
    │   │       │   │       ├── CustomerResult.java
    │   │       │   │       │   # 客户结果视图。包含客户 ID、证件摘要、手机号、状态等。
    │   │       │   │
    │   │       │   │       └── RegisterCustomerResult.java
    │   │       │   │           # 注册客户结果。包含新客户 ID 和注册状态。
    │   │       │   │
    │   │       │   ├── account/
    │   │       │   │   # 账户应用服务。处理开户、账户查询、账户状态变更。
    │   │       │   │
    │   │       │   │   ├── command/
    │   │       │   │   │
    │   │       │   │   │   ├── OpenSavingsAccountCommand.java
    │   │       │   │   │   │   # 开储蓄账户命令。包含客户 ID、户号、初始币种等输入。
    │   │       │   │   │
    │   │       │   │   │   ├── OpenSavingsAccountHandler.java
    │   │       │   │   │   │   # 开储蓄账户处理器。创建 SavingsAccount 并初始化余额。
    │   │       │   │   │
    │   │       │   │   │   ├── OpenFxAccountCommand.java
    │   │       │   │   │   │   # 开外汇账户命令。包含客户 ID、绑定储蓄账户 ID 等输入。
    │   │       │   │   │
    │   │       │   │   │   ├── OpenFxAccountHandler.java
    │   │       │   │   │   │   # 开外汇账户处理器。验证绑定储蓄账户属于同一客户。
    │   │       │   │   │
    │   │       │   │   │   ├── OpenInvestmentAccountCommand.java
    │   │       │   │   │   │   # 开投资账户命令。包含客户 ID 和户号。
    │   │       │   │   │
    │   │       │   │   │   ├── OpenInvestmentAccountHandler.java
    │   │       │   │   │   │   # 开投资账户处理器。确保客户最多一个投资账户。
    │   │       │   │   │
    │   │       │   │   │   ├── FreezeAccountCommand.java
    │   │       │   │   │   │   # 冻结账户命令。包含账户 ID 和冻结原因。
    │   │       │   │   │
    │   │       │   │   │   └── FreezeAccountHandler.java
    │   │       │   │   │       # 冻结账户处理器。改变账户状态并记录业务流水。
    │   │       │   │   │
    │   │       │   │   ├── query/
    │   │       │   │   │
    │   │       │   │   │   ├── FindAccountQuery.java
    │   │       │   │   │   │   # 查询单个账户。
    │   │       │   │   │
    │   │       │   │   │   ├── FindAccountHandler.java
    │   │       │   │   │   │   # 查询单个账户处理器。
    │   │       │   │   │
    │   │       │   │   │   ├── ListCustomerAccountsQuery.java
    │   │       │   │   │   │   # 查询某客户所有账户。
    │   │       │   │   │
    │   │       │   │   │   └── ListCustomerAccountsHandler.java
    │   │       │   │   │       # 客户账户列表查询处理器。
    │   │       │   │   │
    │   │       │   │   └── result/
    │   │       │   │       ├── AccountResult.java
    │   │       │   │       │   # 账户结果视图。包含账户 ID、户号、类型、状态。
    │   │       │   │
    │   │       │   │       └── OpenAccountResult.java
    │   │       │   │           # 开户结果。包含新账户 ID、户号、账户类型。
    │   │       │   ├── card/
    │   │       │   │   # 卡应用服务。处理扣账卡、扣账附属卡、信用卡、信用卡附属卡。
    │   │       │   │
    │   │       │   │   ├── command/
    │   │       │   │   │   ├── IssueDebitCardCommand.java
    │   │       │   │   │   │   # 开扣账卡命令。包含持卡客户、储蓄账户、外汇账户。
    │   │       │   │   │
    │   │       │   │   │   ├── IssueDebitCardHandler.java
    │   │       │   │   │   │   # 开扣账卡处理器。验证账户归属并创建 DebitCard。
    │   │       │   │   │
    │   │       │   │   │   ├── IssueSupplementaryDebitCardCommand.java
    │   │       │   │   │   │   # 开扣账附属卡命令。包含副卡持有人和主扣账卡 ID。
    │   │       │   │   │
    │   │       │   │   │   ├── IssueSupplementaryDebitCardHandler.java
    │   │       │   │   │   │   # 开扣账附属卡处理器。验证副卡持有人已注册。
    │   │       │   │   │
    │   │       │   │   │   ├── IssueCreditCardCommand.java
    │   │       │   │   │   │   # 开信用卡命令。包含客户、信用额度、账单日等信息。
    │   │       │   │   │
    │   │       │   │   │   ├── IssueCreditCardHandler.java
    │   │       │   │   │   │   # 开信用卡处理器。创建信用卡账户和主信用卡。
    │   │       │   │   │
    │   │       │   │   │   ├── IssueSupplementaryCreditCardCommand.java
    │   │       │   │   │   │   # 开信用卡附属卡命令。包含副卡持有人和主信用卡 ID。
    │   │       │   │   │
    │   │       │   │   │   └── IssueSupplementaryCreditCardHandler.java
    │   │       │   │   │       # 开信用卡附属卡处理器。验证主卡状态和副卡持有人。
    │   │       │   │   │
    │   │       │   │   ├── query/
    │   │       │   │   │   ├── FindCardQuery.java
    │   │       │   │   │   │   # 查询卡信息。
    │   │       │   │   │
    │   │       │   │   │   └── FindCardHandler.java
    │   │       │   │   │       # 查询卡处理器。
    │   │       │   │   │
    │   │       │   │   └── result/
    │   │       │   │       ├── DebitCardResult.java
    │   │       │   │       │   # 扣账卡结果视图。
    │   │       │   │
    │   │       │   │       └── CreditCardResult.java
    │   │       │   │           # 信用卡结果视图。
    │   │       │   │
    │   │       │   ├── investment/
    │   │       │   │   # 投资应用服务。处理产品创建、买入、卖出、持仓查询。
    │   │       │   │
    │   │       │   │   ├── command/
    │   │       │   │   │   ├── CreateInvestmentProductCommand.java
    │   │       │   │   │   │   # 创建投资产品命令。
    │   │       │   │   │
    │   │       │   │   │   ├── CreateInvestmentProductHandler.java
    │   │       │   │   │   │   # 创建投资产品处理器。
    │   │       │   │   │
    │   │       │   │   │   ├── BuyProductCommand.java
    │   │       │   │   │   │   # 买入产品命令。包含投资账户、产品、份额、价格。
    │   │       │   │   │
    │   │       │   │   │   ├── BuyProductHandler.java
    │   │       │   │   │   │   # 买入产品处理器。创建订单、记账、更新持仓。
    │   │       │   │   │
    │   │       │   │   │   ├── SellProductCommand.java
    │   │       │   │   │   │   # 卖出产品命令。
    │   │       │   │   │
    │   │       │   │   │   └── SellProductHandler.java
    │   │       │   │   │       # 卖出产品处理器。减少持仓、生成现金入账。
    │   │       │   │   │
    │   │       │   │   ├── query/
    │   │       │   │   │   ├── ListHoldingsQuery.java
    │   │       │   │   │   │   # 查询投资账户持仓。
    │   │       │   │   │
    │   │       │   │   │   └── ListHoldingsHandler.java
    │   │       │   │   │       # 持仓查询处理器。
    │   │       │   │   │
    │   │       │   │   └── result/
    │   │       │   │       ├── InvestmentProductResult.java
    │   │       │   │       │   # 投资产品结果视图。
    │   │       │   │
    │   │       │   │       └── HoldingResult.java
    │   │       │   │           # 投资持仓结果视图。
    │   │       │   ├── credit/
    │   │       │   │   # 信用应用服务。处理额度、账单、还款、信用卡入账。
    │   │       │   │
    │   │       │   │   ├── command/
    │   │       │   │   │   ├── GenerateStatementCommand.java
    │   │       │   │   │   │   # 生成信用卡账单命令。
    │   │       │   │   │
    │   │       │   │   │   ├── GenerateStatementHandler.java
    │   │       │   │   │   │   # 生成账单处理器。聚合账期内交易，生成 Statement。
    │   │       │   │   │
    │   │       │   │   │   ├── RepayCreditCardCommand.java
    │   │       │   │   │   │   # 信用卡还款命令。包含来源账户、信用卡账户、金额。
    │   │       │   │   │
    │   │       │   │   │   └── RepayCreditCardHandler.java
    │   │       │   │   │       # 信用卡还款处理器。记账并更新账单还款状态。
    │   │       │   │   │
    │   │       │   │   ├── query/
    │   │       │   │   │   ├── FindStatementQuery.java
    │   │       │   │   │   │   # 查询信用卡账单。
    │   │       │   │   │
    │   │       │   │   │   └── FindStatementHandler.java
    │   │       │   │   │       # 信用卡账单查询处理器。
    │   │       │   │   │
    │   │       │   │   └── result/
    │   │       │   │       ├── CreditCardAccountResult.java
    │   │       │   │       │   # 信用卡账户结果视图。
    │   │       │   │
    │   │       │   │       └── CreditCardStatementResult.java
    │   │       │   │           # 信用卡账单结果视图。
    │   │       │   │
    │   │       │   ├── ledger/
    │   │       │   │   # 账务应用服务。处理入账、余额查询、分录查询。
    │   │       │   │
    │   │       │   │   ├── command/
    │   │       │   │   │   ├── PostEntriesCommand.java
    │   │       │   │   │   │   # 入账命令。包含业务参考号和若干记账请求。
    │   │       │   │   │
    │   │       │   │   │   └── PostEntriesHandler.java
    │   │       │   │   │       # 入账处理器。创建分录并更新余额。
    │   │       │   │   │
    │   │       │   │   ├── query/
    │   │       │   │   │   ├── FindBalanceQuery.java
    │   │       │   │   │   │   # 查询账户余额。
    │   │       │   │   │
    │   │       │   │   │   ├── FindBalanceHandler.java
    │   │       │   │   │   │   # 账户余额查询处理器。
    │   │       │   │   │
    │   │       │   │   │   ├── ListLedgerEntriesQuery.java
    │   │       │   │   │   │   # 查询账户分录。
    │   │       │   │   │
    │   │       │   │   │   └── ListLedgerEntriesHandler.java
    │   │       │   │   │       # 账户分录查询处理器。
    │   │       │   │   │
    │   │       │   │   └── result/
    │   │       │   │       ├── BalanceResult.java
    │   │       │   │       │   # 余额结果视图。
    │   │       │   │
    │   │       │   │       └── LedgerEntryResult.java
    │   │       │   │           # 分录结果视图。
    │   │       │   │
    │   │       │   └── business/
    │   │       │       # 业务流水应用服务。处理业务码、业务交易、审计查询。
    │   │       │
    │   │       │       ├── command/
    │   │       │       │   ├── StartBusinessTransactionCommand.java
    │   │       │       │   │   # 开始业务交易命令。用于创建统一业务流水。
    │   │       │       │
    │   │       │       │   ├── StartBusinessTransactionHandler.java
    │   │       │       │   │   # 创建业务流水处理器。
    │   │       │       │
    │   │       │       │   ├── CompleteBusinessTransactionCommand.java
    │   │       │       │   │   # 完成业务交易命令。
    │   │       │       │
    │   │       │       │   └── CompleteBusinessTransactionHandler.java
    │   │       │       │       # 完成业务交易处理器。
    │   │       │       │
    │   │       │       ├── query/
    │   │       │       │   ├── FindBusinessTransactionQuery.java
    │   │       │       │   │   # 查询单笔业务流水。
    │   │       │       │
    │   │       │       │   ├── FindBusinessTransactionHandler.java
    │   │       │       │   │   # 查询单笔业务流水处理器。
    │   │       │       │
    │   │       │       │   ├── ListBusinessTransactionsQuery.java
    │   │       │       │   │   # 查询业务流水列表。
    │   │       │       │
    │   │       │       │   └── ListBusinessTransactionsHandler.java
    │   │       │       │       # 查询业务流水列表处理器。
    │   │       │       │
    │   │       │       └── result/
    │   │       │           └── BusinessTransactionResult.java
    │   │       │               # 业务流水结果视图。
    │   │       ├── domain/
    │   │       │   # 领域层。保存业务概念、业务规则和值对象。不得依赖 application、presentation、infrastructure。
    │   │       │
    │   │       │   ├── shared/
    │   │       │   │   # 真正跨领域共享的基础概念。必须克制，不能变成垃圾桶。
    │   │       │   │
    │   │       │   │   ├── EntityId.java
    │   │       │   │   │   # 通用实体 ID 基类或接口。可选，用于统一 ID 行为。
    │   │       │   │
    │   │       │   │   ├── Money.java
    │   │       │   │   │   # 金额值对象。封装 BigDecimal 和 CurrencyCode，禁止 double 存钱。
    │   │       │   │
    │   │       │   │   ├── CurrencyCode.java
    │   │       │   │   │   # 币种代码值对象，例如 USD、CNH、EUR、JPY。
    │   │       │   │
    │   │       │   │   ├── Percentage.java
    │   │       │   │   │   # 百分比值对象。用于利率、手续费率、收益率。
    │   │       │   │
    │   │       │   │   ├── DateRange.java
    │   │       │   │   │   # 日期区间值对象。用于账单周期、统计周期。
    │   │       │   │
    │   │       │   │   ├── DomainException.java
    │   │       │   │   │   # 领域异常基类。表示业务规则被违反。
    │   │       │   │
    │   │       │   │   ├── BusinessRuleViolation.java
    │   │       │   │   │   # 业务规则违反异常。用于表达不满足领域不变量。
    │   │       │   │
    │   │       │   │   ├── DomainEvent.java
    │   │       │   │   │   # 领域事件接口。后续可用于记录 CustomerRegistered、AccountOpened 等事件。
    │   │       │   │
    │   │       │   │   └── DomainEventPublisher.java
    │   │       │   │       # 领域事件发布接口。当前可以先不用，预留扩展点。
    │   │       │   ├── customer/
    │   │       │   │   # 客户领域。负责客户身份、联系方式、税务信息、客户状态。
    │   │       │   │
    │   │       │   │   ├── Customer.java
    │   │       │   │   │   # 客户实体。表示银行注册客户，包含身份信息、地址、税务信息、状态。
    │   │       │   │
    │   │       │   │   ├── CustomerId.java
    │   │       │   │   │   # 客户 ID 值对象。封装内部客户编号。
    │   │       │   │
    │   │       │   │   ├── CustomerStatus.java
    │   │       │   │   │   # 客户状态枚举，例如 ACTIVE、FROZEN、CLOSED。
    │   │       │   │
    │   │       │   │   ├── IdentityDocument.java
    │   │       │   │   │   # 证件值对象。包含证件类型、证件号码、签发地区。
    │   │       │   │
    │   │       │   │   ├── IdentityDocumentType.java
    │   │       │   │   │   # 证件类型枚举，例如 ID_CARD、PASSPORT、HKID。
    │   │       │   │
    │   │       │   │   ├── PhoneNumber.java
    │   │       │   │   │   # 电话号码值对象。封装流动电话格式和基本校验。
    │   │       │   │
    │   │       │   │   ├── Address.java
    │   │       │   │   │   # 地址值对象。用于居住地址和通信地址。
    │   │       │   │
    │   │       │   │   ├── TaxProfile.java
    │   │       │   │   │   # 税务资料值对象。包含是否美国税务居民和 CRS 信息。
    │   │       │   │
    │   │       │   │   ├── CrsInfo.java
    │   │       │   │   │   # CRS 信息值对象。CRS 是 Common Reporting Standard，通用报告准则。
    │   │       │   │
    │   │       │   │   ├── CustomerPolicy.java
    │   │       │   │   │   # 客户领域策略。放客户能否开户、能否持卡等规则。
    │   │       │   │
    │   │       │   │   ├── CustomerRepository.java
    │   │       │   │   │   # 客户仓储接口。定义保存、查询客户的方法，由 infrastructure 实现。
    │   │       │   │
    │   │       │   │   └── CustomerRegistered.java
    │   │       │   │       # 客户注册领域事件。表示一个客户已经完成注册。
    │   │       │   ├── account/
    │   │       │   │   # 账户领域。负责储蓄账户、外汇账户、投资账户、账户状态和账户关系。
    │   │       │   │
    │   │       │   │   ├── Account.java
    │   │       │   │   │   # 账户抽象实体或基础类。表示共有的账户身份、户号、客户归属、状态。
    │   │       │   │
    │   │       │   │   ├── AccountId.java
    │   │       │   │   │   # 账户 ID 值对象。
    │   │       │   │
    │   │       │   │   ├── AccountNumber.java
    │   │       │   │   │   # 户号值对象。封装账户号码格式和唯一性语义。
    │   │       │   │
    │   │       │   │   ├── AccountType.java
    │   │       │   │   │   # 账户类型枚举，例如 SAVINGS、FX、INVESTMENT、CREDIT_CARD。
    │   │       │   │
    │   │       │   │   ├── AccountStatus.java
    │   │       │   │   │   # 账户状态枚举，例如 ACTIVE、FROZEN、CLOSED。
    │   │       │   │
    │   │       │   │   ├── SavingsAccount.java
    │   │       │   │   │   # 储蓄账户实体。表示客户的基础存款账户。
    │   │       │   │
    │   │       │   │   ├── FxAccount.java
    │   │       │   │   │   # 外汇账户实体。必须绑定一个储蓄账户，可持有多币种余额。
    │   │       │   │
    │   │       │   │   ├── InvestmentAccount.java
    │   │       │   │   │   # 投资账户实体。表示客户用于购买理财产品的账户容器。
    │   │       │   │
    │   │       │   │   ├── FxAccountLink.java
    │   │       │   │   │   # 外汇账户与储蓄账户的绑定关系值对象。
    │   │       │   │
    │   │       │   │   ├── AccountOwnershipPolicy.java
    │   │       │   │   │   # 账户归属策略。验证账户是否属于指定客户。
    │   │       │   │
    │   │       │   │   ├── AccountOpeningPolicy.java
    │   │       │   │   │   # 开户策略。检查客户是否允许开某类账户，以及投资账户数量限制。
    │   │       │   │
    │   │       │   │   ├── AccountRepository.java
    │   │       │   │   │   # 账户仓储接口。保存和查询储蓄、外汇、投资账户。
    │   │       │   │
    │   │       │   │   ├── SavingsAccountOpened.java
    │   │       │   │   │   # 储蓄账户已开立领域事件。
    │   │       │   │
    │   │       │   │   ├── FxAccountOpened.java
    │   │       │   │   │   # 外汇账户已开立领域事件。
    │   │       │   │
    │   │       │   │   └── InvestmentAccountOpened.java
    │   │       │   │       # 投资账户已开立领域事件。
    │   │       │   ├── card/
    │   │       │   │   # 卡领域。负责扣账卡、扣账附属卡、信用卡卡片和卡状态。
    │   │       │   │
    │   │       │   │   ├── CardId.java
    │   │       │   │   │   # 卡 ID 值对象。
    │   │       │   │
    │   │       │   │   ├── CardNumber.java
    │   │       │   │   │   # 卡号值对象。封装卡号格式和脱敏显示。
    │   │       │   │
    │   │       │   │   ├── CardStatus.java
    │   │       │   │   │   # 卡状态枚举，例如 ACTIVE、FROZEN、LOST、CANCELLED、EXPIRED。
    │   │       │   │
    │   │       │   │   ├── CardExpiry.java
    │   │       │   │   │   # 卡有效期值对象。
    │   │       │   │
    │   │       │   │   ├── DebitCard.java
    │   │       │   │   │   # 扣账卡实体。绑定一个储蓄账户和一个外汇账户。
    │   │       │   │
    │   │       │   │   ├── SupplementaryDebitCard.java
    │   │       │   │   │   # 扣账附属卡实体。绑定主扣账卡，持有人必须是注册客户。
    │   │       │   │
    │   │       │   │   ├── CreditCard.java
    │   │       │   │   │   # 信用卡卡片实体。表示主信用卡或附属信用卡。
    │   │       │   │
    │   │       │   │   ├── CardRole.java
    │   │       │   │   │   # 卡角色枚举，例如 PRIMARY、SUPPLEMENTARY。
    │   │       │   │
    │   │       │   │   ├── DebitCardBinding.java
    │   │       │   │   │   # 扣账卡绑定值对象。保存储蓄账户 ID 和外汇账户 ID。
    │   │       │   │
    │   │       │   │   ├── SupplementaryCardPolicy.java
    │   │       │   │   │   # 附属卡策略。检查副卡持有人和主卡关系是否合法。
    │   │       │   │
    │   │       │   │   ├── CardIssuingPolicy.java
    │   │       │   │   │   # 发卡策略。检查客户状态、账户绑定、卡数量限制。
    │   │       │   │
    │   │       │   │   ├── CardRepository.java
    │   │       │   │   │   # 卡仓储接口。保存和查询扣账卡、信用卡。
    │   │       │   │
    │   │       │   │   ├── DebitCardIssued.java
    │   │       │   │   │   # 扣账卡已发出领域事件。
    │   │       │   │
    │   │       │   │   ├── SupplementaryDebitCardIssued.java
    │   │       │   │   │   # 扣账附属卡已发出领域事件。
    │   │       │   │
    │   │       │   │   ├── CreditCardIssued.java
    │   │       │   │   │   # 信用卡已发出领域事件。
    │   │       │   │
    │   │       │   │   └── SupplementaryCreditCardIssued.java
    │   │       │   │       # 信用卡附属卡已发出领域事件。
    │   │       │   ├── credit/
    │   │       │   │   # 信用领域。负责信用卡账户、额度、账单、还款、逾期。
    │   │       │   │
    │   │       │   │   ├── CreditCardAccount.java
    │   │       │   │   │   # 信用卡账户实体。管理信用额度、可用额度、账单日、还款日。
    │   │       │   │
    │   │       │   │   ├── CreditCardAccountId.java
    │   │       │   │   │   # 信用卡账户 ID 值对象。可复用 AccountId，也可单独建模。
    │   │       │   │
    │   │       │   │   ├── CreditLimit.java
    │   │       │   │   │   # 信用额度值对象。包含总额度、可用额度、预借现金额度。
    │   │       │   │
    │   │       │   │   ├── BillingCycle.java
    │   │       │   │   │   # 账单周期值对象。包含账单日、还款日、周期范围。
    │   │       │   │
    │   │       │   │   ├── InterestRate.java
    │   │       │   │   │   # 利率值对象。用于信用卡利息计算。
    │   │       │   │
    │   │       │   │   ├── CreditCardStatement.java
    │   │       │   │   │   # 信用卡账单实体。表示一个账期的应还、最低还款、已还状态。
    │   │       │   │
    │   │       │   │   ├── StatementId.java
    │   │       │   │   │   # 账单 ID 值对象。
    │   │       │   │
    │   │       │   │   ├── StatementStatus.java
    │   │       │   │   │   # 账单状态枚举，例如 OPEN、PAID、OVERDUE、CLOSED。
    │   │       │   │
    │   │       │   │   ├── MinimumPaymentPolicy.java
    │   │       │   │   │   # 最低还款策略。根据账单金额计算最低还款额。
    │   │       │   │
    │   │       │   │   ├── RepaymentAllocationPolicy.java
    │   │       │   │   │   # 还款分配策略。决定还款如何抵扣账单、利息、费用、本金。
    │   │       │   │
    │   │       │   │   ├── CreditRepository.java
    │   │       │   │   │   # 信用领域仓储接口。保存信用账户、账单、还款记录。
    │   │       │   │
    │   │       │   │   ├── CreditCardAccountOpened.java
    │   │       │   │   │   # 信用卡账户已开立领域事件。
    │   │       │   │
    │   │       │   │   ├── CreditCardStatementGenerated.java
    │   │       │   │   │   # 信用卡账单已生成领域事件。
    │   │       │   │
    │   │       │   │   └── CreditCardRepaymentReceived.java
    │   │       │   │       # 信用卡还款已收到领域事件。
    │   │       │   ├── investment/
    │   │       │   │   # 投资领域。负责投资产品、订单、持仓、估值、盈亏。
    │   │       │   │
    │   │       │   │   ├── InvestmentProduct.java
    │   │       │   │   │   # 投资产品实体。表示理财产品、基金、债券等可购买产品。
    │   │       │   │
    │   │       │   │   ├── ProductId.java
    │   │       │   │   │   # 产品 ID 值对象。
    │   │       │   │
    │   │       │   │   ├── ProductCode.java
    │   │       │   │   │   # 产品代码值对象。
    │   │       │   │
    │   │       │   │   ├── ProductType.java
    │   │       │   │   │   # 产品类型枚举，例如 FUND、BOND、STRUCTURED_PRODUCT、WEALTH_MANAGEMENT。
    │   │       │   │
    │   │       │   │   ├── ProductStatus.java
    │   │       │   │   │   # 产品状态枚举，例如 LISTED、SUSPENDED、CLOSED。
    │   │       │   │
    │   │       │   │   ├── RiskLevel.java
    │   │       │   │   │   # 风险等级值对象或枚举。用于产品风险和客户适配检查。
    │   │       │   │
    │   │       │   │   ├── InvestmentOrder.java
    │   │       │   │   │   # 投资订单实体。表示买入、卖出、赎回、分红等投资交易。
    │   │       │   │
    │   │       │   │   ├── InvestmentOrderId.java
    │   │       │   │   │   # 投资订单 ID 值对象。
    │   │       │   │
    │   │       │   │   ├── OrderSide.java
    │   │       │   │   │   # 订单方向枚举，例如 BUY、SELL、REDEMPTION、DIVIDEND。
    │   │       │   │
    │   │       │   │   ├── OrderStatus.java
    │   │       │   │   │   # 订单状态枚举，例如 PLACED、SETTLED、FAILED、CANCELLED。
    │   │       │   │
    │   │       │   │   ├── Holding.java
    │   │       │   │   │   # 投资持仓实体。表示投资账户持有某产品的份额、成本、市值。
    │   │       │   │
    │   │       │   │   ├── HoldingId.java
    │   │       │   │   │   # 持仓 ID 值对象。
    │   │       │   │
    │   │       │   │   ├── Quantity.java
    │   │       │   │   │   # 份额值对象。用于投资产品份额，禁止负数。
    │   │       │   │
    │   │       │   │   ├── NetAssetValue.java
    │   │       │   │   │   # 产品净值值对象。NAV，Net Asset Value。
    │   │       │   │
    │   │       │   │   ├── ProductValuation.java
    │   │       │   │   │   # 产品估值实体或值对象。表示某产品某日期的净值。
    │   │       │   │
    │   │       │   │   ├── SuitabilityPolicy.java
    │   │       │   │   │   # 适当性策略。检查客户风险承受能力是否匹配产品风险。
    │   │       │   │
    │   │       │   │   ├── HoldingPolicy.java
    │   │       │   │   │   # 持仓策略。检查持仓是否足够卖出、份额是否非负。
    │   │       │   │
    │   │       │   │   ├── InvestmentRepository.java
    │   │       │   │   │   # 投资领域仓储接口。保存产品、订单、持仓、估值。
    │   │       │   │
    │   │       │   │   ├── InvestmentProductCreated.java
    │   │       │   │   │   # 投资产品已创建领域事件。
    │   │       │   │
    │   │       │   │   ├── InvestmentOrderPlaced.java
    │   │       │   │   │   # 投资订单已提交领域事件。
    │   │       │   │
    │   │       │   │   ├── InvestmentOrderSettled.java
    │   │       │   │   │   # 投资订单已结算领域事件。
    │   │       │   │
    │   │       │   │   └── HoldingChanged.java
    │   │       │   │       # 投资持仓已变化领域事件。
    │   │       │   ├── ledger/
    │   │       │   │   # 账务领域。负责余额、分录、入账批次和不可变记账规则。
    │   │       │   │
    │   │       │   │   ├── Balance.java
    │   │       │   │   │   # 余额实体或状态对象。表示某账户某币种的账面余额和可用余额。
    │   │       │   │
    │   │       │   │   ├── LedgerEntry.java
    │   │       │   │   │   # 账务分录实体。表示一次业务对某账户某币种造成的资金变化。
    │   │       │   │
    │   │       │   │   ├── LedgerEntryId.java
    │   │       │   │   │   # 账务分录 ID 值对象。
    │   │       │   │
    │   │       │   │   ├── PostingBatch.java
    │   │       │   │   │   # 入账批次实体。一次业务可能产生多条分录，批次用于统一提交。
    │   │       │   │
    │   │       │   │   ├── PostingBatchId.java
    │   │       │   │   │   # 入账批次 ID 值对象。
    │   │       │   │
    │   │       │   │   ├── EntryDirection.java
    │   │       │   │   │   # 分录方向枚举，例如 INCREASE、DECREASE，或 DEBIT、CREDIT。
    │   │       │   │
    │   │       │   │   ├── EntryType.java
    │   │       │   │   │   # 分录类型枚举，例如 PRINCIPAL、FEE、INTEREST、DIVIDEND、REPAYMENT。
    │   │       │   │
    │   │       │   │   ├── PostingStatus.java
    │   │       │   │   │   # 入账状态枚举，例如 PENDING、POSTED、REVERSED。
    │   │       │   │
    │   │       │   │   ├── BalancePolicy.java
    │   │       │   │   │   # 余额策略。检查是否允许扣款、可用余额是否充足。
    │   │       │   │
    │   │       │   │   ├── PostingPolicy.java
    │   │       │   │   │   # 入账策略。确保入账幂等、分录不可变、已入账批次不可修改。
    │   │       │   │
    │   │       │   │   ├── LedgerRepository.java
    │   │       │   │   │   # 账务仓储接口。保存余额、分录、入账批次。
    │   │       │   │
    │   │       │   │   ├── LedgerEntryPosted.java
    │   │       │   │   │   # 账务分录已入账领域事件。
    │   │       │   │
    │   │       │   │   ├── BalanceUpdated.java
    │   │       │   │   │   # 余额已更新领域事件。
    │   │       │   │
    │   │       │   │   └── PostingReversed.java
    │   │       │   │       # 入账已冲正领域事件。
    │   │       │   ├── business/
    │   │       │   │   # 业务流水领域。负责业务码、业务交易、业务状态、审计参考号。
    │   │       │   │
    │   │       │   │   ├── BusinessTransaction.java
    │   │       │   │   │   # 业务交易实体。表示一次业务操作，例如开户、开卡、转账、买产品。
    │   │       │   │
    │   │       │   │   ├── BusinessTransactionId.java
    │   │       │   │   │   # 业务交易 ID 值对象。
    │   │       │   │
    │   │       │   │   ├── BusinessReference.java
    │   │       │   │   │   # 业务参考号值对象。用于幂等和外部查询。
    │   │       │   │
    │   │       │   │   ├── BusinessType.java
    │   │       │   │   │   # 业务类型实体或枚举。表示业务码和业务分类。
    │   │       │   │
    │   │       │   │   ├── BusinessTypeCode.java
    │   │       │   │   │   # 业务码值对象，例如 OPEN_SAVINGS_ACCOUNT、BUY_PRODUCT。
    │   │       │   │
    │   │       │   │   ├── BusinessCategory.java
    │   │       │   │   │   # 业务分类枚举，例如 ACCOUNT、CARD、INVESTMENT、CREDIT、LEDGER。
    │   │       │   │
    │   │       │   │   ├── BusinessTransactionStatus.java
    │   │       │   │   │   # 业务交易状态枚举，例如 PENDING、SUCCESS、FAILED、REVERSED。
    │   │       │   │
    │   │       │   │   ├── BusinessChannel.java
    │   │       │   │   │   # 业务渠道枚举，例如 CLI、BRANCH、ATM、MOBILE、SYSTEM。
    │   │       │   │
    │   │       │   │   ├── BusinessTransactionPolicy.java
    │   │       │   │   │   # 业务流水策略。检查业务能否完成、能否冲正、幂等性语义。
    │   │       │   │
    │   │       │   │   ├── BusinessRepository.java
    │   │       │   │   │   # 业务流水仓储接口。保存业务类型和业务交易。
    │   │       │   │
    │   │       │   │   ├── BusinessTransactionStarted.java
    │   │       │   │   │   # 业务交易已开始领域事件。
    │   │       │   │
    │   │       │   │   ├── BusinessTransactionCompleted.java
    │   │       │   │   │   # 业务交易已完成领域事件。
    │   │       │   │
    │   │       │   │   └── BusinessTransactionFailed.java
    │   │       │   │       # 业务交易失败领域事件。
    │   │       └── infrastructure/
    │   │           # 基础设施层。负责数据库、迁移、配置、Repository 实现和事务管理。
    │   │
    │   │           ├── config/
    │   │           │   # 应用配置与依赖组装。
    │   │           │
    │   │           │   ├── AppConfig.java
    │   │           │   │   # 应用组装配置。创建 Repository、Handler、CLI dispatcher 等对象。
    │   │           │
    │   │           │   ├── DatabaseConfig.java
    │   │           │   │   # 数据库配置。读取 JDBC URL、用户名、密码，创建 DataSource。
    │   │           │
    │   │           │   └── PropertiesLoader.java
    │   │           │       # 配置文件读取工具。加载 application.properties。
    │   │           │
    │   │           ├── migration/
    │   │           │   # 数据库迁移。
    │   │           │
    │   │           │   └── FlywayMigrationRunner.java
    │   │           │       # Flyway 迁移执行器。程序启动时执行数据库 schema 迁移。
    │   │           │
    │   │           ├── persistence/
    │   │           │   # 持久化实现。
    │   │           │
    │   │           │   ├── jdbc/
    │   │           │   │   # JDBC 版 Repository 实现。所有 SQL 先集中在这里。
    │   │           │   │
    │   │           │   │   ├── JdbcCustomerRepository.java
    │   │           │   │   │   # CustomerRepository 的 JDBC 实现。
    │   │           │   │
    │   │           │   │   ├── JdbcAccountRepository.java
    │   │           │   │   │   # AccountRepository 的 JDBC 实现。
    │   │           │   │
    │   │           │   │   ├── JdbcCardRepository.java
    │   │           │   │   │   # CardRepository 的 JDBC 实现。
    │   │           │   │
    │   │           │   │   ├── JdbcCreditRepository.java
    │   │           │   │   │   # CreditRepository 的 JDBC 实现。
    │   │           │   │
    │   │           │   │   ├── JdbcInvestmentRepository.java
    │   │           │   │   │   # InvestmentRepository 的 JDBC 实现。
    │   │           │   │
    │   │           │   │   ├── JdbcLedgerRepository.java
    │   │           │   │   │   # LedgerRepository 的 JDBC 实现。
    │   │           │   │
    │   │           │   │   └── JdbcBusinessRepository.java
    │   │           │   │       # BusinessRepository 的 JDBC 实现。
    │   │           │   │
    │   │           │   ├── mapper/
    │   │           │   │   # ResultSet 到领域对象的映射器。避免 Repository 方法太臃肿。
    │   │           │   │
    │   │           │   │   ├── CustomerRowMapper.java
    │   │           │   │   │   # 把 customer 表查询结果映射为 Customer。
    │   │           │   │
    │   │           │   │   ├── AccountRowMapper.java
    │   │           │   │   │   # 把 account 相关表查询结果映射为账户对象。
    │   │           │   │
    │   │           │   │   ├── CardRowMapper.java
    │   │           │   │   │   # 把 card 相关表查询结果映射为卡对象。
    │   │           │   │
    │   │           │   │   ├── CreditRowMapper.java
    │   │           │   │   │   # 把 credit 相关表查询结果映射为信用对象。
    │   │           │   │
    │   │           │   │   ├── InvestmentRowMapper.java
    │   │           │   │   │   # 把 investment 相关表查询结果映射为投资对象。
    │   │           │   │
    │   │           │   │   ├── LedgerRowMapper.java
    │   │           │   │   │   # 把 ledger 相关表查询结果映射为账务对象。
    │   │           │   │
    │   │           │   │   └── BusinessRowMapper.java
    │   │           │   │       # 把 business_transaction 表查询结果映射为 BusinessTransaction。
    │   │           │   │
    │   │           │   ├── sql/
    │   │           │   │   # SQL 常量或 SQL 构造辅助。避免 SQL 字符串散落各处。
    │   │           │   │
    │   │           │   │   ├── CustomerSql.java
    │   │           │   │   │   # 客户相关 SQL。
    │   │           │   │
    │   │           │   │   ├── AccountSql.java
    │   │           │   │   │   # 账户相关 SQL。
    │   │           │   │
    │   │           │   │   ├── CardSql.java
    │   │           │   │   │   # 卡相关 SQL。
    │   │           │   │
    │   │           │   │   ├── CreditSql.java
    │   │           │   │   │   # 信用相关 SQL。
    │   │           │   │
    │   │           │   │   ├── InvestmentSql.java
    │   │           │   │   │   # 投资相关 SQL。
    │   │           │   │
    │   │           │   │   ├── LedgerSql.java
    │   │           │   │   │   # 账务相关 SQL。
    │   │           │   │
    │   │           │   │   └── BusinessSql.java
    │   │           │   │       # 业务流水相关 SQL。
    │   │           │   │
    │   │           │   └── transaction/
    │   │           │       # 数据库事务管理。注意这里是 DB transaction，不是业务 transaction。
    │   │           │
    │   │           │       ├── DbTransactionManager.java
    │   │           │       │   # 数据库事务管理接口。application 层可通过它包裹事务。
    │   │           │
    │   │           │       └── JdbcTransactionManager.java
    │   │           │           # JDBC 事务管理实现。控制 commit、rollback 和 Connection 绑定。
    │   │           │
    │   │           └── id/
    │   │               # ID 生成器。
    │   │
    │   │               ├── IdGenerator.java
    │   │               │   # ID 生成接口。用于生成 CustomerId、AccountId、CardId 等。
    │   │
    │   │               └── UuidIdGenerator.java
    │   │                   # 基于 UUID 的 ID 生成实现。
    │   └── resources/
    │       # 应用资源文件。
    │
    │       ├── application.properties
    │       │   # 应用配置。包含 JDBC URL、数据库用户名、密码、CLI 配置等。
    │
    │       └── db/
    │           └── migration/
    │               # Flyway migration 文件目录。
    │
    │               ├── V1__create_customer_schema.sql
    │               │   # 创建客户相关表，例如 customer。
    │
    │               ├── V2__create_account_schema.sql
    │               │   # 创建账户相关表，例如 account、savings_account、fx_account、investment_account。
    │
    │               ├── V3__create_card_schema.sql
    │               │   # 创建卡相关表，例如 debit_card、supplementary_debit_card、credit_card。
    │
    │               ├── V4__create_credit_schema.sql
    │               │   # 创建信用相关表，例如 credit_card_account、credit_card_statement。
    │
    │               ├── V5__create_investment_schema.sql
    │               │   # 创建投资相关表，例如 investment_product、investment_holding、product_valuation。
    │
    │               ├── V6__create_business_schema.sql
    │               │   # 创建业务流水相关表，例如 business_type、business_transaction。
    │
    │               ├── V7__create_ledger_schema.sql
    │               │   # 创建账务相关表，例如 account_balance、account_entry、posting_batch。
    │
    │               └── V8__seed_reference_data.sql
    │                   # 初始化基础字典数据，例如币种、业务码、产品类型。
    └── test/
        ├── java/
        │   └── com/moesegfault/banking/
        │       # 测试代码。
        │
        │       ├── domain/
        │       │   # 领域层单元测试。只测业务规则，不连数据库。
        │       │
        │       │   ├── customer/
        │       │   │   └── CustomerTest.java
        │       │   │       # 测试客户实体、证件值对象、客户状态规则。
        │       │
        │       │   ├── account/
        │       │   │   └── AccountTest.java
        │       │   │       # 测试账户状态、外汇账户绑定规则。
        │       │
        │       │   ├── card/
        │       │   │   └── CardTest.java
        │       │   │       # 测试发卡规则、附属卡规则。
        │       │
        │       │   ├── credit/
        │       │   │   └── CreditCardAccountTest.java
        │       │   │       # 测试信用额度、账单状态、还款规则。
        │       │
        │       │   ├── investment/
        │       │   │   └── InvestmentHoldingTest.java
        │       │   │       # 测试持仓份额、买卖规则、盈亏计算。
        │       │
        │       │   └── ledger/
        │       │       └── LedgerPostingTest.java
        │       │           # 测试分录入账、余额更新、幂等规则。
        │       │
        │       ├── application/
        │       │   # 应用层测试。测试 handler 编排逻辑，可以用 fake repository。
        │       │
        │       │   ├── customer/
        │       │   │   └── RegisterCustomerHandlerTest.java
        │       │   │       # 测试注册客户应用流程。
        │       │
        │       │   ├── account/
        │       │   │   └── OpenFxAccountHandlerTest.java
        │       │   │       # 测试开外汇账户应用流程。
        │       │
        │       │   └── card/
        │       │       └── IssueDebitCardHandlerTest.java
        │       │           # 测试开扣账卡应用流程。
        │       │
        │       ├── infrastructure/
        │       │   # 基础设施层测试。测试 JDBC repository 和 Flyway migration。
        │       │
        │       │   ├── FlywayMigrationRunnerTest.java
        │       │   │   # 测试数据库 migration 能否成功执行。
        │       │
        │       │   └── JdbcCustomerRepositoryTest.java
        │       │       # 测试客户仓储的数据库读写。
        │       │
        │       └── presentation/
        │           # CLI 测试。测试命令解析和命令分发。
        │
        │           └── cli/
        │               ├── CommandParserTest.java
        │               │   # 测试 CLI 原始输入能否正确解析。
        │
        │               └── CommandDispatcherTest.java
        │                   # 测试命令能否分发到正确 handler。
        │
        └── resources/
            └── test-application.properties
                # 测试环境配置。可以使用测试数据库连接。
```


```text
本项目采用 Maven 单模块结构和经典 DDD 四层架构。

presentation 层负责用户交互。当前实现 CLI，采用 CommandParser + CommandDispatcher 模式，将原始命令解析并分发给具体 CLI handler。未来可以扩展 GUI。

application 层负责应用编排。采用 Command / Handler / Query / Result 命名，不使用 UseCase 命名。Application Handler 负责事务边界、仓储调用和领域对象协作。

domain 层负责核心领域模型。包含实体、值对象、领域策略、仓储接口、领域事件和领域异常。domain 层不依赖任何外层。

infrastructure 层负责技术细节。包含 PostgreSQL JDBC Repository、Flyway migration、配置加载、数据库事务管理和 ID 生成。
```
