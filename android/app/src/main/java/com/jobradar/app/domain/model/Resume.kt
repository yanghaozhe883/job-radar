package com.jobradar.app.domain.model

/** Structured resume model mirroring the finalized resume. Pure domain. */
data class Resume(
    val name: String,
    val tagline: String,
    val contact: String,
    val education: String,
    val skills: List<ResumeSkill>,
    val projects: List<ResumeProject>,
    val otherProjects: List<String>,
    val internship: String?,
    val honors: List<String>,
)

data class ResumeSkill(val label: String, val detail: String)

data class ResumeProject(
    val title: String,
    val meta: String,
    val flagship: Boolean = false,
    val bullets: List<String>,
)
