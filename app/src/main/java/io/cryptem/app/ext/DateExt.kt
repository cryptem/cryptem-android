package io.cryptem.app.ext

import java.text.DateFormat
import java.util.*

object DateExt{
    val format = DateFormat.getDateInstance(DateFormat.MEDIUM)
}

fun Date.format(): String {
    return DateExt.format.format(this)
}


