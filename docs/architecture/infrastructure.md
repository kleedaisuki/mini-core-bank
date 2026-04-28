```text
src/main/java/com/moesegfault/banking/infrastructure/
# 基础设施层。负责数据库、迁移、配置、Repository 实现、事务管理和 runtime 适配。

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

├── gui/
│   # GUI runtime 技术实现。提供 presentation/gui 中抽象接口的具体适配。
│   # 提供多 GUI 技术栈实现（例如 Swing、JavaFX），不承载选择策略。

│   ├── swing/
│   │   # 基于 Swing 的 GUI runtime 实现。

│   │   ├── SwingGuiRuntime.java
│   │   │   # GuiRuntime 的 Swing 实现。管理主窗口生命周期和页面挂载。

│   │   ├── SwingUiThreadScheduler.java
│   │   │   # UiThreadScheduler 的 Swing 实现。基于 EDT 调度 UI 任务。

│   │   ├── SwingGuiResourceLoader.java
│   │   │   # GuiResourceLoader 的 Swing 实现。加载主题、图标和文案资源。

│   │   └── view/
│   │       # Swing 组件适配。实现 presentation/gui/view 抽象接口。

│   │       ├── SwingMainWindowView.java
│   │       │   # MainWindowView 的 Swing 适配实现。

│   │       ├── SwingMainMenuView.java
│   │       │   # MainMenuView 的 Swing 适配实现。

│   │       ├── SwingStatusBarView.java
│   │       │   # StatusBarView 的 Swing 适配实现。

│   │       ├── SwingErrorDialogView.java
│   │       │   # ErrorDialogView 的 Swing 适配实现。

│   │       ├── SwingSuccessDialogView.java
│   │       │   # SuccessDialogView 的 Swing 适配实现。

│   │       ├── SwingConfirmDialogView.java
│   │       │   # ConfirmDialogView 的 Swing 适配实现。

│   │       ├── SwingTableView.java
│   │       │   # TableView 的 Swing 适配实现。

│   │       ├── SwingFormView.java
│   │       │   # FormView 的 Swing 适配实现。

│   │       └── SwingEmptyStateView.java
│   │           # EmptyStateView 的 Swing 适配实现。

│   └── javafx/
│       # 基于 JavaFX 的 GUI runtime 实现。

│       ├── JavaFxGuiRuntime.java
│       │   # GuiRuntime 的 JavaFX 实现。管理 stage 生命周期和页面挂载。

│       ├── JavaFxUiThreadScheduler.java
│       │   # UiThreadScheduler 的 JavaFX 实现。基于 Application Thread 调度。

│       ├── JavaFxGuiResourceLoader.java
│       │   # GuiResourceLoader 的 JavaFX 实现。加载样式、图标和文案资源。

│       └── view/
│           # JavaFX 组件适配。实现 presentation/gui/view 抽象接口。

│           ├── JavaFxMainWindowView.java
│           │   # MainWindowView 的 JavaFX 适配实现。

│           ├── JavaFxMainMenuView.java
│           │   # MainMenuView 的 JavaFX 适配实现。

│           ├── JavaFxStatusBarView.java
│           │   # StatusBarView 的 JavaFX 适配实现。

│           ├── JavaFxErrorDialogView.java
│           │   # ErrorDialogView 的 JavaFX 适配实现。

│           ├── JavaFxSuccessDialogView.java
│           │   # SuccessDialogView 的 JavaFX 适配实现。

│           ├── JavaFxConfirmDialogView.java
│           │   # ConfirmDialogView 的 JavaFX 适配实现。

│           ├── JavaFxTableView.java
│           │   # TableView 的 JavaFX 适配实现。

│           ├── JavaFxFormView.java
│           │   # FormView 的 JavaFX 适配实现。

│           └── JavaFxEmptyStateView.java
│               # EmptyStateView 的 JavaFX 适配实现。

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
