```text
src/main/java/com/moesegfault/banking/presentation/
# 展示层。负责和用户交互。

├── cli/
│   # 命令行界面实现。只处理输入、输出和命令分发，不写领域规则。

│   ├── BankingCli.java
│   │   # CLI 主循环。读取用户输入，调用 CommandParser 和 CommandDispatcher。

│   ├── CliSession.java
│   │   # CLI 会话状态。保存当前输入输出流、是否退出、当前登录客户等临时状态。

│   ├── ConsolePrinter.java
│   │   # 控制台输出工具。负责打印表格、错误信息、成功信息、帮助文本。

│   ├── CommandParser.java
│   │   # 命令解析器。把原始字符串解析为 CliCommand，例如 "customer register --phone ..."。

│   ├── ParsedCommand.java
│   │   # 解析后的命令对象。包含 command path、参数 map、原始输入。

│   ├── CommandDispatcher.java
│   │   # 命令分发器。根据 ParsedCommand 找到对应的 CliCommandHandler 并执行。

│   ├── CliCommandHandler.java
│   │   # CLI 命令处理器接口。所有具体 CLI handler 都实现它。

│   ├── CommandRegistry.java
│   │   # 命令注册表。维护 "customer register" -> handler 的映射。

│   ├── CliExceptionHandler.java
│   │   # CLI 异常处理器。把领域异常、应用异常、系统异常转换成用户可读消息。

│   ├── HelpCommandHandler.java
│   │   # help 命令。列出所有可用命令和参数说明。

│   ├── ExitCommandHandler.java
│   │   # exit / quit 命令。结束 CLI 会话。

│   ├── customer/
│   │   # 客户相关 CLI 命令 handler。

│   │   ├── RegisterCustomerCliHandler.java
│   │   │   # 处理 customer register 命令，并调用 RegisterCustomerHandler。

│   │   ├── ShowCustomerCliHandler.java
│   │   │   # 处理 customer show 命令，查询并展示客户信息。

│   │   └── ListCustomersCliHandler.java
│   │       # 处理 customer list 命令，展示客户列表。

│   ├── account/
│   │   # 账户相关 CLI 命令 handler。

│   │   ├── OpenSavingsAccountCliHandler.java
│   │   │   # 处理 account open-savings 命令。

│   │   ├── OpenFxAccountCliHandler.java
│   │   │   # 处理 account open-fx 命令。

│   │   ├── OpenInvestmentAccountCliHandler.java
│   │   │   # 处理 account open-investment 命令。

│   │   ├── ShowAccountCliHandler.java
│   │   │   # 处理 account show 命令。

│   │   └── ListAccountsCliHandler.java
│   │       # 处理 account list 命令。

│   ├── card/
│   │   # 卡相关 CLI 命令 handler。

│   │   ├── IssueDebitCardCliHandler.java
│   │   │   # 处理 card issue-debit 命令。

│   │   ├── IssueSupplementaryDebitCardCliHandler.java
│   │   │   # 处理 card issue-supplementary-debit 命令。

│   │   ├── IssueCreditCardCliHandler.java
│   │   │   # 处理 card issue-credit 命令。

│   │   ├── IssueSupplementaryCreditCardCliHandler.java
│   │   │   # 处理 card issue-supplementary-credit 命令。

│   │   └── ShowCardCliHandler.java
│   │       # 处理 card show 命令。

│   ├── investment/
│   │   # 投资相关 CLI 命令 handler。

│   │   ├── CreateProductCliHandler.java
│   │   │   # 处理 investment product-create 命令。

│   │   ├── BuyProductCliHandler.java
│   │   │   # 处理 investment buy 命令。

│   │   ├── SellProductCliHandler.java
│   │   │   # 处理 investment sell 命令。

│   │   └── ShowHoldingCliHandler.java
│   │       # 处理 investment holdings 命令。

│   ├── credit/
│   │   # 信用卡账户、账单、还款相关 CLI 命令 handler。

│   │   ├── GenerateStatementCliHandler.java
│   │   │   # 处理 credit generate-statement 命令。

│   │   ├── RepayCreditCardCliHandler.java
│   │   │   # 处理 credit repay 命令。

│   │   └── ShowStatementCliHandler.java
│   │       # 处理 credit statement 命令。

│   ├── ledger/
│   │   # 账务相关 CLI 命令 handler。

│   │   ├── ShowBalanceCliHandler.java
│   │   │   # 处理 ledger balance 命令。

│   │   └── ShowEntriesCliHandler.java
│   │       # 处理 ledger entries 命令。

│   └── business/
│       # 业务流水相关 CLI 命令 handler。

│       ├── ShowBusinessTransactionCliHandler.java
│       │   # 处理 business show 命令。

│       └── ListBusinessTransactionsCliHandler.java
│           # 处理 business list 命令。

├── presentation/
# 展示层。负责和用户交互。当前提供 CLI，未来可以加 GUI / Web。

├── gui/
│   # 图形界面入口。采用简化经典 MVC。
│   # Model 是 GUI 页面状态，不是 domain model。
│   # Controller 处理用户输入并更新 Model。
│   # View 观察 Model 并渲染。

│   ├── BankingGui.java
│   │   # GUI 启动入口。初始化依赖、主窗口、导航器，并启动第一个页面。

│   ├── GuiApplication.java
│   │   # GUI 应用对象。管理 GUI 生命周期、页面注册、主窗口和全局上下文。

│   ├── GuiContext.java
│   │   # GUI 会话上下文。保存当前登录客户、当前页面、locale、主题等 UI 会话状态。

│   ├── GuiNavigator.java
│   │   # 页面导航器。负责在不同 GUI 页面之间切换。

│   ├── GuiPage.java
│   │   # 一个完整 GUI 页面组合。封装 model、view、controller 三件套。

│   ├── GuiPageId.java
│   │   # GUI 页面标识。例如 customer.register、account.show、ledger.entries。

│   ├── GuiPageRegistry.java
│   │   # GUI 页面注册表。维护 GuiPageId -> GuiPageFactory 的映射。

│   ├── GuiPageFactory.java
│   │   # GUI 页面工厂接口。负责创建某个页面的 model、view、controller。

│   ├── GuiExceptionHandler.java
│   │   # GUI 异常处理器。把领域异常、应用异常、系统异常转换成页面错误状态。

│   ├── GuiBootstrap.java
│   │   # GUI 依赖装配入口。集中创建 application handler、repository、page factory 等对象。

│   ├── mvc/
│   │   # 简化经典 MVC 基础设施。Controller 改 Model，View 观察 Model。

│   │   ├── GuiModel.java
│   │   │   # GUI Model 基础接口。表示可被 View 观察的页面状态模型。

│   │   ├── AbstractGuiModel.java
│   │   │   # GUI Model 抽象基类。封装 listener 管理和 fireChanged。

│   │   ├── GuiView.java
│   │   │   # GUI View 基础接口。负责绑定 model、挂载、卸载、渲染。

│   │   ├── GuiController.java
│   │   │   # GUI Controller 基础接口。负责初始化和处理用户输入。

│   │   ├── ModelChangeListener.java
│   │   │   # Model 变更监听器。View 通过它接收 Model 变化通知。

│   │   ├── ModelChangeEvent.java
│   │   │   # Model 变更事件。记录变化来源和可选的变化字段。

│   │   └── ViewEvent.java
│   │       # View 事件对象。用于表达按钮点击、表单字段变化、列表选择等 UI 事件。

│   ├── view/
│   │   # 通用 GUI 视图组件。只负责 UI 展示，不调用 application handler。

│   │   ├── MainWindow.java
│   │   │   # 主窗口。包含菜单栏、内容区域、状态栏。

│   │   ├── MainMenuView.java
│   │   │   # 主菜单视图。提供客户、账户、卡、投资、账务等入口。

│   │   ├── StatusBar.java
│   │   │   # 状态栏组件。显示当前用户、当前页面、系统消息。

│   │   ├── ErrorDialog.java
│   │   │   # 错误弹窗组件。展示用户可读错误信息。

│   │   ├── SuccessDialog.java
│   │   │   # 成功提示弹窗组件。

│   │   ├── ConfirmDialog.java
│   │   │   # 确认弹窗组件。用于高风险操作，例如还款、卖出投资产品。

│   │   ├── TablePanel.java
│   │   │   # 通用表格面板。用于展示客户列表、账户列表、流水列表。

│   │   ├── FormPanel.java
│   │   │   # 通用表单面板。封装字段布局、错误提示、提交按钮区域。

│   │   └── EmptyStatePanel.java
│   │       # 空状态面板。用于列表无数据或查询无结果。

│   ├── customer/
│   │   # 客户相关 GUI MVC 页面。

│   │   ├── RegisterCustomerModel.java
│   │   │   # 注册客户页面状态。保存表单输入、提交状态、错误消息、成功消息。

│   │   ├── RegisterCustomerView.java
│   │   │   # 注册客户页面视图。观察 RegisterCustomerModel 并渲染表单。

│   │   ├── RegisterCustomerController.java
│   │   │   # 注册客户控制器。处理表单事件，调用 RegisterCustomerHandler，更新 Model。

│   │   ├── RegisterCustomerPageFactory.java
│   │   │   # 注册客户页面工厂。创建 model、view、controller 并完成绑定。

│   │   ├── ShowCustomerModel.java
│   │   │   # 客户详情页面状态。保存 customerId、客户详情、loading 状态、错误消息。

│   │   ├── ShowCustomerView.java
│   │   │   # 客户详情页面视图。根据 ShowCustomerModel 渲染客户信息。

│   │   ├── ShowCustomerController.java
│   │   │   # 客户详情控制器。处理查询事件，调用 ShowCustomerHandler，更新 Model。

│   │   ├── ShowCustomerPageFactory.java
│   │   │   # 客户详情页面工厂。

│   │   ├── ListCustomersModel.java
│   │   │   # 客户列表页面状态。保存筛选条件、客户列表、选中行、分页状态。

│   │   ├── ListCustomersView.java
│   │   │   # 客户列表页面视图。观察 ListCustomersModel 并渲染表格。

│   │   ├── ListCustomersController.java
│   │   │   # 客户列表控制器。处理刷新、筛选、选择客户等事件。

│   │   └── ListCustomersPageFactory.java
│   │       # 客户列表页面工厂。

│   ├── account/
│   │   # 账户相关 GUI MVC 页面。

│   │   ├── OpenSavingsAccountModel.java
│   │   │   # 开立储蓄账户页面状态。保存客户编号、币种、初始存款、错误和提交状态。

│   │   ├── OpenSavingsAccountView.java
│   │   │   # 开立储蓄账户页面视图。

│   │   ├── OpenSavingsAccountController.java
│   │   │   # 开立储蓄账户控制器。调用 OpenSavingsAccountHandler 并更新 Model。

│   │   ├── OpenSavingsAccountPageFactory.java
│   │   │   # 开立储蓄账户页面工厂。

│   │   ├── OpenFxAccountModel.java
│   │   │   # 开立外汇账户页面状态。

│   │   ├── OpenFxAccountView.java
│   │   │   # 开立外汇账户页面视图。

│   │   ├── OpenFxAccountController.java
│   │   │   # 开立外汇账户控制器。

│   │   ├── OpenFxAccountPageFactory.java
│   │   │   # 开立外汇账户页面工厂。

│   │   ├── OpenInvestmentAccountModel.java
│   │   │   # 开立投资账户页面状态。

│   │   ├── OpenInvestmentAccountView.java
│   │   │   # 开立投资账户页面视图。

│   │   ├── OpenInvestmentAccountController.java
│   │   │   # 开立投资账户控制器。

│   │   ├── OpenInvestmentAccountPageFactory.java
│   │   │   # 开立投资账户页面工厂。

│   │   ├── ShowAccountModel.java
│   │   │   # 账户详情页面状态。保存 accountId、账户详情、loading 状态、错误消息。

│   │   ├── ShowAccountView.java
│   │   │   # 账户详情页面视图。

│   │   ├── ShowAccountController.java
│   │   │   # 账户详情控制器。

│   │   ├── ShowAccountPageFactory.java
│   │   │   # 账户详情页面工厂。

│   │   ├── ListAccountsModel.java
│   │   │   # 账户列表页面状态。保存客户编号、账户列表、筛选条件、选中行。

│   │   ├── ListAccountsView.java
│   │   │   # 账户列表页面视图。

│   │   ├── ListAccountsController.java
│   │   │   # 账户列表控制器。

│   │   └── ListAccountsPageFactory.java
│   │       # 账户列表页面工厂。

│   ├── card/
│   │   # 卡相关 GUI MVC 页面。

│   │   ├── IssueDebitCardModel.java
│   │   │   # 签发借记卡页面状态。

│   │   ├── IssueDebitCardView.java
│   │   │   # 签发借记卡页面视图。

│   │   ├── IssueDebitCardController.java
│   │   │   # 签发借记卡控制器。

│   │   ├── IssueDebitCardPageFactory.java
│   │   │   # 签发借记卡页面工厂。

│   │   ├── IssueSupplementaryDebitCardModel.java
│   │   │   # 签发附属借记卡页面状态。

│   │   ├── IssueSupplementaryDebitCardView.java
│   │   │   # 签发附属借记卡页面视图。

│   │   ├── IssueSupplementaryDebitCardController.java
│   │   │   # 签发附属借记卡控制器。

│   │   ├── IssueSupplementaryDebitCardPageFactory.java
│   │   │   # 签发附属借记卡页面工厂。

│   │   ├── IssueCreditCardModel.java
│   │   │   # 签发信用卡页面状态。

│   │   ├── IssueCreditCardView.java
│   │   │   # 签发信用卡页面视图。

│   │   ├── IssueCreditCardController.java
│   │   │   # 签发信用卡控制器。

│   │   ├── IssueCreditCardPageFactory.java
│   │   │   # 签发信用卡页面工厂。

│   │   ├── IssueSupplementaryCreditCardModel.java
│   │   │   # 签发附属信用卡页面状态。

│   │   ├── IssueSupplementaryCreditCardView.java
│   │   │   # 签发附属信用卡页面视图。

│   │   ├── IssueSupplementaryCreditCardController.java
│   │   │   # 签发附属信用卡控制器。

│   │   ├── IssueSupplementaryCreditCardPageFactory.java
│   │   │   # 签发附属信用卡页面工厂。

│   │   ├── ShowCardModel.java
│   │   │   # 卡详情页面状态。

│   │   ├── ShowCardView.java
│   │   │   # 卡详情页面视图。

│   │   ├── ShowCardController.java
│   │   │   # 卡详情控制器。

│   │   └── ShowCardPageFactory.java
│   │       # 卡详情页面工厂。

│   ├── investment/
│   │   # 投资相关 GUI MVC 页面。

│   │   ├── CreateProductModel.java
│   │   │   # 创建投资产品页面状态。

│   │   ├── CreateProductView.java
│   │   │   # 创建投资产品页面视图。

│   │   ├── CreateProductController.java
│   │   │   # 创建投资产品控制器。

│   │   ├── CreateProductPageFactory.java
│   │   │   # 创建投资产品页面工厂。

│   │   ├── BuyProductModel.java
│   │   │   # 买入投资产品页面状态。

│   │   ├── BuyProductView.java
│   │   │   # 买入投资产品页面视图。

│   │   ├── BuyProductController.java
│   │   │   # 买入投资产品控制器。

│   │   ├── BuyProductPageFactory.java
│   │   │   # 买入投资产品页面工厂。

│   │   ├── SellProductModel.java
│   │   │   # 卖出投资产品页面状态。

│   │   ├── SellProductView.java
│   │   │   # 卖出投资产品页面视图。

│   │   ├── SellProductController.java
│   │   │   # 卖出投资产品控制器。

│   │   ├── SellProductPageFactory.java
│   │   │   # 卖出投资产品页面工厂。

│   │   ├── ShowHoldingModel.java
│   │   │   # 持仓详情页面状态。

│   │   ├── ShowHoldingView.java
│   │   │   # 持仓详情页面视图。

│   │   ├── ShowHoldingController.java
│   │   │   # 持仓详情控制器。

│   │   └── ShowHoldingPageFactory.java
│   │       # 持仓详情页面工厂。

│   ├── credit/
│   │   # 信用卡账户、账单、还款相关 GUI MVC 页面。

│   │   ├── GenerateStatementModel.java
│   │   │   # 生成信用卡账单页面状态。

│   │   ├── GenerateStatementView.java
│   │   │   # 生成信用卡账单页面视图。

│   │   ├── GenerateStatementController.java
│   │   │   # 生成信用卡账单控制器。

│   │   ├── GenerateStatementPageFactory.java
│   │   │   # 生成信用卡账单页面工厂。

│   │   ├── RepayCreditCardModel.java
│   │   │   # 信用卡还款页面状态。

│   │   ├── RepayCreditCardView.java
│   │   │   # 信用卡还款页面视图。

│   │   ├── RepayCreditCardController.java
│   │   │   # 信用卡还款控制器。

│   │   ├── RepayCreditCardPageFactory.java
│   │   │   # 信用卡还款页面工厂。

│   │   ├── ShowStatementModel.java
│   │   │   # 信用卡账单详情页面状态。

│   │   ├── ShowStatementView.java
│   │   │   # 信用卡账单详情页面视图。

│   │   ├── ShowStatementController.java
│   │   │   # 信用卡账单详情控制器。

│   │   └── ShowStatementPageFactory.java
│   │       # 信用卡账单详情页面工厂。

│   ├── ledger/
│   │   # 账务相关 GUI MVC 页面。

│   │   ├── ShowBalanceModel.java
│   │   │   # 余额查询页面状态。

│   │   ├── ShowBalanceView.java
│   │   │   # 余额查询页面视图。

│   │   ├── ShowBalanceController.java
│   │   │   # 余额查询控制器。

│   │   ├── ShowBalancePageFactory.java
│   │   │   # 余额查询页面工厂。

│   │   ├── ShowEntriesModel.java
│   │   │   # 分录查询页面状态。

│   │   ├── ShowEntriesView.java
│   │   │   # 分录查询页面视图。

│   │   ├── ShowEntriesController.java
│   │   │   # 分录查询控制器。

│   │   └── ShowEntriesPageFactory.java
│   │       # 分录查询页面工厂。

│   └── business/
│       # 业务流水相关 GUI MVC 页面。

│       ├── ShowBusinessTransactionModel.java
│       │   # 业务流水详情页面状态。

│       ├── ShowBusinessTransactionView.java
│       │   # 业务流水详情页面视图。

│       ├── ShowBusinessTransactionController.java
│       │   # 业务流水详情控制器。

│       ├── ShowBusinessTransactionPageFactory.java
│       │   # 业务流水详情页面工厂。

│       ├── ListBusinessTransactionsModel.java
│       │   # 业务流水列表页面状态。

│       ├── ListBusinessTransactionsView.java
│       │   # 业务流水列表页面视图。

│       ├── ListBusinessTransactionsController.java
│       │   # 业务流水列表控制器。

│       └── ListBusinessTransactionsPageFactory.java
│           # 业务流水列表页面工厂。
└── web/
    # Web 展示入口。负责 HTTP API 的 controller、DTO、route 注册和错误映射。
    # 不实现 socket、HTTP parser、event loop。
    # 底层 HTTP runtime 由 infra/web/jdk 等实现提供。

    ├── BankingWeb.java
    │   # Web 启动入口。装配 WebRuntime、routes、controller，并启动 HTTP 服务。

    ├── WebApplication.java
    │   # Web 应用对象。组合 controller、routes、exception mapper 等 Web adapter 组件。

    ├── WebBootstrap.java
    │   # Web 依赖装配入口。创建 application handler、controller、route registrar 和 runtime。

    ├── WebConfig.java
    │   # Web 配置。保存 host、port、线程数等配置。

    ├── WebRuntime.java
    │   # Web runtime 抽象。负责注册路由、启动和停止 HTTP 服务。

    ├── WebRequest.java
    │   # Web 请求抽象。屏蔽 JDK HttpServer、Javalin、Spring 等底层差异。

    ├── WebResponse.java
    │   # Web 响应对象。封装 status code、content type、body。

    ├── WebRouteHandler.java
    │   # Web 路由处理器接口。Controller 方法可以适配成它。

    ├── RouteRegistrar.java
    │   # 路由注册接口。每个业务域实现自己的 route 注册逻辑。

    ├── WebContext.java
    │   # 请求上下文。保存 requestId、traceId、当前用户、locale 等请求级状态。

    ├── WebExceptionMapper.java
    │   # Web 异常映射器。把 application/domain 异常转换成 HTTP status 和 ErrorResponseDto。

    ├── dto/
    │   # Web 通用 DTO。只属于 Web adapter，不进入 application/domain。

    │   ├── ErrorResponseDto.java
    │   │   # 错误响应 DTO。包含 errorCode、message、requestId 等字段。

    │   ├── PageRequestDto.java
    │   │   # 分页请求 DTO。封装 page、size、sort 等参数。

    │   └── PageResponseDto.java
    │       # 分页响应 DTO。封装 items、page、size、total 等字段。

    ├── customer/
    │   # 客户相关 Web controller、routes 和 DTO。

    │   ├── CustomerController.java
    │   │   # 客户 API controller。处理注册客户、查询客户、客户列表请求。

    │   ├── CustomerRoutes.java
    │   │   # 客户路由注册。集中注册 /customers 相关 routes。

    │   ├── RegisterCustomerRequestDto.java
    │   │   # 注册客户请求 DTO。对应 POST /customers 请求体。

    │   ├── CustomerResponseDto.java
    │   │   # 客户响应 DTO。用于客户详情和注册成功响应。

    │   └── CustomerListResponseDto.java
    │       # 客户列表响应 DTO。用于 GET /customers。

    ├── account/
    │   # 账户相关 Web controller、routes 和 DTO。

    │   ├── AccountController.java
    │   │   # 账户 API controller。处理开户、查询账户、账户列表请求。

    │   ├── AccountRoutes.java
    │   │   # 账户路由注册。集中注册 /accounts 相关 routes。

    │   ├── OpenSavingsAccountRequestDto.java
    │   │   # 开立储蓄账户请求 DTO。

    │   ├── OpenFxAccountRequestDto.java
    │   │   # 开立外汇账户请求 DTO。

    │   ├── OpenInvestmentAccountRequestDto.java
    │   │   # 开立投资账户请求 DTO。

    │   ├── AccountResponseDto.java
    │   │   # 账户响应 DTO。用于账户详情和开户成功响应。

    │   └── AccountListResponseDto.java
    │       # 账户列表响应 DTO。

    ├── card/
    │   # 卡相关 Web controller、routes 和 DTO。

    │   ├── CardController.java
    │   │   # 卡 API controller。处理发卡、查询卡等请求。

    │   ├── CardRoutes.java
    │   │   # 卡路由注册。集中注册 /cards 相关 routes。

    │   ├── IssueDebitCardRequestDto.java
    │   │   # 签发借记卡请求 DTO。

    │   ├── IssueSupplementaryDebitCardRequestDto.java
    │   │   # 签发附属借记卡请求 DTO。

    │   ├── IssueCreditCardRequestDto.java
    │   │   # 签发信用卡请求 DTO。

    │   ├── IssueSupplementaryCreditCardRequestDto.java
    │   │   # 签发附属信用卡请求 DTO。

    │   └── CardResponseDto.java
    │       # 卡响应 DTO。用于发卡成功和卡详情响应。

    ├── investment/
    │   # 投资相关 Web controller、routes 和 DTO。

    │   ├── InvestmentController.java
    │   │   # 投资 API controller。处理产品创建、买入、卖出、持仓查询。

    │   ├── InvestmentRoutes.java
    │   │   # 投资路由注册。集中注册 /investment 相关 routes。

    │   ├── CreateProductRequestDto.java
    │   │   # 创建投资产品请求 DTO。

    │   ├── BuyProductRequestDto.java
    │   │   # 买入投资产品请求 DTO。

    │   ├── SellProductRequestDto.java
    │   │   # 卖出投资产品请求 DTO。

    │   └── HoldingResponseDto.java
    │       # 持仓响应 DTO。

    ├── credit/
    │   # 信用卡账户、账单、还款相关 Web controller、routes 和 DTO。

    │   ├── CreditController.java
    │   │   # 信用卡 API controller。处理账单生成、账单查询、信用卡还款。

    │   ├── CreditRoutes.java
    │   │   # 信用卡路由注册。集中注册 /credit 相关 routes。

    │   ├── GenerateStatementRequestDto.java
    │   │   # 生成信用卡账单请求 DTO。

    │   ├── RepayCreditCardRequestDto.java
    │   │   # 信用卡还款请求 DTO。

    │   ├── StatementResponseDto.java
    │   │   # 信用卡账单响应 DTO。

    │   └── RepaymentResponseDto.java
    │       # 信用卡还款响应 DTO。

    ├── ledger/
    │   # 账务相关 Web controller、routes 和 DTO。

    │   ├── LedgerController.java
    │   │   # 账务 API controller。处理余额查询和分录查询。

    │   ├── LedgerRoutes.java
    │   │   # 账务路由注册。集中注册 /ledger 相关 routes。

    │   ├── BalanceResponseDto.java
    │   │   # 余额响应 DTO。

    │   ├── LedgerEntryResponseDto.java
    │   │   # 单条账务分录响应 DTO。

    │   └── LedgerEntriesResponseDto.java
    │       # 多条账务分录响应 DTO。

    └── business/
        # 业务流水相关 Web controller、routes 和 DTO。

        ├── BusinessTransactionController.java
        │   # 业务流水 API controller。处理业务流水详情和列表查询。

        ├── BusinessTransactionRoutes.java
        │   # 业务流水路由注册。集中注册 /business-transactions 相关 routes。

        ├── BusinessTransactionResponseDto.java
        │   # 业务流水响应 DTO。

        └── BusinessTransactionListResponseDto.java
            # 业务流水列表响应 DTO。

```
