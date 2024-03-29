package io.cryptem.app.ui.pay

import android.content.pm.PackageManager
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.AnalyticsRepository
import io.cryptem.app.model.SharedPrefsRepository
import io.cryptem.app.model.WalletRepository
import io.cryptem.app.model.ui.SoftwareWallet
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.base.event.InstallAppEvent
import kodebase.livedata.SafeMutableLiveData
import javax.inject.Inject


@HiltViewModel
class PayVM @Inject constructor(private val walletRepository: WalletRepository, private val packageManager: PackageManager, private val prefs : SharedPrefsRepository, private val analytics : AnalyticsRepository): BaseVM() {

    val defaultWallet = SafeMutableLiveData(prefs.getDefaultSwWallet())
    val wallets = ObservableArrayList<SoftwareWallet>()

    override fun onResume(owner: LifecycleOwner) {
        analytics.logPayScreen()
        wallets.apply {
            clear()
            add(SoftwareWallet.EXODUS)
            add(SoftwareWallet.COINOMI)
        }
    }

    fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun setDefaultWallet(wallet : SoftwareWallet){
        defaultWallet.value = wallet.packageName
        prefs.saveDefaultSwWallet(wallet)
    }

    fun installApp(packageName: String){
        publish(InstallAppEvent(packageName))
    }

    fun showTrezor(){
        navigate(PayFragmentDirections.actionPayFragmentToTrezorFragment())
    }

}