package com.arvi.Model

data class GetKeyMetricsResponse(
    val avgHours: GetKeyMetrics_AvgHours,
    val presentMenCount: GetKeyMetrics_PresentMenCount,
    val workDays: GetKeyMetrics_WorkDays,
    val workHours: GetKeyMetrics_WorkHours
)

data class GetKeyMetrics_AvgHours(
    val label: String,
    val value: String
)

data class GetKeyMetrics_PresentMenCount(
    val label: String,
    val value: String
)

data class GetKeyMetrics_WorkDays(
    val label: String,
    val value: Int
)

data class GetKeyMetrics_WorkHours(
    val label: String,
    val value: String
)