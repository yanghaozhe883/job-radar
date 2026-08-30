package com.jobradar.domain

import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "jobs")
class JobEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val title: String,

    val city: String,

    @Column(name = "salary_min_k")
    val salaryMinK: Int,

    @Column(name = "salary_max_k")
    val salaryMaxK: Int,

    @Column(name = "job_type")
    val jobType: String = "全职",

    val experience: String = "1-3年",

    val education: String = "本科",

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "skill")
    val skills: MutableList<String> = mutableListOf(),

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    val company: CompanyEntity? = null,

    @Column(name = "data_source")
    val dataSource: String? = null,

    /** Stable external id from the source, used for de-duplication. */
    @Column(name = "source_external_id")
    val sourceExternalId: String? = null,

    @Column(name = "published_at")
    val publishedAt: Long? = null,

    @Column(name = "match_score")
    val matchScore: Int = 0,
)
