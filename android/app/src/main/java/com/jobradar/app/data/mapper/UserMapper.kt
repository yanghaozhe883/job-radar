package com.jobradar.app.data.mapper

import com.jobradar.app.data.remote.dto.PreferenceDto
import com.jobradar.app.data.remote.dto.UserDto
import com.jobradar.app.domain.model.User
import com.jobradar.app.domain.model.UserPreference

fun UserDto.toDomain() = User(
    id = id,
    phone = phone,
    nickname = nickname,
    avatarUrl = avatarUrl,
)

fun UserPreference.toDto() = PreferenceDto(
    city = city,
    targetRoles = targetRoles,
    skillTags = skillTags,
    salaryMinK = salaryMinK,
    salaryMaxK = salaryMaxK,
    preferredJobTypes = preferredJobTypes,
    yearsOfExperience = yearsOfExperience,
    preferredCompanies = preferredCompanies,
)

fun PreferenceDto.toDomain() = UserPreference(
    city = city,
    targetRoles = targetRoles,
    skillTags = skillTags,
    salaryMinK = salaryMinK,
    salaryMaxK = salaryMaxK,
    preferredJobTypes = preferredJobTypes,
    yearsOfExperience = yearsOfExperience,
    preferredCompanies = preferredCompanies,
)
