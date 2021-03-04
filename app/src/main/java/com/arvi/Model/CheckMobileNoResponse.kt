package com.arvi.Model

data class CheckMobileNoResponse(
    val currentPage: Int,
    val pageSize: Int,
    val result: List<CheckMobileNoResult>,
    val total: Int,
    val totalPages: Int
)

data class CheckMobileNoResult(
    val createdAt: String,
    val `data`: Any,
    val email: String,
    val id: Int,
    val mobile: String,
    val name: String,
    val permissions: String,
    val picture: Any,
    val status: Int,
    val type: String,
    val updatedAt: String
)