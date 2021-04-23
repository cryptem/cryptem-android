package io.cryptem.app.model.coingecko.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GlobalResponseDto(
    @Json(name = "data")
    val data: GlobalDataDto?
)