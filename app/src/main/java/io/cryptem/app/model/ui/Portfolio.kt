package io.cryptem.app.model.ui

import androidx.lifecycle.MutableLiveData
import io.cryptem.app.ext.toBtcString
import io.cryptem.app.ext.toFiatString
import io.cryptem.app.ext.toPercentString
import io.cryptem.app.util.L
import kotlin.math.min

class Portfolio(var currency: Currency, val deposit: Long) {

    var items : List<PortfolioItem> = ArrayList()

    var valuationPercent = 0.0
    var valuationFiat = 0.0
    var valuationBtc = 0.0
    val percentOnExchange = MutableLiveData(0.0)

    fun recalculate() {
        valuationBtc = 0.0
        valuationFiat = 0.0
        percentOnExchange.value = 0.0
        var valuationOnExchange = 0.0

        for (item in items) {
            item.recalculateValuations()
            valuationBtc += item.valuationBtc
            valuationFiat += item.valuationCustom
            valuationOnExchange += item.valuationOnExchange
        }
        valuationPercent = ((valuationFiat-deposit)/deposit)
        percentOnExchange.value = (valuationOnExchange/valuationFiat)

        for (item in items) {
            item.recalculate(this)
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

    fun getValuationPercentString() : String{
        return valuationPercent.toPercentString(2)
    }

    fun getBtcProgressInt() : Int{
        return min((valuationBtc * 100).toInt(), 100)
    }

    fun getProfitProgressInt() : Int{
        return 100 - ((deposit / valuationFiat) * 100).toInt()
    }

    fun getPercentOnExchangeString() : String?{
        return percentOnExchange.value?.toPercentString(0)
    }
}