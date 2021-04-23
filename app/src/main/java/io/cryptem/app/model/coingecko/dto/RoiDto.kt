package io.cryptem.app.model.coingecko.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RoiDto(
    @Json(name = "currency")
    val currency: String?,
    @Json(name = "percentage")
    val percentage: Double?,
    @Json(name = "times")
    val times: Double?
)