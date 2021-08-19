package com.arvi.Model

data class DetectFaceNewResponse(
    val `data`: DetectFaceNewData
)

data class DetectFaceNewData(
    val createdAt: String,
    val email: String,
    val employeeId: String,
    val id: Int,
    val isAdmin: Int,
    val isSuperAdmin: Int,
    val mobile: String,
    val name: String,
    val permissions: Any,
    val picture: String,
    val roleId: Int,
    val status: Int,
    val token: Any,
    val updatedAt: String,
    val userId: String
)