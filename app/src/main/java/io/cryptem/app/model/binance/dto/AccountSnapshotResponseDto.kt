package io.cryptem.app.model.binance.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccountSnapshotResponseDto(
    @Json(name = "code")
    val code: Int?,
    @Json(name = "msg")
    val msg: String?,
    @Json(name = "snapshotVos")
    val snapshotVos: List<SnapshotVoDto>?
)