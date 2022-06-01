package io.cryptem.app.model.ui

import io.cryptem.app.R
import io.cryptem.app.model.ui.WalletCoin.*

enum class SoftwareWallet(val title : String, val packageName : String, val icon : Int, val supportedCoins : List<WalletCoin>) {
    EXODUS("Exodus", "exodusmovement.exodus", R.drawable.ic_wallet_exodus, listOf(BTC, LTC, XMR)),
    COINOMI("Coinomi", "com.coinomi.wallet",  R.drawable.ic_wallet_coinomi,  listOf(BTC, LTC, XMR))
}