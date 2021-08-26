package io.cryptem.app.model.ui

import com.github.mikephil.charting.data.LineData

class PriceChartData(val key : Key, val data : LineData) {

    data class Key(val id : String, val days: Int)
}