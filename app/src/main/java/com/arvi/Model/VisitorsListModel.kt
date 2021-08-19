package com.arvi.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VisitorsListModel(
    val currentPage: Int?=null,
    val pageSize: Int?=null,
    val result: List<Result>?=null,
    val total: Int?=null,
    val totalPages: Int?=null
): Parcelable

@Parcelize
data class Result(
    val createdAt: String?=null,
    val `data`: Data?=null,
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
data class Data(
    val actualEntryTime: String?=null,
    val employee: Employee?=null,
    val purpose: String?=null
): Parcelable

@Parcelize
data class Employee(
    val company: String?=null,
    val name: String?=null
): Parcelable