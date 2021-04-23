package io.cryptem.app.model.api

import io.cryptem.app.model.api.dto.CoinsResponseDto
import retrofit2.http.GET

interface CryptemApiDef {

    @GET("api/coins.php")
    suspend fun getCoins() : CoinsResponseDto
}