package com.arvi.Model

class GetEmpDayDetailResponse : ArrayList<GetEmpDayDetailResponseItem>()

data class GetEmpDayDetailResponseItem(
    val check_in: String,
    val check_out: String,
    val emp_group: Any,
    val emp_id: String,
    val emp_name: String,
    val emp_role: String,
    val regularization: Any,
    val status: String,
    val workHours: String
)