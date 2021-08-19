package com.arvi.Model

data class NewVisitorRegisterResponse(
    val entry: Any,
    val isExisting: Boolean,
    val visitor: NewVisitorVisitor
)

data class NewVisitorVisitor(
    val company: Company,
    val createdAt: String,
    val `data`: Any,
    val email: Any,
    val id: Int,
    val mobile: String,
    val name: String,
    val permissions: Any,
    val picture: Any,
    val status: Int,
    val type: Any,
    val updatedAt: String
)

data class Company(
    val id: Int
)