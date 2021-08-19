package com.arvi.Model

class GetDesignationListResponse : ArrayList<GetDesignationListResponseItem>()

data class GetDesignationListResponseItem(
    val createdAt: String,
    val id: Int,
    val idDefault: Int,
    val name: String,
    val permissions: Any,
    val role: String,
    val updatedAt: String
){
    override fun toString(): String {
        return role
    }
}