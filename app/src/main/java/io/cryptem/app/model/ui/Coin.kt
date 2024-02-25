package io.cryptem.app.model.ui

import androidx.lifecycle.MutableLiveData
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

    val favorite = MutableLiveData<Boolean>()

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
        return priceBtc?.getPercentChange(time)
    }

    fun getPercentUsd(time : TimeInterval) : Double?{
        return priceUsd?.getPercentChange(time)
    }

    fun getSparkline() : String{
        val id = image?.substringAfter("images/")?.substringBefore("/")
        return "https://www.coingecko.com/coins/$id/sparkline.svg"
    }

    fun isBtc() : Boolean{
        return symbol.equals("BTC", ignoreCase = true)
    }
}