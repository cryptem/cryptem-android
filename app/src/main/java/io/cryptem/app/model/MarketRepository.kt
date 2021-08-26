package io.cryptem.app.model

import android.graphics.Color
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.cryptem.app.AppConfig.COIN_CACHE_MINUTES
import io.cryptem.app.AppConfig.COIN_CHART_CACHE_MINUTES
import io.cryptem.app.AppConfig.MARKET_GLOBAL_DATA_CACHE_MINUTES
import io.cryptem.app.model.coingecko.CoinGeckoApiDef
import io.cryptem.app.model.coingecko.dto.CoinsResponseItemDto
import io.cryptem.app.model.ui.*
import io.cryptem.app.model.ui.Currency
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Singleton
class MarketRepository @Inject constructor(
    val coinGeckoApi: CoinGeckoApiDef,
) {

    private val marketCoinsMap =
        HashedCache(COIN_CACHE_MINUTES, funLoadItem = this::loadMarketCoin, keyMapFun = { it.id })
    private val chartCache =
        HashedCache(COIN_CHART_CACHE_MINUTES, funLoadItem = this::loadChart, keyMapFun = { it.key })
    private val marketGlobalData = Cache(MARKET_GLOBAL_DATA_CACHE_MINUTES, funLoad = this::loadMarketGlobalData)

    private val marketCoinsCache = ArrayList<Coin>()
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
            saveToCache(coin)
        }
        marketCoinsCache.addAll(result)
        return result
    }

    suspend fun getCoin(id: String): Coin? {
        return marketCoinsMap.get(id)
    }

    fun getCoinsFromCache(): ArrayList<Coin> {
        return marketCoinsCache
    }

    fun getCoinFromCache(id: String): Coin? {
        return marketCoinsMap.peek(key = id)
    }

    private fun saveToCache(coin : Coin){
        // Prevent overriding coins with custom prices (portfolio)
        val cached = marketCoinsMap.peek(coin.id)
        if (cached?.priceCustom == null || coin.priceCustom != null){
            marketCoinsMap.put(coin)
        }
    }

    private suspend fun loadMarketCoin(id: String): Coin? {
        val coinBtc = coinGeckoApi.getCoins(currency = "BTC", ids = id).firstOrNull()
        val coinUsd = coinGeckoApi.getCoins(currency = "USD", ids = id).firstOrNull()

        return coinUsd?.toUiEntity()?.apply {
            priceBtc = coinBtc?.toCoinPriceUiEntity()
            priceUsd = coinUsd.toCoinPriceUiEntity()
        }.also {
            it?.let {
                saveToCache(it)
            }
        }
    }

    suspend fun loadPortfolioCoins(ids: String, customCurrency: Currency): List<Coin> {
        val coins = loadCoins(ids = ids, customCurrency = Currency.USD)
        val coinsBtc = loadCoins(ids = ids, customCurrency = Currency.BTC)
        val coinsCustom = if (customCurrency.isUsd()) {
            null
        } else {
            loadCoinsPrice(ids = ids, customCurrency = customCurrency)
        }
        coins.map { coin ->
            coin.priceBtc = coinsBtc.find { it.id == coin.id }?.priceBtc
            coin.priceCustom = if (customCurrency.isUsd()) {
                coin.priceUsd
            } else {
                CoinPrice(
                    coinsCustom?.get(coin.id)?.get(
                        customCurrency.code.toLowerCase(Locale.getDefault())
                    )
                )
            }
            saveToCache(coin)
        }
        return coins
    }

    suspend fun loadCoins(ids: String, customCurrency: Currency): List<Coin> {
        return coinGeckoApi.getCoins(currency = customCurrency.code, ids = ids).map {
            val result = it.toUiEntity(customCurrency)
            return@map result
        }
    }

    suspend fun loadCoinsPrice(
        ids: String,
        customCurrency: Currency
    ): Map<String, Map<String, Double>> {
        return coinGeckoApi.getCoinsPrice(currency = customCurrency.code, ids = ids)
    }

    suspend fun loadCoinPrice(id: String, currency: Currency): Double? {
        return loadCoinsPrice(id, currency)[id]?.get(currency.code.toLowerCase(Locale.getDefault()))
    }

    suspend fun getMarketGlobalData(): MarketGlobalData? {
        return marketGlobalData.get()
    }

    private suspend fun loadMarketGlobalData(): MarketGlobalData? {
        return coinGeckoApi.getGlobalMarketData().data?.toUiEntity()
    }

    private suspend fun loadChart(key : PriceChartData.Key) : PriceChartData{
        val responseUsd = coinGeckoApi.getMarketChart(key.id, "USD", key.days)
        val responseBtc = coinGeckoApi.getMarketChart(key.id, "BTC", key.days)

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

        val data = if (key.id != "bitcoin") {
            val set2 = LineDataSet(values2, "BTC")
            set2.axisDependency = AxisDependency.RIGHT
            set2.color = Color.parseColor("#f7931a")
            set2.lineWidth = 2f
            set2.setDrawCircles(false)
            set2.setDrawValues(false)
            LineData(set1, set2)
        } else {
            LineData(set1)
        }

        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)

        return PriceChartData(key, data)
    }

    suspend fun getChart(id: String, days: Int): PriceChartData? {
        return chartCache.get(PriceChartData.Key(id, days))
    }
}