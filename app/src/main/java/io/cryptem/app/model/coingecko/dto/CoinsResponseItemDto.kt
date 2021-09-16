package io.cryptem.app.model.coingecko.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class CoinsResponseItemDto(
    @Json(name = "ath")
    val ath: Double?,
    @Json(name = "ath_change_percentage")
    val athChangePercentage: Double?,
    @Json(name = "ath_date")
    val athDate: Date?,
    @Json(name = "atl")
    val atl: Double?,
    @Json(name = "atl_change_percentage")
    val atlChangePercentage: Double?,
    @Json(name = "atl_date")
    val atlDate: String?,
    @Json(name = "circulating_supply")
    val circulatingSupply: Double?,
    @Json(name = "current_price")
    val currentPrice: Double,
    @Json(name = "fully_diluted_valuation")
    val fullyDilutedValuation: Double?,
    @Json(name = "high_24h")
    val high24h: Double?,
    @Json(name = "id")
    val id: String,
    @Json(name = "image")
    val image: String?,
    @Json(name = "last_updated")
    val lastUpdated: String?,
    @Json(name = "low_24h")
    val low24h: Double?,
    @Json(name = "market_cap")
    val marketCap: Double?,
    @Json(name = "market_cap_change_24h")
    val marketCapChange24h: Double?,
    @Json(name = "market_cap_change_percentage_24h")
    val marketCapChangePercentage24h: Double?,
    @Json(name = "market_cap_rank")
    val marketCapRank: Int,
    @Json(name = "max_supply")
    val maxSupply: Double?,
    @Json(name = "name")
    val name: String,
    @Json(name = "price_change_24h")
    val priceChange24h: Double?,
    @Json(name = "price_change_percentage_24h")
    val priceChangePercentage24h: Double?,
    @Json(name = "price_change_percentage_24h_in_currency")
    val priceChangePercentage24hInCurrency: Double?,
    @Json(name = "price_change_percentage_30d_in_currency")
    val priceChangePercentage30dInCurrency: Double?,
    @Json(name = "price_change_percentage_7d_in_currency")
    val priceChangePercentage7dInCurrency: Double?,
    @Json(name = "price_change_percentage_1y_in_currency")
    val priceChangePercentage1yInCurrency: Double?,
    @Json(name = "roi")
    val roi: RoiDto?,
    @Json(name = "symbol")
    val symbol: String,
    @Json(name = "total_supply")
    val totalSupply: Double?,
    @Json(name = "total_volume")
    val totalVolume: Double?
)