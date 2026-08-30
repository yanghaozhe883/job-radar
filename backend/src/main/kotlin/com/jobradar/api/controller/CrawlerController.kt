package com.jobradar.api.controller

import com.jobradar.api.ApiResponse
import com.jobradar.crawler.JobIngestionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** Manual trigger + status for the job ingestion pipeline (admin/demo). */
@RestController
@RequestMapping("\${api.base-path:/api/v1}/crawler")
class CrawlerController(
    private val ingestionService: JobIngestionService,
) {

    /** Run one ingestion cycle now. Returns the count of new jobs. */
    @PostMapping("/run")
    fun run(): ApiResponse<Map<String, Int>> =
        ApiResponse.ok(mapOf("inserted" to ingestionService.runOnce()))

    @GetMapping("/status")
    fun status(): ApiResponse<Map<String, Any>> =
        ApiResponse.ok(
            mapOf(
                "endpoint" to "POST /api/v1/crawler/run",
                "note" to "合规采集：仅公开数据、限流、User-Agent 标识、尊重 robots、dataSource 留痕",
            )
        )
}
