# JobRadar · 求职雷达

> **管理求职生命周期，而不是只找工作。**

一个端手一体的求职产品：教你从「投递 → 面试 → Offer」的整个过程一屏管理，而不是把你丢给一个"搜岗位"的搜索框就来去无踪。Android 原生 + Web + 后端 + AI，全部打通。

<p align="center">
  <img src="docs/images/radar.png" alt="求职雷达 雷达页" width="300"/>
</p>

---

## 这是什么

**一句话**：把你求职的每一次动作——收藏、投递、面试、Offer、待办跟进——收进一个界面，端手实时同步，AI 帮你判断值不值得投。

**它和"求职网站"的区别**：求职网站帮你**找**岗位，JobRadar 帮你**管**求职这件事。投到哪了、面到哪了、哪个 offer 更好、这家公司值不值得去——全都在一个地方，清清楚楚。

它不是另一个 AI 聊天框。它是**把 AI 用在求职的真实决策上**，而不是让它陪你闲聊。

---

## 为什么是它

- **端手一体**：Android 原生（Kotlin + Jetpack Compose）+ Web（Next.js）+ 后端（Spring Boot），一套数据契约，全端实时同步。手机上看、电脑上改，状态一致。
- **AI 用在刀刃上**：AI 岗位分析、与你的匹配度、公司值不值得去、JD 浓缩成要点——帮你在关键节点做决定，而不是替代你做决定。
- **架构克制可插拔**：职位数据源是抽象接口，Mock / CSV / 未来任何数据源都是插件，**产品本身不关心数据从哪来**。这就避免了"天天爬虫、天天修接口"的泥潭。
- **干净的工程**：Clean Architecture + 单向数据流 + 分层解耦 + 单元/集成测试 + Lint 0 错误。它证明的不只是"会调 AI"，而是**会做真正的软件**。

---

## 快速开始（3 分钟）

```bash
# 1) 起后端（内置数据 + Mock 数据源，零配置）
cd backend
./gradlew bootRun            # http://localhost:8080

# 2) 起 Web 端（连后端）
cd web
pnpm install && pnpm dev      # http://localhost:3000

# 3) Android（用 Android Studio 打开 android/ 直接运行）
cd android
./gradlew :app:assembleDebug :app:testDebugUnitTest
```

> 后端零配置、开箱即用；Web 连后端、Android 可装。最低门槛即可跑起来看到完整产品。

---

## 架构

```
 Android (Kotlin/Compose)     Web (Next.js)
        │       实时同步          │
        ▼                        ▼
   Backend (Spring Boot)  · 统一数据契约 · 实时推送
        │
        ▼
   职位数据源（可插拔）—— Mock · CSV · 未来更多
        │
        ▼
   AI 层 · 岗位分析 · 匹配度 · 公司画像
```

- **统一契约**：所有端走同一套 `{ code, message, data }` 数据信封，字段 `snake_case`。
- **数据源抽象**：职位读取走一个接口，数据来源完全插件化——换数据源不改任何界面。
- **AI 能力层**：岗位分析、匹配度、公司画像，作为服务能力，而不是独立的聊天入口。

---

## 技术栈

| 端 | 技术 |
|----|------|
| Android | Kotlin · Jetpack Compose · MVI · Hilt · Clean Architecture |
| Web | Next.js · TypeScript · Tailwind CSS |
| 后端 | Kotlin · Spring Boot · WebSocket · 可插拔数据源 · AI 服务层 |
| 数据 | 可插拔数据源（Mock / CSV / 未来插件），统一契约 |

---

## 目录结构

| 目录 | 说明 |
|------|------|
| `android/` | Android 原生应用 |
| `web/` | Web 端（Next.js） |
| `backend/` | 后端服务（Spring Boot） |
| `docs/` | 设计文档 / 规范 |
| `fedata/` | 演示数据 |

---

## 为什么值得看

如果你在做 AI 应用、想做端手一体的产品，或者想看看"怎么把一个 AI 功能做成真正有用的产品"——这里有一套完整的、可运行、可扩展的实践。

它也是一个公开的成长记录：从 v0.1 做一个能跑的产品，到 v0.2 把端手数据链路打通。**按版本长大，而不是按代码行数堆。**

---

## 版本

- **v0.1 · Foundation**：端手一体骨架、可插拔数据源、产品级视觉。
- **v0.2 · Connect**：端手数据源真正打通，Web/App/后端一套模型。
- **v0.3 · Insight**（规划）：AI 让每个岗位"看得懂、比得出、能决策"。

完整路线见 [ROADMAP](ROADMAP.md)，变更见 [CHANGELOG](CHANGELOG.md)。

---

## License

MIT

---

## 关于

一个独立作者持续维护的公开作品。如果你觉得有用，欢迎 star；如果发现问题，欢迎提 Issue——**真正的产品，从第一批用户开始。**

- 👏 [贡献指南](CONTRIBUTING.md) · [行为准则](CODE_OF_CONDUCT.md)
