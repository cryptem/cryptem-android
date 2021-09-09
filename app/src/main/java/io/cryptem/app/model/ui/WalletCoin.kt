package io.cryptem.app.model.ui

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import io.cryptem.app.R

enum class WalletCoin(val id : String, val title : String, @ColorRes val color : Int, @DrawableRes val icon : Int) {
    BTC("bitcoin", "Bitcoin", R.color.coin_bitcoin, R.drawable.ic_coin_bitcoin),
    LTC("litecoin", "Litecoin", R.color.coin_litecoin, R.drawable.ic_coin_litecoin),
    XMR("monero", "Monero", R.color.coin_monero, R.drawable.ic_coin_monero),
    ETH("ethereum", "Ethereum", R.color.coin_ethereum, R.drawable.ic_coin_ethereum),
    ADA("cardano", "Cardano", R.color.coin_cardano, R.drawable.ic_coin_cardano),
    SOL("solana", "Solana", R.color.coin_solana, R.drawable.ic_coin_solana);

}