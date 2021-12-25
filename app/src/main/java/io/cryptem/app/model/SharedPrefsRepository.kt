package io.cryptem.app.model

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import dagger.hilt.android.qualifiers.ApplicationContext
import io.cryptem.app.model.ui.Currency
import io.cryptem.app.model.ui.SoftwareWallet
import io.cryptem.app.model.ui.TimeInterval
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
        const val PORTFOLIO_TIME_INTERVAL = "PORTFOLIO_TIME_INTERVAL"
        const val PORTFOLIO_LAST_ADD = "PORTFOLIO_LAST_ADD"
        const val DEFAULT_WALLET = "DEFAULT_WALLET"
        const val HOME_SCREEN = "HOME_SCREEN"
        const val BINANCE_REGISTERED = "BINANCE_REGISTERED"
        const val BINANCE_SYNC = "BINANCE_SYNC"
        const val BINANCE_API_KEY = "BINANCE_API_KEY"
        const val BINANCE_SECRET_KEY = "BINANCE_SECRET_KEY"
        const val COUNTRY = "COUNTRY"
        const val SW_WALLET = "SW_WALLET"
        const val COIN_PRICE_CHART_DAYS = "COIN_PRICE_CHART_DAYS"
        const val MAP_TYPE = "MAP_TYPE"
        const val FAVORITE_COINS_MODE = "FAVORITE_COINS_MODE"
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
        return try {
            val systemCurrency = java.util.Currency.getInstance(Locale.getDefault())
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

    fun savePortfolioTimeInterval(interval: TimeInterval) {
        prefs.edit().putString(PORTFOLIO_TIME_INTERVAL, interval.name).apply()
    }

    fun getPortfolioTimeInterval(): TimeInterval {
        return TimeInterval.valueOf(prefs.getString(PORTFOLIO_TIME_INTERVAL, TimeInterval.DAY.name)!!)
    }

    fun setPortfolioLastAdd() {
        prefs.edit().putLong(PORTFOLIO_LAST_ADD, System.currentTimeMillis()).apply()
    }

    fun getPortfolioLastAdd() : Long{
        return prefs.getLong(PORTFOLIO_LAST_ADD, 0L)
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
        return prefs.getString(COUNTRY, null) ?: getSystemCountry()
    }

    fun getSystemCountry(): String {
        return Locale.getDefault().country
    }

    fun saveDefaultSwWallet(swWallet: SoftwareWallet) {
        prefs.edit().putString(SW_WALLET, swWallet.packageName).apply()
    }

    fun getDefaultSwWallet(): String {
        return prefs.getString(SW_WALLET, null) ?: SoftwareWallet.EXODUS.packageName
    }

    fun saveBinanceApiKey(apiKey: String?) {
        prefs.edit().putString(BINANCE_API_KEY, apiKey).apply()
    }

    fun getBinanceApiKey(): String? {
        return prefs.getString(BINANCE_API_KEY, null)
    }

    fun saveBinanceSecretKey(secretKey: String?) {
        prefs.edit().putString(BINANCE_SECRET_KEY, secretKey).apply()
    }

    fun getBinanceSecretKey(): String? {
        return prefs.getString(BINANCE_SECRET_KEY, null)
    }

    fun saveBinanceSync(enabled : Boolean) {
        prefs.edit().putBoolean(BINANCE_SYNC, enabled).apply()
    }

    fun isBinanceSyncEnabled(): Boolean {
        return prefs.getBoolean(BINANCE_SYNC, false)
    }

    fun saveChartDays(days: Int) {
        prefs.edit().putInt(COIN_PRICE_CHART_DAYS, days).apply()
    }

    fun getChartDays(): Int {
        return prefs.getInt(COIN_PRICE_CHART_DAYS, 7)
    }

    fun saveMapType(type: Int) {
        prefs.edit().putInt(MAP_TYPE, type).apply()
    }

    fun getMapType() : Int{
        return prefs.getInt(MAP_TYPE, GoogleMap.MAP_TYPE_NORMAL)
    }

    fun saveFavoriteCoinsMode(enabled : Boolean) {
        prefs.edit().putBoolean(FAVORITE_COINS_MODE, enabled).apply()
    }

    fun isFavoriteCoinsMode(): Boolean {
        return prefs.getBoolean(FAVORITE_COINS_MODE, false)
    }
}