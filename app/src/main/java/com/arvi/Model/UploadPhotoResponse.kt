package com.arvi.Model

data class UploadPhotoResponse(
    val `data`: ArrayList<UploadPhotoData>?=null
)

data class UploadPhotoData(
    val filename: String,
    val mimetype: String,
    val path: String
)