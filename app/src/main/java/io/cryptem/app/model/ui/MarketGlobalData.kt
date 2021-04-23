package io.cryptem.app.model.ui

import io.cryptem.app.ext.toFiatString
import io.cryptem.app.ext.toPercentString

class MarketGlobalData(
    val marketCap: Double?,
    val marketCapPercentChange24h: Double?,
    val btcDominance: Double?,
    val ethDominance: Double?,
) {

    fun getBtcDominanceString() : String?{
        return btcDominance?.toPercentString(1)
    }

    fun getEthDominanceString() : String?{
        return ethDominance?.toPercentString(1)
    }

    fun getMarketCapString() : String?{
        return marketCap?.toFiatString(Currency.USD, 0)
    }
}