package io.cryptem.app.model.binance.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetAllResponseItemDto(
    @Json(name = "coin")
    val coin: String,
    @Json(name = "free")
    val free: Double,
    @Json(name = "freeze")
    val freeze: Double,
    @Json(name = "locked")
    val locked: Double,
    @Json(name = "name")
    val name: String?,
    @Json(name = "storage")
    val storage: Double,
    @Json(name = "withdrawing")
    val withdrawing: Double,
)