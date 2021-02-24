package com.arvi.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class VisitorsListModel(
    val currentPage: Int?=null,
    val pageSize: Int?=null,
    val result: ArrayList<Result>?=null,
    val total: Int?=null,
    val totalPages: Int
): Parcelable

@Parcelize
data class Result(
    val addedBy: AddedBy?=null,
    val createdAt: String?=null,
    val `data`: Data?=null,
    val dateOn: String?=null,
    val deviceId: String?=null,
    val id: Int?=null,
    val name: String?=null,
    val status: String?=null,
    val timeOn: String?=null,
    val timezone: String?=null,
    val updatedAt: String?=null,
    val visitingTo: String?=null,
    val visitor: Visitor?=null
): Parcelable

@Parcelize
data class AddedBy(
    val createdAt: String?=null,
    val email: String?=null,
    val employeeId: String?=null,
    val id: Int?=null,
    val isAdmin: Int?=null,
    val isSuperAdmin: Int?=null,
    val mobile: String?=null,
    val name: String?=null,
    val password: String?=null,
    val permissions: String?=null,
    val picture: String?=null,
    val salt: String?=null,
    val status: Int?=null,
    val updatedAt: String?=null
): Parcelable

@Parcelize
data class Data(
    val actualEntry: ActualEntry?=null,
    val addedBy: AddedByX?=null,
    val purpose: String?=null,
    val visitingTo: VisitingTo?=null
): Parcelable

@Parcelize
data class Visitor(
    val createdAt: String?=null,
    val `data`: DataX?=null,
    val email: String?=null,
    val id: Int?=null,
    val mobile: String?=null,
    val name: String?=null,
    val permissions: String?=null,
    val picture: String?=null,
    val status: Int?=null,
    val type: String?=null,
    val updatedAt: String?=null
): Parcelable

@Parcelize
data class ActualEntry(
    val dateOn: String?=null,
    val timeOn: String?=null
): Parcelable

@Parcelize
data class AddedByX(
    val employeeId: String?=null,
    val mobile: String?=null,
    val name: String?=null
): Parcelable

@Parcelize
data class VisitingTo(
    val name: String?=null
): Parcelable

@Parcelize
data class DataX(
    val company: String?=null,
    val purpose: String?=null
): Parcelable