package io.cryptem.app.model

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import dagger.hilt.android.qualifiers.ApplicationContext
import io.cryptem.app.model.ui.Currency
import io.cryptem.app.model.ui.MarketGlobalData
import io.cryptem.app.model.ui.SoftwareWallet
import io.cryptem.app.model.ui.TimeInterval
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheRepository @Inject constructor(@ApplicationContext val context: Context) {

    val prefs = context.getSharedPreferences("cache", Context.MODE_PRIVATE)

    companion object {
        const val FEAR_AND_GREED_INDEX = "FEAR_AND_GREED_INDEX"
        const val ALTCOIN_SEASON_INDEX = "ALTCOIN_SEASON_INDEX"
        const val BTC_DOMINANCE = "BTC_DOMINANCE"
        const val ETH_DOMINANCE = "ETH_DOMINANCE"
        const val MARKETCAP = "MARKETCAP"
        const val MARKETCAP_PERCENT_CHANGE_24H = "MARKETCAP_PERCENT_CHANGE_24H"
    }

    fun saveFearAndGreedIndex(value: Int?) {
        saveInt(FEAR_AND_GREED_INDEX, value)
    }

    fun getFearAndGreedIndex(): Int? {
        return getInt(FEAR_AND_GREED_INDEX)
    }

    fun saveAltcoinSeasonIndex(value: Int?) {
        saveInt(ALTCOIN_SEASON_INDEX, value)
    }

    fun getAltcoinSeasonIndex(): Int? {
        return getInt(ALTCOIN_SEASON_INDEX)
    }

    fun saveBtcDominance(value : Double?){
        saveDouble(BTC_DOMINANCE, value)
    }

    fun getBtcDominance() : Double?{
        return getDouble(BTC_DOMINANCE)
    }

    fun saveEthDominance(value : Double?){
        saveDouble(ETH_DOMINANCE, value)
    }

    fun getEthDominance() : Double?{
        return getDouble(ETH_DOMINANCE)
    }

    fun getMarketcap() : Double?{
        return getDouble(MARKETCAP)
    }

    fun saveMarketcap(value : Double?){
        saveDouble(MARKETCAP, value)
    }

    fun getMarketcapPercentChange24h() : Double?{
        return getDouble(MARKETCAP_PERCENT_CHANGE_24H)
    }

    fun saveMarketcapPercentChange24h(value : Double?){
        saveDouble(MARKETCAP_PERCENT_CHANGE_24H, value)
    }

    fun getMarketGlobalData() : MarketGlobalData{
        return MarketGlobalData(
            marketCap = getMarketcap(),
            marketCapPercentChange24h = getMarketcapPercentChange24h(),
            btcDominance = getBtcDominance(),
            ethDominance = getEthDominance()
        )
    }

    private fun getInt(id : String) : Int?{
        return if (prefs.contains(id)) {
            prefs.getInt(id, 0)
        } else {
            null
        }
    }

    private fun getDouble(id : String) : Double?{
        return if (prefs.contains(id)) {
            prefs.getFloat(id, 0f).toDouble()
        } else {
            null
        }
    }

    private fun saveInt(id : String, value : Int?){
        if (value != null) {
            prefs.edit().putInt(id, value).apply()
        } else {
            prefs.edit().remove(id).apply()
        }
    }

    private fun saveDouble(id : String, value : Double?){
        if (value != null) {
            prefs.edit().putFloat(id, value.toFloat()).apply()
        } else {
            prefs.edit().remove(id).apply()
        }
    }
}