package com.arvi.Adapter

data class GetLeaveRequestResponse(
    val leaves: List<GetLeaveRequest_Leave>,
    val scheduleCreatedTillDate: String,
    val shifts: List<GetLeaveRequest_Shift>
)

data class GetLeaveRequest_Leave(
    val createdAt: String,
    val dateFrom: String,
    val dateTo: String,
    val id: Int,
    val leaveDates: List<String>,
    val reason: String,
    val updatedAt: String,
    val user: GetLeaveRequest_User
)

data class GetLeaveRequest_Shift(
    val createdAt: String,
    val `data`: GetLeaveRequestData,
    val id: Int,
    val name: String,
    val type: String,
    val updatedAt: String
)

data class GetLeaveRequest_User(
    val createdAt: String,
    val email: String,
    val employeeId: String,
    val id: Int,
    val isAdmin: Int,
    val isSuperAdmin: Int,
    val mobile: String,
    val name: String,
    val password: Any,
    val permissions: Any,
    val picture: String,
    val salt: Any,
    val status: Int,
    val updatedAt: String
)

data class GetLeaveRequestData(
    val endWorkHour: GetLeaveRequest_EndWorkHour,
    val overtimeThreshold: Any,
    val startWorkHour: GetLeaveRequest_StartWorkHour
)

data class GetLeaveRequest_EndWorkHour(
    val max: Int,
    val min: Int,
    val time: String
)

data class GetLeaveRequest_StartWorkHour(
    val max: Any,
    val min: Int,
    val time: String
)