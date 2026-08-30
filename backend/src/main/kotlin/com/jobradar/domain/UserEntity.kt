package com.jobradar.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

/** A registered user. `id` is the stable key referenced by the client. */
@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true)
    val phone: String,

    val nickname: String? = null,

    val avatarUrl: String? = null,

    @Column(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)
