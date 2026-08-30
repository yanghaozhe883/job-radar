package com.jobradar.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "companies")
class CompanyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,

    @Column(name = "logo_url")
    val logoUrl: String? = null,

    val industry: String? = null,

    val size: String? = null,

    val city: String? = null,

    @Column(name = "financing_stage")
    val financingStage: String? = null,

    val description: String? = null,
)
