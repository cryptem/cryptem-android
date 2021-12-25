package io.cryptem.app.ui.base.listener

import io.cryptem.app.model.ui.Wallet

interface OnWalletSelectedListener {
    fun onWalletSelected(wallet : Wallet?)
}