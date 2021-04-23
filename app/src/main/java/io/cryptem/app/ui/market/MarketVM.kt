package io.cryptem.app.ui.market

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.ui.Coin
import io.cryptem.app.model.HomeScreen
import io.cryptem.app.model.MarketRepository
import io.cryptem.app.model.SharedPrefsRepository
import io.cryptem.app.model.ui.MarketGlobalData
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.util.L
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketVM @Inject constructor(val prefs : SharedPrefsRepository, val marketRepo: MarketRepository) : BaseVM() {

    val loading = MutableLiveData(false)
    val items = ObservableArrayList<Coin>()
    val currency = MutableLiveData(prefs.getPortfolioCurrency())
    val marketGlobalData = MutableLiveData<MarketGlobalData>()

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
            }.onFailure {
                L.e(it)
                loading.value = false
            }
        }
    }

    fun loadMarketData(){
        viewModelScope.launch {
            kotlin.runCatching {
                marketRepo.getMarketGlobalData()
            }.onSuccess {
                marketGlobalData.value = it
            }.onFailure {
                L.e(it)
            }
        }
    }

    fun showCoin(coin : Coin){
        navigate(MarketFragmentDirections.actionMarketFragmentToCoinFragment(coin.id, coin.name))
    }
}