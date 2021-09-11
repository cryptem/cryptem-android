package io.cryptem.app.ui.trezor

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.AnalyticsRepository
import io.cryptem.app.model.RemoteConfigRepository
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.base.event.UrlEvent
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TrezorVM @Inject constructor(val remoteConfigRepository: RemoteConfigRepository, val analytics: AnalyticsRepository) : BaseVM() {

    val alzaEnabled = MutableLiveData(checkAlzaCz())

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        analytics.logTrezorScreen()
    }

    fun checkAlzaCz() : Boolean{
        val country = Locale.getDefault().country
        val language = Locale.getDefault().language
        return country == "CZ" || country == "SK" || language == "cs" || language == "sk"
    }

    fun buyTrezorOne(){
        publish(UrlEvent(remoteConfigRepository.getTrezorOneLink()))
    }

    fun buyTrezorOneAlza(){
        publish(UrlEvent(remoteConfigRepository.getTrezorOneAlzaLink()))
    }

    fun buyTrezorT(){
        publish(UrlEvent(remoteConfigRepository.getTrezorTLink()))
    }

    fun buyTrezorTAlza(){
        publish(UrlEvent(remoteConfigRepository.getTrezorTAlzaLink()))
    }
}