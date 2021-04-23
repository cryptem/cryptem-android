package io.cryptem.app.ui.request

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.ext.toFiatString
import io.cryptem.app.model.MarketRepository
import io.cryptem.app.model.RemoteConfigRepository
import io.cryptem.app.model.SharedPrefsRepository
import io.cryptem.app.model.WalletRepository
import io.cryptem.app.model.ui.Currency
import io.cryptem.app.model.ui.Wallet
import io.cryptem.app.model.ui.WalletCoin
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.base.listener.OnWalletCoinSelectedListener
import io.cryptem.app.ui.base.listener.OnWalletSelectedListener
import io.cryptem.app.util.L
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestVM @Inject constructor(
    private val repository: WalletRepository,
    private val marketRepository: MarketRepository,
    private val prefs: SharedPrefsRepository,
    private val remoteConfigRepo : RemoteConfigRepository
) : BaseVM(), OnWalletCoinSelectedListener, OnWalletSelectedListener {

    val loadingPrice = MutableLiveData(false)

    val fiatAmountString = MutableLiveData("")
    val fiatAmount = MutableLiveData(0.0)
    val cryptoAmount = MutableLiveData(0.0)

    val coins = ObservableArrayList<WalletCoin?>()
    override val selectedCoin = MutableLiveData<WalletCoin>()
    val coinPrice = MutableLiveData(0.0)

    val rate = MutableLiveData<Double>()

    val currencies = ObservableArrayList<Currency>().apply {
        addAll(remoteConfigRepo.getSupportedCurrencies())
    }
    val currency = MutableLiveData<Currency>()

    val wallets = ObservableArrayList<Wallet>()
    val wallet = MutableLiveData<Wallet>()
    val walletSelector = MutableLiveData(false)
    val qr = MutableLiveData<String>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
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
        coins.addAll(repository.getSupportedCoins())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
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

    private fun init(){
        viewModelScope.launch {
            kotlin.runCatching {
               repository.getDefaultWallet()
            }.onSuccess {
                if (it != null){
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
                    repository.getWallets(it)
                }.onSuccess {
                    when {
                        it.isEmpty() -> {
                            onWalletSelected(null)
                        }
                        it.size == 1 -> {
                            onWalletSelected(it.first())
                        }
                        else -> {
                            walletSelector.value = true
                            wallets.clear()
                            wallets.addAll(it)
                        }
                    }
                }.onFailure {
                    L.e(it)
                }
            }
        }
    }

    override fun onWalletSelected(wallet: Wallet?) {
        repository.setDefaultWallet(wallet?.id)
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
        if (cryptoAmount.value != null && selectedCoin.value != null && currency.value != null) {
            qr.value = "bitcoin:${wallet.value?.address}?amount=${cryptoAmount.value}"
        }
    }

    fun addAddress() {
        selectedCoin.value?.let {
            navigate(RequestFragmentDirections.actionRequestFragmentToWalletFragment(coin = it))
        }
    }

    fun hasDefaultWallet() : Boolean{
        return repository.hasDefaultWallet()
    }
}