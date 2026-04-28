```text
src/main/java/com/moesegfault/banking/infrastructure/
# 基础设施层。负责数据库、迁移、配置、Repository 实现和事务管理。

├── config/
│   # 应用配置与依赖组装。

│   ├── AppConfig.java
│   │   # 应用组装配置。创建 Repository、Handler、CLI dispatcher 等对象。

│   ├── DatabaseConfig.java
│   │   # 数据库配置。读取 JDBC URL、用户名、密码，创建 DataSource。

│   └── PropertiesLoader.java
│       # 配置文件读取工具。加载 application.properties。

├── migration/
│   # 数据库迁移。

│   └── FlywayMigrationRunner.java
│       # Flyway 迁移执行器。程序启动时执行数据库 schema 迁移。

├── persistence/
│   # 持久化实现。

│   ├── Repository.java
│   │   # Repository 的 interface。

│   ├── jdbc/
│   │   # JDBC 版 Repository 实现。所有 SQL 先集中在这里。

│   │   ├── JdbcCustomerRepository.java
│   │   │   # CustomerRepository 的 JDBC 实现。

│   │   ├── JdbcAccountRepository.java
│   │   │   # AccountRepository 的 JDBC 实现。

│   │   ├── JdbcCardRepository.java
│   │   │   # CardRepository 的 JDBC 实现。

│   │   ├── JdbcCreditRepository.java
│   │   │   # CreditRepository 的 JDBC 实现。

│   │   ├── JdbcInvestmentRepository.java
│   │   │   # InvestmentRepository 的 JDBC 实现。

│   │   ├── JdbcLedgerRepository.java
│   │   │   # LedgerRepository 的 JDBC 实现。

│   │   └── JdbcBusinessRepository.java
│   │       # BusinessRepository 的 JDBC 实现。

│   ├── mapper/
│   │   # ResultSet 到领域对象的映射器。避免 Repository 方法太臃肿。
│   │   ├── RowMapper.java
│   │   │   # RowMapper interface。

│   │   ├── CustomerRowMapper.java
│   │   │   # 把 customer 表查询结果映射为 Customer。

│   │   ├── AccountRowMapper.java
│   │   │   # 把 account 相关表查询结果映射为账户对象。

│   │   ├── CardRowMapper.java
│   │   │   # 把 card 相关表查询结果映射为卡对象。

│   │   ├── CreditRowMapper.java
│   │   │   # 把 credit 相关表查询结果映射为信用对象。

│   │   ├── InvestmentRowMapper.java
│   │   │   # 把 investment 相关表查询结果映射为投资对象。

│   │   ├── LedgerRowMapper.java
│   │   │   # 把 ledger 相关表查询结果映射为账务对象。

│   │   └── BusinessRowMapper.java
│   │       # 把 business_transaction 表查询结果映射为 BusinessTransaction。

│   ├── sql/
│   │   # SQL 常量或 SQL 构造辅助。避免 SQL 字符串散落各处。

│   │   ├── CustomerSql.java
│   │   │   # 客户相关 SQL。

│   │   ├── AccountSql.java
│   │   │   # 账户相关 SQL。

│   │   ├── CardSql.java
│   │   │   # 卡相关 SQL。

│   │   ├── CreditSql.java
│   │   │   # 信用相关 SQL。

│   │   ├── InvestmentSql.java
│   │   │   # 投资相关 SQL。

│   │   ├── LedgerSql.java
│   │   │   # 账务相关 SQL。

│   │   └── BusinessSql.java
│   │       # 业务流水相关 SQL。

│   └── transaction/
│       # 数据库事务管理。注意这里是 DB transaction，不是业务 transaction。

│       ├── DbTransactionManager.java
│       │   # 数据库事务管理接口。application 层可通过它包裹事务。

│       └── JdbcTransactionManager.java
│           # JDBC 事务管理实现。控制 commit、rollback 和 Connection 绑定。

├── web/
│   # Web runtime 技术实现。提供 presentation/web/WebRuntime 的具体实现。

│   └── jdk/
│      # 基于 JDK HttpServer 的 Web runtime 实现。

│       ├── JdkHttpWebRuntime.java
│       │   # WebRuntime 的 JDK HttpServer 实现。负责注册 route 并启动服务。

│       ├── JdkWebRequest.java
│       │   # WebRequest 的 JDK HttpExchange 适配实现。

│       ├── JdkWebResponseWriter.java
│       │   # 把 WebResponse 写回 JDK HttpExchange。

│       ├── JdkPathPattern.java
│       │   # JDK runtime 使用的路径匹配工具，支持 /customers/{customerId}。

│       └── JdkHttpRuntimeException.java
│              # JDK HttpServer runtime 异常。
└── id/
    # ID 生成器。

    ├── IdGenerator.java
    │   # ID 生成接口。用于生成 CustomerId、AccountId、CardId 等。

    └── UuidIdGenerator.java
        # 基于 UUID 的 ID 生成实现。
```
