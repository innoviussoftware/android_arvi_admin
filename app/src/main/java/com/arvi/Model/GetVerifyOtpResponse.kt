package com.arvi.Model

data class GetVerifyOtpResponse(
    val accessToken: String,
    val success: Boolean,
    val userExisted: Boolean
)