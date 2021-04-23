package io.cryptem.app.model.ui

import androidx.lifecycle.MutableLiveData

class CryptoAddress(val coin : WalletCoin, address : String?) {
    val address = MutableLiveData<String>()

    init {
        this.address.value = address
    }
}