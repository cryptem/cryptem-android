package io.cryptem.app.model.api.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoinDto(
    @Json(name = "id")
    val id: Long,
    @Json(name = "rank")
    val rank: Int,
    @Json(name = "symbol")
    val symbol: String,
    @Json(name = "slug")
    val slug: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "last_update")
    val lastUpdate: String?,
    @Json(name = "price_btc")
    val priceBtc: Double,
    @Json(name = "price_usd")
    val priceUsd: Double,
    @Json(name = "price_eur")
    val priceEur: Double,
    @Json(name = "price_czk")
    val priceCzk: Double,
    @Json(name = "percent_1d_btc")
    val percentDayBtc: Double?,
    @Json(name = "percent_7d_btc")
    val percentWeekBtc: Double?,
    @Json(name = "percent_30d_btc")
    val percentMonthBtc: Double?,
    @Json(name = "percent_90d_btc")
    val percentQuarterBtc: Double?,
    @Json(name = "percent_1d_usd")
    val percentDayUsd: Double?,
    @Json(name = "percent_7d_usd")
    val percentWeekUsd: Double?,
    @Json(name = "percent_30d_usd")
    val percentMonthUsd: Double?,
    @Json(name = "percent_90d_usd")
    val percentQuarterUsd: Double?,
)