package io.cryptem.app.ui.portfolio.settings

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.*
import io.cryptem.app.model.ui.BinanceKeys
import io.cryptem.app.model.ui.Currency
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.base.event.ScanQrEvent
import io.cryptem.app.ui.portfolio.settings.event.BinanceTestEvent
import kodebase.livedata.SafeMutableLiveData
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioSettingsVM @Inject constructor(
    private val portfolioRepo: PortfolioRepository,
    private val remoteConfigRepo: RemoteConfigRepository,
    private val prefs: SharedPrefsRepository,
    private val binanceRepository: BinanceRepository,
    private val analytics : AnalyticsRepository
) : BaseVM() {

    val deposit = MutableLiveData(portfolioRepo.getPortfolioDeposit().toString())
    val currency = MutableLiveData(portfolioRepo.getPortfolioCurrency())
    val currencies = ObservableArrayList<Currency>()
    private var currencyInitFlag = false
    val binanceSync = SafeMutableLiveData(prefs.isBinanceSyncEnabled())
    val binanceApiKey = MutableLiveData(prefs.getBinanceApiKey())
    val binanceSecretKey = MutableLiveData(prefs.getBinanceSecretKey())

    override fun onCreate(owner: LifecycleOwner) {
        currency.observeForever {
            if (currencyInitFlag) {
                savePortfolio()
            } else {
                currencyInitFlag = true
            }
        }
        binanceSync.observeForever {
            if (!it) {
                prefs.saveBinanceSync(false)
            }
        }
        loadCurrnecies()
    }

    override fun onResume(owner: LifecycleOwner) {
        analytics.logPortfolioSettingsScreen()
    }

    override fun onPause(owner: LifecycleOwner) {
        portfolioRepo.clearPortfolioCache()
    }

    fun onSwitchClick() {
        if (binanceSync.value && !binanceApiKey.value.isNullOrEmpty() && !binanceSecretKey.value.isNullOrEmpty()) {
            testBinance()
        }
    }

    private fun loadCurrnecies() {
        viewModelScope.launch {
            kotlin.runCatching {
                remoteConfigRepo.getSupportedCurrencies()
            }.onSuccess {
                currencies.addAll(it)
            }
        }
    }

    fun savePortfolio() {
        portfolioRepo.setPortfolioDeposit(deposit.value?.toLongOrNull() ?: 0L)
        portfolioRepo.setPortfolioCurrency(currency.value!!)
    }

    fun scanBinanceApiKey() {
        publish(ScanQrEvent())
    }

    fun parseBinanceKeys(json: String) {
        val moshi = Moshi.Builder().build();
        val jsonAdapter: JsonAdapter<BinanceKeys> = moshi.adapter(BinanceKeys::class.java)
        val keys = jsonAdapter.fromJson(json)
        binanceApiKey.value = keys?.apiKey
        binanceSecretKey.value = keys?.secretKey
        saveBinanceKeys()
        testBinance()
    }

    private fun saveBinanceKeys(){
        prefs.saveBinanceApiKey(binanceApiKey.value)
        prefs.saveBinanceSecretKey(binanceSecretKey.value)
    }

    fun testBinance() {
        saveBinanceKeys()
        viewModelScope.launch {
            kotlin.runCatching {
                binanceRepository.getAll()
            }.onSuccess {
                prefs.saveBinanceSync(true)
                publish(BinanceTestEvent(true))
            }.onFailure {
                prefs.saveBinanceSync(false)
                publish(BinanceTestEvent(false))
            }
        }
    }

    fun depositWithdraw(){
        navigate(PortfolioSettingsFragmentDirections.actionPortfolioSettingsFragmentToDialogDepositWithdraw(portfolioRepo.getPortfolioCurrency().toString()))
    }
}