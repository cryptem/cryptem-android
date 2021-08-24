package io.cryptem.app.model.ui

import android.os.Parcelable
import io.cryptem.app.ext.format
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class CoinPrice(
    val currentPrice: Double?,
    val percentChange24h: Double? = null,
    val percentChange7d: Double? = null,
    val percentChange30d: Double? = null,
    val ath : Double? = null,
    val athPercent: Double? = null,
    val athDate : Date? = null
) : Parcelable {

    fun getAthDateString() : String?{
        return athDate?.format()
    }
}