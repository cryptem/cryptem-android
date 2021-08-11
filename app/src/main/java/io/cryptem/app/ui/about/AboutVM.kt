package io.cryptem.app.ui.about

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.R
import io.cryptem.app.model.RemoteConfigRepository
import io.cryptem.app.model.ui.Partner
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

    val partners = ObservableArrayList<Partner>().apply {
        add(Partner("CoinGecko", R.drawable.ic_partner_coingecko, "https://coingecko.com"))
        add(Partner("Binance", R.drawable.ic_partner_binance, remoteConfigRepository.getBinanceLink()))
        add(Partner("Trezor", R.drawable.ic_partner_trezor, remoteConfigRepository.getTrezorLink()))
        add(Partner("SimpleCoin", R.drawable.ic_partner_simplecoin, remoteConfigRepository.getSimpleCoinLink()))
        add(Partner("Bitcoinovej Kanál", R.drawable.ic_partner_bitcoinovej_kanal, "https://www.youtube.com/channel/UCCegl13nmUvxUKMJqng1S-A"))
    }

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
        publish(UrlEvent("https://cryptem.io"))
    }

    fun coinGecko(){
        publish(UrlEvent("https://coingecko.com"))
    }

    fun partnerClick(partner : Partner){
        publish(UrlEvent(partner.url))
    }
}