package io.cryptem.app.model.coingecko

import io.cryptem.app.model.coingecko.dto.CoinsResponseItemDto
import io.cryptem.app.model.coingecko.dto.GlobalResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoApiDef {

    @GET("api/v3/coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") currency: String,
        @Query("ids") ids: String? = null,
        @Query("price_change_percentage") priceChangePercentage: String = "24h,7d,30d",
        @Query("per_page") perPage : Int? = 250,
        @Query("page") page : Int? = null,
    ): List<CoinsResponseItemDto>

    @GET("api/v3/simple/price")
    suspend fun getCoinsPrice(
        @Query("vs_currencies") currency: String,
        @Query("ids") ids: String
    ): Map<String, Map<String, Double>>

    @GET("api/v3/global")
    suspend fun getGlobalMarketData() : GlobalResponseDto


}