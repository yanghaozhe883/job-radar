# 求职雷达 · 后端（Kotlin + Spring Boot）

> 与 Android 客户端完全对齐的 REST + WebSocket 后端。所有接口返回统一信封 `{ code, message, data }`，契约见 `docs/方向总纲.md` §8.2。

---

## 快速开始

**零配置运行（H2 内存库，开箱即用）：**
```bash
./gradlew bootRun                     # 或
java -jar build/libs/jobradar-backend-1.0.0.jar \
     --spring.profiles.active=h2 --server.port=8080
```
默认**无演示数据**（真实接口，jobs 返回空）；要灌入演示职位数据便于本地/demo：
```bash
java -jar build/libs/jobradar-backend-1.0.0.jar \
     --spring.profiles.active=h2 --server.port=8080 --demo.seed=true
```
（`demo.seed` 默认 `false`，见 `application.yml`；由 `config/DataSeeder` 在开启时灌入示例数据。）

**切 PostgreSQL（生产）：**
```bash
java -jar build/libs/jobradar-backend-1.0.0.jar \
     --spring.profiles.active=postgres \
     --DB_URL=jdbc:postgresql://localhost:5432/jobradar \
     --DB_USER=jobradar --DB_PASSWORD=jobradar
```

**测试（含架构约束校验）：**
```bash
./gradlew test
```

---

## API 契约

统一响应：
```json
{ "code": 0, "message": "ok", "data": { ... } }
```
- `code==0` 成功；非 0 为业务错误。错误也走信封：`{ "code": 404, "message": "职位不存在" }`。
- 字段统一 **snake_case**（`salary_min_k`、`match_score`、`page_size` 等），与客户端 `JobDto` 完全一致。

### 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/jobs` | 职位流，支持 `city`/`keyword`/`job_type`/`min_salary_k`/`sort`/`page`/`page_size` |
| GET | `/api/v1/jobs/{id}` | 职位详情 |
| GET | `/api/v1/jobs/radar/hits` | 雷达实时命中（`count`/`keywords`） |
| POST | `/api/v1/push/demo` | 演示：广播一条 WebSocket 推送（触发 Android 雷达点亮） |
| WS | `ws://host:8080/jobs/stream` | 实时职位推送（`JobPushEvent`） |
| POST | `/api/v1/auth/login` | 短信登录（get-or-create 用户） |
| GET | `/api/v1/users/{id}` | 用户信息 |
| GET | `/api/v1/users/{id}/jobs?status=` | 我的收藏/投递列表 |
| PUT | `/api/v1/users/{id}/jobs/{jobId}/status` | 收藏/投递/已读/隐藏 |
| GET/PUT | `/api/v1/users/{id}/preferences` | 雷达偏好（JSON 文档） |
| POST | `/api/v1/crawler/run` | 手动跑一次采集管线 |
| GET | `/api/v1/crawler/status` | 采集器状态/说明 |
| POST | `/api/v1/ai/chat` | AI 助手：代理到本地 AnythingLLM 知识库 |

---

## AI 知识库接入（AnythingLLM）

`config/LlmProperties` + `ai/AiService` 实现。**API key 只存在后端环境变量**（`ANYTHINGLLM_API_KEY`），Android 端永远不持有——符合 Clean Arch + 密钥安全。

```
Android AI 助手 → POST /api/v1/ai/chat → backend AiService → AnythingLLM /workspace/{slug}/chat → 回答
```

- 端点：`POST /api/v1/ai/chat`（body: `{message, mode=query|chat, workspaceSlug}`）。
- 配置：`LLM_BASE_URL`(默认 `http://127.0.0.1:3001`)、`ANYTHINGLLM_API_KEY`、`LLM_WORKSPACE`(默认 `job-radar`)。
- **注意**：后端代理调用 AnythingLLM 必须 **强制 HTTP/1.1**（`HttpClient.Version.HTTP_1_1`）——HTTP/2 协商会导致 AnythingLLM 返回 400，这是联调踩到的真实坑。
- 已实测：`Android → backend → AnythingLLM` 全链路返回 200（对有嵌入文档的库可正确检索回答）。

---

## 数据采集管线（合规优先）

`config/CrawlerProperties` + `crawler/` 包实现，**默认关闭**（`crawler.enabled=false`）。
固化合规红线（方向总纲 §9.1）：仅公开数据；限流（`rate-limit-ms`）；User-Agent 自标识；
尊重 robots.txt（best-effort）；`dataSource` 留痕；不碰登录态/用户数据。

```
JobSource (demo | http)             可插拔源
  → RawJob (归一化)                 与持久化解耦
  → JobIngestionService             拉取→去重(sourceExternalId)→入库→广播新职位
  → @Scheduled + POST /crawler/run  定时 + 手动触发
```

- 默认 `crawler.source=demo`（内置样例，离线可测）。接真实数据用 `crawler.source=http`
  + `crawler.target-url`（期望返回 JSON 数组，字段同契约）。
- 建库：`--crawler.enabled=true`。实测：定时/手动均可采集，重复运行不重复入库。

---

## 架构分层

```
config/        ApiProperties + DataSeeder（种子数据）
api/           ApiResponse 信封 + 全局异常处理 + 控制器 + dto（snake_case）
domain/        JPA 实体（CompanyEntity / JobEntity）
repo/          Spring Data JPA 仓库（JobRepository / CompanyRepository）
service/       JobService（搜索/排序/分页/雷达命中）+ JobMapper（实体<->DTO）
websocket/     WebSocketConfig + JobPushHandler（实时广播）
```

- **控制器**只做参数绑定与信封装帧；**业务**在 `JobService`；**持久化**在 `repo`。
- 全局异常处理把任何错误都规整成 `{ code, message, data }`，客户端永不面对框架裸错误体。
- 层间用 DTO 隔离，实体不直接暴露到响应。

---

## 与 Android 客户端的对接

客户端 `data/remote/JobApiService.kt` 的 3 个端点与本后端一一对应；`ApiResponse<T>`
信封与 `ApiResponse.kt` 一致。客户端 `NetworkModule` 默认地址 `http://10.0.2.2:8080/`
（模拟器访问宿主机/后端），后端用 `--server.port=8080` 即可对接。

WebSocket：客户端连接 `/jobs/stream`；后端在检测到新职位时 `JobPushHandler.broadcast`，
推送 `{ jobId, title, matchScore }`，驱动 Android 雷达点亮。

---

*状态：可运行骨架，接口契约已验证（H2 profile 下 GET /jobs、/jobs/{id}、/jobs/radar/hits、404 均按 { code, message, data } 返回）。*
