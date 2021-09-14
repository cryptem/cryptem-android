package io.cryptem.app.model.ui

import androidx.lifecycle.MutableLiveData
import io.cryptem.app.ext.toBtcString
import io.cryptem.app.ext.toFiatString
import io.cryptem.app.ext.toPercentString
import kotlin.math.min

class Portfolio(var currency: Currency, val deposit: Long) {

    var items : List<PortfolioItem> = ArrayList()

    var valuationPercent = 0.0
    var valuationFiat = 0.0
    var valuationBtc = 0.0
    var percentOnExchange = 0.0
    val topCoins = HashMap<TimeInterval, PortfolioItem?>()
    val worstCoins = HashMap<TimeInterval, PortfolioItem?>()
    val percentGains = HashMap<TimeInterval, Double?>()

    fun recalculate() {
        valuationBtc = 0.0
        valuationFiat = 0.0
        percentOnExchange = 0.0
        var valuationOnExchange = 0.0

        for (item in items) {
            item.recalculateValuations()
            valuationBtc += item.valuationBtc
            valuationFiat += item.valuationCustom
            valuationOnExchange += item.valuationOnExchange
        }
        valuationPercent = ((valuationFiat-deposit)/deposit)
        percentOnExchange = (valuationOnExchange/valuationFiat)

        for (item in items) {
            item.recalculate(this)
        }
        setTopAndWorst()
        setPercentGains()
    }

    private fun setTopAndWorst(){
        TimeInterval.values().forEach { interval ->
            topCoins[interval] = items.maxByOrNull { it.coin.getPercentUsd(interval) ?: 0.0 }
            worstCoins[interval] = items.minByOrNull { it.coin.getPercentUsd(interval) ?: 0.0 }
        }
    }

    private fun setPercentGains(){
        TimeInterval.values().forEach { interval ->
            var weightedPercent = 0.0
            items.forEach {
                weightedPercent += (it.portfolioFiatPercent * (it.coin.getPercentUsd(interval) ?: 0.0))
            }
            percentGains[interval] = weightedPercent
        }
    }

    fun getDepositString() : String{
        return deposit.toDouble().toFiatString(currency, 0)
    }

    fun getValuationFiatString() : String{
        return valuationFiat.toFiatString(currency, 0)
    }

    fun getValuationBtcString() : String{
        return valuationBtc.toBtcString()
    }

    fun getValuationPercentStringAbs() : String{
        return (valuationPercent).toPercentString(1, true)
    }

    fun getBtcProgressInt() : Int{
        return min((valuationBtc * 100).toInt(), 100)
    }

    fun getProfitProgressInt() : Int{
        return 100 - ((deposit / valuationFiat) * 100).toInt()
    }

    fun getPercentOnExchangeString() : String{
        return percentOnExchange.toPercentString(0)
    }
}