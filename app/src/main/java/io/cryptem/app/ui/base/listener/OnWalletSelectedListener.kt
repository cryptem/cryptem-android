package io.cryptem.app.ui.base.listener

import androidx.lifecycle.LiveData
import io.cryptem.app.model.ui.Wallet
import io.cryptem.app.model.ui.WalletCoin

interface OnWalletSelectedListener {
    fun onWalletSelected(wallet : Wallet?)
}