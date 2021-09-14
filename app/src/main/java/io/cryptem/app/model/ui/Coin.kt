package io.cryptem.app.model.ui

import android.os.Parcelable
import io.cryptem.app.ext.toBtcString
import io.cryptem.app.ext.toFiatString
import io.cryptem.app.ext.toSatString
import kotlinx.parcelize.Parcelize

@Parcelize
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
) : Parcelable {

    fun getPriceDouble(currency : Currency) : Double?{
        return when (currency){
            Currency.BTC -> priceBtc?.currentPrice
            Currency.USD -> priceUsd?.currentPrice
            else -> priceCustom?.currentPrice
        }
    }

    fun getCoinPrice(currency : Currency) : CoinPrice?{
        return when (currency){
            Currency.BTC -> priceBtc
            Currency.USD -> priceUsd
            else -> priceCustom
        }
    }

    fun getPriceString(currency : Currency) : String?{
        return getPriceDouble(currency)?.toFiatString(currency)
    }

    fun getUsdPriceString() : String?{
        return getPriceString(Currency.USD)
    }

    fun getBtcPriceString() : String?{
        return getPriceDouble(Currency.BTC)?.toBtcString()
    }

    fun getSatPriceString() : String?{
        return getPriceDouble(Currency.BTC)?.toSatString()
    }

    fun getAthUsdString() : String?{
        return priceUsd?.ath?.toFiatString(Currency.USD)
    }

    fun getAthBtcString() : String?{
        return priceBtc?.ath?.toBtcString()
    }

    fun getMarketCapString() : String?{
        return marketCap?.toFiatString(Currency.USD, 0)
    }

    fun getPercentBtc(time : TimeInterval) : Double?{
        return when(time){
            TimeInterval.DAY -> priceBtc?.percentChange24h
            TimeInterval.WEEK -> priceBtc?.percentChange7d
            TimeInterval.MONTH -> priceBtc?.percentChange30d
        }
    }

    fun getPercentUsd(time : TimeInterval) : Double?{
        return when(time){
            TimeInterval.DAY -> priceUsd?.percentChange24h
            TimeInterval.WEEK -> priceUsd?.percentChange7d
            TimeInterval.MONTH -> priceUsd?.percentChange30d
        }
    }

    fun getSparkline() : String{
        val id = image?.substringAfter("images/")?.substringBefore("/")
        return "https://www.coingecko.com/coins/$id/sparkline"
    }

    fun isBtc() : Boolean{
        return symbol.equals("BTC", ignoreCase = true)
    }
}