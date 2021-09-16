package io.cryptem.app.model.db

import io.cryptem.app.model.db.entity.PortfolioDbEntity
import io.cryptem.app.model.db.entity.WalletDbEntity
import io.cryptem.app.model.ui.*

fun PortfolioDbEntity.toCoin(): Coin {
    return Coin(
        id = id,
        symbol = symbol,
        name = name,
        image = image,
        rank = marketCapRank,
        priceBtc = CoinPrice(
            currentPrice = currentPriceBtc,
            percentChange24h = priceChangePercentage24hBtc,
            percentChange7d = priceChangePercentage7dBtc,
            percentChange30d = priceChangePercentage30dBtc,
            percentChange1y = priceChangePercentage1yBtc
        ),
        priceUsd = CoinPrice(
            currentPrice = currentPriceUsd,
            percentChange24h = priceChangePercentage24hUsd,
            percentChange7d = priceChangePercentage7dUsd,
            percentChange30d = priceChangePercentage30dUsd,
            percentChange1y = priceChangePercentage1yUsd
        ),
        priceCustom = CoinPrice(currentPrice = currentPriceCustom)
    )
}

fun PortfolioDbEntity.toUiEntity(currency : Currency) : PortfolioItem{
    return PortfolioItem(coin = toCoin(),
        amountExchange = amountExchange,
        amountWallet = amountWallet,
        currency = currency)
}

fun WalletDbEntity.toUiEntity(): Wallet {
    return Wallet(
        id = id,
        coin = WalletCoin.valueOf(coin),
        name = privateName,
        address = address
    )
}

fun Wallet.toDbEntity(): WalletDbEntity {
    return WalletDbEntity(
        id = id,
        coin = coin.name,
        privateName = name.value,
        address = address.value
    )
}