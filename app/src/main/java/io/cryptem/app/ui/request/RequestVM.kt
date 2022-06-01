package io.cryptem.app.ui.request

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.ext.toFiatString
import io.cryptem.app.model.*
import io.cryptem.app.model.ui.Currency
import io.cryptem.app.model.ui.SoftwareWallet
import io.cryptem.app.model.ui.Wallet
import io.cryptem.app.model.ui.WalletCoin
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.base.event.RunAppEvent
import io.cryptem.app.ui.base.listener.OnWalletCoinSelectedListener
import io.cryptem.app.ui.base.listener.OnWalletSelectedListener
import io.cryptem.app.util.L
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RequestVM @Inject constructor(
    private val walletRepository: WalletRepository,
    private val marketRepository: MarketRepository,
    private val prefs: SharedPrefsRepository,
    private val remoteConfigRepo: RemoteConfigRepository,
    private val analytics : AnalyticsRepository
) : BaseVM(), OnWalletCoinSelectedListener, OnWalletSelectedListener {

    val loadingPrice = MutableLiveData(false)

    val fiatAmountString = MutableLiveData("")
    val fiatAmount = MutableLiveData(0.0)
    val cryptoAmount = MutableLiveData(0.0)

    val coins = ObservableArrayList<WalletCoin?>()
    override val selectedCoin = MutableLiveData<WalletCoin>()
    val coinPrice = MutableLiveData(0.0)

    val rate = MutableLiveData<Double>()

    val currencies = ObservableArrayList<Currency>()
    val currency = MutableLiveData<Currency>()

    val wallets = ObservableArrayList<Wallet>()
    val wallet = MutableLiveData<Wallet?>()
    val walletSelector = MutableLiveData(false)
    val qr = MutableLiveData<String>()
    var isInit = false

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    override fun onCreate(owner: LifecycleOwner) {
        prefs.setHomeScreen(HomeScreen.PAYMENT_REQUEST)
        loadCurrnecies()

        fiatAmountString.observeForever {
            fiatAmount.value = it.toDoubleOrNull() ?: 0.0
            calculateCryptoAmount()
        }

        currency.observeForever {
            prefs.saveDefaultCurrency(it)
            loadCoinPrice()
        }
        selectedCoin.observeForever {
            loadCoinPrice()
        }

        currency.value = prefs.getDefaultCurrency()

        coins.clear()
        coins.addAll(walletRepository.getSupportedCoins())
    }

    override fun onResume(owner: LifecycleOwner) {
        analytics.logRequestScreen()
        isInit = false
        init()
    }

    override fun onCoinSelected(coin: WalletCoin) {
        this.selectedCoin.value = coin
        getWallets()
    }

    private fun clearPrices() {
        coinPrice.value = 0.0
        cryptoAmount.value = 0.0
        rate.value = 0.0
    }

    private fun loadCurrnecies(){
        viewModelScope.launch {
            kotlin.runCatching {
                remoteConfigRepo.getSupportedCurrencies()
            }.onSuccess {
                currencies.addAll(it)
            }
        }
    }

    private fun loadCoinPrice() {
        clearPrices()
        if (selectedCoin.value != null && currency.value != null) {
            loadingPrice.value = true
            viewModelScope.launch {
                kotlin.runCatching {
                    marketRepository.loadCoinPrice(
                        id = selectedCoin.value!!.id,
                        currency = currency.value!!
                    )
                }.onSuccess { price ->
                    L.d("loaded price $price")
                    price?.let {
                        coinPrice.value = it
                        rate.value = 1.0 / it
                        calculateCryptoAmount()
                    }
                    loadingPrice.value = false
                }.onFailure {
                    loadingPrice.value = false
                    L.e(it)
                }
            }
        }
    }

    private fun init() {
        viewModelScope.launch {
            kotlin.runCatching {
                walletRepository.getDefaultWallet()
            }.onSuccess {
                if (it != null) {
                    onCoinSelected(it.coin)
                } else {
                    onCoinSelected(WalletCoin.BTC)
                }
            }.onFailure {
                L.e(it)
            }
        }
    }

    private fun getWallets() {
        selectedCoin.value?.let {
            viewModelScope.launch {
                kotlin.runCatching {
                    walletRepository.getWallets(it)
                }.onSuccess {
                    when {
                        it.isEmpty() -> {
                            onWalletSelected(null)
                        }
                        it.size == 1 -> {
                            onWalletSelected(it.first())
                        }
                        else -> {
                            val foundDefaultWallet = it.find { it.id == prefs.getDefaultWallet() }
                            if (!isInit && foundDefaultWallet != null) {
                                onWalletSelected(foundDefaultWallet)
                                isInit = true
                            } else {
                                wallet.value = null
                                walletSelector.value = true
                                wallets.clear()
                                wallets.addAll(it)
                            }
                        }
                    }
                }.onFailure {
                    L.e(it)
                }
            }
        }
    }

    override fun onWalletSelected(wallet: Wallet?) {
        walletRepository.setDefaultWallet(wallet?.id)
        this.wallet.value = wallet
        wallets.clear()
        walletSelector.value = false
        generateQR()
    }

    fun formatFiat(amount: Double, currency: Currency): String {
        return amount.toFiatString(currency)
    }

    private fun calculateCryptoAmount() {
        if (fiatAmountString.value != null && currency.value != null) {
            val fiatAmountNumber = fiatAmountString.value?.toDoubleOrNull() ?: 0.0
            cryptoAmount.value = fiatAmountNumber * (rate.value ?: 0.0)
        }
        generateQR()
    }

    private fun generateQR() {

        val address = wallet.value?.address?.value
        val amount =
            DecimalFormat("#.########", DecimalFormatSymbols.getInstance(Locale.US)).format(
                cryptoAmount.value
            )

        if (address != null && amount != null && selectedCoin.value != null && currency.value != null) {
            qr.value = when (selectedCoin.value) {
                WalletCoin.BTC -> "bitcoin:${address}?amount=${amount}"
                WalletCoin.LTC -> "litecoin:${address}?amount=${amount}"
                WalletCoin.XMR -> "monero:${address}?amount=${amount}"
                WalletCoin.ETH -> "ethereum:${address}?amount=${amount}"
                WalletCoin.ADA -> "cardano:${address}?amount=${amount}"
                WalletCoin.SOL -> "solana:${address}?amount=${amount}"
                else -> null
            }
        }
    }

    fun addAddress() {
        selectedCoin.value?.let {
            navigate(RequestFragmentDirections.actionRequestFragmentToWalletFragment(coin = it))
        }
    }

    fun createWallet(){
        publish(RunAppEvent(SoftwareWallet.EXODUS.packageName))
    }

    fun hasDefaultWallet(): Boolean {
        return walletRepository.hasDefaultWallet()
    }
}