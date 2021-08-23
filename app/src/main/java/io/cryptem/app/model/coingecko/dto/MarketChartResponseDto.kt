package io.cryptem.app.model.coingecko.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarketChartResponseDto(
    @Json(name = "market_caps")
    val marketCaps: List<List<Double>>?,
    @Json(name = "prices")
    val prices: List<List<Double>>?,
    @Json(name = "total_volumes")
    val totalVolumes: List<List<Double>>?
)