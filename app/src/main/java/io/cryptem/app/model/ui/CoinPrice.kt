package io.cryptem.app.model.ui

import io.cryptem.app.ext.format
import java.util.*
import kotlin.collections.HashMap

class CoinPrice(
    val currentPrice: Double?,
    percentChange24h: Double? = null,
    percentChange7d: Double? = null,
    percentChange30d: Double? = null,
    percentChange1y: Double? = null,
    val ath : Double? = null,
    val athPercent: Double? = null,
    val athDate : Date? = null
) {
    private val percentChange = HashMap<TimeInterval, Double?>()

    init {
        percentChange[TimeInterval.DAY] = percentChange24h
        percentChange[TimeInterval.WEEK] = percentChange7d
        percentChange[TimeInterval.MONTH] = percentChange30d
        percentChange[TimeInterval.YEAR] = percentChange1y
        percentChange[TimeInterval.ATH] = athPercent
    }

    fun getAthDateString() : String?{
        return athDate?.format()
    }

    fun getPercentChange(interval: TimeInterval) : Double?{
        return percentChange[interval]
    }
}