package io.cryptem.app.model.ui

import io.cryptem.app.ext.toBtcString
import io.cryptem.app.ext.toFiatString
import io.cryptem.app.ext.toSatString

class Coin(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String?,
    val rank: Int?,
    var priceBtc : CoinPrice? = null,
    var priceUsd : CoinPrice? = null,
    var priceCustom : CoinPrice? = null,
    var marketCap : Double? = null,
) {

    fun getPrice(currency : Currency) : Double?{
        return when (currency){
            Currency.BTC -> priceBtc?.currentPrice
            Currency.USD -> priceUsd?.currentPrice
            else -> priceCustom?.currentPrice
        }
    }

    fun getPriceString(currency : Currency) : String?{
        return getPrice(currency)?.toFiatString(currency)
    }

    fun getUsdPriceString() : String?{
        return getPriceString(Currency.USD)
    }

    fun getBtcPriceString() : String?{
        return getPrice(Currency.BTC)?.toBtcString()
    }

    fun getSatPriceString() : String?{
        return getPrice(Currency.BTC)?.toSatString()
    }

    fun getMarketCapString() : String?{
        return marketCap?.toFiatString(Currency.USD, 0)
    }

    fun getPercentBtc(time : PercentTimeInterval) : Double?{
        return when(time){
            PercentTimeInterval.DAY -> priceBtc?.percentChange24h
            PercentTimeInterval.WEEK -> priceBtc?.percentChange7d
            PercentTimeInterval.MONTH -> priceBtc?.percentChange30d
        }
    }

    fun getPercentUsd(time : PercentTimeInterval) : Double?{
        return when(time){
            PercentTimeInterval.DAY -> priceUsd?.percentChange24h
            PercentTimeInterval.WEEK -> priceUsd?.percentChange7d
            PercentTimeInterval.MONTH -> priceUsd?.percentChange30d
        }
    }

    fun isBtc() : Boolean{
        return symbol.equals("BTC", ignoreCase = true)
    }
}