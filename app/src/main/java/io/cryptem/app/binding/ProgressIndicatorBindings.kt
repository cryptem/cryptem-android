package io.cryptem.app.binding

import android.view.View
import android.widget.ProgressBar
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator

@BindingAdapter(value = ["tint"], requireAll = false)
fun CircularProgressIndicator.bindTint(color: Int) {
    setIndicatorColor(color, color, color)
}

@BindingAdapter(value = ["indeterminate"], requireAll = false)
fun LinearProgressIndicator.bindIndeterminate(enable : Boolean){
    visibility = View.INVISIBLE
    isIndeterminate = enable
    visibility = View.VISIBLE
}

@BindingAdapter(value = ["tint"], requireAll = false)
fun LinearProgressIndicator.bindTint(color: Int) {
    setIndicatorColor(color, color, color)
}

@BindingAdapter(value = ["tintResource"], requireAll = false)
fun LinearProgressIndicator.bindTintResource(colorRes: Int) {
    val color = ResourcesCompat.getColor(context.resources, colorRes, null)
    setIndicatorColor(color, color, color)
}
