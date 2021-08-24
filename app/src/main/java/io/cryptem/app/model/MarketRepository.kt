package io.cryptem.app.model

import android.graphics.Color
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.cryptem.app.model.coingecko.CoinGeckoApiDef
import io.cryptem.app.model.coingecko.dto.CoinsResponseItemDto
import io.cryptem.app.model.ui.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketRepository @Inject constructor(
    val coinGeckoApi: CoinGeckoApiDef,
) {

    private val marketCoinsMap =
        HashedCache(15, funLoadItem = this::loadMarketCoin, keyMapFun = { it.id })
    private val marketGlobalData = Cache(60, funLoad = this::loadMarketGlobalData)

    val marketCoinsCache = ArrayList<Coin>()
    var marketCoinsPage = 1
        private set

    suspend fun getCoinsNextPage(forceReload: Boolean = false): List<Coin> {
        if (forceReload) {
            marketCoinsCache.clear()
            marketCoinsPage = 1
        }

        val resultUsd = coinGeckoApi.getCoins(currency = "USD", page = marketCoinsPage)
        val resultBtc = coinGeckoApi.getCoins(currency = "BTC", page = marketCoinsPage)
        val btcPriceMap = HashMap<String, CoinsResponseItemDto>()

        resultBtc.forEach {
            btcPriceMap[it.id] = it
        }

        marketCoinsPage += 1
        val result = resultUsd.map {
            it.toUiEntity(Currency.USD)
                .apply { priceBtc = btcPriceMap[it.id]?.toCoinPriceUiEntity() }
        }.onEach { coin ->
            marketCoinsMap.put(coin)
        }
        marketCoinsCache.addAll(result)
        return result
    }

    suspend fun getCoin(id: String): Coin? {
        return marketCoinsMap.get(id)
    }

    suspend fun loadMarketCoin(id: String): Coin? {
        val coinBtc = coinGeckoApi.getCoins(currency = "BTC", ids = id).firstOrNull()
        val coinUsd = coinGeckoApi.getCoins(currency = "USD", ids = id).firstOrNull()

        return coinUsd?.toUiEntity()?.apply {
            priceBtc = coinBtc?.toCoinPriceUiEntity()
            priceUsd = coinUsd.toCoinPriceUiEntity()
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

    suspend fun getChart(id: String, days: Int): LineData {
        val responseUsd = coinGeckoApi.getMarketChart(id, "USD", days)
        val responseBtc = coinGeckoApi.getMarketChart(id, "BTC", days)

        val values1 = responseUsd.prices?.map {
            Entry(it[0].toFloat(), it[1].toFloat())
        }

        val values2 = responseBtc.prices?.map {
            Entry(it[0].toFloat(), it[1].toFloat())
        }

        val set1 = LineDataSet(values1, "USD")

        set1.axisDependency = AxisDependency.LEFT
        set1.color = Color.BLACK
        set1.lineWidth = 2f
        set1.setDrawCircles(false)
        set1.setDrawValues(false)

        val set2 = LineDataSet(values2, "BTC")
        set2.axisDependency = AxisDependency.RIGHT
        set2.color = Color.parseColor("#f7931a")
        set2.lineWidth = 2f
        set2.setDrawCircles(false)
        set2.setDrawValues(false)

        val data = LineData(set1, set2)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)

        return data
    }
}