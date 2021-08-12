package io.cryptem.app.model.ui

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class BinanceKeys(
    @Json(name = "apiKey")
    val apiKey : String,
    @Json(name = "secretKey")
    val secretKey : String)