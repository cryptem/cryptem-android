package io.cryptem.app.binding

import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData

@BindingAdapter("data")
fun LineChart.bindData(data : LineData?) {
    setData(data)
    invalidate()
}