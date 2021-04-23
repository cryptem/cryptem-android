package io.cryptem.app.model.ui

class CoinPrice(
    val currentPrice: Double?,
    val percentChange24h: Double? = null,
    val percentChange7d: Double? = null,
    val percentChange30d: Double? = null,
) {
}