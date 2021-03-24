package com.arvi.Model

class GetWorkShiftListResponse : ArrayList<GetWorkShiftListResponseItem>()

data class GetWorkShiftListResponseItem(
    val createdAt: String,
    val data: WorkShift_Data?,
    val id: Int,
    val name: String,
    val type: String,
    val updatedAt: String
){
    override fun toString(): String {
        return name
    }
}

data class WorkShift_Data(
    val endWorkHour: WorkShift_EndWorkHour,
    val overtimeThreshold: String,
    val startWorkHour: WorkShift_StartWorkHour
)

data class WorkShift_EndWorkHour(
    val max: Int,
    val min: Int,
    val time: String
)

data class WorkShift_StartWorkHour(
    val max: String,
    val min: Int,
    val time: String
)