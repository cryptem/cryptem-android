package io.cryptem.app.binding

import android.content.res.ColorStateList
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton


@BindingAdapter(value = ["iconTintResource"], requireAll = false)
fun MaterialButton.bindIconTintResource(colorRes: Int) {
    if (colorRes != 0) {
        setIconTintResource(colorRes)
    }
}

@BindingAdapter(value = ["backgroundTintResource"], requireAll = false)
fun MaterialButton.bindBackgroundTintResource(colorRes: Int) {
    if (colorRes != 0) {
        backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(context.resources, colorRes, null))
    }
}