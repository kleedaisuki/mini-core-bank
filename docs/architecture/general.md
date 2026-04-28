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
