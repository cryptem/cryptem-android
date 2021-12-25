package io.cryptem.app.model

import android.content.Context
import android.os.Bundle
import com.google.android.gms.maps.MapFragment
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import io.cryptem.app.ui.about.AboutFragment
import io.cryptem.app.ui.coin.CoinFragment
import io.cryptem.app.ui.market.MarketFragment
import io.cryptem.app.ui.pay.PayFragment
import io.cryptem.app.ui.poieditor.PoiEditorFragment
import io.cryptem.app.ui.portfolio.PortfolioFragment
import io.cryptem.app.ui.portfolio.settings.PortfolioSettingsFragment
import io.cryptem.app.ui.request.RequestFragment
import io.cryptem.app.ui.trezor.TrezorFragment
import io.cryptem.app.ui.wallets.WalletFragment
import io.cryptem.app.ui.wallets.WalletsFragment
import javax.inject.Inject


class AnalyticsRepository @Inject constructor(@ApplicationContext context : Context) {

    private val analytics = FirebaseAnalytics.getInstance(context)

    fun logMarketScreen(){
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Market")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, MarketFragment::class.simpleName)
        })
    }

    fun logPortfolioScreen(){
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Portfolio")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, PortfolioFragment::class.simpleName)
        })
    }

    fun logPortfolioSettingsScreen(){
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Portfolio Settings")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, PortfolioSettingsFragment::class.simpleName)
        })
    }

    fun logPayScreen(){
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Pay")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, PayFragment::class.simpleName)
        })
    }

    fun logRequestScreen(){
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Request Payment")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, RequestFragment::class.simpleName)
        })
    }

    fun logMapScreen(){
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Map")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, MapFragment::class.simpleName)
        })
    }

    fun logTrezorScreen(){
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Trezor")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, TrezorFragment::class.simpleName)
        })
    }

    fun logAboutScreen(){
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "About")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, AboutFragment::class.simpleName)
        })
    }

    fun logWalletsScreen(){
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Wallets")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, WalletsFragment::class.simpleName)
        })
    }

    fun logWalletScreen(){
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Wallet")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, WalletFragment::class.simpleName)
        })
    }

    fun logPoiEditorScreen(){
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Poi Editor")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, PoiEditorFragment::class.simpleName)
        })
    }

    fun logCoinScreen(symbol : String){
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Coin")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, CoinFragment::class.simpleName)
            putString(FirebaseAnalytics.Param.CURRENCY, symbol)
        })
    }
}