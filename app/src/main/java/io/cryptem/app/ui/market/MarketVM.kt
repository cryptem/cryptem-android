package io.cryptem.app.ui.market

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.R
import io.cryptem.app.ext.toPercentString
import io.cryptem.app.model.AnalyticsRepository
import io.cryptem.app.model.ui.Coin
import io.cryptem.app.model.HomeScreen
import io.cryptem.app.model.MarketRepository
import io.cryptem.app.model.SharedPrefsRepository
import io.cryptem.app.model.coingecko.CoinGeckoApiDef
import io.cryptem.app.model.ui.MarketGlobalData
import io.cryptem.app.model.ui.PercentTimeInterval
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.util.L
import kodebase.livedata.SafeMutableLiveData
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MarketVM @Inject constructor(private val prefs : SharedPrefsRepository, private val marketRepo: MarketRepository, private val analytics: AnalyticsRepository) : BaseVM() {

    val reloading = SafeMutableLiveData(false)
    val loading = SafeMutableLiveData(false)
    val error = MutableLiveData(false)
    val items = ObservableArrayList<Coin>()
    val currency = MutableLiveData(prefs.getPortfolioCurrency())
    val marketGlobalData = MutableLiveData<MarketGlobalData>()
    val altcoinIndex = MutableLiveData<Double>()
    val altcoinIndexInt = MutableLiveData<Int>()
    val altcoinIndexColorRes = SafeMutableLiveData(R.color.white)
    val percentInterval = listOf(MutableLiveData(PercentTimeInterval.DAY), MutableLiveData(PercentTimeInterval.WEEK))

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        prefs.setHomeScreen(HomeScreen.MARKET)
        loadGlobalMarketData()
        loadMarketFromCache()
        loadCoins(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        analytics.logMarketScreen()
    }

    private fun loadMarketFromCache(){
        items.addAll(marketRepo.getCoinsFromCache())
        if (items.isNotEmpty()){
            calculateAltcoinIndex(items)
        }
    }

    fun loadCoins(forceReload : Boolean = false){
       startLoading(forceReload)
        viewModelScope.launch {
            kotlin.runCatching {
                marketRepo.getCoinsNextPage(forceReload)
            }.onSuccess {
                stopLoading()
                if (forceReload){
                    items.clear()
                    calculateAltcoinIndex(it)
                }
                items.addAll(it)
            }.onFailure {
                L.e(it)
                error.value = true
                stopLoading()
            }
        }
    }

    private fun startLoading(forceReload : Boolean){
        if(forceReload){
            reloading.value = true
        } else {
            loading.value = true
        }
    }

    private fun stopLoading(){
        reloading.value = false
        loading.value = false
    }

    fun calculateAltcoinIndex(data : List<Coin>){
        var betterThanBtc = 0
        var count = 0
        val stableCoins = listOf("DAI", "UST")

        for (i in 0 until CoinGeckoApiDef.PAGE_SIZE){
            val symbol = data[i].symbol.uppercase()
            if (!stableCoins.contains(symbol) && !symbol.contains("BTC") && !symbol.contains("USD")) {
                if (data[i].priceBtc?.percentChange30d ?: 0.0 > 0.0) {
                    betterThanBtc += 1
                }
                count+=1
                if (count == 50){
                    break
                }
            }
        }
        altcoinIndex.value = betterThanBtc / 50.0
        altcoinIndexInt.value = ((altcoinIndex.value ?: 0.0) * 100.0).toInt()
        altcoinIndexColorRes.value = when{
            ((altcoinIndexInt.value?:0) < 25) -> R.color.trend_down_light
            ((altcoinIndexInt.value?:0) > 75) -> R.color.trend_up_light
            else -> R.color.white
        }
    }

    fun loadGlobalMarketData(){
        viewModelScope.launch {
            kotlin.runCatching {
                marketRepo.getMarketGlobalData()
            }.onSuccess {
                marketGlobalData.value = it
            }.onFailure {
                error.value = true
                L.e(it)
            }
        }
    }

    fun showCoin(coin : Coin){
        navigate(MarketFragmentDirections.actionMarketFragmentToCoinFragment(coin.id, coin.symbol, coin.name))
    }

    fun toggleTrendTime(index : Int){
        percentInterval[index].value = when(percentInterval[index].value){
            PercentTimeInterval.DAY -> PercentTimeInterval.WEEK
            PercentTimeInterval.WEEK -> PercentTimeInterval.MONTH
            PercentTimeInterval.MONTH -> PercentTimeInterval.DAY
            null -> PercentTimeInterval.DAY
        }
    }

    fun getAltcoinIndexString(value : Double?) : String{
        return value?.toPercentString(0) ?: "... %"
    }

    fun retry(){
        error.value = false
        loadGlobalMarketData()
        loadCoins()
    }
}