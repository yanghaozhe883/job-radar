# Contributing to JobRadar

感谢你愿意为 JobRadar 贡献。我们是**按产品标准**维护这个仓库的——有版本、有规范、有 Issue、有 Review。

## 开发流程（每出一个功能）

```
1. 开一个 Issue（说明想解决什么 / 为什么）
2. 从 main 拉一个 feature branch
3. 写代码 + 补测试
4. 开 Pull Request
5. Review → Merge
6. 归入下一个 Release
```

> 单个小功能不用这么重，用 Commit 规范即可；一个能改变产品形态的功能，请走完整流程。

## Commit 规范（Conventional Commits）

请用约定式提交，让 Git 历史清晰可读：

```
feat(scope): 新增功能
fix(scope): 修复
refactor(scope): 重构
docs(scope): 文档
test(scope): 测试
style(scope): 格式
chore(scope): 杂项

# scope 例：web / android / backend / provider / docs / api
```

✅ 好的：
```
feat(provider): introduce csv provider
```
❌ 避免：
```
fix
update
update2
```

## 代码规范

- **后端**：Kotlin + Spring Boot，统一 `{ code, message, data }` 契约，字段 `snake_case`。
- **Android**：Clean Architecture + MVI 单向数据流 + Hilt。
- **Web**：Next.js + TypeScript + Tailwind，深色"君子内敛"设计语言。
- **数据源**：一切数据读取走 `JobProvider` 接口，**不写爬虫**。新源 = 实现 `JobProvider` + 注册。

## 测试

改动前请跑对应测试，保持 `Lint 0 错误`、测试全绿：

```bash
# 后端
cd backend && ./gradlew test
# Android
cd android && ./gradlew :app:testDebugUnitTest :app:lintDebug
```

## 提交前的检查清单

- [ ] 遵守 Commit 规范
- [ ] 改动不破坏现有测试 / Lint
- [ ] 新数据源走 `JobProvider` 接口（不碰爬虫）
- [ ] 符合产品名词语（Pipeline / Resume Workspace / Opportunity Explorer…）
