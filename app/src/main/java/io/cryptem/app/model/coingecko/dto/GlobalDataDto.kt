package io.cryptem.app.model.coingecko.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GlobalDataDto(
    @Json(name = "active_cryptocurrencies")
    val activeCryptocurrencies: Int?,
    @Json(name = "ended_icos")
    val endedIcos: Int?,
    @Json(name = "market_cap_change_percentage_24h_usd")
    val marketCapChangePercentage24hUsd: Double?,
    @Json(name = "market_cap_percentage")
    val marketCapPercentage: Map<String, Double>,
    @Json(name = "markets")
    val markets: Int?,
    @Json(name = "ongoing_icos")
    val ongoingIcos: Int?,
    @Json(name = "total_market_cap")
    val totalMarketCap: Map<String, Double>,
    @Json(name = "total_volume")
    val totalVolume: Map<String, Double>?,
    @Json(name = "upcoming_icos")
    val upcomingIcos: Int?,
    @Json(name = "updated_at")
    val updatedAt: Int?
)