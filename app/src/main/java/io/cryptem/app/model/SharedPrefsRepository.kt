package io.cryptem.app.model

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.cryptem.app.model.ui.Currency
import io.cryptem.app.model.ui.SoftwareWallet
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsRepository @Inject constructor(@ApplicationContext val context: Context) {

    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    companion object {
        const val CURRENCY = "CURRENCY"
        const val PORTFOLIO_CURRENCY = "PORTFOLIO_CURRENCY"
        const val PORTFOLIO_DEPOSIT = "PORTFOLIO_DEPOSIT"
        const val PORTFOLIO_WITHDRAWAL = "PORTFOLIO_WITHDRAWAL"
        const val USE_SATS = "USE_SATS"
        const val COIN = "COIN"
        const val DEFAULT_WALLET = "DEFAULT_WALLET"
        const val ID = "ID"
        const val LAST_MARKET_COINS_REFRESH = "LAST_MARKET_COINS_REFRESH"
        const val LAST_MARKET_GLOBAL_DATA_REFRESH = "LAST_MARKET_GLOBAL_DATA_REFRESH"
        const val LAST_PORTFOLIO_REFRESH = "LAST_PORTFOLIO_REFRESH"
        const val HOME_SCREEN = "HOME_SCREEN"
        const val BINANCE_REGISTERED = "BINANCE_REGISTERED"
        const val COUNTRY = "COUNTRY"
        const val SW_WALLET = "SW_WALLET"
    }

    fun saveDefaultWallet(id: Long?) {
        if (id != null) {
            prefs.edit().putLong(DEFAULT_WALLET, id).apply()
        } else {
            prefs.edit().remove(DEFAULT_WALLET).apply()
        }
    }

    fun getDefaultWallet(): Long? {
        return if (prefs.contains(DEFAULT_WALLET)) {
            prefs.getLong(DEFAULT_WALLET, 0L)
        } else {
            null
        }
    }

    fun saveDefaultCurrency(currency: Currency) {
        return prefs.edit().putString(CURRENCY, currency.code).apply()
    }

    fun getDefaultCurrency(): Currency {
        return prefs.getString(CURRENCY, null)?.let {
            Currency(it)
        } ?: getSystemCurrency()
    }

    private fun getSystemCurrency(): Currency {
        val systemCurrency = java.util.Currency.getInstance(Locale.getDefault())
        return try {
            Currency(systemCurrency.currencyCode.toUpperCase(Locale.getDefault()))
        } catch (t: Throwable) {
            Currency.USD
        }
    }

    fun savePortfolioCurrency(currency: Currency) {
        return prefs.edit().putString(PORTFOLIO_CURRENCY, currency.code).apply()
    }

    fun getPortfolioCurrency(): Currency {
        return prefs.getString(PORTFOLIO_CURRENCY, null)?.let {
            Currency(it)
        } ?: getSystemCurrency()
    }

    fun savePortfolioDeposit(buyIn: Long) {
        prefs.edit().putLong(PORTFOLIO_DEPOSIT, buyIn).apply()
    }

    fun getPortfolioDeposit(): Long {
        return prefs.getLong(PORTFOLIO_DEPOSIT, 0)
    }

    fun savePortfolioWithdrawal(buyIn: Long) {
        prefs.edit().putLong(PORTFOLIO_WITHDRAWAL, buyIn).apply()
    }

    fun getPortfolioWithdrawal(): Long {
        return prefs.getLong(PORTFOLIO_WITHDRAWAL, 0)
    }

    fun setHomeScreen(homeScreen: HomeScreen) {
        prefs.edit().putString(HOME_SCREEN, homeScreen.name).apply()
    }

    fun getHomeScreen(): HomeScreen {
        return HomeScreen.valueOf(prefs.getString(HOME_SCREEN, HomeScreen.MARKET.name)!!)
    }

    fun saveBinanceRegistered() {
        prefs.edit().putBoolean(BINANCE_REGISTERED, true).apply()
    }

    fun isBinanceRegistered(): Boolean {
        return prefs.getBoolean(BINANCE_REGISTERED, false)
    }

    fun saveCountry(country: String) {
        prefs.edit().putString(COUNTRY, country).apply()
    }

    fun getCountry(): String {
        return prefs.getString(COUNTRY, null) ?: Locale.getDefault().country
    }

    fun saveDefaultSwWallet(swWallet: SoftwareWallet) {
        prefs.edit().putString(SW_WALLET, swWallet.packageName).apply()
    }

    fun getDefaultSwWallet(): String {
        return prefs.getString(SW_WALLET, null) ?: SoftwareWallet.EXODUS.packageName
    }
}