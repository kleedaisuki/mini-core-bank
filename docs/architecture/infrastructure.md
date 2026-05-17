```text
src/main/java/com/moesegfault/banking/infrastructure/
# 基础设施层。负责配置、数据库迁移、Repository 实现、事务管理和 ID 生成。
# GUI runtime 和 Web runtime 已移除，当前没有桌面或 HTTP adapter。

├── config/
│   # 应用配置与资源加载。
│
│   ├── AppConfig.java
│   │   # 应用组装配置。创建 DataSource、Repository 和 ID 生成器。
│
│   ├── DatabaseConfig.java
│   │   # 数据库配置。读取 JDBC URL、用户名、密码，创建 DataSource。
│
│   └── PropertiesLoader.java
│       # 配置文件读取工具。合并 properties 和 JSON 默认配置。

├── migration/
│   # 数据库迁移。
│
│   └── FlywayMigrationRunner.java
│       # Flyway 迁移执行器。程序启动时执行数据库 schema 迁移。

├── persistence/
│   # 持久化实现。
│
│   ├── Repository.java
│   │   # Repository 门面。按接口类型暴露具体仓储实现。
│
│   ├── jdbc/
│   │   # JDBC 版 Repository 实现。
│
│   │   ├── JdbcRepository.java
│   │   │   # 聚合各子域 JDBC repository 的仓储门面。
│
│   │   ├── JdbcRepositorySupport.java
│   │   │   # JDBC 查询辅助逻辑。
│
│   │   ├── JdbcCustomerRepository.java
│   │   │   # CustomerRepository 的 JDBC 实现。
│
│   │   ├── JdbcAccountRepository.java
│   │   │   # AccountRepository 的 JDBC 实现。
│
│   │   ├── JdbcCardRepository.java
│   │   │   # CardRepository 的 JDBC 实现。
│
│   │   ├── JdbcCreditRepository.java
│   │   │   # CreditRepository 的 JDBC 实现。
│
│   │   ├── JdbcInvestmentRepository.java
│   │   │   # InvestmentRepository 的 JDBC 实现。
│
│   │   ├── JdbcLedgerRepository.java
│   │   │   # LedgerRepository 的 JDBC 实现。
│
│   │   └── JdbcBusinessRepository.java
│   │       # BusinessRepository 的 JDBC 实现。
│
│   ├── mapper/
│   │   # ResultSet 到领域对象的映射器。避免 Repository 方法太臃肿。
│
│   ├── sql/
│   │   # SQL 常量。避免 SQL 字符串散落各处。
│
│   └── transaction/
│       # 数据库事务管理。注意这里是 DB transaction，不是业务 transaction。
│
│       ├── DbTransactionManager.java
│       │   # 数据库事务管理接口。application 层可通过它包裹事务。
│
│       └── JdbcTransactionManager.java
│           # JDBC 事务管理实现。基于 Spring TransactionTemplate。

└── id/
    # ID 生成器。

    ├── IdGenerator.java
    │   # ID 生成接口。用于生成 CustomerId、AccountId、CardId 等。

    └── UuidIdGenerator.java
        # 基于 UUID 的 ID 生成实现。
```
