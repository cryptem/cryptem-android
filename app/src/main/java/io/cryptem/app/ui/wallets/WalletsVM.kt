package io.cryptem.app.ui.wallets

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.AnalyticsRepository
import io.cryptem.app.model.WalletRepository
import io.cryptem.app.model.ui.Wallet
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.base.listener.OnWalletSelectedListener
import io.cryptem.app.util.L
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletsVM @Inject constructor(val repo : WalletRepository, val analytics: AnalyticsRepository) : BaseVM(), OnWalletSelectedListener {

    val wallets = ObservableArrayList<Wallet>()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        analytics.logWalletsScreen()
        loadWallets()
    }

    fun loadWallets(){
        viewModelScope.launch {
            kotlin.runCatching {
                repo.getWallets()
            }.onSuccess {
                wallets.clear()
                wallets.addAll(it)
            }.onFailure {
                L.e(it)
            }
        }
    }

    fun editWallet(wallet : Wallet){
        wallet.id?.let {
            navigate(WalletsFragmentDirections.actionWalletsFragmentToWalletFragment(it, wallet.coin))
        }
    }

    fun addWallet(){
        navigate(WalletsFragmentDirections.actionWalletsFragmentToWalletFragment())
    }

    override fun onWalletSelected(wallet: Wallet?) {
        wallet?.let {
            editWallet(it)
        }
    }
}