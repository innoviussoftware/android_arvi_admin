package com.arvi.Model

data class UserLoginResponse(
    val accessToken: String,
    val company: UserLoginCompany,
    val user: UserLoginUser
)

data class UserLoginCompany(
    val companyId: String,
    val createdAt: String,
    val `data`: Any,
    val faceDetection: FaceDetection,
    val id: Int,
    val name: String,
    val status: Int,
    val updatedAt: String
)

data class UserLoginUser(
    val address: Any,
    val company: CompanyX,
    val createdAt: String,
    val email: String,
    val id: Int,
    val isAdmin: Int,
    val mobile: Any,
    val name: String,
    val password: String,
    val permissions: Any,
    val role: String,
    val salt: String,
    val updatedAt: String
)

data class FaceDetection(
    val faceCollectionArn: String,
    val faceCollectionId: String,
    val faceCollectionStatus: Int,
    val faceModelVersion: String
)

data class CompanyX(
    val companyId: String,
    val createdAt: String,
    val `data`: Any,
    val faceDetection: FaceDetectionX,
    val id: Int,
    val name: String,
    val status: Int,
    val updatedAt: String
)

data class FaceDetectionX(
    val faceCollectionArn: String,
    val faceCollectionId: String,
    val faceCollectionStatus: Int,
    val faceModelVersion: String
)