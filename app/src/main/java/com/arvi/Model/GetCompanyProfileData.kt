package com.arvi.Model

data class GetCompanyProfileData(
    val company: GetCompanyProfileData_Company,
    val user: GetCompanyProfileData_User
)

data class GetCompanyProfileData_Company(
    val companyId: String,
    val createdAt: String,
    val `data`: Any,
    val faceDetection: GetCompanyProfileData_FaceDetection,
    val id: Int,
    val name: String,
    val status: Int,
    val updatedAt: String
)

data class GetCompanyProfileData_User(
    val address: Any,
    val createdAt: String,
    val email: String,
    val id: Int,
    val isAdmin: Int,
    val mobile: Any,
    val name: String,
    val permissions: Any,
    val role: String,
    val updatedAt: String
)

data class GetCompanyProfileData_FaceDetection(
    val faceCollectionArn: String,
    val faceCollectionId: String,
    val faceCollectionStatus: Int,
    val faceModelVersion: String
)