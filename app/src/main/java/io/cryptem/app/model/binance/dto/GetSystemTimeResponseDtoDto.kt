package io.cryptem.app.model.binance.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetSystemTimeResponseDtoDto(
    @Json(name = "code")
    val code: Int?,
    @Json(name = "data")
    val `data`: Long?,
    @Json(name = "msg")
    val msg: String?
)