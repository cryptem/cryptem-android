package io.cryptem.app.model.ui

import java.util.Currency

class Currency(ticker : String) {

    companion object{
        val BTC = Currency("BTC")
        val USD = Currency("USD")
    }

    val currency : Currency = Currency.getInstance(ticker.toUpperCase())
    val code = currency.currencyCode

    override fun toString(): String {
        return when{
            currency.currencyCode == "BTC" -> "₿"
            currency.currencyCode == "USD" -> "$"
            else -> currency.symbol
        }
    }

    fun isBtc() : Boolean{
        return currency.currencyCode == "BTC"
    }

    fun isUsd() : Boolean{
        return currency.currencyCode == "USD"
    }

    override fun equals(other: Any?): Boolean {
        return other?.let {
            if (it is io.cryptem.app.model.ui.Currency){
                return currency.currencyCode == (other as io.cryptem.app.model.ui.Currency).currency.currencyCode
            } else {
                false
            }
        } ?: false
    }
}