package io.cryptem.app.ext

import io.cryptem.app.model.ui.Coin
import io.cryptem.app.model.ui.Currency
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*
import kotlin.math.absoluteValue

object FormatUtils {

    @JvmStatic
    fun formatPercent(value: Double, decimalDigits: Int = 1, abs: Boolean): String {
        return value.toPercentString(decimalDigits = decimalDigits, abs = abs)
    }

    @JvmStatic
    fun formatTrendPercent(value: Double): String {
        return (value / 100.0).toPercentString(decimalDigits = 1, abs = true)
    }
}

fun Double.toFiatString(currency: Currency, decimalDigits: Int = 2): String {
    val format: NumberFormat = DecimalFormat.getCurrencyInstance(Locale.getDefault())
    format.currency = currency.currency

    format.maximumFractionDigits = decimalDigits
    return format.format(this).let {
        return@let when {
            currency.isUsd() -> {
                it.replace("US$", "$")
            }
            else -> {
                it
            }
        }
    }
}

fun Double.toBtcString(): String {
    val format: NumberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    format.minimumFractionDigits = 8
    format.maximumFractionDigits = 8
    return "${format.format(this)} ₿"
}

fun Double.toSatString(): String {
    val format: NumberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    format.maximumFractionDigits = 0
    return "${format.format(this * 100000000)} sat"
}

fun Double.toPercentString(decimalDigits: Int = 2, abs: Boolean = false): String {
    val format = NumberFormat.getPercentInstance(Locale.getDefault())
    format.minimumFractionDigits = decimalDigits
    format.maximumFractionDigits = decimalDigits
    return format.format(if (abs) this.absoluteValue else this)
}

fun Double.toAmountString(symbol: String? = null, maxDecimals : Int = 8): String {
    val format: NumberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    format.maximumFractionDigits = maxDecimals
    return if (symbol != null) {
        "${format.format(this)} $symbol"
    } else {
        format.format(this)
    }
}


