package io.cryptem.app.ui.portfolio.depositwithdraw

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.cryptem.app.ui.base.BaseVM
import kodebase.livedata.SafeMutableLiveData
import javax.inject.Inject

@HiltViewModel
class DepositWithdrawVM @Inject constructor() : BaseVM() {

    val isDeposit = SafeMutableLiveData(true)
    val isWithdraw = SafeMutableLiveData(false)
    val value = MutableLiveData<String>()
    val valueNumber = SafeMutableLiveData(0L)
    val currency = MutableLiveData<String>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        value.observeForever {
            valueNumber.value = it.toLongOrNull() ?: 0L
        }
    }
}