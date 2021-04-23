package io.cryptem.app.model.ui

import androidx.annotation.StringRes
import io.cryptem.app.R

enum class PercentTimeInterval(@StringRes val title : Int) {
    DAY(R.string.time_day),
    WEEK(R.string.time_week),
    MONTH(R.string.time_month),
}