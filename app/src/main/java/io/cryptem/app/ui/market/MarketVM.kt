package io.cryptem.app.ui.market

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.ext.toPercentString
import io.cryptem.app.model.ui.Coin
import io.cryptem.app.model.HomeScreen
import io.cryptem.app.model.MarketRepository
import io.cryptem.app.model.SharedPrefsRepository
import io.cryptem.app.model.ui.MarketGlobalData
import io.cryptem.app.model.ui.PercentTimeInterval
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.util.L
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MarketVM @Inject constructor(val prefs : SharedPrefsRepository, val marketRepo: MarketRepository) : BaseVM() {

    val loading = MutableLiveData(false)
    val error = MutableLiveData(false)
    val items = ObservableArrayList<Coin>()
    val currency = MutableLiveData(prefs.getPortfolioCurrency())
    val marketGlobalData = MutableLiveData<MarketGlobalData>(MarketGlobalData())
    val altcoinIndex = MutableLiveData<Double>()
    val altcoinIndexInt = MutableLiveData<Int>()
    val percentInterval = listOf(MutableLiveData(PercentTimeInterval.DAY), MutableLiveData(PercentTimeInterval.WEEK))

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        prefs.setHomeScreen(HomeScreen.MARKET)
        loadMarketData()
        loadCoins()
    }

    fun loadCoins(){
        loading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                marketRepo.getCoins(false)
            }.onSuccess {
                loading.value = false
                items.clear()
                items.addAll(it)
                calculateAltcoinIndex(it)
            }.onFailure {
                L.e(it)
                error.value = true
                loading.value = false
            }
        }
    }

    fun calculateAltcoinIndex(data : List<Coin>){
        var betterThanBtc = 0
        var count = 0
        val stableCoins = listOf("BTC","USDT", "USDC", "DAI", "BUSD", "WBTC", "UST", "HUSD", "TUSD")

        for (i in 0 until 100){
            if (!stableCoins.contains(data[i].symbol.toUpperCase(Locale.getDefault()))) {
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
    }

    fun loadMarketData(){
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
        navigate(MarketFragmentDirections.actionMarketFragmentToCoinFragment(coin.id, coin.name))
    }

    fun toggleTrendTime(index : Int){
        percentInterval[index].value = when(percentInterval[index].value){
            PercentTimeInterval.DAY -> PercentTimeInterval.WEEK
            PercentTimeInterval.WEEK -> PercentTimeInterval.MONTH
            PercentTimeInterval.MONTH -> PercentTimeInterval.DAY
            null -> PercentTimeInterval.DAY
        }
    }

    fun getAltcoinIndexString(value : Double?) : String?{
        return value?.toPercentString(0) ?: "..."
    }

    fun retry(){
        error.value = false
        loadMarketData()
        loadCoins()
    }
}