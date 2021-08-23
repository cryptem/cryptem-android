package io.cryptem.app.ui.coin

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DateFormat
import java.util.*

class DateAxisFormatter : ValueFormatter() {

    val formatter = DateFormat.getDateInstance(DateFormat.SHORT)

    override fun getFormattedValue(value: Float): String {
        return formatter.format(Date(value.toLong()))
    }

}