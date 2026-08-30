package com.jobradar.app.domain.repository

import com.jobradar.app.domain.model.Resume
import com.jobradar.app.domain.model.ResumeProject
import com.jobradar.app.domain.model.ResumeSkill
import javax.inject.Inject

/** Provides the user's resume content. For now it's the finalized, static resume. */
interface ResumeRepository {
    fun getResume(): Resume
}

class InMemoryResumeRepository @Inject constructor() : ResumeRepository {
    override fun getResume(): Resume = Resume(
        name = "杨浩哲",
        tagline = "Agent · AI Application · Full Stack",
        contact = "137-3003-9933  |  3529682358@163.com  |  河北保定  |  2027年6月毕业",
        education = "河北农业大学（保定西校区）  国际经济与贸易（本科）  |  2023.09 - 2027.06（预计毕业）",
        skills = listOf(
            ResumeSkill("AI Application", "熟悉多模型路由与 API 集成（OpenAI / DeepSeek 等），掌握 RAG 完整链路（AnythingLLM / 向量化 / 多工作区路由），具备多 Agent 编排、Tool Calling 与状态机设计实践经验。"),
            ResumeSkill("Agent Engineering", "具备任务编排、角色化执行、人工审批、执行证据与知识库能力，可完成多 Agent 协作系统的设计、调度与落地。"),
            ResumeSkill("Full Stack", "熟悉 Python、Go、TypeScript、Kotlin；熟练用 Next.js 构建流式 Web 应用，掌握 Android 原生开发（Jetpack Compose / MVI）及微信小程序开发。"),
            ResumeSkill("Infra", "熟悉 Linux / Shell 脚本，掌握 Docker 容器化沙箱与部署，熟练配置 Caddy / Nginx 及 CI/CD 自动化流水线。"),
        ),
        projects = listOf(
            ResumeProject(
                title = "王庭 Aether Court（多 Agent 协作与调度平台）",
                meta = "个人自研 | 2026.08 - 至今",
                flagship = true,
                bullets = listOf(
                    "架构设计：从零设计可插拔的多 Agent 协作网关，接入 Codex / Claude / DeepSeek 等多模型，统一管理多模型能力，支持跨 Agent 状态共享与 CLI / API 通信。",
                    "核心引擎：自研任务状态机与编排器，通过 SQLite 实现任务流（编排 / 审批 / 产物）的单写事务与全量留痕；引入 LiteLLM 统一网关，实现模型动态路由、降级与 Token 成本双控。",
                    "成果：已累计执行 85+ 任务（66 完成）、447 步、113 条审批、186 个成果物、1909 条事件；全程成本约 $0.99。",
                    "工程闭环：搭建 Docker 沙箱隔离执行环境，替代裸进程运行代码生成与测试；集成知识库与人工审批流，保障 AI 输出成果的可控性与可追溯性。",
                ),
            ),
            ResumeProject(
                title = "求职雷达（原生 Android 应用 + 后端）",
                meta = "个人项目 | 2026.08 - 至今",
                bullets = listOf(
                    "从 0 到 1 独立完成 Android + Spring Boot 全栈开发。",
                    "客户端：原生 Android（Kotlin / Jetpack Compose），采用 Clean Architecture + MVI 单向数据流 + Hilt，构建雷达 / 机会 / 收藏 / 我的四模块。",
                    "实时与后端：Spring Boot 后端对接统一 { code, message, data } 契约；通过 WebSocket 实时推送职位驱动雷达点亮，用户数据与后端同步。",
                    "AI 与质量：集成知识库 AI 助手；双端 40+ 单元 / 集成测试。",
                ),
            ),
            ResumeProject(
                title = "芥子 Singularity（流式 AI 对话助手，含知识库 RAG）",
                meta = "个人项目 | 2026.08 - 至今",
                bullets = listOf(
                    "流式引擎：Next.js + TypeScript 实现 SSE 流式对话（前端逐字渲染），多模型一键切换、按模型自动路由。",
                    "RAG 链路：接入自建知识库检索（/recall），提问时注入「答案 + 来源」，实现检索→注入→流式生成的完整 RAG。",
                    "工程安全：服务端统一代理第三方模型 API，客户端无敏感凭据暴露；Docker / Caddy 自托管。",
                ),
            ),
        ),
        otherProjects = listOf(
            "自建 AnythingLLM 知识库，20+ Workspace、400+ 文档同步，封装统一 Memory Agent。",
            "Multi-Agent 内容生产流水线（AI 网文系统），长篇连载持续产出数十章。",
            "星序 StarOS 微信小程序，30+ 页面、10+ 云函数，支持 AI 答疑、AI 出题、联网检索。",
            "浏览器插件、Go 工具接口、记账系统等多个个人项目。",
        ),
        internship = "中电智广（石家庄）  运维实习生 | 2026.07 - 至今\n负责 Linux 服务器日常巡检与故障排查，编写 Shell 自动化脚本，配置 Zabbix 监控告警，部署 Docker / Nginx 测试环境，积累生产环境运维与排障经验。",
        honors = listOf(
            "POCIB 全国外贸从业能力大赛团体三等奖（2025）；三创赛校级二等奖（2025）；河北省计算机应用技能大赛优秀奖（Linux 系统管理方向，2026）。",
        ),
    )
}
