package com.arvi.Model

class GetAttendanceSummaryResponse : ArrayList<GetAttendanceSummaryResponseItem>()

data class GetAttendanceSummaryResponseItem(
    val color: String,
    val label: String,
    val metric: String,
    val text: String
)