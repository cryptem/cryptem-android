package io.cryptem.app.model

import io.cryptem.app.model.coingecko.CoinGeckoApiDef
import io.cryptem.app.model.coingecko.dto.CoinsResponseItemDto
import io.cryptem.app.model.ui.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketRepository @Inject constructor(
    val coinGeckoApi: CoinGeckoApiDef,
) {

    private val marketCoins = ListCache(5, funLoad = this::loadMarketCoins)
    private val marketCoinsMap =
        HashedCache(15, funLoadItem = this::loadMarketCoin, keyMapFun = { it.id })
    private val marketGlobalData = Cache(60, funLoad = this::loadMarketGlobalData)

    private suspend fun loadMarketCoins(): List<Coin> {
        val resultUsd = coinGeckoApi.getCoins("USD")
        val resultBtc = coinGeckoApi.getCoins("BTC")
        val btcPriceMap = HashMap<String, CoinsResponseItemDto>()

        resultBtc.forEach {
            btcPriceMap[it.id] = it
        }

        return resultUsd.map {
            it.toUiEntity(Currency.USD)
                .apply { priceBtc = btcPriceMap[it.id]?.toCoinPriceUiEntity() }
        }
    }

    suspend fun getCoins(force: Boolean = false): List<Coin> {
        val result = marketCoins.get(force)
        result.forEach {
            marketCoinsMap.put(it)
        }
        return result
    }

    suspend fun getCoin(id: String): Coin? {
        return marketCoinsMap.get(id)
    }

    suspend fun loadMarketCoin(id: String): Coin? {
        val coinBtc = coinGeckoApi.getCoins(currency = "BTC", ids = id).firstOrNull()
        val coinUsd = coinGeckoApi.getCoins(currency = "USD", ids = id).firstOrNull()

        return coinBtc?.toUiEntity()?.apply {
            priceBtc = coinBtc.toCoinPriceUiEntity()
            priceUsd = coinUsd?.toCoinPriceUiEntity()
        }
    }

    suspend fun loadCoins(ids: String, customCurrency: Currency): List<Coin> {
        return coinGeckoApi.getCoins(currency = customCurrency.code, ids = ids).map {
            val result = it.toUiEntity(customCurrency)
            return@map result
        }
    }

    suspend fun loadCoin(id: String, customCurrency: Currency): Coin? {
        val coin = coinGeckoApi.getCoins(currency = customCurrency.code, ids = id).firstOrNull()
        return coin?.toUiEntity(customCurrency)
    }

    suspend fun loadCoinsPrice(
        ids: String,
        customCurrency: Currency
    ): Map<String, Map<String, Double>> {
        return coinGeckoApi.getCoinsPrice(currency = customCurrency.code, ids = ids)
    }

    suspend fun loadCoinPrice(id: String, currency: Currency): Double? {
        return loadCoinsPrice(id, currency)[id]?.get(currency.code.toLowerCase())
    }

    suspend fun getMarketGlobalData(): MarketGlobalData? {
        return marketGlobalData.get()
    }

    private suspend fun loadMarketGlobalData(): MarketGlobalData? {
        return coinGeckoApi.getGlobalMarketData().data?.toUiEntity()
    }
}