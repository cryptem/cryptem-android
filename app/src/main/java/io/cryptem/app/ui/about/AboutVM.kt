package io.cryptem.app.ui.about

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.RemoteConfigRepository
import io.cryptem.app.model.ui.WalletCoin
import io.cryptem.app.ui.about.event.ClipboardEvent
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.base.event.UrlEvent
import javax.inject.Inject


@HiltViewModel
class AboutVM @Inject constructor(val clipboardManager: ClipboardManager, val remoteConfigRepository: RemoteConfigRepository, val vibrator: Vibrator) : BaseVM() {

    val btcAddress = MutableLiveData("")
    val ethAddress = MutableLiveData("")
    val adaAddress = MutableLiveData("")
    val ltcAddress = MutableLiveData("")
    val xmrAddress = MutableLiveData("")

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        btcAddress.value = remoteConfigRepository.getDonateAddress("BTC")
        ethAddress.value = remoteConfigRepository.getDonateAddress("ETH")
        adaAddress.value = remoteConfigRepository.getDonateAddress("ADA")
        ltcAddress.value = remoteConfigRepository.getDonateAddress("LTC")
        xmrAddress.value = remoteConfigRepository.getDonateAddress("XMR")
    }

    fun copyAddress(address: String, coin : WalletCoin){
        val clip = ClipData.newPlainText("Cryptem Donate", address)
        clipboardManager.setPrimaryClip(clip)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(50)
        }

        publish(ClipboardEvent(coin))
    }

    fun gitHub(){
        publish(UrlEvent("https://github.com/cryptem"))
    }

    fun coinGecko(){
        publish(UrlEvent("https://coingecko.com"))
    }
}