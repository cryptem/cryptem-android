package io.cryptem.app.model.binance

import io.cryptem.app.model.binance.dto.GetAllResponseItemDto
import retrofit2.http.GET
import retrofit2.http.Query

interface BinanceApiDef {

    @GET("sapi/v1/capital/config/getall")
    suspend fun getAll(
        @Query("timestamp") timestamp: Long? = System.currentTimeMillis(),
        @Query("recvWindow") recvWindow: Long? = 60000,
    ): List<GetAllResponseItemDto>

}