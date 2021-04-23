package io.cryptem.app.binding

import android.content.res.ColorStateList
import android.widget.ProgressBar
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter


@BindingAdapter(value = ["tintResource"], requireAll = false)
fun ProgressBar.bindTintResource(colorRes: Int) {
    if (colorRes != 0) {
        indeterminateTintList =
            ColorStateList.valueOf(ResourcesCompat.getColor(context.resources, colorRes, null))
    }
}
