package io.cryptem.app.model.ui

import io.cryptem.app.ext.toFiatString
import io.cryptem.app.ext.toPercentString

class MarketGlobalData(
    val marketCap: Double? = null,
    val marketCapPercentChange24h: Double? = null,
    val btcDominance: Double? = null,
    val ethDominance: Double? = null,
) {

    fun getBtcDominanceString() : String?{
        return btcDominance?.toPercentString(1) ?: "..."
    }

    fun getEthDominanceString() : String?{
        return ethDominance?.toPercentString(1) ?: "..."
    }

    fun getMarketCapString() : String?{
        return marketCap?.toFiatString(Currency.USD, 0) ?: "..."
    }

    fun getBtcDominanceInt() : Int{
        return ((btcDominance ?: 0.0) * 100).toInt()
    }

    fun getEthDominanceInt() : Int{
        return ((ethDominance ?: 0.0) * 100).toInt()
    }
}