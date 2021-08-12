package io.cryptem.app.model.binance.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BalanceDto(
    @Json(name = "asset")
    val asset: String,
    @Json(name = "free")
    val free: Double,
    @Json(name = "locked")
    val locked: Double
)