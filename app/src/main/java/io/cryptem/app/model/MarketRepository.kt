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
import io.cryptem.app.model.db.FavoriteCoinsDatabase
import io.cryptem.app.model.db.entity.FavoriteCoinDbEntity
import io.cryptem.app.model.db.toUiEntity
import io.cryptem.app.model.ui.*
import io.cryptem.app.model.ui.Currency
import io.cryptem.app.util.L
import kotlinx.coroutines.Dispatchers
import okhttp3.*
import okio.IOException
import java.text.ParseException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class MarketRepository @Inject constructor(
    private val coinGeckoApi: CoinGeckoApiDef,
    private val favoritesDb: FavoriteCoinsDatabase,
    private val httpClient: OkHttpClient,
    private val cache: CacheRepository,
    private val remoteConfigRepository: RemoteConfigRepository
) {

    private val marketCoinsMap =
        HashedCache(COIN_CACHE_MINUTES, funLoadItem = this::loadMarketCoin, keyMapFun = { it.id })
    private val chartCache =
        HashedCache(COIN_CHART_CACHE_MINUTES, funLoadItem = this::loadChart, keyMapFun = { it.key })

    private val marketCoinsCache = ArrayList<Coin>()
    private val favoriteCoinsCache = ArrayList<Coin>()
    private val favoritesMap = HashMap<String, Boolean>()
    var marketCoinsPage = 1
        private set

    suspend fun getCoinsNextPage(forceReload: Boolean = false): List<Coin> {
        if (forceReload) {
            marketCoinsCache.clear()
            marketCoinsPage = 1
            loadFavoriteIds()
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
                .apply {
                    priceBtc = btcPriceMap[it.id]?.toCoinPriceUiEntity()
                    favorite.postValue(favoritesMap[it.id] == true)
                }
        }.onEach { coin ->
            saveToCache(coin)
        }
        marketCoinsCache.addAll(result)
        return result
    }

    suspend fun getFavoriteCoins(forceReload: Boolean = false): List<Coin> {
        if (forceReload) {
            favoriteCoinsCache.clear()
        }

        val ids = loadFavoriteIds()
        if (ids.isEmpty()) {
            return emptyList()
        }
        val idsString = ids.joinToString(",")
        val resultUsd = coinGeckoApi.getCoins(ids = idsString, currency = "USD")
        val resultBtc = coinGeckoApi.getCoins(ids = idsString, currency = "BTC")
        val btcPriceMap = HashMap<String, CoinsResponseItemDto>()

        resultBtc.forEach {
            btcPriceMap[it.id] = it
        }

        val result = resultUsd.map {
            it.toUiEntity(Currency.USD)
                .apply {
                    priceBtc = btcPriceMap[it.id]?.toCoinPriceUiEntity()
                    favorite.postValue(favoritesMap[it.id] == true)
                }
        }.onEach { coin ->
            saveToCache(coin)
        }
        favoriteCoinsCache.addAll(result)
        return result
    }

    suspend fun search(name: String): List<Coin> {
        if (marketCoinsCache.isEmpty()) {
            getCoinsNextPage(true)
        }
        return marketCoinsCache.filter { it.name.startsWith(name, true) }
    }

    suspend fun getCoin(id: String): Coin? {
        return marketCoinsMap.get(id)
    }

    fun getCoinsFromCache(): ArrayList<Coin> {
        return marketCoinsCache
    }

    fun getFavoritesFromCache(): ArrayList<Coin> {
        return favoriteCoinsCache
    }

    fun getCoinFromCache(id: String): Coin? {
        return marketCoinsMap.peek(key = id)
    }

    private fun saveToCache(coin: Coin) {
        // Prevent overriding coins with custom prices (portfolio)
        val cached = marketCoinsMap.peek(coin.id)
        if (cached?.priceCustom == null || coin.priceCustom != null) {
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

    suspend fun loadMarketGlobalData(): MarketGlobalData? {
        return coinGeckoApi.getGlobalMarketData().data?.toUiEntity()?.apply {
            cache.saveMarketcap(marketCap)
            cache.saveMarketcapPercentChange24h(marketCapPercentChange24h)
            cache.saveBtcDominance(btcDominance)
            cache.saveEthDominance(ethDominance)
        }
    }

    private suspend fun loadChart(key: PriceChartData.Key): PriceChartData {
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

    private suspend fun loadFavoriteIds(): List<String> {
        return favoritesDb.dao().getFavorites().map { it.toUiEntity() }
            .onEach {
                favoritesMap[it] = true
            }
    }

    suspend fun addToFavorites(id: String) {
        favoritesDb.dao().addFavorite(FavoriteCoinDbEntity(id))
        favoritesMap[id] = true
    }

    suspend fun removeFromFavorites(id: String) {
        favoritesDb.dao().removeFavorite(FavoriteCoinDbEntity(id))
        favoriteCoinsCache.removeAll { it.id == id }
        favoritesMap.remove(id)
    }

    suspend fun loadFearAndGreedIndex(): Int? {
        val result = getCryptoIndex(remoteConfigRepository.getFearAndGreedIndexUrl(), remoteConfigRepository.getFearAndGreedIndexRegex())
        cache.saveFearAndGreedIndex(result)
        return result
    }

    suspend fun loadAltcoinSeasonIndex(): Int? {
        val result = getCryptoIndex(remoteConfigRepository.getAltcoinSeasonIndexUrl(), remoteConfigRepository.getAltcoinSeasonIndexRegex())
        cache.saveAltcoinSeasonIndex(result)
        return result
    }

    fun getFearAndGreedIndex(): Int? {
        return cache.getFearAndGreedIndex()
    }

    fun getAltcoinSeasonIndex(): Int? {
        return cache.getAltcoinSeasonIndex()
    }

    private suspend fun getCryptoIndex(url : String, regexString : String) : Int?{
        return suspendCoroutine { cont ->
            val request: Request = Request.Builder().url(url).build()

            httpClient.newCall(request).enqueue(object: Callback{
                override fun onFailure(call: Call, e: java.io.IOException) {
                    L.e(e)
                    cont.resumeWith(Result.success(null))
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.string()?.let {
                        val regex = regexString.toRegex(RegexOption.DOT_MATCHES_ALL)
                        val result = regex.find(it)?.groupValues?.get(1)?.toIntOrNull()
                        cont.resumeWith(Result.success(result))
                    } ?: kotlin.run {
                        L.e(IOException("Error loading $url"))
                        cont.resumeWith(Result.success(null))
                    }
                }
            })
        }
    }

    fun getMarketGlobalDataFromCache(): MarketGlobalData? {
        return cache.getMarketGlobalData()
    }
}