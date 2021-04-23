package io.cryptem.app.binding

import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator

@BindingAdapter(value = ["tint"], requireAll = false)
fun CircularProgressIndicator.bindTint(color: Int) {
    setIndicatorColor(color, color, color)
}
