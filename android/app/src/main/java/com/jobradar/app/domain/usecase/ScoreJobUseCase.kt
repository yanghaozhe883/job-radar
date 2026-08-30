package com.jobradar.app.domain.usecase

import com.jobradar.app.domain.model.Company
import com.jobradar.app.domain.model.ExperienceLevel
import com.jobradar.app.domain.model.Job
import com.jobradar.app.domain.model.JobType
import com.jobradar.app.domain.model.MatchScore
import com.jobradar.app.domain.model.UserPreference
import javax.inject.Inject

/**
 * The radar matching engine. A pure, domain-only function — no Android types.
 *
 * It scores a [Job] against the [UserPreference] and produces a weighted
 * [MatchScore] plus a human-readable reason. Designed to be independently
 * unit-testable and, later, replaceable by a vector/semantic backend while the
 * UI contract stays identical.
 */
class ScoreJobUseCase @Inject constructor() {

    operator fun invoke( job: Job, preference: UserPreference): MatchScore {
        val salary = scoreSalary(job, preference)
        val skill = scoreSkill(job, preference)
        val company = scoreCompany(job, preference)
        val total = (salary + skill + company).coerceIn(0, 100)
        return MatchScore(
            total = total,
            salaryMatch = salary,
            skillMatch = skill,
            companyMatch = company,
            reason = buildReason(total, job, preference),
        )
    }

    /** 0..40 — do the pay ranges overlap and is it above our floor? */
    private fun scoreSalary(job: Job, p: UserPreference): Int {
        val jobMin = job.salaryMinK
        val jobMax = job.salaryMaxK
        val overlaps = jobMax >= p.salaryMinK && jobMin <= p.salaryMaxK
        if (!overlaps) return 8
        val midpoint = (jobMax + jobMin) / 2
        val target = (p.salaryMaxK + p.salaryMinK) / 2
        return when {
            midpoint >= target -> 40
            midpoint >= target * 0.85 -> 32
            midpoint >= target * 0.7 -> 24
            else -> 16
        }
    }

    /** 0..35 — skill overlap as a Jaccard-like ratio against target roles+skills. */
    private fun scoreSkill(job: Job, p: UserPreference): Int {
        val jobText = buildString {
            append(job.title); append(' ')
            job.skills.forEach { append(it); append(' ') }
            job.description?.let { append(it) }
        }.lowercase()
        val targets = (p.targetRoles + p.skillTags).filter { it.isNotBlank() }
        if (targets.isEmpty()) return 20
        val hitCount = targets.count { it.lowercase() in jobText }
        val ratio = hitCount.toFloat() / targets.size
        return (ratio * 35).toInt().coerceIn(0, 35)
    }

    /** 0..25 — company attractiveness/recency heuristic. */
    private fun scoreCompany(job: Job, p: UserPreference): Int {
        var score = 12 // base
        val company: Company? = job.company
        if (company != null) {
            if (p.preferredCompanies.any { it.equals(company.name, ignoreCase = true) }) score += 10
            when (company.industry) {
                "互联网", "人工智能", "金融科技" -> score += 4
                null -> {}
                else -> score += 1
            }
        }
        if (job.jobType == JobType.FULL_TIME || job.jobType == JobType.REMOTE) score += 2
        if (job.experience == ExperienceLevel.JUNIOR || job.experience == ExperienceLevel.MID) score += 2
        return score.coerceIn(0, 25)
    }

    private fun buildReason(total: Int, job: Job, p: UserPreference): String = when {
        total >= 80 -> "匹配度极高：薪资、技能与你的目标高度契合"
        total >= 60 -> "匹配度良好，主要技能方向一致"
        total >= 40 -> "有一定相关性，可考虑作为备选机会"
        else -> "部分领域有交集，建议结合自身判断"
    }
}
