```text
src/main/java/com/moesegfault/banking/presentation/
# 展示层。当前只保留 CLI，用作手动操作和业务流程演练入口。

└── cli/
    # 命令行界面实现。只处理输入、输出和命令分发，不写领域规则。
    # GUI 和 Web adapter 已移除；项目定位回到 core banking kata。

    ├── Main.java
    │   # CLI 进程入口。选择 one-shot 命令模式或交互式 shell 模式。

    ├── CliApplication.java
    │   # CLI 应用对象。组合 parser、registry 和 dispatcher。

    ├── CliShell.java
    │   # 交互式 shell。复用同一个 CLI runtime 连续执行命令。

    ├── CliHelpCatalog.java
    │   # CLI 帮助目录。集中维护命令用途、参数与示例。

    ├── CommandParser.java
    │   # 命令解析器。把原始字符串解析为 ParsedCommand。

    ├── ParsedCommand.java
    │   # 解析后的命令对象。包含 command path、参数 map、原始输入。

    ├── CommandDispatcher.java
    │   # 命令分发器。根据 ParsedCommand 找到对应 CliCommandHandler 并执行。

    ├── CliCommandHandler.java
    │   # CLI 命令处理器接口。所有具体 CLI handler 都实现它。

    ├── CommandRegistry.java
    │   # 命令注册表。维护 "customer register" -> handler 的映射。

    ├── bootstrap/
    │   # CLI 依赖装配和 runtime 生命周期管理。
    │
    │   ├── CliBootstrap.java
    │   │   # 装配配置、迁移、仓储、application handler 与 CLI 应用。
    │
    │   └── CliRuntime.java
    │       # 持有 CLI 应用和需要关闭的基础设施资源。

    ├── customer/
    │   # 客户相关 CLI 命令 handler。
    │
    │   ├── CustomerCliCommandRegistration.java
    │   │   # 注册 customer 命令族。
    │
    │   ├── RegisterCustomerCliHandler.java
    │   │   # 处理 customer register 命令。
    │
    │   ├── ShowCustomerCliHandler.java
    │   │   # 处理 customer show 命令。
    │
    │   └── ListCustomersCliHandler.java
    │       # 处理 customer list 命令。

    ├── account/
    │   # 账户相关 CLI 命令 handler、选项读取和输出格式化。

    ├── card/
    │   # 卡相关 CLI 命令 handler、选项读取和输出格式化。

    ├── credit/
    │   # 信用卡账户、账单、还款相关 CLI 命令 handler。

    ├── ledger/
    │   # 账务相关 CLI 命令 handler、选项读取和输出格式化。

    ├── business/
    │   # 业务流水查询相关 CLI 命令 handler。

    └── investment/
        # 投资产品、下单、持仓相关 CLI 命令 handler。
```
