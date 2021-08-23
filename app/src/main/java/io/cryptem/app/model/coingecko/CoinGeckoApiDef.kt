package io.cryptem.app.model.coingecko

import io.cryptem.app.model.coingecko.dto.CoinsResponseItemDto
import io.cryptem.app.model.coingecko.dto.GlobalResponseDto
import io.cryptem.app.model.coingecko.dto.MarketChartResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoApiDef {

    @GET("api/v3/coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") currency: String,
        @Query("ids") ids: String? = null,
        @Query("price_change_percentage") priceChangePercentage: String = "24h,7d,30d",
        @Query("per_page") perPage : Int? = PAGE_SIZE,
        @Query("page") page : Int? = null,
    ): List<CoinsResponseItemDto>

    @GET("api/v3/simple/price")
    suspend fun getCoinsPrice(
        @Query("vs_currencies") currency: String,
        @Query("ids") ids: String
    ): Map<String, Map<String, Double>>

    @GET("api/v3/global")
    suspend fun getGlobalMarketData() : GlobalResponseDto

    @GET("api/v3/coins/{id}/market_chart")
    suspend fun getMarketChart(@Path("id") id : String, @Query("vs_currency") currency: String, @Query("days") days : Int) : MarketChartResponseDto

    companion object{
        const val PAGE_SIZE = 100
    }
}