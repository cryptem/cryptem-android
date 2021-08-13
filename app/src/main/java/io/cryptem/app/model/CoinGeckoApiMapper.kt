package io.cryptem.app.model

import io.cryptem.app.model.coingecko.dto.CoinsResponseItemDto
import io.cryptem.app.model.coingecko.dto.GlobalDataDto
import io.cryptem.app.model.ui.Coin
import io.cryptem.app.model.ui.CoinPrice
import io.cryptem.app.model.ui.Currency
import io.cryptem.app.model.ui.MarketGlobalData

fun CoinsResponseItemDto.toUiEntity(currency: Currency? = null): Coin {
    return Coin(
        id = id,
        name = name,
        symbol = symbol.toUpperCase(),
        image = image,
        rank = marketCapRank,
        priceBtc = if (currency?.isBtc() == true) this.toCoinPriceUiEntity() else null,
        priceUsd = if (currency?.isUsd() == true) this.toCoinPriceUiEntity() else null,
        priceCustom = if (currency != null && !currency.isBtc() && !currency.isUsd()) this.toCoinPriceUiEntity() else null,
        marketCap = marketCap
    )
}

fun CoinsResponseItemDto.toCoinPriceUiEntity(): CoinPrice {
    return CoinPrice(
        currentPrice = currentPrice,
        percentChange24h = priceChangePercentage24hInCurrency,
        percentChange7d = priceChangePercentage7dInCurrency,
        percentChange30d = priceChangePercentage30dInCurrency,
        ath = ath,
        athPercent = athChangePercentage,
        athDate = athDate
    )
}

fun GlobalDataDto.toUiEntity(): MarketGlobalData {
    return MarketGlobalData(marketCap = totalMarketCap["usd"],
        marketCapPercentChange24h = marketCapChangePercentage24hUsd,
        btcDominance = marketCapPercentage["btc"]?.let { it / 100.0 },
        ethDominance = marketCapPercentage["eth"]?.let { it / 100.0 }
    )
}