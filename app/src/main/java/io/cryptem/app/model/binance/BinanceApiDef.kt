package io.cryptem.app.model.binance

import io.cryptem.app.model.binance.dto.AccountSnapshotResponseDto
import io.cryptem.app.model.coingecko.dto.CoinsResponseItemDto
import io.cryptem.app.model.coingecko.dto.GlobalResponseDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BinanceApiDef {

    @GET("sapi/v1/accountSnapshot")
    suspend fun getAccountSnapshot(
        @Query("type") type: String = "SPOT",
        @Query("timestamp") timestamp: Long? = System.currentTimeMillis(),
    ): AccountSnapshotResponseDto
}