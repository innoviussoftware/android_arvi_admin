package com.arvi.Model

 data class CompaniesUsersResponse(
    val currentPage: Int?=null,
    val pageSize: Int?=null,
    val result: List<CompaniesUsersResult>?=null,
    val total: Int?=null,
    val totalPages: Int?=null
)

data class CompaniesUsersResult(
    val createdAt: String?=null,
    val email: String?=null,
    val employeeId: String?=null,
    val group: CompaniesUsersGroup?=null,
    val id: Int?=null,
    val isAdmin: Int?=null,
    val isSuperAdmin: Int?=null,
    val mobile: String?=null,
    val name: String?=null,
    val password: String?=null,
    val permissions: String?=null,
    val picture: String?=null,
    val role: CompaniesUsersRole?=null,
    val salt: String?=null,
    val status: Int?=null,
    val updatedAt: String?=null
)

data class CompaniesUsersGroup(
    val createdAt: String?=null,
    val date: String?=null,
    val group: String?=null,
    val id: Int?=null,
    val idDefault: Int?=null,
    val name: String?=null,
    val updatedAt: String?=null
)

data class CompaniesUsersRole(
    val createdAt: String?=null,
    val id: Int?=null,
    val idDefault: Int?=null,
    val name: String?=null,
    val permissions: String?=null,
    val role: String?=null,
    val updatedAt: String?=null
)