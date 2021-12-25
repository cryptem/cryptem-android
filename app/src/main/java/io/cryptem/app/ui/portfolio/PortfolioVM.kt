package io.cryptem.app.ui.portfolio

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.LineData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.*
import io.cryptem.app.model.ui.Coin
import io.cryptem.app.model.ui.Portfolio
import io.cryptem.app.model.ui.TimeInterval
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.base.event.UpdateWidgetEvent
import io.cryptem.app.util.L
import kodebase.livedata.SafeMutableLiveData
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioVM @Inject constructor(private val prefs : SharedPrefsRepository, private val marketRepo : MarketRepository, private val portfolioRepo : PortfolioRepository, private val analytics : AnalyticsRepository) : BaseVM() {

    val loading = MutableLiveData(false)
    val loadingCoins = SafeMutableLiveData(false)
    val portfolio = MutableLiveData<Portfolio>()
    val items = ObservableArrayList<Any>()
    val coins = ObservableArrayList<Coin>()
    val addCoinMode = MutableLiveData(false)
    val timeInterval = SafeMutableLiveData(prefs.getPortfolioTimeInterval())
    val chartData = MutableLiveData<LineData>()
    val coinsSearch = SafeMutableLiveData("")

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        prefs.setHomeScreen(HomeScreen.PORTFOLIO)
        loadPortfolioFromDb()
        loadChart()

        coinsSearch.observeForever {
            searchCoins(it)
        }
    }

    private fun searchCoins(name : String){
        coins.clear()
        viewModelScope.launch {
            kotlin.runCatching {
                marketRepo.search(name)
            }.onSuccess {
                coins.addAll(it)
            }.onFailure {
                L.e(it)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        analytics.logPortfolioScreen()
        loadPortfolio()
    }

    private fun loadPortfolioFromDb(){
        viewModelScope.launch {
            kotlin.runCatching {
                portfolioRepo.getPortfolioFromDb()
            }.onSuccess {
                showPortfolio(it)
            }.onFailure {
                L.e(it)
            }
        }
    }

    fun loadPortfolio(force : Boolean = false){
        loading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                portfolioRepo.getPortfolio(force)
            }.onSuccess {
                loading.value = false
                showPortfolio(it)
                publish(UpdateWidgetEvent())
            }.onFailure {
                loading.value = false
                L.e(it)
            }
        }
    }

    private fun showPortfolio(newPortfolio : Portfolio){
        portfolio.value = newPortfolio
        items.clear()
        items.addAll(newPortfolio.items.sortedByDescending { it.portfolioFiatPercent })
        loadChart()
    }

    fun loadCoinsNextPage(){
        loadingCoins.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                marketRepo.getCoinsNextPage()
            }.onSuccess {
                loadingCoins.value = false
                coins.addAll(it)
            }.onFailure {
                loadingCoins.value = false
                L.e(it)
            }
        }
    }

    private fun loadChart(){
        viewModelScope.launch {
            kotlin.runCatching {
                portfolioRepo.getChart(timeInterval.value)
            }.onSuccess {
                chartData.value = it
            }.onFailure {
                L.e(it)
            }
        }
    }

    fun toggleTrendTime(){
        timeInterval.value = when(timeInterval.value){
            TimeInterval.DAY -> TimeInterval.WEEK
            TimeInterval.WEEK -> TimeInterval.MONTH
            TimeInterval.MONTH -> TimeInterval.YEAR
            else -> TimeInterval.DAY
        }
        prefs.savePortfolioTimeInterval(timeInterval.value)
        loadChart()
    }

    fun showCoinDetail(){
        addCoinMode.value = true
    }

    fun showCoinDetail(coin: Coin){
        navigate(PortfolioFragmentDirections.actionPortfolioFragmentToCoinFragment(coin.id, coin.symbol, coin.name))
    }

    fun addCoin(coin: Coin){
        addCoinMode.value = false
        navigate(PortfolioFragmentDirections.actionPortfolioFragmentToCoinFragment(coin.id, coin.symbol, coin.name, addToPortfolio = true))
    }

    fun showTrezor(){
        navigate(PortfolioFragmentDirections.actionPortfolioFragmentToTrezorFragment())
    }
}