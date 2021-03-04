package com.arvi.Model

data class GetVisitorListResponse(
    val currentPage: Int,
    val pageSize: Int,
    val result: List<GetVisitorListResult>,
    val total: Int,
    val totalPages: Int
)

data class GetVisitorListResult(
    val addedBy: AddedBy,
    val createdAt: String,
    val `data`: GetVisitorListData,
    val dateOn: String,
    val deviceId: Any,
    val id: Int,
    val name: String,
    val status: String,
    val timeOn: String,
    val timezone: String,
    val updatedAt: String,
    val visitingTo: Any,
    val visitor: Any
)

data class AddedBy(
    val createdAt: String,
    val email: String,
    val employeeId: String,
    val id: Int,
    val isAdmin: Int,
    val isSuperAdmin: Int,
    val mobile: String,
    val name: String,
    val password: String,
    val permissions: Any,
    val picture: String?=null,
    val salt: String,
    val status: Int,
    val updatedAt: String
)

data class GetVisitorListData(
    val addedBy: AddedByX,
    val expectedEntry: ExpectedEntry,
    val expectedEntryTime: String,
    val visitingTo: VisitingTo,
    val visitor: Visitor,
    val purpose:String,
    val company:String
)

data class AddedByX(
    val employeeId: String,
    val mobile: String,
    val name: String
)

data class ExpectedEntry(
    val dateOn: String,
    val timeOn: String
)

data class VisitingTo(
    val name: String
)

data class Visitor(
    val mobile: String,
    val name: String
)