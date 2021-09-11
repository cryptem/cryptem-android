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
import io.cryptem.app.model.*
import io.cryptem.app.model.ui.Coin
import io.cryptem.app.model.ui.CoinPrice
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.base.event.UrlEvent
import io.cryptem.app.util.L
import kodebase.livedata.SafeMutableLiveData
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CoinVM @Inject constructor(
    private val marketRepo: MarketRepository,
    private val portfolioRepo: PortfolioRepository,
    val prefs: SharedPrefsRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val analytics : AnalyticsRepository
) : BaseVM() {

    var id : String = ""
    var symbol : String = ""
    val coin = MutableLiveData<Coin>()
    val currency = SafeMutableLiveData(portfolioRepo.getPortfolioCurrency())

    val amountExchange = MutableLiveData("")
    val amountWallet = MutableLiveData("")
    val amountTotal = MutableLiveData<String>()
    val amountTotalFiat = MutableLiveData<String>()
    val amountTotalBtc = MutableLiveData<String>()

    val simpleCoinVisible = MutableLiveData<Boolean>()
    val isInPortfolio = MutableLiveData(false)
    val editMode = SafeMutableLiveData(false)
    val loadingChart = MutableLiveData(false)

    val chartData = MutableLiveData<LineData>()
    val chartRadios =
        arrayOf(
            SafeMutableLiveData(false),
            SafeMutableLiveData(false),
            SafeMutableLiveData(false),
            SafeMutableLiveData(false)
        )
    // Prevents double loading
    var currentChartDays: Int = 0
    val chartSelectedDate = MutableLiveData<String>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        checkIsInPortfolio()
        checkSimpleCoin()
        setupChartRadios()

        getCoinFromCache()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        analytics.logCoinScreen(symbol)
        loadData()
    }

    private fun checkIsInPortfolio() {
        viewModelScope.launch {
            kotlin.runCatching {
                portfolioRepo.isInPortfolio(id)
            }.onSuccess {
                isInPortfolio.value = it
            }.onFailure {
                L.e(it)
            }
        }
    }

    private fun setupChartRadios() {
        setupRadio(0, 1)
        setupRadio(1, 7)
        setupRadio(2, 30)
        setupRadio(3, 365)

        when (prefs.getChartDays()) {
            1 -> chartRadios[0].value = true
            7 -> chartRadios[1].value = true
            30 -> chartRadios[2].value = true
            365 -> chartRadios[3].value = true
        }
    }

    private fun setupRadio(radioIndex: Int, days: Int) {
        chartRadios[radioIndex].observeForever {
            if (it && currentChartDays != days) {
                loadChart(days)
                prefs.saveChartDays(days)
                currentChartDays = days
            }
        }
    }

    private fun checkSimpleCoin() {
            simpleCoinVisible.value =
                (id == "bitcoin" || id == "ethereum" || id == "litecoin" || id == "bitcoin-cash" || id == "ripple")
    }

    private fun recalculatePortfolio() {
        val amountWallet = amountWallet.value?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
        val amountExchange = amountExchange.value?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
        amountTotal.value = (amountWallet + amountExchange).toAmountString(symbol)
        coin.value?.priceCustom?.currentPrice?.let {
            amountTotalFiat.value =
                ((amountWallet + amountExchange) * it).toFiatString(currency.value, 0)
        }

        coin.value?.priceBtc?.currentPrice?.let {
            amountTotalBtc.value = ((amountWallet + amountExchange) * it).toBtcString()
        }
    }

    private fun getCoinFromCache(){
        viewModelScope.launch {
            kotlin.runCatching {
                marketRepo.getCoinFromCache(id)
            }.onSuccess {
                it?.let {
                    coin.value = it
                }
            }
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
                chartData.value = it?.data
            }.onFailure {
                loadingChart.value = false
                L.e(it)
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            kotlin.runCatching {
                portfolioRepo.getPortfolioCoin(id = id)
            }.onSuccess {
                if (it != null) {
                    val format = NumberFormat.getInstance(Locale.getDefault())
                    format.isGroupingUsed = false
                    amountExchange.value =
                        format.format(it.amountExchange).replace("\\s".toRegex(), "")
                    amountWallet.value =
                        format.format(it.amountWallet).replace("\\s".toRegex(), "")
                    recalculatePortfolio()
                    loadCustomPrice()
                }
                loadCoinDetails()
            }.onFailure {
                L.e(it)
            }
        }
    }

    private fun loadCustomPrice() {
        if (coin.value?.priceCustom == null) {
            viewModelScope.launch {
                kotlin.runCatching {
                    marketRepo.loadCoinPrice(id = id, currency.value)
                }.onSuccess {
                    coin.value?.priceCustom = CoinPrice(it)
                    recalculatePortfolio()
                }.onFailure {
                    L.e(it)
                }
            }
        }
    }

    fun savePortfolio() {
        editMode.value = false
        if (amountExchange.value.isNullOrEmpty()) {
            amountExchange.value = "0"
        }
        if (amountWallet.value.isNullOrEmpty()) {
            amountWallet.value = "0"
        }

        coin.value?.let {
            viewModelScope.launch {
                kotlin.runCatching {
                    portfolioRepo.addPortfolioCoin(
                        it,
                        amountExchange = amountExchange.value?.replace(",", ".")
                            ?.toDoubleOrNull()
                            ?: 0.0,
                        amountWallet = amountWallet.value?.replace(",", ".")?.toDoubleOrNull()
                            ?: 0.0
                    )
                }.onSuccess {
                    isInPortfolio.value = true
                    loadCustomPrice()
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
        publish(UrlEvent("https://www.coingecko.com/en/coins/${id}"))
    }

    fun showBinanceBtcTrade() {
        publish(UrlEvent("https://www.binance.com/en/trade/${symbol}_BTC"))
    }

    fun showBinanceUsdtTrade() {
        publish(UrlEvent("https://www.binance.com/en/trade/${symbol}_USDT"))
    }

    fun getBinanceLink(): String {
        return remoteConfigRepository.getBinanceLink()
    }

    fun setSelectedTimestamp(timestamp: Long) {
        val formatter = if (currentChartDays == 1){
            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
        } else {
            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
        }
        chartSelectedDate.value = formatter.format(Date(timestamp))
    }
}