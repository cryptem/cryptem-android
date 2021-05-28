package io.cryptem.app.ui.trezor

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.model.RemoteConfigRepository
import io.cryptem.app.ui.base.BaseVM
import io.cryptem.app.ui.base.event.UrlEvent
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TrezorVM @Inject constructor(val remoteConfigRepository: RemoteConfigRepository) : BaseVM() {

    val alzaEnabled = MutableLiveData(checkAlzaCz())

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