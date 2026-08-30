# Changelog

JobRadar 按版本成长。每个版本都是值得发布、值得回顾、值得展示的里程碑。

## v0.2 · Connect ✅（已发布 `v0.2.0`）

**Mission：所有客户端只认 Backend，Backend 只认 JobProvider。**

- 规范产品读取 API：`/jobs` · `/jobs/{id}` · `/companies/{name}` · `/providers`（走 JobProvider）
- **Web 接入后端**：Web 只认 `/api/jobs`（薄代理），不知道 Mock / CSV / 数据源——数据源换多少次，Web 零修改。
- **统一领域模型**：跨端一套 `JobDto` / `CompanyDto`（snake_case），业务层与数据源解耦。
- 接口稳定，不再乱长：Agent / AI 的未来能力都建立在这套 API 上。

> 原则：**产品按版本成长，不按代码行数成长。** 做到"把数据链路打通"就发布，不因为还能继续写就继续写。

## v0.1 · Foundation ✅（已发布 `v0.1.0`）

**发布日期**：(tag `v0.1.0`)

### 为什么做 JobRadar

我们不是想做另一个 "Job Search"——那只是一个搜索框，搜完就结束了。

JobRadar 想做的是 **Job Lifecycle（求职生命周期）**：收藏、投递记录、面试、Offer、AI 建议、公司画像、时间线、提醒——一屏管理，端手实时同步。

因为**真正的求职，不是"搜到一份工作"，而是"manage 整个从投递到 offer 的过程"。** JobRadar 像管理一个产品一样，管理你的职业。

### 这个版本交付了什么

- **端手一体的产品骨架**：Android 原生 + Web + 后端，统一 `{ code, message, data }` 契约，全端实时同步。
- **产品级视觉**：深色 + 青绿「君子内敛」设计语言，玻璃质感、辉光、雷达扫描光影、六边形能力雷达。
- **可插拔数据源**（`JobProvider`）：产品读取层完全抽象，Mock / CSV / 未来任一源都是插件，**绝不碰爬虫**。
- **AI 原生**：AI 助手接本地知识库（AnythingLLM + RAG）、JD 分析、匹配度、公司画像。
- **深邃工程**：Clean Architecture + MVI 单向数据流 + Hilt + 契约层 + 单元/集成测试 + Lint 0 错误。

### 架构要点

```
Android (Kotlin/Compose/MVI)   Web (Next.js)
        │        端手同步          │
        ▼                          ▼
   Backend (Spring Boot)
  { code, message, data } 契约 · WebSocket 实时推送
        │
        ▼
  JobProvider (可插拔) ──► Mock · CSV · API · Plugin(未来)
        │
        ▼
  AI 层 · LLM 网关 · RAG 知识库 · Agent
```

### 版本目标

`v0.1 · Foundation` 的意义：**把产品的地基打牢**——品牌、愿景、架构、数据源抽象、多端骨架、产品语言、Git 历史。它不是"做完了一个功能"，而是"一个产品开始成形"。
