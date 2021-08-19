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
    val visitingTo: VisitingToX,
    val visitor: GetVisitorData? = null
)

data class GetVisitorImages(
    val path: String,
    val mimetype: String,
    val filename: String
)

data class GetVisitorData(
    val id: Int,
    val mobile: String,
    val name: String,
    val status: Int,
    val picture:String
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
    val picture: String,
    val salt: String,
    val status: Int,
    val updatedAt: String
)

data class GetVisitorListData(
    val addedBy: AddedByX,
    val expectedEntry: ExpectedEntry,
    val actualEntry: ActualEntry,
    val expectedEntryTime: String,
    val actualEntryTime: String,
    val type: String,
    val visitingTo: VisitingTo,
    val visitor: Visitor,
    val comingFrom: String,
    val purpose: String,
    val images: List<GetVisitorImages>

)


data class VisitingToX(
    val createdAt: String,
    val email: String,
    val employeeId: String,
    val id: Int,
    val isAdmin: Int,
    val isSuperAdmin: Int,
    val mobile: String,
    val name: String,
    val password: Any,
    val permissions: Any,
    val picture: String,
    val salt: Any,
    val status: Int,
    val updatedAt: String
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

data class ActualEntry(
    val dateOn: String,
    val timeOn: String
)

data class VisitingTo(
    val employeeId: String,
    val mobile: String,
    val name: String
)

data class Visitor(
    val mobile: String,
    val name: String
)