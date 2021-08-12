package io.cryptem.app.model.binance.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SnapshotVoDto(
    @Json(name = "data")
    val `data`: DataDto?,
    @Json(name = "type")
    val type: String?,
    @Json(name = "updateTime")
    val updateTime: Long?
)