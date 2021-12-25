package io.cryptem.app.model.ui

import kodebase.livedata.SafeMutableLiveData

class Wallet(
    val id : Long? = null,
    var coin: WalletCoin = WalletCoin.BTC,
    name: String = "",
    address: String = "",
) {
    val name = SafeMutableLiveData(name)
    val address = SafeMutableLiveData(address)

    fun getMaskedAddress() : String{
        val lastIndex = address.value.length - 4
        return address.value.replaceRange(0, if (lastIndex > 0) lastIndex else address.value.length, "****************")
    }
}