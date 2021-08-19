package com.arvi.Model

data class KioskQRGetResponse(
    val kioskId: String,
    val qrCode: String,
    val recordId: Int
)