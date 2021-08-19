package com.arvi.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class GetRegularisationRequestResponse : ArrayList<GetRegularisationRequestResponseItem>()

@Parcelize
data class GetRegularisationRequestResponseItem(
    val createdAt: String?="",
    val dateOn: String?="",
    val id: Int?=0,
    val inAt: String?="",
    val outAt: String?="",
    val regularization: Regularization?=null,
    val status: String?="",
    val updatedAt: String?="",
    val user: Regularisation_User?=null,
    val workHours: String?=""
):Parcelable

@Parcelize
data class Regularization(
    val `in`: Boolean?=false,
    val `out`: Boolean?=false
):Parcelable

@Parcelize
data class Regularisation_User(
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