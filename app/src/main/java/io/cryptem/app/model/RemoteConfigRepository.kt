package io.cryptem.app.model

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import io.cryptem.app.BuildConfig
import io.cryptem.app.R
import io.cryptem.app.model.ui.Currency
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigRepository  @Inject constructor(){

    val remoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            if (BuildConfig.DEBUG){
                minimumFetchIntervalInSeconds = 60
            } else {
                minimumFetchIntervalInSeconds = 60 * 60 * 24
            }
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate()
    }

    fun getSupportedCurrencies() : List<Currency>{
        return remoteConfig.getString(SUPPORTED_CURRENCIES).split(",").map {
            Currency(it.trim())
        }.sortedBy {
            it.code
        }
    }

    fun getSupportedCountries() : List<String>{
        return remoteConfig.getString(SUPPORTED_COUNTRIES).split(",").map { it.trim() }
    }

    fun getDonateAddress(coin : String) : String{
        return remoteConfig.getString(when(coin.toUpperCase(Locale.getDefault())){
            "BTC" -> DONATE_BTC
            "LTC" -> DONATE_LTC
            "XMR" -> DONATE_XMR
            "ETH" -> DONATE_ETH
            "ADA" -> DONATE_ADA
            else -> DONATE_BTC
        })
    }

    fun getTrezorOneLink() : String{
        return remoteConfig.getString(TREZOR_ONE)
    }

    fun getTrezorOneAlzaLink() : String{
        return remoteConfig.getString(TREZOR_ONE_ALZA)
    }

    fun getTrezorTLink() : String{
        return remoteConfig.getString(TREZOR_T)
    }

    fun getTrezorTAlzaLink() : String{
        return remoteConfig.getString(TREZOR_T_ALZA)
    }

    fun getBinanceLink() : String{
        return remoteConfig.getString(BINANCE)
    }

    companion object{
        const val SUPPORTED_CURRENCIES = "supportedCurrencies"
        const val SUPPORTED_COUNTRIES = "supportedCountries"

        const val BINANCE = "binance"
        const val TREZOR_ONE = "trezorOne"
        const val TREZOR_ONE_ALZA = "trezorOneAlza"
        const val TREZOR_T = "trezorT"
        const val TREZOR_T_ALZA = "trezorTAlza"

        const val DONATE_BTC = "donateBtc"
        const val DONATE_LTC = "donateLtc"
        const val DONATE_XMR = "donateXmr"
        const val DONATE_ETH = "donateEth"
        const val DONATE_ADA = "donateAda"
    }
}