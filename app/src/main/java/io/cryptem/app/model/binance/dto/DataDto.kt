package io.cryptem.app.model.binance.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DataDto(
    @Json(name = "balances")
    val balances: List<BalanceDto>?,
    @Json(name = "totalAssetOfBtc")
    val totalAssetOfBtc: String?
)