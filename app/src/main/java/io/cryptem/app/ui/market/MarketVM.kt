package io.cryptem.app.ui.market

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.R
import io.cryptem.app.ext.toPercentString
import io.cryptem.app.model.*
import io.cryptem.app.model.ui.Coin
import io.cryptem.app.model.ui.MarketGlobalData
import io.cryptem.app.model.ui.TimeInterval
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.base.event.UrlEvent
import io.cryptem.app.util.L
import kodebase.livedata.SafeMutableLiveData
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketVM @Inject constructor(
    private val prefs: SharedPrefsRepository,
    private val marketRepo: MarketRepository,
    private val analytics: AnalyticsRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
) : BaseVM() {

    val reloading = SafeMutableLiveData(false)
    val reloadingFavorites = SafeMutableLiveData(false)
    val loading = SafeMutableLiveData(false)
    val error = MutableLiveData(false)
    val items = ObservableArrayList<Coin>()
    val favorites = ObservableArrayList<Coin>()
    val currency = MutableLiveData(prefs.getPortfolioCurrency())
    val marketGlobalData = MutableLiveData<MarketGlobalData>()
    val altcoinIndex = MutableLiveData<Int?>(marketRepo.getAltcoinSeasonIndex())
    val fearAndGreedIndex = MutableLiveData<Int?>(marketRepo.getFearAndGreedIndex())
    val percentInterval = listOf(
        MutableLiveData(prefs.getMarketTimeInterval(0)),
        MutableLiveData(prefs.getMarketTimeInterval(1))
    )
    val favoriteMode = SafeMutableLiveData(prefs.isFavoriteCoinsMode())
    val saleMode = SafeMutableLiveData(prefs.isMarketSaleMode())

    init {
        favoriteMode.observeForever {
            prefs.saveFavoriteCoinsMode(it)
        }
        saleMode.observeForever {
            prefs.saveMarketSaleMode(it)
        }
        percentInterval.forEachIndexed { index, liveData ->
            liveData.observeForever {
                prefs.saveMarketTimeInterval(index, it)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        prefs.setHomeScreen(HomeScreen.MARKET)
        getMarketGlobalDataFromCache()
        loadMarketFromCache()
        loadFavoritesFromCache()
        loadCoins(true)
        loadFavorites(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        analytics.logMarketScreen()
        loadGlobalMarketData()
    }

    private fun loadMarketFromCache() {
        items.addAll(marketRepo.getCoinsFromCache())
    }

    private fun loadFavoritesFromCache() {
        favorites.addAll(marketRepo.getFavoritesFromCache())
    }

    fun loadCoins(forceReload: Boolean = false) {
        startLoading(forceReload)
        viewModelScope.launch {
            kotlin.runCatching {
                marketRepo.getCoinsNextPage(forceReload)
            }.onSuccess {
                stopLoading()
                if (forceReload) {
                    items.clear()
                }
                items.addAll(it)
            }.onFailure {
                L.e(it)
                error.value = true
                stopLoading()
            }
        }
    }

    fun loadFavorites(forceReload: Boolean = false) {
        reloadingFavorites.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                marketRepo.getFavoriteCoins(forceReload)
            }.onSuccess {
                reloadingFavorites.value = false
                if (forceReload) {
                    favorites.clear()
                }
                favorites.addAll(it)
            }.onFailure {
                L.e(it)
                error.value = true
                reloadingFavorites.value = false
            }
        }
    }

    private fun startLoading(forceReload: Boolean) {
        if (forceReload) {
            reloading.value = true
        } else {
            loading.value = true
        }
    }

    private fun stopLoading() {
        reloading.value = false
        loading.value = false
    }

    fun getIndexColorRes(value: Int?): Int {
        return when {
            (value != null && value < 15) -> R.color.crypto_index_1
            (value in 15..25) -> R.color.crypto_index_2
            (value in 25..35) -> R.color.crypto_index_3
            (value in 35..45) -> R.color.crypto_index_4
            (value in 45..55) -> R.color.crypto_index_5
            (value in 55..65) -> R.color.crypto_index_6
            (value in 65..75) -> R.color.crypto_index_7
            (value in 75..85) -> R.color.crypto_index_8
            (value != null && value > 85) -> R.color.crypto_index_9
            else -> R.color.crypto_index_1
        }
    }

    fun loadGlobalMarketData() {
        viewModelScope.launch {
            kotlin.runCatching {
                marketRepo.loadMarketGlobalData()
            }.onSuccess {
                marketGlobalData.value = it
            }.onFailure {
                error.value = true
                L.e(it)
            }
        }
        loadFearAndGreed()
        loadAltcoinSeason()
    }

    fun loadFearAndGreed() {
        viewModelScope.launch {
            fearAndGreedIndex.value = marketRepo.loadFearAndGreedIndex()
        }
    }

    fun loadAltcoinSeason() {
        viewModelScope.launch {
            altcoinIndex.value = marketRepo.loadAltcoinSeasonIndex()
        }
    }

    fun showCoin(coin: Coin) {
        navigate(
            MarketFragmentDirections.actionMarketFragmentToCoinFragment(
                coin.id,
                coin.symbol,
                coin.name
            )
        )
    }

    fun toggleFavorite(coin: Coin): Boolean {
        viewModelScope.launch {
            kotlin.runCatching {
                if (coin.favorite.value == false) {
                    marketRepo.addToFavorites(coin.id)
                } else {
                    marketRepo.removeFromFavorites(coin.id)
                }
            }.onSuccess {
                coin.favorite.value = coin.favorite.value != true
                items.find { it.id == coin.id }?.favorite?.value = coin.favorite.value
                if (coin.favorite.value == false) {
                    favorites.remove(coin)
                }
            }
        }
        return true
    }

    fun toggleSaleMode() {
        saleMode.value = !saleMode.value
    }

    fun toggleTrendTime(index: Int) {
        percentInterval[index].value = when (percentInterval[index].value) {
            TimeInterval.DAY -> TimeInterval.WEEK
            TimeInterval.WEEK -> TimeInterval.MONTH
            TimeInterval.MONTH -> TimeInterval.YEAR
            TimeInterval.YEAR -> TimeInterval.ATH
            TimeInterval.ATH -> TimeInterval.DAY
            else -> TimeInterval.DAY
        }
    }

    fun getPercentString(value: Int?): String {
        return value?.toPercentString() ?: "... %"
    }

    fun getMarketGlobalDataFromCache() {
        marketGlobalData.value = marketRepo.getMarketGlobalDataFromCache()
    }

    fun refresh(){
        loadCoins(true)
        loadGlobalMarketData()
    }

    fun retry() {
        error.value = false
        loadGlobalMarketData()
        loadCoins()
    }

    fun showFearAndGreedIndexWeb() {
        publish(UrlEvent(remoteConfigRepository.getFearAndGreedIndexUrl()))
    }

    fun showAltcoinSeasonIndexWeb() {
        publish(UrlEvent(remoteConfigRepository.getAltcoinSeasonIndexUrl()))
    }
}