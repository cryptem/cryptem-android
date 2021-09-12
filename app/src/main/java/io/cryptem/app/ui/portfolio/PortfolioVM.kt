package io.cryptem.app.ui.portfolio

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.*
import io.cryptem.app.model.ui.*
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
    val deposit = MutableLiveData<String>()
    val depositNumber = MutableLiveData<Long>()
    val items = ObservableArrayList<Any>()
    val coins = ObservableArrayList<Coin>()
    val addCoinMode = MutableLiveData(false)
    val trendTime = MutableLiveData(PercentTimeInterval.DAY)
    val layoutStrategy = PortfolioLayoutStrategy()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        prefs.setHomeScreen(HomeScreen.PORTFOLIO)
        loadPortfolioFromDb()
        loadFromCache()
        if (coins.isEmpty()){
            loadCoins()
        }
    }

    private fun loadFromCache(){
        coins.addAll(marketRepo.getCoinsFromCache())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        analytics.logPortfolioScreen()
        loadPortfolio()
    }

    fun loadPortfolioFromDb(){
        viewModelScope.launch {
            kotlin.runCatching {
                portfolioRepo.getPortfolioFromDb()
            }.onSuccess {
                portfolio.value = it
                depositNumber.value = it.deposit
                deposit.value = it.deposit.toString()
                items.clear()
                items.addAll(it.items.sortedByDescending { it.portfolioFiatPercent })
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
                portfolio.value = it
                deposit.value = it.deposit.toString()
                items.clear()
                items.addAll(it.items.sortedByDescending { it.portfolioFiatPercent })
                publish(UpdateWidgetEvent())
            }.onFailure {
                loading.value = false
                L.e(it)
            }
        }
    }

    fun loadCoins(){
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

    fun toggleTrendTime(){
        trendTime.value = when(trendTime.value){
            PercentTimeInterval.DAY -> PercentTimeInterval.WEEK
            PercentTimeInterval.WEEK -> PercentTimeInterval.MONTH
            PercentTimeInterval.MONTH -> PercentTimeInterval.DAY
            null -> PercentTimeInterval.DAY
        }
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

    fun savePortfolio(){
        portfolioRepo.setPortfolioDeposit(depositNumber.value ?: 0L)
        loadPortfolio(true)
    }

    fun depositWithdraw(){
        navigate(PortfolioFragmentDirections.actionPortfolioFragmentToDialogDepositWithdraw(portfolioRepo.getPortfolioCurrency().toString()))
    }

}