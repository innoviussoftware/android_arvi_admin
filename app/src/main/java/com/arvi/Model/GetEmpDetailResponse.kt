package com.arvi.Model

data class GetEmpDetailResponse(
    val createdAt: String,
    val email: String,
    val employeeId: String,
    val faces: List<GetEmpDetail_Face>,
    val group:  GetEmpDetail_Group,
    val id: Int,
    val isAdmin: Int,
    val isSuperAdmin: Int,
    val mobile: String,
    val name: String,
    val password: Any,
    val permissions: Any,
    val picture: String,
    val role: GetEmpDetail_Role,
    val salt: Any,
    val status: Int,
    val updatedAt: String
)

data class GetEmpDetail_Face(
    val createdAt: String,
    val `data`:  GetEmpDetail_Data,
    val externalImageId: String,
    val faceId: String,
    val id: Int,
    val imageId: String,
    val updatedAt: String
)

data class GetEmpDetail_Group(
    val createdAt: String,
    val date: Any,
    val group: String,
    val id: Int,
    val idDefault: Int,
    val name: String,
    val updatedAt: String
)

data class GetEmpDetail_Role(
    val createdAt: String,
    val id: Int,
    val idDefault: Int,
    val name: String,
    val permissions: Any,
    val role: String,
    val updatedAt: String
)

data class GetEmpDetail_Data(
    val Confidence: Double,
    val path: String
)