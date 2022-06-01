package io.cryptem.app.model.binance

import io.cryptem.app.model.binance.dto.GetAllResponseItemDto
import io.cryptem.app.model.binance.dto.GetSystemTimeResponseDtoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface BinanceApiDef {

    @GET("sapi/v1/capital/config/getall")
    suspend fun getAll(
        @Query("timestamp") timestamp: Long?,
        @Query("recvWindow") recvWindow: Long? = 60000,
    ): List<GetAllResponseItemDto>

}