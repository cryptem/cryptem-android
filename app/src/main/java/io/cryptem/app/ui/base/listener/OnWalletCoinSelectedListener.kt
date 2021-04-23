package io.cryptem.app.ui.base.listener

import androidx.lifecycle.LiveData
import io.cryptem.app.model.ui.WalletCoin

interface OnWalletCoinSelectedListener {
    fun onCoinSelected(coin : WalletCoin)
    val selectedCoin : LiveData<WalletCoin>
}