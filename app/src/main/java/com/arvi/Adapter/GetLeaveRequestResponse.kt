package com.arvi.Adapter

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetLeaveRequestResponse(
    val leaves: List<GetLeaveRequest_Leave>?=null,
    val scheduleCreatedTillDate: String?="",
    val shifts: List<GetLeaveRequest_Shift>?=null
):Parcelable

@Parcelize
data class GetLeaveRequest_Leave(
    val createdAt: String?="",
    val dateFrom: String?="",
    val dateTo: String?="",
    val id: Int?=0,
    val leaveDates: List<String>?=null,
    val reason: String?="",
    val updatedAt: String?="",
    val user: GetLeaveRequest_User?=null
):Parcelable

@Parcelize
data class GetLeaveRequest_Shift(
    val createdAt: String?="",
    val `data`: GetLeaveRequestData?=null,
    val id: Int?=0,
    val name: String?="",
    val type: String?="",
    val updatedAt: String?=""
):Parcelable

@Parcelize
data class GetLeaveRequest_User(
    val createdAt: String?="",
    val email: String?="",
    val employeeId: String?="",
    val id: Int?=0,
    val isAdmin: Int?=0,
    val isSuperAdmin: Int?=0,
    val mobile: String?="",
    val name: String?="",
    val password: String?="",
    val permissions: String?="",
    val picture: String?="",
    val salt: String?="",
    val status: Int?=0,
    val updatedAt: String?=""
):Parcelable

@Parcelize
data class GetLeaveRequestData(
    val endWorkHour: GetLeaveRequest_EndWorkHour?=null,
  /*  val overtimeThreshold: Any,*/
    val startWorkHour: GetLeaveRequest_StartWorkHour?=null
):Parcelable

@Parcelize
data class GetLeaveRequest_EndWorkHour(
    val max: Int?=0,
    val min: Int?=0,
    val time: String?=""
):Parcelable

@Parcelize
data class GetLeaveRequest_StartWorkHour(
    val max: Int?=0,
    val min: Int?=0,
    val time: String?=""
):Parcelable