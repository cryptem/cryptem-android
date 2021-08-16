package io.cryptem.app.model.ui

import io.cryptem.app.ext.toAmountString
import io.cryptem.app.ext.toBtcString
import io.cryptem.app.ext.toFiatString
import io.cryptem.app.ext.toPercentString

class PortfolioItem(
    val coin: Coin,
    val amountExchange: Double,
    val amountWallet: Double,
    val currency: Currency
) {
    var valuationBtc = 0.0
    var valuationCustom = 0.0
    var valuationUsd = 0.0
    var valuationOnExchange = 0.0
    var portfolioBtcPercent = 0.0
    var portfolioFiatPercent = 0.0
    var valuationBtcPercent = 0.0
    var valuationFiatPercent = 0.0
    var averagePrice = 0.0
    var dca = 0.0
    val amount = amountExchange + amountWallet

    fun recalculateValuations(){
        valuationBtc = amount * (coin.priceBtc?.currentPrice ?: 0.0)
        valuationCustom = amount * (coin.priceCustom?.currentPrice ?: 0.0)
        valuationUsd = amount * (coin.priceUsd?.currentPrice ?: 0.0)
        valuationOnExchange = amountExchange * (coin.priceCustom?.currentPrice ?: 0.0)
    }

    fun recalculate(portfolio: Portfolio) {
        portfolioBtcPercent = (valuationBtc / portfolio.valuationBtc)
        portfolioFiatPercent = (valuationCustom / portfolio.valuationFiat)
        averagePrice = (portfolio.deposit * portfolioFiatPercent) / amount

        val currencyRate = valuationUsd / valuationCustom
        dca = ((portfolio.deposit * currencyRate) * portfolioFiatPercent) / amount
    }

    fun getExchangeAmountString(): String {
        return amountExchange.toAmountString(coin)
    }

    fun getWalletAmountString(): String {
        return amountWallet.toAmountString(coin)
    }

    fun getAmountString() : String{
        return (amountExchange + amountWallet).toAmountString(coin)
    }

    fun getAvgPriceString(): String {
        return averagePrice.toFiatString(currency)
    }

    fun getValuationFiatString(): String {
        return valuationCustom.toFiatString(currency, 0)
    }

    fun getValuationBtcString(): String {
        return valuationBtc.toBtcString()
    }

    fun getDcaString(): String {
        return dca.toFiatString(Currency.USD)
    }

    fun getPortfolioPercentString(): String {
        return portfolioFiatPercent.toPercentString(0)
    }

    fun getPortfolioPercentInt(): Int {
        return (portfolioFiatPercent * 100).toInt()
    }
}

