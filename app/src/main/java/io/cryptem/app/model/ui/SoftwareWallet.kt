package io.cryptem.app.model.ui

import io.cryptem.app.R
import io.cryptem.app.model.ui.WalletCoin.*

enum class SoftwareWallet(val title : String, val packageName : String, val icon : Int, val supportedCoins : List<WalletCoin>) {
    EXODUS("Exodus", "exodusmovement.exodus", R.drawable.ic_wallet_exodus, listOf(BTC, ETH, ADA, LTC, XMR)),
    COINOMI("Coinomi", "com.coinomi.wallet",  R.drawable.ic_wallet_coinomi,  listOf(BTC, ETH, LTC, XMR)),
    MYCELIUM("Mycelium", "com.mycelium.wallet",  R.drawable.ic_wallet_mycelium,  listOf(BTC, ETH)),
    YOROI("Yoroi", "com.emurgo", R.drawable.ic_wallet_yoroi,  listOf(ADA)),
}