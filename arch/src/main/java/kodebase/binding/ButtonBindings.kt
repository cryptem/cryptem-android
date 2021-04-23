package kodebase.binding

import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton

/**
 * Handcrafted by Stepan Sonsky
 * Apache 2.0 License
 * https://github.com/kodebase-android
 */

@BindingAdapter("textResource")
fun MaterialButton.bindTextResource(@StringRes resId: Int?) {
    if (resId != null) {
        setText(resId)
    } else {
        text = null
    }
}