package kodebase.binding

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

/**
 * Handcrafted by Stepan Sonsky
 * Apache 2.0 License
 * https://github.com/kodebase-android
 */

@BindingAdapter("errorResource")
fun TextInputLayout.bindErrorResource(resId: Int?) {
    error = if (resId != null && resId != 0) {
        context.getString(resId)
    } else
        null
}