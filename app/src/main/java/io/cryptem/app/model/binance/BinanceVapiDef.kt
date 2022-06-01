package io.cryptem.app.model.binance

import io.cryptem.app.model.binance.dto.GetAllResponseItemDto
import io.cryptem.app.model.binance.dto.GetSystemTimeResponseDtoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface BinanceVapiDef {

    @GET("vapi/v1/time")
    suspend fun getServerTime() : GetSystemTimeResponseDtoDto

}