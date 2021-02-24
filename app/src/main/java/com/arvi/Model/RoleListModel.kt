package com.arvi.Model

class RoleListModel : ArrayList<RoleListModelItem>()

data class RoleListModelItem(
    val createdAt: String,
    val id: Int,
    val idDefault: Int,
    val name: String,
    val permissions: String,
    val role: String,
    val updatedAt: String

) {
    override fun toString(): String {
        return name
    }
}