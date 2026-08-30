# 求职雷达 APP · Android（原生）

> 真正的原生 Android 应用。核心卖点：**严格 Clean Architecture + MVI 单向数据流 + Hilt 依赖注入**，UI 与业务完全解耦，数据不写死在视图层；同时把「炫技级」UI/UX 落到雷达、机会流、职位详情等页面。

本工程是 `docs/方向总纲.md` 与 `docs/设计系统规范.md` 的 Android 落地方案。

---

## 快速开始

1. 用 **Android Studio**（Ladybug 或更新）打开本目录 `android/`。
2. 首次打开会自动下载 **Gradle Wrapper 8.11.1** 与依赖。
3. 同步后会按 `local.properties` 中的 `sdk.dir` 定位 SDK（本机为 `C:\Users\Lenovo\AppData\Local\Android\Sdk`）。
4. 运行 `app`，先见**登录页**（手机号 + 验证码，演示可填任意合法手机号和 6 位码）；登录后进入 4 Tab：**雷达 / 机会 / 收藏 / 我的**。
5. 数据完全来自真实后端（`{ code, message, data }` 契约）；本地仅作 Room 缓存。需先启动后端
   （`jobradar/backend`，默认 `http://10.0.2.2:8080`），否则展示错误/空态。演示数据用后端
   `--demo.seed=true` 开关。

命令行构建：
```bash
./gradlew :app:assembleDebug      # 打包
./gradlew :app:testDebugUnitTest  # 单元测试（含匹配引擎）
```

---

## 架构总览（Clean Architecture）

依赖方向严格**由外向内**：`presentation → domain ← data`，领域层零框架依赖。

```
┌────────────────────────────────────────────────────────────┐
│  presentation（UI）  Compose + MVI + Hilt viewModel          │
│   - 只收集 Event、渲染 State、观察 Effect（单向数据流）        │
├────────────────────────────────────────────────────────────┤
│  domain（业务）           纯 Kotlin，不 import Android        │
│   - model / repository(接口) / usecase(含匹配引擎)            │
├────────────────────────────────────────────────────────────┤
│  data（数据实现）  Retrofit + Room + DataStore + WebSocket    │
│   - 实现 domain.repository 接口；Hilt 绑定注入                │
├────────────────────────────────────────────────────────────┤
│  core   MviContract / MviViewModel / dispatcher / AppResult  │
└────────────────────────────────────────────────────────────┘
```

### 单向数据流（MVI）
每个 feature 独立一套契约（`RadarContract` / `JobsContract` / …），继承统一基类：

| 层 | 职责 |
|----|------|
| `State` | 当前 UI 快照（不可变） |
| `Event` | **唯一输入**，用户意图 |
| `Effect` | 一次性副作用（导航 / toast / 触觉） |
| `MviViewModel` | 唯一事实源，reducer 产出新 State，发射 Effect |
| `UiState` → `collectAsStateWithLifecycle` | UI 仅订阅渲染 |

**规则**：UI 从不直接改状态，只 `onEvent(Event)`；业务逻辑全部收敛到 `domain.usecase`。这样 `ScoreJobUseCase`（匹配引擎）可完全脱离 Android 独立单测。

### Hilt 依赖注入
- `@HiltAndroidApp`（Application）+ `@AndroidEntryPoint`（Activity）
- `@Module @InstallIn(SingletonComponent)`：`NetworkModule` / `DatabaseModule` / `RepositoryModule`(Binds) / `DispatcherModule`
- `RepositoryModule` 把 `domain.repository` 接口绑定到 `data` 实现——Clean Architecture 的核心接缝
- 各 ViewModel 用 `@HiltViewModel` + 构造函数注入

---

## 数据与后端契约

所有后端接口返回统一信封：

```json
{ "code": 0, "message": "ok", "data": { ... } }
```

- `data/remote/ApiResponse.kt` 定义了 `ApiResponse<T>`，`code==0` 为成功。
- `data/remote/JobApiService.kt` 为 Retrofit 接口，`{ code, message, data }` 已对齐。
- `data/remote/WebSocketClient.kt` 为 OkHttp WebSocket 封装，供后端 `/jobs/stream` 实时推送新职位。
- `NetworkModule` 中 `API_BASE_URL` 默认 `http://10.0.2.2:8080/`（模拟器访问宿主机），可改为真实地址。
- 发布前 `usesCleartextTraffic` 需按后端 HTTPS 策略收紧（见 `AndroidManifest.xml`）。

> **无 Mock**：应用数据完全来自真实后端，本地仅 Room 缓存（无任何演示/假数据兜底）。后端不可达时
> 展示错误/空态。演示数据在**后端**用 `--demo.seed=true` 开启（见 `backend/README.md`）。

---

## 动效/炫技实现

| 页面 | 效果 | 关键实现 |
|------|------|---------|
| 雷达页 | 扫描波纹/扫掠线/粒子/检测光晕 | `RadarCore.kt`（Canvas + `rememberInfiniteTransition`，周期 1600ms） |
| 机会流 | 卡片 3D 倾斜 + 弹簧回弹 | `JobsScreen.kt`（`graphicsLayer.rotationX` + `spring(dampingRatio≈0.55)`） |
| 职位详情 | 毛玻璃头图 + 技能匹配可视化 | `JobDetailScreen.kt`（`RenderEffect` blur + Canvas 环形填充） |
| 全局 | 共享元素转场预留 | `JobRadarNavHost.kt`（NavHost 转场 + 预留给 `SharedTransitionLayout`） |
| 底部导航 | 选中缩放高亮 | `BottomNavBar.kt`（`animateFloatAsState` + `spring`） |

设计令牌统一在 `presentation/theme/`（`Color.kt` / `Type.kt` / `Shape.kt`），与 `设计系统规范.md` 对齐。

---

## 测试

- `domain/usecase/ScoreJobUseCaseTest.kt` — 匹配引擎纯逻辑单测（`testDebugUnitTest`）。
- 可扩展：`data/mapper/JobMapperTest`、各 `*ViewModelTest`（注入 fake repository），因 dispatcher 已抽象，均无需 Android 环境。

---

## 目录速览

```
app/src/main/java/com/jobradar/app/
  core/mvi/            MviContract + MviViewModel（UDF 基类）
  core/dispatcher/     AppDispatcher（可注入 dispatcher）
  core/common/         AppResult / AppError
  domain/model/        Company / Job / UserPreference / JobFilter / MatchScore
  domain/repository/   JobRepository / UserPreferencesRepository（接口）
  domain/usecase/      ScoreJobUseCase / Observe* / GetJobDetail …
  data/local/          Room（Dao/Entity/Database）+ DataStore
  data/remote/         ApiResponse / JobApiService / WebSocketClient / dto
  data/mapper/         JobMapper（DTO/Entity ↔ Domain）
  data/repository/     RepositoryImpl（注入绑定）
  di/                  Network / Database / Repository / Dispatcher Module
  presentation/theme/  设计令牌 + Theme
  presentation/ui/components/  GlassSurface / PrimaryButton / MatchRing / TagChip / JobCard / BottomNavBar
  presentation/{radar,jobs,favorites,profile,detail}/  MVI feature (Contract + ViewModel + Screen)
  presentation/auth/     登录页（MVI feature）
  presentation/root/     RootViewModel（会话门控：登录↔主界面）
  presentation/navigation/  JobRadarNavHost
```

---

## 登录 / 会话门控

- 启动后 `JobRadarRoot` 观察会话（`AuthRepository.observeSession`，DataStore 持久化）。
- **未登录 → 登录页**；**已登录 → 主界面（4 Tab）**，用 `Crossfade` 过渡。
- 「我的」页提供「退出登录」（清会话 → 自动回到登录页）。
- 登录用「手机号 + 验证码」；验证逻辑抽成纯函数 `AuthValidator`（可单测）。

> ⚠️ 曾在此发现并修复一个真实崩溃：两个 Repository 各自声明了同名 DataStore
> （`jobradar_prefs`），触发 AndroidX「multiple DataStores active for the same file」。
> 已重构为**单一共享 `appDataStore`**（`data/local/AppDataStore.kt`），彻底规避。

---

## 面试讲稿 / 亮点（可展开讲的点）

1. **严格单向数据流**：每个 Feature 一套 `Contract`（State/Event/Effect）+ 继承 `MviViewModel`；
   UI 从不改状态，只 `onEvent()`，业务全在 `domain.usecase`。
2. **Clean Architecture 纪律**：`presentation → domain ← data`，领域层零 Android 依赖；
   `RepositoryModule`（`@Binds`）把领域接口绑到数据实现——架构核心接缝。
3. **前后端契约对齐**：统一 `{ code, message, data }`，字段 snake_case，WebSocket 消息体一致；
   甚至在联调中定位并修掉了 2 处真实的序列化 bug（`PageDto.has_more` 缺注解、`JobPushEvent` 缺 `@Serializable`）。
4. **可测试性**：匹配引擎（`ScoreJobUseCase`）、DTO 映射（`JobMapper`）、登录校验（`AuthValidator`）
   均为纯逻辑，11 个单测在 `testDebugUnitTest` 全绿（无需 Android 环境）。
5. **真实设备排错**：模拟器实测中抓出并修复了一个 DataStore 共享崩溃（见上），体现真机验证价值。
6. **动效**：`JobRadarMotion` 为唯一动效权威（弹簧各档、easing），雷达页内环虚线/检测弹环在真机校准。
7. **实时推送**：`OkHttpWebSocketClient` 对接后端 `/jobs/stream`，收到广播即触发雷达「检测到新机会」闪光。
8. **质量门**：`lintDebug` 0 错误（`NewApi`/`ComposableNaming`/`ModifierParameter`/`UnusedResources`/
   `MonochromeLauncherIcon`/`ObsoleteSdkInt`/重复依赖等已逐一清零），仅剩 Kotlin 2.1.0 版本提示
   （有意锁 2.0.21 以配合 AGP 8.7.3）。深链缺 jobId、空数据等边界已做防护。

*状态：可运行完整应用（登录 + 4 Tab + 实时雷达 + 真实后端数据），Lint 0 错误。*

