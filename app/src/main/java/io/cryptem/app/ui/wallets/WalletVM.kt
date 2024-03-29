package io.cryptem.app.ui.wallets

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.AnalyticsRepository
import io.cryptem.app.model.SharedPrefsRepository
import io.cryptem.app.model.WalletRepository
import io.cryptem.app.model.ui.Wallet
import io.cryptem.app.model.ui.WalletCoin
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.base.event.ScanQrEvent
import io.cryptem.app.ui.base.listener.OnWalletCoinSelectedListener
import io.cryptem.app.ui.wallets.event.RemoveWalletEvent
import io.cryptem.app.util.L
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletVM @Inject constructor(val repo : WalletRepository, val prefs : SharedPrefsRepository, val analytics: AnalyticsRepository) : BaseVM(), OnWalletCoinSelectedListener {

    val coins = ObservableArrayList<WalletCoin>()
    val wallet = MutableLiveData<Wallet>()
    val scanWarning = MutableLiveData(false)
    override val selectedCoin = MutableLiveData<WalletCoin>()

    override fun onCreate(owner: LifecycleOwner) {
        selectedCoin.observeForever {
            wallet.value?.coin = it
        }

        coins.clear()
        coins.addAll(repo.getSupportedCoins())
    }

    override fun onResume(owner: LifecycleOwner) {
        analytics.logWalletScreen()
    }

    fun scanAddress(){
        scanWarning.value = true
        wallet.value?.let {
            publish(ScanQrEvent())
        }
    }

    fun loadWallet(id: Long) {
        viewModelScope.launch {
            kotlin.runCatching {
                repo.getWallet(id)
            }.onSuccess {
                it?.let {
                    wallet.value = it
                    selectedCoin.value = it.coin
                }
            }.onFailure {
                L.e(it)
            }
        }
    }

    fun save(){
        wallet.value?.let {
            viewModelScope.launch {
                kotlin.runCatching {
                    repo.saveWallet(it)
                }.onSuccess {
                    navigateUp()
                }.onFailure {
                    L.e(it)
                }
            }
        }
    }

    fun remove(){
        publish(RemoveWalletEvent())
    }

    override fun onCoinSelected(coin: WalletCoin) {
        selectedCoin.value = coin
    }

    fun confirmRemove() {
        wallet.value?.let {
            viewModelScope.launch {
                kotlin.runCatching {
                    repo.removeWallet(it)
                    if (it.id == prefs.getDefaultWallet()){
                        prefs.saveDefaultWallet(null)
                    }
                }.onSuccess {
                    navigateUp()
                }.onFailure {
                    L.e(it)
                }
            }
        }
    }


}