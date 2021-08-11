package io.cryptem.app.ext

import io.cryptem.app.App
import io.cryptem.app.model.ui.Coin
import java.text.DateFormat
import java.util.*
import kotlin.math.absoluteValue

object DateExt{
    val format = DateFormat.getDateInstance(DateFormat.MEDIUM)
}

fun Date.format(): String {
    return DateExt.format.format(this)
}


