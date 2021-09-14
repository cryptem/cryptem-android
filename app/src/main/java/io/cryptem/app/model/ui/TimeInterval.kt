package io.cryptem.app.model.ui

import androidx.annotation.StringRes
import io.cryptem.app.R
import java.util.concurrent.TimeUnit

enum class TimeInterval(@StringRes val title : Int, val miliseconds : Long) {
    DAY(R.string.time_day, TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)),
    WEEK(R.string.time_week, TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS)),
    MONTH(R.string.time_month, TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS)),
}