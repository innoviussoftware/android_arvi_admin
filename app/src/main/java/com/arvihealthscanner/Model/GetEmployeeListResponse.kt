package com.arvihealthscanner.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
data class GetEmployeeListResponse(
    val currentPage: Int?=0,
    val pageSize: String?="",
    val result: List<GetEmployeeListResult>?=null,
    val total: Int?=0,
    val totalPages: Int
):Parcelable

@Parcelize
data class GetEmployeeListResult(
    val createdAt: String?="",
    val email: String?="",
    val employeeId: String?="",
    val group: GetEmployeeListGroup?=null,
    val id: Int?=0,
    val isAdmin: Int?=0,
    val isSuperAdmin: Int?=0,
    val mobile: String?="",
    val name: String?="",
    val password: String?="",
    val picture: String?="",
    val role: GetEmployeeListRole?=null,
    val salt: String?="",
    val status: Int?=0,
    val updatedAt: String
):Parcelable{
    override fun toString(): String {
        return name!!+" - "+employeeId
    }
}

@Parcelize
data class GetEmployeeListGroup(
    val createdAt: String?="",
    val date:String?="",
    val group: String?="",
    val id: Int?=0,
    val idDefault: Int?=0,
    val name: String?="",
    val updatedAt: String
):Parcelable

@Parcelize
data class GetEmployeeListRole(
    val createdAt: String?="",
    val id: Int?=0,
    val idDefault: Int?=0,
    val name: String?="",
    val role: String?="",
    val updatedAt: String
):Parcelable