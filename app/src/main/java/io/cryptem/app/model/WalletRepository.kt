package io.cryptem.app.model

import io.cryptem.app.model.db.WalletDatabase
import io.cryptem.app.model.db.toDbEntity
import io.cryptem.app.model.db.toUiEntity
import io.cryptem.app.model.ui.Currency
import io.cryptem.app.model.ui.Wallet
import io.cryptem.app.model.ui.WalletCoin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    val prefs: SharedPrefsRepository,
    val db: WalletDatabase,
    val marketRepo: MarketRepository
) {

    fun getMyAddress(coin: WalletCoin): String {
        return ""
    }

    fun getSupportedCoins(): List<WalletCoin> {
        return listOf(
            WalletCoin.BTC,
            WalletCoin.LTC,
            WalletCoin.XMR
        )
    }

    suspend fun saveWallet(wallet: Wallet) {

        var id = wallet.id
        if (wallet.id == null) {
            id = db.dao().addWallet(wallet.toDbEntity())
        } else {
            db.dao().updateWallet(wallet.toDbEntity())
        }
        if (prefs.getDefaultWallet() == null){
            setDefaultWallet(id)
        }
    }

    suspend fun getWallets(coin: WalletCoin? = null): List<Wallet> {
        return if (coin == null) {
            db.dao().getWallets()
        } else {
            db.dao().getWallets(coin.name)
        }.map {
            it.toUiEntity()
        }
    }


    suspend fun getWallet(id: Long) : Wallet? {
        return db.dao().getWallet(id)?.toUiEntity()
    }

    fun hasDefaultWallet() : Boolean{
        return prefs.getDefaultWallet() != null
    }


    fun setDefaultWallet(id: Long?) {
        prefs.saveDefaultWallet(id)
    }

    suspend fun getDefaultWallet() : Wallet? {
        return prefs.getDefaultWallet()?.let {
            return db.dao().getWallet(it)?.toUiEntity()
        }
    }

    fun getPortfolioCurrency(): Currency {
        return prefs.getPortfolioCurrency()
    }

    fun setPortfolioDeposit(amount: Long) {
        prefs.savePortfolioDeposit(amount)
    }

    fun setPortfolioCurrency(currency: Currency) {
        prefs.savePortfolioCurrency(currency)
    }

    fun getPortfolioDeposit(): Long {
        return prefs.getPortfolioDeposit()
    }

    suspend fun removeWallet(it: Wallet) {
        db.dao().deleteWallet(it.toDbEntity())
    }



}