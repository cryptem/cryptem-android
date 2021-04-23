package io.cryptem.app.ui.coin

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.ext.toAmountString
import io.cryptem.app.ext.toBtcString
import io.cryptem.app.ext.toFiatString
import io.cryptem.app.model.MarketRepository
import io.cryptem.app.model.PortfolioRepository
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
    val portfolioRepo: PortfolioRepository
) : BaseVM() {

    val coin = MutableLiveData<Coin?>()
    val porfolioItem = MutableLiveData<PortfolioItem>()
    val amountExchange = MutableLiveData<String>()
    val amountWallet = MutableLiveData<String>()
    val amountTotal = MutableLiveData<String>()
    val amountTotalFiat = MutableLiveData<String>()
    val amountTotalBtc = MutableLiveData<String>()
    val priceCustom = MutableLiveData<Double?>()
    var id: String = ""
    val currency = SafeMutableLiveData(portfolioRepo.getPortfolioCurrency())

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        loadCoin()
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
    }

    private fun recalculatePortfolio(){
        val amountWallet = amountWallet.value?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
        val amountExchange = amountExchange.value?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
        amountTotal.value = (amountWallet + amountExchange).toAmountString(coin.value)
        priceCustom.value?.let {
            amountTotalFiat.value = ((amountWallet + amountExchange)*it).toFiatString(currency.value, 0)
        }
        coin.value?.priceBtc?.currentPrice?.let {
            amountTotalBtc.value = ((amountWallet + amountExchange)*it).toBtcString()
        }
    }

    fun loadCoin() {
        viewModelScope.launch {
            kotlin.runCatching {
                marketRepo.getCoin(id = id)
            }.onSuccess {
                coin.value = it
                recalculatePortfolio()
            }.onFailure {
                L.e(it)
            }
        }
    }

    fun loadPortfolio() {
        viewModelScope.launch {
            kotlin.runCatching {
                portfolioRepo.getPortfolioCoin(id = id)
            }.onSuccess {
                if (it != null){
                    coin.value = it.coin
                    val format = NumberFormat.getInstance(Locale.getDefault())
                    amountExchange.value = format.format(it.amountExchange).replace("\\s".toRegex(), "")
                    amountWallet.value = format.format(it.amountWallet).replace("\\s".toRegex(), "")
                    priceCustom.value = it.coin.priceCustom?.currentPrice
                    recalculatePortfolio()
                }
            }.onFailure {
                L.e(it)
            }
        }
    }

    fun loadCustomPrice(){
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

    fun save() {
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
                navigate(CoinFragmentDirections.actionPortfolioEditFragmentToPortfolioFragment())
            }.onFailure {
                L.e(it)
            }
        }
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
}