package io.cryptem.app.model.api.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoinsResponseDto(
    @Json(name = "coins")
    val coins: List<CoinDto>
)