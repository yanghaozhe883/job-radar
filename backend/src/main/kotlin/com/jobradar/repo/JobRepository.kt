package com.jobradar.repo

import com.jobradar.domain.CompanyEntity
import com.jobradar.domain.JobEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface JobRepository : JpaRepository<JobEntity, Long> {

    fun findByCity(city: String): List<JobEntity>

    @Query(
        """
        SELECT j FROM JobEntity j
        WHERE (:city IS NULL OR j.city = :city)
          AND (:keyword IS NULL OR lower(j.title) LIKE lower(concat('%', :keyword, '%')))
          AND (:jobType IS NULL OR j.jobType = :jobType)
          AND (:minSalaryK IS NULL OR j.salaryMaxK >= :minSalaryK)
        ORDER BY j.matchScore DESC, j.publishedAt DESC
        """
    )
    fun search(
        @Param("city") city: String?,
        @Param("keyword") keyword: String?,
        @Param("jobType") jobType: String?,
        @Param("minSalaryK") minSalaryK: Int?,
    ): List<JobEntity>

    /** Radar picks the newest published jobs. */
    fun findByOrderByPublishedAtDesc(): List<JobEntity>

    fun findByDataSourceAndSourceExternalId(dataSource: String, sourceExternalId: String): java.util.Optional<JobEntity>
}

interface CompanyRepository : JpaRepository<CompanyEntity, Long> {
    fun findByName(name: String): java.util.Optional<CompanyEntity>
}
