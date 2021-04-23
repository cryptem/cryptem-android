package kodebase.binding

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

/**
 * Handcrafted by Stepan Sonsky
 * Apache 2.0 License
 * https://github.com/kodebase-android
 */

@BindingAdapter("textColorResource")
fun TextView.bindTextColor(resId: Int?) {
    if (resId != null && resId != 0) {
        setTextColor(ContextCompat.getColor(context, resId))
    }
}

@BindingAdapter("textResource")
fun TextView.bindTextResource(resId: Int?) {
    if (resId != null) {
        setText(resId)
    } else {
        text = null
    }
}