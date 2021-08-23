package io.cryptem.app.ui.coin

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.LineData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.ext.toAmountString
import io.cryptem.app.ext.toBtcString
import io.cryptem.app.ext.toFiatString
import io.cryptem.app.model.MarketRepository
import io.cryptem.app.model.PortfolioRepository
import io.cryptem.app.model.RemoteConfigRepository
import io.cryptem.app.model.SharedPrefsRepository
import io.cryptem.app.model.ui.Coin
import io.cryptem.app.model.ui.PortfolioItem
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.base.event.UrlEvent
import io.cryptem.app.util.L
import kodebase.livedata.SafeMutableLiveData
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CoinVM @Inject constructor(
    val marketRepo: MarketRepository,
    val portfolioRepo: PortfolioRepository,
    val prefs: SharedPrefsRepository,
    val remoteConfigRepository: RemoteConfigRepository
) : BaseVM() {

    val coin = MutableLiveData<Coin?>()
    val portfolioItem = MutableLiveData<PortfolioItem>()
    val amountExchange = MutableLiveData("0")
    val amountWallet = MutableLiveData("0")
    val amountTotal = MutableLiveData<String>()
    val amountTotalFiat = MutableLiveData<String>()
    val amountTotalBtc = MutableLiveData<String>()
    val priceCustom = MutableLiveData<Double?>()
    var id: String = ""
    val currency = SafeMutableLiveData(portfolioRepo.getPortfolioCurrency())
    val simpleCoinVisible = MutableLiveData<Boolean>()
    val isInPortfolio = MutableLiveData(false)
    var addToPortfolio = false
    val editMode = SafeMutableLiveData(false)
    val loadingChart = MutableLiveData(false)
    val chartData = MutableLiveData<LineData>()
    val chartRadios =
        arrayOf(SafeMutableLiveData(false), SafeMutableLiveData(false), SafeMutableLiveData(false), SafeMutableLiveData(false))

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        loadCoinDetails()
        loadPortfolio()
        loadCustomPrice()

        amountWallet.observeForever {
            recalculatePortfolio()
        }
        amountExchange.observeForever {
            recalculatePortfolio()
        }
        priceCustom.observeForever {
            recalculatePortfolio()
        }
        checkSimpleCoin()
        setupChartRadios()
    }

    private fun setupChartRadios() {
        chartRadios[0].observeForever {
            if (it) {
                loadChart(1)
                prefs.saveChartDays(1)
            }
        }
        chartRadios[1].observeForever {
            if (it) {
                loadChart(7)
                prefs.saveChartDays(7)
            }
        }
        chartRadios[2].observeForever {
            if (it) {
                loadChart(30)
                prefs.saveChartDays(30)
            }
        }
        chartRadios[3].observeForever {
            if (it) {
                loadChart(365)
                prefs.saveChartDays(365)
            }
        }
        when(prefs.getChartDays()){
            1 -> chartRadios[0].value = true
            7 -> chartRadios[1].value = true
            30 -> chartRadios[2].value = true
            365 -> chartRadios[3].value = true
        }
    }

    private fun checkSimpleCoin() {
        simpleCoinVisible.value =
            (id == "bitcoin" || id == "ethereum" || id == "litecoin" || id == "bitcoin-cash" || id == "ripple")
    }

    private fun recalculatePortfolio() {
        val amountWallet = amountWallet.value?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
        val amountExchange = amountExchange.value?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
        amountTotal.value = (amountWallet + amountExchange).toAmountString(coin.value)
        priceCustom.value?.let {
            amountTotalFiat.value =
                ((amountWallet + amountExchange) * it).toFiatString(currency.value, 0)
        }
        coin.value?.priceBtc?.currentPrice?.let {
            amountTotalBtc.value = ((amountWallet + amountExchange) * it).toBtcString()
        }
    }

    private fun loadCoinDetails() {
        viewModelScope.launch {
            kotlin.runCatching {
                marketRepo.getCoin(id = id)
            }.onSuccess {
                it?.let {
                    coin.value = it
                    recalculatePortfolio()
                    if (addToPortfolio) {
                        savePortfolio()
                    }
                }
            }.onFailure {
                L.e(it)
            }
        }
    }

    private fun loadChart(days: Int) {
        loadingChart.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                marketRepo.getChart(id, days)
            }.onSuccess {
                loadingChart.value = false
                chartData.value = it
            }.onFailure {
                loadingChart.value = false
                L.e(it)
            }
        }
    }

    private fun loadPortfolio() {
        viewModelScope.launch {
            kotlin.runCatching {
                portfolioRepo.getPortfolioCoin(id = id)
            }.onSuccess {
                if (it != null) {
                    isInPortfolio.value = true
                    //coin.value = it.coin
                    val format = NumberFormat.getInstance(Locale.getDefault())
                    format.isGroupingUsed = false
                    amountExchange.value =
                        format.format(it.amountExchange).replace("\\s".toRegex(), "")
                    amountWallet.value = format.format(it.amountWallet).replace("\\s".toRegex(), "")
                    priceCustom.value = it.coin.priceCustom?.currentPrice
                    recalculatePortfolio()
                }
            }.onFailure {
                L.e(it)
            }
        }
    }

    private fun loadCustomPrice() {
        viewModelScope.launch {
            kotlin.runCatching {
                marketRepo.loadCoinPrice(id = id, currency.value)
            }.onSuccess {
                priceCustom.value = it
                recalculatePortfolio()
            }.onFailure {
                L.e(it)
            }
        }
    }

    fun savePortfolio() {
        editMode.value = false
        if (amountExchange.value.isNullOrEmpty()){
            amountExchange.value = "0"
        }
        if (amountWallet.value.isNullOrEmpty()){
            amountWallet.value = "0"
        }

        coin.value?.let {
            viewModelScope.launch {
                kotlin.runCatching {
                    portfolioRepo.addPortfolioCoin(
                        it,
                        amountExchange = amountExchange.value?.replace(",", ".")?.toDoubleOrNull()
                            ?: 0.0,
                        amountWallet = amountWallet.value?.replace(",", ".")?.toDoubleOrNull()
                            ?: 0.0
                    )
                }.onSuccess {
                    isInPortfolio.value = true
                    recalculatePortfolio()
                }.onFailure {
                    L.e(it)
                }
            }
        }
    }

    fun remove() {
        viewModelScope.launch {
            kotlin.runCatching {
                portfolioRepo.removePortfolioCoin(coin.value!!.id)
            }.onSuccess {
                editMode.value = false
                isInPortfolio.value = false
            }.onFailure {
                L.e(it)
            }
        }
    }

    fun showSimpleCoin() {
        publish(UrlEvent(remoteConfigRepository.getSimpleCoinLink()))
    }

    fun showCoinGecko() {
        publish(UrlEvent("https://www.coingecko.com/en/coins/${coin.value?.id}"))
    }

    fun showBinanceBtcTrade() {
        publish(UrlEvent("https://www.binance.com/en/trade/${coin.value?.symbol}_BTC"))
    }

    fun showBinanceUsdtTrade() {
        publish(UrlEvent("https://www.binance.com/en/trade/${coin.value?.symbol}_USDT"))
    }

    fun getBinanceLink(): String {
        return remoteConfigRepository.getBinanceLink()
    }
}