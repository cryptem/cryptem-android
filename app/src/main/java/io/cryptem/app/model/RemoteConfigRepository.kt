package io.cryptem.app.model

import io.cryptem.app.model.ui.Currency
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigRepository  @Inject constructor(){

    fun getSupportedCurrencies() : List<Currency>{
        return listOf("USD", "EUR", "CZK", "GBP", "RUB", "UAH", "CAD", "AUD", "DKK", "NOK", "SEK", "CHF").map {
            Currency(it)
        }.sortedBy {
            it.code
        }
    }
}