package com.arvi.Model

class GetGroupListResponse : ArrayList<GetGroupListResponseItem>()

data class GetGroupListResponseItem(
    val createdAt: String,
    val date: Any,
    val group: String,
    val id: Int,
    val idDefault: Int,
    val name: String,
    val updatedAt: String
){
    override fun toString(): String {
        return name
    }
}