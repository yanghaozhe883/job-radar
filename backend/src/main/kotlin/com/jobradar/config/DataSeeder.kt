package com.jobradar.config

import com.jobradar.domain.CompanyEntity
import com.jobradar.domain.JobEntity
import com.jobradar.repo.CompanyRepository
import com.jobradar.repo.JobRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Demo seeder — ONLY active when `demo.seed=true`.
 *
 * This is sample data for local/dev, explicitly separated from the real job
 * pipeline. By default it is OFF, so in production the job endpoints serve real
 * (scraped/ingested) data. Enable for a zero-setup demo:
 *   --demo.seed=true
 */
@Configuration
@ConditionalOnProperty(name = ["demo.seed"], havingValue = "true")
class DataSeeder {

    @Bean
    fun seed(repo: JobRepository, companyRepo: CompanyRepository): CommandLineRunner =
        CommandLineRunner {
            if (repo.count() > 0) return@CommandLineRunner

            val hexing = companyRepo.save(
                CompanyEntity(name = "北辰科技", industry = "互联网", size = "100-499", city = "上海", description = "专注求职大数据与 AI 匹配的科技公司。")
            )
            val yunqi = companyRepo.save(
                CompanyEntity(name = "云启科技", industry = "人工智能", size = "500-999", city = "杭州")
            )
            val shenlan = companyRepo.save(
                CompanyEntity(name = "深蓝智能", industry = "人工智能", size = "1000+", city = "北京")
            )
            val xinghai = companyRepo.save(
                CompanyEntity(name = "星海信息", industry = "金融科技", size = "100-499", city = "深圳")
            )

            repo.saveAll(
                listOf(
                    JobEntity(title = "Android 工程师", city = "上海", salaryMinK = 25, salaryMaxK = 40,
                        skills = mutableListOf("Kotlin", "Jetpack Compose", "Clean Architecture", "Hilt", "Android"),
                        description = "负责求职雷达 App 的架构设计与核心功能开发，打造行业顶级 UI/UX。要求精通 Kotlin、Jetpack Compose、Clean Architecture 与单向数据流。",
                        company = hexing, matchScore = 92, publishedAt = 1_700_000_000_000),
                    JobEntity(title = "前端开发工程师", city = "杭州", salaryMinK = 20, salaryMaxK = 35,
                        skills = mutableListOf("TypeScript", "React", "Vite", "WebGL"), matchScore = 78,
                        description = "负责数据可视化与炫技级交互页面，追求极致动画与性能。",
                        company = yunqi, publishedAt = 1_700_010_000_000),
                    JobEntity(title = "算法工程师", city = "北京", salaryMinK = 35, salaryMaxK = 55,
                        skills = mutableListOf("Python", "PyTorch", "NLP", "RAG"), matchScore = 85,
                        description = "研发职位匹配与语义向量检索，构建智能雷达推荐引擎。",
                        company = shenlan, publishedAt = 1_700_020_000_000),
                    JobEntity(title = "Flutter 开发工程师", city = "深圳", salaryMinK = 22, salaryMaxK = 38,
                        skills = mutableListOf("Flutter", "Dart", "Clean Architecture", "BLoC"), matchScore = 60,
                        description = "远程协作，跨端产品研发，注重代码质量与工程化。",
                        company = xinghai, publishedAt = 1_700_030_000_000),
                    JobEntity(title = "产品经理", city = "上海", salaryMinK = 25, salaryMaxK = 45,
                        skills = mutableListOf("产品规划", "用户研究", "数据驱动", "AIGC"), matchScore = 70,
                        description = "负责求职雷达的核心产品迭代，深入理解求职者痛点。",
                        company = hexing, publishedAt = 1_700_040_000_000),
                    JobEntity(title = "iOS 开发工程师", city = "上海", salaryMinK = 26, salaryMaxK = 42,
                        skills = mutableListOf("Swift", "SwiftUI", "Metal", "动画"), matchScore = 88,
                        description = "原生 iOS 顶级 UI/UX 打磨，动效与性能优化。",
                        company = hexing, publishedAt = 1_700_050_000_000),
                )
            )
        }
}
